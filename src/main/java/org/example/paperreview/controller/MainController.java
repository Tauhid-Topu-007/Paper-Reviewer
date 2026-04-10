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
    @FXML private Label pageNumberLabel;
    @FXML private Label totalPagesLabel;

    // Extracted info labels
    @FXML private Label paperTitleLabel;
    @FXML private Label authorsLabel;
    @FXML private Label domainLabel;
    @FXML private TextArea problemLabel;
    @FXML private TextArea gapLabel;
    @FXML private Label questionsLabel;
    @FXML private Label hypothesisLabel;
    @FXML private TextArea methodologyLabel;
    @FXML private Label dataCollectionLabel;
    @FXML private TextArea findingsLabel;
    @FXML private Label citationsLabel;
    @FXML private Label publicationLabel;
    @FXML private Label modelsUsedLabel;

    // New enhanced fields
    @FXML private TextArea abstractLabel;
    @FXML private Label keywordsLabel;
    @FXML private Label venueLabel;
    @FXML private Label doiLabel;
    @FXML private Label yearLabel;
    @FXML private Label sampleSizeLabel;
    @FXML private Label toolsLabel;
    @FXML private Label metricsLabel;
    @FXML private TextArea limitationsLabel;
    @FXML private TextArea futureWorkLabel;
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
    @FXML private Label recommendationLabel;

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
    private int currentPage = 1;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        pdfParser = new PDFParserService();
        nlpService = new NLPAnalysisService();
        sectionClassifier = new SectionClassifierService();
        exportService = new ExportService();
        pdfHighlighter = new PDFHighlighterService();
        currentAnalysis = new PaperAnalysis();

        strengthsList.setPlaceholder(new Label("No strengths identified yet"));
        weaknessesList.setPlaceholder(new Label("No weaknesses identified yet"));
        futureScopeList.setPlaceholder(new Label("No future scope identified yet"));

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
            maximizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand");
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
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }\n" +
                    "        .container { max-width: 800px; margin: 50px auto; background: white; border-radius: 15px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); overflow: hidden; }\n" +
                    "        .header { background: #2c3e50; color: white; padding: 20px; text-align: center; }\n" +
                    "        .content { padding: 30px; }\n" +
                    "        .info-box { background: #e8f5e9; padding: 15px; border-radius: 8px; margin: 15px 0; border-left: 4px solid #4CAF50; }\n" +
                    "        h2 { color: #2c3e50; margin-top: 0; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class='container'>\n" +
                    "        <div class='header'>\n" +
                    "            <h1>📄 Academic Paper Review Assistant</h1>\n" +
                    "        </div>\n" +
                    "        <div class='content'>\n" +
                    "            <h2>✅ PDF Loaded Successfully!</h2>\n" +
                    "            <div class='info-box'>\n" +
                    "                <strong>📁 File:</strong> " + file.getName() + "<br>\n" +
                    "                <strong>📄 Pages:</strong> " + pdfParser.getNumberOfPages() + "<br>\n" +
                    "                <strong>📊 Status:</strong> Ready for analysis\n" +
                    "            </div>\n" +
                    "            <h3>🎯 Next Steps:</h3>\n" +
                    "            <ol>\n" +
                    "                <li>Click <strong>'Analyze Paper'</strong> to extract all information</li>\n" +
                    "                <li>View extracted information in the tabs</li>\n" +
                    "                <li>Click <strong>'Highlight Extracted Info'</strong> to create highlighted PDF</li>\n" +
                    "            </ol>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            pdfPreview.getEngine().loadContent(htmlContent);
            pdfViewerLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
            pdfPreview.getEngine().loadContent("<html><body><h3>PDF Loaded</h3><p>File: " + file.getName() + "</p></body></html>");
            pdfViewerLoaded = true;
        }
    }

    @FXML
    private void prevPage() {
        // Simple navigation - reload the page
        if (currentPage > 1) {
            currentPage--;
            pageNumberLabel.setText(String.valueOf(currentPage));
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            pageNumberLabel.setText(String.valueOf(currentPage));
        }
    }

    @FXML
    private void zoomIn() {
        // Zoom functionality
    }

    @FXML
    private void zoomOut() {
        // Zoom functionality
    }

    @FXML
    private void resetZoom() {
        // Reset zoom functionality
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

                addToHighlightMap(extractedInfoMap, "Paper Title", info.getPaperTitle());
                addToHighlightMap(extractedInfoMap, "Abstract", info.getAbstractText());
                addToHighlightMap(extractedInfoMap, "Research Problem", info.getResearchProblem());
                addToHighlightMap(extractedInfoMap, "Research Gap", info.getResearchGap());
                addToHighlightMap(extractedInfoMap, "Methodology", info.getMethodology());
                addToHighlightMap(extractedInfoMap, "Executive Summary", currentAnalysis.getSummary());

                if (info.getKeyFindings() != null) {
                    for (int i = 0; i < info.getKeyFindings().size(); i++) {
                        String finding = info.getKeyFindings().get(i);
                        if (isValidForHighlight(finding)) {
                            extractedInfoMap.put("Key Finding " + (i + 1), finding);
                        }
                    }
                }

                updateMessage("Searching " + extractedInfoMap.size() + " items in PDF...");

                PDFHighlighterService highlighter = new PDFHighlighterService();
                return highlighter.createHighlightedPDF(currentPDFFile, extractedInfoMap);
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
                showInfo("Highlight Complete", "PDF highlighted successfully!");
            } catch (IOException ex) {
                showInfo("Highlight Complete", "File saved at: " + highlightedPDFFile.getAbsolutePath());
            }
        });

        highlightTask.setOnFailed(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Highlight failed");
            progressIndicator.setVisible(false);
            Throwable exception = highlightTask.getException();
            showError("Highlight Failed", exception != null ? exception.getMessage() : "Unknown error");
        });

        new Thread(highlightTask).start();
    }

    private void addToHighlightMap(Map<String, String> map, String key, String value) {
        if (isValidForHighlight(value)) {
            String shortValue = value.length() > 500 ? value.substring(0, 500) : value;
            map.put(key, shortValue);
        }
    }

    private boolean isValidForHighlight(String text) {
        if (text == null || text.isEmpty()) return false;
        String[] invalidValues = {"N/A", "Not found", "Not available", "Not clearly stated",
                "Not explicitly stated", "Not clearly described", "Not specified",
                "No summary available", "Abstract not available", "Keywords not found"};
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
                showError("Cannot Open PDF", e.getMessage());
            }
        } else {
            showError("No Highlighted PDF", "Please click 'Highlight Extracted Info' first");
        }
    }

    @FXML
    private void saveHighlightedPDF() {
        if (highlightedPDFFile == null || !highlightedPDFFile.exists()) {
            showError("No Highlighted PDF", "Please click 'Highlight Extracted Info' first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Highlighted PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("highlighted_paper.pdf");

        File saveFile = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (saveFile != null) {
            try {
                java.nio.file.Files.copy(highlightedPDFFile.toPath(), saveFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                showInfo("Save Successful", "Highlighted PDF saved to:\n" + saveFile.getAbsolutePath());
                statusLabel.setText("Highlighted PDF saved!");
            } catch (IOException e) {
                showError("Save Failed", e.getMessage());
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
            return "✅ Strongly Accept - High quality paper";
        } else if (score >= 70) {
            return "📝 Accept - Good quality paper with minor revisions";
        } else if (score >= 60) {
            return "🔄 Minor Revisions - Paper needs improvements";
        } else if (score >= 50) {
            return "⚠️ Major Revisions - Paper requires substantial changes";
        } else {
            return "❌ Reject - Paper does not meet quality standards";
        }
    }

    private void displayResults(PaperAnalysis analysis) {
        ExtractedInfo info = analysis.getExtractedInfo();

        Platform.runLater(() -> {
            // Basic Information
            paperTitleLabel.setText(getValidText(info.getPaperTitle(), "Title not found"));
            authorsLabel.setText(getValidText(info.getAuthors(), "Authors not found"));
            publicationLabel.setText(getValidText(info.getPublicationCategory(), "N/A"));

            abstractLabel.setText(getValidText(info.getAbstractText(), "Abstract not available"));
            keywordsLabel.setText(getValidText(info.getKeywords(), "Keywords not found"));
            venueLabel.setText(getValidText(info.getPublicationVenue(), "Not specified"));
            doiLabel.setText(getValidText(info.getDoi(), "Not available"));
            yearLabel.setText(getValidText(info.getPublicationYear(), "Unknown"));
            sampleSizeLabel.setText(getValidText(info.getSampleSize(), "Not specified"));
            toolsLabel.setText(getValidText(info.getToolsAndTechnologies(), "Not specified"));
            metricsLabel.setText(getValidText(info.getEvaluationMetrics(), "Not specified"));
            limitationsLabel.setText(getValidText(info.getLimitations(), "Not specified"));
            futureWorkLabel.setText(getValidText(info.getFutureWork(), "Not specified"));
            figuresLabel.setText(String.valueOf(info.getFigureCount()));
            tablesLabel.setText(String.valueOf(info.getTableCount()));
            referencesLabel.setText(String.valueOf(info.getTotalReferences()));
            readabilityLabel.setText(String.format("%.2f - %s", info.getReadabilityScore(), info.getLanguageComplexity()));

            // Research Components
            domainLabel.setText(getValidText(info.getResearchDomain(), "Not classified"));
            problemLabel.setText(getValidText(info.getResearchProblem(), "Not clearly stated"));
            gapLabel.setText(getValidText(info.getResearchGap(), "Not explicitly stated"));

            if (info.getResearchQuestions() != null && !info.getResearchQuestions().isEmpty()) {
                questionsLabel.setText(String.join("\n", info.getResearchQuestions()));
            } else {
                questionsLabel.setText("N/A");
            }

            hypothesisLabel.setText(getValidText(info.getResearchHypothesis(), "No explicit hypothesis"));
            methodologyLabel.setText(getValidText(info.getMethodology(), "Not clearly described"));
            dataCollectionLabel.setText(getValidText(info.getDataCollectionMethods(), "Not specified"));

            if (info.getKeyFindings() != null && !info.getKeyFindings().isEmpty()) {
                findingsLabel.setText(String.join("\n", info.getKeyFindings()));
            } else {
                findingsLabel.setText("No key findings extracted");
            }

            citationsLabel.setText(info.getCitations() != null ? info.getCitations().size() + " citations found" : "0 citations found");

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
            if (info.getLibrariesUsedInPaper() != null && !info.getLibrariesUsedInPaper().isEmpty()) {
                modelsText.append("Libraries: ").append(String.join(", ", info.getLibrariesUsedInPaper()));
            }

            if (modelsUsedLabel != null) {
                modelsUsedLabel.setText(modelsText.length() > 0 ? modelsText.toString() : "No specific models detected");
            }

            // Review Assistant
            summaryArea.setText(getValidText(analysis.getSummary(), "No summary available"));
            recommendationLabel.setText(getValidText(analysis.getRecommendation(), "N/A"));

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
            writingQualityLabel.setText(getValidText(analysis.getWritingQualityComments(), "N/A"));
            plagiarismLabel.setText(getValidText(analysis.getPlagiarismRiskIndicator(), "N/A"));
        });
    }

    private String getValidText(String text, String defaultValue) {
        if (text == null || text.isEmpty() || text.equals("null")) {
            return defaultValue;
        }
        return text;
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
        modelsUsedLabel.setText("");

        abstractLabel.setText("");
        keywordsLabel.setText("");
        venueLabel.setText("");
        doiLabel.setText("");
        yearLabel.setText("");
        sampleSizeLabel.setText("");
        toolsLabel.setText("");
        metricsLabel.setText("");
        limitationsLabel.setText("");
        futureWorkLabel.setText("");
        figuresLabel.setText("");
        tablesLabel.setText("");
        referencesLabel.setText("");
        readabilityLabel.setText("");

        summaryArea.clear();
        strengthsList.getItems().clear();
        weaknessesList.getItems().clear();
        futureScopeList.getItems().clear();
        qualityScoreBar.setProgress(0);
        qualityScoreLabel.setText("");
        writingQualityLabel.setText("");
        plagiarismLabel.setText("");
        recommendationLabel.setText("");

        currentAnalysis = null;
        statusLabel.setText("Cleared");
    }

    @FXML
    private void handleToggleDarkMode() {
        Scene scene = pdfPreview.getScene();
        if (scene.getStylesheets().isEmpty()) {
            scene.getStylesheets().add(getClass().getResource("/org/example/paperreview/dark-mode.css").toExternalForm());
            statusLabel.setText("Dark mode enabled");
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
        alert.setContentText("Version 2.0\n\nA comprehensive tool for automatic analysis of academic papers.\n\nFeatures:\n- Extract paper information automatically\n- NLP-based analysis\n- Quality assessment with recommendations\n- PDF highlighting\n- Model detection from papers\n- Export to CSV, DOCX, PDF");
        alert.showAndWait();
    }

    @FXML
    private void handleDocumentation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Documentation");
        alert.setHeaderText("How to Use");
        alert.setContentText("1. Click 'Upload PDF' to select a research paper\n" +
                "2. Click 'Analyze Paper' to extract information\n" +
                "3. View extracted information in the tabs\n" +
                "4. Click 'Highlight Extracted Info' to create a highlighted PDF\n" +
                "5. The highlighted PDF will open automatically\n" +
                "6. Use 'Open Highlighted PDF' to view it again\n" +
                "7. Use 'Save Highlighted PDF' to save it permanently");
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