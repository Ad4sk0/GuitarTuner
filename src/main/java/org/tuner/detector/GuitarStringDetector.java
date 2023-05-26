package org.tuner.detector;

import org.tuner.detector.dto.DetailedPitchDetection;

import java.util.Optional;

public interface GuitarStringDetector {
    Optional<DetailedPitchDetection> detect(double[] signal, float sampleRate);
}
