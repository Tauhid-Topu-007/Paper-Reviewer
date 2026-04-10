// MainController.java
package org.example.paperreview.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
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
        // For preview, we can show a message or implement actual PDF rendering
        // Since WebView doesn't directly support PDF, we'll show a message
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
            showError("Analysis Failed", analysisTask.getException().getMessage());
        });

        new Thread(analysisTask).start();
    }

    private void displayResults(PaperAnalysis analysis) {
        ExtractedInfo info = analysis.getExtractedInfo();

        Platform.runLater(() -> {
            // Extracted information
            paperTitleLabel.setText(info.getPaperTitle());
            authorsLabel.setText(info.getAuthors());
            domainLabel.setText(info.getResearchDomain());
            problemLabel.setText(info.getResearchProblem());
            gapLabel.setText(info.getResearchGap());
            questionsLabel.setText(String.join("\n", info.getResearchQuestions()));
            hypothesisLabel.setText(info.getResearchHypothesis());
            methodologyLabel.setText(info.getMethodology());
            dataCollectionLabel.setText(info.getDataCollectionMethods());
            findingsLabel.setText(String.join("\n", info.getKeyFindings()));
            citationsLabel.setText(String.valueOf(info.getCitations().size()) + " citations found");
            publicationLabel.setText(info.getPublicationCategory());

            // Review assistant
            summaryArea.setText(analysis.getSummary());

            strengthsList.getItems().clear();
            strengthsList.getItems().addAll(analysis.getStrengths());

            weaknessesList.getItems().clear();
            weaknessesList.getItems().addAll(analysis.getWeaknesses());

            futureScopeList.getItems().clear();
            futureScopeList.getItems().addAll(analysis.getFutureResearchScope());

            qualityScoreBar.setProgress(analysis.getQualityScore() / 100.0);
            qualityScoreLabel.setText(String.format("Quality Score: %.1f/100", analysis.getQualityScore()));

            writingQualityLabel.setText(analysis.getWritingQualityComments());
            plagiarismLabel.setText(analysis.getPlagiarismRiskIndicator());
        });
    }

    @FXML
    private void handleShowSourceLines() {
        if (currentAnalysis == null) {
            showError("No Analysis", "Please analyze a paper first");
            return;
        }

        // Highlight source lines in the PDF preview
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

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToCSV(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to CSV: " + file.getName());
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

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToDOCX(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to DOCX: " + file.getName());
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

        File file = fileChooser.showSaveDialog(pdfPreview.getScene().getWindow());
        if (file != null) {
            try {
                exportService.exportToPDF(currentAnalysis, file.getAbsolutePath());
                statusLabel.setText("Exported to PDF: " + file.getName());
            } catch (IOException e) {
                showError("Export Failed", e.getMessage());
            } catch (com.itextpdf.text.DocumentException e) {
                showError("Export Failed", "PDF document error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExport() {
        // Show export options dialog
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
        if (scene.getStylesheets().isEmpty()) {
            scene.getStylesheets().add(getClass().getResource("/org/example/paperreview/dark-mode.css").toExternalForm());
        } else {
            scene.getStylesheets().clear();
        }
    }

    @FXML
    private void handleResetView() {
        // Reset any zoom or view settings
        pdfPreview.getEngine().reload();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Academic Paper Review Assistant");
        alert.setContentText("Version 1.0\n\nA comprehensive tool for automatic analysis of academic papers.\n\nFeatures:\n- Automatic extraction of research components\n- NLP-based analysis\n- Quality assessment\n- Export capabilities");
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
                "5. Use 'Show Source Lines' to see extraction sources");
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
}