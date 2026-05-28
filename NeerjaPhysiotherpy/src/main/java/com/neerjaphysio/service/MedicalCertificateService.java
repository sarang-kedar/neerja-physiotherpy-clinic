package com.neerjaphysio.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.dto.MedicalCertificateDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.MedicalCertificate;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.model.Payment;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.MedicalCertificateRepository;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.PaymentRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class MedicalCertificateService {

    private final MedicalCertificateRepository medicalCertificateRepository;
    private final PDFGenerationService pdfService;
    private final PatientRepository patientRepository;
    private final TreatmentSessionRepository treatmentRepository;
    private final PaymentRepository paymentRepository;

    private static final DateTimeFormatter NUM_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public MedicalCertificateService(MedicalCertificateRepository medicalCertificateRepository,
                                     PDFGenerationService pdfService,
                                     PatientRepository patientRepository,
                                     TreatmentSessionRepository treatmentRepository,
                                     PaymentRepository paymentRepository) {
        this.medicalCertificateRepository = medicalCertificateRepository;
        this.pdfService = pdfService;
        this.patientRepository = patientRepository;
        this.treatmentRepository = treatmentRepository;
        this.paymentRepository = paymentRepository;
    }

    public MedicalCertificate generateAndSave(Long patientId,
                                              LocalDate invoiceDate,
                                              LocalDate fromDate,
                                              LocalDate toDate,
                                              String surgeonName) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        // Generate PDF using certificate date and surgeon name (or patient.referredBy inside PDF generator)
        byte[] pdfBytes = pdfService.generateMedicalCertificatePDF(patientId, invoiceDate, fromDate, toDate, surgeonName);

        String invoiceNumber = "MC-" + invoiceDate.format(NUM_FMT) + "-" + (medicalCertificateRepository.count() + 1);

        MedicalCertificate mc = new MedicalCertificate();
        mc.setInvoiceNumber(invoiceNumber);
        mc.setPatient(patient);
        mc.setInvoiceDate(invoiceDate);
        mc.setFromDate(fromDate);
        mc.setToDate(toDate);
        mc.setPdfFile(pdfBytes);

        return medicalCertificateRepository.save(mc);
    }

    @Transactional(readOnly = true)
    public byte[] getPdf(Long id) {
        MedicalCertificate mc = medicalCertificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalCertificate", "id", id));
        return mc.getPdfFile();
    }

    @Transactional(readOnly = true)
    public List<MedicalCertificateDTO> listAll() {
        return medicalCertificateRepository.findAll().stream()
                .map(MedicalCertificateDTO::fromEntity).toList();
    }
}
