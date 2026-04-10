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

            // Basic Information
            writer.append("CATEGORY,SUB_CATEGORY,VALUE\n");
            writer.append("Basic Information,Paper Title,").append(escapeCSV(info.getPaperTitle())).append("\n");
            writer.append("Basic Information,Authors,").append(escapeCSV(info.getAuthors())).append("\n");
            writer.append("Basic Information,Abstract,").append(escapeCSV(info.getAbstractText())).append("\n");
            writer.append("Basic Information,Keywords,").append(escapeCSV(info.getKeywords())).append("\n");
            writer.append("Basic Information,Publication Year,").append(escapeCSV(info.getPublicationYear())).append("\n");
            writer.append("Basic Information,Publication Venue,").append(escapeCSV(info.getPublicationVenue())).append("\n");
            writer.append("Basic Information,DOI,").append(escapeCSV(info.getDoi())).append("\n");
            writer.append("Basic Information,Corresponding Author,").append(escapeCSV(info.getCorrespondingAuthor())).append("\n");
            writer.append("Basic Information,Affiliations,").append(escapeCSV(info.getAffiliations())).append("\n");

            // Research Components
            writer.append("Research,Research Domain,").append(escapeCSV(info.getResearchDomain())).append("\n");
            writer.append("Research,Research Problem,").append(escapeCSV(info.getResearchProblem())).append("\n");
            writer.append("Research,Research Gap,").append(escapeCSV(info.getResearchGap())).append("\n");
            writer.append("Research,Research Questions,").append(escapeCSV(String.join("; ", info.getResearchQuestions()))).append("\n");
            writer.append("Research,Hypothesis,").append(escapeCSV(info.getResearchHypothesis())).append("\n");
            writer.append("Research,Objectives,").append(escapeCSV(info.getResearchObjectives())).append("\n");
            writer.append("Research,Contributions,").append(escapeCSV(info.getContributions())).append("\n");

            // Methodology
            writer.append("Methodology,Methodology,").append(escapeCSV(info.getMethodology())).append("\n");
            writer.append("Methodology,Data Collection,").append(escapeCSV(info.getDataCollectionMethods())).append("\n");
            writer.append("Methodology,Sample Size,").append(escapeCSV(info.getSampleSize())).append("\n");
            writer.append("Methodology,Data Analysis,").append(escapeCSV(info.getDataAnalysisMethods())).append("\n");
            writer.append("Methodology,Tools Used,").append(escapeCSV(info.getToolsAndTechnologies())).append("\n");
            writer.append("Methodology,Experimental Setup,").append(escapeCSV(info.getExperimentalSetup())).append("\n");
            writer.append("Methodology,Evaluation Metrics,").append(escapeCSV(info.getEvaluationMetrics())).append("\n");

            // Results
            writer.append("Results,Key Findings,").append(escapeCSV(String.join("; ", info.getKeyFindings()))).append("\n");
            writer.append("Results,Results Summary,").append(escapeCSV(String.join("; ", info.getResults()))).append("\n");
            writer.append("Results,Statistical Results,").append(escapeCSV(String.join("; ", info.getStatisticalResults()))).append("\n");

            // Analysis
            writer.append("Analysis,Discussion,").append(escapeCSV(info.getDiscussion())).append("\n");
            writer.append("Analysis,Limitations,").append(escapeCSV(info.getLimitations())).append("\n");
            writer.append("Analysis,Future Work,").append(escapeCSV(info.getFutureWork())).append("\n");
            writer.append("Analysis,Practical Implications,").append(escapeCSV(String.join("; ", info.getPracticalImplications()))).append("\n");
            writer.append("Analysis,Theoretical Implications,").append(escapeCSV(String.join("; ", info.getTheoreticalImplications()))).append("\n");

            // Metadata
            writer.append("Metadata,Page Count,").append(String.valueOf(info.getPageCount())).append("\n");
            writer.append("Metadata,Figure Count,").append(String.valueOf(info.getFigureCount())).append("\n");
            writer.append("Metadata,Table Count,").append(String.valueOf(info.getTableCount())).append("\n");
            writer.append("Metadata,Equation Count,").append(String.valueOf(info.getEquationCount())).append("\n");
            writer.append("Metadata,Total References,").append(String.valueOf(info.getTotalReferences())).append("\n");
            writer.append("Metadata,Citations Found,").append(String.valueOf(info.getCitations().size())).append("\n");
            writer.append("Metadata,Average Sentence Length,").append(String.format("%.2f", info.getAverageSentenceLength())).append("\n");
            writer.append("Metadata,Readability Score,").append(String.format("%.2f", info.getReadabilityScore())).append("\n");
            writer.append("Metadata,Language Complexity,").append(escapeCSV(info.getLanguageComplexity())).append("\n");

            // Key Terms
            if (info.getKeyTerms() != null && !info.getKeyTerms().isEmpty()) {
                writer.append("Key Terms,Terms,").append(escapeCSV(String.join(", ", info.getKeyTerms()))).append("\n");
            }

            // Quality Assessment
            writer.append("Assessment,Quality Score,").append(String.valueOf(analysis.getQualityScore())).append("\n");
            writer.append("Assessment,Writing Quality,").append(escapeCSV(analysis.getWritingQualityComments())).append("\n");
            writer.append("Assessment,Novelty Assessment,").append(escapeCSV(analysis.getNoveltyAssessment())).append("\n");
            writer.append("Assessment,Impact Assessment,").append(escapeCSV(analysis.getImpactAssessment())).append("\n");
            writer.append("Assessment,Plagiarism Risk,").append(escapeCSV(analysis.getPlagiarismRiskIndicator())).append("\n");
            writer.append("Assessment,Recommendation,").append(escapeCSV(analysis.getRecommendation())).append("\n");

            // Summary
            writer.append("Summary,Executive Summary,").append(escapeCSV(analysis.getSummary())).append("\n");
            writer.append("Summary,Strengths,").append(escapeCSV(String.join("; ", analysis.getStrengths()))).append("\n");
            writer.append("Summary,Weaknesses,").append(escapeCSV(String.join("; ", analysis.getWeaknesses()))).append("\n");
            writer.append("Summary,Future Scope,").append(escapeCSV(String.join("; ", analysis.getFutureResearchScope()))).append("\n");
            if (analysis.getSuggestedImprovements() != null && !analysis.getSuggestedImprovements().isEmpty()) {
                writer.append("Summary,Suggested Improvements,").append(escapeCSV(String.join("; ", analysis.getSuggestedImprovements()))).append("\n");
            }
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
        titleRun.setFontSize(18);
        titleRun.setText("Academic Paper Analysis Report");

        // Basic Information
        addSection(document, "1. Basic Information",
                "Title: " + info.getPaperTitle() + "\n" +
                        "Authors: " + info.getAuthors() + "\n" +
                        "Publication Year: " + info.getPublicationYear() + "\n" +
                        "Venue: " + info.getPublicationVenue() + "\n" +
                        "DOI: " + info.getDoi() + "\n" +
                        "Keywords: " + info.getKeywords());

        // Abstract
        addSection(document, "2. Abstract", info.getAbstractText());

        // Research Components
        addSection(document, "3. Research Components",
                "Research Domain: " + info.getResearchDomain() + "\n\n" +
                        "Research Problem: " + info.getResearchProblem() + "\n\n" +
                        "Research Gap: " + info.getResearchGap() + "\n\n" +
                        "Research Questions: \n" + formatList(info.getResearchQuestions()) + "\n" +
                        "Hypothesis: " + info.getResearchHypothesis() + "\n\n" +
                        "Objectives: " + info.getResearchObjectives() + "\n\n" +
                        "Contributions: " + info.getContributions());

        // Methodology
        addSection(document, "4. Methodology",
                "Methodology: " + info.getMethodology() + "\n\n" +
                        "Data Collection: " + info.getDataCollectionMethods() + "\n\n" +
                        "Sample Size: " + info.getSampleSize() + "\n\n" +
                        "Data Analysis: " + info.getDataAnalysisMethods() + "\n\n" +
                        "Tools Used: " + info.getToolsAndTechnologies() + "\n\n" +
                        "Evaluation Metrics: " + info.getEvaluationMetrics());

        // Results
        addSection(document, "5. Results",
                "Key Findings:\n" + formatList(info.getKeyFindings()) + "\n\n" +
                        "Results Summary:\n" + formatList(info.getResults()) + "\n\n" +
                        "Statistical Results: " + String.join(", ", info.getStatisticalResults()));

        // Analysis
        addSection(document, "6. Analysis",
                "Discussion: " + info.getDiscussion() + "\n\n" +
                        "Limitations: " + info.getLimitations() + "\n\n" +
                        "Future Work: " + info.getFutureWork() + "\n\n" +
                        "Practical Implications:\n" + formatList(info.getPracticalImplications()) + "\n\n" +
                        "Theoretical Implications:\n" + formatList(info.getTheoreticalImplications()));

        // Metadata
        addSection(document, "7. Paper Metadata",
                "Page Count: " + info.getPageCount() + "\n" +
                        "Figures: " + info.getFigureCount() + "\n" +
                        "Tables: " + info.getTableCount() + "\n" +
                        "Equations: " + info.getEquationCount() + "\n" +
                        "Total References: " + info.getTotalReferences() + "\n" +
                        "Citations Found: " + info.getCitations().size() + "\n" +
                        "Readability Score: " + String.format("%.2f", info.getReadabilityScore()) + "\n" +
                        "Language Complexity: " + info.getLanguageComplexity());

        // Quality Assessment
        addSection(document, "8. Quality Assessment",
                "Quality Score: " + analysis.getQualityScore() + "/100\n\n" +
                        "Writing Quality: " + analysis.getWritingQualityComments() + "\n\n" +
                        "Novelty: " + analysis.getNoveltyAssessment() + "\n\n" +
                        "Impact: " + analysis.getImpactAssessment() + "\n\n" +
                        "Plagiarism Risk: " + analysis.getPlagiarismRiskIndicator() + "\n\n" +
                        "Recommendation: " + analysis.getRecommendation());

        // Summary
        addSection(document, "9. Executive Summary", analysis.getSummary());
        addSection(document, "10. Strengths", formatList(analysis.getStrengths()));
        addSection(document, "11. Weaknesses", formatList(analysis.getWeaknesses()));
        addSection(document, "12. Future Research Scope", formatList(analysis.getFutureResearchScope()));

        if (analysis.getSuggestedImprovements() != null && !analysis.getSuggestedImprovements().isEmpty()) {
            addSection(document, "13. Suggested Improvements", formatList(analysis.getSuggestedImprovements()));
        }

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

        // Create fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Title
        Paragraph title = new Paragraph("Academic Paper Analysis Report", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Basic Information
        Paragraph basicHeading = new Paragraph("1. Basic Information", headingFont);
        basicHeading.setSpacingBefore(10);
        document.add(basicHeading);
        document.add(new Paragraph("Title: " + info.getPaperTitle(), normalFont));
        document.add(new Paragraph("Authors: " + info.getAuthors(), normalFont));
        document.add(new Paragraph("Year: " + info.getPublicationYear(), normalFont));
        document.add(new Paragraph("Venue: " + info.getPublicationVenue(), normalFont));
        document.add(new Paragraph("DOI: " + info.getDoi(), normalFont));
        document.add(new Paragraph(" "));

        // Abstract
        Paragraph abstractHeading = new Paragraph("2. Abstract", headingFont);
        abstractHeading.setSpacingBefore(10);
        document.add(abstractHeading);
        document.add(new Paragraph(info.getAbstractText(), normalFont));
        document.add(new Paragraph(" "));

        // Research Components
        Paragraph researchHeading = new Paragraph("3. Research Components", headingFont);
        researchHeading.setSpacingBefore(10);
        document.add(researchHeading);
        document.add(new Paragraph("Domain: " + info.getResearchDomain(), normalFont));
        document.add(new Paragraph("Problem: " + info.getResearchProblem(), normalFont));
        document.add(new Paragraph("Gap: " + info.getResearchGap(), normalFont));
        document.add(new Paragraph("Questions: " + String.join(", ", info.getResearchQuestions()), normalFont));
        document.add(new Paragraph("Hypothesis: " + info.getResearchHypothesis(), normalFont));
        document.add(new Paragraph(" "));

        // Methodology
        Paragraph methodHeading = new Paragraph("4. Methodology", headingFont);
        methodHeading.setSpacingBefore(10);
        document.add(methodHeading);
        document.add(new Paragraph(info.getMethodology(), normalFont));
        document.add(new Paragraph(" "));

        // Results
        Paragraph resultsHeading = new Paragraph("5. Key Findings", headingFont);
        resultsHeading.setSpacingBefore(10);
        document.add(resultsHeading);
        for (String finding : info.getKeyFindings()) {
            document.add(new Paragraph("• " + finding, normalFont));
        }
        document.add(new Paragraph(" "));

        // Quality Assessment
        Paragraph qualityHeading = new Paragraph("6. Quality Assessment", headingFont);
        qualityHeading.setSpacingBefore(10);
        document.add(qualityHeading);
        document.add(new Paragraph("Quality Score: " + analysis.getQualityScore() + "/100", boldFont));
        document.add(new Paragraph("Writing Quality: " + analysis.getWritingQualityComments(), normalFont));
        document.add(new Paragraph(" "));

        // Summary
        Paragraph summaryHeading = new Paragraph("7. Executive Summary", headingFont);
        summaryHeading.setSpacingBefore(10);
        document.add(summaryHeading);
        document.add(new Paragraph(analysis.getSummary(), normalFont));
        document.add(new Paragraph(" "));

        // Strengths and Weaknesses
        Paragraph strengthsHeading = new Paragraph("8. Strengths", headingFont);
        strengthsHeading.setSpacingBefore(10);
        document.add(strengthsHeading);
        for (String strength : analysis.getStrengths()) {
            document.add(new Paragraph("• " + strength, normalFont));
        }
        document.add(new Paragraph(" "));

        Paragraph weaknessesHeading = new Paragraph("9. Weaknesses", headingFont);
        weaknessesHeading.setSpacingBefore(10);
        document.add(weaknessesHeading);
        for (String weakness : analysis.getWeaknesses()) {
            document.add(new Paragraph("• " + weakness, normalFont));
        }

        // Recommendation
        Paragraph recommendationHeading = new Paragraph("10. Recommendation", headingFont);
        recommendationHeading.setSpacingBefore(10);
        document.add(recommendationHeading);
        document.add(new Paragraph(analysis.getRecommendation(), boldFont));

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