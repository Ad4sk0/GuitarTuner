package detector;

import detector.dto.*;
import detector.frequency.*;
import detector.model.*;
import detector.noise.NoiseReductorImpl;
import detector.pitch.*;
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
    public Optional<DetailedPitchDetection> detect(double[] signal, float sampleRate) {
        Optional<DetailedPitchDetection> detectionOptional = frequencyDetector.detectFrequency(signal, sampleRate);
        if (detectionOptional.isEmpty()) {
            return Optional.empty();
        }
        DetailedPitchDetection detection = detectionOptional.get();
        Pitch pitch = pitchDetector.detectPitch(detection.getDetectedFrequency());
        detection.setClosestPitch(pitch);
        return Optional.of(detection);
    }
}
