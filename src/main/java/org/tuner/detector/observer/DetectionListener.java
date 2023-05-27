package org.tuner.detector.observer;

import org.tuner.detector.dto.DetailedPitchDetection;

public interface DetectionListener {

    void onNewDetection(DetailedPitchDetection pitchDetection);

}
