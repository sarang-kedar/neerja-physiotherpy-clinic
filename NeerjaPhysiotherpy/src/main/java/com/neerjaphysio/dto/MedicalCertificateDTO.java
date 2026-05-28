package com.neerjaphysio.dto;

import java.time.LocalDate;

import com.neerjaphysio.model.MedicalCertificate;

public class MedicalCertificateDTO {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private Long patientId;
    private Long treatmentId;
    private Long paymentId;
    private LocalDate fromDate;
    private LocalDate toDate;

    public MedicalCertificateDTO() {}

    public MedicalCertificateDTO(Long id, String invoiceNumber, LocalDate invoiceDate, Long patientId, Long treatmentId, Long paymentId, LocalDate fromDate, LocalDate toDate) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.patientId = patientId;
        this.treatmentId = treatmentId;
        this.paymentId = paymentId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public static MedicalCertificateDTO fromEntity(MedicalCertificate mc) {
        if (mc == null) return null;
        return new MedicalCertificateDTO(mc.getId(), mc.getInvoiceNumber(), mc.getInvoiceDate(), mc.getPatient() != null ? mc.getPatient().getId() : null,
                mc.getTreatment() != null ? mc.getTreatment().getId() : null,
                mc.getPayment() != null ? mc.getPayment().getId() : null,
                mc.getFromDate(), mc.getToDate());
    }

    public Long getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public Long getPatientId() { return patientId; }
    public Long getTreatmentId() { return treatmentId; }
    public Long getPaymentId() { return paymentId; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
}
