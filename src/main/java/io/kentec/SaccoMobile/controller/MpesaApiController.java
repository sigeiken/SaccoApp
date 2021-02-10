package io.kentec.SaccoMobile.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.kentec.SaccoMobile.service.MpesaService;
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
@RequestMapping("api/financial")
public class MpesaApiController {

    @Autowired
    private MpesaService mpesaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MpesaApiController.class);

    @PostMapping(value = "/stkcallback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stkCallBack(@RequestBody String payload) {
        LOGGER.info("POST | /api/financial/stkcallback/{} ", payload);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            JsonObject jsonObjectResponse = new JsonParser().parse(payload).getAsJsonObject();
            String MerchantRequestID = jsonObjectResponse.getAsJsonObject("Body").getAsJsonObject("stkCallback").get("MerchantRequestID").getAsString();
            String CheckoutRequestID = jsonObjectResponse.getAsJsonObject("Body").getAsJsonObject("stkCallback").get("CheckoutRequestID").getAsString();
            int ResultCode = jsonObjectResponse.getAsJsonObject("Body").getAsJsonObject("stkCallback").get("ResultCode").getAsInt();
            LOGGER.info("POST | /api/sacco/stkcallback/Params:MerchantRequestID={},CheckoutRequestID={},ResultCode={}", MerchantRequestID, CheckoutRequestID, ResultCode);
            if (ResultCode != 0){
                mpesaService.processStkResult(ResultCode, MerchantRequestID, CheckoutRequestID, null, null, null);
                LOGGER.info("POST | /api/financial/stkcallback/-ResultCode NEGATIVE");
            }else if (jsonObjectResponse.getAsJsonObject("Body").getAsJsonObject("stkCallback").has("CallbackMetadata")) {
                LOGGER.info("POST |ResultCode POSITIVE /api/financial/stkcallback/-CallbackMetadata data sent");
                JsonArray resultParamsArray = jsonObjectResponse.getAsJsonObject("Body").getAsJsonObject("stkCallback").getAsJsonObject("CallbackMetadata").get("Item").getAsJsonArray();
                Map<String, Object> keyValues = new LinkedHashMap<>();
                for (JsonElement object : resultParamsArray) {
                    JsonObject obj = object.getAsJsonObject();
                    String name = obj.get("Name").getAsString();
                    String value = obj.has("Value") ? obj.get("Value").getAsString() : "N/A";
                    keyValues.put(name, value);
                    LOGGER.info("POST | /api/sacco/stkcallback/-CallbackMetadata Name={},Value={}", name, value);
                }
                mpesaService.processStkResult(ResultCode, MerchantRequestID, CheckoutRequestID, keyValues.get("MpesaReceiptNumber").toString(), keyValues.get("PhoneNumber").toString(), keyValues.get("Balance").toString());
            }
            responseMap.put("ResultCode", "0");
            LOGGER.info("POST | /api/sacco/stkcallback/ responseMap= {}", responseMap);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("POST | /api/sacco/stkcallback/ responseMap= {}", responseMap, e);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
    }

    @PostMapping("/b2cresulturl")
    public ResponseEntity<?> b2cresult(@RequestBody String payload){
        LOGGER.info("POST | /api/financial/b2c-result-url/{} ", payload);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            JsonObject jsonObjectResponse = new JsonParser().parse(payload).getAsJsonObject();
            int ResultType = jsonObjectResponse.getAsJsonObject("Result").get("ResultType").getAsInt();
            int ResultCode = jsonObjectResponse.getAsJsonObject("Result").get("ResultCode").getAsInt();
            String OriginatorConversationID = jsonObjectResponse.getAsJsonObject("Result").get("OriginatorConversationID").getAsString();
            String ConversationID = jsonObjectResponse.getAsJsonObject("Result").get("ConversationID").getAsString();
            String TransactionID = jsonObjectResponse.getAsJsonObject("Result").get("TransactionID").getAsString();
            LOGGER.info("POST /api/financial/b2c-result-url/ ::ResultType={} ,ResultCode={},OriginatorConversationID={},ConversationID={},TransactionID={}", ResultType, ResultCode, OriginatorConversationID, ConversationID, TransactionID);
            if (ResultCode != 0) {
                LOGGER.warn("POST W| /api/financial/b2c-result-url//-ResultParameters data not sent..NEGATIVE");
                mpesaService.processB2CResult(ResultCode, OriginatorConversationID, ConversationID, TransactionID, null, null, null, null, null);
            } else if (jsonObjectResponse.getAsJsonObject("Result").has("ResultParameters")) {
                LOGGER.info("POST | /api/financial/b2c-result-url/-ResultParameters data sent");
                JsonArray resultParamsArray = jsonObjectResponse.getAsJsonObject("Result").getAsJsonObject("ResultParameters").getAsJsonArray("ResultParameter");
                Map<String, Object> keyValues = new LinkedHashMap<>();
                for (JsonElement object : resultParamsArray) {
                    String key = object.getAsJsonObject().get("Key").getAsString();
                    String value = object.getAsJsonObject().get("Value").getAsString();
                    keyValues.put(key, value);
                    LOGGER.info("POST | /api/financial/b2cresult/-ResultParameter Key={},Value={}", key, value);
                }
                mpesaService.processB2CResult(ResultCode, OriginatorConversationID, ConversationID, TransactionID, null, keyValues.get("ReceiverPartyPublicName").toString(),  keyValues.get("B2CWorkingAccountAvailableFunds").toString(), keyValues.get("B2CUtilityAccountAvailableFunds").toString(), keyValues.get("B2CChargesPaidAccountAvailableFunds").toString());
            }

            responseMap.put("ResultCode", "0");
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }catch (Exception e) {
            responseMap.put("ResultCode", "01");
            e.printStackTrace();
            LOGGER.error("POST E| /api/financial/b2c-result-url/ responseMap= {}", responseMap, e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
            }
        }
    }
