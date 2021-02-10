package io.kentec.SaccoMobile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kentec.SaccoMobile.entity.Customers;
import io.kentec.SaccoMobile.request.DepositRequest;
import io.kentec.SaccoMobile.request.TransactionRequest;
import io.kentec.SaccoMobile.service.MpesaService;
import io.kentec.SaccoMobile.service.TransactionService;
import io.kentec.SaccoMobile.shared.SharedFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private MpesaService mpesaService;

    @Autowired
    private TransactionService transactionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deposit(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        LOGGER.info("POST | /api/transactions/save {}", transactionRequest);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            transactionRequest.setMsisdn(SharedFunctions.formatPhoneNumber(transactionRequest.getMsisdn()));
            Customers customer = mpesaService.getCustomerByMsisdn(transactionRequest.getMsisdn());
            mpesaService.initiateStkPush(transactionRequest.getMsisdn(), transactionRequest.getAmount(), "S" + transactionRequest.getAccount());
            responseMap.put("responseCode", "00");
            responseMap.put("responseMessage", "Your request for a deposit has been received. You will receive a prompt to authorise the transaction shortly.");
            LOGGER.info("POST | Msisdn={} /api/sacco/save/ responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }catch (Exception e){
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessage", e.getMessage());
            LOGGER.info("POST | Msisdn={} /api/sacco/save/ responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/repayloan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loanRepayment(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        LOGGER.info("POST | /api/transactions/repayloan {}", transactionRequest);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            transactionRequest.setMsisdn(SharedFunctions.formatPhoneNumber(transactionRequest.getMsisdn()));
            Customers customer = mpesaService.getCustomerByMsisdn(transactionRequest.getMsisdn());
            mpesaService.initiateStkPush(transactionRequest.getMsisdn(), transactionRequest.getAmount(), "L" + customer.getIdNo());
            responseMap.put("responseCode", "00");
            responseMap.put("responseMessage", "Your request for Loan repayment has been received. You will receive a prompt to authorise the transaction shortly.");
            LOGGER.info("POST | Msisdn={} /api/transactions/repayloan responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }catch (Exception e){
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessage", e.getMessage());
            LOGGER.info("POST | Msisdn={} /api/transactions/repayloan responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/withdraw", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdraw(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        LOGGER.info("POST | /api/transactions/withdraw {}", transactionRequest);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            transactionRequest.setMsisdn(SharedFunctions.formatPhoneNumber(transactionRequest.getMsisdn()));
            transactionService.withdraw(transactionRequest.getMsisdn(), transactionRequest.getAmount(), transactionRequest.getAccount() );
            responseMap.put("responseCode", "00");
            responseMap.put("responseMessage", "Your transaction of Kshs. " + transactionRequest.getAmount() +" has been credited to " + transactionRequest.getMsisdn() +". MPESA Ref.PAK4SHVB98.");
            LOGGER.info("POST | Account={} /api/sacco/withdraw responseMap= {}", transactionRequest.getAccount(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }catch (Exception e){
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessage", e.getMessage());
            LOGGER.info("POST | Msisdn={} /api/sacco/save/ responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/applyloan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> applyloan(@RequestBody TransactionRequest transactionRequest) throws JsonProcessingException {
        LOGGER.info("POST | /api/transactions/applyloan {}", transactionRequest);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            transactionRequest.setMsisdn(SharedFunctions.formatPhoneNumber(transactionRequest.getMsisdn()));
            transactionService.applyloan(transactionRequest.getMsisdn(), transactionRequest.getAmount(), transactionRequest.getAccount() );
            responseMap.put("responseCode", "00");
            responseMap.put("responseMessage", "We are processing your loan application.You will receive an SMS advice shortly.");
            LOGGER.info("POST | Account={} /api/transactions/applyloan responseMap= {}", transactionRequest.getAccount(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }catch (Exception e){
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessage", e.getMessage());
            LOGGER.info("POST | Msisdn={} /api/transactions/applyloan responseMap= {}", transactionRequest.getMsisdn(), responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
    }
}
