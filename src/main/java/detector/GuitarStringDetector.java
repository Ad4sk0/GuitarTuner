package detector;

import detector.dto.*;

import java.util.Optional;

public interface GuitarStringDetector {
    Optional<DetailedPitchDetection> detect(double[] signal, float sampleRate);
}
