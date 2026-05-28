package com.neerjaphysio.model;

import java.time.LocalDate;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 0)
    @Max(value = 150)
    @Column(nullable = false)
    private Integer age;

    @NotBlank(message = "Sex is required")
    @Column(length = 10)
    private String sex;

    @NotNull(message = "Weight is required")
    @Positive
    @Column(nullable = false)
    private Double weight;

    @Column(length = 100)
    private String referredBy;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    @Column(length = 15)
    private String mobileNumber;

    // Changed: Removed @Lob to support Java 25 / Hibernate 7 String functions
    @Column(columnDefinition = "TEXT")
    private String clinicalHistory;

    // Changed: Removed @Lob to fix the LOWER() function error in Repository
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientType patientType;

    @NotNull
    @Column(nullable = false)
    private LocalDate admissionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TreatmentSession> treatmentSessions = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    public enum PatientType {
        CLINIC("Clinic Visit"),
        HOME("Home Based Physiotherapy"),
        ONLINE("Online Consultation");

        private final String displayName;
        PatientType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public String getReferredBy() { return referredBy; }
    public void setReferredBy(String referredBy) { this.referredBy = referredBy; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getClinicalHistory() { return clinicalHistory; }
    public void setClinicalHistory(String clinicalHistory) { this.clinicalHistory = clinicalHistory; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public PatientType getPatientType() { return patientType; }
    public void setPatientType(PatientType patientType) { this.patientType = patientType; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }

    public Set<TreatmentSession> getTreatmentSessions() { return treatmentSessions; }
    public void setTreatmentSessions(Set<TreatmentSession> treatmentSessions) { this.treatmentSessions = treatmentSessions; }

    public void addTreatmentSession(TreatmentSession session) {
        treatmentSessions.add(session);
        session.setPatient(this);
    }

    public Patient() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}