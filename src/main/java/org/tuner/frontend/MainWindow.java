package org.tuner.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {

    private final MainWindowController controller;

    public MainWindow(Stage stage) throws IOException {
        final int WIDTH = 320;
        final int HEIGHT = 200;
        final String TITLE = "Tuner";

        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("tuner-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        controller = fxmlLoader.getController();

        stage.setTitle(TITLE);
        stage.setScene(scene);

        stage.show();
    }

    public MainWindowController getController() {
        return controller;
    }
}
