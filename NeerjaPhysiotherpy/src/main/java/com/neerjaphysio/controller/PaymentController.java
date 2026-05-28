package com.neerjaphysio.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.ApiResponse;
import com.neerjaphysio.dto.PaymentDTO;
import com.neerjaphysio.dto.RevenueAnalyticsDTO;
import com.neerjaphysio.model.Payment;
import com.neerjaphysio.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(
            @Valid @RequestBody Payment payment,
            @RequestParam List<Long> sessionIds) {
        Payment created = paymentService.createPayment(payment, sessionIds);
        return ResponseEntity.ok(ApiResponse.success("Payment created and sessions marked as paid", PaymentDTO.fromEntity(created)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPayment(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", payment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted and sessions marked as unpaid", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentDTO>>> getAllPayments(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDTO> payments = paymentService.getAllPayments(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", payments));
    }

    @GetMapping("/mode/{mode}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByMode(
            @PathVariable String mode,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            Payment.PaymentMode paymentMode = Payment.PaymentMode.valueOf(mode.toUpperCase());
            List<PaymentDTO> payments = paymentService.getPaymentsByMode(paymentMode, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", payments));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment mode: " + mode + ". Valid values: CASH, UPI, CHEQUE, RTGS, NEFT, DEMAND_DRAFT");
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<RevenueAnalyticsDTO>> getRevenueAnalytics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        RevenueAnalyticsDTO analytics = paymentService.getRevenueAnalytics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
    }

    @GetMapping("/audit-trail")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAuditTrail(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<PaymentDTO> auditTrail = paymentService.getAuditTrail(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Audit trail retrieved successfully", auditTrail));
    }

    @GetMapping("/audit/all")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllAuditablePayments() {
        List<PaymentDTO> payments = paymentService.getAllAuditablePayments();
        return ResponseEntity.ok(ApiResponse.success("Auditable payments retrieved successfully", payments));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<ApiResponse<Double>> getDailyRevenue(@RequestParam LocalDate date) {
        Double revenue = paymentService.getDailyRevenue(date);
        return ResponseEntity.ok(ApiResponse.success("Daily revenue retrieved successfully", revenue));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<Double>> getMonthlyRevenue(@RequestParam String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        Double revenue = paymentService.getMonthlyRevenue(ym);
        return ResponseEntity.ok(ApiResponse.success("Monthly revenue retrieved successfully", revenue));
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<ApiResponse<Double>> getYearlyRevenue(@RequestParam int year) {
        Double revenue = paymentService.getYearlyRevenue(year);
        return ResponseEntity.ok(ApiResponse.success("Yearly revenue retrieved successfully", revenue));
    }

    @GetMapping("/revenue/weekly")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getWeeklyRevenueBreakdown(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Double> breakdown = paymentService.getWeeklyRevenueBreakdown(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Weekly breakdown retrieved successfully", breakdown));
    }
}