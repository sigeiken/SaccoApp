package io.kentec.SaccoMobile.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "TRANSACTIONS")
public class Transactions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "TRX_REFERENCE")
    private String trxReference;
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;
    @Column(name = "PROCESSING_STATUS")
    private String processingStatus;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TIME_PROCESSED")
    private Instant timeProcessed;
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Customers customerId;

    public Transactions() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrxReference() {
        return trxReference;
    }

    public void setTrxReference(String trxReference) {
        this.trxReference = trxReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimeProcessed() {
        return timeProcessed;
    }

    public void setTimeProcessed(Instant timeProcessed) {
        this.timeProcessed = timeProcessed;
    }

    public Customers getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Customers customerId) {
        this.customerId = customerId;
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
        if (!(object instanceof Transactions)) {
            return false;
        }
        Transactions other = (Transactions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "io.kentec.SaccoMobile.entity.Transactions[" +
                "id=" + id +
                ']';
    }
}
