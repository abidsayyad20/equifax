package com.kfs.service.impl;


import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.kfs.repository.LeadsRepository;
import com.kfs.service.HdfcKFSService;

@Service
public class HdfcKFSServiceImpl extends PdfPageEventHelper implements HdfcKFSService {
	
	@Autowired
	LeadsRepository leadsRepository;
    
    private static final Font titleFont = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
    private static final Font subtitleFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
    private static final Font bold = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
    private static final Font normal = new Font(FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font smallNormal = new Font(FontFamily.HELVETICA, 7, Font.NORMAL);

    @Override
    public byte[] generateHDFCReport() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // Tight margins to match the exact look of the PDF document
            Document document = new Document(PageSize.A4, 24, 24, 24, 24);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(this);
            document.open();

            // --- PAGE 1: Header + Main Table + Upfront Fees ---
            addLogoAndHeader(document);
            createPage1MainLoanTable(document);
            createPage1UpfrontFeesTable(document);
            createPage1BottomSummary(document);
            
            // --- PAGE 2: Contingent Charges ---
            document.newPage();
            createPage2ContingentChargesTable(document);
            
            // --- PAGE 3: Qualitative Information ---
            document.newPage();
            createPage3QualitativeInfoTable(document);
            
            // --- PAGE 4: Computation of APR ---
            document.newPage();
            createPage4ComputationOfAprTable(document);
            
            // --- PAGE 5 & 6: Repayment Schedule ---
            document.newPage();
            createRepaymentSchedule(document);

            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    private void addLogoAndHeader(Document document) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] {5f, 5f});
        
        try {
            Image img = Image.getInstance("C:/Users/Abid/Pictures/Screenshots/HDFC-Bank-logo.png");
            img.scaleToFit(110, 35);
            PdfPCell cell = new PdfPCell(img);
            cell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell);
        } catch (Exception e) {
            headerTable.addCell(getNoBorderCell("HDFC BANK", titleFont, Element.ALIGN_LEFT));
        }
        
        headerTable.addCell(getNoBorderCell("", smallNormal, Element.ALIGN_RIGHT));
        document.add(headerTable);
        
        Paragraph title = new Paragraph("\nKey Fact Statement/Fact Sheet", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subText = new Paragraph("KFS Ref No: 32190e3b-ab74-57cb-ac1e-b0df77aeeebe", bold);
        subText.setAlignment(Element.ALIGN_CENTER);
        document.add(subText);
        
        String disclaimer = "*This indicative Key Fact Statement ('KFS') is based on the information provided by you. Any change in said information by you and/or the Bank's assessment on the said information, on the predefined criteria, may lead to revision in the finalized KFS.\nPart 1 (Interest rate and fees/charges)\n";
        Paragraph discPara = new Paragraph(disclaimer, smallNormal);
        discPara.setAlignment(Element.ALIGN_CENTER);
        discPara.setSpacingAfter(6);
        document.add(discPara);
    }
    
    private void createPage1MainLoanTable(Document document) throws Exception {
        PdfPTable table = new PdfPTable(12);
        table.setWidthPercentage(100);
        String tenure=leadsRepository.findByTenure();
        // --- ROW 1 ---
        table.addCell(getGridCell("1", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Loan proposal/\naccount No.", normal, 2));
        table.addCell(getGridCell("**********", normal, 3));
        table.addCell(getGridCell("Type of Loan", normal, 3));
        table.addCell(getGridCell("Personal Loan", normal, 3));

        // --- ROW 2 ---
        table.addCell(getGridCell("2", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Disbursed Loan amount (in Rupees)", normal, 5));
        table.addCell(getGridCell("300000", normal, 6));

        // --- ROW 3 ---
        table.addCell(getGridCell("3", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Disbursal schedule\n(i) Disbursement in stages or 100% upfront.", normal, 5));
        table.addCell(getGridCell("100% Upfront", normal, 6));

        // --- ROW 3 (Part ii) ---
        table.addCell(getGridCell("", normal, 1)); 
        table.addCell(getGridCell("(ii) If it is stage wise, mention the clause of loan agreement having relevant details", normal, 5));
        table.addCell(getGridCell("NOT APPLICABLE", normal, 6));

        // --- ROW 4 ---
        table.addCell(getGridCell("4", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Loan term (year/months/days)", normal, 5));
        table.addCell(getGridCell( tenure +" Months", normal, 6));

        // --- ROW 5: Section Header ---
        table.addCell(getGridCell("5", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Instalment details", bold, Element.ALIGN_CENTER, 11));

        // --- ROW 5 Sub-headers ---
        table.addCell(getGridCell("", normal, 1));
        table.addCell(getGridCell("Type of instalments", bold, Element.ALIGN_CENTER, 3));
        table.addCell(getGridCell("Number of\nEPIs", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("EPI (₹)", bold, Element.ALIGN_CENTER, 2));
        table.addCell(getGridCell("Commencement of repayment, post disbursement", bold, Element.ALIGN_CENTER, 5));

        // --- ROW 5 Values ---
        table.addCell(getGridCell("", normal, 1));
        table.addCell(getGridCell("Monthly", normal, Element.ALIGN_CENTER, 3));
        table.addCell(getGridCell("48", normal, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("8349.00", normal, Element.ALIGN_CENTER, 2));
        table.addCell(getGridCell("7th of next month post disbursement", normal, Element.ALIGN_CENTER, 5));

        // --- ROW 6 ---
        table.addCell(getGridCell("6", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Interest rate (%) and type (fixed or floating or hybrid)", normal, 5));
        table.addCell(getGridCell("15% PER ANNUM (FIXED RATE OF INTEREST)", normal, 6));

        // --- ROW 7 ---
        table.addCell(getGridCell("7", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Additional Information in case of Floating rate of interest:\nReference Benchmark / Benchmark rate (%) (B) / Spread (%) (S)\nFinal rate (%) R=(B)+(S) / Reset periodicity (Months)", normal, 5));
        table.addCell(getGridCell("NOT APPLICABLE / NOT APPLICABLE\nNOT APPLICABLE / NOT APPLICABLE\nNOT APPLICABLE", normal, 6));

        // --- ROW 8 ---
        table.addCell(getGridCell("8", bold, Element.ALIGN_CENTER, 1));
        table.addCell(getGridCell("Impact of change in the reference benchmark\nEPI (₹) / No. of EPIs", normal, 5));
        table.addCell(getGridCell("NOT APPLICABLE\nNOT APPLICABLE", normal, 6));

        document.add(table);
    }

    private void createPage1UpfrontFeesTable(Document document) throws Exception {
        Paragraph p = new Paragraph("\nDetails of Upfront Fees & One-time Charges", subtitleFont);
        p.setSpacingAfter(4);
        document.add(p);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {0.5f, 3.5f, 2f, 2f, 2f, 2f});

        PdfPCell c1 = getCell("Fee/ Charges", bold, Element.ALIGN_CENTER); c1.setRowspan(2); c1.setColspan(2); table.addCell(c1);
        PdfPCell c2 = getCell("Payable to the RE (A)", bold, Element.ALIGN_CENTER); c2.setColspan(2); table.addCell(c2);
        PdfPCell c3 = getCell("Payable to a third party through RE (B)", bold, Element.ALIGN_CENTER); c3.setColspan(2); table.addCell(c3);

        table.addCell(getCell("One-time/ Recurring", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Amount (in Rs.) / %", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("One-time/ Recurring", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Amount (in Rs.) / %", bold, Element.ALIGN_CENTER));

        // i
        table.addCell(getCell("(i)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Processing fees", normal));
        table.addCell(getCell("One-time", normal, Element.ALIGN_CENTER)); table.addCell(getCell("2359.00", normal, Element.ALIGN_RIGHT));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        // ii
        table.addCell(getCell("(ii)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Insurance charges", normal));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        // iii
        table.addCell(getCell("(iii)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Valuation fees", normal));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        // iv
        table.addCell(getCell("(iv)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Stamp Duty", normal));
        table.addCell(getCell("One-time", normal, Element.ALIGN_CENTER)); table.addCell(getCell("300.00", normal, Element.ALIGN_RIGHT));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));
        // v
        table.addCell(getCell("(v)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Gap Interest", normal));
        table.addCell(getCell("One-time", normal, Element.ALIGN_CENTER)); table.addCell(getCell("0.00", normal, Element.ALIGN_RIGHT));
        table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_CENTER)); table.addCell(getCell("NOT APPLICABLE", normal, Element.ALIGN_RIGHT));

        document.add(table);
    }

    private void createPage1BottomSummary(Document document) throws Exception {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {0.5f, 5.5f, 4f});
        table.setSpacingBefore(6);

        table.addCell(getCell("9", bold));
        table.addCell(getCell("Annual Percentage Rate (APR) (%)", bold));
        table.addCell(getCell("15.46%", bold));

        table.addCell(getCell("10", bold));
        table.addCell(getCell("Details of Contingent Charges (in Rs. or %, as applicable)", bold));
        table.addCell(getCell("See details on Next Page...", normal));

        document.add(table);
    }

    private void createPage2ContingentChargesTable(Document document) throws Exception {
        Paragraph title = new Paragraph("Details of Contingent Charges (Penalties & Foreclosures)\n", subtitleFont);
        title.setSpacingAfter(6);
        document.add(title);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {0.6f, 4.4f, 5f});

        table.addCell(getCell("(i)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Penal charges, if any, in case of delayed payment", bold));
        table.addCell(getCell("Delayed instalment payment charge: 18% p.a plus applicable government taxes on overdue instalment amount", normal));

        table.addCell(getCell("(ii)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Other penal charges, if any", bold));
        table.addCell(getCell("Payment Return Charges: without prejudice to the civil and criminal rights and remedies of the bank for the dishonour:\nRs 450 + applicable government taxes per instance", normal));

        table.addCell(getCell("(iii)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Foreclosure / Premature Closure charges, if applicable", bold));
        table.addCell(getCell("Premature Closure Charges For full pre-payment:\nComputed on principal outstanding Post Cooling off/look-up Period of 03 days from the loan disbursal date.\n- Up-to 24 EMI repayment - 4% of principal outstanding\n- Post 24 EMI and up to 36 EMI repayment - 3% of principal outstanding\n- Post 36 EMI repayment - 2% of principal outstanding\nGovernment taxes charged additionally.\n\nPremature closure charges for part pre-payment:\nComputed on part-payment amount. Allowed post payment of first EMI.\n- Post 01 EMI and up to 24 EMI repayment - 4% of part-payment amount.\n- Post 24 EMI and up to 36 EMI repayment - 3% of part-payment amount.\n- Post 36 EMI repayment - 2% of part-payment amount.\n*Allowed up to 25% of Principal Outstanding, only once in the financial year and twice during the loan tenure.", normal));

        table.addCell(getCell("(iv)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Charges for switching of loans from floating to fixed rate and vice versa", bold));
        table.addCell(getCell("NOT APPLICABLE", normal));

        table.addCell(getCell("(v)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Any other charges (please specify)", bold));
        table.addCell(getCell("Loan Cancellation Charges: Allowed within cooling off/look-up period of 03 days. Interest from disbursement to cancellation date is borne by customer. Processing Fee, Stamp duty and GST are non-refundable.\n\nRebooking Charges: Rs. 1000/- + taxes\nRe-payment mode change charges: Rs. 500/- + taxes\nAmortization Schedule Charges: Rs 50/- + taxes for Physical copy (Free via Web portal)\nLegal Collection & Incidental Charges: At Actuals\nStamp Duty & Other statutory charges: As per state laws.", normal));

        document.add(table);
    }

    private void createPage3QualitativeInfoTable(Document document) throws Exception {
        Paragraph title = new Paragraph("Part 2 (Other qualitative information)\n", titleFont);
        title.setSpacingAfter(6);
        document.add(title);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {0.6f, 4.4f, 5f});

        table.addCell(getCell("1", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("Clause of Loan agreement relating to engagement of recovery agents", bold));
        table.addCell(getCell("In the event of default, the details of authorised associate to approach for recovery of dues will be intimated to you through a payment reminder communication and any change in details would be intimated to you thereon. List of authorised associates empanelled for handling collections are updated on the bank's website.", normal));

        table.addCell(getCell("2", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("Clause of Loan agreement which details grievance redressal mechanism", bold));
        table.addCell(getCell("Name, designation, address and phone number of nodal grievance redressal officer updated on the bank's website: https://www.hdfcbank.com/personal/need-help/grievance-redressal-digital", normal));

        table.addCell(getCell("3", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("Phone number and email id of the nodal grievance redressal officer", bold));
        table.addCell(getCell("Mrs. Shalini Tandon (Grievance Redressal Officer)\nGrievance Redressal Cell, HDFC Bank Limited, 1st Floor, Empire Plaza - 1, Lal Bahadur Shastri Marg, Chandan Nagar, Vikroli West, Mumbai - 400083.\nCall: 1800 266 4060 | Email ID: grievance.redressaldl@hdfcbank.com\nMonday to Saturday: 9:30 am to 5:30 pm (Not available on 2nd & 4th Saturdays, Sundays and Bank Holidays)", normal));

        table.addCell(getCell("4", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("Whether the loan is, or in future maybe, subject to transfer to other REs or securitisation (Yes/ No)", bold));
        table.addCell(getCell("Yes", normal));

        table.addCell(getCell("5", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("In case of lending under collaborative lending arrangements (e.g., co-lending/ outsourcing), following additional details may be furnished:\n- Name of partner RE / funding proportion\n- Blended rate of interest\n- Name of originating RE / proportion", bold));
        table.addCell(getCell("NOT APPLICABLE\n\nNOT APPLICABLE\n\nNOT APPLICABLE", normal));

        table.addCell(getCell("6", normal, Element.ALIGN_CENTER));
        table.addCell(getCell("In case of digital loans, following specific disclosures may be furnished:\n(i) Cooling off/look-up period\n(ii) Details of LSP acting as recovery agent", bold));
        table.addCell(getCell("3 Days\n\nIn the event of default, details will be shared via payment reminder communication. Active collection vendor list link is active on bank website.", normal));

        document.add(table);

        Paragraph notes = new Paragraph("\nNote:-\n'The fees and service charges mentioned in the Key Fact Statement shall be deemed to be considered as issuance of receipt.'\na) All the above service charges / fees / Commissions are exclusive of taxes.\nb) 10% discount to senior citizens on all the charges.", smallNormal);
        document.add(notes);
    }

    private void createPage4ComputationOfAprTable(Document document) throws Exception {
        Paragraph title = new Paragraph("Computation of APR\n", titleFont);
        title.setSpacingAfter(4);
        document.add(title);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {0.8f, 5.2f, 4f});

        table.addCell(getCell("Sr. No.", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Parameter", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Details", bold, Element.ALIGN_CENTER));

        table.addCell(getCell("(1)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Disbursed Loan amount (in Rupees)", normal)); table.addCell(getCell("300000", normal));
        table.addCell(getCell("(2)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Loan Term (years / months / days)", normal)); table.addCell(getCell("48 months", normal));
        table.addCell(getCell("(a)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("No. of instalments for payment of principal, in case of non-equated periodic loans", normal)); table.addCell(getCell("NOT APPLICABLE", normal));
        table.addCell(getCell("(b)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Type of EPI / Amount of each EPI and nos of EPIs", normal)); table.addCell(getCell("Monthly\n8349.00 / 48", normal));
        table.addCell(getCell("(c)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("No. of instalments for payment of capitalised interest, if any", normal)); table.addCell(getCell("NOT APPLICABLE", normal));
        table.addCell(getCell("(d)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Commencement of repayments, post disbursement", normal)); table.addCell(getCell("7th of next month post disbursement", normal));
        table.addCell(getCell("(3)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Interest rate type (fixed or floating or hybrid)", normal)); table.addCell(getCell("Fixed", normal));
        table.addCell(getCell("(4)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Rate of Interest", normal)); table.addCell(getCell("15% Per Annum", normal));
        table.addCell(getCell("(5)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Total Interest Amount to be charged during the entire tenor of the loan", normal)); table.addCell(getCell("100763", normal));
        table.addCell(getCell("(6)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Fee/ Charges payable (in Rupees)\n(a) Payable to the RE\n(b) Payable to third-party routed through RE", normal)); table.addCell(getCell("2659\n2659\nNot Applicable", normal));
        table.addCell(getCell("(7)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Net disbursed amount (1-6) (in Rupees)", normal)); table.addCell(getCell("297341.00", bold));
        table.addCell(getCell("(8)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Total amount to be paid by the borrower (sum of 1 and 5) (in Rupees)", normal)); table.addCell(getCell("400763", bold));
        table.addCell(getCell("(9)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Annual Percentage Rate- Effective annualized interest rate (in percentage)", normal)); table.addCell(getCell("15.46%", bold));
        table.addCell(getCell("(10)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Schedule of disbursement as per terms and conditions", normal)); table.addCell(getCell("Detailed schedule attached", normal));
        table.addCell(getCell("(11)", normal, Element.ALIGN_CENTER)); table.addCell(getCell("Due date of payment of instalment and interest", normal)); table.addCell(getCell("7th of every month", normal));

        document.add(table);
        
        Paragraph note = new Paragraph("\n*Net Disbursed Amount might vary depending on the gap interest amount, if applicable", smallNormal);
        document.add(note);
    }

    private void createRepaymentSchedule(Document document) throws Exception {
        Paragraph title = new Paragraph("Illustrative Repayment Schedule", titleFont);
        title.setSpacingAfter(4);
        document.add(title);
        
        Paragraph details = new Paragraph("Customer Name: *********** Loan Type: Personal Loan\nAmount Financed: 300000    Tenure: 48 Months    Frequency: Monthly\n", bold);
        details.setSpacingAfter(6);
        document.add(details);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {1f, 1.8f, 1.8f, 1.8f, 1.8f, 2.2f});
        table.setHeaderRows(1);

        table.addCell(getCell("Instalment No.", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Due Date", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Instalment Amt (₹)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Principal (₹)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Interest (₹)", bold, Element.ALIGN_CENTER));
        table.addCell(getCell("Outstanding Principal (₹)", bold, Element.ALIGN_CENTER));

        String[][] rawData = {
            {"1", "07-Dec-25", "8349", "4599.22", "3750.00", "295400.78"},
            {"2", "07-Jan-26", "8349", "4656.71", "3692.51", "290744.07"},
            {"3", "07-Feb-26", "8349", "4714.92", "3634.30", "286029.15"},
            {"4", "07-Mar-26", "8349", "4773.86", "3575.36", "281255.29"},
            {"5", "07-Apr-26", "8349", "4833.53", "3515.69", "276421.76"},
            {"6", "07-May-26", "8349", "4893.95", "3455.27", "271527.81"},
            {"7", "07-Jun-26", "8349", "4955.12", "3394.10", "266572.69"},
            {"8", "07-Jul-26", "8349", "5017.06", "3332.16", "261555.63"},
            {"9", "07-Aug-26", "8349", "5079.77", "3269.45", "256475.86"},
            {"10", "07-Sep-26", "8349", "5143.27", "3205.95", "251332.59"},
            {"11", "07-Oct-26", "8349", "5207.56", "3141.66", "246125.03"},
            {"12", "07-Nov-26", "8349", "5272.66", "3076.56", "240852.37"},
            {"13", "07-Dec-26", "8349", "5338.57", "3010.65", "235513.80"},
            {"14", "07-Jan-27", "8349", "5405.30", "2943.92", "230108.50"},
            {"15", "07-Feb-27", "8349", "5472.86", "2876.36", "224635.64"},
            {"16", "07-Mar-27", "8349", "5541.27", "2807.95", "219094.37"},
            {"17", "07-Apr-27", "8349", "5610.54", "2738.68", "213483.83"},
            {"18", "07-May-27", "8349", "5680.67", "2668.55", "207803.16"},
            {"19", "07-Jun-27", "8349", "5751.68", "2597.54", "202051.48"},
            {"20", "07-Jul-27", "8349", "5823.58", "2525.64", "196227.90"},
            {"21", "07-Aug-27", "8349", "5896.37", "2452.85", "190331.53"},
            {"22", "07-Sep-27", "8349", "5970.08", "2379.14", "184361.45"},
            {"23", "07-Oct-27", "8349", "6044.70", "2304.52", "178316.75"},
            {"24", "07-Nov-27", "8349", "6120.26", "2228.96", "172196.49"},
            {"25", "07-Dec-27", "8349", "6196.76", "2152.46", "165999.73"},
            {"26", "07-Jan-28", "8349", "6274.22", "2075.00", "159725.51"},
            {"27", "07-Feb-28", "8349", "6352.65", "1996.57", "153372.86"},
            {"28", "07-Mar-28", "8349", "6432.06", "1917.16", "146940.80"},
            {"29", "07-Apr-28", "8349", "6512.46", "1836.76", "140428.34"},
            {"30", "07-May-28", "8349", "6593.87", "1755.35", "133834.47"},
            {"31", "07-Jun-28", "8349", "6676.29", "1672.93", "127158.18"},
            {"32", "07-Jul-28", "8349", "6759.74", "1589.48", "120398.44"},
            {"33", "07-Aug-28", "8349", "6844.24", "1504.98", "113554.20"},
            {"34", "07-Sep-28", "8349", "6929.79", "1419.43", "106624.41"},
            {"35", "07-Oct-28", "8349", "7016.41", "1332.81", "99608.00"},
            {"36", "07-Nov-28", "8349", "7104.12", "1245.10", "92503.88"},
            {"37", "07-Dec-28", "8349", "7192.92", "1156.30", "85310.96"},
            {"38", "07-Jan-29", "8349", "7282.83", "1066.39", "78028.13"},
            {"39", "07-Feb-29", "8349", "7373.87", "975.35", "70654.26"},
            {"40", "07-Mar-29", "8349", "7466.04", "883.18", "63188.22"},
            {"41", "07-Apr-29", "8349", "7559.37", "789.85", "55628.85"},
            {"42", "07-May-29", "8349", "7653.86", "695.36", "47974.99"},
            {"43", "07-Jun-29", "8349", "7749.53", "599.69", "40225.46"},
            {"44", "07-Jul-29", "8349", "7846.40", "502.82", "32379.06"},
            {"45", "07-Aug-29", "8349", "7944.48", "404.74", "24434.58"},
            {"46", "07-Sep-29", "8349", "8043.79", "305.43", "16390.79"},
            {"47", "07-Oct-29", "8349", "8144.34", "204.88", "8246.45"},
            {"48", "07-Nov-29", "8350", "8246.45", "103.08", "0.00"}
        };
        
        for (String[] row : rawData) {
            table.addCell(getCell(row[0], normal, Element.ALIGN_CENTER));
            table.addCell(getCell(row[1], normal, Element.ALIGN_CENTER));
            table.addCell(getCell(row[2], normal, Element.ALIGN_RIGHT));
            table.addCell(getCell(row[3], normal, Element.ALIGN_RIGHT));
            table.addCell(getCell(row[4], normal, Element.ALIGN_RIGHT));
            table.addCell(getCell(row[5], normal, Element.ALIGN_RIGHT));
        }

        // Summary Line
        PdfPCell summaryLabel = new PdfPCell(new Phrase("Total", bold));
        summaryLabel.setColspan(2);
        table.addCell(summaryLabel);
        table.addCell(getCell("400763", bold, Element.ALIGN_RIGHT));
        table.addCell(getCell("300000", bold, Element.ALIGN_RIGHT));
        table.addCell(getCell("100763", bold, Element.ALIGN_RIGHT));
        table.addCell(getCell("", normal));

        document.add(table);
    }

    private PdfPCell getGridCell(String text, Font font, int colspan) {
        return getGridCell(text, font, Element.ALIGN_LEFT, colspan);
    }

    private PdfPCell getGridCell(String text, Font font, int alignment, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private PdfPCell getCell(String text, Font font) {
        return getCell(text, font, Element.ALIGN_LEFT);
    }

    private PdfPCell getCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private PdfPCell getNoBorderCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
}