package detector.pitch;

import detector.model.Pitch;

public interface PitchDetector {
    Pitch detectPitch(double frequency);
}
