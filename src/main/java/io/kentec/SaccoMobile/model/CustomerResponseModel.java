package io.kentec.SaccoMobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CustomerResponseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("idNo")
    private int idNo;

    public CustomerResponseModel() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getIdNo() {
        return idNo;
    }

    public void setIdNo(int idNo) {
        this.idNo = idNo;
    }

    @Override
    public String toString() {
        return "CustomerResponseModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", idNo=" + idNo +
                '}';
    }
}
