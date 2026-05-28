package com.neerjaphysio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    long countByInvoiceDateStartingWith(String prefix);
    
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.patient ORDER BY i.createdAt DESC")
    List<Invoice> findAllWithPatient();
}
