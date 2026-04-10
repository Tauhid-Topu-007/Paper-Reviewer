package org.example.paperreview.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.paperreview.model.PaperAnalysis;
import org.example.paperreview.model.ExtractedInfo;
import org.example.paperreview.service.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private WebView pdfPreview;
    @FXML private ProgressBar progressIndicator;
    @FXML private Label statusLabel;

    // Extracted info labels
    @FXML private Label paperTitleLabel;
    @FXML private Label authorsLabel;
    @FXML private Label domainLabel;
    @FXML private Label problemLabel;
    @FXML private Label gapLabel;
    @FXML private Label questionsLabel;
    @FXML private Label hypothesisLabel;
    @FXML private Label methodologyLabel;
    @FXML private Label dataCollectionLabel;
    @FXML private Label findingsLabel;
    @FXML private Label citationsLabel;
    @FXML private Label publicationLabel;

    // New enhanced fields
    @FXML private Label abstractLabel;
    @FXML private Label keywordsLabel;
    @FXML private Label venueLabel;
    @FXML private Label doiLabel;
    @FXML private Label yearLabel;
    @FXML private Label sampleSizeLabel;
    @FXML private Label toolsLabel;
    @FXML private Label metricsLabel;
    @FXML private Label limitationsLabel;
    @FXML private Label futureWorkLabel;
    @FXML private Label figuresLabel;
    @FXML private Label tablesLabel;
    @FXML private Label referencesLabel;
    @FXML private Label readabilityLabel;

    // Review assistant components
    @FXML private TextArea summaryArea;
    @FXML private ListView<String> strengthsList;
    @FXML private ListView<String> weaknessesList;
    @FXML private ListView<String> futureScopeList;
    @FXML private ProgressBar qualityScoreBar;
    @FXML private Label qualityScoreLabel;
    @FXML private Label writingQualityLabel;
    @FXML private Label plagiarismLabel;

    // Window control variables
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMaximized = false;
    private double previousWidth = 1200;
    private double previousHeight = 800;
    private double previousX = 0;
    private double previousY = 0;

    private PDFParserService pdfParser;
    private NLPAnalysisService nlpService;
    private SectionClassifierService sectionClassifier;
    private ExportService exportService;
    private PaperAnalysis currentAnalysis;
    private File currentPDFFile;
    private boolean pdfViewerLoaded = false;

    @FXML
    public void initialize() {
        pdfParser = new PDFParserService();
        nlpService = new NLPAnalysisService();
        sectionClassifier = new SectionClassifierService();
        exportService = new ExportService();
        currentAnalysis = new PaperAnalysis();

        // Set up list views
        strengthsList.setPlaceholder(new Label("No strengths identified yet"));
        weaknessesList.setPlaceholder(new Label("No weaknesses identified yet"));
        futureScopeList.setPlaceholder(new Label("No future scope identified yet"));

        // Setup window dragging after scene is available
        Platform.runLater(() -> setupWindowDragging());
    }

    private void setupWindowDragging() {
        Scene scene = pdfPreview.getScene();
        if (scene != null) {
            scene.setOnMousePressed(event -> {
                Stage stage = (Stage) pdfPreview.getScene().getWindow();
                if (!isMaximized && event.getY() <= 40) {
                    xOffset = stage.getX() - event.getScreenX();
                    yOffset = stage.getY() - event.getScreenY();
                }
            });

            scene.setOnMouseDragged(event -> {
                Stage stage = (Stage) pdfPreview.getScene().getWindow();
                if (!isMaximized && event.getY() <= 40) {
                    stage.setX(event.getScreenX() + xOffset);
                    stage.setY(event.getScreenY() + yOffset);
                }
            });
        }
    }

    // Window Control Methods
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) pdfPreview.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) pdfPreview.getScene().getWindow();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        if (!isMaximized) {
            previousWidth = stage.getWidth();
            previousHeight = stage.getHeight();
            previousX = stage.getX();
            previousY = stage.getY();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            isMaximized = true;

            Button maximizeButton = (Button) pdfPreview.getScene().lookup("#maximizeButton");
            if (maximizeButton != null) {
                maximizeButton.setText("❐");
            }
        } else {
            stage.setX(previousX);
            stage.setY(previousY);
            stage.setWidth(previousWidth);
            stage.setHeight(previousHeight);
            isMaximized = false;

            Button maximizeButton = (Button) pdfPreview.getScene().lookup("#maximizeButton");
            if (maximizeButton != null) {
                maximizeButton.setText("□");
            }
        }
    }

    @FXML
    private void handleClose() {
        Platform.exit();
    }

    @FXML
    private void handleMinimizeEnter() {
        Button minimizeButton = (Button) pdfPreview.getScene().lookup("#minimizeButton");
        if (minimizeButton != null) {
            minimizeButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleMinimizeExit() {
        Button minimizeButton = (Button) pdfPreview.getScene().lookup("#minimizeButton");
        if (minimizeButton != null) {
            minimizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleMaximizeEnter() {
        Button maximizeButton = (Button) pdfPreview.getScene().lookup("#maximizeButton");
        if (maximizeButton != null) {
            maximizeButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleMaximizeExit() {
        Button maximizeButton = (Button) pdfPreview.getScene().lookup("#maximizeButton");
        if (maximizeButton != null) {
            maximizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleCloseEnter() {
        Button closeButton = (Button) pdfPreview.getScene().lookup("#closeButton");
        if (closeButton != null) {
            closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleCloseExit() {
        Button closeButton = (Button) pdfPreview.getScene().lookup("#closeButton");
        if (closeButton != null) {
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleOpenPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        Stage stage = (Stage) pdfPreview.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            currentPDFFile = file;
            statusLabel.setText("Loading PDF: " + file.getName());

            try {
                pdfParser.loadPDF(file);
                loadPDFPreview(file);
                statusLabel.setText("PDF loaded successfully");
            } catch (IOException e) {
                showError("Error loading PDF", e.getMessage());
                statusLabel.setText("Error loading PDF");
            }
        }
    }

    private void loadPDFPreview(File file) {
        try {
            // Convert PDF file to data URL for PDF.js
            String pdfDataUrl = "file:///" + file.getAbsolutePath().replace("\\", "/");

            // Load the PDF.js viewer HTML
            java.net.URL viewerUrl = getClass().getResource("/org/example/paperreview/pdf-viewer.html");
            if (viewerUrl != null) {
                pdfPreview.getEngine().load(viewerUrl.toExternalForm());
                pdfViewerLoaded = false;

                // Wait for page to load then load the PDF
                pdfPreview.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        pdfViewerLoaded = true;
                        String script = String.format(
                                "if (typeof loadPDFFromJava !== 'undefined') {" +
                                        "  loadPDFFromJava('%s');" +
                                        "}",
                                pdfDataUrl
                        );
                        Platform.runLater(() -> pdfPreview.getEngine().executeScript(script));
                    }
                });
            } else {
                // Fallback to simple HTML view
                pdfPreview.getEngine().loadContent(
                        "<html><body style='font-family: Arial; padding: 20px;'>" +
                                "<h2>PDF Loaded Successfully</h2>" +
                                "<p>File: " + file.getName() + "</p>" +
                                "<p>Pages: " + pdfParser.getNumberOfPages() + "</p>" +
                                "<p style='color: orange;'>Note: Advanced highlighting requires internet connection for PDF.js library</p>" +
                                "<p>Click 'Highlight Extracted Info' to see extracted information highlighted</p>" +
                                "</body></html>"
                );
                pdfViewerLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            pdfPreview.getEngine().loadContent(
                    "<html><body style='font-family: Arial; padding: 20px;'>" +
                            "<h2>PDF Viewer Error</h2>" +
                            "<p>Could not load PDF viewer: " + e.getMessage() + "</p>" +
                            "<p>File: " + file.getName() + "</p>" +
                            "<p>Pages: " + pdfParser.getNumberOfPages() + "</p>" +
                            "</body></html>"
            );
            pdfViewerLoaded = true;
        }
    }

    @FXML
    private void handleHighlightExtractedInfo() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        if (currentPDFFile == null) {
            showError("No PDF", "Please load a PDF file first");
            return;
        }

        if (!pdfViewerLoaded) {
            showError("PDF Viewer Not Ready", "Please wait for the PDF viewer to load completely");
            return;
        }

        statusLabel.setText("Preparing highlights for extracted information...");

        // Create highlights data from extracted information
        ExtractedInfo info = currentAnalysis.getExtractedInfo();
        List<HighlightData> highlights = new ArrayList<>();

        // Highlight research problem
        if (info.getResearchProblem() != null && !info.getResearchProblem().equals("Not clearly stated")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getResearchProblem()), 1, "#FFEB3B"));
        }

        // Highlight research gap
        if (info.getResearchGap() != null && !info.getResearchGap().equals("Not explicitly stated")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getResearchGap()), 1, "#FFC107"));
        }

        // Highlight methodology
        if (info.getMethodology() != null && !info.getMethodology().equals("Not clearly described")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getMethodology()), 2, "#4CAF50"));
        }

        // Highlight key findings
        if (info.getKeyFindings() != null && !info.getKeyFindings().isEmpty()) {
            for (String finding : info.getKeyFindings()) {
                if (finding != null && !finding.equals("Key findings not clearly presented")) {
                    highlights.add(new HighlightData(extractKeyPhrases(finding), 3, "#2196F3"));
                }
            }
        }

        // Highlight limitations
        if (info.getLimitations() != null && !info.getLimitations().equals("Not explicitly stated")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getLimitations()), 4, "#F44336"));
        }

        // Highlight future work
        if (info.getFutureWork() != null && !info.getFutureWork().equals("Not discussed")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getFutureWork()), 5, "#9C27B0"));
        }

        // Highlight abstract
        if (info.getAbstractText() != null && !info.getAbstractText().equals("Abstract not available")) {
            highlights.add(new HighlightData(extractKeyPhrases(info.getAbstractText()), 1, "#E1BEE7"));
        }

        // Convert to JSON and send to PDF viewer
        String highlightsJson = convertHighlightsToJson(highlights);

        // Execute JavaScript in WebView to highlight
        String script = String.format(
                "if (typeof highlightFromJava !== 'undefined') {" +
                        "  highlightFromJava('%s');" +
                        "} else {" +
                        "  console.log('Highlight function not available');" +
                        "  alert('PDF viewer not fully loaded. Please wait and try again.');" +
                        "}",
                escapeJson(highlightsJson)
        );

        pdfPreview.getEngine().executeScript(script);
        statusLabel.setText("Highlights applied to PDF!");
        showInfo("Highlight Complete", "Extracted information has been highlighted in the PDF viewer.\n\nColor Legend:\n• Yellow: Research Problem\n• Orange: Research Gap\n• Green: Methodology\n• Blue: Key Findings\n• Red: Limitations\n• Purple: Future Work\n• Lavender: Abstract");
    }

    // Helper class for highlight data
    private static class HighlightData {
        List<String> texts;
        int page;
        String color;

        HighlightData(List<String> texts, int page, String color) {
            this.texts = texts;
            this.page = page;
            this.color = color;
        }
    }

    // Extract key phrases from text (split into meaningful chunks)
    private List<String> extractKeyPhrases(String text) {
        List<String> phrases = new ArrayList<>();
        if (text == null || text.isEmpty()) return phrases;

        // Split by sentences
        String[] sentences = text.split("[.!?]+");
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 15 && trimmed.length() < 200) {
                phrases.add(trimmed);
            }
        }

        // If no sentences found, add the whole text
        if (phrases.isEmpty() && text.length() > 15) {
            phrases.add(text.substring(0, Math.min(200, text.length())));
        }

        return phrases;
    }

    // Convert highlights to JSON
    private String convertHighlightsToJson(List<HighlightData> highlights) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < highlights.size(); i++) {
            HighlightData h = highlights.get(i);
            if (i > 0) json.append(",");
            json.append("{");
            json.append("\"texts\":").append(convertTextsToJson(h.texts));
            json.append(",\"page\":").append(h.page);
            json.append(",\"color\":\"").append(h.color).append("\"");
            json.append("}");
        }
        json.append("]");
        return json.toString();
    }

    // Convert texts list to JSON array
    private String convertTextsToJson(List<String> texts) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < texts.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(texts.get(i))).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    // Escape JSON string
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @FXML
    private void handleAnalyze() {
        if (currentPDFFile == null) {
            showError("No PDF Loaded", "Please upload a PDF file first");
            return;
        }

        Task<PaperAnalysis> analysisTask = new Task<>() {
            @Override
            protected PaperAnalysis call() throws Exception {
                updateMessage("Extracting text from PDF...");
                String fullText = pdfParser.getFullText();
                List<String> pages = pdfParser.getPages();

                updateMessage("Analyzing paper with NLP...");
                ExtractedInfo info = nlpService.analyzePaper(fullText, pages);

                updateMessage("Generating comprehensive review...");
                PaperAnalysis analysis = new PaperAnalysis();
                analysis.setExtractedInfo(info);

                String summary = nlpService.generateSummary(fullText, info);
                analysis.setSummary(summary);

                List<String> strengths = nlpService.identifyStrengths(fullText, info);
                analysis.setStrengths(strengths);

                List<String> weaknesses = nlpService.identifyWeaknesses(fullText, info);
                analysis.setWeaknesses(weaknesses);

                List<String> futureScope = nlpService.identifyFutureScope(fullText, info);
                analysis.setFutureResearchScope(futureScope);

                double qualityScore = nlpService.calculateQualityScore(info, strengths, weaknesses);
                analysis.setQualityScore(qualityScore);

                String writingQuality = nlpService.assessWritingQuality(fullText);
                analysis.setWritingQualityComments(writingQuality);

                String plagiarismRisk = nlpService.assessPlagiarismRisk(fullText);
                analysis.setPlagiarismRiskIndicator(plagiarismRisk);

                String novelty = nlpService.assessNovelty(fullText, info);
                analysis.setNoveltyAssessment(novelty);

                String impact = nlpService.assessImpact(fullText, info);
                analysis.setImpactAssessment(impact);

                String recommendation = generateRecommendation(qualityScore, strengths, weaknesses);
                analysis.setRecommendation(recommendation);

                List<String> improvements = generateImprovements(weaknesses, info);
                analysis.setSuggestedImprovements(improvements);

                return analysis;
            }
        };

        analysisTask.setOnRunning(e -> {
            progressIndicator.setVisible(true);
            progressIndicator.progressProperty().bind(analysisTask.progressProperty());
            statusLabel.textProperty().bind(analysisTask.messageProperty());
        });

        analysisTask.setOnSucceeded(e -> {
            currentAnalysis = analysisTask.getValue();
            displayResults(currentAnalysis);
            progressIndicator.setVisible(false);
            statusLabel.textProperty().unbind();
            statusLabel.setText("Analysis complete!");
        });

        analysisTask.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            statusLabel.textProperty().unbind();
            statusLabel.setText("Analysis failed");
            Throwable exception = analysisTask.getException();
            if (exception != null) {
                showError("Analysis Failed", exception.getMessage());
            }
        });

        new Thread(analysisTask).start();
    }

    private String generateRecommendation(double score, List<String> strengths, List<String> weaknesses) {
        if (score >= 80) {
            return "Strongly Accept - High quality paper with minor improvements needed";
        } else if (score >= 70) {
            return "Accept - Good quality paper with some revisions required";
        } else if (score >= 60) {
            return "Minor Revisions - Paper has potential but needs significant improvements";
        } else if (score >= 50) {
            return "Major Revisions - Paper requires substantial changes";
        } else {
            return "Reject - Paper does not meet quality standards";
        }
    }

    private List<String> generateImprovements(List<String> weaknesses, ExtractedInfo info) {
        List<String> improvements = new ArrayList<>();

        for (String weakness : weaknesses) {
            if (weakness.contains("sample size")) {
                improvements.add("Consider increasing sample size or adding validation study");
            } else if (weakness.contains("limitation")) {
                improvements.add("Address the identified limitations in future work");
            } else if (weakness.contains("evaluation")) {
                improvements.add("Include more comprehensive evaluation metrics");
            }
        }

        if (info.getFigureCount() < 3) {
            improvements.add("Add more figures to illustrate key concepts");
        }

        if (info.getTableCount() < 2) {
            improvements.add("Include tables to summarize key results");
        }

        if (improvements.isEmpty()) {
            improvements.add("Continue to validate findings with real-world applications");
        }

        return improvements;
    }

    private void displayResults(PaperAnalysis analysis) {
        ExtractedInfo info = analysis.getExtractedInfo();

        Platform.runLater(() -> {
            // Basic Information
            paperTitleLabel.setText(info.getPaperTitle() != null ? info.getPaperTitle() : "N/A");
            authorsLabel.setText(info.getAuthors() != null ? info.getAuthors() : "N/A");
            publicationLabel.setText(info.getPublicationCategory() != null ? info.getPublicationCategory() : "N/A");

            // Enhanced Information
            if (abstractLabel != null) abstractLabel.setText(info.getAbstractText() != null ? info.getAbstractText() : "N/A");
            if (keywordsLabel != null) keywordsLabel.setText(info.getKeywords() != null ? info.getKeywords() : "N/A");
            if (venueLabel != null) venueLabel.setText(info.getPublicationVenue() != null ? info.getPublicationVenue() : "N/A");
            if (doiLabel != null) doiLabel.setText(info.getDoi() != null ? info.getDoi() : "N/A");
            if (yearLabel != null) yearLabel.setText(info.getPublicationYear() != null ? info.getPublicationYear() : "N/A");
            if (sampleSizeLabel != null) sampleSizeLabel.setText(info.getSampleSize() != null ? info.getSampleSize() : "N/A");
            if (toolsLabel != null) toolsLabel.setText(info.getToolsAndTechnologies() != null ? info.getToolsAndTechnologies() : "N/A");
            if (metricsLabel != null) metricsLabel.setText(info.getEvaluationMetrics() != null ? info.getEvaluationMetrics() : "N/A");
            if (limitationsLabel != null) limitationsLabel.setText(info.getLimitations() != null ? info.getLimitations() : "N/A");
            if (futureWorkLabel != null) futureWorkLabel.setText(info.getFutureWork() != null ? info.getFutureWork() : "N/A");
            if (figuresLabel != null) figuresLabel.setText(String.valueOf(info.getFigureCount()));
            if (tablesLabel != null) tablesLabel.setText(String.valueOf(info.getTableCount()));
            if (referencesLabel != null) referencesLabel.setText(String.valueOf(info.getTotalReferences()));
            if (readabilityLabel != null) readabilityLabel.setText(String.format("%.2f - %s", info.getReadabilityScore(), info.getLanguageComplexity()));

            // Research Components
            domainLabel.setText(info.getResearchDomain() != null ? info.getResearchDomain() : "N/A");
            problemLabel.setText(info.getResearchProblem() != null ? info.getResearchProblem() : "N/A");
            gapLabel.setText(info.getResearchGap() != null ? info.getResearchGap() : "N/A");
            questionsLabel.setText(info.getResearchQuestions() != null && !info.getResearchQuestions().isEmpty() ?
                    String.join("\n", info.getResearchQuestions()) : "N/A");
            hypothesisLabel.setText(info.getResearchHypothesis() != null ? info.getResearchHypothesis() : "N/A");
            methodologyLabel.setText(info.getMethodology() != null ? info.getMethodology() : "N/A");
            dataCollectionLabel.setText(info.getDataCollectionMethods() != null ? info.getDataCollectionMethods() : "N/A");
            findingsLabel.setText(info.getKeyFindings() != null && !info.getKeyFindings().isEmpty() ?
                    String.join("\n", info.getKeyFindings()) : "N/A");
            citationsLabel.setText(info.getCitations() != null ? info.getCitations().size() + " citations found" : "0 citations found");

            // Review assistant
            summaryArea.setText(analysis.getSummary() != null ? analysis.getSummary() : "No summary available");

            strengthsList.getItems().clear();
            if (analysis.getStrengths() != null && !analysis.getStrengths().isEmpty()) {
                strengthsList.getItems().addAll(analysis.getStrengths());
            } else {
                strengthsList.setPlaceholder(new Label("No strengths identified"));
            }

            weaknessesList.getItems().clear();
            if (analysis.getWeaknesses() != null && !analysis.getWeaknesses().isEmpty()) {
                weaknessesList.getItems().addAll(analysis.getWeaknesses());
            } else {
                weaknessesList.setPlaceholder(new Label("No weaknesses identified"));
            }

            futureScopeList.getItems().clear();
            if (analysis.getFutureResearchScope() != null && !analysis.getFutureResearchScope().isEmpty()) {
                futureScopeList.getItems().addAll(analysis.getFutureResearchScope());
            } else {
                futureScopeList.setPlaceholder(new Label("No future scope identified"));
            }

            qualityScoreBar.setProgress(analysis.getQualityScore() / 100.0);
            qualityScoreLabel.setText(String.format("Quality Score: %.1f/100", analysis.getQualityScore()));
            writingQualityLabel.setText(analysis.getWritingQualityComments() != null ? analysis.getWritingQualityComments() : "N/A");
            plagiarismLabel.setText(analysis.getPlagiarismRiskIndicator() != null ? analysis.getPlagiarismRiskIndicator() : "N/A");
        });
    }

    @FXML
    private void handleShowSourceLines() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        pdfPreview.getEngine().executeScript(
                "document.body.style.backgroundColor = '#ffff00';" +
                        "alert('Source highlighting would show exact extraction locations from the PDF.');"
        );

        statusLabel.setText("Source highlighting feature ready");
    }

    @FXML
    private void handleExportCSV() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("paper_analysis_report.csv");

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToCSV(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to CSV: " + file.getName());
                showInfo("Export Successful", "Report exported to " + file.getName());
            } catch (IOException e) {
                showError("Export Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportDOCX() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to DOCX");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DOCX Files", "*.docx")
        );
        fileChooser.setInitialFileName("paper_analysis_report.docx");

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToDOCX(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to DOCX: " + file.getName());
                showInfo("Export Successful", "Report exported to " + file.getName());
            } catch (IOException e) {
                showError("Export Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("paper_analysis_report.pdf");

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToPDF(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to PDF: " + file.getName());
                showInfo("Export Successful", "Report exported to " + file.getName());
            } catch (Exception e) {
                showError("Export Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleExport() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", "CSV", "DOCX", "PDF");
        dialog.setTitle("Export Format");
        dialog.setHeaderText("Choose export format");
        dialog.setContentText("Format:");

        dialog.showAndWait().ifPresent(format -> {
            switch (format) {
                case "CSV": handleExportCSV(); break;
                case "DOCX": handleExportDOCX(); break;
                case "PDF": handleExportPDF(); break;
            }
        });
    }

    @FXML
    private void handleClear() {
        paperTitleLabel.setText("");
        authorsLabel.setText("");
        domainLabel.setText("");
        problemLabel.setText("");
        gapLabel.setText("");
        questionsLabel.setText("");
        hypothesisLabel.setText("");
        methodologyLabel.setText("");
        dataCollectionLabel.setText("");
        findingsLabel.setText("");
        citationsLabel.setText("");
        publicationLabel.setText("");

        if (abstractLabel != null) abstractLabel.setText("");
        if (keywordsLabel != null) keywordsLabel.setText("");
        if (venueLabel != null) venueLabel.setText("");
        if (doiLabel != null) doiLabel.setText("");
        if (yearLabel != null) yearLabel.setText("");
        if (sampleSizeLabel != null) sampleSizeLabel.setText("");
        if (toolsLabel != null) toolsLabel.setText("");
        if (metricsLabel != null) metricsLabel.setText("");
        if (limitationsLabel != null) limitationsLabel.setText("");
        if (futureWorkLabel != null) futureWorkLabel.setText("");
        if (figuresLabel != null) figuresLabel.setText("");
        if (tablesLabel != null) tablesLabel.setText("");
        if (referencesLabel != null) referencesLabel.setText("");
        if (readabilityLabel != null) readabilityLabel.setText("");

        summaryArea.clear();
        strengthsList.getItems().clear();
        weaknessesList.getItems().clear();
        futureScopeList.getItems().clear();
        qualityScoreBar.setProgress(0);
        qualityScoreLabel.setText("");
        writingQualityLabel.setText("");
        plagiarismLabel.setText("");

        currentAnalysis = null;
        statusLabel.setText("Cleared");
    }

    @FXML
    private void handleToggleDarkMode() {
        Scene scene = pdfPreview.getScene();
        String darkModeCSS = "/org/example/paperreview/dark-mode.css";

        if (scene.getStylesheets().isEmpty()) {
            try {
                if (getClass().getResource(darkModeCSS) != null) {
                    scene.getStylesheets().add(getClass().getResource(darkModeCSS).toExternalForm());
                    statusLabel.setText("Dark mode enabled");
                } else {
                    showError("CSS Not Found", "Dark mode CSS file not found");
                }
            } catch (Exception e) {
                showError("Error", "Could not load dark mode CSS");
            }
        } else {
            scene.getStylesheets().clear();
            statusLabel.setText("Light mode enabled");
        }
    }

    @FXML
    private void handleResetView() {
        pdfPreview.getEngine().reload();
        statusLabel.setText("View reset");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Academic Paper Review Assistant");
        alert.setContentText("Version 2.0\n\nA comprehensive tool for automatic analysis of academic papers.\n\nEnhanced Features:\n- Comprehensive information extraction\n- Advanced NLP-based analysis\n- Quality assessment with recommendations\n- Multiple export formats (CSV, DOCX, PDF)\n- PDF highlighting of extracted information\n- Paper metadata extraction\n- Readability analysis\n- Custom window controls\n- Dark/Light mode support");
        alert.showAndWait();
    }

    @FXML
    private void handleDocumentation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Documentation");
        alert.setHeaderText("How to Use");
        alert.setContentText("1. Click 'Upload PDF' to select a research paper\n" +
                "2. Click 'Analyze Paper' to extract comprehensive information\n" +
                "3. View extracted information in the tabs\n" +
                "4. Click 'Highlight Extracted Info' to see where information was found in the PDF\n" +
                "5. Check quality assessment and recommendations\n" +
                "6. Export results using the export button\n" +
                "7. Use the custom title bar buttons to control the window\n\n" +
                "Highlight Colors:\n" +
                "• Yellow: Research Problem\n" +
                "• Orange: Research Gap\n" +
                "• Green: Methodology\n" +
                "• Blue: Key Findings\n" +
                "• Red: Limitations\n" +
                "• Purple: Future Work\n" +
                "• Lavender: Abstract\n\n" +
                "The analysis extracts:\n" +
                "• Basic paper information (title, authors, abstract, keywords)\n" +
                "• Research components (problem, gap, questions, hypothesis)\n" +
                "• Methodology details (methods, tools, sample size, metrics)\n" +
                "• Results and findings\n" +
                "• Limitations and future work\n" +
                "• Quality assessment and recommendations\n" +
                "• Paper metadata (figures, tables, references, readability)");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}