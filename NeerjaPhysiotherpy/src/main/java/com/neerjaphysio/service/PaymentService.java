package com.neerjaphysio.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.dto.PaymentDTO;
import com.neerjaphysio.dto.RevenueAnalyticsDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Payment;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.PaymentRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TreatmentSessionRepository treatmentSessionRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          TreatmentSessionRepository treatmentSessionRepository) {
        this.paymentRepository = paymentRepository;
        this.treatmentSessionRepository = treatmentSessionRepository;
    }

    public Payment createPayment(Payment payment, List<Long> sessionIds) {
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        // Save the payment first
        Payment savedPayment = paymentRepository.save(payment);

        // Link treatment sessions to this payment and mark as paid
        Set<TreatmentSession> linkedSessions = new HashSet<>();
        for (Long sessionId : sessionIds) {
            TreatmentSession session = treatmentSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("TreatmentSession", "id", sessionId));

            session.setPayment(savedPayment);
            session.setIsPaid(true);
            treatmentSessionRepository.save(session);
            linkedSessions.add(session);
        }
        savedPayment.setTreatmentSessions(linkedSessions);

        return paymentRepository.save(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return PaymentDTO.fromEntity(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        // Unlink and mark sessions as unpaid
        for (TreatmentSession session : payment.getTreatmentSessions()) {
            session.setPayment(null);
            session.setIsPaid(false);
            treatmentSessionRepository.save(session);
        }

        paymentRepository.delete(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate, pageable)
                .map(PaymentDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByMode(Payment.PaymentMode mode, LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentModeAndDateRange(mode, startDate, endDate)
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RevenueAnalyticsDTO getRevenueAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        Double dailyRevenue = paymentRepository.getDailyRevenue(today);
        Double weeklyRevenue = paymentRepository.getWeeklyRevenue(today);
        Double monthlyRevenue = paymentRepository.getMonthlyRevenue(today);
        Double yearlyRevenue = paymentRepository.getYearlyRevenue(today.getYear());

        Long totalPayments = paymentRepository.countByPaymentDateBetween(startDate, endDate);

        // Total revenue for the requested date range
        Double totalRevenue = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate)
                .stream()
                .mapToDouble(p -> p.getAmountPaid() != null ? p.getAmountPaid() : 0.0)
                .sum();

        Long totalSessions = (long) treatmentSessionRepository
                .findBySessionDateBetweenOrderBySessionDateDesc(startDate, endDate)
                .stream()
                .filter(TreatmentSession::getIsPaid)
                .count();

        Double averagePaymentAmount = totalPayments > 0 ? totalRevenue / totalPayments : 0.0;

        // Payment mode breakdown
        List<RevenueAnalyticsDTO.PaymentModeBreakdown> breakdowns = new ArrayList<>();
        List<Object[]> modeData = paymentRepository.getPaymentSummaryByMode(startDate, endDate);

        for (Object[] row : modeData) {
            Payment.PaymentMode mode = (Payment.PaymentMode) row[0];
            Long count = ((Number) row[1]).longValue();
            Double amount = ((Number) row[2]).doubleValue();
            Double percentage = totalRevenue > 0 ? (amount / totalRevenue) * 100 : 0.0;

            breakdowns.add(RevenueAnalyticsDTO.PaymentModeBreakdown.builder()
                    .paymentMode(mode.getDisplayName())
                    .count(count)
                    .totalAmount(amount)
                    .percentage(percentage)
                    .build());
        }

        return RevenueAnalyticsDTO.builder()
                .dailyRevenue(dailyRevenue != null ? dailyRevenue : 0.0)
                .weeklyRevenue(weeklyRevenue != null ? weeklyRevenue : 0.0)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : 0.0)
                .yearlyRevenue(yearlyRevenue != null ? yearlyRevenue : 0.0)
                .totalPayments(totalPayments)
                .totalSessions(totalSessions)
                .averagePaymentAmount(averagePaymentAmount)
                .paymentModeBreakdown(breakdowns)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAuditTrail(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate)
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllAuditablePayments() {
        return paymentRepository.findAllAuditablePayments()
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getDailyRevenue(LocalDate date) {
        Double revenue = paymentRepository.getDailyRevenue(date);
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getMonthlyRevenue(YearMonth yearMonth) {
        Double revenue = paymentRepository.getMonthlyRevenue(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1));
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getYearlyRevenue(int year) {
        Double revenue = paymentRepository.getYearlyRevenue(year);
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getWeeklyRevenueBreakdown(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> weeklyBreakdown = new LinkedHashMap<>();

        List<Payment> payments = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate);

        // Use ISO week fields for standard week numbers
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);
        Map<Integer, Double> weekMap = new TreeMap<>();
        for (Payment payment : payments) {
            int week = payment.getPaymentDate().get(weekFields.weekOfWeekBasedYear());
            weekMap.merge(week, payment.getAmountPaid() != null ? payment.getAmountPaid() : 0.0, Double::sum);
        }

        weekMap.forEach((week, amount) -> weeklyBreakdown.put("Week " + week, amount));

        return weeklyBreakdown;
    }
}