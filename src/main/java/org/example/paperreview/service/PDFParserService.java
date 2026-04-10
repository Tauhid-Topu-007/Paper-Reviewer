package org.example.paperreview.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFParserService {
    private PDDocument document;
    private String fullText;
    private List<String> pages;

    public void loadPDF(File pdfFile) throws IOException {
        // For PDFBox 3.x, use Loader.loadPDF() instead of PDDocument.load()
        document = Loader.loadPDF(pdfFile);
        extractText();
    }

    private void extractText() throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        fullText = stripper.getText(document);

        // Split by pages
        pages = new ArrayList<>();
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            pages.add(stripper.getText(document));
        }
    }

    public String getFullText() {
        return fullText;
    }

    public List<String> getPages() {
        return pages;
    }

    public int getNumberOfPages() {
        return document != null ? document.getNumberOfPages() : 0;
    }

    public void close() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    public String getTextForPage(int pageNumber) {
        if (pageNumber >= 1 && pageNumber <= pages.size()) {
            return pages.get(pageNumber - 1);
        }
        return "";
    }
}