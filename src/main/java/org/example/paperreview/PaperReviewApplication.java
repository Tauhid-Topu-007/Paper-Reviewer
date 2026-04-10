package org.example.paperreview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PaperReviewApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/paperreview/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        // Apply CSS (only if file exists)
        try {
            String cssPath = "/org/example/paperreview/styles.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } else {
                System.out.println("CSS file not found, continuing without styles");
            }
        } catch (Exception e) {
            System.out.println("Could not load CSS: " + e.getMessage());
        }

        stage.setTitle("Academic Paper Review Assistant");

        // Add icon only if it exists
        try {
            String iconPath = "/org/example/paperreview/icon.png";
            if (getClass().getResourceAsStream(iconPath) != null) {
                stage.getIcons().add(new Image(getClass().getResourceAsStream(iconPath)));
            } else {
                System.out.println("Icon not found, continuing without icon");
            }
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}