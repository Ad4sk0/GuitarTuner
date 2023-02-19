package detector;

import detector.noise.NoiseReductor;
import fft.FFT;
import util.ArrayUtils;
import util.SignalUtils;

import java.util.Arrays;
import java.util.Optional;


class FrequencyDetectorHPS implements FrequencyDetector {
    private final FFT fft;
    private final NoiseReductor noiseReductor;

    private final int HPS_LOOPS = 5;

    private final int MIN_FREQUENCY = 62;
    private final int MIN_SIGNAL_POWER = 100;

    public FrequencyDetectorHPS(FFT fft, NoiseReductor noiseReductor) {
        this.fft = fft;
        this.noiseReductor = noiseReductor;
    }

    @Override
    public Optional<Double> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < MIN_SIGNAL_POWER) {
            return Optional.empty();
        }
        double[] signalHanning = SignalUtils.applyHanningWindow(signal);
        double[] fftResult = fft.calculateFFT(signalHanning);
        double[] fftResultWithoutNoise = noiseReductor.removeNoise(fftResult, samplingFrequency, MIN_FREQUENCY);
        double[] result = Arrays.copyOfRange(fftResultWithoutNoise, 0, fftResult.length / 2);
        double[] resultHps = SignalUtils.applyHps(result, HPS_LOOPS);
        int maxIdx = ArrayUtils.findIndexOfMaxValue(resultHps).orElseThrow(RuntimeException::new);
        double frequency = maxIdx * (HPS_LOOPS - 1) * (samplingFrequency / fftResult.length);
        return Optional.of(frequency);
    }
}
