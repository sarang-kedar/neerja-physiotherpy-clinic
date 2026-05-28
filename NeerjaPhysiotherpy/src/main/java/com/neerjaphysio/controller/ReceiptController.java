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
import com.neerjaphysio.dto.PatientDTO;
import com.neerjaphysio.dto.PaymentDTO;
import com.neerjaphysio.dto.ReceiptDTO;
import com.neerjaphysio.model.Receipt;
import com.neerjaphysio.service.PaymentService;
import com.neerjaphysio.service.PatientService;
import com.neerjaphysio.service.ReceiptService;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReceiptController {

    private final ReceiptService receiptService;
    private final PatientService patientService;
    private final PaymentService paymentService;

    public ReceiptController(ReceiptService receiptService,
                            PatientService patientService,
                            PaymentService paymentService) {
        this.receiptService = receiptService;
        this.patientService = patientService;
        this.paymentService = paymentService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ReceiptDTO>> generate(
            @RequestParam Long patientId,
            @RequestParam(required = false) Long paymentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receiptDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam Double amountReceived,
            @RequestParam(required = false, defaultValue = "0") int numberOfSession) {

        Receipt receipt = receiptService.generateAndSave(patientId, paymentId, receiptDate, fromDate, toDate, amountReceived, numberOfSession);
        return ResponseEntity.ok(ApiResponse.success("Receipt generated", ReceiptDTO.fromEntity(receipt)));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        byte[] pdf = receiptService.getPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("receipt_" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/form-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFormData() {
        Map<String, Object> data = new HashMap<>();
        List<PatientDTO> patients = patientService.getPatientsByDateRange(LocalDate.now().minusYears(5), LocalDate.now());
        List<PaymentDTO> payments = paymentService.getAllAuditablePayments();
        data.put("patients", patients);
        data.put("payments", payments);
        return ResponseEntity.ok(ApiResponse.success("Form data", data));
    }

    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculate(@RequestParam Long patientId,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        ReceiptService.ReceiptCalculation calc = receiptService.calculateForPeriod(patientId, fromDate, toDate);
        Map<String, Object> out = new HashMap<>();
        out.put("sessionCount", calc.sessionCount);
        out.put("totalCharges", calc.totalCharges);
        out.put("avgRate", calc.avgRate);
        out.put("totalPayments", calc.totalPayments);
        return ResponseEntity.ok(ApiResponse.success("Calculation", out));
    }

    @GetMapping("/payments-by-patient")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPatientPayments(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<PaymentDTO> payments = receiptService.getPaymentDTOsByPatientAndDateRange(patientId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Patient payments", payments));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiptDTO>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("Receipts", receiptService.listAll()));
    }
}
