package org.example.paperreview.model;

import java.util.ArrayList;
import java.util.List;

public class ExtractedInfo {
    private String paperTitle;
    private String authors;
    private String researchDomain;
    private String researchProblem;
    private String researchGap;
    private List<String> researchQuestions;
    private String researchHypothesis;
    private String methodology;
    private String dataCollectionMethods;
    private List<String> keyFindings;
    private List<String> citations;
    private String publicationCategory;

    public ExtractedInfo() {
        this.researchQuestions = new ArrayList<>();
        this.keyFindings = new ArrayList<>();
        this.citations = new ArrayList<>();
    }

    // Getters and Setters
    public String getPaperTitle() { return paperTitle; }
    public void setPaperTitle(String paperTitle) { this.paperTitle = paperTitle; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

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

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getDataCollectionMethods() { return dataCollectionMethods; }
    public void setDataCollectionMethods(String dataCollectionMethods) { this.dataCollectionMethods = dataCollectionMethods; }

    public List<String> getKeyFindings() { return keyFindings; }
    public void setKeyFindings(List<String> keyFindings) { this.keyFindings = keyFindings; }

    public List<String> getCitations() { return citations; }
    public void setCitations(List<String> citations) { this.citations = citations; }

    public String getPublicationCategory() { return publicationCategory; }
    public void setPublicationCategory(String publicationCategory) { this.publicationCategory = publicationCategory; }
}