package org.tuner.detector.frequency;

import java.util.function.Supplier;

public enum DetectorAlgorithm {

    AUTOCORRELATION(FrequencyDetectorAutocorrelation::new),

    CEPSTRUM(FrequencyDetectorCepstrum::new),

    HPS(FrequencyDetectorHPS::new);

    private final Supplier<FrequencyDetector> implementationSupplier;

    DetectorAlgorithm(Supplier<FrequencyDetector> implementationSupplier) {
        this.implementationSupplier = implementationSupplier;
    }

    public Supplier<FrequencyDetector> getImplementationSupplier() {
        return implementationSupplier;
    }
}
