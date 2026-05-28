package com.neerjaphysio.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.ApiResponse;
import com.neerjaphysio.dto.BillingDTO;
import com.neerjaphysio.dto.TreatmentSessionDTO;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.service.TreatmentSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TreatmentSessionController {

    private final TreatmentSessionService sessionService;

    public TreatmentSessionController(TreatmentSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TreatmentSessionDTO>> createSession(@Valid @RequestBody TreatmentSession session) {
        TreatmentSession created = sessionService.createSession(session);
        return ResponseEntity.ok(ApiResponse.success("Session created successfully", TreatmentSessionDTO.fromEntity(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TreatmentSessionDTO>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentSession sessionDetails) {
        TreatmentSession updated = sessionService.updateSession(id, sessionDetails);
        return ResponseEntity.ok(ApiResponse.success("Session updated successfully", TreatmentSessionDTO.fromEntity(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<TreatmentSessionDTO>>> getSessionsByPatient(@PathVariable Long patientId) {
        List<TreatmentSessionDTO> sessions = sessionService.getSessionsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved successfully", sessions));
    }

    @GetMapping("/patient/{patientId}/unpaid")
    public ResponseEntity<ApiResponse<List<TreatmentSessionDTO>>> getUnpaidSessionsByPatient(@PathVariable Long patientId) {
        List<TreatmentSessionDTO> sessions = sessionService.getUnpaidSessionsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Unpaid sessions retrieved successfully", sessions));
    }

    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<String>>> getSessionLocations() {
        List<String> locations = sessionService.getAvailableLocations();
        return ResponseEntity.ok(ApiResponse.success("Session locations retrieved successfully", locations));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<TreatmentSessionDTO>>> getSessionsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TreatmentSessionDTO> sessions = sessionService.getSessionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved successfully", sessions));
    }

    @GetMapping("/unpaid/date-range")
    public ResponseEntity<ApiResponse<List<TreatmentSessionDTO>>> getUnpaidSessionsInDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<TreatmentSessionDTO> sessions = sessionService.getUnpaidSessionsInDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Unpaid sessions retrieved successfully", sessions));
    }

    @GetMapping("/billing/{patientId}")
    public ResponseEntity<ApiResponse<BillingDTO>> getBillingForPatient(
            @PathVariable Long patientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        BillingDTO billing = sessionService.getBillingForPatient(patientId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Billing retrieved successfully", billing));
    }

    @GetMapping("/stats/unpaid-amount/{patientId}")
    public ResponseEntity<ApiResponse<Double>> getTotalUnpaidAmount(@PathVariable Long patientId) {
        Double amount = sessionService.getTotalUnpaidAmountForPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Unpaid amount retrieved successfully", amount));
    }

    @GetMapping("/stats/paid-amount")
    public ResponseEntity<ApiResponse<Double>> getTotalPaidAmountInRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Double amount = sessionService.getTotalPaidAmountInDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Paid amount retrieved successfully", amount));
    }
}