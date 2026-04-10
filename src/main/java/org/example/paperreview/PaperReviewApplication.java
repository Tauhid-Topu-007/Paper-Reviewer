package org.example.paperreview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class PaperReviewApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/paperreview/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800); // Smaller default size

        // Remove default window decorations
        stage.initStyle(StageStyle.UNDECORATED);

        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Set initial window size (80% of screen size, max 1400x900)
        double initialWidth = Math.min(1200, screenWidth * 0.8);
        double initialHeight = Math.min(800, screenHeight * 0.8);

        stage.setTitle("Academic Paper Review Assistant");
        stage.setScene(scene);

        // Set minimum window size
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Set maximum window size (optional - prevent going too large)
        stage.setMaxWidth(screenWidth);
        stage.setMaxHeight(screenHeight);

        // Center the window on screen
        stage.setX((screenWidth - initialWidth) / 2);
        stage.setY((screenHeight - initialHeight) / 2);

        stage.setWidth(initialWidth);
        stage.setHeight(initialHeight);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}