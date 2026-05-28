package com.neerjaphysio.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Column(nullable = false)
    private Double amountPaid;

    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode;

    // Audit Fields for Different Payment Modes
    @Column(length = 50)
    private String transactionId; // For UPI, NEFT, RTGS

    @Column(length = 20)
    private String chequeNumber; // For Cheque payments

    @Column(length = 100)
    private String bankName; // For Cheque/Bank transfers

    @Column(length = 50)
    private String utrNumber; // For RTGS/NEFT

    @Column(length = 50)
    private String referenceNumber; // General reference

    @Column(columnDefinition = "TEXT")
    private String remarks; // Additional notes for audit

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<TreatmentSession> treatmentSessions = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private Patient patient;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PaymentMode {
        CASH("Cash"),
        UPI("UPI Transfer"),
        CHEQUE("Cheque"),
        RTGS("RTGS"),
        NEFT("NEFT"),
        DEMAND_DRAFT("Demand Draft");

        private final String displayName;

        PaymentMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Utility method to get audit details based on payment mode
    public String getAuditDetails() {
        return switch (this.paymentMode) {
            case CASH -> "Cash payment - " + remarks;
            case UPI -> "UPI Transaction ID: " + transactionId;
            case CHEQUE -> "Cheque No: " + chequeNumber + ", Bank: " + bankName;
            case RTGS -> "RTGS UTR: " + utrNumber + ", Bank: " + bankName;
            case NEFT -> "NEFT UTR: " + utrNumber + ", Bank: " + bankName;
            case DEMAND_DRAFT -> "DD No: " + chequeNumber + ", Bank: " + bankName;
        };
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Set<TreatmentSession> getTreatmentSessions() {
		if (treatmentSessions == null) {
			treatmentSessions = new HashSet<TreatmentSession>();
		}
		return treatmentSessions;
	}

	public void setTreatmentSessions(Set<TreatmentSession> treatmentSessions) {
		this.treatmentSessions = treatmentSessions == null ? new HashSet<TreatmentSession>() : treatmentSessions;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public int hashCode() {
		// Keep hashCode stable and avoid traversing bidirectional collections.
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Payment other)) {
			return false;
		}
		return id != null && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", amountPaid=" + amountPaid + ", paymentDate=" + paymentDate + ", paymentMode="
				+ paymentMode + ", transactionId=" + transactionId + ", chequeNumber=" + chequeNumber + ", bankName="
				+ bankName + ", utrNumber=" + utrNumber + ", referenceNumber=" + referenceNumber + ", remarks="
				+ remarks + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", treatmentSessionCount="
				+ (treatmentSessions == null ? 0 : treatmentSessions.size()) + "]";
	}
    
   //noargs constructor 
	public Payment() {
	}

	// All args constructor
	public Payment(Long id, Double amountPaid, LocalDate paymentDate, PaymentMode paymentMode, String transactionId,
				   String chequeNumber, String bankName, String utrNumber, String referenceNumber, String remarks,
				   LocalDateTime createdAt, LocalDateTime updatedAt, Set<TreatmentSession> treatmentSessions) {
		this.id = id;
		this.amountPaid = amountPaid;
		this.paymentDate = paymentDate;
		this.paymentMode = paymentMode;
		this.transactionId = transactionId;
		this.chequeNumber = chequeNumber;
		this.bankName = bankName;
		this.utrNumber = utrNumber;
		this.referenceNumber = referenceNumber;
		this.remarks = remarks;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.treatmentSessions = treatmentSessions;
	}
}
