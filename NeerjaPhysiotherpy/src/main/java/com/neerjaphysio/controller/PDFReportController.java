package com.neerjaphysio.controller;

import java.time.LocalDate;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.BillingDTO;
import com.neerjaphysio.service.PDFGenerationService;
import com.neerjaphysio.service.TreatmentSessionService;

@RestController
@RequestMapping("/api/reports/pdf")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PDFReportController {

    private final PDFGenerationService pdfService;
    private final TreatmentSessionService sessionService;

    public PDFReportController(PDFGenerationService pdfService, TreatmentSessionService sessionService) {
        this.pdfService = pdfService;
        this.sessionService = sessionService;
    }

    @GetMapping("/session-details")
    public ResponseEntity<byte[]> generateSessionDetailsPDF(
            @RequestParam Long patientId,
            @RequestParam(defaultValue = "1") int sessionStart,
            @RequestParam(defaultValue = "15") int sessionEnd) {

        byte[] pdfContent = pdfService.generateSessionDetailsPDF(patientId, sessionStart, sessionEnd);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("session_details_" + patientId + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/medical-certificate")
    public ResponseEntity<byte[]> generateMedicalCertificatePDF(
            @RequestParam Long patientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate certificateDate,
            @RequestParam(required = false) String surgeonName) {

        byte[] pdfContent = pdfService.generateMedicalCertificatePDF(patientId, certificateDate, startDate, endDate, surgeonName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("medical_certificate_" + patientId + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/invoice")
    public ResponseEntity<byte[]> generateInvoicePDF(
            @RequestParam Long patientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        // Load full billing data before generating the PDF
        BillingDTO billing = sessionService.getBillingForPatient(patientId, startDate, endDate);
        byte[] pdfContent = pdfService.generateInvoicePDF(billing);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("invoice_" + patientId + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/receipt")
    public ResponseEntity<byte[]> generateReceiptPDF(
            @RequestParam Long patientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Double amountReceived) {

        byte[] pdfContent = pdfService.generateReceiptPDF(patientId, startDate, endDate, amountReceived,0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("receipt_" + patientId + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}