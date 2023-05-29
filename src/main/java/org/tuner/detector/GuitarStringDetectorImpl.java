package org.tuner.detector;

import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.frequency.FrequencyDetector;
import org.tuner.detector.frequency.FrequencyDetectorAutocorrelation;
import org.tuner.detector.model.Pitch;
import org.tuner.detector.pitch.PitchDetector;
import org.tuner.detector.pitch.PitchDetectorImpl;

import java.util.Optional;

public class GuitarStringDetectorImpl implements GuitarStringDetector {

    private FrequencyDetector frequencyDetector;
    private final PitchDetector pitchDetector;

    public GuitarStringDetectorImpl() {
        this.frequencyDetector = new FrequencyDetectorAutocorrelation();
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

    public FrequencyDetector getFrequencyDetector() {
        return frequencyDetector;
    }

    public void setFrequencyDetector(FrequencyDetector frequencyDetector) {
        this.frequencyDetector = frequencyDetector;
    }
}
