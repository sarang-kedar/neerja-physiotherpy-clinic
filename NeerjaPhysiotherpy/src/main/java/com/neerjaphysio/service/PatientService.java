package com.neerjaphysio.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.dto.PatientDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final TreatmentSessionRepository treatmentSessionRepository;

    public PatientService(PatientRepository patientRepository,
                          TreatmentSessionRepository treatmentSessionRepository) {
        this.patientRepository = patientRepository;
        this.treatmentSessionRepository = treatmentSessionRepository;
    }

    public Patient createPatient(Patient patient) {
        if (patient.getAdmissionDate() == null) {
            patient.setAdmissionDate(LocalDate.now());
        }
        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        PatientDTO dto = PatientDTO.fromEntity(patient);
        dto.setTotalSessions(treatmentSessionRepository.countSessionsByPatient(id));
        dto.setPaidSessions(treatmentSessionRepository.countPaidSessionsByPatient(id));
        dto.setTotalUnpaidAmount(treatmentSessionRepository.getTotalUnpaidAmountForPatient(id));

        return dto;
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        patient.setName(patientDetails.getName());
        patient.setAge(patientDetails.getAge());
        patient.setSex(patientDetails.getSex());
        patient.setWeight(patientDetails.getWeight());
        patient.setReferredBy(patientDetails.getReferredBy());
        patient.setMobileNumber(patientDetails.getMobileNumber());
        patient.setClinicalHistory(patientDetails.getClinicalHistory());
        patient.setDiagnosis(patientDetails.getDiagnosis());
        patient.setPatientType(patientDetails.getPatientType());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> searchPatients(String searchTerm, Pageable pageable) {
        return patientRepository.searchPatients(searchTerm, pageable)
                .map(this::enrichPatientDTO);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(this::enrichPatientDTO);
    }

    @Transactional(readOnly = true)
    public Page<PatientDTO> getPatientsByType(Patient.PatientType type, Pageable pageable) {
        return patientRepository.findByPatientType(type, pageable)
                .map(this::enrichPatientDTO);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByDateRange(LocalDate startDate, LocalDate endDate) {
        return patientRepository.findByAdmissionDateBetween(startDate, endDate)
                .stream()
                .map(this::enrichPatientDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getNewPatientCountFrom(LocalDate date) {
        return patientRepository.countNewPatientsFrom(date);
    }

    /**
     * Enriches a PatientDTO with session statistics.
     */
    private PatientDTO enrichPatientDTO(Patient patient) {
        PatientDTO dto = PatientDTO.fromEntity(patient);
        Long patientId = patient.getId();
        dto.setTotalSessions(treatmentSessionRepository.countSessionsByPatient(patientId));
        dto.setPaidSessions(treatmentSessionRepository.countPaidSessionsByPatient(patientId));
        dto.setTotalUnpaidAmount(treatmentSessionRepository.getTotalUnpaidAmountForPatient(patientId));
        return dto;
    }
}