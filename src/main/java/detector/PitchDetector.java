package detector;

import detector.model.Pitch;

interface PitchDetector {
    Pitch detectPitch(double frequency);
}
