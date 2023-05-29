package org.tuner.detector.frequency;

import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.noise.NoiseReductor;
import org.tuner.detector.noise.NoiseReductorImpl;
import org.tuner.tool.fft.FFT;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;
import org.tuner.tool.util.ArrayUtils;
import org.tuner.tool.util.SignalUtils;

import java.util.Arrays;
import java.util.Optional;


public class FrequencyDetectorHPS implements FrequencyDetector {
    private final FFT fft;
    private final NoiseReductor noiseReductor;
    private final int downSampleLoops;
    private final int minFrequency;
    private final int minSignalPower;

    public FrequencyDetectorHPS() {
        // Properties
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        downSampleLoops = propertyService.getInt("down.sample.loops", 6);
        minFrequency = propertyService.getInt("min.frequency", 50);
        minSignalPower = propertyService.getInt("min.signal.power", 100);
        this.fft = new FFT();
        this.noiseReductor = new NoiseReductorImpl();
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < minSignalPower) {
            return Optional.empty();
        }
        double[] fftResult = fft.calculateFFT(signal);
        double[] fftResultWithoutNoise = noiseReductor.removeNoise(fftResult, samplingFrequency, minFrequency);
        double[] fftResultHalf = Arrays.copyOfRange(fftResultWithoutNoise, 0, fftResult.length / 2);
        double[][] downSampledSignals = SignalUtils.downSampleSignal(fftResultHalf, downSampleLoops);
        double[] resultHps = SignalUtils.getHpsResult(downSampledSignals, fftResultHalf);
        int maxIdx = ArrayUtils.findIndexOfMaxValue(resultHps).orElseThrow(IllegalStateException::new);
        double frequency = SignalUtils.convertFftIndexToFrequency(maxIdx, samplingFrequency, fftResult.length);

        var detection = new DetailedPitchDetection(frequency);
        detection.setFftResult(fftResultHalf);
        detection.setHpsResult(resultHps);
        detection.setDownSampledSignals(downSampledSignals);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }
}
