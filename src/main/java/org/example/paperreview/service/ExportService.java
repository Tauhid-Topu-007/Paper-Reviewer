package org.example.paperreview.service;

import com.itextpdf.text.Document;
import org.example.paperreview.model.PaperAnalysis;
import org.example.paperreview.model.ExtractedInfo;
import org.apache.poi.xwpf.usermodel.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExportService {

    public void exportToCSV(PaperAnalysis analysis, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            ExtractedInfo info = analysis.getExtractedInfo();

            writer.append("CATEGORY,SUB_CATEGORY,VALUE\n");

            // Basic Information - Point by point
            writer.append("BASIC INFORMATION,1. Paper Title,").append(escapeCSV(info.getPaperTitle())).append("\n");
            writer.append("BASIC INFORMATION,2. Authors,").append(escapeCSV(info.getAuthors())).append("\n");
            writer.append("BASIC INFORMATION,3. Publication Year,").append(escapeCSV(info.getPublicationYear())).append("\n");
            writer.append("BASIC INFORMATION,4. Publication Venue,").append(escapeCSV(info.getPublicationVenue())).append("\n");
            writer.append("BASIC INFORMATION,5. DOI,").append(escapeCSV(info.getDoi())).append("\n");
            writer.append("BASIC INFORMATION,6. Keywords,").append(escapeCSV(info.getKeywords())).append("\n");

            // Abstract
            writer.append("\nABSTRACT,Abstract Text,").append(escapeCSV(info.getAbstractText())).append("\n");

            // Research Components - Point by point
            writer.append("\nRESEARCH COMPONENTS,1. Research Domain,").append(escapeCSV(info.getResearchDomain())).append("\n");
            writer.append("RESEARCH COMPONENTS,2. Research Problem,").append(escapeCSV(info.getResearchProblem())).append("\n");
            writer.append("RESEARCH COMPONENTS,3. Research Gap,").append(escapeCSV(info.getResearchGap())).append("\n");
            writer.append("RESEARCH COMPONENTS,4. Research Questions,").append(escapeCSV(String.join("\n• ", info.getResearchQuestions()))).append("\n");
            writer.append("RESEARCH COMPONENTS,5. Hypothesis,").append(escapeCSV(info.getResearchHypothesis())).append("\n");
            writer.append("RESEARCH COMPONENTS,6. Objectives,").append(escapeCSV(info.getResearchObjectives())).append("\n");
            writer.append("RESEARCH COMPONENTS,7. Contributions,").append(escapeCSV(info.getContributions())).append("\n");

            // Models and Frameworks Used
            writer.append("\nMODELS & FRAMEWORKS,Models Used,").append(escapeCSV(String.join(", ", extractModels(info)))).append("\n");
            writer.append("MODELS & FRAMEWORKS,Frameworks Used,").append(escapeCSV(String.join(", ", extractFrameworks(info)))).append("\n");
            writer.append("MODELS & FRAMEWORKS,Algorithms Used,").append(escapeCSV(String.join(", ", extractAlgorithms(info)))).append("\n");

            // Tables and Figures
            writer.append("\nTABLES & FIGURES,Number of Tables,").append(String.valueOf(info.getTableCount())).append("\n");
            writer.append("TABLES & FIGURES,Number of Figures,").append(String.valueOf(info.getFigureCount())).append("\n");
            writer.append("TABLES & FIGURES,Number of Equations,").append(String.valueOf(info.getEquationCount())).append("\n");

            // Table details if available
            if (info.getTableDescriptions() != null && !info.getTableDescriptions().isEmpty()) {
                for (int i = 0; i < info.getTableDescriptions().size(); i++) {
                    writer.append("TABLES & FIGURES,Table " + (i+1) + " Description,").append(escapeCSV(info.getTableDescriptions().get(i))).append("\n");
                }
            }

            // Figure details if available
            if (info.getFigureDescriptions() != null && !info.getFigureDescriptions().isEmpty()) {
                for (int i = 0; i < info.getFigureDescriptions().size(); i++) {
                    writer.append("TABLES & FIGURES,Figure " + (i+1) + " Description,").append(escapeCSV(info.getFigureDescriptions().get(i))).append("\n");
                }
            }

            // Methodology - Point by point
            writer.append("\nMETHODOLOGY,1. Methodology Type,").append(escapeCSV(info.getMethodology())).append("\n");
            writer.append("METHODOLOGY,2. Data Collection,").append(escapeCSV(info.getDataCollectionMethods())).append("\n");
            writer.append("METHODOLOGY,3. Sample Size,").append(escapeCSV(info.getSampleSize())).append("\n");
            writer.append("METHODOLOGY,4. Data Analysis,").append(escapeCSV(info.getDataAnalysisMethods())).append("\n");
            writer.append("METHODOLOGY,5. Tools & Technologies,").append(escapeCSV(info.getToolsAndTechnologies())).append("\n");
            writer.append("METHODOLOGY,6. Experimental Setup,").append(escapeCSV(info.getExperimentalSetup())).append("\n");
            writer.append("METHODOLOGY,7. Evaluation Metrics,").append(escapeCSV(info.getEvaluationMetrics())).append("\n");

            // Results - Point by point
            writer.append("\nRESULTS,1. Key Findings,").append(escapeCSV(String.join("\n• ", info.getKeyFindings()))).append("\n");
            writer.append("RESULTS,2. Results Summary,").append(escapeCSV(String.join("\n• ", info.getResults()))).append("\n");
            writer.append("RESULTS,3. Statistical Results,").append(escapeCSV(String.join(", ", info.getStatisticalResults()))).append("\n");

            // Performance Metrics
            if (info.getPerformanceMetrics() != null && !info.getPerformanceMetrics().isEmpty()) {
                writer.append("RESULTS,4. Performance Metrics,").append(escapeCSV(formatMetrics(info.getPerformanceMetrics()))).append("\n");
            }

            // Analysis - Point by point
            writer.append("\nANALYSIS,1. Discussion,").append(escapeCSV(info.getDiscussion())).append("\n");
            writer.append("ANALYSIS,2. Limitations,").append(escapeCSV(info.getLimitations())).append("\n");
            writer.append("ANALYSIS,3. Future Work,").append(escapeCSV(info.getFutureWork())).append("\n");
            writer.append("ANALYSIS,4. Practical Implications,").append(escapeCSV(String.join("\n• ", info.getPracticalImplications()))).append("\n");
            writer.append("ANALYSIS,5. Theoretical Implications,").append(escapeCSV(String.join("\n• ", info.getTheoreticalImplications()))).append("\n");

            // Comparison Metrics
            writer.append("\nCOMPARISON METRICS,Quality Score,").append(String.valueOf(analysis.getQualityScore())).append("/100\n");
            writer.append("COMPARISON METRICS,Readability Score,").append(String.format("%.2f", info.getReadabilityScore())).append("\n");
            writer.append("COMPARISON METRICS,Language Complexity,").append(escapeCSV(info.getLanguageComplexity())).append("\n");
            writer.append("COMPARISON METRICS,Total References,").append(String.valueOf(info.getTotalReferences())).append("\n");
            writer.append("COMPARISON METRICS,Page Count,").append(String.valueOf(info.getPageCount())).append("\n");

            // Strengths - Point by point
            writer.append("\nSTRENGTHS,Strengths,").append(escapeCSV(String.join("\n• ", analysis.getStrengths()))).append("\n");

            // Weaknesses - Point by point
            writer.append("\nWEAKNESSES,Weaknesses,").append(escapeCSV(String.join("\n• ", analysis.getWeaknesses()))).append("\n");

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
        titleRun.setText("Comprehensive Review and Comparison Report");

        // Basic Information Section
        addPointSection(document, "SECTION 1: BASIC INFORMATION", new String[]{
                "Paper Title: " + info.getPaperTitle(),
                "Authors: " + info.getAuthors(),
                "Publication Year: " + info.getPublicationYear(),
                "Publication Venue: " + info.getPublicationVenue(),
                "DOI: " + info.getDoi(),
                "Keywords: " + info.getKeywords()
        });

        // Abstract Section
        addAbstractSection(document, "SECTION 2: ABSTRACT", info.getAbstractText());

        // Research Components Section
        addPointSection(document, "SECTION 3: RESEARCH COMPONENTS", new String[]{
                "Research Domain: " + info.getResearchDomain(),
                "Research Problem: " + info.getResearchProblem(),
                "Research Gap: " + info.getResearchGap(),
                "Research Questions:",
                "Hypothesis: " + info.getResearchHypothesis(),
                "Objectives: " + info.getResearchObjectives(),
                "Contributions: " + info.getContributions()
        });

        // Add research questions as sub-points
        if (info.getResearchQuestions() != null && !info.getResearchQuestions().isEmpty()) {
            for (String question : info.getResearchQuestions()) {
                addBulletPoint(document, "  • " + question);
            }
        }

        // Models and Frameworks Section
        List<String> models = extractModels(info);
        List<String> frameworks = extractFrameworks(info);
        List<String> algorithms = extractAlgorithms(info);

        if (!models.isEmpty() || !frameworks.isEmpty() || !algorithms.isEmpty()) {
            addSectionHeader(document, "SECTION 4: MODELS, FRAMEWORKS & ALGORITHMS");
            if (!models.isEmpty()) {
                addBulletPoint(document, "Models Used:");
                for (String model : models) {
                    addBulletPoint(document, "  • " + model);
                }
            }
            if (!frameworks.isEmpty()) {
                addBulletPoint(document, "Frameworks Used:");
                for (String framework : frameworks) {
                    addBulletPoint(document, "  • " + framework);
                }
            }
            if (!algorithms.isEmpty()) {
                addBulletPoint(document, "Algorithms Used:");
                for (String algorithm : algorithms) {
                    addBulletPoint(document, "  • " + algorithm);
                }
            }
            addEmptyLine(document);
        }

        // Tables and Figures Section
        addSectionHeader(document, "SECTION 5: TABLES, FIGURES & EQUATIONS");
        addComparisonTable(document, info);

        // Methodology Section
        addPointSection(document, "SECTION 6: METHODOLOGY", new String[]{
                "Methodology Type: " + info.getMethodology(),
                "Data Collection Methods: " + info.getDataCollectionMethods(),
                "Sample Size: " + info.getSampleSize(),
                "Data Analysis Methods: " + info.getDataAnalysisMethods(),
                "Tools & Technologies: " + info.getToolsAndTechnologies(),
                "Experimental Setup: " + info.getExperimentalSetup(),
                "Evaluation Metrics: " + info.getEvaluationMetrics()
        });

        // Results Section
        addSectionHeader(document, "SECTION 7: RESULTS & FINDINGS");
        addBulletPoint(document, "Key Findings:");
        for (String finding : info.getKeyFindings()) {
            addBulletPoint(document, "  • " + finding);
        }
        addEmptyLine(document);

        addBulletPoint(document, "Results Summary:");
        for (String result : info.getResults()) {
            addBulletPoint(document, "  • " + result);
        }
        addEmptyLine(document);

        if (info.getPerformanceMetrics() != null && !info.getPerformanceMetrics().isEmpty()) {
            addBulletPoint(document, "Performance Metrics:");
            for (int i = 0; i < info.getPerformanceMetrics().size(); i++) {
                addBulletPoint(document, "  • Metric " + (i+1) + ": " + info.getPerformanceMetrics().get(i));
            }
        }

        // Analysis Section
        addPointSection(document, "SECTION 8: ANALYSIS", new String[]{
                "Discussion: " + info.getDiscussion(),
                "Limitations: " + info.getLimitations(),
                "Future Work: " + info.getFutureWork(),
                "Practical Implications:",
                "Theoretical Implications:"
        });

        for (String implication : info.getPracticalImplications()) {
            addBulletPoint(document, "  • " + implication);
        }
        for (String implication : info.getTheoreticalImplications()) {
            addBulletPoint(document, "  • " + implication);
        }

        // Comparison Metrics Section
        addSectionHeader(document, "SECTION 9: COMPARISON METRICS");
        addComparisonMetricsTable(document, analysis, info);

        // Strengths and Weaknesses
        addPointSection(document, "SECTION 10: STRENGTHS", analysis.getStrengths().toArray(new String[0]));
        addPointSection(document, "SECTION 11: WEAKNESSES", analysis.getWeaknesses().toArray(new String[0]));

        // Quality Assessment
        addSectionHeader(document, "SECTION 12: QUALITY ASSESSMENT");
        addQualityAssessment(document, analysis, info);

        // Executive Summary
        addAbstractSection(document, "SECTION 13: EXECUTIVE SUMMARY", analysis.getSummary());

        // Recommendation
        addSectionHeader(document, "FINAL RECOMMENDATION");
        XWPFParagraph recPara = document.createParagraph();
        XWPFRun recRun = recPara.createRun();
        recRun.setBold(true);
        recRun.setFontSize(14);
        recRun.setText(analysis.getRecommendation());
        recRun.setColor("0066CC");

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

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Font subSectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font bulletFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        // Title
        Paragraph title = new Paragraph("ACADEMIC PAPER ANALYSIS REPORT", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Basic Information
        addPDFSection(document, "SECTION 1: BASIC INFORMATION", sectionFont);
        addPDFPoint(document, "Paper Title:", info.getPaperTitle(), normalFont, bulletFont);
        addPDFPoint(document, "Authors:", info.getAuthors(), normalFont, bulletFont);
        addPDFPoint(document, "Publication Year:", info.getPublicationYear(), normalFont, bulletFont);
        addPDFPoint(document, "Publication Venue:", info.getPublicationVenue(), normalFont, bulletFont);
        addPDFPoint(document, "DOI:", info.getDoi(), normalFont, bulletFont);
        addPDFPoint(document, "Keywords:", info.getKeywords(), normalFont, bulletFont);
        document.add(new Paragraph(" "));

        // Abstract
        addPDFSection(document, "SECTION 2: ABSTRACT", sectionFont);
        Paragraph abstractPara = new Paragraph(info.getAbstractText(), normalFont);
        abstractPara.setIndentationLeft(20);
        document.add(abstractPara);
        document.add(new Paragraph(" "));

        // Research Components
        addPDFSection(document, "SECTION 3: RESEARCH COMPONENTS", sectionFont);
        addPDFPoint(document, "Research Domain:", info.getResearchDomain(), normalFont, bulletFont);
        addPDFPoint(document, "Research Problem:", info.getResearchProblem(), normalFont, bulletFont);
        addPDFPoint(document, "Research Gap:", info.getResearchGap(), normalFont, bulletFont);
        addPDFPoint(document, "Research Questions:", "", normalFont, bulletFont);
        for (String question : info.getResearchQuestions()) {
            addPDFBullet(document, question, bulletFont);
        }
        addPDFPoint(document, "Hypothesis:", info.getResearchHypothesis(), normalFont, bulletFont);
        addPDFPoint(document, "Objectives:", info.getResearchObjectives(), normalFont, bulletFont);
        addPDFPoint(document, "Contributions:", info.getContributions(), normalFont, bulletFont);
        document.add(new Paragraph(" "));

        // Models and Frameworks
        addPDFSection(document, "SECTION 4: MODELS, FRAMEWORKS & ALGORITHMS", sectionFont);
        List<String> models = extractModels(info);
        List<String> frameworks = extractFrameworks(info);
        List<String> algorithms = extractAlgorithms(info);

        if (!models.isEmpty()) {
            addPDFSubSection(document, "Models Used:", subSectionFont);
            for (String model : models) {
                addPDFBullet(document, model, bulletFont);
            }
        }
        if (!frameworks.isEmpty()) {
            addPDFSubSection(document, "Frameworks Used:", subSectionFont);
            for (String framework : frameworks) {
                addPDFBullet(document, framework, bulletFont);
            }
        }
        if (!algorithms.isEmpty()) {
            addPDFSubSection(document, "Algorithms Used:", subSectionFont);
            for (String algorithm : algorithms) {
                addPDFBullet(document, algorithm, bulletFont);
            }
        }
        document.add(new Paragraph(" "));

        // Tables and Figures
        addPDFSection(document, "SECTION 5: TABLES, FIGURES & EQUATIONS", sectionFont);
        addPDFPoint(document, "Number of Tables:", String.valueOf(info.getTableCount()), normalFont, bulletFont);
        addPDFPoint(document, "Number of Figures:", String.valueOf(info.getFigureCount()), normalFont, bulletFont);
        addPDFPoint(document, "Number of Equations:", String.valueOf(info.getEquationCount()), normalFont, bulletFont);
        document.add(new Paragraph(" "));

        // Methodology
        addPDFSection(document, "SECTION 6: METHODOLOGY", sectionFont);
        addPDFPoint(document, "Methodology Type:", info.getMethodology(), normalFont, bulletFont);
        addPDFPoint(document, "Data Collection:", info.getDataCollectionMethods(), normalFont, bulletFont);
        addPDFPoint(document, "Sample Size:", info.getSampleSize(), normalFont, bulletFont);
        addPDFPoint(document, "Data Analysis:", info.getDataAnalysisMethods(), normalFont, bulletFont);
        addPDFPoint(document, "Tools & Technologies:", info.getToolsAndTechnologies(), normalFont, bulletFont);
        addPDFPoint(document, "Evaluation Metrics:", info.getEvaluationMetrics(), normalFont, bulletFont);
        document.add(new Paragraph(" "));

        // Results
        addPDFSection(document, "SECTION 7: RESULTS & FINDINGS", sectionFont);
        addPDFSubSection(document, "Key Findings:", subSectionFont);
        for (String finding : info.getKeyFindings()) {
            addPDFBullet(document, finding, bulletFont);
        }
        addPDFSubSection(document, "Results Summary:", subSectionFont);
        for (String result : info.getResults()) {
            addPDFBullet(document, result, bulletFont);
        }
        document.add(new Paragraph(" "));

        // Strengths and Weaknesses
        addPDFSection(document, "SECTION 8: STRENGTHS", sectionFont);
        for (String strength : analysis.getStrengths()) {
            addPDFBullet(document, strength, bulletFont);
        }
        document.add(new Paragraph(" "));

        addPDFSection(document, "SECTION 9: WEAKNESSES", sectionFont);
        for (String weakness : analysis.getWeaknesses()) {
            addPDFBullet(document, weakness, bulletFont);
        }
        document.add(new Paragraph(" "));

        // Quality Assessment
        addPDFSection(document, "SECTION 10: QUALITY ASSESSMENT", sectionFont);
        addPDFPoint(document, "Quality Score:", analysis.getQualityScore() + "/100", normalFont, bulletFont);
        addPDFPoint(document, "Writing Quality:", analysis.getWritingQualityComments(), normalFont, bulletFont);
        addPDFPoint(document, "Novelty Assessment:", analysis.getNoveltyAssessment(), normalFont, bulletFont);
        addPDFPoint(document, "Impact Assessment:", analysis.getImpactAssessment(), normalFont, bulletFont);
        addPDFPoint(document, "Plagiarism Risk:", analysis.getPlagiarismRiskIndicator(), normalFont, bulletFont);
        document.add(new Paragraph(" "));

        // Recommendation
        addPDFSection(document, "FINAL RECOMMENDATION", sectionFont);
        Paragraph recPara = new Paragraph(analysis.getRecommendation(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.RED));
        recPara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(recPara);

        document.close();
    }

    private void addComparisonTable(XWPFDocument document, ExtractedInfo info) {
        XWPFTable table = document.createTable(4, 2);
        table.setWidth("100%");

        // Header row
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("Metric");
        headerRow.getCell(1).setText("Value");

        // Data rows
        table.getRow(1).getCell(0).setText("Number of Tables");
        table.getRow(1).getCell(1).setText(String.valueOf(info.getTableCount()));

        table.getRow(2).getCell(0).setText("Number of Figures");
        table.getRow(2).getCell(1).setText(String.valueOf(info.getFigureCount()));

        table.getRow(3).getCell(0).setText("Number of Equations");
        table.getRow(3).getCell(1).setText(String.valueOf(info.getEquationCount()));

        addEmptyLine(document);
    }

    private void addComparisonMetricsTable(XWPFDocument document, PaperAnalysis analysis, ExtractedInfo info) {
        XWPFTable table = document.createTable(6, 2);
        table.setWidth("100%");

        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("Metric");
        headerRow.getCell(1).setText("Value");

        table.getRow(1).getCell(0).setText("Quality Score");
        table.getRow(1).getCell(1).setText(analysis.getQualityScore() + "/100");

        table.getRow(2).getCell(0).setText("Readability Score");
        table.getRow(2).getCell(1).setText(String.format("%.2f", info.getReadabilityScore()));

        table.getRow(3).getCell(0).setText("Language Complexity");
        table.getRow(3).getCell(1).setText(info.getLanguageComplexity());

        table.getRow(4).getCell(0).setText("Total References");
        table.getRow(4).getCell(1).setText(String.valueOf(info.getTotalReferences()));

        table.getRow(5).getCell(0).setText("Page Count");
        table.getRow(5).getCell(1).setText(String.valueOf(info.getPageCount()));

        addEmptyLine(document);
    }

    private void addQualityAssessment(XWPFDocument document, PaperAnalysis analysis, ExtractedInfo info) {
        XWPFTable table = document.createTable(5, 2);
        table.setWidth("100%");

        table.getRow(0).getCell(0).setText("Writing Quality");
        table.getRow(0).getCell(1).setText(analysis.getWritingQualityComments());

        table.getRow(1).getCell(0).setText("Novelty");
        table.getRow(1).getCell(1).setText(analysis.getNoveltyAssessment());

        table.getRow(2).getCell(0).setText("Impact");
        table.getRow(2).getCell(1).setText(analysis.getImpactAssessment());

        table.getRow(3).getCell(0).setText("Plagiarism Risk");
        table.getRow(3).getCell(1).setText(analysis.getPlagiarismRiskIndicator());

        table.getRow(4).getCell(0).setText("Overall Score");
        table.getRow(4).getCell(1).setText(analysis.getQualityScore() + "/100");

        addEmptyLine(document);
    }

    private void addPointSection(XWPFDocument document, String title, String[] points) {
        addSectionHeader(document, title);
        for (String point : points) {
            if (point != null && !point.isEmpty() && !point.equals("null")) {
                XWPFParagraph para = document.createParagraph();
                XWPFRun run = para.createRun();
                run.setText("• " + point);
                run.setFontSize(12);
            }
        }
        addEmptyLine(document);
    }

    private void addAbstractSection(XWPFDocument document, String title, String abstractText) {
        addSectionHeader(document, title);
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setText(abstractText);
        run.setFontSize(12);
        run.setItalic(true);
        addEmptyLine(document);
    }

    private void addSectionHeader(XWPFDocument document, String title) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setBold(true);
        run.setFontSize(16);
        run.setText(title);
        run.addBreak();
    }

    private void addBulletPoint(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontSize(12);
    }

    private void addEmptyLine(XWPFDocument document) {
        XWPFParagraph para = document.createParagraph();
        para.createRun().addBreak();
    }

    private void addPDFSection(Document document, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    private void addPDFSubSection(Document document, String title, Font font) throws DocumentException {
        Paragraph subSection = new Paragraph(title, font);
        subSection.setSpacingBefore(10);
        subSection.setSpacingAfter(5);
        document.add(subSection);
    }

    private void addPDFPoint(Document document, String label, String value, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph point = new Paragraph();
        point.add(new Chunk("• " + label + " ", labelFont));
        point.add(new Chunk(value, valueFont));
        point.setIndentationLeft(20);
        document.add(point);
    }

    private void addPDFBullet(Document document, String text, Font font) throws DocumentException {
        Paragraph bullet = new Paragraph("  • " + text, font);
        bullet.setIndentationLeft(30);
        document.add(bullet);
    }

    private List<String> extractModels(ExtractedInfo info) {
        List<String> models = new java.util.ArrayList<>();
        String text = (info.getToolsAndTechnologies() + " " + info.getMethodology()).toLowerCase();
        String[] commonModels = {"CNN", "RNN", "LSTM", "BERT", "GPT", "Transformer", "ResNet", "VGG",
                "YOLO", "SVM", "Random Forest", "Decision Tree", "KNN", "K-Means",
                "PCA", "t-SNE", "Autoencoder", "GAN", "DNN", "MLP"};
        for (String model : commonModels) {
            if (text.contains(model.toLowerCase())) {
                models.add(model);
            }
        }
        return models;
    }

    private List<String> extractFrameworks(ExtractedInfo info) {
        List<String> frameworks = new java.util.ArrayList<>();
        String text = (info.getToolsAndTechnologies() + " " + info.getMethodology()).toLowerCase();
        String[] commonFrameworks = {"TensorFlow", "PyTorch", "Keras", "Scikit-learn", "Pandas", "NumPy",
                "Spring", "Django", "Flask", "React", "Angular", "Vue", "Node.js",
                "Hadoop", "Spark", "Kafka", "Docker", "Kubernetes"};
        for (String framework : commonFrameworks) {
            if (text.contains(framework.toLowerCase())) {
                frameworks.add(framework);
            }
        }
        return frameworks;
    }

    private List<String> extractAlgorithms(ExtractedInfo info) {
        List<String> algorithms = new java.util.ArrayList<>();
        String text = (info.getToolsAndTechnologies() + " " + info.getMethodology() + " " +
                String.join(" ", info.getKeyFindings())).toLowerCase();
        String[] commonAlgorithms = {"Gradient Descent", "Backpropagation", "Q-Learning", "Genetic Algorithm",
                "A*", "Dijkstra", "BFS", "DFS", "Dynamic Programming", "Greedy",
                "KMP", "Rabin-Karp", "Quick Sort", "Merge Sort", "Binary Search"};
        for (String algorithm : commonAlgorithms) {
            if (text.contains(algorithm.toLowerCase())) {
                algorithms.add(algorithm);
            }
        }
        return algorithms;
    }

    private String formatMetrics(List<Double> metrics) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < metrics.size(); i++) {
            sb.append("Metric ").append(i+1).append(": ").append(metrics.get(i));
            if (i < metrics.size() - 1) sb.append("; ");
        }
        return sb.toString();
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