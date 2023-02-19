package detector;

import detector.model.Detection;

import java.util.Optional;

public interface GuitarStringDetector {
    Optional<Detection> detect(double[] signal, float sampleRate);
}
