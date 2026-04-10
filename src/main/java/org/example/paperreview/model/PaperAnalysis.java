package org.example.paperreview.model;

import java.util.List;
import java.util.ArrayList;

public class PaperAnalysis {
    private ExtractedInfo extractedInfo;
    private String summary;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> futureResearchScope;
    private double qualityScore;
    private String writingQualityComments;
    private String plagiarismRiskIndicator;
    private String noveltyAssessment;
    private String impactAssessment;
    private String recommendation;
    private List<String> suggestedImprovements;

    public PaperAnalysis() {
        this.extractedInfo = new ExtractedInfo();
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.futureResearchScope = new ArrayList<>();
        this.suggestedImprovements = new ArrayList<>();
    }

    // Getters and Setters
    public ExtractedInfo getExtractedInfo() { return extractedInfo; }
    public void setExtractedInfo(ExtractedInfo extractedInfo) { this.extractedInfo = extractedInfo; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getFutureResearchScope() { return futureResearchScope; }
    public void setFutureResearchScope(List<String> futureResearchScope) { this.futureResearchScope = futureResearchScope; }

    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }

    public String getWritingQualityComments() { return writingQualityComments; }
    public void setWritingQualityComments(String writingQualityComments) { this.writingQualityComments = writingQualityComments; }

    public String getPlagiarismRiskIndicator() { return plagiarismRiskIndicator; }
    public void setPlagiarismRiskIndicator(String plagiarismRiskIndicator) { this.plagiarismRiskIndicator = plagiarismRiskIndicator; }

    public String getNoveltyAssessment() { return noveltyAssessment; }
    public void setNoveltyAssessment(String noveltyAssessment) { this.noveltyAssessment = noveltyAssessment; }

    public String getImpactAssessment() { return impactAssessment; }
    public void setImpactAssessment(String impactAssessment) { this.impactAssessment = impactAssessment; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public List<String> getSuggestedImprovements() { return suggestedImprovements; }
    public void setSuggestedImprovements(List<String> suggestedImprovements) { this.suggestedImprovements = suggestedImprovements; }
}