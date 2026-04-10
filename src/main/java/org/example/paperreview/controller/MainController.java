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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private PDFHighlighterService pdfHighlighter;
    private PaperAnalysis currentAnalysis;
    private File currentPDFFile;
    private File highlightedPDFFile;
    private boolean pdfViewerLoaded = false;

    @FXML
    public void initialize() {
        pdfParser = new PDFParserService();
        nlpService = new NLPAnalysisService();
        sectionClassifier = new SectionClassifierService();
        exportService = new ExportService();
        pdfHighlighter = new PDFHighlighterService();
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
                statusLabel.setText("PDF loaded successfully. Pages: " + pdfParser.getNumberOfPages());
            } catch (IOException e) {
                showError("Error loading PDF", e.getMessage());
                statusLabel.setText("Error loading PDF");
            }
        }
    }

    private void loadPDFPreview(File file) {
        try {
            // Simple HTML viewer that works reliably
            String htmlContent = "<html><body style='font-family: Arial; padding: 20px; text-align: center;'>" +
                    "<h2 style='color: #2c3e50;'>📄 PDF Loaded Successfully</h2>" +
                    "<p><strong>File:</strong> " + file.getName() + "</p>" +
                    "<p><strong>Pages:</strong> " + pdfParser.getNumberOfPages() + "</p>" +
                    "<div style='background-color: #e8f5e9; padding: 15px; border-radius: 8px; margin-top: 20px;'>" +
                    "<p style='color: #2e7d32;'>✓ PDF is ready for analysis</p>" +
                    "<p>Click <strong>'Analyze Paper'</strong> to extract information</p>" +
                    "<p>Click <strong>'Highlight Extracted Info'</strong> to create a highlighted PDF</p>" +
                    "</div>" +
                    "<hr style='margin: 20px;'>" +
                    "<p style='color: #666;'>💡 <strong>Tip:</strong> For best viewing experience, use the <strong>'Open Highlighted PDF'</strong> button to open in your default PDF viewer.</p>" +
                    "</body></html>";

            pdfPreview.getEngine().loadContent(htmlContent);
            pdfViewerLoaded = true;
            statusLabel.setText("PDF loaded: " + file.getName());

        } catch (Exception e) {
            e.printStackTrace();
            pdfPreview.getEngine().loadContent(
                    "<html><body style='font-family: Arial; padding: 20px;'>" +
                            "<h2>PDF Loaded</h2>" +
                            "<p>File: " + file.getName() + "</p>" +
                            "<p>Pages: " + pdfParser.getNumberOfPages() + "</p>" +
                            "<p>Click 'Open Highlighted PDF' to view the PDF with highlights</p>" +
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

        statusLabel.setText("Creating highlighted PDF...");
        progressIndicator.setVisible(true);

        Task<File> highlightTask = new Task<>() {
            @Override
            protected File call() throws Exception {
                updateMessage("Preparing extracted information for highlighting...");

                ExtractedInfo info = currentAnalysis.getExtractedInfo();
                Map<String, String> extractedInfoMap = new LinkedHashMap<>();

                // Add ALL extracted information for highlighting
                addToHighlightMap(extractedInfoMap, "Paper Title", info.getPaperTitle());
                addToHighlightMap(extractedInfoMap, "Authors", info.getAuthors());
                addToHighlightMap(extractedInfoMap, "Abstract", info.getAbstractText());
                addToHighlightMap(extractedInfoMap, "Keywords", info.getKeywords());
                addToHighlightMap(extractedInfoMap, "Research Domain", info.getResearchDomain());
                addToHighlightMap(extractedInfoMap, "Research Problem", info.getResearchProblem());
                addToHighlightMap(extractedInfoMap, "Research Gap", info.getResearchGap());
                addToHighlightMap(extractedInfoMap, "Methodology", info.getMethodology());
                addToHighlightMap(extractedInfoMap, "Executive Summary", currentAnalysis.getSummary());

                // Add ALL research questions
                if (info.getResearchQuestions() != null) {
                    for (int i = 0; i < info.getResearchQuestions().size(); i++) {
                        String question = info.getResearchQuestions().get(i);
                        if (isValidForHighlight(question)) {
                            extractedInfoMap.put("Research Question " + (i + 1), question);
                        }
                    }
                }

                // Add ALL key findings
                if (info.getKeyFindings() != null) {
                    for (int i = 0; i < info.getKeyFindings().size(); i++) {
                        String finding = info.getKeyFindings().get(i);
                        if (isValidForHighlight(finding)) {
                            extractedInfoMap.put("Key Finding " + (i + 1), finding);
                        }
                    }
                }

                // Add hypothesis
                addToHighlightMap(extractedInfoMap, "Hypothesis", info.getResearchHypothesis());

                // Add data collection methods
                addToHighlightMap(extractedInfoMap, "Data Collection", info.getDataCollectionMethods());

                // Add tools and technologies
                addToHighlightMap(extractedInfoMap, "Tools Used", info.getToolsAndTechnologies());

                // Add evaluation metrics
                addToHighlightMap(extractedInfoMap, "Evaluation Metrics", info.getEvaluationMetrics());

                // Add limitations
                addToHighlightMap(extractedInfoMap, "Limitations", info.getLimitations());

                // Add future work
                addToHighlightMap(extractedInfoMap, "Future Work", info.getFutureWork());

                // Add practical implications
                if (info.getPracticalImplications() != null) {
                    for (int i = 0; i < info.getPracticalImplications().size(); i++) {
                        String implication = info.getPracticalImplications().get(i);
                        if (isValidForHighlight(implication)) {
                            extractedInfoMap.put("Practical Implication " + (i + 1), implication);
                        }
                    }
                }

                // Add theoretical implications
                if (info.getTheoreticalImplications() != null) {
                    for (int i = 0; i < info.getTheoreticalImplications().size(); i++) {
                        String implication = info.getTheoreticalImplications().get(i);
                        if (isValidForHighlight(implication)) {
                            extractedInfoMap.put("Theoretical Implication " + (i + 1), implication);
                        }
                    }
                }

                updateMessage("Searching " + extractedInfoMap.size() + " items in PDF...");

                System.out.println("\n========== STARTING HIGHLIGHT PROCESS ==========");
                System.out.println("Total items to search: " + extractedInfoMap.size());

                // Create highlighted PDF
                PDFHighlighterService highlighter = new PDFHighlighterService();
                File highlightedFile = highlighter.createHighlightedPDF(currentPDFFile, extractedInfoMap);

                System.out.println("========== HIGHLIGHT PROCESS COMPLETED ==========\n");

                return highlightedFile;
            }
        };

        highlightTask.setOnRunning(e -> {
            statusLabel.textProperty().bind(highlightTask.messageProperty());
        });

        highlightTask.setOnSucceeded(e -> {
            statusLabel.textProperty().unbind();
            highlightedPDFFile = highlightTask.getValue();
            statusLabel.setText("Highlighted PDF created successfully!");
            progressIndicator.setVisible(false);

            try {
                java.awt.Desktop.getDesktop().open(highlightedPDFFile);
                showInfo("Highlight Complete",
                        "✓ PDF has been highlighted successfully!\n\n" +
                                "All extracted information has been highlighted in YELLOW color.\n\n" +
                                "The highlighted PDF has been opened automatically.\n\n" +
                                "Check the console for detailed search results.");
            } catch (IOException ex) {
                showInfo("Highlight Complete",
                        "✓ PDF has been highlighted successfully!\n\nFile saved at:\n" +
                                highlightedPDFFile.getAbsolutePath());
            }
        });

        highlightTask.setOnFailed(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Highlight failed");
            progressIndicator.setVisible(false);
            Throwable exception = highlightTask.getException();
            showError("Highlight Failed", exception != null ? exception.getMessage() : "Unknown error");
            exception.printStackTrace();
        });

        new Thread(highlightTask).start();
    }

    private void addToHighlightMap(Map<String, String> map, String key, String value) {
        if (isValidForHighlight(value)) {
            String shortValue = value.length() > 500 ? value.substring(0, 500) : value;
            map.put(key, shortValue);
            System.out.println("Added to highlight map: " + key + " (" + shortValue.length() + " chars)");
        } else {
            System.out.println("Skipped (invalid): " + key);
        }
    }

    private boolean isValidForHighlight(String text) {
        if (text == null || text.isEmpty()) return false;
        String[] invalidValues = {"N/A", "Not found", "Not available", "Not clearly stated",
                "Not explicitly stated", "Not clearly described", "Not specified",
                "No summary available", "Abstract not available", "Keywords not found",
                "Authors not found", "Title not found", "", "null"};
        for (String invalid : invalidValues) {
            if (text.equals(invalid)) return false;
        }
        return text.length() > 15;
    }

    @FXML
    private void openHighlightedPDF() {
        if (highlightedPDFFile != null && highlightedPDFFile.exists()) {
            try {
                java.awt.Desktop.getDesktop().open(highlightedPDFFile);
                statusLabel.setText("Opened highlighted PDF");
            } catch (IOException e) {
                showError("Cannot Open PDF", "Could not open the PDF file: " + e.getMessage());
            }
        } else {
            showError("No Highlighted PDF", "Please click 'Highlight Extracted Info' first to create a highlighted PDF");
        }
    }

    @FXML
    private void saveHighlightedPDF() {
        if (highlightedPDFFile == null || !highlightedPDFFile.exists()) {
            showError("No Highlighted PDF", "Please click 'Highlight Extracted Info' first to create a highlighted PDF");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Highlighted PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("highlighted_paper.pdf");

        File saveFile = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (saveFile != null) {
            try {
                java.nio.file.Files.copy(highlightedPDFFile.toPath(), saveFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                showInfo("Save Successful", "Highlighted PDF saved to:\n" + saveFile.getAbsolutePath());
                statusLabel.setText("Highlighted PDF saved!");
            } catch (IOException e) {
                showError("Save Failed", "Could not save file: " + e.getMessage());
            }
        }
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
            paperTitleLabel.setText(info.getPaperTitle() != null ? info.getPaperTitle() : "N/A");
            authorsLabel.setText(info.getAuthors() != null ? info.getAuthors() : "N/A");
            publicationLabel.setText(info.getPublicationCategory() != null ? info.getPublicationCategory() : "N/A");

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

            summaryArea.setText(analysis.getSummary() != null ? analysis.getSummary() : "No summary available");

            strengthsList.getItems().clear();
            if (analysis.getStrengths() != null && !analysis.getStrengths().isEmpty()) {
                strengthsList.getItems().addAll(analysis.getStrengths());
            }

            weaknessesList.getItems().clear();
            if (analysis.getWeaknesses() != null && !analysis.getWeaknesses().isEmpty()) {
                weaknessesList.getItems().addAll(analysis.getWeaknesses());
            }

            futureScopeList.getItems().clear();
            if (analysis.getFutureResearchScope() != null && !analysis.getFutureResearchScope().isEmpty()) {
                futureScopeList.getItems().addAll(analysis.getFutureResearchScope());
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
        statusLabel.setText("Use 'Highlight Extracted Info' to see source locations");
    }

    @FXML
    private void handleExportCSV() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOCX Files", "*.docx"));
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
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
        alert.setContentText("Version 2.0\n\nA comprehensive tool for automatic analysis of academic papers.\n\nEnhanced Features:\n- Comprehensive information extraction\n- Advanced NLP-based analysis\n- Quality assessment with recommendations\n- Multiple export formats (CSV, DOCX, PDF)\n- PDF highlighting with yellow color\n- Paper metadata extraction\n- Readability analysis\n- Custom window controls\n- Dark/Light mode support");
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
                "4. Click 'Highlight Extracted Info' to create a highlighted PDF\n" +
                "5. The highlighted PDF will open automatically with YELLOW highlights\n" +
                "6. Use 'Save Highlighted PDF' to save it permanently\n\n" +
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