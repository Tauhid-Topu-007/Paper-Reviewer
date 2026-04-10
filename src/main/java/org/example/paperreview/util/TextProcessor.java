package org.example.paperreview.util;

import java.util.*;
import java.util.regex.Pattern;

public class TextProcessor {

    public String cleanText(String text) {
        // Remove extra whitespace
        text = text.replaceAll("\\s+", " ");
        // Remove non-printable characters
        text = text.replaceAll("[^\\x20-\\x7E\\n\\r\\t]", "");
        return text.trim();
    }

    public List<String> extractKeywords(String text, int limit) {
        Map<String, Integer> wordFreq = new HashMap<>();

        // Simple keyword extraction based on frequency
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 3 && !isStopWord(word)) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordFreq.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            keywords.add(sorted.get(i).getKey());
        }

        return keywords;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at",
                "to", "for", "of", "with", "by", "this", "that", "these",
                "those", "is", "are", "was", "were", "be", "been", "being",
                "have", "has", "had", "having", "do", "does", "did", "doing");
        return stopWords.contains(word);
    }

    public List<String> extractSentences(String text) {
        Pattern sentencePattern = Pattern.compile("[^.!?]+[.!?]");
        var matcher = sentencePattern.matcher(text);
        List<String> sentences = new ArrayList<>();
        while (matcher.find()) {
            sentences.add(matcher.group().trim());
        }
        return sentences;
    }

    public boolean containsAcademicTerms(String text) {
        String[] academicTerms = {"study", "research", "paper", "method", "result", "finding",
                "analysis", "evaluation", "experiment", "hypothesis", "theory"};
        text = text.toLowerCase();
        for (String term : academicTerms) {
            if (text.contains(term)) {
                return true;
            }
        }
        return false;
    }
}