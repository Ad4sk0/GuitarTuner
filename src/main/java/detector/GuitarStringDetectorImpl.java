package detector;

import detector.model.Detection;
import detector.model.Pitch;
import detector.noise.NoiseReductorImpl;
import fft.FFT;

import java.util.Optional;

public class GuitarStringDetectorImpl implements GuitarStringDetector {

    private final FrequencyDetector frequencyDetector;
    private final PitchDetector pitchDetector;

    public GuitarStringDetectorImpl() {
        this.frequencyDetector = new FrequencyDetectorHPS(new FFT(), new NoiseReductorImpl());
        this.pitchDetector = new PitchDetectorImpl();
    }

    @Override
    public Optional<Detection> detect(double[] signal, float sampleRate) {
        Optional<Double> frequencyDetectedOptional = frequencyDetector.detectFrequency(signal, sampleRate);
        if (frequencyDetectedOptional.isEmpty()) {
            return Optional.empty();
        }
        double frequencyDetected = frequencyDetectedOptional.get();
        Pitch pitch = pitchDetector.detectPitch(frequencyDetected);
        return Optional.of(new Detection(frequencyDetected, pitch));
    }
}
