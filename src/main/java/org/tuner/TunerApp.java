package org.tuner;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tuner.detector.observer.DetectionProducer;
import org.tuner.frontend.MainWindow;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TunerApp extends Application {

    private final Logger logger = Logger.getLogger(TunerApp.class.getName());
    private GuitarTuner guitarTuner;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainWindow mainWindow = new MainWindow(stage);
        DetectionProducer detectionProducer = runTuner();
        mainWindow.setDetectionProducer(detectionProducer);
    }

    @Override
    public void stop() {
        logger.info("Stopping TunerApp");
        if (guitarTuner != null) {
            guitarTuner.stop();
        }
        System.exit(0);
    }

    public DetectionProducer runTuner() {
        guitarTuner = new GuitarTuner();
        Executors.newSingleThreadExecutor().submit(guitarTuner::run);
        return guitarTuner;
    }
}