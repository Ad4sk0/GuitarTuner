package org.tuner.benchmark;

import org.tuner.detector.model.Pitch;

public record DetectionStatistic(Pitch expectedPitch, Pitch actualPitch, double actualFrequency) {

}
