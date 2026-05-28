package com.neerjaphysio.dto;

import java.time.LocalDate;

import com.neerjaphysio.model.Receipt;

public class ReceiptDTO {

    private Long id;
    private String receiptNumber;
    private LocalDate receiptDate;
    private Long patientId;
    private String patientName;
    private Long paymentId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Double amountReceived;

    public ReceiptDTO() {}

    public ReceiptDTO(Long id, String receiptNumber, LocalDate receiptDate, Long patientId, String patientName, 
                      Long paymentId, LocalDate fromDate, LocalDate toDate, Double amountReceived) {
        this.id = id;
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.paymentId = paymentId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.amountReceived = amountReceived;
    }

    public static ReceiptDTO fromEntity(Receipt r) {
        if (r == null) return null;
        return new ReceiptDTO(r.getId(), r.getReceiptNumber(), r.getReceiptDate(), 
                r.getPatient() != null ? r.getPatient().getId() : null,
                r.getPatient() != null ? r.getPatient().getName() : null,
                r.getPayment() != null ? r.getPayment().getId() : null,
                r.getFromDate(), r.getToDate(), r.getAmountReceived());
    }

    public Long getId() { return id; }
    public String getReceiptNumber() { return receiptNumber; }
    public LocalDate getReceiptDate() { return receiptDate; }
    public Long getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public Long getPaymentId() { return paymentId; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public Double getAmountReceived() { return amountReceived; }
}
