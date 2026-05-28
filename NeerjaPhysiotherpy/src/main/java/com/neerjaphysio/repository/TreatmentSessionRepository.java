package com.neerjaphysio.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.TreatmentSession;

@Repository
public interface TreatmentSessionRepository extends JpaRepository<TreatmentSession, Long> {

    List<TreatmentSession> findByPatientIdOrderBySessionDateDesc(Long patientId);

    List<TreatmentSession> findByPatientIdAndIsPaidFalse(Long patientId);

    List<TreatmentSession> findBySessionDateBetweenOrderBySessionDateDesc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT ts FROM TreatmentSession ts WHERE ts.patient.id = :patientId AND ts.isPaid = false")
    List<TreatmentSession> findUnpaidSessionsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT ts FROM TreatmentSession ts WHERE ts.sessionDate BETWEEN :startDate AND :endDate AND ts.isPaid = false")
    List<TreatmentSession> findUnpaidSessionsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(ts.sessionCharge), 0) FROM TreatmentSession ts WHERE ts.patient.id = :patientId AND ts.isPaid = false")
    Double getTotalUnpaidAmountForPatient(@Param("patientId") Long patientId);

    @Query("SELECT COALESCE(SUM(ts.sessionCharge), 0) FROM TreatmentSession ts WHERE ts.sessionDate BETWEEN :startDate AND :endDate AND ts.isPaid = true")
    Double getTotalPaidAmountInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ts) FROM TreatmentSession ts WHERE ts.patient.id = :patientId")
    long countSessionsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT COUNT(ts) FROM TreatmentSession ts WHERE ts.patient.id = :patientId AND ts.isPaid = true")
    long countPaidSessionsByPatient(@Param("patientId") Long patientId);

    @Query("SELECT DISTINCT ts.location FROM TreatmentSession ts WHERE ts.location IS NOT NULL AND ts.location <> '' ORDER BY ts.location")
    List<String> findDistinctLocations();
    
    List<TreatmentSession> findByPatientIdAndSessionDateBetweenOrderBySessionDateDesc(Long patientId, LocalDate startDate, LocalDate endDate);
}
