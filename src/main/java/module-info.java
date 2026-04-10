module org.example.paperreview {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires org.apache.opennlp.tools;
    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.ooxml;
    requires itextpdf;  // iText 5 module
    requires org.slf4j;

    opens org.example.paperreview to javafx.fxml;
    opens org.example.paperreview.controller to javafx.fxml;
    opens org.example.paperreview.model to javafx.base;
    exports org.example.paperreview;
}