package org.tuner.tool.util;

import java.util.Arrays;

public class SignalUtils {

    private SignalUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static double calculatePower(double[] signal) {
        double sum = 0;
        for (double value : signal) {
            sum += Math.abs(value);
        }
        return sum / signal.length;
    }

    public static double[] applyHanningWindow(double[] array) {
        int n = array.length;
        double[] result = new double[n];

        double[] h = new double[n];
        int startX = 1 - n;
        double step = 2;
        for (int i = 0; i < n; i++) {
            h[i] = startX + i * step;
        }

        for (int i = 0; i < n; i++) {
            h[i] = 0.5 + 0.5 * Math.cos(Math.PI * h[i] / (n - 1));
        }

        for (int i = 0; i < n; i++) {
            result[i] = array[i] * h[i];
        }
        return result;
    }

    public static double[][] downSampleSignal(double[] signal, int n) {
        double[][] result = new double[n][];
        for (int i = 0; i < n; i++) {
            int divisor = 2 + i;
            int currentLen = signal.length / divisor;
            result[i] = new double[currentLen];
            for (int j = 0; j < currentLen; j++) {
                int originalIdx = j * divisor;
                result[i][j] = signal[originalIdx];
            }
        }
        return result;
    }

    public static double[] getHpsResult(double[][] downSampledSignals, double[] originalSignal) {
        int finalLen = downSampledSignals[downSampledSignals.length - 1].length;
        double[] result = Arrays.copyOfRange(originalSignal, 0, finalLen);
        for (int i = 0; i < finalLen; i++) {
            result[i] = 1;
            for (double[] downSampledSignal : downSampledSignals) {
                result[i] *= downSampledSignal[i];
            }
        }
        return result;
    }

    public static double convertFftIndexToFrequency(int idx, float samplingFrequency, int fftLength) {
        return idx * (samplingFrequency / fftLength);
    }
}
