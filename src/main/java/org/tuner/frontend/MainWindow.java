package org.tuner.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.observer.DetectionListener;
import org.tuner.detector.observer.DetectionProducer;

import java.io.IOException;

public class MainWindow implements DetectionListener {

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

    public void setDetectionProducer(DetectionProducer detectionProducer) {
        controller.setDetectionProducer(detectionProducer);
        detectionProducer.addListener(this);
    }

    @Override
    public void onNewDetection(DetailedPitchDetection pitchDetection) {
        controller.onNewDetectionAction(pitchDetection);
    }
}
