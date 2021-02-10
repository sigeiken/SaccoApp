package io.kentec.SaccoMobile.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class DepositRequest implements Serializable {

    private static final long serialVersionUID = -1610140498143487871L;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("amount")
    private BigDecimal amount;

    public DepositRequest() {
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DepositRequest{" +
                "msisdn='" + msisdn + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
