package frontend;

import detector.dto.*;
import org.example.chart.*;
import org.example.chart.impl.bar.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TestGUI extends JFrame {

    private final TunerPanel tunerPanel;
    private final DynamicChart signalChart;
    private final DynamicChart fftChart;
    private final DynamicChart hpsChart;
    private final List<DynamicChart> downSampledCharts;
    private final int FFT_SAMPLES_SIZE = 6000;

    public TestGUI() throws HeadlessException {

        double yMin = 0;
        double yMax = 1;

        this.setVisible(true);
        this.setSize(1600, 800);
        this.setLayout(new GridLayout(9,1));

        tunerPanel = new TunerPanel();
        this.add(tunerPanel);

        signalChart = new BarChart();
        signalChart.setFixedYMin(yMin);
        signalChart.setFixedYMax(yMax);
        signalChart.setTitle("Signal");
        this.add((Component) signalChart);

        fftChart = new BarChart();
        fftChart.setFixedYMin(yMin);
        fftChart.setFixedYMax(yMax);
        fftChart.setTitle("FFT");
        this.add((Component) fftChart);

        downSampledCharts = new ArrayList<>();
        for (int i = 1 ; i <= 5; i++) {
            var hpsChart = new BarChart();
            hpsChart.setFixedYMin(yMin);
            hpsChart.setFixedYMax(yMax);
            hpsChart.setTitle("DownSampled " + i);
            this.add(hpsChart);
            downSampledCharts.add(hpsChart);
        }

        hpsChart = new BarChart();
        hpsChart.setFixedYMin(yMin);
        hpsChart.setFixedYMax(yMax);
        hpsChart.setTitle("HPS result");
        this.add((Component) hpsChart);
        this.revalidate();
    }

    public void updateSignalChart(double[] xData, double[] yData, double maxValue) {
        double[] yDataNormalized = normalize(yData, maxValue);
        signalChart.updateData(xData, yDataNormalized);
    }

    public void updateFftChart(double[] xData, double[] yData, double maxValue) {
        double[] xDataTrimmed = trim(xData, FFT_SAMPLES_SIZE);
        double[] yDataTrimmed = trim(yData, FFT_SAMPLES_SIZE);
        double[] yDataNormalized = normalize(yDataTrimmed, maxValue);
        fftChart.updateData(xDataTrimmed, yDataNormalized);
    }

    public void updateDownSampledCharts(double[] xData, double[][] downSampledSignals, double maxValue, int targetSize) {
        double[] xDataTrimmed = trim(xData, FFT_SAMPLES_SIZE);
        for (int i = 0; i < downSampledSignals.length; i++) {
            double[] paddedArray = padSignalWithZeros(downSampledSignals[i], targetSize);
            double[] yDataTrimmed = trim(paddedArray, FFT_SAMPLES_SIZE);
            double[] yDataNormalized = normalize(yDataTrimmed, maxValue);
            downSampledCharts.get(i).updateData(xDataTrimmed, yDataNormalized);
        }
    }

    public void updateHpsChart(double[] xData, double[] yData, double maxValue, int targetSize) {
        double[] paddedArray = padSignalWithZeros(yData, targetSize);
        double[] xDataTrimmed = trim(xData, FFT_SAMPLES_SIZE);
        double[] yDataTrimmed = trim(paddedArray, FFT_SAMPLES_SIZE);
        double[] yDataNormalized = normalize(yDataTrimmed, maxValue);
        hpsChart.updateData(xDataTrimmed, yDataNormalized);
    }

    private double[] padSignalWithZeros(double[] signal, int targetSize) {
        double[] paddedArray = new double[targetSize];
        System.arraycopy(signal, 0, paddedArray, 0, signal.length);
        return paddedArray;
    }

    private double[] normalize(double[] signal, double maxValue) {
        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            result[i] = signal[i] / maxValue;
        }
        return result;
    }

    private double[] trim(double[] signal, int targetSize) {
        return Arrays.copyOfRange(signal, 0, targetSize);
    }

    public DynamicChart getSignalChart() {
        return signalChart;
    }

    public DynamicChart getFftChart() {
        return fftChart;
    }

    public List<DynamicChart> getDownSampledCharts() {
        return downSampledCharts;
    }

    public DynamicChart getHpsChart() {
        return hpsChart;
    }

    public DetailedPitchDetection getDetection() {
        return tunerPanel.getDetection();
    }

    public void setDetection(DetailedPitchDetection detection) {
        tunerPanel.setDetection(detection);
    }
}
