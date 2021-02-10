package io.kentec.SaccoMobile.service;

public interface BatchService {
    public void processPendingC2BTransactions();
    void processPendingB2CTransactions();
}
