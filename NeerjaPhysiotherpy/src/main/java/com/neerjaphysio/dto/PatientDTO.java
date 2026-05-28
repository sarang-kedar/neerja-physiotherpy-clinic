package com.neerjaphysio.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.neerjaphysio.model.Patient;

public class PatientDTO {

    private Long id;
    private String name;
    private Integer age;
    private String sex;
    private Double weight;
    private String referredBy;
    private String mobileNumber;
    private String clinicalHistory;
    private String diagnosis;
    private String patientType;
    private LocalDate admissionDate;
    private Long totalSessions;
    private Long paidSessions;
    private Double totalUnpaidAmount;

    
    public PatientDTO() {
				
	}
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getClinicalHistory() {
		return clinicalHistory;
	}

	public void setClinicalHistory(String clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getPatientType() {
		return patientType;
	}

	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}

	public LocalDate getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(LocalDate admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Long getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Long totalSessions) {
		this.totalSessions = totalSessions;
	}

	public Long getPaidSessions() {
		return paidSessions;
	}

	public void setPaidSessions(Long paidSessions) {
		this.paidSessions = paidSessions;
	}

	public Double getTotalUnpaidAmount() {
		return totalUnpaidAmount;
	}

	public void setTotalUnpaidAmount(Double totalUnpaidAmount) {
		this.totalUnpaidAmount = totalUnpaidAmount;
	}

	public PatientDTO(Long id, String name, Integer age, String sex, Double weight, String referredBy,
			String mobileNumber, String clinicalHistory, String diagnosis, String patientType,
			LocalDate admissionDate, Long totalSessions, Long paidSessions, Double totalUnpaidAmount) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.weight = weight;
		this.referredBy = referredBy;
		this.mobileNumber = mobileNumber;
		this.clinicalHistory = clinicalHistory;
		this.diagnosis = diagnosis;
		this.patientType = patientType;
		this.admissionDate = admissionDate;
		this.totalSessions = totalSessions;
		this.paidSessions = paidSessions;
		this.totalUnpaidAmount = totalUnpaidAmount;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(admissionDate, age, clinicalHistory, diagnosis, id, name, paidSessions, patientType,
				referredBy, sex, totalSessions, totalUnpaidAmount, weight);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientDTO other = (PatientDTO) obj;
		return Objects.equals(admissionDate, other.admissionDate) && Objects.equals(age, other.age)
				&& Objects.equals(clinicalHistory, other.clinicalHistory) && Objects.equals(diagnosis, other.diagnosis)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(paidSessions, other.paidSessions) && Objects.equals(patientType, other.patientType)
				&& Objects.equals(referredBy, other.referredBy) && Objects.equals(sex, other.sex)
				&& Objects.equals(totalSessions, other.totalSessions)
				&& Objects.equals(totalUnpaidAmount, other.totalUnpaidAmount) && Objects.equals(weight, other.weight);
	}
	
	
	@Override
	public String toString() {
		return "PatientDTO [id=" + id + ", name=" + name + ", age=" + age + ", sex=" + sex + ", weight=" + weight
				+ ", referredBy=" + referredBy + ", clinicalHistory=" + clinicalHistory + ", diagnosis=" + diagnosis
				+ ", patientType=" + patientType + ", admissionDate=" + admissionDate + ", totalSessions="
				+ totalSessions + ", paidSessions=" + paidSessions + ", totalUnpaidAmount=" + totalUnpaidAmount + "]";
	}
	// Builder Method
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PatientDTO dto;

        public Builder() {
            dto = new PatientDTO();
        }

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder name(String name) {
            dto.name = name;
            return this;
        }

        public Builder age(Integer age) {
            dto.age = age;
            return this;
        }

        public Builder sex(String sex) {
            dto.sex = sex;
            return this;
        }

        public Builder weight(Double weight) {
            dto.weight = weight;
            return this;
        }

        public Builder referredBy(String referredBy) {
            dto.referredBy = referredBy;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            dto.mobileNumber = mobileNumber;
            return this;
        }

        public Builder clinicalHistory(String clinicalHistory) {
            dto.clinicalHistory = clinicalHistory;
            return this;
        }

        public Builder diagnosis(String diagnosis) {
            dto.diagnosis = diagnosis;
            return this;
        }

        public Builder patientType(String patientType) {
            dto.patientType = patientType;
            return this;
        }

        public Builder admissionDate(LocalDate admissionDate) {
            dto.admissionDate = admissionDate;
            return this;
        }

        public Builder totalSessions(Long totalSessions) {
            dto.totalSessions = totalSessions;
            return this;
        }

        public Builder paidSessions(Long paidSessions) {
            dto.paidSessions = paidSessions;
            return this;
        }

        public Builder totalUnpaidAmount(Double totalUnpaidAmount) {
            dto.totalUnpaidAmount = totalUnpaidAmount;
            return this;
        }

        public PatientDTO build() {
            return dto;
        }
    }

    // fromEntity Method
    public static PatientDTO fromEntity(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .sex(patient.getSex())
                .weight(patient.getWeight())
                .referredBy(patient.getReferredBy())
                .mobileNumber(patient.getMobileNumber())
                .clinicalHistory(patient.getClinicalHistory())
                .diagnosis(patient.getDiagnosis())
                .patientType(patient.getPatientType().getDisplayName())
                .admissionDate(patient.getAdmissionDate())
                .build();
    }
}
