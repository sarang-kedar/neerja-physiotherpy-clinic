package com.neerjaphysio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    
    @Query("SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.patient LEFT JOIN FETCH r.payment ORDER BY r.createdAt DESC")
    List<Receipt> findAllWithPatientAndPayment();
}
