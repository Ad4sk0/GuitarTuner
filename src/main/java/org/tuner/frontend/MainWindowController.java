package org.tuner.frontend;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.model.Pitch;
import org.tuner.detector.observer.DetectionProducer;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainWindowController implements Initializable {

    private final Logger logger = Logger.getLogger(MainWindowController.class.getName());
    private final NumberFormat numberFormatter = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final String CHANGE_ALGORITHM_NOT_IMPLEMENTED_MSG = "Changing algorithms at runtime not implemented yet";
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
    private Timeline clearDetectionTimeline;
    private Timeline sliderLinePathTimeLine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearDetectionData();
        frequencyLabel.setText("Play any note");
        sliderLinePathTimeLine = new Timeline();
        initializeClearDetectionAnimation();
    }

    private void initializeClearDetectionAnimation() {
        final int clearTimeInSeconds = 2;
        clearDetectionTimeline = new Timeline(new KeyFrame(Duration.seconds(clearTimeInSeconds), x -> {
            clearDetectionData();
            sliderLinePathTimeLine.stop();
        }));
    }

    private void clearDetectionData() {
        frequencyLabel.setText("");
        noteLabel.setText("");
        diffLabel.setText("");
        sliderLine.setVisible(false);
    }

    public void onNewDetectionAction(DetailedPitchDetection pitchDetection) {
        clearDetectionTimeline.playFromStart();
        double frequency = pitchDetection.getDetectedFrequency();
        Pitch pitch = pitchDetection.getClosestPitch();
        double diff = pitchDetection.getDifference();

        Platform.runLater(() -> noteLabel.setText(pitch.toString()));
        Platform.runLater(() -> frequencyLabel.setText(numberFormatter.format(frequency)));
        Platform.runLater(() -> diffLabel.setText(numberFormatter.format(diff)));

        adjustSlider(pitch, diff);
    }

    public void chooseHpsAlgorithm() {
        logger.warning(CHANGE_ALGORITHM_NOT_IMPLEMENTED_MSG);
    }

    public void chooseAutocorrelationAlgorithm() {
        logger.warning(CHANGE_ALGORITHM_NOT_IMPLEMENTED_MSG);
    }

    public void chooseCepstrumAlgorithm() {
        logger.warning(CHANGE_ALGORITHM_NOT_IMPLEMENTED_MSG);
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
        if (sliderLine.isVisible()) {
            moveSliderWithAnimation(newX);
        } else {
            sliderLine.setStartX(newX);
            sliderLine.setEndX(newX);
            sliderLine.setVisible(true);
        }
    }

    private void moveSliderWithAnimation(double newX) {
        sliderLinePathTimeLine.stop();
        sliderLinePathTimeLine.getKeyFrames().clear();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), new KeyValue(sliderLine.startXProperty(), newX), new KeyValue(sliderLine.endXProperty(), newX));
        sliderLinePathTimeLine.getKeyFrames().add(keyFrame);
        sliderLinePathTimeLine.play();
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