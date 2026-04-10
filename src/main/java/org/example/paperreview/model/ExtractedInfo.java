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

    // Constructor
    public ExtractedInfo() {
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
    }
}