package org.tuner.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.tuner.detector.model.Pitch;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

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

    private final NumberFormat numberFormatter = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void onNewFrequencyAction(double frequency) {
        Pitch pitch = getClosestNotePitch(frequency);
        double diff = pitch.getFrequency() - frequency;

        Platform.runLater(() -> {
            frequencyLabel.setText(numberFormatter.format(frequency));
        });
        Platform.runLater(() -> {
            noteLabel.setText(pitch.toString());
        });
        Platform.runLater(() -> {
            diffLabel.setText(numberFormatter.format(diff));
        });

        adjustSlider(pitch, diff);
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

    private Pitch getClosestNotePitch(double frequency) {
        Pitch result = Pitch.A1;
        double dist = Double.MAX_VALUE;
        for (Pitch pitch : Pitch.values()) {
            double curDist = pitch.getFrequency() - frequency;
            if (Math.abs(curDist) < dist) {
                result = pitch;
                dist = Math.abs(curDist);
            }
            if (curDist > 0) {
                break;
            }
        }
        return result;
    }
}