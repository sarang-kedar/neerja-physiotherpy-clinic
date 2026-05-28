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
import com.neerjaphysio.dto.MedicalCertificateDTO;
import com.neerjaphysio.dto.PatientDTO;
import com.neerjaphysio.dto.PaymentDTO;
import com.neerjaphysio.dto.TreatmentSessionDTO;
import com.neerjaphysio.model.MedicalCertificate;
import com.neerjaphysio.service.MedicalCertificateService;
import com.neerjaphysio.service.PatientService;
import com.neerjaphysio.service.PaymentService;
import com.neerjaphysio.service.TreatmentSessionService;

@RestController
@RequestMapping("/api/medical-certificates")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MedicalCertificateController {

    private final MedicalCertificateService mcService;
    private final PatientService patientService;
    private final TreatmentSessionService sessionService;
    private final PaymentService paymentService;

    public MedicalCertificateController(MedicalCertificateService mcService,
                                        PatientService patientService,
                                        TreatmentSessionService sessionService,
                                        PaymentService paymentService) {
        this.mcService = mcService;
        this.patientService = patientService;
        this.sessionService = sessionService;
        this.paymentService = paymentService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<MedicalCertificateDTO>> generate(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate invoiceDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String surgeonName) {

        MedicalCertificate mc = mcService.generateAndSave(patientId, invoiceDate, fromDate, toDate, surgeonName);
        return ResponseEntity.ok(ApiResponse.success("Medical certificate generated", MedicalCertificateDTO.fromEntity(mc)));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        byte[] pdf = mcService.getPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("medical_certificate_" + id + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/form-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFormData() {
        Map<String, Object> data = new HashMap<>();
        // fetch patients from last 5 years to keep the list reasonable
        List<PatientDTO> patients = patientService.getPatientsByDateRange(LocalDate.now().minusYears(5), LocalDate.now());
        data.put("patients", patients);
        return ResponseEntity.ok(ApiResponse.success("Form data", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicalCertificateDTO>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("Medical certificates", mcService.listAll()));
    }
}
