package org.tuner.detector.frequency;

import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.noise.NoiseReductor;
import org.tuner.tool.fft.FFT;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;
import org.tuner.tool.util.SignalUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class FrequencyDetectorAutocorrelation implements FrequencyDetector {

    private final int MIN_SIGNAL_POWER;

    public FrequencyDetectorAutocorrelation(FFT fft, NoiseReductor noiseReductor) {
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        MIN_SIGNAL_POWER = propertyService.getInt("min.signal.power", 100);
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < MIN_SIGNAL_POWER) {
            return Optional.empty();
        }
        double[] autocorrelation = calculateAutocorrelationWithNormalization(signal, signal.length);
        int peakIdx = getFirstPeakIdx(autocorrelation, 1000, 0.7, samplingFrequency);
        double frequency = samplingFrequency / peakIdx;
        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        detection.setFftResult(autocorrelation);
        return Optional.of(detection);
    }

    public int getFirstPeakIdx(double[] autocorrelation, double frequencyThresh, double autocorrelationThresh, double samplingFrequency) {
        boolean peakFound = false;
        int peakIdx = -1;
        double peakMaxValue = 0;
        for (int i = 0; i < autocorrelation.length; i++) {

            // Ignore frequencies > frequencyThresh
            if (samplingFrequency / i > frequencyThresh) {
                continue;
            }

            if (autocorrelation[i] > autocorrelationThresh) {
                if (autocorrelation[i] > peakMaxValue) {
                    peakIdx = i;
                    peakMaxValue = autocorrelation[i];
                }
                peakFound = true;
            } else if (peakFound) {
                break;
            }
        }
        return peakIdx;
    }

    /**
     * Calculate average distance between autocorrelation peaks. First and last indices are ignored.
     *
     * @param peaks peaks from autocorrelation function
     * @return avg distance between peaks
     */
    public double getAvgDist(int[] peaks) {
        if (peaks.length < 2) {
            return 0;
        }
        int totalDist = 0;
        for (int i = 1; i < peaks.length; i++) {
            totalDist += peaks[i] - peaks[i - 1];
        }
        return totalDist / (double) (peaks.length - 3);
    }

    public double[] calculateAutocorrelationWithNormalization(double[] signal, int lags) {
        var mean = Arrays.stream(signal).average().getAsDouble();
        var variance = getVariance(signal, mean);

        double[] result = new double[signal.length];
        result[0] = 1;
        for (int lag = 1; lag < lags; lag++) {
            double sum = 0;
            for (int lagIdx = lag; lagIdx < signal.length; lagIdx++) {
                var originIdx = lagIdx - lag;
                sum += (signal[originIdx] - mean) * (signal[lagIdx] - mean);
            }
            result[lag] = sum / variance;
        }
        return result;
    }

    public double[] calculateAutocorrelation(double[] signal, int lags) {

        double[] result = new double[signal.length];
        result[0] = 1;
        for (int lag = 1; lag < lags; lag++) {
            double sum = 0;
            for (int lagIdx = lag; lagIdx < signal.length; lagIdx++) {
                var originIdx = lagIdx - lag;
                sum += signal[originIdx] * signal[lagIdx];
            }
            result[lag] = sum;
        }
        return result;
    }

    private double getVariance(double[] signal, double mean) {
        double result = 0;
        for (double x : signal) {
            result += Math.pow(x - mean, 2);
        }
        return result;
    }

    public int[] getPeaks(double[] arr) {
        if (arr.length == 0) {
            return new int[0];
        }
        List<Integer> result = new ArrayList<>();
        double last = arr[0];
        boolean isRising = false;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < last) {
                if (isRising) {
                    result.add(i - 1);
                }
                isRising = false;
            } else {
                isRising = true;
            }
            last = arr[i];
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
