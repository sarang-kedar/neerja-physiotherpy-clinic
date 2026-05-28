package com.neerjaphysio.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.neerjaphysio.dto.InvoiceDTO;
import com.neerjaphysio.exception.ResourceNotFoundException;
import com.neerjaphysio.model.Invoice;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.model.Payment;
import com.neerjaphysio.model.TreatmentSession;
import com.neerjaphysio.repository.InvoiceRepository;
import com.neerjaphysio.repository.PatientRepository;
import com.neerjaphysio.repository.TreatmentSessionRepository;

@Service
@Transactional
public class InvoiceService {

  // ── Colours (matching the image) ────────────────────────────────────────
  private static final DeviceRgb NAVY = new DeviceRgb(26, 58, 108);
  private static final DeviceRgb ACCENT = new DeviceRgb(31, 84, 147);
  private static final DeviceRgb SIDEBAR_BG = new DeviceRgb(220, 228, 245);
  private static final DeviceRgb FOOTER_BG = new DeviceRgb(40, 60, 100);

  // ── Clinic constants ─────────────────────────────────────────────────────
  private static final String CLINIC_NAME = "NEERJA PHYSIOTHERAPY CLINIC";
  private static final String DOCTOR_NAME = "Dr. Aradhna Kedar (Lakhpati)";
  private static final String QUALIF = "B.Pth, M.P.T (Cardiovascular & Respiratory Physiotherapy)";
  private static final String REG_NO = "Reg. No. 2021/04/PT/009212";
  private static final String PHONE = "9970045015";
  private static final String ADDRESS =
      "93 Shree Ram Nagar Uday Nagar Square Ring Road near AB Association Nagpur - 440034";
  private static final String SIG_NAME = "Dr. Aradhan Rajendra Kedar";

  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  // ── Services listed on the sidebar ───────────────────────────────────────
  private static final String[] SERVICES = {"Pain Management", "Orthopedic Rehabilitation",
      "Cardiovascular Rehabilitation", "Respiratory Rehabilitation", "Neurological Rehabilitation",
      "Pediatric Rehabilitation", "Sport Rehabilitation", "Gym Rehabilitation",
      "Postural Correction", "Spine Correction", "Post-Surgery Rehabilitation",
      "Geriatric Rehabilitation", "Women's Health", "Online Consultation", "Online Treatment",
      "Home Based Physiotherapy", "Hospital Based Physiotherapy"};

  // ── Repos ────────────────────────────────────────────────────────────────
  private final InvoiceRepository invoiceRepository;
  private final PatientRepository patientRepository;
  private final TreatmentSessionRepository sessionRepository;

  public InvoiceService(InvoiceRepository invoiceRepository, PatientRepository patientRepository,
      TreatmentSessionRepository sessionRepository) {
    this.invoiceRepository = invoiceRepository;
    this.patientRepository = patientRepository;
    this.sessionRepository = sessionRepository;
  }

  // ═════════════════════════════════════════════════════════════════════════
  // Public API
  // ═════════════════════════════════════════════════════════════════════════

  public Invoice generateAndSaveInvoice(Long patientId, LocalDate invoiceDate, LocalDate fromDate,
      LocalDate toDate) {

    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

    List<TreatmentSession> sessions = sessionRepository
        .findByPatientIdAndSessionDateBetweenOrderBySessionDateDesc(patientId, fromDate, toDate);

    List<Payment> payments = sessions.stream().map(TreatmentSession::getPayment)
        .filter(p -> p != null).distinct().collect(Collectors.toList());

    double totalAmount = sessions.stream()
        .mapToDouble(s -> s.getSessionCharge() != null ? s.getSessionCharge() : 0.0).sum();
    double amountPaid = payments.stream()
        .mapToDouble(p -> p.getAmountPaid() != null ? p.getAmountPaid() : 0.0).sum();
    double balanceDue = totalAmount - amountPaid;

    String invoiceNumber = generateInvoiceNumber(invoiceDate);

    byte[] pdfBytes = buildInvoicePDF(invoiceNumber, invoiceDate, patient, sessions, payments,
        fromDate, toDate, totalAmount, amountPaid, balanceDue);

    Invoice invoice = new Invoice();
    invoice.setInvoiceNumber(invoiceNumber);
    invoice.setInvoiceDate(invoiceDate);
    invoice.setPatient(patient);
    invoice.setFromDate(fromDate);
    invoice.setToDate(toDate);
    invoice.setTotalAmount(totalAmount);
    invoice.setAmountPaid(amountPaid);
    invoice.setBalanceDue(balanceDue);
    invoice.setPdfData(pdfBytes);
    return invoiceRepository.save(invoice);
  }

  @Transactional(readOnly = true)
  public List<InvoiceDTO> getInvoicesByPatient(Long patientId) {
    return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
        .map(this::toDTO).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<InvoiceDTO> getAllInvoices() {
    return invoiceRepository.findAllWithPatient().stream().map(this::toDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public byte[] getInvoicePdfBytes(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    return invoice.getPdfData();
  }

  // ═════════════════════════════════════════════════════════════════════════
  // PDF Construction – matches the image exactly
  // ═════════════════════════════════════════════════════════════════════════

  private byte[] buildInvoicePDF(String invoiceNumber, LocalDate invoiceDate, Patient patient,
      List<TreatmentSession> sessions, List<Payment> payments, LocalDate fromDate, LocalDate toDate,
      double totalAmount, double amountPaid, double balanceDue) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
    Document document = new Document(pdfDoc, PageSize.A4);
    document.setMargins(0, 0, 0, 0); // we control all spacing via tables

    float pageW = PageSize.A4.getWidth(); // 595 pt

    // ── 1. Top blue bar ────────────────────────────────────────────────
    document.add(colorBar(pageW, 8f, ACCENT));

    // ── 2. Header (logo | clinic name / doctor info) ──────────────────
    document.add(buildHeader(pageW));

    // ── 3. Bottom-of-header blue bar ──────────────────────────────────
    document.add(colorBar(pageW, 4f, ACCENT));

    // ── 4. Body: sidebar | invoice content ────────────────────────────
    float sidebarW = 140f;
    float contentW = pageW - sidebarW;

    Table body = new Table(new float[] {sidebarW, contentW})
        .setWidth(UnitValue.createPointValue(pageW)).setFixedLayout();

    // Left – sidebar
    Cell sidebarCell = new Cell().setBackgroundColor(SIDEBAR_BG).setBorder(Border.NO_BORDER)
        .setPadding(10).setVerticalAlignment(VerticalAlignment.TOP);
    buildSidebar(sidebarCell);
    body.addCell(sidebarCell);

    // Right – invoice content
    Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(12)
        .setVerticalAlignment(VerticalAlignment.TOP);
    buildContent(contentCell, invoiceNumber, invoiceDate, patient, sessions, payments, fromDate,
        toDate, totalAmount, amountPaid, balanceDue, contentW - 24);
    body.addCell(contentCell);

    document.add(body);

    // ── 5. Footer ─────────────────────────────────────────────────────
    document.add(buildFooter(pageW));

    document.close();
    return baos.toByteArray();
  }

  // ─── Header ──────────────────────────────────────────────────────────────
  private Table buildHeader(float pageW) {
    Table hdr = new Table(new float[] {70f, pageW - 70f})
        .setWidth(UnitValue.createPointValue(pageW)).setFixedLayout().setMarginBottom(0);

    // Logo cell
    Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(12).setPaddingTop(8)
        .setPaddingBottom(8).setVerticalAlignment(VerticalAlignment.MIDDLE);
    try {
      InputStream is = getClass().getClassLoader().getResourceAsStream("static/images/image0.png");
      if (is != null) {
        ImageData imgData = ImageDataFactory.create(is.readAllBytes());
        Image logo = new Image(imgData).scaleToFit(52, 52);
        logoCell.add(logo);
      }
    } catch (Exception ignored) {
    }
    hdr.addCell(logoCell);

    // Clinic info cell
    Cell infoCell = new Cell().setBorder(Border.NO_BORDER).setPaddingTop(8).setPaddingBottom(6)
        .setPaddingRight(10);
    infoCell.add(
        new Paragraph(CLINIC_NAME).setFontColor(NAVY).setFontSize(20).setBold().setMarginBottom(2));
    infoCell.add(
        new Paragraph(DOCTOR_NAME).setFontColor(NAVY).setFontSize(12).setBold().setMarginBottom(1));
    infoCell.add(new Paragraph(QUALIF).setFontSize(8).setMarginBottom(1));
    infoCell.add(new Paragraph(REG_NO).setFontSize(8));
    hdr.addCell(infoCell);

    return hdr;
  }

  // ─── Left sidebar ────────────────────────────────────────────────────────
  private void buildSidebar(Cell cell) {
    cell.add(new Paragraph("Physiotherapy Services").setFontSize(9).setMarginBottom(8));
    for (String svc : SERVICES) {
      cell.add(new Paragraph("\u2022 " + svc).setFontSize(7.5f).setMarginBottom(3));
    }
  }

  // ─── Right invoice content ───────────────────────────────────────────────
  private void buildContent(Cell cell, String invoiceNumber, LocalDate invoiceDate, Patient patient,
      List<TreatmentSession> sessions, List<Payment> payments, LocalDate fromDate, LocalDate toDate,
      double totalAmount, double amountPaid, double balanceDue, float availW) {

    // Title
    cell.add(new Paragraph("INVOICE").setBold().setFontSize(16)
        .setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));

    // Invoice Number | Invoice Date (two-column row)
    Table metaRow = new Table(new float[] {availW / 2f, availW / 2f})
        .setWidth(UnitValue.createPercentValue(100));
    metaRow.addCell(
        noBorderCell(para().add(("Invoice Number: ")).add(txt(invoiceNumber)).setFontSize(9)));
    metaRow
        .addCell(noBorderCell(para().add(("Invoice Date: ")).add(txt(invoiceDate.format(DATE_FMT)))
            .setFontSize(9).setTextAlignment(TextAlignment.RIGHT)));
    cell.add(metaRow);

    // Patient Name (underlined field)
    cell.add(underlinedField("Patient Name: ", patient.getName()));

    // Diagnosis (underlined field)
    String diag =
        patient.getDiagnosis() != null && !patient.getDiagnosis().isBlank() ? patient.getDiagnosis()
            : "-";
    cell.add(underlinedField("Diagnosis: ", diag));

    cell.add(new Paragraph("").setMarginBottom(6));

    // "Service item" label
    cell.add(new Paragraph("Service item").setFontSize(9).setBold()
        .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));

    // Service table: Date | Therapy | Session | Rate | Amount
    Table svcTable =
        new Table(new float[] {55, 145, 45, 50, 55}).setWidth(UnitValue.createPercentValue(100));

    svcTable.addCell(svcHdr("Date"));
    svcTable.addCell(svcHdr("Therapy"));
    svcTable.addCell(svcHdr("Session"));
    svcTable.addCell(svcHdr("Rate"));
    svcTable.addCell(svcHdr("Amount"));

    int sessionNo = 1;
    for (TreatmentSession s : sessions) {
      double charge = s.getSessionCharge() != null ? s.getSessionCharge() : 0.0;
      svcTable.addCell(svcData(s.getSessionDate().format(DATE_FMT)));
      svcTable
          .addCell(svcData(s.getTherapyDescription() != null ? s.getTherapyDescription() : "-"));
      svcTable.addCell(svcData(String.valueOf(sessionNo++)));
      svcTable.addCell(svcData(String.format("%.2f", charge)));
      svcTable.addCell(svcData(String.format("%.2f", charge)));
    }

    // Grand Total row
    svcTable.addCell(new Cell(1, 4)
        .add(new Paragraph("Grand Total INR").setFontSize(9).setTextAlignment(TextAlignment.CENTER))
        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
    svcTable
        .addCell(new Cell().add(new Paragraph(String.format("%.2f", totalAmount)).setFontSize(9))
            .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3));
    cell.add(svcTable);

    cell.add(new Paragraph("").setMarginBottom(6));

    // ── Payment Details ────────────────────────────────────────────────
    cell.add(new Paragraph("Payment Details:").setFontSize(9).setMarginBottom(3));

    // Line 1: Received a sum of ₹ X Rupees in Words Y (using GRAND TOTAL)
    String totalAmtStr = String.format("%.2f", totalAmount);
    String totalAmtWords =
        totalAmount > 0 ? rupeesToWords(totalAmount) : "___________________________";
    cell.add(para().add(txt("Received a sum of \u20B9 ")).add(totalAmtStr)
        .add(txt("  Rupees in Words (")).add(totalAmtWords).add(txt(")")).setFontSize(9)
        .setMarginBottom(3));

    // Line 2: from <patient name>
    cell.add(underlinedField("from  ", patient.getName()));

    // Line 3: All payment methods (unique only)
    if (!payments.isEmpty()) {
      // Collect unique payment methods using LinkedHashSet to maintain order
      Set<String> uniquePaymentModes = new LinkedHashSet<>();
      for (Payment p : payments) {
        if (p.getPaymentMode() != null) {
          uniquePaymentModes.add(p.getPaymentMode().getDisplayName());
        }
      }

      Paragraph viaPara = para().add(txt("via ")).setFontSize(9);
      if (!uniquePaymentModes.isEmpty()) {
        String modesStr = String.join(" + ", uniquePaymentModes);
        viaPara.add(bold(modesStr));
      }

      // Show all payment references
      StringBuilder allRefs = new StringBuilder();
      for (Payment p : payments) {
        if (p.getPaymentMode() != null) {
          String ref = getPaymentRef(p);
          if (!ref.equals("_______________") && !ref.isEmpty()) {
            if (allRefs.length() > 0)
              allRefs.append(" | ");
            allRefs.append(ref);
          }
        }
      }

      if (allRefs.length() > 0) {
        viaPara.add(txt("  (Ref: ")).add(bold(allRefs.toString())).add(txt(")"));
      }
      viaPara.setMarginBottom(3);
      cell.add(viaPara);
    } else {
      // No payments yet
      Paragraph viaPara =
          para().add(txt("via ")).add(txt("_______________")).setFontSize(9).setMarginBottom(3);
      cell.add(viaPara);
    }

    // Line 4: on <date>
    cell.add(para().add(txt("on ")).add(txt(invoiceDate.format(DATE_FMT))).add(txt("."))
        .setFontSize(9).setMarginBottom(12));

    // Signature row: Date left | Doctor name right
    Table sigRow = new Table(new float[] {availW / 2f, availW / 2f})
        .setWidth(UnitValue.createPercentValue(100));
    sigRow.addCell(noBorderCell(
        para().add(bold("Date: ")).add(txt(invoiceDate.format(DATE_FMT))).setFontSize(9)));
    sigRow.addCell(noBorderCell(
        para(SIG_NAME).setBold().setFontSize(9).setTextAlignment(TextAlignment.RIGHT)));
    cell.add(sigRow);
  }

  // ─── Footer bar ───────────────────────────────────────────────────────────
  private Table buildFooter(float pageW) {
    Table footer = new Table(new float[] {pageW}).setWidth(UnitValue.createPointValue(pageW))
        .setFixedLayout().setMarginTop(6);

    Cell fc = new Cell().setBackgroundColor(FOOTER_BG).setBorder(Border.NO_BORDER).setPadding(8);
    // Use a textual fallback (Tel:) so the phone info is always visible even if symbol glyphs
    // are not available in the PDF fonts embedded in the document.
    fc.add(new Paragraph("Tel: " + PHONE).setFontColor(ColorConstants.WHITE).setFontSize(9)
        .setMarginBottom(2));
    // keep address plain ASCII-friendly so it renders with default fonts
    fc.add(new Paragraph("ADDRESS: " + ADDRESS).setFontColor(ColorConstants.WHITE).setFontSize(8));
    footer.addCell(fc);
    return footer;
  }

  // ═════════════════════════════════════════════════════════════════════════
  // Tiny helpers
  // ═════════════════════════════════════════════════════════════════════════
  /** Solid-colour horizontal bar */
  private Table colorBar(float pageW, float height, DeviceRgb color) {
    Table bar =
        new Table(new float[] {pageW}).setWidth(UnitValue.createPointValue(pageW)).setFixedLayout();
    bar.addCell(new Cell().setHeight(height).setBackgroundColor(color).setBorder(Border.NO_BORDER));
    return bar;
  }

  /** Field-like paragraph: shows a label and a value (no underline) */
  private Paragraph underlinedField(String label, String value) {
    // show the label (bold) followed by the value; remove the underline for cleaner output
    return para()
        .add((label))
        .add(txt(" " + (value != null ? value : "")))
        .setFontSize(9)
        .setMarginBottom(4);
  }

  private Cell svcHdr(String text) {
    return new Cell()
        .add(new Paragraph(text).setBold().setFontSize(8).setTextAlignment(TextAlignment.CENTER))
        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3);
  }

  private Cell svcData(String text) {
    return new Cell().add(new Paragraph(text).setFontSize(8))
        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(3);
  }

  private Cell noBorderCell(Paragraph content) {
    return new Cell().setBorder(Border.NO_BORDER).add(content);
  }

  private Paragraph para(String text) {
    return new Paragraph(text);
  }

  private Paragraph para() {
    return new Paragraph();
  }

  private Text bold(String t) {
    return new Text(t).setBold();
  }

  private Text txt(String t) {
    return new Text(t);
  }

  // ═════════════════════════════════════════════════════════════════════════
  // Payment reference helper
  // ═════════════════════════════════════════════════════════════════════════

  private String getPaymentRef(Payment p) {
    if (p == null || p.getPaymentMode() == null)
      return "_______________";
    return switch (p.getPaymentMode()) {
      case UPI -> p.getTransactionId() != null ? p.getTransactionId() : "_______________";
      case CHEQUE -> p.getChequeNumber() != null ? p.getChequeNumber() : "_______________";
      case RTGS, NEFT -> p.getUtrNumber() != null ? p.getUtrNumber() : "_______________";
      case DEMAND_DRAFT -> p.getChequeNumber() != null ? p.getChequeNumber() : "_______________";
      default -> p.getReferenceNumber() != null ? p.getReferenceNumber() : "_______________";
    };
  }

  // ═════════════════════════════════════════════════════════════════════════
  // Rupees-to-words (Indian system)
  // ═════════════════════════════════════════════════════════════════════════

  private String rupeesToWords(double amount) {
    long rupees = (long) amount;
    long paise = Math.round((amount - rupees) * 100);
    String words = numToWords(rupees) + " Rupees";
    if (paise > 0)
      words += " and " + numToWords(paise) + " Paise";
    return words + " Only";
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

  // ═════════════════════════════════════════════════════════════════════════
  // DTO mapper
  // ═════════════════════════════════════════════════════════════════════════

  private String generateInvoiceNumber(LocalDate date) {
    String prefix = "INV-" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    long count = invoiceRepository.count() + 1;
    return String.format("%s-%04d", prefix, count);
  }

  private InvoiceDTO toDTO(Invoice inv) {
    return new InvoiceDTO(inv.getId(), inv.getInvoiceNumber(),
        inv.getInvoiceDate() != null ? inv.getInvoiceDate().toString() : null,
        inv.getPatient() != null ? inv.getPatient().getId() : null,
        inv.getPatient() != null ? inv.getPatient().getName() : null,
        inv.getFromDate() != null ? inv.getFromDate().toString() : null,
        inv.getToDate() != null ? inv.getToDate().toString() : null, inv.getTotalAmount(),
        inv.getAmountPaid(), inv.getBalanceDue(),
        inv.getCreatedAt() != null ? inv.getCreatedAt().toString() : null);
  }
}
