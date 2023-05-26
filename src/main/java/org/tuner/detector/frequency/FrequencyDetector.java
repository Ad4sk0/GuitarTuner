package org.tuner.detector.frequency;

import org.tuner.detector.dto.DetailedPitchDetection;

import java.util.Optional;

public interface FrequencyDetector {
    Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency);
}
