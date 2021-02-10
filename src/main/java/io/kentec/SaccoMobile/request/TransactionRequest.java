package io.kentec.SaccoMobile.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest implements Serializable {

    private static final long serialVersionUID = 7037373317053426620L;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("account")
    private String account;
    @JsonProperty("msisdn")
    private String msisdn;


    public TransactionRequest() {
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "amount='" + amount + '\'' +
                ", account='" + account + '\'' +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }
}
