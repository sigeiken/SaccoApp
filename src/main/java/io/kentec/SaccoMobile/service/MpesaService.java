package io.kentec.SaccoMobile.service;

import io.kentec.SaccoMobile.entity.Customers;
import io.kentec.SaccoMobile.exceptions.NonRollBackException;

import java.math.BigDecimal;

public interface MpesaService {
    void initiateStkPush(String msisdn, String amount, String billReference) throws NonRollBackException;
    void processStkResult(int resultCode, String merchantRequestID, String checkoutRequestID, String mpesaReceiptNumber, String msisdn,String C2BOrgBalance) throws NonRollBackException;
    Customers getCustomerByMsisdn(String msisdn);
    void initiateB2CPayment(String msisdn, String amount) throws NonRollBackException;
    void processB2CResult(int ResultCode, String OriginatorConversationID, String ConversationID, String TransactionID, String msisdn, String ReceiverPartyPublicName, String B2CWorkingAccountAvailableFunds, String B2CUtilityAccountAvailableFunds, String B2CChargesPaidAccountAvailableFunds) throws Exception;

}
