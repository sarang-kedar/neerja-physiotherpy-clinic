package com.neerjaphysio.model;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "treatment_sessions")
public class TreatmentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_time")
    private LocalTime sessionTime;

    @NotBlank(message = "Therapy type is required")
    @Column(length = 255)
    private String therapyDescription;

	@NotBlank(message = "Session location is required")
	@Column(length = 100, nullable = false)
    private String location; // Clinic, Home, Online

    @NotNull(message = "Session charge is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive")
    @Column
    private Double sessionCharge;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (isPaid == null) {
            isPaid = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(LocalDate sessionDate) {
		this.sessionDate = sessionDate;
	}

	public LocalTime getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(LocalTime sessionTime) {
		this.sessionTime = sessionTime;
	}

	public String getTherapyDescription() {
		return therapyDescription;
	}

	public void setTherapyDescription(String therapyDescription) {
		this.therapyDescription = therapyDescription;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getSessionCharge() {
		return sessionCharge;
	}

	public void setSessionCharge(Double sessionCharge) {
		this.sessionCharge = sessionCharge;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@Override
	public int hashCode() {
		// Keep hashCode stable and avoid traversing lazy relations.
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TreatmentSession other)) {
			return false;
		}
		return id != null && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "TreatmentSession [id=" + id + ", sessionDate=" + sessionDate + ", sessionTime=" + sessionTime
				+ ", therapyDescription=" + therapyDescription + ", location=" + location + ", sessionCharge="
				+ sessionCharge + ", isPaid=" + isPaid + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", patientId=" + (patient != null ? patient.getId() : null)
				+ ", paymentId=" + (payment != null ? payment.getId() : null) + "]";
	}

	public TreatmentSession(Long id, @NotNull LocalDate sessionDate, LocalTime sessionTime,
			@NotBlank(message = "Therapy type is required") String therapyDescription, String location,
			@NotNull(message = "Session charge is required") @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive") Double sessionCharge,
			Boolean isPaid, LocalDate createdAt, LocalDate updatedAt, Patient patient, Payment payment) {
		super();
		this.id = id;
		this.sessionDate = sessionDate;
		this.sessionTime = sessionTime;
		this.therapyDescription = therapyDescription;
		this.location = location;
		this.sessionCharge = sessionCharge;
		this.isPaid = isPaid;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.patient = patient;
		this.payment = payment;
	}
    
    // Getters and Setters
    //noargs constructor
		public TreatmentSession() {
			
		}
}
