package com.neerjaphysio.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.ApiResponse;
import com.neerjaphysio.dto.InvoiceDTO;
import com.neerjaphysio.dto.PatientDTO;
import com.neerjaphysio.model.Invoice;
import com.neerjaphysio.service.InvoiceService;
import com.neerjaphysio.service.PatientService;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PatientService patientService;

    public InvoiceController(InvoiceService invoiceService, PatientService patientService) {
        this.invoiceService = invoiceService;
        this.patientService = patientService;
    }

    /**
     * Generate and save an invoice, returns invoice metadata + invoiceId so the
     * frontend can immediately fetch and display the PDF.
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<InvoiceDTO>> generateInvoice(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invoiceDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        Invoice invoice = invoiceService.generateAndSaveInvoice(patientId, invoiceDate, fromDate, toDate);
        InvoiceDTO dto = toDTO(invoice);
        return ResponseEntity.ok(ApiResponse.success("Invoice generated successfully", dto));
    }

    /**
     * Download / view the PDF for a specific invoice.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePDF(@PathVariable Long id) {
        byte[] pdfBytes = invoiceService.getInvoicePdfBytes(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("invoice_" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    /**
     * List all invoices for a patient.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByPatient(
            @PathVariable Long patientId) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved", invoices));
    }

    /**
     * List all invoices.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success("Invoices retrieved", invoices));
    }

    /**
     * Get form data for invoice generation (patients list).
     */
    @GetMapping("/form-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFormData() {
        Map<String, Object> data = new HashMap<>();
        List<PatientDTO> patients = patientService.getPatientsByDateRange(LocalDate.now().minusYears(5), LocalDate.now());
        data.put("patients", patients);
        return ResponseEntity.ok(ApiResponse.success("Form data", data));
    }

    private InvoiceDTO toDTO(Invoice inv) {
        return new InvoiceDTO(
                inv.getId(),
                inv.getInvoiceNumber(),
                inv.getInvoiceDate() != null ? inv.getInvoiceDate().toString() : null,
                inv.getPatient() != null ? inv.getPatient().getId() : null,
                inv.getPatient() != null ? inv.getPatient().getName() : null,
                inv.getFromDate() != null ? inv.getFromDate().toString() : null,
                inv.getToDate() != null ? inv.getToDate().toString() : null,
                inv.getTotalAmount(),
                inv.getAmountPaid(),
                inv.getBalanceDue(),
                inv.getCreatedAt() != null ? inv.getCreatedAt().toString() : null
        );
    }
}
