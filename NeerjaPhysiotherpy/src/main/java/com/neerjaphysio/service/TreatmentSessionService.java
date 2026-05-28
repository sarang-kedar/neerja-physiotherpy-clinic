package com.neerjaphysio.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.dto.BillingDTO;
import com.neerjaphysio.dto.TreatmentSessionDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class TreatmentSessionService {

    private final TreatmentSessionRepository sessionRepository;
    private final PatientRepository patientRepository;

    public TreatmentSessionService(TreatmentSessionRepository sessionRepository,
                                   PatientRepository patientRepository) {
        this.sessionRepository = sessionRepository;
        this.patientRepository = patientRepository;
    }

    public TreatmentSession createSession(TreatmentSession session) {
        if (session.getSessionDate() == null) {
            session.setSessionDate(LocalDate.now());
        }
        return sessionRepository.save(session);
    }

    public TreatmentSession updateSession(Long id, TreatmentSession sessionDetails) {
        TreatmentSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentSession", "id", id));

        session.setSessionDate(sessionDetails.getSessionDate());
        session.setSessionTime(sessionDetails.getSessionTime());
        session.setTherapyDescription(sessionDetails.getTherapyDescription());
        session.setLocation(sessionDetails.getLocation());
        session.setSessionCharge(sessionDetails.getSessionCharge());

        return sessionRepository.save(session);
    }

    public void deleteSession(Long id) {
        TreatmentSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentSession", "id", id));
        sessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public List<TreatmentSessionDTO> getSessionsByPatient(Long patientId) {
        return sessionRepository.findByPatientIdOrderBySessionDateDesc(patientId).stream()
                .map(TreatmentSessionDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreatmentSessionDTO> getUnpaidSessionsByPatient(Long patientId) {
        return sessionRepository.findUnpaidSessionsByPatient(patientId).stream()
                .map(TreatmentSessionDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreatmentSessionDTO> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return sessionRepository.findBySessionDateBetweenOrderBySessionDateDesc(startDate, endDate).stream()
                .map(TreatmentSessionDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreatmentSessionDTO> getUnpaidSessionsInDateRange(LocalDate startDate, LocalDate endDate) {
        return sessionRepository.findUnpaidSessionsInDateRange(startDate, endDate).stream()
                .map(TreatmentSessionDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillingDTO getBillingForPatient(Long patientId, LocalDate startDate, LocalDate endDate) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        // Use the dedicated repository query instead of filtering in memory
        List<TreatmentSession> sessions =
                sessionRepository.findByPatientIdAndSessionDateBetweenOrderBySessionDateDesc(patientId, startDate, endDate);

        double grandTotal = sessions.stream()
                .mapToDouble(s -> s.getSessionCharge() != null ? s.getSessionCharge() : 0.0)
                .sum();

        double amountPaid = sessions.stream()
                .filter(TreatmentSession::getIsPaid)
                .mapToDouble(s -> s.getSessionCharge() != null ? s.getSessionCharge() : 0.0)
                .sum();

        return new BillingDTO(patientId, patient.getName(), startDate, endDate,
                sessions.stream().map(TreatmentSessionDTO::fromEntity).collect(Collectors.toList()),
                grandTotal, amountPaid, grandTotal - amountPaid);
    }

    @Transactional(readOnly = true)
    public Double getTotalUnpaidAmountForPatient(Long patientId) {
        Double amount = sessionRepository.getTotalUnpaidAmountForPatient(patientId);
        return amount != null ? amount : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getTotalPaidAmountInDateRange(LocalDate startDate, LocalDate endDate) {
        Double amount = sessionRepository.getTotalPaidAmountInDateRange(startDate, endDate);
        return amount != null ? amount : 0.0;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableLocations() {
        return sessionRepository.findDistinctLocations();
    }
}