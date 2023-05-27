package org.tuner.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.observer.DetectionListener;
import org.tuner.detector.observer.DetectionProducer;

import java.io.IOException;

public class SignalChart implements DetectionListener {
    private final SignalChartController controller;

    public SignalChart(DetectionProducer detectionProducer) {
        detectionProducer.addListener(this);

        final int WIDTH = 640;
        final int HEIGHT = 480;
        final String TITLE = "Signal chart";

        FXMLLoader fxmlLoader = new FXMLLoader(SignalChart.class.getResource("signal-chart-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        controller = fxmlLoader.getController();
        Stage stage = new Stage();
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> detectionProducer.removeListener(this));
    }

    @Override
    public void onNewDetection(DetailedPitchDetection pitchDetection) {
        controller.onNewDetectionAction(pitchDetection);
    }

    public SignalChartController getController() {
        return controller;
    }
}
