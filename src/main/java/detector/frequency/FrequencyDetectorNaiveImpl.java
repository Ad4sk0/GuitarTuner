package detector.frequency;

import detector.dto.*;
import detector.noise.NoiseReductor;
import fft.FFT;
import util.ArrayUtils;
import util.properties.*;

import java.util.Arrays;
import java.util.Optional;


public class FrequencyDetectorNaiveImpl implements FrequencyDetector {

    private final FFT fft;
    private final NoiseReductor noiseReductor;
    private final int MIN_FREQUENCY;

    public FrequencyDetectorNaiveImpl(FFT fft, NoiseReductor noiseReductor) {
        // Properties
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        MIN_FREQUENCY = propertyService.getInt("min.frequency", 50);

        this.fft = fft;
        this.noiseReductor = noiseReductor;
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double[] fftResult = fft.calculateFFT(signal);
        double[] fftResultWithoutNoise = noiseReductor.removeNoise(fftResult, samplingFrequency, MIN_FREQUENCY);
        double[] fftResultHalf = Arrays.copyOfRange(fftResultWithoutNoise, 0, fftResult.length / 2);
        int maxIdx = ArrayUtils.findIndexOfMaxValue(fftResultHalf).orElseThrow(IllegalStateException::new);
        double frequency = maxIdx * (samplingFrequency / fftResult.length);
        return Optional.of(new DetailedPitchDetection(frequency));
    }
}
