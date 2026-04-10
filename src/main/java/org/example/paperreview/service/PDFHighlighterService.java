package org.example.paperreview.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFHighlighterService {

    public File createHighlightedPDF(File inputPdf, Map<String, String> extractedInfo) throws IOException {
        // Create output file in temp directory
        String outputPath = System.getProperty("java.io.tmpdir") +
                File.separator + "highlighted_" + System.currentTimeMillis() + ".pdf";
        File outputFile = new File(outputPath);

        // Load the original PDF using Loader.loadPDF() for PDFBox 3.x
        try (PDDocument document = Loader.loadPDF(inputPdf)) {

            System.out.println("PDF loaded. Pages: " + document.getNumberOfPages());
            System.out.println("Searching for " + extractedInfo.size() + " items to highlight...");

            int highlightCount = 0;
            int itemNumber = 1;

            // For each extracted information, search and highlight
            for (Map.Entry<String, String> entry : extractedInfo.entrySet()) {
                String category = entry.getKey();
                String searchText = entry.getValue();

                if (searchText != null && !searchText.isEmpty() && searchText.length() > 15) {
                    System.out.println("(" + itemNumber + "/" + extractedInfo.size() + ") Highlighting: " + category);
                    int count = highlightTextInPDF(document, searchText);
                    highlightCount += count;
                    itemNumber++;
                }
            }

            System.out.println("Total highlights applied: " + highlightCount);

            // Save the highlighted PDF
            document.save(outputFile);
        }

        return outputFile;
    }

    private int highlightTextInPDF(PDDocument document, String searchText) throws IOException {
        int highlightCount = 0;

        // Search in all pages
        for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++) {
            highlightCount += highlightTextOnPage(document, searchText, pageNum);
        }

        return highlightCount;
    }

    private int highlightTextOnPage(PDDocument document, String searchText, int pageNum) throws IOException {
        PDPage page = document.getPage(pageNum - 1);
        PDRectangle pageSize = page.getMediaBox();
        float pageHeight = pageSize.getHeight();

        List<float[]> highlightRects = new ArrayList<>();

        // Custom stripper to find text positions
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                String lowerText = text.toLowerCase();
                String lowerSearch = searchText.toLowerCase();

                if (lowerText.contains(lowerSearch)) {
                    float minX = Float.MAX_VALUE;
                    float maxX = Float.MIN_VALUE;
                    float minY = Float.MAX_VALUE;
                    float maxY = Float.MIN_VALUE;
                    boolean found = false;

                    for (TextPosition position : textPositions) {
                        String charText = position.getUnicode();
                        if (charText != null && !charText.trim().isEmpty()) {
                            if (lowerSearch.contains(charText.toLowerCase())) {
                                float x = position.getX();
                                float y = position.getY();
                                float width = position.getWidth();
                                float height = position.getHeight();

                                minX = Math.min(minX, x);
                                maxX = Math.max(maxX, x + width);
                                minY = Math.min(minY, y);
                                maxY = Math.max(maxY, y + height);
                                found = true;
                            }
                        }
                    }

                    if (found && minX != Float.MAX_VALUE) {
                        float rectX = minX;
                        float rectY = pageHeight - maxY;
                        float rectWidth = maxX - minX;
                        float rectHeight = maxY - minY;

                        if (rectWidth > 0 && rectHeight > 0 && rectWidth < pageSize.getWidth()) {
                            highlightRects.add(new float[]{rectX, rectY, rectWidth, rectHeight});
                        }
                    }
                }
                super.writeString(text, textPositions);
            }
        };

        stripper.setStartPage(pageNum);
        stripper.setEndPage(pageNum);
        stripper.getText(document);

        // Apply highlights if we found matching positions
        if (!highlightRects.isEmpty()) {
            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                // Set transparency for highlight
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(0.5f);
                contentStream.setGraphicsStateParameters(graphicsState);

                // Set yellow color (RGB: 1, 1, 0)
                contentStream.setNonStrokingColor(1f, 1f, 0f);

                for (float[] rect : highlightRects) {
                    contentStream.addRect(rect[0], rect[1], rect[2], rect[3]);
                    contentStream.fill();
                }
            }
        }

        return highlightRects.size();
    }
}