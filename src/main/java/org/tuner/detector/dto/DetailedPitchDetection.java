package org.tuner.detector.dto;

import org.tuner.detector.model.Pitch;

public class DetailedPitchDetection {
    private final double detectedFrequency;
    private Pitch closestPitch;
    private double closesPitchFrequency;
    private double difference;
    private double signalPower;

    private double[] signal = new double[0];
    private double[] fftResult = new double[0];
    private double[] hpsResult = new double[0];
    private double[][] downSampledSignals = new double[0][0];

    public DetailedPitchDetection(double detectedFrequency) {
        this.detectedFrequency = detectedFrequency;
    }

    public double[] getSignal() {
        return signal;
    }

    public void setSignal(double[] signal) {
        this.signal = signal;
    }

    public double getDetectedFrequency() {
        return detectedFrequency;
    }

    public Pitch getClosestPitch() {
        return closestPitch;
    }

    public void setClosestPitch(Pitch closestPitch) {
        this.closesPitchFrequency = closestPitch.getFrequency();
        difference = detectedFrequency - closesPitchFrequency;
        this.closestPitch = closestPitch;
    }

    public double getClosesPitchFrequency() {
        return closesPitchFrequency;
    }

    public double getDifference() {
        return difference;
    }

    public double getSignalPower() {
        return signalPower;
    }

    public void setSignalPower(double signalPower) {
        this.signalPower = signalPower;
    }

    public double[] getFftResult() {
        return fftResult;
    }

    public void setFftResult(double[] fftResult) {
        this.fftResult = fftResult;
    }

    public double[] getHpsResult() {
        return hpsResult;
    }

    public void setHpsResult(double[] hpsResult) {
        this.hpsResult = hpsResult;
    }

    public double[][] getDownSampledSignals() {
        return downSampledSignals;
    }

    public void setDownSampledSignals(double[][] downSampledSignals) {
        this.downSampledSignals = downSampledSignals;
    }

    @Override
    public String toString() {
        return "Detection{" + "detectedFrequency=" + detectedFrequency + ", closestPitch=" + closestPitch + ", closesPitchFrequency=" + closesPitchFrequency + ", difference=" + difference + ", signalPower=" + signalPower + '}';
    }
}
