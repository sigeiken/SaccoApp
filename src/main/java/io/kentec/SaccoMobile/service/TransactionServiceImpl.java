package io.kentec.SaccoMobile.service;

import io.kentec.SaccoMobile.entity.*;
import io.kentec.SaccoMobile.exceptions.NonRollBackException;
import io.kentec.SaccoMobile.repository.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;


@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private CbsService cbsService;

    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private SystemMessagesRepository systemMessagesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processLoanRepayment(String msisdn, String amount, String mpesaReceipt, String account) throws NonRollBackException {
        try{
            account = account.substring(1);
            Customers customer = customerRepository.findByMsisdn(msisdn);
            String url = environment.getProperty("cbs.url") + "withdraw";
            String requestBody = "{\n"
                    + "    \"action\": \"LOAN_REPAYMENT\",\n"
                    + "    \"parameters\":{\n"
                    + "        \"currency\":\"Kes\",\n"
                    + "        \"account\":\"" + account + "\",\n" //TODO: get customer account number
                    + "        \"amount\": \"" + amount + "\",\n"
                    + "        \"phoneNo\":\"" + msisdn + "\"\n"
                    + "    },\n"
                    + "    \"username\": \"" + environment.getRequiredProperty("cbs.username") + "\",\n"
                    + "    \"password\": \"" + environment.getRequiredProperty("cbs.password") + "\"\n"
                    + "}";
            String response = doPostToCbs(url, requestBody);
            JSONObject res = new JSONObject(response);
            String responseCode = res.getString("responseCode");
            String responseDescription = res.getString("responseDescription");
            if (responseCode.equals("00")) { //success response
                //Save transaction to transactions table
                if (responseCode.equals("00")) { //success response
                    //Save transaction to transactions table and send notification to customer
                    Transactions transaction = new Transactions();
                    transaction.setAmount(new BigDecimal(amount));
                    transaction.setProcessingStatus("COMPLETE_OK");
                    transaction.setTransactionType("LOAN_REPAYMENT");
                    transaction.setDescription("Transaction Completed Successfully");
                    transaction.setTrxReference(mpesaReceipt);
                    transaction.setTimeProcessed(Instant.now());
                    transaction.setCustomerId(customer);
                    transactionsRepository.save(transaction);

                }else {
                    //Save transaction to transactions table and send notification to customer
                    Transactions transaction = new Transactions();
                    transaction.setAmount(new BigDecimal(amount));
                    transaction.setProcessingStatus("COMPLETE_OK");
                    transaction.setTransactionType("DEPOSIT");
                    transaction.setDescription(responseDescription);
                    transaction.setTrxReference(mpesaReceipt);
                    transaction.setTimeProcessed(Instant.now());
                    transaction.setCustomerId(customer);

                    transactionsRepository.save(transaction);
                }
            }
        }catch (Exception e){
            LOGGER.error("Msisdn={} E .Savings deposit failed, Amount={}.", msisdn, amount);
            throw new NonRollBackException(e.getMessage());
        }
    }

    //Send request to core banking system
    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processSavingsDeposit(String msisdn, String amount, String mpesaReceipt, String account) throws NonRollBackException {
        try {
            account = account.substring(1);
            Customers customer = customerRepository.findByMsisdn(msisdn);
            String url = environment.getProperty("cbs.url") + "save";
            String requestBody = "{\n"
                    + "    \"action\": \"SAVE\",\n"
                    + "    \"parameters\":{\n"
                    + "        \"currency\":\"Kes\",\n"
                    + "        \"account\":\"" + account + "\",\n" //TODO: get customer account number
                    + "        \"amount\": \"" + amount + "\",\n"
                    + "        \"phoneNo\":\"" + msisdn + "\"\n"
                    + "    },\n"
                    + "    \"username\": \"" + environment.getRequiredProperty("cbs.username") + "\",\n"
                    + "    \"password\": \"" + environment.getRequiredProperty("cbs.password") + "\"\n"
                    + "}";
            String response = doPostToCbs(url,requestBody);
            JSONObject res = new JSONObject(response);
            String responseCode = res.getString("responseCode");
            String responseDescription = res.getString("responseDescription");
            if (responseCode.equals("00")) { //success response
                //Save transaction to transactions table and send notification to customer
                Transactions transaction = new Transactions();
                transaction.setAmount(new BigDecimal(amount));
                transaction.setProcessingStatus("COMPLETE_OK");
                transaction.setTransactionType("DEPOSIT");
                transaction.setDescription("Transaction Completed Successfully");
                transaction.setTrxReference(mpesaReceipt);
                transaction.setTimeProcessed(Instant.now());
                transaction.setCustomerId(customer);

                transactionsRepository.save(transaction);

                MessageTemplate template = messageTemplateRepository.findByMessageCode("12");//successful deposit
                String customerName = customer.getFirstName() != null ? customer.getFirstName() : "Customer";
                //Dear <NAME>, your deposit of Ksh. <AMOUNT> to account <ACCOUNT> was successful
                String messageContent = template.getMessage().replace("<NAME>", customerName).replace("<AMOUNT>", amount).replace("<ACCOUNT>", account);

                SystemMessages message = new SystemMessages();
                message.setMessage(messageContent);
                message.setRecepient(msisdn);
                message.setStatus("0");
                message.setMsgType("SMS");
                message.setDeliveryStatus("INITIATED");
                message.setLogDate(Instant.now());

                systemMessagesRepository.save(message);

            }else { //Retry
                Transactions transaction = new Transactions();
                transaction.setAmount(new BigDecimal(amount));
                transaction.setProcessingStatus("COMPLETE_FAILED");
                transaction.setTransactionType("DEPOSIT");
                transaction.setDescription(responseDescription);
                transaction.setTrxReference(mpesaReceipt);
                transaction.setTimeProcessed(Instant.now());
                transaction.setCustomerId(customer);
                transactionsRepository.save(transaction);
            }
        }catch (Exception e){
            LOGGER.error("Msisdn={} E .Savings deposit failed, Amount={}.", msisdn, amount);
            throw new NonRollBackException(e.getMessage());
        }

    }

    @Override
    public void withdraw(String msisdn, String amount, String account) throws NonRollBackException {
        //call cbs if request is successful in cbs deposit requested amount to users mpesa account
        try {

            String requestBody = "{\n"
                    + "    \"action\": \"WITHDRAW\",\n"
                    + "    \"parameters\":{\n"
                    + "        \"currency\":\"Kes\",\n"
                    + "        \"account\":\"" + account + "\",\n" //TODO: get customer account number
                    + "        \"amount\": \"" + amount + "\",\n"
                    + "        \"phoneNo\":\"" + msisdn + "\"\n"
                    + "    },\n"
                    + "    \"username\": \"" + environment.getRequiredProperty("cbs.username") + "\",\n"
                    + "    \"password\": \"" + environment.getRequiredProperty("cbs.password") + "\"\n"
                    + "}";
            processB2CResultFromCbs(msisdn, amount, requestBody);
        }catch (Exception e){
            LOGGER.error("Msisdn={} E .Savings deposit failed, Amount={} Account={}.", msisdn, amount, account);
            throw new NonRollBackException(e.getMessage());
        }
    }

    @Override
    public void applyloan(String msisdn, String amount, String account) throws NonRollBackException {
        //call cbs, if request is successful on cbs save transaction to mpesatable so that it can be procossed
        try {
            String requestBody = "{\n"
                    + "    \"action\": \"APPLY_LOAN\",\n"
                    + "    \"parameters\":{\n"
                    + "        \"currency\":\"Kes\",\n"
                    + "        \"account\":\"" + account + "\",\n" //TODO: get customer account number
                    + "        \"amount\": \"" + amount + "\",\n"
                    + "        \"phoneNo\":\"" + msisdn + "\"\n"
                    + "    },\n"
                    + "    \"username\": \"" + environment.getRequiredProperty("cbs.username") + "\",\n"
                    + "    \"password\": \"" + environment.getRequiredProperty("cbs.password") + "\"\n"
                    + "}";
            processB2CResultFromCbs(msisdn, amount, requestBody);
        }catch (Exception e){
            LOGGER.error("Msisdn={} E .Savings deposit failed, Amount={} Account={}.", msisdn, amount, account);
            throw new NonRollBackException(e.getMessage());
        }
    }

    private void processB2CResultFromCbs(String msisdn, String amount, String requestBody) throws Exception {
        try {
            String url = environment.getProperty("cbs.url") + "withdraw";
            String response = doPostToCbs(url, requestBody);
            JSONObject res = new JSONObject(response);
            String responseCode = res.getString("responseCode");
            String responseDescription = res.getString("responseDescription");
            if (responseCode.equals("00")){ //success response
                //Save transaction to mpesa table
                MpesaTransactions mpesaTransaction = new MpesaTransactions();
                mpesaTransaction.setMsisdn(msisdn);
                mpesaTransaction.setAmount(new BigDecimal(amount));
                mpesaTransaction.setMpesaTranType("B2C");
                mpesaTransaction.setProcessingStatus("PROCESSED");
                mpesaTransaction.setTimeProcessed(Instant.now());

                mpesaTransactionsRepository.save(mpesaTransaction);
            }else {
                //send notifications to customer
            }
        }catch (Exception e){
            throw new Exception("ErrorOccurred " + e.getMessage());
        }

    }

    private String doPostToCbs(String url, String requestBody) throws Exception {
        try {
            return cbsService.makeCbsCall(url,requestBody);
        }catch (Exception e){
            throw new Exception("Error calling CBC " + e.getMessage());
        }
    }
}
