package com.neerjaphysio.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neerjaphysio.dto.ReceiptDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.model.Payment;
import com.neerjaphysio.model.Receipt;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.PaymentRepository;
import com.neerjaphysio.repository.ReceiptRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PDFGenerationService pdfService;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;
    private final TreatmentSessionRepository sessionRepository;

    private static final DateTimeFormatter NUM_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ReceiptService(ReceiptRepository receiptRepository,
                          PDFGenerationService pdfService,
                          PatientRepository patientRepository,
                          PaymentRepository paymentRepository,
                          TreatmentSessionRepository sessionRepository) {
        this.receiptRepository = receiptRepository;
        this.pdfService = pdfService;
        this.patientRepository = patientRepository;
        this.paymentRepository = paymentRepository;
        this.sessionRepository = sessionRepository;
    }

    public Receipt generateAndSave(Long patientId, Long paymentId, LocalDate receiptDate,
                                   LocalDate fromDate, LocalDate toDate, Double amountReceived, int numberOfSession) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        Payment payment = null;
        if (paymentId != null) {
            payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        }
        // Use provided numberOfSession, or calculate from sessions if not provided
        int sessionCount = numberOfSession > 0 ? numberOfSession : sessionRepository.findByPatientIdAndSessionDateBetweenOrderBySessionDateDesc(patientId, fromDate, toDate).size();
        // Generate PDF
        byte[] pdfBytes = pdfService.generateReceiptPDF(patientId, receiptDate, fromDate, toDate, amountReceived, payment, sessionCount);

        String receiptNumber = "RCP-" + receiptDate.format(NUM_FMT) + "-" + (receiptRepository.count() + 1);

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setPatient(patient);
        receipt.setPayment(payment);
        receipt.setReceiptDate(receiptDate);
        receipt.setFromDate(fromDate);
        receipt.setToDate(toDate);
        receipt.setAmountReceived(amountReceived);
        receipt.setPdfFile(pdfBytes);

        return receiptRepository.save(receipt);
    }

    @Transactional(readOnly = true)
    public ReceiptCalculation calculateForPeriod(Long patientId, LocalDate fromDate, LocalDate toDate) {
        // collect sessions in period
        List<TreatmentSession> sessions = sessionRepository.findByPatientIdAndSessionDateBetweenOrderBySessionDateDesc(patientId, fromDate, toDate);
        double totalCharges = sessions.stream()
                .mapToDouble(s -> s.getSessionCharge() != null ? s.getSessionCharge() : 0.0)
                .sum();
        int sessionCount = sessions.size();

        // collect distinct payments
        List<com.neerjaphysio.model.Payment> payments = sessions.stream()
                .map(TreatmentSession::getPayment)
                .filter(p -> p != null)
                .distinct()
                .collect(Collectors.toList());
        double totalPayments = payments.stream()
                .mapToDouble(p -> p.getAmountPaid() != null ? p.getAmountPaid() : 0.0)
                .sum();

        double avgRate = sessionCount > 0 ? totalCharges / sessionCount : 0.0;
        return new ReceiptCalculation(sessionCount, totalCharges, avgRate, totalPayments);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.neerjaphysio.model.Payment> getPaymentsByPatientAndDateRange(Long patientId, LocalDate fromDate, LocalDate toDate) {
        // get payments directly by patient and date range
        return paymentRepository.findByPatientIdAndPaymentDateBetween(patientId, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.neerjaphysio.dto.PaymentDTO> getPaymentDTOsByPatientAndDateRange(Long patientId, LocalDate fromDate, LocalDate toDate) {
        java.util.List<com.neerjaphysio.model.Payment> payments = paymentRepository.findByPatientIdAndPaymentDateBetween(patientId, fromDate, toDate);
        return payments.stream().map(com.neerjaphysio.dto.PaymentDTO::fromEntity).collect(java.util.stream.Collectors.toList());
    }

    public static class ReceiptCalculation {
        public final int sessionCount;
        public final double totalCharges;
        public final double avgRate;
        public final double totalPayments;

        public ReceiptCalculation(int sessionCount, double totalCharges, double avgRate, double totalPayments) {
            this.sessionCount = sessionCount;
            this.totalCharges = totalCharges;
            this.avgRate = avgRate;
            this.totalPayments = totalPayments;
        }
    }

    @Transactional(readOnly = true)
    public byte[] getPdf(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", id));
        return receipt.getPdfFile();
    }

    @Transactional(readOnly = true)
    public List<ReceiptDTO> listAll() {
        return receiptRepository.findAllWithPatientAndPayment().stream()
                .map(ReceiptDTO::fromEntity).collect(Collectors.toList());
    }
}
