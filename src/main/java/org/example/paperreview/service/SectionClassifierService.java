package org.example.paperreview.service;

import java.util.*;
import java.util.regex.Pattern;

public class SectionClassifierService {

    public enum SectionType {
        TITLE, ABSTRACT, INTRODUCTION, LITERATURE_REVIEW,
        METHODOLOGY, RESULTS, DISCUSSION, CONCLUSION,
        REFERENCES, APPENDIX, UNKNOWN
    }

    public Map<SectionType, String> classifySections(String fullText) {
        Map<SectionType, String> sections = new HashMap<>();
        String lowerText = fullText.toLowerCase();

        // Classify abstract
        int abstractStart = findSectionStart(lowerText, "abstract");
        if (abstractStart != -1) {
            int abstractEnd = findSectionEnd(lowerText, abstractStart, "introduction", "keywords");
            sections.put(SectionType.ABSTRACT, extractSection(fullText, abstractStart, abstractEnd));
        }

        // Classify introduction
        int introStart = findSectionStart(lowerText, "introduction");
        if (introStart != -1) {
            int introEnd = findSectionEnd(lowerText, introStart, "related work", "literature", "background", "methodology");
            sections.put(SectionType.INTRODUCTION, extractSection(fullText, introStart, introEnd));
        }

        // Classify methodology
        int methodStart = findSectionStart(lowerText, "methodology", "methods", "experimental setup");
        if (methodStart != -1) {
            int methodEnd = findSectionEnd(lowerText, methodStart, "results", "experiments", "evaluation");
            sections.put(SectionType.METHODOLOGY, extractSection(fullText, methodStart, methodEnd));
        }

        // Classify results
        int resultsStart = findSectionStart(lowerText, "results", "experiments");
        if (resultsStart != -1) {
            int resultsEnd = findSectionEnd(lowerText, resultsStart, "discussion", "conclusion");
            sections.put(SectionType.RESULTS, extractSection(fullText, resultsStart, resultsEnd));
        }

        // Classify conclusion
        int conclusionStart = findSectionStart(lowerText, "conclusion", "conclusions");
        if (conclusionStart != -1) {
            int conclusionEnd = findSectionEnd(lowerText, conclusionStart, "references", "acknowledgment");
            sections.put(SectionType.CONCLUSION, extractSection(fullText, conclusionStart, conclusionEnd));
        }

        return sections;
    }

    private int findSectionStart(String text, String... sectionNames) {
        for (String sectionName : sectionNames) {
            Pattern pattern = Pattern.compile("(?m)^\\s*" + Pattern.quote(sectionName) + "\\b", Pattern.CASE_INSENSITIVE);
            var matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.start();
            }
        }
        return -1;
    }

    private int findSectionEnd(String text, int start, String... nextSectionNames) {
        int end = text.length();
        for (String nextSection : nextSectionNames) {
            Pattern pattern = Pattern.compile("(?m)^\\s*" + Pattern.quote(nextSection) + "\\b", Pattern.CASE_INSENSITIVE);
            var matcher = pattern.matcher(text.substring(start));
            if (matcher.find()) {
                end = start + matcher.start();
                break;
            }
        }
        return end;
    }

    private String extractSection(String fullText, int start, int end) {
        if (start != -1 && end > start) {
            return fullText.substring(start, end).trim();
        }
        return "";
    }
}