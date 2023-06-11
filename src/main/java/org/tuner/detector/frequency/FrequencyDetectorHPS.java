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


public class FrequencyDetectorHPS implements FrequencyDetector {
    private final FastFourierTransformer fft;
    private final int downSampleLoops;
    private final int minFrequency;
    private final int maxFrequency;
    private final int minSignalPower;
    private final boolean applyWindow;
    private final boolean applyInterpolation;

    public FrequencyDetectorHPS() {
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        minFrequency = propertyService.getInt("min.frequency", 60);
        maxFrequency = propertyService.getInt("max.frequency", 500);
        minSignalPower = propertyService.getInt("min.signal.power", 100);
        downSampleLoops = propertyService.getInt("down.sample.loops", 6);
        applyWindow = propertyService.getBoolean("harmonic_product.apply.window", false);
        applyInterpolation = propertyService.getBoolean("harmonic_product.apply.interpolation", false);
        this.fft = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < minSignalPower) {
            return Optional.empty();
        }

        double[] signalToProcess = signal;
        if (applyWindow) {
            signalToProcess = SignalUtils.applyHannWindow(signal);
        }

        double[] magnitude = calculateFFTMagnitude(signalToProcess);
        double[] harmonicProductSpectrum = calculateHarmonicProductSpectrum(magnitude);

        int resultIdx = SignalUtils.getHighestPeakIdxFreqDomainFromRange(harmonicProductSpectrum, samplingFrequency, minFrequency,
                maxFrequency, signal.length);

        double frequency;
        if (applyInterpolation) {
            frequency = calculateFrequencyWithInterpolation(resultIdx, magnitude, samplingFrequency, signal.length);
        } else {
            frequency = SignalUtils.convertFftIndexToFrequency(resultIdx, samplingFrequency, signal.length);
        }

        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }

    private double[] calculateFFTMagnitude(double[] signalToProcess) {
        Complex[] fftResult = fft.transform(signalToProcess, TransformType.FORWARD);
        Complex[] fftResultHalf = Arrays.copyOfRange(fftResult, 0, fftResult.length / 2);
        return SignalUtils.calculateMagnitude(fftResultHalf);
    }

    private double[] calculateHarmonicProductSpectrum(double[] fftMagnitude) {
        double[][] downSampledSignals = SignalUtils.downSampleSignal(fftMagnitude, downSampleLoops);
        return getHarmonicProductResult(downSampledSignals, fftMagnitude);
    }

    public double[] getHarmonicProductResult(double[][] downSampledSignals, double[] originalSignal) {
        int finalLen = downSampledSignals[downSampledSignals.length - 1].length;
        double[] result = Arrays.copyOfRange(originalSignal, 0, originalSignal.length);

        for (int i = 0; i < finalLen; i++) {
            for (double[] downSampledSignal : downSampledSignals) {
                result[i] *= downSampledSignal[i];
            }
        }
        return result;
    }

    private double calculateFrequencyWithInterpolation(int idx, double[] fftMagnitude, double samplingFrequency,
                                                       int signalLength) {
        return SignalUtils.convertFftIndexToFrequencyWithInterpolation(idx, fftMagnitude, samplingFrequency,
                signalLength);
    }
}
