package io.kentec.SaccoMobile.service;

public interface SmsSenderService {
    void sendSMS(String recepient, String message) throws Exception;
}
