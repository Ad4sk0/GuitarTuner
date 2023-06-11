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

    public FrequencyDetectorHPS() {
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        minFrequency = propertyService.getInt("min.frequency", 60);
        maxFrequency = propertyService.getInt("max.frequency", 500);
        minSignalPower = propertyService.getInt("min.signal.power", 100);
        downSampleLoops = propertyService.getInt("down.sample.loops", 6);
        this.fft = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < minSignalPower) {
            return Optional.empty();
        }
        double[] harmonicProductSpectrum = calculateHarmonicProductSpectrum(signal);
        double frequency = SignalUtils.getHighestFrequencyByPeakIdxFreqDomainFromRange(harmonicProductSpectrum, samplingFrequency,
                minFrequency, maxFrequency, signal.length);
        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }

    private double[] calculateHarmonicProductSpectrum(double[] signal) {
        Complex[] fftResult = fft.transform(signal, TransformType.FORWARD);
        Complex[] fftResultHalf = Arrays.copyOfRange(fftResult, 0, fftResult.length / 2);
        double[] magnitude = SignalUtils.calculateMagnitude(fftResultHalf);
        double[][] downSampledSignals = SignalUtils.downSampleSignal(magnitude, downSampleLoops);
        return getHpsResult(downSampledSignals, magnitude);
    }

    public static double[] getHpsResult(double[][] downSampledSignals, double[] originalSignal) {
        int finalLen = downSampledSignals[downSampledSignals.length - 1].length;
        double[] result = Arrays.copyOfRange(originalSignal, 0, originalSignal.length);

        for (int i = 0; i < finalLen; i++) {
            for (double[] downSampledSignal : downSampledSignals) {
                result[i] *= downSampledSignal[i];
            }
        }
        return result;
    }
}
