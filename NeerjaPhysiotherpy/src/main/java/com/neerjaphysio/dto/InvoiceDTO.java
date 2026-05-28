package com.neerjaphysio.dto;

public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;
    private String invoiceDate;
    private Long patientId;
    private String patientName;
    private String fromDate;
    private String toDate;
    private Double totalAmount;
    private Double amountPaid;
    private Double balanceDue;
    private String createdAt;

    public InvoiceDTO() {}

    public InvoiceDTO(Long id, String invoiceNumber, String invoiceDate,
                      Long patientId, String patientName,
                      String fromDate, String toDate,
                      Double totalAmount, Double amountPaid, Double balanceDue,
                      String createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balanceDue = balanceDue;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public Double getBalanceDue() { return balanceDue; }
    public void setBalanceDue(Double balanceDue) { this.balanceDue = balanceDue; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
