package org.tuner.detector;

import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.frequency.FrequencyDetector;
import org.tuner.detector.frequency.FrequencyDetectorHPS;
import org.tuner.detector.model.Pitch;
import org.tuner.detector.noise.NoiseReductorImpl;
import org.tuner.detector.pitch.PitchDetector;
import org.tuner.detector.pitch.PitchDetectorImpl;
import org.tuner.tool.fft.FFT;

import java.util.Optional;

public class GuitarStringDetectorImpl implements GuitarStringDetector {

    private final FrequencyDetector frequencyDetector;
    private final PitchDetector pitchDetector;

    public GuitarStringDetectorImpl() {
        this.frequencyDetector = new FrequencyDetectorHPS(new FFT(), new NoiseReductorImpl());
//        this.frequencyDetector = new FrequencyDetectorAutocorrelation(null, new NoiseReductorImpl());
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
