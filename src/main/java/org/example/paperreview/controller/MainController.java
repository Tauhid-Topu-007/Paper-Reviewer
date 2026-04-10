package org.example.paperreview.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.paperreview.model.PaperAnalysis;
import org.example.paperreview.model.ExtractedInfo;
import org.example.paperreview.service.*;
import java.io.File;
import java.io.IOException;
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
            // Store current position and size
            previousWidth = stage.getWidth();
            previousHeight = stage.getHeight();
            previousX = stage.getX();
            previousY = stage.getY();

            // Maximize to screen bounds (accounting for taskbar)
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            isMaximized = true;

            // Change button text
            Button maximizeButton = (Button) pdfPreview.getScene().lookup("#maximizeButton");
            if (maximizeButton != null) {
                maximizeButton.setText("❐");
            }
        } else {
            // Restore to previous size
            stage.setX(previousX);
            stage.setY(previousY);
            stage.setWidth(previousWidth);
            stage.setHeight(previousHeight);
            isMaximized = false;

            // Change button text
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

    // Hover effects for window buttons
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
        pdfPreview.getEngine().loadContent(
                "<html><body style='font-family: Arial; padding: 20px;'>" +
                        "<h2>PDF Loaded Successfully</h2>" +
                        "<p>File: " + file.getName() + "</p>" +
                        "<p>Pages: " + pdfParser.getNumberOfPages() + "</p>" +
                        "<p>Click 'Analyze Paper' to extract information</p>" +
                        "</body></html>"
        );
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

                updateMessage("Generating review...");
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

    private void displayResults(PaperAnalysis analysis) {
        ExtractedInfo info = analysis.getExtractedInfo();

        Platform.runLater(() -> {
            // Extracted information
            paperTitleLabel.setText(info.getPaperTitle() != null ? info.getPaperTitle() : "N/A");
            authorsLabel.setText(info.getAuthors() != null ? info.getAuthors() : "N/A");
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
            publicationLabel.setText(info.getPublicationCategory() != null ? info.getPublicationCategory() : "N/A");

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
                        "alert('Source highlighting feature would highlight the exact lines from which information was extracted. In a production environment, this would integrate with PDF.js or similar library to highlight specific text positions.');"
        );

        statusLabel.setText("Source highlighting would be implemented with PDF.js integration");
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
        alert.setContentText("Version 1.0\n\nA comprehensive tool for automatic analysis of academic papers.\n\nFeatures:\n- Automatic extraction of research components\n- NLP-based analysis\n- Quality assessment\n- Export capabilities (CSV, DOCX, PDF)\n- Custom window controls\n- Dark/Light mode support");
        alert.showAndWait();
    }

    @FXML
    private void handleDocumentation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Documentation");
        alert.setHeaderText("How to Use");
        alert.setContentText("1. Click 'Upload PDF' to select a research paper\n" +
                "2. Click 'Analyze Paper' to extract information\n" +
                "3. View results in the tabs\n" +
                "4. Export results using the export button\n" +
                "5. Use the custom title bar buttons to minimize, maximize, and close\n" +
                "6. Drag the title bar to move the window\n" +
                "7. Resize the window by dragging the edges\n\n" +
                "Tip: The analysis may take a few seconds depending on paper length");
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