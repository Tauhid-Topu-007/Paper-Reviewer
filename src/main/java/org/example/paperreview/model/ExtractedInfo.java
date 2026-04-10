package org.example.paperreview.model;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class ExtractedInfo {
    // Basic Information
    private String paperTitle;
    private String authors;
    private String abstractText;
    private String keywords;
    private String publicationCategory;
    private String publicationYear;
    private String publicationVenue;
    private String doi;
    private String correspondingAuthor;
    private String affiliations;

    // Research Components
    private String researchDomain;
    private String researchProblem;
    private String researchGap;
    private List<String> researchQuestions;
    private String researchHypothesis;
    private String researchObjectives;
    private String contributions;

    // Methodology
    private String methodology;
    private String dataCollectionMethods;
    private String sampleSize;
    private String dataAnalysisMethods;
    private String toolsAndTechnologies;
    private String experimentalSetup;
    private String evaluationMetrics;

    // Results
    private List<String> keyFindings;
    private List<String> results;
    private List<String> statisticalResults;
    private List<Double> performanceMetrics;

    // Analysis
    private String discussion;
    private String limitations;
    private String futureWork;
    private List<String> practicalImplications;
    private List<String> theoreticalImplications;

    // References
    private List<String> citations;
    private int totalReferences;
    private Map<String, Integer> citationDistribution;
    private List<String> highlyCitedPapers;

    // Additional Metadata
    private int pageCount;
    private int figureCount;
    private int tableCount;
    private int equationCount;
    private double averageSentenceLength;
    private double readabilityScore;
    private String languageComplexity;
    private List<String> keyTerms;
    private Map<String, Integer> termFrequency;

    // Table and Figure Details
    private List<String> tableDescriptions;
    private List<String> figureDescriptions;
    private List<String> tableTitles;
    private List<String> figureTitles;
    private Map<String, String> tableData;
    private Map<String, String> figureData;

    // Model and Framework Details
    private List<String> modelsUsed;
    private List<String> frameworksUsed;
    private List<String> algorithmsUsed;
    private List<String> librariesUsed;

    // Dataset Information
    private String datasetName;
    private String datasetSize;
    private String datasetSource;
    private List<String> dataSources;

    // Performance Comparison
    private Map<String, Double> benchmarkResults;
    private List<String> comparisonMethods;
    private String baselineMethod;

    // Statistical Analysis
    private String statisticalSignificance;
    private String confidenceInterval;
    private String pValues;
    private String effectSize;

    // Getters and Setters
    public String getPaperTitle() { return paperTitle; }
    public void setPaperTitle(String paperTitle) { this.paperTitle = paperTitle; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getPublicationCategory() { return publicationCategory; }
    public void setPublicationCategory(String publicationCategory) { this.publicationCategory = publicationCategory; }

    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }

    public String getPublicationVenue() { return publicationVenue; }
    public void setPublicationVenue(String publicationVenue) { this.publicationVenue = publicationVenue; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public String getCorrespondingAuthor() { return correspondingAuthor; }
    public void setCorrespondingAuthor(String correspondingAuthor) { this.correspondingAuthor = correspondingAuthor; }

    public String getAffiliations() { return affiliations; }
    public void setAffiliations(String affiliations) { this.affiliations = affiliations; }

    public String getResearchDomain() { return researchDomain; }
    public void setResearchDomain(String researchDomain) { this.researchDomain = researchDomain; }

    public String getResearchProblem() { return researchProblem; }
    public void setResearchProblem(String researchProblem) { this.researchProblem = researchProblem; }

    public String getResearchGap() { return researchGap; }
    public void setResearchGap(String researchGap) { this.researchGap = researchGap; }

    public List<String> getResearchQuestions() { return researchQuestions; }
    public void setResearchQuestions(List<String> researchQuestions) { this.researchQuestions = researchQuestions; }

    public String getResearchHypothesis() { return researchHypothesis; }
    public void setResearchHypothesis(String researchHypothesis) { this.researchHypothesis = researchHypothesis; }

    public String getResearchObjectives() { return researchObjectives; }
    public void setResearchObjectives(String researchObjectives) { this.researchObjectives = researchObjectives; }

    public String getContributions() { return contributions; }
    public void setContributions(String contributions) { this.contributions = contributions; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getDataCollectionMethods() { return dataCollectionMethods; }
    public void setDataCollectionMethods(String dataCollectionMethods) { this.dataCollectionMethods = dataCollectionMethods; }

    public String getSampleSize() { return sampleSize; }
    public void setSampleSize(String sampleSize) { this.sampleSize = sampleSize; }

    public String getDataAnalysisMethods() { return dataAnalysisMethods; }
    public void setDataAnalysisMethods(String dataAnalysisMethods) { this.dataAnalysisMethods = dataAnalysisMethods; }

    public String getToolsAndTechnologies() { return toolsAndTechnologies; }
    public void setToolsAndTechnologies(String toolsAndTechnologies) { this.toolsAndTechnologies = toolsAndTechnologies; }

    public String getExperimentalSetup() { return experimentalSetup; }
    public void setExperimentalSetup(String experimentalSetup) { this.experimentalSetup = experimentalSetup; }

    public String getEvaluationMetrics() { return evaluationMetrics; }
    public void setEvaluationMetrics(String evaluationMetrics) { this.evaluationMetrics = evaluationMetrics; }

    public List<String> getKeyFindings() { return keyFindings; }
    public void setKeyFindings(List<String> keyFindings) { this.keyFindings = keyFindings; }

    public List<String> getResults() { return results; }
    public void setResults(List<String> results) { this.results = results; }

    public List<String> getStatisticalResults() { return statisticalResults; }
    public void setStatisticalResults(List<String> statisticalResults) { this.statisticalResults = statisticalResults; }

    public List<Double> getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(List<Double> performanceMetrics) { this.performanceMetrics = performanceMetrics; }

    public String getDiscussion() { return discussion; }
    public void setDiscussion(String discussion) { this.discussion = discussion; }

    public String getLimitations() { return limitations; }
    public void setLimitations(String limitations) { this.limitations = limitations; }

    public String getFutureWork() { return futureWork; }
    public void setFutureWork(String futureWork) { this.futureWork = futureWork; }

    public List<String> getPracticalImplications() { return practicalImplications; }
    public void setPracticalImplications(List<String> practicalImplications) { this.practicalImplications = practicalImplications; }

    public List<String> getTheoreticalImplications() { return theoreticalImplications; }
    public void setTheoreticalImplications(List<String> theoreticalImplications) { this.theoreticalImplications = theoreticalImplications; }

    public List<String> getCitations() { return citations; }
    public void setCitations(List<String> citations) { this.citations = citations; }

    public int getTotalReferences() { return totalReferences; }
    public void setTotalReferences(int totalReferences) { this.totalReferences = totalReferences; }

    public Map<String, Integer> getCitationDistribution() { return citationDistribution; }
    public void setCitationDistribution(Map<String, Integer> citationDistribution) { this.citationDistribution = citationDistribution; }

    public List<String> getHighlyCitedPapers() { return highlyCitedPapers; }
    public void setHighlyCitedPapers(List<String> highlyCitedPapers) { this.highlyCitedPapers = highlyCitedPapers; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public int getFigureCount() { return figureCount; }
    public void setFigureCount(int figureCount) { this.figureCount = figureCount; }

    public int getTableCount() { return tableCount; }
    public void setTableCount(int tableCount) { this.tableCount = tableCount; }

    public int getEquationCount() { return equationCount; }
    public void setEquationCount(int equationCount) { this.equationCount = equationCount; }

    public double getAverageSentenceLength() { return averageSentenceLength; }
    public void setAverageSentenceLength(double averageSentenceLength) { this.averageSentenceLength = averageSentenceLength; }

    public double getReadabilityScore() { return readabilityScore; }
    public void setReadabilityScore(double readabilityScore) { this.readabilityScore = readabilityScore; }

    public String getLanguageComplexity() { return languageComplexity; }
    public void setLanguageComplexity(String languageComplexity) { this.languageComplexity = languageComplexity; }

    public List<String> getKeyTerms() { return keyTerms; }
    public void setKeyTerms(List<String> keyTerms) { this.keyTerms = keyTerms; }

    public Map<String, Integer> getTermFrequency() { return termFrequency; }
    public void setTermFrequency(Map<String, Integer> termFrequency) { this.termFrequency = termFrequency; }

    // Table and Figure Details Getters and Setters
    public List<String> getTableDescriptions() { return tableDescriptions; }
    public void setTableDescriptions(List<String> tableDescriptions) { this.tableDescriptions = tableDescriptions; }

    public List<String> getFigureDescriptions() { return figureDescriptions; }
    public void setFigureDescriptions(List<String> figureDescriptions) { this.figureDescriptions = figureDescriptions; }

    public List<String> getTableTitles() { return tableTitles; }
    public void setTableTitles(List<String> tableTitles) { this.tableTitles = tableTitles; }

    public List<String> getFigureTitles() { return figureTitles; }
    public void setFigureTitles(List<String> figureTitles) { this.figureTitles = figureTitles; }

    public Map<String, String> getTableData() { return tableData; }
    public void setTableData(Map<String, String> tableData) { this.tableData = tableData; }

    public Map<String, String> getFigureData() { return figureData; }
    public void setFigureData(Map<String, String> figureData) { this.figureData = figureData; }

    // Model and Framework Getters and Setters
    public List<String> getModelsUsed() { return modelsUsed; }
    public void setModelsUsed(List<String> modelsUsed) { this.modelsUsed = modelsUsed; }

    public List<String> getFrameworksUsed() { return frameworksUsed; }
    public void setFrameworksUsed(List<String> frameworksUsed) { this.frameworksUsed = frameworksUsed; }

    public List<String> getAlgorithmsUsed() { return algorithmsUsed; }
    public void setAlgorithmsUsed(List<String> algorithmsUsed) { this.algorithmsUsed = algorithmsUsed; }

    public List<String> getLibrariesUsed() { return librariesUsed; }
    public void setLibrariesUsed(List<String> librariesUsed) { this.librariesUsed = librariesUsed; }

    // Dataset Information Getters and Setters
    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }

    public String getDatasetSize() { return datasetSize; }
    public void setDatasetSize(String datasetSize) { this.datasetSize = datasetSize; }

    public String getDatasetSource() { return datasetSource; }
    public void setDatasetSource(String datasetSource) { this.datasetSource = datasetSource; }

    public List<String> getDataSources() { return dataSources; }
    public void setDataSources(List<String> dataSources) { this.dataSources = dataSources; }

    // Performance Comparison Getters and Setters
    public Map<String, Double> getBenchmarkResults() { return benchmarkResults; }
    public void setBenchmarkResults(Map<String, Double> benchmarkResults) { this.benchmarkResults = benchmarkResults; }

    public List<String> getComparisonMethods() { return comparisonMethods; }
    public void setComparisonMethods(List<String> comparisonMethods) { this.comparisonMethods = comparisonMethods; }

    public String getBaselineMethod() { return baselineMethod; }
    public void setBaselineMethod(String baselineMethod) { this.baselineMethod = baselineMethod; }

    // Statistical Analysis Getters and Setters
    public String getStatisticalSignificance() { return statisticalSignificance; }
    public void setStatisticalSignificance(String statisticalSignificance) { this.statisticalSignificance = statisticalSignificance; }

    public String getConfidenceInterval() { return confidenceInterval; }
    public void setConfidenceInterval(String confidenceInterval) { this.confidenceInterval = confidenceInterval; }

    public String getPValues() { return pValues; }
    public void setPValues(String pValues) { this.pValues = pValues; }

    public String getEffectSize() { return effectSize; }
    public void setEffectSize(String effectSize) { this.effectSize = effectSize; }

    // Constructor
    public ExtractedInfo() {
        // Initialize all collections
        this.researchQuestions = new ArrayList<>();
        this.keyFindings = new ArrayList<>();
        this.results = new ArrayList<>();
        this.statisticalResults = new ArrayList<>();
        this.performanceMetrics = new ArrayList<>();
        this.citations = new ArrayList<>();
        this.highlyCitedPapers = new ArrayList<>();
        this.practicalImplications = new ArrayList<>();
        this.theoreticalImplications = new ArrayList<>();
        this.keyTerms = new ArrayList<>();
        this.citationDistribution = new HashMap<>();
        this.termFrequency = new HashMap<>();

        // Initialize new collections
        this.tableDescriptions = new ArrayList<>();
        this.figureDescriptions = new ArrayList<>();
        this.tableTitles = new ArrayList<>();
        this.figureTitles = new ArrayList<>();
        this.tableData = new HashMap<>();
        this.figureData = new HashMap<>();
        this.modelsUsed = new ArrayList<>();
        this.frameworksUsed = new ArrayList<>();
        this.algorithmsUsed = new ArrayList<>();
        this.librariesUsed = new ArrayList<>();
        this.dataSources = new ArrayList<>();
        this.benchmarkResults = new HashMap<>();
        this.comparisonMethods = new ArrayList<>();

        // Set default values
        this.paperTitle = "Not found";
        this.authors = "Not found";
        this.abstractText = "Abstract not available";
        this.keywords = "Not specified";
        this.publicationCategory = "Not categorized";
        this.publicationYear = "Unknown";
        this.publicationVenue = "Not specified";
        this.doi = "Not available";
        this.correspondingAuthor = "Not specified";
        this.affiliations = "Not specified";
        this.researchDomain = "Not classified";
        this.researchProblem = "Not clearly stated";
        this.researchGap = "Not explicitly stated";
        this.researchHypothesis = "No explicit hypothesis";
        this.researchObjectives = "Not clearly stated";
        this.contributions = "Not explicitly stated";
        this.methodology = "Not clearly described";
        this.dataCollectionMethods = "Not specified";
        this.sampleSize = "Not specified";
        this.dataAnalysisMethods = "Not specified";
        this.toolsAndTechnologies = "Not specified";
        this.experimentalSetup = "Not described";
        this.evaluationMetrics = "Not specified";
        this.discussion = "Not available";
        this.limitations = "Not explicitly stated";
        this.futureWork = "Not discussed";
        this.datasetName = "Not specified";
        this.datasetSize = "Not specified";
        this.datasetSource = "Not specified";
        this.baselineMethod = "Not specified";
        this.statisticalSignificance = "Not reported";
        this.confidenceInterval = "Not reported";
        this.pValues = "Not reported";
        this.effectSize = "Not reported";
    }

    // Helper method to add table information
    public void addTableInfo(String title, String description, String data) {
        if (tableTitles == null) tableTitles = new ArrayList<>();
        if (tableDescriptions == null) tableDescriptions = new ArrayList<>();
        if (tableData == null) tableData = new HashMap<>();

        tableTitles.add(title);
        tableDescriptions.add(description);
        if (data != null) {
            tableData.put(title, data);
        }
    }

    // Helper method to add figure information
    public void addFigureInfo(String title, String description, String data) {
        if (figureTitles == null) figureTitles = new ArrayList<>();
        if (figureDescriptions == null) figureDescriptions = new ArrayList<>();
        if (figureData == null) figureData = new HashMap<>();

        figureTitles.add(title);
        figureDescriptions.add(description);
        if (data != null) {
            figureData.put(title, data);
        }
    }

    // Helper method to add model information
    public void addModel(String modelName) {
        if (modelsUsed == null) modelsUsed = new ArrayList<>();
        if (!modelsUsed.contains(modelName)) {
            modelsUsed.add(modelName);
        }
    }

    // Helper method to add framework information
    public void addFramework(String frameworkName) {
        if (frameworksUsed == null) frameworksUsed = new ArrayList<>();
        if (!frameworksUsed.contains(frameworkName)) {
            frameworksUsed.add(frameworkName);
        }
    }

    // Helper method to add algorithm information
    public void addAlgorithm(String algorithmName) {
        if (algorithmsUsed == null) algorithmsUsed = new ArrayList<>();
        if (!algorithmsUsed.contains(algorithmName)) {
            algorithmsUsed.add(algorithmName);
        }
    }

    // Helper method to get summary of tables and figures
    public String getTablesAndFiguresSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Tables: ").append(tableCount).append("\n");
        summary.append("Figures: ").append(figureCount).append("\n");
        summary.append("Equations: ").append(equationCount).append("\n");

        if (tableTitles != null && !tableTitles.isEmpty()) {
            summary.append("\nTable Titles:\n");
            for (String title : tableTitles) {
                summary.append("  • ").append(title).append("\n");
            }
        }

        if (figureTitles != null && !figureTitles.isEmpty()) {
            summary.append("\nFigure Titles:\n");
            for (String title : figureTitles) {
                summary.append("  • ").append(title).append("\n");
            }
        }

        return summary.toString();
    }

    // Helper method to get models and frameworks summary
    public String getModelsAndFrameworksSummary() {
        StringBuilder summary = new StringBuilder();

        if (modelsUsed != null && !modelsUsed.isEmpty()) {
            summary.append("Models: ").append(String.join(", ", modelsUsed)).append("\n");
        }

        if (frameworksUsed != null && !frameworksUsed.isEmpty()) {
            summary.append("Frameworks: ").append(String.join(", ", frameworksUsed)).append("\n");
        }

        if (algorithmsUsed != null && !algorithmsUsed.isEmpty()) {
            summary.append("Algorithms: ").append(String.join(", ", algorithmsUsed)).append("\n");
        }

        if (librariesUsed != null && !librariesUsed.isEmpty()) {
            summary.append("Libraries: ").append(String.join(", ", librariesUsed)).append("\n");
        }

        return summary.toString();
    }

    // Helper method to get dataset information
    public String getDatasetSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Dataset: ").append(datasetName).append("\n");
        summary.append("Size: ").append(datasetSize).append("\n");
        summary.append("Source: ").append(datasetSource).append("\n");

        if (dataSources != null && !dataSources.isEmpty()) {
            summary.append("Data Sources:\n");
            for (String source : dataSources) {
                summary.append("  • ").append(source).append("\n");
            }
        }

        return summary.toString();
    }

    // Helper method to get performance comparison
    public String getPerformanceComparison() {
        StringBuilder summary = new StringBuilder();

        if (benchmarkResults != null && !benchmarkResults.isEmpty()) {
            summary.append("Benchmark Results:\n");
            for (Map.Entry<String, Double> entry : benchmarkResults.entrySet()) {
                summary.append("  • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        if (comparisonMethods != null && !comparisonMethods.isEmpty()) {
            summary.append("Comparison Methods: ").append(String.join(", ", comparisonMethods)).append("\n");
        }

        if (baselineMethod != null && !baselineMethod.equals("Not specified")) {
            summary.append("Baseline: ").append(baselineMethod).append("\n");
        }

        return summary.toString();
    }

    // Helper method to get statistical analysis summary
    public String getStatisticalSummary() {
        StringBuilder summary = new StringBuilder();

        if (statisticalSignificance != null && !statisticalSignificance.equals("Not reported")) {
            summary.append("Statistical Significance: ").append(statisticalSignificance).append("\n");
        }

        if (confidenceInterval != null && !confidenceInterval.equals("Not reported")) {
            summary.append("Confidence Interval: ").append(confidenceInterval).append("\n");
        }

        if (pValues != null && !pValues.equals("Not reported")) {
            summary.append("P-values: ").append(pValues).append("\n");
        }

        if (effectSize != null && !effectSize.equals("Not reported")) {
            summary.append("Effect Size: ").append(effectSize).append("\n");
        }

        if (statisticalResults != null && !statisticalResults.isEmpty()) {
            summary.append("\nStatistical Results:\n");
            for (String result : statisticalResults) {
                summary.append("  • ").append(result).append("\n");
            }
        }

        return summary.toString();
    }
}