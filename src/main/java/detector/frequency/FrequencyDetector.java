package detector.frequency;

import detector.dto.*;

import java.util.Optional;

public interface FrequencyDetector {
    Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency);
}
