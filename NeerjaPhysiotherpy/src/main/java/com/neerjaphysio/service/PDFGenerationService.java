package com.neerjaphysio.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.neerjaphysio.dto.BillingDTO;
import com.neerjaphysio.dto.TreatmentSessionDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional(readOnly = true)
public class PDFGenerationService {

  private final PatientRepository patientRepository;
  private final TreatmentSessionRepository treatmentSessionRepository;

  public PDFGenerationService(PatientRepository patientRepository,
      TreatmentSessionRepository treatmentSessionRepository) {
    this.patientRepository = patientRepository;
    this.treatmentSessionRepository = treatmentSessionRepository;
  }

  private static final DeviceRgb ACCENT = new DeviceRgb(31, 84, 147);
  private static final String CLINIC_NAME = "NEERJA PHYSIOTHERAPY CLINIC";
  private static final String DOCTOR_NAME = "Dr. Aradhna Kedar (Lakhpati)";
  private static final String QUALIFICATIONS =
      "B.Pth, M.P.T (Cardiovascular & Respiratory Physiotherapy)";
  private static final String REG_NO = "Reg. No. 2021/04/PT/009212";
  private static final String ADDRESS =
      "93 Shree Ram Nagar, Uday Nagar Square, Ring Road, Nagpur - 440034";
  private static final String PHONE = "9970045015";
  private static final String TAGLINE = "Healing Through Movement";

  public byte[] generateSessionDetailsPDF(Long patientId, int sessionStart, int sessionEnd) {
    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    // Header
    addHeader(document);

    // Session Details Title
    document.add(new Paragraph("SESSION DETAILS").setTextAlignment(TextAlignment.CENTER)
        .setFontSize(16).setBold());

    document.add(new Paragraph(
        "Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
    document.add(new Paragraph("Patient Name: " + patient.getName()));
    document.add(new Paragraph(
        "Diagnosis: " + (patient.getDiagnosis() != null ? patient.getDiagnosis() : "N/A")));

    // Sessions Table
    Table table = new Table(5);
    table.addCell(createHeaderCell("Sr. No."));
    table.addCell(createHeaderCell("Date"));
    table.addCell(createHeaderCell("Time"));
    table.addCell(createHeaderCell("Location"));
    table.addCell(createHeaderCell("Charge (INR)"));

    List<TreatmentSession> sessions =
        treatmentSessionRepository.findByPatientIdOrderBySessionDateDesc(patientId);
    int index = 1;
    for (TreatmentSession session : sessions) {
      if (index >= sessionStart && index <= sessionEnd) {
        table.addCell(String.valueOf(index));
        table.addCell(session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        table.addCell(session.getSessionTime() != null ? session.getSessionTime().toString() : "-");
        table.addCell(session.getLocation() != null ? session.getLocation() : "-");
        table.addCell(String.format("%.2f", session.getSessionCharge()));
      }
      index++;
    }

    document.add(table);

    // Footer
    addFooter(document);

    document.close();
    return baos.toByteArray();
  }

  
  public byte[] generateMedicalCertificatePDF(Long patientId, LocalDate certificateDate,
      LocalDate startDate, LocalDate endDate, String surgeonName) {
    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);
    document.setMargins(0, 0, 0, 0);

    // Header with logo and clinic info
    float pageW = PageSize.A4.getWidth();
    document.add(colorBar(pageW, 8f, ACCENT));
    addMCHeader(document);
    document.add(colorBar(pageW, 8f, ACCENT));

    // Main body: 2-column layout (sidebar + content)
    float sidebarW = 140f;
    float contentW = 455f;
    Table bodyLayout = new Table(new float[] {sidebarW, contentW})
        .setWidth(UnitValue.createPointValue(595f)).setFixedLayout();

    // Left sidebar: Physiotherapy Services
    Cell sidebarCell = new Cell().setBackgroundColor(new DeviceRgb(220, 228, 245))
        .setBorder(Border.NO_BORDER).setPadding(10).setVerticalAlignment(VerticalAlignment.TOP);
    buildMCSidebar(sidebarCell);
    bodyLayout.addCell(sidebarCell);

    // Right content: Certificate
    Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(15)
        .setVerticalAlignment(VerticalAlignment.TOP);
    buildMCContent(contentCell, patient, certificateDate, startDate, endDate, surgeonName,
        contentW - 30);
    bodyLayout.addCell(contentCell);

    document.add(bodyLayout);

    // Footer
    addMCFooter(document);

    document.close();
    return baos.toByteArray();
  }

  private void addMCHeader(Document document) {
    Table hdr = new Table(new float[] {70f, 525f}).setWidth(UnitValue.createPointValue(595f))
        .setFixedLayout().setMarginBottom(0);

    // Logo cell
    Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(12).setPaddingTop(8)
        .setPaddingBottom(8).setVerticalAlignment(VerticalAlignment.MIDDLE);
    try {
      InputStream is = getClass().getClassLoader().getResourceAsStream("static/images/image0.png");
      if (is != null) {
        com.itextpdf.io.image.ImageData imgData =
            com.itextpdf.io.image.ImageDataFactory.create(is.readAllBytes());
        com.itextpdf.layout.element.Image logo =
            new com.itextpdf.layout.element.Image(imgData).scaleToFit(52, 52);
        logoCell.add(logo);
      }
    } catch (Exception ignored) {
    }
    hdr.addCell(logoCell);

    // Clinic info cell
    Cell infoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingTop(8).setPaddingBottom(6)
        .setPaddingRight(10);
    infoCell.add(new Paragraph(CLINIC_NAME).setFontColor(new DeviceRgb(26, 58, 108)).setFontSize(20)
        .setBold().setMarginBottom(2));
    infoCell.add(new Paragraph(DOCTOR_NAME).setFontColor(new DeviceRgb(26, 58, 108)).setFontSize(12)
        .setBold().setMarginBottom(1));
    infoCell.add(new Paragraph(QUALIFICATIONS).setFontSize(8).setMarginBottom(1));
    infoCell.add(new Paragraph(REG_NO).setFontSize(8));
    hdr.addCell(infoCell);

    document.add(hdr);
  }

  private void buildMCSidebar(Cell cell) {
    cell.add(new Paragraph("Physiotherapy Services").setFontSize(9).setMarginBottom(8));
    String[] services =
        {"Pain Management", "Orthopedic Rehabilitation", "Cardiovascular Rehabilitation",
            "Respiratory Rehabilitation", "Neurological Rehabilitation", "Pediatric Rehabilitation",
            "Sport Rehabilitation", "Gym Rehabilitation", "Postural Correction", "Spine Correction",
            "Post-Surgery Rehabilitation", "Geriatric Rehabilitation", "Women's Health",
            "Online Consultation", "Online Treatment", "Home Based Physiotherapy",
            "Hospital Based Physiotherapy"};
    for (String svc : services) {
      cell.add(new Paragraph("\u2022 " + svc).setFontSize(7.5f).setMarginBottom(2));
    }
  }

  private void buildMCContent(Cell cell, Patient patient, LocalDate certificateDate,
      LocalDate startDate, LocalDate endDate, String surgeonName, float availW) {
    // Top right: Date field
    Paragraph dateP = new Paragraph().add("Date: ")
        .add(certificateDate != null
            ? certificateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            : "")
        .setTextAlignment(TextAlignment.RIGHT).setFontSize(9).setMarginBottom(10);
    cell.add(dateP);

    // Patient details row
    Table detailsTable = new Table(3).setWidth(UnitValue.createPercentValue(100));
    detailsTable.addCell(new Cell()
        .add(new Paragraph("Name: " + (patient.getName() != null ? patient.getName() : ""))
            .setFontSize(9))
        .setBorder(Border.NO_BORDER));
    detailsTable.addCell(new Cell().add(
        new Paragraph("Age: " + (patient.getAge() != null ? patient.getAge() : "")).setFontSize(9))
        .setBorder(Border.NO_BORDER));
    detailsTable
        .addCell(
            new Cell()
                .add(new Paragraph("Sex: " + (patient.getSex() != null ? patient.getSex() : ""))
                    .setFontSize(9))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
    detailsTable.addCell(new Cell(1, 2)
        .add(new Paragraph("Weight: " + (patient.getWeight() != null ? patient.getWeight() : ""))
            .setFontSize(9))
        .setBorder(Border.NO_BORDER));
    cell.add(detailsTable);
    cell.add(new Paragraph("").setMarginBottom(6));

    // "To Whom It May Concern"
    cell.add(new Paragraph("To Whom It May Concern").setTextAlignment(TextAlignment.CENTER)
        .setBold().setFontSize(13).setMarginBottom(12));

    // Get surgeon name: prefer surgeonName param, then patient.referredBy, then blank line
    String effectiveSurgeon = (surgeonName != null && !surgeonName.isBlank()) ? surgeonName
        : (patient.getReferredBy() != null && !patient.getReferredBy().isBlank()
            ? patient.getReferredBy()
            : "___________________");

    // Certificate body
    Paragraph body = new Paragraph();
    body.add("This is to certify that the patient ");
    body.add(patient.getName() != null ? patient.getName() : "").setFontSize(9);
    body.add(",\n");
    body.add(
        "received physiotherapy treatment at Neerja Physiotherapy Clinic, Nagpur. The patient\n");
    body.add(
        "underwent the prescribed procedures and therapeutic exercises as per the treatment plan\n");
    body.add("recommended by the consulting surgeon ");
    body.add(effectiveSurgeon);
    body.add(",\n");
    body.add("during the period from ");
    body.add(startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        : "___________");
    body.add(" to ");
    body.add(endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        : "___________");
    body.add(".\n\n");
    body.add("All sessions were conducted under professional supervision, and the treatment was\n");
    body.add(
        "carried out in accordance with the prescribed medical guidelines. Detailed session records\n");
    body.add("are attached herewith for your reference.\n\n");
    body.add("This certificate is being issued upon request for insurance/medical claim purposes.");
    body.setFontSize(9);
    body.setTextAlignment(TextAlignment.JUSTIFIED);
    cell.add(body);

    cell.add(new Paragraph("").setMarginBottom(12));

    // Signature block (right-aligned) - always use fixed doctor name
    String signName = "Dr. Aradhan Rajendra Kedar";
    Paragraph sig1 = new Paragraph(signName).setBold().setFontSize(10)
        .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(1);
    Paragraph sig2 = new Paragraph("Neerja Physiotherapy Clinic, Nagpur").setFontSize(9)
        .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(0);
    cell.add(sig1);
    cell.add(sig2);
  }

  private void addMCFooter(Document document) {
    Table footer = new Table(new float[] {595f}).setWidth(UnitValue.createPointValue(595f))
        .setFixedLayout().setMarginTop(6);

    Cell fc = new Cell().setBackgroundColor(new DeviceRgb(40, 60, 100)).setBorder(Border.NO_BORDER)
        .setPadding(8);
    fc.add(new Paragraph("TEL:  " + PHONE)
        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE).setFontSize(9)
        .setMarginBottom(2));
    fc.add(new Paragraph("ADDRESS:  " + ADDRESS)
        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE).setFontSize(8));
    footer.addCell(fc);
    document.add(footer);
  }

  public byte[] generateInvoicePDF(BillingDTO billing) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    // Header
    addHeader(document);

    // Invoice Title
    document.add(new Paragraph("INVOICE").setTextAlignment(TextAlignment.CENTER).setFontSize(16));

    document.add(new Paragraph("Invoice Number: ________________     Invoice Date: "
        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
    document.add(new Paragraph("Patient Name: " + billing.getPatientName()));
    document.add(new Paragraph(""));

    // Handle pagination - 15 sessions per page
    List<TreatmentSessionDTO> sessions =
        billing.getSessions() != null ? billing.getSessions() : java.util.List.of();
    int totalSessions = sessions.size();
    int sessionsPerPage = 15;
    int totalPages = (totalSessions + sessionsPerPage - 1) / sessionsPerPage;

    double grandTotal = 0;
    for (int pageNum = 0; pageNum < totalPages; pageNum++) {
      int startIndex = pageNum * sessionsPerPage;
      int endIndex = Math.min(startIndex + sessionsPerPage, totalSessions);

      // Service Items Table
      Table table = new Table(5);
      table.addCell(createHeaderCell("Date"));
      table.addCell(createHeaderCell("Therapy"));
      table.addCell(createHeaderCell("Session"));
      table.addCell(createHeaderCell("Rate"));
      table.addCell(createHeaderCell("Amount"));

      double pageTotal = 0;
      for (int i = startIndex; i < endIndex; i++) {
        TreatmentSessionDTO session = sessions.get(i);
        table.addCell(session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        table.addCell(session.getTherapyDescription());
        table.addCell(String.valueOf(i + 1));
        table.addCell(String.format("%.2f", session.getSessionCharge()));
        table.addCell(String.format("%.2f", session.getSessionCharge()));
        double charge = session.getSessionCharge() != null ? session.getSessionCharge() : 0.0;
        pageTotal += charge;
        grandTotal += charge;
      }

      // Page Total Row
      Cell pageTotalCell = new Cell(1, 4).add(new Paragraph("Page Total INR"));
      pageTotalCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
      table.addCell(pageTotalCell);
      table.addCell(String.format("%.2f", pageTotal));

      document.add(table);
      document.add(new Paragraph(""));

      // Add page break if not last page
      if (pageNum < totalPages - 1) {
        document.add(new Paragraph("").setMarginBottom(400));
        addHeader(document);
        document.add(new Paragraph(""));
      }
    }

    // Grand Total Row (on last page)
    Table grandTotalTable = new Table(5);
    grandTotalTable.addCell(createHeaderCell(""));
    grandTotalTable.addCell(createHeaderCell(""));
    grandTotalTable.addCell(createHeaderCell(""));
    grandTotalTable.addCell(createHeaderCell(""));
    grandTotalTable.addCell(createHeaderCell(""));

    Cell grandTotalCell = new Cell(1, 4).add(new Paragraph("Grand Total INR"));
    grandTotalCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
    grandTotalTable.addCell(grandTotalCell);
    grandTotalTable.addCell(String.format("%.2f", grandTotal));
    document.add(grandTotalTable);

    document.add(new Paragraph(""));

    // Payment Details - Show actual invoice data
    document.add(new Paragraph("Payment Details:"));
    document.add(new Paragraph("Received a sum of ₹ " + String.format("%.2f", grandTotal)
        + " Rupees in Words " + convertNumberToWords(grandTotal) + " only"));
    document.add(new Paragraph("from " + billing.getPatientName()));
    document.add(new Paragraph(
        "via Cash/Cheque/Demand Draft/UPI/NEFT/RTGS (Cheque/DD No.: __________________)"));
    document.add(new Paragraph("on _______________"));

    document.add(new Paragraph(""));
    document.add(new Paragraph(""));
    document.add(new Paragraph("Date: _______________          " + DOCTOR_NAME));

    // Footer
    addFooter(document);

    document.close();
    return baos.toByteArray();
  }

  public byte[] generateReceiptPDF(Long patientId, LocalDate receiptDate, LocalDate startDate,
      LocalDate endDate, Double amountReceived, com.neerjaphysio.model.Payment payment,
      int numberOfSession) {
    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);
    document.setMargins(0, 0, 0, 0);

    // Header with logo and clinic info
    float pageW = PageSize.A4.getWidth();
    document.add(colorBar(pageW, 8f, ACCENT));
    addMCHeader(document);
    document.add(colorBar(pageW, 8f, ACCENT));

    // Main body: 2-column layout (sidebar + content)
    float sidebarW = 140f;
    float contentW = 455f;
    Table bodyLayout = new Table(new float[] {sidebarW, contentW})
        .setWidth(UnitValue.createPointValue(595f)).setFixedLayout();

    // Left sidebar: Physiotherapy Services
    Cell sidebarCell = new Cell().setBackgroundColor(new DeviceRgb(220, 228, 245))
        .setBorder(Border.NO_BORDER).setPadding(10).setVerticalAlignment(VerticalAlignment.TOP);
    buildMCSidebar(sidebarCell);
    bodyLayout.addCell(sidebarCell);

    // Right content: Receipt
    Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(15)
        .setVerticalAlignment(VerticalAlignment.TOP);
    buildReceiptContent(contentCell, patient, receiptDate, startDate, endDate, amountReceived,
        payment, contentW - 30, numberOfSession);
    bodyLayout.addCell(contentCell);

    document.add(bodyLayout);

    // Footer
    addMCFooter(document);

    document.close();
    return baos.toByteArray();
  }

  // Backwards-compatible overload used by PDFReportController when payment or explicit receiptDate
  // isn't provided
  public byte[] generateReceiptPDF(Long patientId, LocalDate startDate, LocalDate endDate,
      Double amountReceived, int numberOfSession) {
    return generateReceiptPDF(patientId, LocalDate.now(), startDate, endDate, amountReceived, null,
        numberOfSession);
  }

  private Table colorBar(float pageW, float height, DeviceRgb color) {
    Table bar =
        new Table(new float[] {pageW}).setWidth(UnitValue.createPointValue(pageW)).setFixedLayout();
    bar.addCell(new Cell().setHeight(height).setBackgroundColor(color).setBorder(Border.NO_BORDER));
    return bar;
  }

  private void addHeader(Document document) {
    float pageW = PageSize.A4.getWidth();
    // Top accent bar
    document.add(colorBar(pageW, 8f, ACCENT));

    // Header boxed content with a thin border
    // Use percent width so the box respects document margins and renders the border
    Table headerBox = new Table(1).setWidth(UnitValue.createPercentValue(100)).setFixedLayout()
        .setMarginTop(6).setMarginBottom(6).setBorder(Border.NO_BORDER);
    Cell boxCell = new Cell().setBorder(new SolidBorder(new DeviceRgb(26, 58, 108), 0.9f))
        .setPadding(8).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(null);
    boxCell.add(new Paragraph(CLINIC_NAME).setFontSize(14).setBold()
        .setFontColor(new DeviceRgb(26, 58, 108)).setMarginBottom(2));
    boxCell.add(new Paragraph(DOCTOR_NAME).setFontSize(11).setBold()
        .setFontColor(new DeviceRgb(26, 58, 108)).setMarginBottom(2));
    boxCell.add(new Paragraph(QUALIFICATIONS).setFontSize(9).setMarginBottom(1));
    boxCell.add(new Paragraph(REG_NO).setFontSize(9));
    headerBox.addCell(boxCell);
    document.add(headerBox);

    // Bottom accent bar
    document.add(colorBar(pageW, 8f, ACCENT));
  }

  private void addFooter(Document document) {
    document.add(new Paragraph(""));
    document.add(new Paragraph(""));
    document.add(new Paragraph(PHONE).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
    document.add(new Paragraph(ADDRESS).setTextAlignment(TextAlignment.CENTER).setFontSize(9));
  }

  private void buildReceiptContent(Cell cell, Patient patient, LocalDate receiptDate,
      LocalDate startDate, LocalDate endDate, Double amountReceived,
      com.neerjaphysio.model.Payment payment, float availW, int numberOfSession) {
    // Receipt title
    cell.add(new Paragraph("RECEIPT").setTextAlignment(TextAlignment.CENTER).setBold()
        .setFontSize(16).setMarginBottom(10));

    // Top row: Receipt No and Date
    Table topRow = new Table(2).setWidth(UnitValue.createPercentValue(100));
    topRow.addCell(new Cell().add(new Paragraph("Receipt No.: " + (receiptDate != null
        ? "RCP-" + receiptDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        : "")).setFontSize(9)).setBorder(Border.NO_BORDER));
    topRow.addCell(new Cell()
        .add(new Paragraph("Date: "
            + (receiptDate != null ? receiptDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : "")).setFontSize(9).setTextAlignment(TextAlignment.RIGHT))
        .setBorder(Border.NO_BORDER));
    cell.add(topRow);
    cell.add(new Paragraph("").setMarginBottom(8));

    // Receipt body
    Paragraph body = new Paragraph();
    body.add("Received with thanks from ");
    body.add(patient.getName() != null ? patient.getName() : "").setFontSize(9);
    body.add(",\n");
    body.add("a sum of ₹ ");
    body.add(amountReceived != null ? String.format("%.2f", amountReceived) : "__________")
        .setFontSize(9);
    body.add("  (Rupees ");
    body.add(amountReceived != null ? convertNumberToWords(amountReceived) + " only"
        : "___________________________________________ only").setFontSize(9);
    body.add("),\n");
    body.add("paid via ");
    String paymentMode = "Cash/Cheque/Demand Draft/UPI/NEFT/RTGS";
    if (payment != null && payment.getPaymentMode() != null) {
      paymentMode = payment.getPaymentMode().getDisplayName();
    }
    body.add(paymentMode).setFontSize(9);

    // Payment reference
    if (payment != null && payment.getPaymentMode() != null) {
      String ref = getPaymentReferenceForReceipt(payment);
      if (ref != null && !ref.isEmpty()) {
        body.add("  (" + ref + ")").setFontSize(9);
      }
    }

    body.add(",\n");
    body.add("towards physiotherapy treatment charges for the period from ");
    body.add(startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        : "__________").setFontSize(9);
    body.add(" to ");
    body.add(
        endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "__________")
        .setFontSize(9);
    body.add(",\n");
    body.add("at the rate of INR ");
    body.add(numberOfSession > 0
        ? String.format("%.2f", amountReceived != null ? amountReceived / numberOfSession : 0.0)
        : "__________").setFontSize(9);
    // body.add("____________________ ").setFontSize(9);
    body.add(" per day/session.\n\n");
    body.setFontSize(9);
    body.setTextAlignment(TextAlignment.LEFT);
    cell.add(body);

    // Total amount
    cell.add(new Paragraph("Total Amount Received: ₹ "
        + (amountReceived != null ? String.format("%.2f", amountReceived) : "")).setFontSize(9)
            .setMarginBottom(12));

    // Signature block
    Table sigTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
    sigTable.addCell(new Cell().add(new Paragraph("Date: "
        + (receiptDate != null ? receiptDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            : "")).setFontSize(9))
        .setBorder(Border.NO_BORDER));
    sigTable.addCell(new Cell().add(new Paragraph("Dr. Aradhan Rajendra Kedar").setFontSize(9)
        .setTextAlignment(TextAlignment.RIGHT).setBold()).setBorder(Border.NO_BORDER));
    cell.add(sigTable);
  }

  private String getPaymentReferenceForReceipt(com.neerjaphysio.model.Payment p) {
    if (p == null || p.getPaymentMode() == null)
      return "";
    return switch (p.getPaymentMode()) {
      case CASH -> "";
      case UPI -> p.getTransactionId() != null ? "Transaction ID: " + p.getTransactionId() : "";
      case CHEQUE -> p.getChequeNumber() != null ? "Cheque No.: " + p.getChequeNumber() : "";
      case DEMAND_DRAFT -> p.getChequeNumber() != null ? "DD No.: " + p.getChequeNumber() : "";
      case RTGS, NEFT -> p.getUtrNumber() != null ? "UTR: " + p.getUtrNumber() : "";
      default -> p.getReferenceNumber() != null ? p.getReferenceNumber() : "";
    };
  }

  private String convertNumberToWords(Double amount) {
    long rupees = (long) Math.floor(amount);
    long paise = Math.round((amount - rupees) * 100);
    String words = numToWords(rupees) + " Rupees";
    if (paise > 0)
      words += " and " + numToWords(paise) + " Paise";
    return words;
  }

  private static final String[] ONES = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
      "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
      "Seventeen", "Eighteen", "Nineteen"};
  private static final String[] TENS =
      {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

  private String numToWords(long n) {
    if (n == 0)
      return "Zero";
    if (n < 20)
      return ONES[(int) n];
    if (n < 100)
      return TENS[(int) (n / 10)] + (n % 10 != 0 ? " " + ONES[(int) (n % 10)] : "");
    if (n < 1_000)
      return ONES[(int) (n / 100)] + " Hundred" + (n % 100 != 0 ? " " + numToWords(n % 100) : "");
    if (n < 1_00_000)
      return numToWords(n / 1_000) + " Thousand"
          + (n % 1_000 != 0 ? " " + numToWords(n % 1_000) : "");
    if (n < 1_00_00_000)
      return numToWords(n / 1_00_000) + " Lakh"
          + (n % 1_00_000 != 0 ? " " + numToWords(n % 1_00_000) : "");
    return numToWords(n / 1_00_00_000) + " Crore"
        + (n % 1_00_00_000 != 0 ? " " + numToWords(n % 1_00_00_000) : "");
  }

  private Cell createHeaderCell(String text) {
    Cell cell = new Cell().add(new Paragraph(text).setBold());
    cell.setTextAlignment(TextAlignment.CENTER);
    cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
    return cell;
  }
}
