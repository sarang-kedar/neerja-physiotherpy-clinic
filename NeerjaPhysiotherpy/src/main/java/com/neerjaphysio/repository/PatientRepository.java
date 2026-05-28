package com.neerjaphysio.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Using CAST to String ensures compatibility with Hibernate 7 validation
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(CAST(p.name AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CAST(p.diagnosis AS string)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Patient> searchPatients(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Patient> findByPatientType(Patient.PatientType patientType, Pageable pageable);

    List<Patient> findByAdmissionDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<Patient> findByNameAndAdmissionDate(String name, LocalDate admissionDate);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.admissionDate >= :date")
    long countNewPatientsFrom(@Param("date") LocalDate date);
}