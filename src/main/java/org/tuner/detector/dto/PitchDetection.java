package org.tuner.detector.dto;

import org.tuner.detector.model.Pitch;

public class PitchDetection {
    private final double detectedFrequency;
    private final Pitch closestPitch;
    private final double closesPitchFrequency;
    private final double difference;

    public PitchDetection(double detectedFrequency, Pitch closestPitch) {
        this.detectedFrequency = detectedFrequency;
        this.closestPitch = closestPitch;
        this.closesPitchFrequency = closestPitch.getFrequency();
        difference = Math.abs(detectedFrequency - closesPitchFrequency);
    }

    public double getDetectedFrequency() {
        return detectedFrequency;
    }

    public Pitch getClosestPitch() {
        return closestPitch;
    }

    public double getClosesPitchFrequency() {
        return closesPitchFrequency;
    }

    public double getDifference() {
        return difference;
    }

    @Override
    public String toString() {
        return "Detection{" +
                "detectedFrequency=" + detectedFrequency +
                ", closestPitch=" + closestPitch +
                ", closesPitchFrequency=" + closesPitchFrequency +
                ", difference=" + difference +
                '}';
    }
}
