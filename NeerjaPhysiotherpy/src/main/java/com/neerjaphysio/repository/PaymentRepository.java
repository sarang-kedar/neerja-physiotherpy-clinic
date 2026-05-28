package com.neerjaphysio.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neerjaphysio.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentDateBetweenOrderByPaymentDateDesc(LocalDate startDate, LocalDate endDate);

    Page<Payment> findByPaymentDateBetweenOrderByPaymentDateDesc(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentMode = :mode AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentModeAndDateRange(
            @Param("mode") Payment.PaymentMode mode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.paymentDate = :date")
    Double getDailyRevenue(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE WEEK(p.paymentDate) = WEEK(:date) AND YEAR(p.paymentDate) = YEAR(:date)")
    Double getWeeklyRevenue(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE MONTH(p.paymentDate) = MONTH(:date) AND YEAR(p.paymentDate) = YEAR(:date)")
    Double getMonthlyRevenue(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE YEAR(p.paymentDate) = :year")
    Double getYearlyRevenue(@Param("year") int year);

    @Query("SELECT p.paymentMode, COUNT(p), COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate GROUP BY p.paymentMode")
    List<Object[]> getPaymentSummaryByMode(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Payment p WHERE p.transactionId IS NOT NULL OR p.chequeNumber IS NOT NULL OR p.utrNumber IS NOT NULL")
    List<Payment> findAllAuditablePayments();

    List<Payment> findByPaymentDateGreaterThanEqualOrderByPaymentDateDesc(LocalDate date);

    long countByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Payment p WHERE p.patient.id = :patientId AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate DESC")
    List<Payment> findByPatientIdAndPaymentDateBetween(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
