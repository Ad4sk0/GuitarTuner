package org.tuner.detector.observer;

public interface DetectionProducer {

    void addListener(DetectionListener detectionListener);

    void removeListener(DetectionListener detectionListener);

}
