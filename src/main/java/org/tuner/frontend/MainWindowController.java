package org.tuner.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.model.Pitch;
import org.tuner.detector.observer.DetectionProducer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

public class MainWindowController {

    private final Logger logger = Logger.getLogger(MainWindowController.class.getName());
    private final NumberFormat numberFormatter = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    @FXML
    private Rectangle sliderRectangle;
    @FXML
    private Line sliderLine;
    @FXML
    private Pane sliderPane;
    @FXML
    private Label noteLabel;
    @FXML
    private Label frequencyLabel;
    @FXML
    private Label diffLabel;
    private DetectionProducer detectionProducer;

    public void onNewDetectionAction(DetailedPitchDetection pitchDetection) {
        double frequency = pitchDetection.getDetectedFrequency();
        Pitch pitch = pitchDetection.getClosestPitch();
        double diff = pitchDetection.getDifference();

        Platform.runLater(() -> frequencyLabel.setText(numberFormatter.format(frequency)));
        Platform.runLater(() -> noteLabel.setText(pitch.toString()));
        Platform.runLater(() -> diffLabel.setText(numberFormatter.format(diff)));

        adjustSlider(pitch, diff);
    }

    public void chooseHpsAlgorithm() {
        logger.warning("Changing algorithms at runtime not implemented yet");
    }

    public void chooseAutocorrelationAlgorithm() {
        logger.warning("Changing algorithms at runtime not implemented yet");
    }

    public void signalChartAction() {
        new SignalChart(detectionProducer);
    }

    private void adjustSlider(Pitch pitch, double diff) {
        Pitch previousPitch = getPreviousPitch(pitch);
        Pitch nextPitch = getNextPitch(pitch);

        double maxFreqDiff = Math.min(pitch.getFrequency() - previousPitch.getFrequency(), nextPitch.getFrequency() - pitch.getFrequency());
        double maxSliderDist = sliderRectangle.getWidth() / 2;

        double midPosition = sliderPane.getWidth() / 2;
        double positionChange = diff / maxFreqDiff * maxSliderDist;

        double newX = midPosition + positionChange;
        sliderLine.setStartX(newX);
        sliderLine.setEndX(newX);
    }

    private Pitch getPreviousPitch(Pitch pitch) {
        Pitch prev = Pitch.A1;
        for (Pitch currentPitch : Pitch.values()) {
            if (currentPitch == pitch) {
                return prev;
            }
            prev = currentPitch;
        }
        return prev;
    }

    private Pitch getNextPitch(Pitch pitch) {
        boolean getNext = false;
        for (Pitch currentPitch : Pitch.values()) {
            if (getNext) {
                return currentPitch;
            }
            if (currentPitch == pitch) {
                getNext = true;
            }
        }
        return Pitch.G_SHARP_6;
    }

    public void setDetectionProducer(DetectionProducer detectionProducer) {
        this.detectionProducer = detectionProducer;
    }
}