package io.kentec.SaccoMobile.service;

import io.kentec.SaccoMobile.exceptions.NonRollBackException;
import io.kentec.SaccoMobile.request.TransactionRequest;

public interface TransactionService {
    void processLoanRepayment(String msisdn, String amount, String mpesaReceipt, String account) throws NonRollBackException;

    void processSavingsDeposit(String msisdn, String amount, String mpesaReceipt, String account) throws NonRollBackException;

    void withdraw(String msisdn, String amount, String account) throws NonRollBackException;

    void applyloan(String msisdn, String amount, String account) throws NonRollBackException;
}
