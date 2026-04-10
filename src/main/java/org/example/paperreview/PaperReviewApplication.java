package org.example.paperreview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PaperReviewApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/paperreview/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        // Remove default window decorations (title bar)
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setTitle("Academic Paper Review Assistant");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}