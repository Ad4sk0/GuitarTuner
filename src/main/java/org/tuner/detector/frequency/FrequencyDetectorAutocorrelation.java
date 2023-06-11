package org.tuner.detector.frequency;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;
import org.tuner.tool.util.SignalUtils;

import java.util.Arrays;
import java.util.Optional;


public class FrequencyDetectorAutocorrelation implements FrequencyDetector {
    private final FastFourierTransformer fft;
    private final int minFrequency;
    private final int maxFrequency;
    private final int minSignalPower;

    public FrequencyDetectorAutocorrelation() {
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        minFrequency = propertyService.getInt("min.frequency", 60);
        maxFrequency = propertyService.getInt("max.frequency", 500);
        minSignalPower = propertyService.getInt("min.signal.power", 100);
        this.fft = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < minSignalPower) {
            return Optional.empty();
        }
        double[] autocorrelation = calculateAutocorrelationWithFFT(signal);
        double frequency = SignalUtils.getHighestFrequencyByPeakIdxTimeDomainFromRange(autocorrelation, samplingFrequency,
                minFrequency, maxFrequency);
        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }

    private double[] calculateAutocorrelationWithFFT(double[] signal) {
        Complex[] fftResult = fft.transform(signal, TransformType.FORWARD);
        double[] powerSpectrum = calculatePowerSpectrum(fftResult);
        Complex[] ifftResult = fft.transform(powerSpectrum, TransformType.INVERSE);
        return SignalUtils.getRealPart(ifftResult);
    }

    private double[] calculatePowerSpectrum(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = Math.pow(complexArray[i].abs(), 2);
        }
        return result;
    }

    public double[] calculateAutocorrelationWithNormalization(double[] signal, int lags) {
        double mean = Arrays.stream(signal).average().orElse(0);
        double variance = calculateVariance(signal, mean);

        if (variance == 0) {
            throw new IllegalStateException();
        }

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

    private double calculateVariance(double[] signal, double mean) {
        double result = 0;
        for (double x : signal) {
            result += Math.pow(x - mean, 2);
        }
        return result;
    }
}
