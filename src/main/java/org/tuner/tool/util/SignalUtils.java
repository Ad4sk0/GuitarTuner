package org.tuner.tool.util;

import org.apache.commons.math3.complex.Complex;

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

    public static double convertFftIndexToFrequency(int idx, float samplingFrequency, int signalLength) {
        return idx * (samplingFrequency / signalLength);
    }

    public static int convertFrequencyToFFTIndex(double frequency, float samplingFrequency, int signalLength) {
        return (int) (frequency * signalLength / samplingFrequency);
    }

    public static double convertFftIndexToFrequencyWithInterpolation(int idx, double[] freqDomainArray,
                                                                     double samplingFrequency, int signalLength) {
        if (idx < 1 || idx + 1 >= freqDomainArray.length) {
            throw new IllegalArgumentException("Unable to interpolate index");
        }
        double prev = freqDomainArray[idx - 1];
        double curr = freqDomainArray[idx];
        double next = freqDomainArray[idx + 1];
        double p = (prev - next) / (2 * (prev - 2 * curr + next));
        double interpolatedIdx = idx + p;
        return interpolatedIdx * samplingFrequency / signalLength;
    }

    public static double[] createHannWindow(int n) {
        double[] window = new double[n];
        for (int i = 0; i < n; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * (i / (double) n)));
        }
        return window;
    }

    public static double[] applyHannWindow(double[] signal) {
        double[] window = createHannWindow(signal.length);
        return multiply(signal, window);
    }

    public static double[] multiply(double[] s1, double[] s2) {
        if (s1.length != s2.length) {
            throw new IllegalArgumentException("Signal lengths are not the same");
        }
        double[] result = new double[s1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = s1[i] * s2[i];
        }
        return result;
    }

    public static double[] calculateMagnitude(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = Math.sqrt(Math.pow(complexArray[i].getReal(), 2) + Math.pow(complexArray[i].getImaginary(), 2));
        }
        return result;
    }

    public static double[] getRealPart(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = complexArray[i].getReal();
        }
        return result;
    }

    public static int getHighestPeakIndexFromRange(double[] array, int minIdx, int maxIdx) {
        if (minIdx > maxIdx) {
            throw new IllegalArgumentException("Min index less than max index");
        }
        if (minIdx < 0) {
            throw new IllegalArgumentException("Min index less than 0");
        }
        if (maxIdx > array.length) {
            throw new IllegalArgumentException("max index greater than array length");
        }

        int resultIdx = minIdx;
        double maxValue = array[minIdx];
        for (int i = minIdx + 1; i <= maxIdx; i++) {
            if (array[i] > maxValue) {
                resultIdx = i;
                maxValue = array[i];
            }
        }
        return resultIdx;
    }

    public static int getHighestPeakIdxFreqDomainFromRange(double[] freqDomainArray,
                                                           float samplingFrequency, double minFrequency,
                                                           double maxFrequency, int signalLength) {
        int minIdx = convertFrequencyToFFTIndex(minFrequency, samplingFrequency, signalLength);
        int maxIdx = convertFrequencyToFFTIndex(maxFrequency, samplingFrequency, signalLength);
        return getHighestPeakIndexFromRange(freqDomainArray, minIdx, maxIdx);
    }

    public static double getHighestFrequencyByPeakIdxTimeDomainFromRange(double[] timeDomainArray,
                                                                         float samplingFrequency, double minFrequency,
                                                                         double maxFrequency) {
        int minIdx = (int) (samplingFrequency / maxFrequency);
        int maxIdx = (int) (samplingFrequency / minFrequency);
        int resultIdx = getHighestPeakIndexFromRange(timeDomainArray, minIdx, maxIdx);
        return samplingFrequency / resultIdx;
    }
}
