package org.example.paperreview.service;

import org.example.paperreview.model.ExtractedInfo;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.BreakIterator;

public class NLPAnalysisService {

    // Domain-specific keywords
    private static final Set<String> DOMAIN_KEYWORDS = Set.of(
            "machine learning", "deep learning", "artificial intelligence", "neural network",
            "data mining", "big data", "cloud computing", "internet of things",
            "cybersecurity", "blockchain", "computer vision", "natural language processing",
            "software engineering", "web development", "mobile computing", "distributed systems",
            "database", "information retrieval", "human-computer interaction", "robotics"
    );

    // Methodology keywords
    private static final Set<String> METHODOLOGY_KEYWORDS = Set.of(
            "qualitative", "quantitative", "mixed method", "case study", "survey",
            "experiment", "simulation", "literature review", "systematic review",
            "action research", "design science", "grounded theory", "ethnography"
    );

    public ExtractedInfo analyzePaper(String fullText, List<String> pages) {
        ExtractedInfo info = new ExtractedInfo();

        if (fullText == null || fullText.isEmpty()) {
            return info;
        }

        // Extract basic information
        info.setPaperTitle(extractTitle(fullText));
        info.setAuthors(extractAuthors(fullText));
        info.setAbstractText(extractAbstract(fullText));
        info.setKeywords(extractKeywords(fullText));
        info.setPublicationYear(extractYear(fullText));
        info.setPublicationVenue(extractVenue(fullText));
        info.setDoi(extractDOI(fullText));
        info.setCorrespondingAuthor(extractCorrespondingAuthor(fullText));
        info.setAffiliations(extractAffiliations(fullText));

        // Extract research components
        info.setResearchDomain(extractResearchDomain(fullText));
        info.setResearchProblem(extractResearchProblem(fullText));
        info.setResearchGap(extractResearchGap(fullText));
        info.setResearchQuestions(extractResearchQuestions(fullText));
        info.setResearchHypothesis(extractHypothesis(fullText));
        info.setResearchObjectives(extractObjectives(fullText));
        info.setContributions(extractContributions(fullText));

        // Extract methodology
        info.setMethodology(extractMethodology(fullText));
        info.setDataCollectionMethods(extractDataCollectionMethods(fullText));
        info.setSampleSize(extractSampleSize(fullText));
        info.setDataAnalysisMethods(extractDataAnalysisMethods(fullText));
        info.setToolsAndTechnologies(extractToolsAndTechnologies(fullText));
        info.setExperimentalSetup(extractExperimentalSetup(fullText));
        info.setEvaluationMetrics(extractEvaluationMetrics(fullText));

        // Extract results
        info.setKeyFindings(extractKeyFindings(fullText));
        info.setResults(extractResults(fullText));
        info.setStatisticalResults(extractStatisticalResults(fullText));
        info.setPerformanceMetrics(extractPerformanceMetrics(fullText));

        // Extract analysis
        info.setDiscussion(extractDiscussion(fullText));
        info.setLimitations(extractLimitations(fullText));
        info.setFutureWork(extractFutureWork(fullText));
        info.setPracticalImplications(extractPracticalImplications(fullText));
        info.setTheoreticalImplications(extractTheoreticalImplications(fullText));

        // Extract references
        info.setCitations(extractCitations(fullText));
        info.setTotalReferences(calculateTotalReferences(fullText));
        info.setCitationDistribution(analyzeCitationDistribution(fullText));
        info.setHighlyCitedPapers(extractHighlyCitedPapers(fullText));

        // Extract metadata
        info.setPageCount(pages != null ? pages.size() : 0);
        info.setFigureCount(countFigures(fullText));
        info.setTableCount(countTables(fullText));
        info.setEquationCount(countEquations(fullText));
        info.setAverageSentenceLength(calculateAverageSentenceLength(fullText));
        info.setReadabilityScore(calculateReadabilityScore(fullText));
        info.setLanguageComplexity(assessLanguageComplexity(fullText));
        info.setKeyTerms(extractKeyTerms(fullText));
        info.setTermFrequency(calculateTermFrequency(fullText));

        return info;
    }

    private String extractTitle(String text) {
        // Look for common title patterns
        String[] lines = text.split("\\n");
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            String line = lines[i].trim();
            if (line.length() > 10 && line.length() < 200 &&
                    !line.toLowerCase().contains("abstract") &&
                    !line.toLowerCase().contains("introduction")) {
                return line;
            }
        }
        return "Title not found";
    }

    private String extractAuthors(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:author|by)[:\\s]+([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*(?:,\\s*[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Authors not found";
    }

    private String extractAbstract(String text) {
        Pattern pattern = Pattern.compile("(?i)abstract[\\s\\n]+(.+?)(?=\\n\\s*(?:introduction|keywords|1\\.|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String abstractText = matcher.group(1).trim();
            return abstractText.length() > 500 ? abstractText.substring(0, 500) + "..." : abstractText;
        }
        return "Abstract not found";
    }

    private String extractKeywords(String text) {
        Pattern pattern = Pattern.compile("(?i)keywords?[\\s:]+(.+?)(?=\\n|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Keywords not found";
    }

    private String extractYear(String text) {
        Pattern pattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "Year not found";
    }

    private String extractVenue(String text) {
        String[] venues = {"IEEE", "ACM", "Springer", "Elsevier", "MDPI", "Wiley", "Taylor & Francis",
                "CVPR", "ICCV", "ICML", "NeurIPS", "ACL", "EMNLP", "NAACL", "AAAI", "IJCAI"};
        for (String venue : venues) {
            if (text.contains(venue)) {
                return venue;
            }
        }
        return "Venue not specified";
    }

    private String extractDOI(String text) {
        Pattern pattern = Pattern.compile("10\\.\\d{4,9}/[-._;()/:A-Z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "DOI not found";
    }

    private String extractCorrespondingAuthor(String text) {
        Pattern pattern = Pattern.compile("(?i)corresponding author[\\s:]+([^\\n,]+(?:\\s+[^\\n,]+)*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Not specified";
    }

    private String extractAffiliations(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:affiliation|institution|university)[\\s:]+([^\\n.]+(?:\\s+[^\\n.]+)*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Not specified";
    }

    private String extractResearchDomain(String text) {
        String lowerText = text.toLowerCase();
        for (String domain : DOMAIN_KEYWORDS) {
            if (lowerText.contains(domain)) {
                return domain;
            }
        }
        return "Not classified";
    }

    private String extractResearchProblem(String text) {
        String[] patterns = {
                "(?i)(?:problem|challenge|issue|gap)[\\s:]+(.+?)(?=\\.\\s+[A-Z])",
                "(?i)(?:this paper addresses|this work addresses|we address)[\\s:]+(.+?)(?=\\.)"
        };

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String problem = matcher.group(1).trim();
                return problem.length() > 300 ? problem.substring(0, 300) + "..." : problem;
            }
        }
        return "Research problem not clearly stated";
    }

    private String extractResearchGap(String text) {
        String[] patterns = {
                "(?i)(?:research gap|gap in the literature|limited research|little is known)[\\s:]+(.+?)(?=\\.\\s+[A-Z])",
                "(?i)(?:however|nevertheless|despite|although)[\\s,]+(.+?)(?=\\.)"
        };

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "Research gap not explicitly stated";
    }

    private List<String> extractResearchQuestions(String text) {
        List<String> questions = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(?:RQ\\d+|research question \\d+)[\\s:]+(.+?)(?=\\.|\\n)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            questions.add(matcher.group(1).trim());
        }

        if (questions.isEmpty()) {
            Pattern altPattern = Pattern.compile("(?i)(?:we ask|we investigate|we explore)[\\s:]+(.+?)(?=\\.|\\?)");
            Matcher altMatcher = altPattern.matcher(text);
            while (altMatcher.find() && questions.size() < 3) {
                questions.add(altMatcher.group(1).trim());
            }
        }

        return questions.isEmpty() ? List.of("Research questions not explicitly stated") : questions;
    }

    private String extractHypothesis(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:hypothesis|H\\d+)[\\s:]+(.+?)(?=\\.)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No explicit hypothesis stated";
    }

    private String extractObjectives(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:objective|aim|goal|purpose)[\\s:]+(.+?)(?=\\.\\s+[A-Z])");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Objectives not clearly stated";
    }

    private String extractContributions(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:contribution|main contribution|key contribution)[\\s:]+(.+?)(?=\\.\\s+[A-Z])");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Contributions not explicitly stated";
    }

    private String extractMethodology(String text) {
        for (String method : METHODOLOGY_KEYWORDS) {
            Pattern pattern = Pattern.compile("(?i)\\b" + method + "\\b.*?\\.", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String sentence = matcher.group();
                return sentence.length() > 200 ? sentence.substring(0, 200) + "..." : sentence;
            }
        }
        return "Methodology not clearly described";
    }

    private String extractDataCollectionMethods(String text) {
        String[] methods = {"survey", "questionnaire", "interview", "focus group", "observation",
                "experiment", "simulation", "case study", "dataset", "data collection"};

        for (String method : methods) {
            Pattern pattern = Pattern.compile("(?i)\\b" + method + "\\b[^.]+\\.");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        }
        return "Data collection method not specified";
    }

    private String extractSampleSize(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:sample size|n\\s*=\\s*|participants?|subjects?)\\s*(?:of\\s*)?(\\d+)(?:\\s*participants?)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Sample size not specified";
    }

    private String extractDataAnalysisMethods(String text) {
        String[] methods = {"t-test", "ANOVA", "regression", "correlation", "chi-square",
                "factor analysis", "cluster analysis", "thematic analysis",
                "content analysis", "descriptive statistics"};

        for (String method : methods) {
            if (text.toLowerCase().contains(method)) {
                return method;
            }
        }
        return "Analysis method not specified";
    }

    private String extractToolsAndTechnologies(String text) {
        String[] tools = {"Python", "Java", "C++", "MATLAB", "R", "SPSS", "SAS", "WEKA",
                "TensorFlow", "PyTorch", "Keras", "scikit-learn", "NLTK", "OpenCV"};

        List<String> foundTools = new ArrayList<>();
        for (String tool : tools) {
            if (text.contains(tool)) {
                foundTools.add(tool);
            }
        }

        return foundTools.isEmpty() ? "Not specified" : String.join(", ", foundTools);
    }

    private String extractExperimentalSetup(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:experimental setup|experiment design|experimental configuration)[\\s:]+(.+?)(?=\\.\\s+[A-Z])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Experimental setup not described";
    }

    private String extractEvaluationMetrics(String text) {
        String[] metrics = {"accuracy", "precision", "recall", "F1-score", "AUC", "ROC",
                "MSE", "MAE", "RMSE", "R-squared", "silhouette score", "perplexity"};

        List<String> foundMetrics = new ArrayList<>();
        for (String metric : metrics) {
            if (text.toLowerCase().contains(metric)) {
                foundMetrics.add(metric);
            }
        }

        return foundMetrics.isEmpty() ? "Not specified" : String.join(", ", foundMetrics);
    }

    private List<String> extractKeyFindings(String text) {
        List<String> findings = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(?:finding|result|show|demonstrate|indicate|reveal)[\\s:]+(.+?)(?=\\.\\s+[A-Z])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && findings.size() < 5) {
            String finding = matcher.group(1).trim();
            if (finding.length() > 20 && finding.length() < 200) {
                findings.add(finding);
            }
        }

        return findings.isEmpty() ? List.of("Key findings not clearly presented") : findings;
    }

    private List<String> extractResults(String text) {
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(?:result|outcome)\\s+\\d+[\\s:]+(.+?)(?=\\.)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && results.size() < 5) {
            results.add(matcher.group(1).trim());
        }

        return results.isEmpty() ? List.of("Detailed results not presented") : results;
    }

    private List<String> extractStatisticalResults(String text) {
        List<String> stats = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b[pP]\\s*[=<]\\s*0\\.\\d+\\b|\\b[tT]\\s*=\\s*\\d+\\.\\d+\\b|\\b[Ff]\\s*=\\s*\\d+\\.\\d+\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && stats.size() < 10) {
            stats.add(matcher.group());
        }

        return stats;
    }

    private List<Double> extractPerformanceMetrics(String text) {
        List<Double> metrics = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)%?\\s*(?:accuracy|precision|recall|f1)");
        Matcher matcher = pattern.matcher(text.toLowerCase());

        while (matcher.find() && metrics.size() < 10) {
            try {
                metrics.add(Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException e) {
                // Skip invalid numbers
            }
        }

        return metrics;
    }

    private String extractDiscussion(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:discussion|analysis|interpretation)[\\s\\n]+(.+?)(?=\\n\\s*(?:conclusion|limitation|future work|\\d+\\.|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String discussion = matcher.group(1).trim();
            return discussion.length() > 500 ? discussion.substring(0, 500) + "..." : discussion;
        }
        return "Discussion section not found";
    }

    private String extractLimitations(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:limitation|constraint|shortcoming|drawback)[\\s:]+(.+?)(?=\\.\\s+[A-Z])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Limitations not explicitly stated";
    }

    private String extractFutureWork(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:future work|future research|future direction|further research)[\\s:]+(.+?)(?=\\.\\s+[A-Z]|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Future work not discussed";
    }

    private List<String> extractPracticalImplications(String text) {
        List<String> implications = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(?:practical implication|application|real-world impact)[\\s:]+(.+?)(?=\\.)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && implications.size() < 3) {
            implications.add(matcher.group(1).trim());
        }

        return implications;
    }

    private List<String> extractTheoreticalImplications(String text) {
        List<String> implications = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(?:theoretical implication|contribution to theory)[\\s:]+(.+?)(?=\\.)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && implications.size() < 3) {
            implications.add(matcher.group(1).trim());
        }

        return implications;
    }

    private List<String> extractCitations(String text) {
        List<String> citations = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[\\d+\\]|\\[[A-Za-z]+\\d{4}\\]|\\([A-Za-z]+\\s+\\d{4}\\)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            citations.add(matcher.group());
        }

        return citations;
    }

    private int calculateTotalReferences(String text) {
        Pattern pattern = Pattern.compile("(?:references|bibliography)[\\s\\n]+(.+?)(?=\\n\\s*(?:appendix|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text.toLowerCase());

        if (matcher.find()) {
            String refSection = matcher.group(1);
            String[] lines = refSection.split("\\n");
            int count = 0;
            for (String line : lines) {
                if (line.trim().matches("^\\[?\\d+\\]?\\s+.*")) {
                    count++;
                }
            }
            return count > 0 ? count : lines.length;
        }
        return 0;
    }

    private Map<String, Integer> analyzeCitationDistribution(String text) {
        Map<String, Integer> distribution = new HashMap<>();
        String[] years = {"2020", "2021", "2022", "2023", "2024"};

        for (String year : years) {
            Pattern pattern = Pattern.compile(year);
            Matcher matcher = pattern.matcher(text);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            if (count > 0) {
                distribution.put(year, count);
            }
        }

        return distribution;
    }

    private List<String> extractHighlyCitedPapers(String text) {
        List<String> papers = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?:cited by|references?)\\s+(\\d+)\\s*(?:papers?|works?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find() && papers.size() < 5) {
            papers.add(matcher.group());
        }

        return papers;
    }

    private int countFigures(String text) {
        Pattern pattern = Pattern.compile("(?i)figure\\s+\\d+");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int countTables(String text) {
        Pattern pattern = Pattern.compile("(?i)table\\s+\\d+");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int countEquations(String text) {
        Pattern pattern = Pattern.compile("(?i)equation\\s+\\(?\\d+\\)?");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private double calculateAverageSentenceLength(String text) {
        String[] sentences = text.split("[.!?]+");
        if (sentences.length == 0) return 0;

        int totalWords = 0;
        for (String sentence : sentences) {
            totalWords += sentence.trim().split("\\s+").length;
        }

        return (double) totalWords / sentences.length;
    }

    private double calculateReadabilityScore(String text) {
        // Simplified Flesch Reading Ease calculation
        String[] sentences = text.split("[.!?]+");
        String[] words = text.split("\\s+");
        String[] syllables = text.split("[aeiouAEIOU]+");

        if (sentences.length == 0 || words.length == 0) return 0;

        double avgSentenceLength = (double) words.length / sentences.length;
        double avgSyllablesPerWord = (double) syllables.length / words.length;

        // Flesch Reading Ease formula
        double score = 206.835 - (1.015 * avgSentenceLength) - (84.6 * avgSyllablesPerWord);
        return Math.max(0, Math.min(100, score));
    }

    private String assessLanguageComplexity(String text) {
        double avgSentenceLength = calculateAverageSentenceLength(text);
        double readability = calculateReadabilityScore(text);

        if (avgSentenceLength > 25 && readability < 30) {
            return "Very Complex (Expert level)";
        } else if (avgSentenceLength > 20 && readability < 50) {
            return "Complex (Graduate level)";
        } else if (avgSentenceLength > 15 && readability < 70) {
            return "Moderate (Undergraduate level)";
        } else {
            return "Simple (General audience)";
        }
    }

    private List<String> extractKeyTerms(String text) {
        List<String> keyTerms = new ArrayList<>();
        String lowerText = text.toLowerCase();

        // Technical terms to look for
        String[] technicalTerms = {
                "algorithm", "framework", "model", "system", "approach", "method", "technique",
                "analysis", "evaluation", "validation", "optimization", "classification",
                "regression", "clustering", "prediction", "detection", "recognition"
        };

        for (String term : technicalTerms) {
            if (lowerText.contains(term) && !keyTerms.contains(term)) {
                keyTerms.add(term);
            }
        }

        return keyTerms.size() > 10 ? keyTerms.subList(0, 10) : keyTerms;
    }

    private Map<String, Integer> calculateTermFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");

        // Common stop words to ignore
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
                "of", "with", "by", "is", "are", "was", "were", "be", "been", "being");

        for (String word : words) {
            word = word.replaceAll("[^a-z]", "");
            if (word.length() > 3 && !stopWords.contains(word)) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        // Keep only top 30 terms
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(30)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    // Existing methods from your original service...
    public String generateSummary(String fullText, ExtractedInfo info) {
        StringBuilder summary = new StringBuilder();
        summary.append("This paper presents research in the field of ").append(info.getResearchDomain()).append(". ");
        summary.append("The study addresses ").append(info.getResearchProblem()).append(". ");

        if (info.getResearchGap() != null && !info.getResearchGap().equals("Research gap not explicitly stated")) {
            summary.append("It addresses the research gap: ").append(info.getResearchGap()).append(". ");
        }

        summary.append("The methodology employs ").append(info.getMethodology()).append(". ");

        List<String> keyFindings = info.getKeyFindings();
        if (!keyFindings.isEmpty() && !keyFindings.get(0).equals("Key findings not clearly presented")) {
            summary.append("Key findings include: ");
            for (int i = 0; i < Math.min(3, keyFindings.size()); i++) {
                summary.append(keyFindings.get(i));
                if (i < Math.min(3, keyFindings.size()) - 1) summary.append("; ");
            }
            summary.append(". ");
        }

        return summary.toString();
    }

    public List<String> identifyStrengths(String fullText, ExtractedInfo info) {
        List<String> strengths = new ArrayList<>();

        // Check for various strength indicators
        if (info.getSampleSize() != null && !info.getSampleSize().equals("Sample size not specified")) {
            try {
                int sampleSize = Integer.parseInt(info.getSampleSize());
                if (sampleSize > 100) {
                    strengths.add("Large sample size (" + sampleSize + " participants)");
                }
            } catch (NumberFormatException e) {
                // Not a number, skip
            }
        }

        if (!info.getEvaluationMetrics().equals("Not specified")) {
            strengths.add("Comprehensive evaluation using " + info.getEvaluationMetrics());
        }

        if (!info.getToolsAndTechnologies().equals("Not specified")) {
            strengths.add("Uses modern tools and technologies: " + info.getToolsAndTechnologies());
        }

        if (!info.getStatisticalResults().isEmpty()) {
            strengths.add("Provides detailed statistical analysis");
        }

        if (info.getFigureCount() > 5) {
            strengths.add("Well-illustrated with " + info.getFigureCount() + " figures");
        }

        if (info.getTableCount() > 3) {
            strengths.add("Clear presentation of data through " + info.getTableCount() + " tables");
        }

        if (info.getTotalReferences() > 50) {
            strengths.add("Comprehensive literature review with " + info.getTotalReferences() + " references");
        }

        if (strengths.isEmpty()) {
            strengths.add("Strengths not clearly identified in the paper");
        }

        return strengths;
    }

    public List<String> identifyWeaknesses(String fullText, ExtractedInfo info) {
        List<String> weaknesses = new ArrayList<>();

        if (info.getSampleSize().equals("Sample size not specified")) {
            weaknesses.add("Sample size not clearly stated");
        } else if (info.getSampleSize().matches("\\d+")) {
            try {
                int sampleSize = Integer.parseInt(info.getSampleSize());
                if (sampleSize < 30) {
                    weaknesses.add("Small sample size (" + sampleSize + " participants) may limit generalizability");
                }
            } catch (NumberFormatException e) {
                // Not a number
            }
        }

        if (info.getLimitations() != null && !info.getLimitations().equals("Limitations not explicitly stated")) {
            weaknesses.add("Acknowledges limitations: " + info.getLimitations());
        } else {
            weaknesses.add("Limitations not explicitly discussed");
        }

        if (info.getEvaluationMetrics().equals("Not specified")) {
            weaknesses.add("Evaluation metrics not clearly defined");
        }

        if (info.getDataCollectionMethods().equals("Data collection method not specified")) {
            weaknesses.add("Data collection method not clearly described");
        }

        if (info.getStatisticalResults().isEmpty()) {
            weaknesses.add("Limited statistical analysis presented");
        }

        if (info.getTotalReferences() < 20) {
            weaknesses.add("Limited literature review (" + info.getTotalReferences() + " references)");
        }

        if (weaknesses.isEmpty()) {
            weaknesses.add("Weaknesses not clearly identified in the paper");
        }

        return weaknesses;
    }

    public List<String> identifyFutureScope(String fullText, ExtractedInfo info) {
        List<String> futureScope = new ArrayList<>();

        if (info.getFutureWork() != null && !info.getFutureWork().equals("Future work not discussed")) {
            futureScope.add(info.getFutureWork());
        } else {
            futureScope.add("Future research directions not explicitly discussed");
        }

        // Suggest additional future directions based on limitations
        if (info.getLimitations() != null && !info.getLimitations().equals("Limitations not explicitly stated")) {
            futureScope.add("Address limitations: " + info.getLimitations());
        }

        return futureScope;
    }

    public double calculateQualityScore(ExtractedInfo info, List<String> strengths, List<String> weaknesses) {
        double score = 50.0; // Base score

        // Add points for strengths
        score += strengths.size() * 5;

        // Subtract points for weaknesses
        score -= weaknesses.size() * 3;

        // Add points for methodology clarity
        if (!info.getMethodology().equals("Methodology not clearly described")) {
            score += 10;
        }

        // Add points for results presentation
        if (!info.getKeyFindings().isEmpty() && !info.getKeyFindings().get(0).equals("Key findings not clearly presented")) {
            score += 10;
        }

        // Add points for references
        if (info.getTotalReferences() > 30) {
            score += 5;
        } else if (info.getTotalReferences() > 50) {
            score += 10;
        }

        // Add points for figures and tables
        if (info.getFigureCount() + info.getTableCount() > 5) {
            score += 5;
        }

        // Cap at 100
        return Math.min(100, Math.max(0, score));
    }

    public String assessWritingQuality(String fullText) {
        double readability = calculateReadabilityScore(fullText);
        double avgSentenceLength = calculateAverageSentenceLength(fullText);

        StringBuilder assessment = new StringBuilder();

        if (readability >= 60) {
            assessment.append("Writing is clear and accessible. ");
        } else if (readability >= 40) {
            assessment.append("Writing is moderately complex but readable. ");
        } else {
            assessment.append("Writing is highly complex and may be difficult for some readers. ");
        }

        if (avgSentenceLength < 15) {
            assessment.append("Sentences are concise and easy to follow.");
        } else if (avgSentenceLength < 25) {
            assessment.append("Sentence length is appropriate for academic writing.");
        } else {
            assessment.append("Sentences are quite long and could be broken down for better readability.");
        }

        return assessment.toString();
    }

    public String assessPlagiarismRisk(String fullText) {
        // This is a simplified check - in production, you'd use actual plagiarism detection APIs
        String lowerText = fullText.toLowerCase();

        // Check for common academic phrases that might indicate proper citation
        boolean hasCitations = lowerText.contains("et al") || lowerText.contains("according to") ||
                lowerText.contains("as stated in") || lowerText.contains("reference");

        if (!hasCitations) {
            return "High risk - Limited citations detected";
        } else if (lowerText.contains("cited by") || lowerText.contains("references")) {
            return "Low risk - Proper citation practice observed";
        } else {
            return "Medium risk - Some citations present but limited";
        }
    }

    public String assessNovelty(String fullText, ExtractedInfo info) {
        StringBuilder novelty = new StringBuilder();

        if (info.getResearchGap() != null && !info.getResearchGap().equals("Research gap not explicitly stated")) {
            novelty.append("Paper addresses a research gap. ");
        }

        if (info.getContributions() != null && !info.getContributions().equals("Contributions not explicitly stated")) {
            novelty.append("Contributions are clearly articulated. ");
        }

        String lowerText = fullText.toLowerCase();
        if (lowerText.contains("novel") || lowerText.contains("first") || lowerText.contains("state-of-the-art")) {
            novelty.append("Claims novelty/innovation in the research.");
        } else {
            novelty.append("Novelty not strongly emphasized.");
        }

        return novelty.toString();
    }

    public String assessImpact(String fullText, ExtractedInfo info) {
        StringBuilder impact = new StringBuilder();

        if (!info.getPracticalImplications().isEmpty()) {
            impact.append("Has practical implications. ");
        }

        if (!info.getTheoreticalImplications().isEmpty()) {
            impact.append("Contributes to theoretical understanding. ");
        }

        if (info.getPerformanceMetrics() != null && !info.getPerformanceMetrics().isEmpty()) {
            double bestMetric = info.getPerformanceMetrics().stream().max(Double::compare).orElse(0.0);
            if (bestMetric > 90) {
                impact.append("Excellent performance metrics (>90%). ");
            } else if (bestMetric > 80) {
                impact.append("Good performance metrics (>80%). ");
            } else if (bestMetric > 70) {
                impact.append("Moderate performance metrics. ");
            }
        }

        if (impact.length() == 0) {
            impact.append("Impact not clearly demonstrated.");
        }

        return impact.toString();
    }
}