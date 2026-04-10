package org.example.paperreview.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaperAnalysis {
    private ExtractedInfo extractedInfo;
    private Map<String, List<SourceHighlight>> sourceHighlights;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> futureResearchScope;
    private String summary;
    private Double qualityScore;
    private String writingQualityComments;
    private String plagiarismRiskIndicator;
    private long processingTime;

    public PaperAnalysis() {
        this.extractedInfo = new ExtractedInfo();
        this.sourceHighlights = new HashMap<>();
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.futureResearchScope = new ArrayList<>();
    }

    // Getters and Setters
    public ExtractedInfo getExtractedInfo() { return extractedInfo; }
    public void setExtractedInfo(ExtractedInfo extractedInfo) { this.extractedInfo = extractedInfo; }

    public Map<String, List<SourceHighlight>> getSourceHighlights() { return sourceHighlights; }
    public void setSourceHighlights(Map<String, List<SourceHighlight>> sourceHighlights) { this.sourceHighlights = sourceHighlights; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getFutureResearchScope() { return futureResearchScope; }
    public void setFutureResearchScope(List<String> futureResearchScope) { this.futureResearchScope = futureResearchScope; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }

    public String getWritingQualityComments() { return writingQualityComments; }
    public void setWritingQualityComments(String writingQualityComments) { this.writingQualityComments = writingQualityComments; }

    public String getPlagiarismRiskIndicator() { return plagiarismRiskIndicator; }
    public void setPlagiarismRiskIndicator(String plagiarismRiskIndicator) { this.plagiarismRiskIndicator = plagiarismRiskIndicator; }

    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
}