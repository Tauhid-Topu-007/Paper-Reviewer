package org.example.paperreview.model;

public class SourceHighlight {
    private String fieldName;
    private String text;
    private int pageNumber;
    private int startPosition;
    private int endPosition;

    public SourceHighlight(String fieldName, String text, int pageNumber, int startPosition, int endPosition) {
        this.fieldName = fieldName;
        this.text = text;
        this.pageNumber = pageNumber;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    // Getters and Setters
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getStartPosition() { return startPosition; }
    public void setStartPosition(int startPosition) { this.startPosition = startPosition; }

    public int getEndPosition() { return endPosition; }
    public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
}