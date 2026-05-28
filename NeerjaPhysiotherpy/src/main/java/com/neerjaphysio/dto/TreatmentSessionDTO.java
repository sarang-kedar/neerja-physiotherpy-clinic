package com.neerjaphysio.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import com.neerjaphysio.model.TreatmentSession;

public class TreatmentSessionDTO {

    private Long id;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private String therapyDescription;
    private String location;
    private Double sessionCharge;
    private Boolean isPaid;
    private Long patientId;
    private String patientName;


    // Builder Method
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TreatmentSessionDTO dto;

        public Builder() {
            dto = new TreatmentSessionDTO();
        }

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder sessionDate(LocalDate sessionDate) {
            dto.sessionDate = sessionDate;
            return this;
        }

        public Builder sessionTime(LocalTime sessionTime) {
            dto.sessionTime = sessionTime;
            return this;
        }

        public Builder therapyDescription(String therapyDescription) {
            dto.therapyDescription = therapyDescription;
            return this;
        }

        public Builder location(String location) {
            dto.location = location;
            return this;
        }

        public Builder sessionCharge(Double sessionCharge) {
            dto.sessionCharge = sessionCharge;
            return this;
        }

        public Builder isPaid(Boolean isPaid) {
            dto.isPaid = isPaid;
            return this;
        }

        public Builder patientId(Long patientId) {
            dto.patientId = patientId;
            return this;
        }

        public Builder patientName(String patientName) {
            dto.patientName = patientName;
            return this;
        }

        public TreatmentSessionDTO build() {
            return dto;
        }
    }

    // fromEntity Method
    public static TreatmentSessionDTO fromEntity(TreatmentSession session) {
        return TreatmentSessionDTO.builder()
                .id(session.getId())
                .sessionDate(session.getSessionDate())
                .sessionTime(session.getSessionTime())
                .therapyDescription(session.getTherapyDescription())
                .location(session.getLocation())
                .sessionCharge(session.getSessionCharge())
                .isPaid(session.getIsPaid())
                .patientId(session.getPatient().getId())
                .patientName(session.getPatient().getName())
                .build();
    }

    // Getters and Setters

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

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
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

	@Override
	public int hashCode() {
		return Objects.hash(id, isPaid, location, patientId, patientName, sessionCharge, sessionDate, sessionTime,
				therapyDescription);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreatmentSessionDTO other = (TreatmentSessionDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(isPaid, other.isPaid)
				&& Objects.equals(location, other.location) && Objects.equals(patientId, other.patientId)
				&& Objects.equals(patientName, other.patientName) && Objects.equals(sessionCharge, other.sessionCharge)
				&& Objects.equals(sessionDate, other.sessionDate) && Objects.equals(sessionTime, other.sessionTime)
				&& Objects.equals(therapyDescription, other.therapyDescription);
	}

	@Override
	public String toString() {
		return "TreatmentSessionDTO [id=" + id + ", sessionDate=" + sessionDate + ", sessionTime=" + sessionTime
				+ ", therapyDescription=" + therapyDescription + ", location=" + location + ", sessionCharge="
				+ sessionCharge + ", isPaid=" + isPaid + ", patientId=" + patientId + ", patientName=" + patientName
				+ "]";
	}
    
    
    
}
