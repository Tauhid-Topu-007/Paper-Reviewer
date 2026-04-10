package org.example.paperreview.service;

import org.example.paperreview.model.PaperAnalysis;
import org.apache.poi.xwpf.usermodel.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ExportService {

    public void exportToCSV(PaperAnalysis analysis, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Field,Value\n");
            writer.append("Paper Title,").append(escapeCSV(analysis.getExtractedInfo().getPaperTitle())).append("\n");
            writer.append("Authors,").append(escapeCSV(analysis.getExtractedInfo().getAuthors())).append("\n");
            writer.append("Research Domain,").append(escapeCSV(analysis.getExtractedInfo().getResearchDomain())).append("\n");
            writer.append("Research Problem,").append(escapeCSV(analysis.getExtractedInfo().getResearchProblem())).append("\n");
            writer.append("Research Gap,").append(escapeCSV(analysis.getExtractedInfo().getResearchGap())).append("\n");
            writer.append("Methodology,").append(escapeCSV(analysis.getExtractedInfo().getMethodology())).append("\n");
            writer.append("Quality Score,").append(String.valueOf(analysis.getQualityScore())).append("\n");
            writer.append("Summary,").append(escapeCSV(analysis.getSummary())).append("\n");
        }
    }

    public void exportToDOCX(PaperAnalysis analysis, String filePath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setText("Paper Analysis Report");

        addSection(document, "Paper Information",
                "Title: " + analysis.getExtractedInfo().getPaperTitle() + "\n" +
                        "Authors: " + analysis.getExtractedInfo().getAuthors() + "\n" +
                        "Domain: " + analysis.getExtractedInfo().getResearchDomain());

        addSection(document, "Research Problem", analysis.getExtractedInfo().getResearchProblem());
        addSection(document, "Research Gap", analysis.getExtractedInfo().getResearchGap());
        addSection(document, "Methodology", analysis.getExtractedInfo().getMethodology());
        addSection(document, "Summary", analysis.getSummary());

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        }
        document.close();
    }

    public void exportToPDF(PaperAnalysis analysis, String filePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        document.add(new Paragraph("Paper Analysis Report"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Paper Information"));
        document.add(new Paragraph("Title: " + analysis.getExtractedInfo().getPaperTitle()));
        document.add(new Paragraph("Authors: " + analysis.getExtractedInfo().getAuthors()));
        document.add(new Paragraph("Domain: " + analysis.getExtractedInfo().getResearchDomain()));

        document.add(new Paragraph("\nResearch Problem"));
        document.add(new Paragraph(analysis.getExtractedInfo().getResearchProblem()));

        document.add(new Paragraph("\nResearch Gap"));
        document.add(new Paragraph(analysis.getExtractedInfo().getResearchGap()));

        document.add(new Paragraph("\nMethodology"));
        document.add(new Paragraph(analysis.getExtractedInfo().getMethodology()));

        document.add(new Paragraph("\nQuality Assessment"));
        document.add(new Paragraph("Score: " + analysis.getQualityScore() + "/100"));
        if (analysis.getWritingQualityComments() != null) {
            document.add(new Paragraph(analysis.getWritingQualityComments()));
        }

        document.close();
    }

    private void addSection(XWPFDocument document, String title, String content) {
        XWPFParagraph sectionPara = document.createParagraph();
        XWPFRun sectionRun = sectionPara.createRun();
        sectionRun.setBold(true);
        sectionRun.setFontSize(14);
        sectionRun.setText(title);

        XWPFParagraph contentPara = document.createParagraph();
        contentPara.createRun().setText(content);
        contentPara.createRun().addBreak();
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}