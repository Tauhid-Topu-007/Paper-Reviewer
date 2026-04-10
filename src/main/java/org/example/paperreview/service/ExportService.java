package org.example.paperreview.service;

import org.example.paperreview.model.PaperAnalysis;
import org.example.paperreview.model.ExtractedInfo;
import org.apache.poi.xwpf.usermodel.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService {

    public void exportToCSV(PaperAnalysis analysis, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            ExtractedInfo info = analysis.getExtractedInfo();

            writer.append("CATEGORY,SUB_CATEGORY,VALUE\n");

            // Basic Information
            writer.append("BASIC INFORMATION,Paper Title,").append(escapeCSV(info.getPaperTitle())).append("\n");
            writer.append("BASIC INFORMATION,Authors,").append(escapeCSV(info.getAuthors())).append("\n");
            writer.append("BASIC INFORMATION,Publication Year,").append(escapeCSV(info.getPublicationYear())).append("\n");
            writer.append("BASIC INFORMATION,Venue,").append(escapeCSV(info.getPublicationVenue())).append("\n");
            writer.append("BASIC INFORMATION,DOI,").append(escapeCSV(info.getDoi())).append("\n");
            writer.append("BASIC INFORMATION,Keywords,").append(escapeCSV(info.getKeywords())).append("\n");

            // Abstract
            writer.append("\nABSTRACT,Abstract Text,").append(escapeCSV(info.getAbstractText())).append("\n");

            // Research Components
            writer.append("\nRESEARCH COMPONENTS,Research Domain,").append(escapeCSV(info.getResearchDomain())).append("\n");
            writer.append("RESEARCH COMPONENTS,Research Problem,").append(escapeCSV(info.getResearchProblem())).append("\n");
            writer.append("RESEARCH COMPONENTS,Research Gap,").append(escapeCSV(info.getResearchGap())).append("\n");
            writer.append("RESEARCH COMPONENTS,Research Questions,").append(escapeCSV(String.join("; ", info.getResearchQuestions()))).append("\n");
            writer.append("RESEARCH COMPONENTS,Hypothesis,").append(escapeCSV(info.getResearchHypothesis())).append("\n");
            writer.append("RESEARCH COMPONENTS,Objectives,").append(escapeCSV(info.getResearchObjectives())).append("\n");
            writer.append("RESEARCH COMPONENTS,Contributions,").append(escapeCSV(info.getContributions())).append("\n");

            // Models Used
            writer.append("\nMODELS & FRAMEWORKS,Models Used,").append(escapeCSV(String.join(", ", info.getModelsUsedInPaper()))).append("\n");
            writer.append("MODELS & FRAMEWORKS,Algorithms Used,").append(escapeCSV(String.join(", ", info.getAlgorithmsUsedInPaper()))).append("\n");
            writer.append("MODELS & FRAMEWORKS,Frameworks Used,").append(escapeCSV(String.join(", ", info.getFrameworksUsedInPaper()))).append("\n");

            // Tables and Figures
            writer.append("\nTABLES & FIGURES,Number of Tables,").append(String.valueOf(info.getTableCount())).append("\n");
            writer.append("TABLES & FIGURES,Number of Figures,").append(String.valueOf(info.getFigureCount())).append("\n");
            writer.append("TABLES & FIGURES,Number of Equations,").append(String.valueOf(info.getEquationCount())).append("\n");

            // Methodology
            writer.append("\nMETHODOLOGY,Methodology Type,").append(escapeCSV(info.getMethodology())).append("\n");
            writer.append("METHODOLOGY,Data Collection,").append(escapeCSV(info.getDataCollectionMethods())).append("\n");
            writer.append("METHODOLOGY,Sample Size,").append(escapeCSV(info.getSampleSize())).append("\n");
            writer.append("METHODOLOGY,Data Analysis,").append(escapeCSV(info.getDataAnalysisMethods())).append("\n");
            writer.append("METHODOLOGY,Tools Used,").append(escapeCSV(info.getToolsAndTechnologies())).append("\n");
            writer.append("METHODOLOGY,Evaluation Metrics,").append(escapeCSV(info.getEvaluationMetrics())).append("\n");

            // Results
            writer.append("\nRESULTS,Key Findings,").append(escapeCSV(String.join("; ", info.getKeyFindings()))).append("\n");
            writer.append("RESULTS,Results Summary,").append(escapeCSV(String.join("; ", info.getResults()))).append("\n");
            writer.append("RESULTS,Statistical Results,").append(escapeCSV(String.join(", ", info.getStatisticalResults()))).append("\n");

            // Analysis
            writer.append("\nANALYSIS,Discussion,").append(escapeCSV(info.getDiscussion())).append("\n");
            writer.append("ANALYSIS,Limitations,").append(escapeCSV(info.getLimitations())).append("\n");
            writer.append("ANALYSIS,Future Work,").append(escapeCSV(info.getFutureWork())).append("\n");
            writer.append("ANALYSIS,Practical Implications,").append(escapeCSV(String.join("; ", info.getPracticalImplications()))).append("\n");
            writer.append("ANALYSIS,Theoretical Implications,").append(escapeCSV(String.join("; ", info.getTheoreticalImplications()))).append("\n");

            // Metrics
            writer.append("\nCOMPARISON METRICS,Quality Score,").append(String.valueOf(analysis.getQualityScore())).append("/100\n");
            writer.append("COMPARISON METRICS,Readability Score,").append(String.format("%.2f", info.getReadabilityScore())).append("\n");
            writer.append("COMPARISON METRICS,Language Complexity,").append(escapeCSV(info.getLanguageComplexity())).append("\n");
            writer.append("COMPARISON METRICS,Total References,").append(String.valueOf(info.getTotalReferences())).append("\n");
            writer.append("COMPARISON METRICS,Page Count,").append(String.valueOf(info.getPageCount())).append("\n");

            // Strengths and Weaknesses
            writer.append("\nSTRENGTHS,Strengths,").append(escapeCSV(String.join("; ", analysis.getStrengths()))).append("\n");
            writer.append("\nWEAKNESSES,Weaknesses,").append(escapeCSV(String.join("; ", analysis.getWeaknesses()))).append("\n");

            // Assessments
            writer.append("\nASSESSMENTS,Writing Quality,").append(escapeCSV(analysis.getWritingQualityComments())).append("\n");
            writer.append("ASSESSMENTS,Novelty Assessment,").append(escapeCSV(analysis.getNoveltyAssessment())).append("\n");
            writer.append("ASSESSMENTS,Impact Assessment,").append(escapeCSV(analysis.getImpactAssessment())).append("\n");
            writer.append("ASSESSMENTS,Plagiarism Risk,").append(escapeCSV(analysis.getPlagiarismRiskIndicator())).append("\n");
            writer.append("ASSESSMENTS,Recommendation,").append(escapeCSV(analysis.getRecommendation())).append("\n");

            // Summary
            writer.append("\nEXECUTIVE SUMMARY,Summary,").append(escapeCSV(analysis.getSummary())).append("\n");
        }
    }

    public void exportToDOCX(PaperAnalysis analysis, String filePath) throws IOException {
        XWPFDocument document = new XWPFDocument();
        ExtractedInfo info = analysis.getExtractedInfo();

        // Title
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setText("ACADEMIC PAPER ANALYSIS REPORT");
        titleRun.addBreak();
        titleRun.setFontSize(14);
        titleRun.setText("Comprehensive Review Report");

        // Basic Information
        addSection(document, "1. BASIC INFORMATION",
                "Paper Title: " + info.getPaperTitle() + "\n" +
                        "Authors: " + info.getAuthors() + "\n" +
                        "Publication Year: " + info.getPublicationYear() + "\n" +
                        "Venue: " + info.getPublicationVenue() + "\n" +
                        "DOI: " + info.getDoi() + "\n" +
                        "Keywords: " + info.getKeywords());

        // Abstract
        addSection(document, "2. ABSTRACT", info.getAbstractText());

        // Research Components
        addSection(document, "3. RESEARCH COMPONENTS",
                "Research Domain: " + info.getResearchDomain() + "\n\n" +
                        "Research Problem: " + info.getResearchProblem() + "\n\n" +
                        "Research Gap: " + info.getResearchGap() + "\n\n" +
                        "Research Questions: \n" + formatList(info.getResearchQuestions()) + "\n" +
                        "Hypothesis: " + info.getResearchHypothesis() + "\n\n" +
                        "Objectives: " + info.getResearchObjectives() + "\n\n" +
                        "Contributions: " + info.getContributions());

        // Models Used
        StringBuilder modelsText = new StringBuilder();
        if (info.getModelsUsedInPaper() != null && !info.getModelsUsedInPaper().isEmpty()) {
            modelsText.append("Models: ").append(String.join(", ", info.getModelsUsedInPaper())).append("\n");
        }
        if (info.getAlgorithmsUsedInPaper() != null && !info.getAlgorithmsUsedInPaper().isEmpty()) {
            modelsText.append("Algorithms: ").append(String.join(", ", info.getAlgorithmsUsedInPaper())).append("\n");
        }
        if (info.getFrameworksUsedInPaper() != null && !info.getFrameworksUsedInPaper().isEmpty()) {
            modelsText.append("Frameworks: ").append(String.join(", ", info.getFrameworksUsedInPaper())).append("\n");
        }
        if (modelsText.length() > 0) {
            addSection(document, "4. MODELS & ALGORITHMS USED", modelsText.toString());
        }

        // Methodology
        addSection(document, "5. METHODOLOGY",
                "Methodology: " + info.getMethodology() + "\n\n" +
                        "Data Collection: " + info.getDataCollectionMethods() + "\n\n" +
                        "Sample Size: " + info.getSampleSize() + "\n\n" +
                        "Data Analysis: " + info.getDataAnalysisMethods() + "\n\n" +
                        "Tools Used: " + info.getToolsAndTechnologies() + "\n\n" +
                        "Evaluation Metrics: " + info.getEvaluationMetrics());

        // Results
        addSection(document, "6. RESULTS",
                "Key Findings:\n" + formatList(info.getKeyFindings()) + "\n\n" +
                        "Results Summary:\n" + formatList(info.getResults()) + "\n\n" +
                        "Statistical Results: " + String.join(", ", info.getStatisticalResults()));

        // Analysis
        addSection(document, "7. ANALYSIS",
                "Discussion: " + info.getDiscussion() + "\n\n" +
                        "Limitations: " + info.getLimitations() + "\n\n" +
                        "Future Work: " + info.getFutureWork() + "\n\n" +
                        "Practical Implications:\n" + formatList(info.getPracticalImplications()) + "\n\n" +
                        "Theoretical Implications:\n" + formatList(info.getTheoreticalImplications()));

        // Metrics
        addSection(document, "8. PAPER METRICS",
                "Page Count: " + info.getPageCount() + "\n" +
                        "Figures: " + info.getFigureCount() + "\n" +
                        "Tables: " + info.getTableCount() + "\n" +
                        "Equations: " + info.getEquationCount() + "\n" +
                        "Total References: " + info.getTotalReferences() + "\n" +
                        "Citations Found: " + info.getCitations().size() + "\n" +
                        "Readability Score: " + String.format("%.2f", info.getReadabilityScore()) + "\n" +
                        "Language Complexity: " + info.getLanguageComplexity());

        // Quality Assessment
        addSection(document, "9. QUALITY ASSESSMENT",
                "Quality Score: " + analysis.getQualityScore() + "/100\n\n" +
                        "Writing Quality: " + analysis.getWritingQualityComments() + "\n\n" +
                        "Novelty: " + analysis.getNoveltyAssessment() + "\n\n" +
                        "Impact: " + analysis.getImpactAssessment() + "\n\n" +
                        "Plagiarism Risk: " + analysis.getPlagiarismRiskIndicator() + "\n\n" +
                        "Recommendation: " + analysis.getRecommendation());

        // Summary
        addSection(document, "10. EXECUTIVE SUMMARY", analysis.getSummary());
        addSection(document, "11. STRENGTHS", formatList(analysis.getStrengths()));
        addSection(document, "12. WEAKNESSES", formatList(analysis.getWeaknesses()));
        addSection(document, "13. FUTURE RESEARCH SCOPE", formatList(analysis.getFutureResearchScope()));

        // Save document
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        }
        document.close();
    }

    public void exportToPDF(PaperAnalysis analysis, String filePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        ExtractedInfo info = analysis.getExtractedInfo();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Title
        Paragraph title = new Paragraph("ACADEMIC PAPER ANALYSIS REPORT", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Basic Information
        addPDFSection(document, "1. BASIC INFORMATION", headingFont);
        addPDFLine(document, "Paper Title: " + info.getPaperTitle(), normalFont);
        addPDFLine(document, "Authors: " + info.getAuthors(), normalFont);
        addPDFLine(document, "Year: " + info.getPublicationYear(), normalFont);
        addPDFLine(document, "Venue: " + info.getPublicationVenue(), normalFont);
        addPDFLine(document, "DOI: " + info.getDoi(), normalFont);
        document.add(new Paragraph(" "));

        // Abstract
        addPDFSection(document, "2. ABSTRACT", headingFont);
        addPDFLine(document, info.getAbstractText(), normalFont);
        document.add(new Paragraph(" "));

        // Research Components
        addPDFSection(document, "3. RESEARCH COMPONENTS", headingFont);
        addPDFLine(document, "Domain: " + info.getResearchDomain(), normalFont);
        addPDFLine(document, "Problem: " + info.getResearchProblem(), normalFont);
        addPDFLine(document, "Gap: " + info.getResearchGap(), normalFont);
        addPDFLine(document, "Hypothesis: " + info.getResearchHypothesis(), normalFont);
        document.add(new Paragraph(" "));

        // Models Used
        if (info.getModelsUsedInPaper() != null && !info.getModelsUsedInPaper().isEmpty()) {
            addPDFSection(document, "4. MODELS & ALGORITHMS", headingFont);
            addPDFLine(document, "Models: " + String.join(", ", info.getModelsUsedInPaper()), normalFont);
            addPDFLine(document, "Algorithms: " + String.join(", ", info.getAlgorithmsUsedInPaper()), normalFont);
            addPDFLine(document, "Frameworks: " + String.join(", ", info.getFrameworksUsedInPaper()), normalFont);
            document.add(new Paragraph(" "));
        }

        // Methodology
        addPDFSection(document, "5. METHODOLOGY", headingFont);
        addPDFLine(document, info.getMethodology(), normalFont);
        document.add(new Paragraph(" "));

        // Results
        addPDFSection(document, "6. KEY FINDINGS", headingFont);
        for (String finding : info.getKeyFindings()) {
            addPDFBullet(document, finding, normalFont);
        }
        document.add(new Paragraph(" "));

        // Quality Assessment
        addPDFSection(document, "7. QUALITY ASSESSMENT", headingFont);
        addPDFLine(document, "Quality Score: " + analysis.getQualityScore() + "/100", boldFont);
        addPDFLine(document, "Writing Quality: " + analysis.getWritingQualityComments(), normalFont);
        addPDFLine(document, "Recommendation: " + analysis.getRecommendation(), boldFont);
        document.add(new Paragraph(" "));

        // Summary
        addPDFSection(document, "8. EXECUTIVE SUMMARY", headingFont);
        addPDFLine(document, analysis.getSummary(), normalFont);
        document.add(new Paragraph(" "));

        // Strengths and Weaknesses
        addPDFSection(document, "9. STRENGTHS", headingFont);
        for (String strength : analysis.getStrengths()) {
            addPDFBullet(document, strength, normalFont);
        }
        document.add(new Paragraph(" "));

        addPDFSection(document, "10. WEAKNESSES", headingFont);
        for (String weakness : analysis.getWeaknesses()) {
            addPDFBullet(document, weakness, normalFont);
        }

        document.close();
    }

    private void addSection(XWPFDocument document, String title, String content) {
        XWPFParagraph sectionPara = document.createParagraph();
        XWPFRun sectionRun = sectionPara.createRun();
        sectionRun.setBold(true);
        sectionRun.setFontSize(14);
        sectionRun.setText(title);
        sectionRun.addBreak();

        XWPFParagraph contentPara = document.createParagraph();
        contentPara.createRun().setText(content);
        contentPara.createRun().addBreak();
    }

    private void addPDFSection(Document document, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    private void addPDFLine(Document document, String text, Font font) throws DocumentException {
        Paragraph line = new Paragraph(text, font);
        line.setIndentationLeft(20);
        document.add(line);
    }

    private void addPDFBullet(Document document, String text, Font font) throws DocumentException {
        Paragraph bullet = new Paragraph("  • " + text, font);
        bullet.setIndentationLeft(30);
        document.add(bullet);
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append("• ").append(item).append("\n");
        }
        return sb.toString();
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