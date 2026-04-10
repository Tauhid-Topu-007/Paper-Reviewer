package org.example.paperreview.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFHighlighterService {

    public File createHighlightedPDF(File inputPdf, Map<String, String> extractedInfo) throws IOException {
        String outputPath = System.getProperty("java.io.tmpdir") +
                File.separator + "highlighted_" + System.currentTimeMillis() + ".pdf";
        File outputFile = new File(outputPath);

        try (PDDocument document = Loader.loadPDF(inputPdf)) {

            System.out.println("=== PDF Highlighting Started ===");
            System.out.println("Total items to highlight: " + extractedInfo.size());

            Map<Integer, List<HighlightRect>> allHighlights = new HashMap<>();
            int totalMatches = 0;
            int itemNumber = 1;

            for (Map.Entry<String, String> entry : extractedInfo.entrySet()) {
                String category = entry.getKey();
                String searchText = entry.getValue();

                System.out.println("[" + itemNumber + "/" + extractedInfo.size() + "] Searching: " + category);

                if (searchText != null && searchText.length() > 15) {
                    for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++) {
                        List<HighlightRect> highlights = findTextOnPage(document, pageNum, searchText, category);
                        if (!highlights.isEmpty()) {
                            allHighlights.computeIfAbsent(pageNum, k -> new ArrayList<>()).addAll(highlights);
                            totalMatches += highlights.size();
                        }
                    }
                }
                itemNumber++;
            }

            System.out.println("Total matches found: " + totalMatches);

            if (totalMatches > 0) {
                applyHighlightsToDocument(document, allHighlights);
                System.out.println("Highlights applied successfully!");
            }

            document.save(outputFile);
        }

        return outputFile;
    }

    private List<HighlightRect> findTextOnPage(PDDocument document, int pageNum, String searchText, String category) throws IOException {
        List<HighlightRect> highlights = new ArrayList<>();
        PDPage page = document.getPage(pageNum - 1);
        PDRectangle pageSize = page.getMediaBox();
        final float pageHeight = pageSize.getHeight();
        final String finalCategory = category;
        final int finalPageNum = pageNum;

        List<String> searchChunks = breakTextIntoChunks(searchText);
        final List<String> finalSearchChunks = new ArrayList<>(searchChunks);

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                String pageText = text.toLowerCase();

                for (String chunk : finalSearchChunks) {
                    String lowerChunk = chunk.toLowerCase();
                    if (pageText.contains(lowerChunk)) {
                        int foundIndex = pageText.indexOf(lowerChunk);

                        if (foundIndex >= 0) {
                            int charCount = 0;
                            float minX = Float.MAX_VALUE;
                            float maxX = Float.MIN_VALUE;
                            float minY = Float.MAX_VALUE;
                            float maxY = Float.MIN_VALUE;
                            String matchedText = "";

                            for (TextPosition pos : textPositions) {
                                String charText = pos.getUnicode();
                                if (charText != null && !charText.trim().isEmpty()) {
                                    if (charCount >= foundIndex && charCount < foundIndex + lowerChunk.length()) {
                                        float x = pos.getX();
                                        float y = pos.getY();
                                        float width = pos.getWidth();
                                        float height = pos.getHeight();

                                        minX = Math.min(minX, x);
                                        maxX = Math.max(maxX, x + width);
                                        minY = Math.min(minY, y);
                                        maxY = Math.max(maxY, y + height);
                                        matchedText += charText;
                                    }
                                    charCount++;
                                }
                            }

                            if (minX != Float.MAX_VALUE) {
                                HighlightRect hr = new HighlightRect();
                                hr.x = minX - 1;
                                hr.y = pageHeight - maxY - 2;
                                hr.width = maxX - minX + 2;
                                hr.height = maxY - minY + 4;
                                hr.category = finalCategory;
                                hr.text = matchedText.length() > 0 ? matchedText : chunk;
                                highlights.add(hr);
                            }
                        }
                    }
                }
                super.writeString(text, textPositions);
            }
        };

        stripper.setStartPage(finalPageNum);
        stripper.setEndPage(finalPageNum);
        stripper.getText(document);

        return removeDuplicateHighlights(highlights);
    }

    private List<String> breakTextIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        String cleanText = text.replaceAll("[^a-zA-Z0-9\\s.,;:!?'\"-]", " ").trim();

        if (cleanText.length() > 20 && cleanText.length() < 300) {
            chunks.add(cleanText);
        }

        String[] sentences = cleanText.split("[.!?]+");
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 20 && trimmed.length() < 200) {
                chunks.add(trimmed);
            }
        }

        String[] words = cleanText.split("\\s+");
        if (words.length >= 5) {
            StringBuilder keyPhrase = new StringBuilder();
            for (int i = 0; i < Math.min(10, words.length); i++) {
                keyPhrase.append(words[i]).append(" ");
            }
            String phrase = keyPhrase.toString().trim();
            if (phrase.length() > 15 && !chunks.contains(phrase)) {
                chunks.add(phrase);
            }
        }

        Set<String> uniqueChunks = new LinkedHashSet<>(chunks);
        return new ArrayList<>(uniqueChunks);
    }

    private List<HighlightRect> removeDuplicateHighlights(List<HighlightRect> highlights) {
        List<HighlightRect> unique = new ArrayList<>();
        for (HighlightRect hr : highlights) {
            boolean isDuplicate = false;
            for (HighlightRect existing : unique) {
                if (Math.abs(existing.x - hr.x) < 10 && Math.abs(existing.y - hr.y) < 10) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                unique.add(hr);
            }
        }
        return unique;
    }

    private void applyHighlightsToDocument(PDDocument document, Map<Integer, List<HighlightRect>> allHighlights) throws IOException {
        for (Map.Entry<Integer, List<HighlightRect>> entry : allHighlights.entrySet()) {
            int pageNum = entry.getKey();
            List<HighlightRect> highlights = entry.getValue();
            PDPage page = document.getPage(pageNum - 1);

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                // Set semi-transparent yellow highlight (so text remains visible)
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(0.4f); // 40% opacity - text will be visible
                contentStream.setGraphicsStateParameters(graphicsState);
                contentStream.setNonStrokingColor(1f, 1f, 0f); // Yellow color

                for (HighlightRect hr : highlights) {
                    contentStream.addRect(hr.x, hr.y, hr.width, hr.height);
                    contentStream.fill();
                }
            }
        }
    }

    private static class HighlightRect {
        float x;
        float y;
        float width;
        float height;
        String category;
        String text;
    }
}