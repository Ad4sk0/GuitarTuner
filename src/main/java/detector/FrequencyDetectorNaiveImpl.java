package detector;

import detector.noise.NoiseReductor;
import fft.FFT;
import util.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;


class FrequencyDetectorNaiveImpl implements FrequencyDetector {

    private final FFT fft;
    private final NoiseReductor noiseReductor;
    private final int MIN_FREQUENCY = 62;

    public FrequencyDetectorNaiveImpl(FFT fft, NoiseReductor noiseReductor) {
        this.fft = fft;
        this.noiseReductor = noiseReductor;
    }

    @Override
    public Optional<Double> detectFrequency(double[] signal, float samplingFrequency) {
        double[] fftResult = fft.calculateFFT(signal);
        double[] fftResultWithoutNoise = noiseReductor.removeNoise(fftResult, samplingFrequency, MIN_FREQUENCY);
        double[] fftResultHalf = Arrays.copyOfRange(fftResultWithoutNoise, 0, fftResult.length / 2);
        int maxIdx = ArrayUtils.findIndexOfMaxValue(fftResultHalf).orElseThrow(RuntimeException::new);
        double frequency = maxIdx * (samplingFrequency / fftResult.length);
        return Optional.of(frequency);
    }
}
