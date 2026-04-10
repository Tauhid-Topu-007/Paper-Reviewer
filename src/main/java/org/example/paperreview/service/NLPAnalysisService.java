package org.example.paperreview.service;

import org.example.paperreview.model.ExtractedInfo;
import org.example.paperreview.model.SourceHighlight;
import org.example.paperreview.util.TextProcessor;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class NLPAnalysisService {
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private TextProcessor textProcessor;

    public NLPAnalysisService() {
        try {
            // Load NLP models
            InputStream sentenceModelStream = getClass().getResourceAsStream("/models/en-sent.bin");
            if (sentenceModelStream != null) {
                SentenceModel sentenceModel = new SentenceModel(sentenceModelStream);
                sentenceDetector = new SentenceDetectorME(sentenceModel);
            }

            InputStream tokenModelStream = getClass().getResourceAsStream("/models/en-token.bin");
            if (tokenModelStream != null) {
                TokenizerModel tokenModel = new TokenizerModel(tokenModelStream);
                tokenizer = new TokenizerME(tokenModel);
            }

            textProcessor = new TextProcessor();
        } catch (Exception e) {
            System.err.println("Could not load NLP models: " + e.getMessage());
        }
    }

    public ExtractedInfo analyzePaper(String fullText, List<String> pages) {
        ExtractedInfo info = new ExtractedInfo();
        Map<String, List<SourceHighlight>> highlights = new HashMap<>();

        // Extract title (usually first line or line with largest font)
        info.setPaperTitle(extractTitle(fullText, pages));

        // Extract authors
        info.setAuthors(extractAuthors(fullText));

        // Extract research domain
        info.setResearchDomain(extractResearchDomain(fullText));

        // Extract research problem
        info.setResearchProblem(extractResearchProblem(fullText));

        // Extract research gap
        info.setResearchGap(extractResearchGap(fullText));

        // Extract research questions
        info.setResearchQuestions(extractResearchQuestions(fullText));

        // Extract hypothesis
        info.setResearchHypothesis(extractHypothesis(fullText));

        // Extract methodology
        info.setMethodology(extractMethodology(fullText));

        // Extract data collection methods
        info.setDataCollectionMethods(extractDataCollectionMethods(fullText));

        // Extract key findings
        info.setKeyFindings(extractKeyFindings(fullText));

        // Extract citations
        info.setCitations(extractCitations(fullText));

        // Extract publication category
        info.setPublicationCategory(extractPublicationCategory(fullText));

        return info;
    }

    private String extractTitle(String fullText, List<String> pages) {
        // Look for title in first page, typically larger text or first few lines
        if (!pages.isEmpty()) {
            String firstPage = pages.get(0);
            String[] lines = firstPage.split("\\n");
            for (int i = 0; i < Math.min(10, lines.length); i++) {
                String line = lines[i].trim();
                if (line.length() > 20 && line.length() < 200 &&
                        !line.toLowerCase().contains("abstract") &&
                        !line.toLowerCase().contains("keywords")) {
                    return line;
                }
            }
        }
        return "Title not found";
    }

    private String extractAuthors(String fullText) {
        // Look for author patterns in first few pages
        Pattern authorPattern = Pattern.compile("(?i)(?:by|authors?)[:\\s]+([A-Z][a-z]+\\s+[A-Z][a-z]+(?:\\s*,\\s*[A-Z][a-z]+\\s+[A-Z][a-z]+)*)");
        var matcher = authorPattern.matcher(fullText.substring(0, Math.min(5000, fullText.length())));
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Authors not found";
    }

    private String extractResearchDomain(String fullText) {
        String lowerText = fullText.toLowerCase();
        String[] domains = {"machine learning", "artificial intelligence", "computer vision",
                "natural language processing", "data mining", "software engineering",
                "cybersecurity", "networking", "cloud computing", "big data",
                "iot", "blockchain", "robotics", "biomedical", "physics",
                "chemistry", "biology", "mathematics"};

        for (String domain : domains) {
            if (lowerText.contains(domain)) {
                return domain.substring(0, 1).toUpperCase() + domain.substring(1);
            }
        }
        return "Domain not classified";
    }

    private String extractResearchProblem(String fullText) {
        // Look for problem statements
        Pattern[] problemPatterns = {
                Pattern.compile("(?i)(?:problem|challenge|issue|limitation)[\\s:]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:however|but|unfortunately)[\\s:]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:address|solve|tackle)[\\s]+(?:the|this)[\\s]+([^.]+[\\.!?])")
        };

        for (Pattern pattern : problemPatterns) {
            var matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                String problem = matcher.group(1).trim();
                if (problem.length() > 50 && problem.length() < 500) {
                    return problem;
                }
            }
        }
        return "Research problem not explicitly stated";
    }

    private String extractResearchGap(String fullText) {
        // Look for gap statements
        Pattern[] gapPatterns = {
                Pattern.compile("(?i)(?:gap|missing|lack of|insufficient|limited research)[\\s:]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:little is known|not been studied|remains unclear)[\\s:]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(:(?:to the best of our knowledge|however)[^.]+[\\.!?])")
        };

        for (Pattern pattern : gapPatterns) {
            var matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                String gap = matcher.group(1).trim();
                if (gap.length() > 50 && gap.length() < 400) {
                    return gap;
                }
            }
        }
        return "Research gap not explicitly identified";
    }

    private List<String> extractResearchQuestions(String fullText) {
        List<String> questions = new ArrayList<>();

        // Look for question patterns
        Pattern questionPattern = Pattern.compile("(?i)(?:RQ\\d+|research question|question)[\\s:\\-]+([^?]+\\?)");
        var matcher = questionPattern.matcher(fullText);

        while (matcher.find() && questions.size() < 5) {
            String question = matcher.group(1).trim();
            if (question.length() > 20 && question.length() < 300) {
                questions.add(question);
            }
        }

        if (questions.isEmpty()) {
            questions.add("No explicit research questions found");
        }
        return questions;
    }

    private String extractHypothesis(String fullText) {
        Pattern[] hypothesisPatterns = {
                Pattern.compile("(?i)(?:hypothesis|h\\d+)[\\s:\\-]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:we hypothesize|propose|postulate)[\\s]+that[\\s]+([^.]+[\\.!?])")
        };

        for (Pattern pattern : hypothesisPatterns) {
            var matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                String hypothesis = matcher.group(1).trim();
                if (hypothesis.length() > 30 && hypothesis.length() < 300) {
                    return hypothesis;
                }
            }
        }
        return "No explicit hypothesis stated";
    }

    private String extractMethodology(String fullText) {
        // Look for methodology section
        String methodologySection = extractSection(fullText, "methodology", "results", "experiment");
        if (!methodologySection.isEmpty()) {
            String[] sentences = methodologySection.split("[.!?]");
            StringBuilder methodology = new StringBuilder();
            for (int i = 0; i < Math.min(5, sentences.length); i++) {
                methodology.append(sentences[i].trim()).append(". ");
            }
            return methodology.toString();
        }
        return "Methodology not explicitly described";
    }

    private String extractDataCollectionMethods(String fullText) {
        Pattern[] collectionPatterns = {
                Pattern.compile("(?i)(?:data collection|collected data|gathered data)[\\s:]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:survey|questionnaire|interview|experiment|dataset|corpus)[\\s:]+([^.]+[\\.!?])")
        };

        for (Pattern pattern : collectionPatterns) {
            var matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "Data collection method not specified";
    }

    private List<String> extractKeyFindings(String fullText) {
        List<String> findings = new ArrayList<>();

        // Look for findings in results/conclusion sections
        String resultsSection = extractSection(fullText, "results", "conclusion", "discussion");

        Pattern[] findingPatterns = {
                Pattern.compile("(?i)(?:finding|result|observation|discovery)[\\s:\\-]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:we found|we observe|we discover)[\\s]+that[\\s]+([^.]+[\\.!?])"),
                Pattern.compile("(?i)(?:shows|demonstrates|indicates|reveals)[\\s]+that[\\s]+([^.]+[\\.!?])")
        };

        for (Pattern pattern : findingPatterns) {
            var matcher = pattern.matcher(resultsSection);
            while (matcher.find() && findings.size() < 7) {
                String finding = matcher.group(1).trim();
                if (finding.length() > 30 && finding.length() < 300 && !findings.contains(finding)) {
                    findings.add(finding);
                }
            }
        }

        if (findings.isEmpty()) {
            findings.add("Key findings not explicitly listed");
        }
        return findings;
    }

    private List<String> extractCitations(String fullText) {
        List<String> citations = new ArrayList<>();

        // Look for citation patterns
        Pattern citationPattern = Pattern.compile("\\[\\d+\\]|\\[([A-Za-z]+\\s+\\d{4})\\]|\\(([A-Za-z]+\\s+\\d{4})\\)");
        var matcher = citationPattern.matcher(fullText);

        Set<String> uniqueCitations = new HashSet<>();
        while (matcher.find() && uniqueCitations.size() < 20) {
            String citation = matcher.group();
            uniqueCitations.add(citation);
        }

        citations.addAll(uniqueCitations);
        return citations;
    }

    private String extractPublicationCategory(String fullText) {
        String lowerText = fullText.toLowerCase();
        if (lowerText.contains("elsevier")) return "Elsevier";
        if (lowerText.contains("springer")) return "Springer";
        if (lowerText.contains("ieee")) return "IEEE";
        if (lowerText.contains("acm")) return "ACM";
        if (lowerText.contains("scopus")) {
            if (lowerText.contains("q1")) return "Scopus Q1";
            if (lowerText.contains("q2")) return "Scopus Q2";
            if (lowerText.contains("q3")) return "Scopus Q3";
            if (lowerText.contains("q4")) return "Scopus Q4";
            return "Scopus (Unspecified)";
        }
        return "Not specified";
    }

    private String extractSection(String fullText, String sectionName, String endMarker, String alternativeMarker) {
        String lowerText = fullText.toLowerCase();
        String lowerSection = sectionName.toLowerCase();

        int startIndex = lowerText.indexOf(lowerSection);
        if (startIndex == -1) {
            return "";
        }

        int endIndex = lowerText.indexOf(endMarker.toLowerCase(), startIndex + lowerSection.length());
        if (endIndex == -1 && alternativeMarker != null) {
            endIndex = lowerText.indexOf(alternativeMarker.toLowerCase(), startIndex + lowerSection.length());
        }

        if (endIndex == -1) {
            endIndex = Math.min(startIndex + 3000, fullText.length());
        }

        return fullText.substring(startIndex, endIndex);
    }

    public String generateSummary(String fullText, ExtractedInfo info) {
        StringBuilder summary = new StringBuilder();

        summary.append("This research paper titled \"").append(info.getPaperTitle()).append("\" ");

        if (!info.getAuthors().equals("Authors not found")) {
            summary.append("by ").append(info.getAuthors()).append(" ");
        }

        summary.append("addresses ").append(info.getResearchProblem().toLowerCase()).append(" ");

        if (!info.getResearchGap().equals("Research gap not explicitly identified")) {
            summary.append("The research gap identified is: ").append(info.getResearchGap()).append(" ");
        }

        summary.append("The study employs ").append(info.getMethodology().toLowerCase()).append(" ");

        if (!info.getKeyFindings().isEmpty() && !info.getKeyFindings().get(0).equals("Key findings not explicitly listed")) {
            summary.append("Key findings include: ").append(info.getKeyFindings().get(0));
            for (int i = 1; i < Math.min(3, info.getKeyFindings().size()); i++) {
                summary.append("; ").append(info.getKeyFindings().get(i));
            }
        }

        return summary.toString();
    }

    public List<String> identifyStrengths(String fullText, ExtractedInfo info) {
        List<String> strengths = new ArrayList<>();

        // Look for strength indicators
        String lowerText = fullText.toLowerCase();

        if (info.getMethodology() != null && !info.getMethodology().contains("not explicitly")) {
            strengths.add("Clear and well-defined methodology section");
        }

        if (info.getKeyFindings().size() >= 3) {
            strengths.add("Multiple significant findings presented");
        }

        if (info.getCitations().size() > 20) {
            strengths.add("Comprehensive literature review with extensive citations");
        }

        if (lowerText.contains("novel") || lowerText.contains("innovative")) {
            strengths.add("Novel/Innovative approach presented");
        }

        if (lowerText.contains("comprehensive") || lowerText.contains("extensive")) {
            strengths.add("Comprehensive analysis performed");
        }

        if (strengths.isEmpty()) {
            strengths.add("Basic research structure followed");
        }

        return strengths;
    }

    public List<String> identifyWeaknesses(String fullText, ExtractedInfo info) {
        List<String> weaknesses = new ArrayList<>();

        // Look for weakness indicators
        if (info.getMethodology().contains("not explicitly")) {
            weaknesses.add("Methodology not clearly described");
        }

        if (info.getDataCollectionMethods().contains("not specified")) {
            weaknesses.add("Data collection method not specified");
        }

        if (info.getKeyFindings().isEmpty() || info.getKeyFindings().get(0).equals("Key findings not explicitly listed")) {
            weaknesses.add("Key findings not clearly articulated");
        }

        String lowerText = fullText.toLowerCase();
        if (lowerText.contains("limitation") || lowerText.contains("future work")) {
            Pattern limitPattern = Pattern.compile("(?i)(?:limitation|future work)[^.]+[.!?]");
            var matcher = limitPattern.matcher(fullText);
            if (matcher.find()) {
                String limitation = matcher.group();
                if (limitation.length() < 200) {
                    weaknesses.add(limitation);
                }
            }
        }

        if (weaknesses.isEmpty()) {
            weaknesses.add("No explicit limitations mentioned in the paper");
        }

        return weaknesses;
    }

    public List<String> identifyFutureScope(String fullText, ExtractedInfo info) {
        List<String> futureScope = new ArrayList<>();

        Pattern futurePattern = Pattern.compile("(?i)(?:future work|future research|further study|future direction)[^.]+[.!?]");
        var matcher = futurePattern.matcher(fullText);

        while (matcher.find() && futureScope.size() < 5) {
            String scope = matcher.group().trim();
            if (scope.length() > 30 && scope.length() < 300) {
                futureScope.add(scope);
            }
        }

        if (futureScope.isEmpty()) {
            futureScope.add("Future research directions not specified");
        }

        return futureScope;
    }

    public double calculateQualityScore(ExtractedInfo info, List<String> strengths, List<String> weaknesses) {
        double score = 70.0; // Base score

        // Adjust based on extracted info completeness
        if (!info.getPaperTitle().contains("not found")) score += 2;
        if (!info.getAuthors().contains("not found")) score += 2;
        if (!info.getResearchProblem().contains("not")) score += 5;
        if (!info.getResearchGap().contains("not")) score += 5;
        if (!info.getResearchQuestions().get(0).contains("not")) score += 3;
        if (!info.getMethodology().contains("not")) score += 5;
        if (info.getKeyFindings().size() >= 3 && !info.getKeyFindings().get(0).contains("not")) score += 5;
        if (info.getCitations().size() > 15) score += 3;

        // Adjust based on strengths/weaknesses
        score += strengths.size() * 2;
        score -= weaknesses.size() * 1.5;

        return Math.min(100, Math.max(0, score));
    }

    public String assessWritingQuality(String fullText) {
        String[] sentences = fullText.split("[.!?]");
        double avgSentenceLength = 0;
        for (String sentence : sentences) {
            avgSentenceLength += sentence.trim().split("\\s+").length;
        }
        avgSentenceLength /= sentences.length;

        if (avgSentenceLength < 15) {
            return "Good: Clear and concise writing style with short sentences";
        } else if (avgSentenceLength < 25) {
            return "Satisfactory: Moderate sentence length, generally readable";
        } else {
            return "Needs improvement: Sentences are too long, consider breaking them down";
        }
    }

    public String assessPlagiarismRisk(String fullText) {
        // This is a heuristic assessment, not actual plagiarism detection
        String lowerText = fullText.toLowerCase();

        int suspiciousPatterns = 0;

        // Check for overly common phrases
        String[] commonPhrases = {"et al", "as shown in", "previous studies have shown",
                "it is well known that", "according to"};
        for (String phrase : commonPhrases) {
            if (lowerText.contains(phrase)) {
                suspiciousPatterns++;
            }
        }

        if (suspiciousPatterns > 10) {
            return "Medium Risk: Contains many common academic phrases that might indicate template-based writing";
        } else if (suspiciousPatterns > 5) {
            return "Low Risk: Generally original writing with standard academic language";
        } else {
            return "Low Risk: Appears to be original work";
        }
    }
}
