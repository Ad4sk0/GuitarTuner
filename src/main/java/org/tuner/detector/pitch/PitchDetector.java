package org.tuner.detector.pitch;

import org.tuner.detector.model.Pitch;

public interface PitchDetector {
    Pitch detectPitch(double frequency);
}
