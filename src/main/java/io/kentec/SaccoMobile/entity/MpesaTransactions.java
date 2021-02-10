package io.kentec.SaccoMobile.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "MPESA_TRANSACTIONS")
public class MpesaTransactions implements Serializable {

    private static final long serialVersionUID = 7609557752583057300L;

    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TIME_PROCESSED")
    private Instant timeProcessed;
    @Column(name = "MPESA_RECEIPT")
    private String mpesaReceipt;
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @Column(name = "MPESA_TRAN_TYPE")
    private String mpesaTranType;
    @Column(name = "MSISDN")
    private String msisdn;
    @Column(name = "SENDER_PARTY")
    private String senderParty;
    @Column(name = "RECEIVER_PARTY")
    private String receiverParty;
    @Column(name = "RECEIVER_PARTY_PUBLIC_NAME")
    private String receiverPartyPublicName;
    @Column(name = "BILL_REFERENCE")
    private String billReference;
    @Column(name = "PROCESSING_STATUS")
    private String processingStatus;
    @Column(name = "ORIGINATOR_CONVERSATION_ID")
    private String originatorConversationId;
    @Column(name = "CONVERSATION_ID")
    private String conversationId;
    @Column(name = "MERCHANT_REQUEST_ID")
    private String merchantRequestId;
    @Column(name = "CHECKOUT_REQUEST_ID")
    private String checkoutRequestId;
    @Column(name = "C2B_ORG_BALANCE")
    private String c2bOrgBalance;
    @Column(name = "B2C_WORKING_ACC_AVAILBLE_FUNDS")
    private String b2cWorkingAccAvailbleFunds;
    @Column(name = "B2C_UTILITY_ACC_AVAILBLE_FUNDS")
    private String b2cUtilityAccAvailbleFunds;
    @Column(name = "B2C_CHARGS_PAID_ACC_AVL_FUNDS")
    private String b2cChargsPaidAccAvlFunds;

    public MpesaTransactions() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimeProcessed() {
        return timeProcessed;
    }

    public void setTimeProcessed(Instant timeProcessed) {
        this.timeProcessed = timeProcessed;
    }

    public String getMpesaReceipt() {
        return mpesaReceipt;
    }

    public void setMpesaReceipt(String mpesaReceipt) {
        this.mpesaReceipt = mpesaReceipt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMpesaTranType() {
        return mpesaTranType;
    }

    public void setMpesaTranType(String mpesaTranType) {
        this.mpesaTranType = mpesaTranType;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSenderParty() {
        return senderParty;
    }

    public void setSenderParty(String senderParty) {
        this.senderParty = senderParty;
    }

    public String getReceiverParty() {
        return receiverParty;
    }

    public void setReceiverParty(String receiverParty) {
        this.receiverParty = receiverParty;
    }

    public String getReceiverPartyPublicName() {
        return receiverPartyPublicName;
    }

    public void setReceiverPartyPublicName(String receiverPartyPublicName) {
        this.receiverPartyPublicName = receiverPartyPublicName;
    }

    public String getBillReference() {
        return billReference;
    }

    public void setBillReference(String billReference) {
        this.billReference = billReference;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getOriginatorConversationId() {
        return originatorConversationId;
    }

    public void setOriginatorConversationId(String originatorConversationId) {
        this.originatorConversationId = originatorConversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMerchantRequestId() {
        return merchantRequestId;
    }

    public void setMerchantRequestId(String merchantRequestId) {
        this.merchantRequestId = merchantRequestId;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    public void setCheckoutRequestId(String checkoutRequestId) {
        this.checkoutRequestId = checkoutRequestId;
    }

    public String getC2bOrgBalance() {
        return c2bOrgBalance;
    }

    public void setC2bOrgBalance(String c2bOrgBalance) {
        this.c2bOrgBalance = c2bOrgBalance;
    }

    public String getB2cWorkingAccAvailbleFunds() {
        return b2cWorkingAccAvailbleFunds;
    }

    public void setB2cWorkingAccAvailbleFunds(String b2cWorkingAccAvailbleFunds) {
        this.b2cWorkingAccAvailbleFunds = b2cWorkingAccAvailbleFunds;
    }

    public String getB2cUtilityAccAvailbleFunds() {
        return b2cUtilityAccAvailbleFunds;
    }

    public void setB2cUtilityAccAvailbleFunds(String b2cUtilityAccAvailbleFunds) {
        this.b2cUtilityAccAvailbleFunds = b2cUtilityAccAvailbleFunds;
    }

    public String getB2cChargsPaidAccAvlFunds() {
        return b2cChargsPaidAccAvlFunds;
    }

    public void setB2cChargsPaidAccAvlFunds(String b2cChargsPaidAccAvlFunds) {
        this.b2cChargsPaidAccAvlFunds = b2cChargsPaidAccAvlFunds;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MpesaTransactions)) {
            return false;
        }
        MpesaTransactions other = (MpesaTransactions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "io.kentec.SaccoMobile.entity.MpesaTransactions[" +
                "id=" + id +
                ']';
    }
}
