package detector;

import java.util.Optional;

interface FrequencyDetector {
    Optional<Double> detectFrequency(double[] signal, float samplingFrequency);
}
