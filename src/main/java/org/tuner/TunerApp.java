package org.tuner;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tuner.detector.observer.DetectionProducer;
import org.tuner.frontend.MainWindow;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TunerApp extends Application {

    private final Logger logger = Logger.getLogger(TunerApp.class.getName());
    private GuitarTuner guitarTuner;

    public static void main(String[] args) {
        configureLogging();
        launch();
    }

    private static void configureLogging() {
        String loggingPropertiesFileName = "logging.properties";
        URL loggingProperties = TunerApp.class.getClassLoader().getResource(loggingPropertiesFileName);
        if (loggingProperties == null) {
            throw new IllegalStateException(String.format("Unable to find logging properties file: %s", loggingPropertiesFileName));
        }
        try (var fileInputStream = new FileInputStream(loggingProperties.getFile())) {
            LogManager.getLogManager().readConfiguration(fileInputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to setup logger: " + e);
        }
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