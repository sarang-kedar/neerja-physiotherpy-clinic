package com.neerjaphysio.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.neerjaphysio.model.Payment;

public class PaymentDTO {

    private Long id;
    private Double amountPaid;
    private LocalDate paymentDate;
    private String paymentMode;
    private String transactionId;
    private String chequeNumber;
    private String bankName;
    private String utrNumber;
    private String referenceNumber;
    private String remarks;
    private Integer sessionCount;
    private String auditDetails;

    public PaymentDTO() {
				
	}

	public PaymentDTO(Long id, Double amountPaid, LocalDate paymentDate, String paymentMode, String transactionId,
			String chequeNumber, String bankName, String utrNumber, String referenceNumber, String remarks,
			Integer sessionCount, String auditDetails) {
		super();
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
		this.sessionCount = sessionCount;
		this.auditDetails = auditDetails;
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

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
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

	public Integer getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(Integer sessionCount) {
		this.sessionCount = sessionCount;
	}

	public String getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(String auditDetails) {
		this.auditDetails = auditDetails;
	}

	// Builder Method
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final PaymentDTO dto;

        public Builder() {
            dto = new PaymentDTO();
        }

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder amountPaid(Double amountPaid) {
            dto.amountPaid = amountPaid;
            return this;
        }

        public Builder paymentDate(LocalDate paymentDate) {
            dto.paymentDate = paymentDate;
            return this;
        }

        public Builder paymentMode(String paymentMode) {
            dto.paymentMode = paymentMode;
            return this;
        }

        public Builder transactionId(String transactionId) {
            dto.transactionId = transactionId;
            return this;
        }

        public Builder chequeNumber(String chequeNumber) {
            dto.chequeNumber = chequeNumber;
            return this;
        }

        public Builder bankName(String bankName) {
            dto.bankName = bankName;
            return this;
        }

        public Builder utrNumber(String utrNumber) {
            dto.utrNumber = utrNumber;
            return this;
        }

        public Builder referenceNumber(String referenceNumber) {
            dto.referenceNumber = referenceNumber;
            return this;
        }

        public Builder remarks(String remarks) {
            dto.remarks = remarks;
            return this;
        }

        public Builder sessionCount(Integer sessionCount) {
            dto.sessionCount = sessionCount;
            return this;
        }

        public Builder auditDetails(String auditDetails) {
            dto.auditDetails = auditDetails;
            return this;
        }

        public PaymentDTO build() {
            return dto;
        }
    }

    // fromEntity Method
    public static PaymentDTO fromEntity(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .paymentMode(payment.getPaymentMode().getDisplayName())
				.transactionId(payment.getTransactionId())
				.chequeNumber(payment.getChequeNumber())
				.bankName(payment.getBankName())
				.utrNumber(payment.getUtrNumber())
				.referenceNumber(payment.getReferenceNumber())
				.remarks(payment.getRemarks())
				// Avoid initializing lazy treatmentSessions collection here to prevent LazyInitializationException
				.sessionCount(null)
				.auditDetails(payment.getAuditDetails())
                .build();
    }

	@Override
	public int hashCode() {
		return Objects.hash(amountPaid, auditDetails, bankName, chequeNumber, id, paymentDate, paymentMode,
				referenceNumber, remarks, sessionCount, transactionId, utrNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentDTO other = (PaymentDTO) obj;
		return Objects.equals(amountPaid, other.amountPaid) && Objects.equals(auditDetails, other.auditDetails)
				&& Objects.equals(bankName, other.bankName) && Objects.equals(chequeNumber, other.chequeNumber)
				&& Objects.equals(id, other.id) && Objects.equals(paymentDate, other.paymentDate)
				&& Objects.equals(paymentMode, other.paymentMode)
				&& Objects.equals(referenceNumber, other.referenceNumber) && Objects.equals(remarks, other.remarks)
				&& Objects.equals(sessionCount, other.sessionCount)
				&& Objects.equals(transactionId, other.transactionId) && Objects.equals(utrNumber, other.utrNumber);
	}

	@Override
	public String toString() {
		return "PaymentDTO [id=" + id + ", amountPaid=" + amountPaid + ", paymentDate=" + paymentDate + ", paymentMode="
				+ paymentMode + ", transactionId=" + transactionId + ", chequeNumber=" + chequeNumber + ", bankName="
				+ bankName + ", utrNumber=" + utrNumber + ", referenceNumber=" + referenceNumber + ", remarks="
				+ remarks + ", sessionCount=" + sessionCount + ", auditDetails=" + auditDetails + "]";
	}
    
    
}

