package org.tuner;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tuner.frontend.MainWindow;
import org.tuner.frontend.MainWindowController;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TunerApp extends Application {

    private final Logger logger = Logger.getLogger(TunerApp.class.getName());
    private MainWindowController mainWindowController;
    private GuitarTuner guitarTuner;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainWindow mainWindow = new MainWindow(stage);
        mainWindowController = mainWindow.getController();
        runTuner();
    }

    @Override
    public void stop() {
        logger.info("Stopping TunerApp");
        if (guitarTuner != null) {
            guitarTuner.stop();
        }
        System.exit(0);
    }

    public void runTuner() {
        guitarTuner = new GuitarTuner(mainWindowController);
        Executors.newSingleThreadExecutor().submit(guitarTuner::run);
    }
}