package com.neerjaphysio.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class BillingDTO {
    private Long patientId;
    private String patientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TreatmentSessionDTO> sessions;
    private Double grandTotal;
    private Double amountPaid;
    private Double balanceDue;
    
    public BillingDTO() {
    			
    }
	public BillingDTO(Long patientId, String patientName, LocalDate startDate, LocalDate endDate,
			List<TreatmentSessionDTO> sessions, Double grandTotal, Double amountPaid, Double balanceDue) {
		super();
		this.patientId = patientId;
		this.patientName = patientName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sessions = sessions;
		this.grandTotal = grandTotal;
		this.amountPaid = amountPaid;
		this.balanceDue = balanceDue;
	}
	public Long getPatientId() {
		return patientId;
	}
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public List<TreatmentSessionDTO> getSessions() {
		return sessions;
	}
	public void setSessions(List<TreatmentSessionDTO> sessions) {
		this.sessions = sessions;
	}
	public Double getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}
	public Double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public Double getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(Double balanceDue) {
		this.balanceDue = balanceDue;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(amountPaid, balanceDue, endDate, grandTotal, patientId, patientName, sessions, startDate);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BillingDTO other = (BillingDTO) obj;
		return Objects.equals(amountPaid, other.amountPaid) && Objects.equals(balanceDue, other.balanceDue)
				&& Objects.equals(endDate, other.endDate) && Objects.equals(grandTotal, other.grandTotal)
				&& Objects.equals(patientId, other.patientId) && Objects.equals(patientName, other.patientName)
				&& Objects.equals(sessions, other.sessions) && Objects.equals(startDate, other.startDate);
	}

	@Override
	public String toString() {
		return "BillingDTO [patientId=" + patientId + ", patientName=" + patientName + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", sessionCount=" + (sessions != null ? sessions.size() : 0)
				+ ", grandTotal=" + grandTotal + ", amountPaid=" + amountPaid + ", balanceDue=" + balanceDue + "]";
	}
}

