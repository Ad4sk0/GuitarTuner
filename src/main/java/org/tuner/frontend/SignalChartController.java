package org.tuner.frontend;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import org.tuner.detector.dto.DetailedPitchDetection;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SignalChartController implements Initializable {

    private final Logger logger = Logger.getLogger(SignalChartController.class.getName());

    @FXML
    private LineChart<String, Double> lineChart;

    public void updateChart(DetailedPitchDetection pitchDetection) {
        // TODO
    }

    public void onNewDetectionAction(DetailedPitchDetection pitchDetection) {
        updateChart(pitchDetection);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO initialize chart
    }
}
