package io.kentec.SaccoMobile.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.kentec.SaccoMobile.entity.Customers;
import io.kentec.SaccoMobile.entity.MessageTemplate;
import io.kentec.SaccoMobile.entity.MpesaTransactions;
import io.kentec.SaccoMobile.entity.SystemMessages;
import io.kentec.SaccoMobile.exceptions.NonRollBackException;
import io.kentec.SaccoMobile.exceptions.RollBackException;
import io.kentec.SaccoMobile.repository.CustomerRepository;
import io.kentec.SaccoMobile.repository.MessageTemplateRepository;
import io.kentec.SaccoMobile.repository.MpesaTransactionsRepository;
import io.kentec.SaccoMobile.repository.SystemMessagesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class MpesaServiceImpl implements MpesaService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;


    @Autowired
    private Environment environment;

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private SystemMessagesRepository systemMessagesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MpesaServiceImpl.class);

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public Customers getCustomerByMsisdn(String msisdn) {
        try {
            return customerRepository.findByMsisdn(msisdn);
        }catch (Exception e){
            throw new RollBackException("Customer account not found.");
        }
    }

    //Initiate STK PUSH
    @Override
    public void initiateStkPush(String msisdn, String amount, String billReference) throws NonRollBackException {
        Date now = new Date(System.currentTimeMillis());
        try {
            String _timestamp = SDF.format(now);

            String requestBody = "{\n"
                    + "     \"BusinessShortCode\": \"" + environment.getRequiredProperty("mpesa.shortcode") + "\",\n"
                    + "     \"Password\": \"" + getRequestPassword(environment.getRequiredProperty("mpesa.shortcode"), environment.getRequiredProperty("mpesa.stk_push_passkey"), _timestamp) + "\",\n"
                    + "     \"Timestamp\": \"" + _timestamp + "\",\n"
                    + "     \"TransactionType\": \"CustomerPayBillOnline\",\n"
                    + "     \"Amount\": \"" + amount + "\",\n"
                    + "     \"PartyA\": \"" + msisdn + "\",\n"
                    + "     \"PartyB\":  \"" + environment.getRequiredProperty("mpesa.shortcode") + "\",\n"
                    + "     \"PhoneNumber\": \"" + msisdn + "\",\n"
                    + "     \"CallBackURL\": \"" + environment.getRequiredProperty("mpesa.stkcallback") + "\",\n"
                    + "     \"AccountReference\": \"" + billReference + "\",\n"
                    + "     \"TransactionDesc\": \"Online STK Push\"\n"
                    + "    }";

            LOGGER.info("Msisdn={}|C2B STK PUSH REQBODY={}", msisdn, requestBody);
            String response = doPostRequest(environment.getRequiredProperty("mpesa.stk_push_url"), requestBody, "Bearer " + MpesaToken.token);
            LOGGER.info("Msisdn={}|C2B STK PUSH response={}", msisdn, response);
            JsonObject jsonObjectResponse = new JsonParser().parse(response).getAsJsonObject();
            String MerchantRequestID = jsonObjectResponse.get("MerchantRequestID").getAsString();
            String CheckoutRequestID = jsonObjectResponse.get("CheckoutRequestID").getAsString();
            String ResponseCode = jsonObjectResponse.get("ResponseCode").getAsString();

            LOGGER.info("Msisdn={}|C2B STK PUSH response params:MerchantRequestID={},CheckoutRequestID={},ResponseCode={}", msisdn, MerchantRequestID, CheckoutRequestID, ResponseCode);
            if (ResponseCode.contentEquals("0")) {
                MpesaTransactions transaction = new MpesaTransactions();
                transaction.setAmount(new BigDecimal(amount));
                transaction.setMsisdn(msisdn);
                transaction.setBillReference(billReference);
                transaction.setMpesaTranType("STK_PUSH");
                transaction.setProcessingStatus("PENDING");
                transaction.setTimeProcessed(Instant.now());
                transaction.setMerchantRequestId(MerchantRequestID);
                transaction.setCheckoutRequestId(CheckoutRequestID);

                mpesaTransactionsRepository.save(transaction);
            }
        }catch (Exception e){
            throw new NonRollBackException("STK push processing exception: " + e.getMessage());
        }
    }

    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void initiateB2CPayment(String msisdn, String amount) throws NonRollBackException {
        try {
            String requestBody = "{\n"
                    +"  \"InitiatorName\": \""+ environment.getRequiredProperty("mpesa.InitiatorName") +"\",\n"
                    +"  \"SecurityCredential\": \""+ environment.getRequiredProperty("mpesa.SecurityCredential") +"\",\n"
                    +"  \"CommandID\": \"[CommandID]\",\n"
                    +"  \"Amount\": \""+ amount +"\",\n"
                    +"  \"PartyA\": \""+ environment.getRequiredProperty("mpesa.PartyA") +"\",\n"
                    +"  \"PartyB\": \""+ msisdn +"\",\n"
                    +"  \"Remarks\": \"\",\n"
                    +"  \"QueueTimeOutURL\": \""+ environment.getRequiredProperty("mpesa.QueueTimeOutURL") +"\",\n"
                    +"  \"ResultURL\": \""+ environment.getRequiredProperty("mpesa.ResultURL") +"\",\n"
                    +"  \"Occassion\": \"\"\n"
                    +"  }";

            LOGGER.info("Msisdn={}|B2C REQUESTBODY={}", msisdn, requestBody);
            String response = doPostRequest(environment.getRequiredProperty("mpesa.b2c_url"), requestBody, "Bearer " + MpesaToken.token);
            LOGGER.info("Msisdn={}|B2C response={}", msisdn, response);
            JsonObject jsonObjectResponse = new JsonParser().parse(response).getAsJsonObject();
            String OriginatorConversationID = jsonObjectResponse.get("OriginatorConversationID").getAsString();
            String ConversationID = jsonObjectResponse.get("ConversationID").getAsString();
            String ResponseCode = jsonObjectResponse.get("ResponseCode").getAsString();
            if (ResponseCode.contentEquals("0")) {
                MpesaTransactions mpesaTransaction = new MpesaTransactions();
                mpesaTransaction.setMpesaTranType("B2C");
                mpesaTransaction.setSenderParty(environment.getRequiredProperty("mpesa.PartyA"));
                mpesaTransaction.setAmount(new BigDecimal(amount));
                mpesaTransaction.setProcessingStatus("PENDING");
                mpesaTransaction.setReceiverParty(msisdn);
                mpesaTransaction.setMsisdn(msisdn);
                mpesaTransaction.setTimeProcessed(Instant.now());
                mpesaTransaction.setConversationId(ConversationID);
                mpesaTransaction.setOriginatorConversationId(OriginatorConversationID);
            }
        }catch (Exception e){
            throw new NonRollBackException("C2B processing exception:" + e.getMessage());
        }
    }

    @Override
    public void processB2CResult(int ResultCode, String OriginatorConversationID, String ConversationID, String TransactionID, String msisdn, String ReceiverPartyPublicName, String B2CWorkingAccountAvailableFunds, String B2CUtilityAccountAvailableFunds, String B2CChargesPaidAccountAvailableFunds) throws Exception {
        try {
            long trancount = mpesaTransactionsRepository.getB2CTransactionCountByOriginatorConversationIdConversationId("B2C", "PENDING", OriginatorConversationID, ConversationID);
            LOGGER.info("Count " + trancount);
            if (trancount >= 1) {
                MpesaTransactions mpesaTransaction = mpesaTransactionsRepository.getB2CTransactionByOriginatorConversationIdConversationId("B2C", "PENDING", OriginatorConversationID, ConversationID);
                LOGGER.info("ResultCode " + ResultCode);
                if (ResultCode == 0) {
                    mpesaTransaction.setMpesaTranType("B2C");
                    mpesaTransaction.setMpesaReceipt(TransactionID);
                    mpesaTransaction.setProcessingStatus("COMPLETED_OK");
                    mpesaTransaction.setReceiverPartyPublicName(ReceiverPartyPublicName);
                    mpesaTransaction.setB2cChargsPaidAccAvlFunds(B2CChargesPaidAccountAvailableFunds);
                    mpesaTransaction.setB2cUtilityAccAvailbleFunds(B2CUtilityAccountAvailableFunds);
                    mpesaTransaction.setB2cWorkingAccAvailbleFunds(B2CWorkingAccountAvailableFunds);
                } else {
                    mpesaTransaction.setProcessingStatus("COMPLETED_FAILED");
                    LOGGER.error("OriginatorConversationID={}|B2C result processing .Failed on MPESA", OriginatorConversationID);
                    return;
                }
                mpesaTransactionsRepository.save(mpesaTransaction);
            }else {
                LOGGER.warn("Msisdn={}|B2C callback processing waiting...on write from initiating method..retrying in 15 sec(s).PROBABLE DOUBLE/DUPLICATE POSTING FROM MPESA", msisdn);
                // Thread.sleep(15000);
                // processB2CResult(ResultCode, OriginatorConversationID, ConversationID, TransactionID, msisdn);
            }
        }catch (Exception e){
            LOGGER.error("Msisdn={}|B2C result processing error:{}", msisdn, e);
            throw new Exception("B2C result  processing exception:" + e.getMessage());
        }
    }

    private String doPostRequest(String endPointURL, String requestBody, String authKey) throws IOException {
        URL url = new URL(endPointURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json;charset=UTF-8");
        connection.setRequestProperty("Authorization",  authKey);
        connection.setRequestProperty("Content-Length", "" + Integer.toString(requestBody.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream());) {
            wr.writeBytes(requestBody);
            wr.flush();
        }
        StringBuilder response = new StringBuilder();
        try (InputStream is = connection.getInputStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }


    //Generate password
    private static String getRequestPassword(String shortcode, String passkey, String timestamp) {
        return Base64.getEncoder().encodeToString(new StringBuilder().append(shortcode).append(passkey).append(timestamp).toString().getBytes());
    }

    @Override
    public void processStkResult(int resultCode, String merchantRequestID, String checkoutRequestID, String mpesaReceiptNumber, String msisdn, String C2BOrgBalance) throws NonRollBackException {
        try{
            long trancount = mpesaTransactionsRepository.getStkC2BTransactionCountByCheckoutRequestIdMerchantRequestId("STK_PUSH", "PENDING", checkoutRequestID, merchantRequestID);
            if (trancount >= 1){
                MpesaTransactions mpesaTransaction = mpesaTransactionsRepository.getStkC2BTransactionByCheckoutRequestIdMerchantRequestId("STK_PUSH", "PENDING", checkoutRequestID, merchantRequestID);
                if (resultCode != 0) {
                    mpesaTransaction.setProcessingStatus("COMPLETED_FAILED");
                    Customers customer = customerRepository.findByMsisdn(mpesaTransaction.getMsisdn());

                    MessageTemplate template = messageTemplateRepository.findByMessageCode("13");//failed deposit
                    String customerName = customer.getFirstName() != null ? customer.getFirstName() : "Customer";
                    //Dear <NAME>, your deposit of Ksh. <AMOUNT> to account <ACCOUNT> Failed
                    String messageContent = template.getMessage().replace("<NAME>", customerName).replace("<AMOUNT>", mpesaTransaction.getAmount().toString());

                    SystemMessages message = new SystemMessages();
                    message.setMessage(messageContent);
                    message.setRecepient(mpesaTransaction.getMsisdn());
                    message.setStatus("0");
                    message.setMsgType("SMS");
                    message.setDeliveryStatus("INITIATED");
                    message.setLogDate(Instant.now());

                    systemMessagesRepository.save(message);
                }else{
                    mpesaTransaction.setMpesaTranType("C2B");
                    mpesaTransaction.setMpesaReceipt(mpesaReceiptNumber);
                    mpesaTransaction.setC2bOrgBalance(C2BOrgBalance);
                }
                mpesaTransactionsRepository.save(mpesaTransaction);
                LOGGER.info("Msisdn={}|C2B STK callback results processed for checkoutRequestId={},merchantRequestId={},mpesaReceiptNumber={}", msisdn, merchantRequestID, checkoutRequestID, mpesaReceiptNumber);
            }else {
                LOGGER.warn("Msisdn={}|C2B STK callback processing waiting...on write from initiating method..retrying in 15 sec(s).PROBABLE DOUBLE/DUPLICATE POSTING FROM MPESA", msisdn);
                //  Thread.sleep(15000);
                //  processStkResult(ResultCode, merchantRequestId, checkoutRequestId, mpesaReceiptNumber, msisdn);
            }

        }catch (Exception e) {
            throw new NonRollBackException("stk c2b result  processing exception:" + e.getMessage());
        }
    }

}
