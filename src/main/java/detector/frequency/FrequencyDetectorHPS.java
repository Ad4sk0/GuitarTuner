package detector.frequency;

import detector.dto.*;
import detector.noise.NoiseReductor;
import fft.FFT;
import util.ArrayUtils;
import util.SignalUtils;
import util.properties.*;

import java.util.Arrays;
import java.util.Optional;


public class FrequencyDetectorHPS implements FrequencyDetector {
    private final FFT fft;
    private final NoiseReductor noiseReductor;
    private final int DOWN_SAMPLE_LOOPS;
    private final int MIN_FREQUENCY;
    private final int MIN_SIGNAL_POWER;

    public FrequencyDetectorHPS(FFT fft, NoiseReductor noiseReductor) {
        // Properties
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        DOWN_SAMPLE_LOOPS = propertyService.getInt("down.sample.loops", 5);
        MIN_FREQUENCY = propertyService.getInt("min.frequency", 50);
        MIN_SIGNAL_POWER = propertyService.getInt("min.signal.power", 100);

        this.fft = fft;
        this.noiseReductor = noiseReductor;
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < MIN_SIGNAL_POWER) {
            return Optional.empty();
        }
        double[] signalHanning = SignalUtils.applyHanningWindow(signal);
        double[] fftResult = fft.calculateFFT(signalHanning);
        double[] fftResultWithoutNoise = noiseReductor.removeNoise(fftResult, samplingFrequency, MIN_FREQUENCY);
        double[] fftResultHalf = Arrays.copyOfRange(fftResultWithoutNoise, 0, fftResult.length / 2);
        double[][] downSampledSignals = SignalUtils.downSampleSignal(fftResultHalf, DOWN_SAMPLE_LOOPS);
        double[]  resultHps = SignalUtils.getHpsResult(downSampledSignals);
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
