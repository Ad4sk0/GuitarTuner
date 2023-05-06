package detector.dto;

import detector.model.*;

public class DetailedPitchDetection {
    private double detectedFrequency;
    private Pitch closestPitch;
    private double closesPitchFrequency;
    private double difference;
    private double signalPower;
    private double[] fftResult;
    private double[] hpsResult;
    private double[][] downSampledSignals;

    public DetailedPitchDetection(double detectedFrequency) {
        this.detectedFrequency = detectedFrequency;
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
        return "Detection{" +
                "detectedFrequency=" + detectedFrequency +
                ", closestPitch=" + closestPitch +
                ", closesPitchFrequency=" + closesPitchFrequency +
                ", difference=" + difference +
                ", signalPower=" + signalPower +
                '}';
    }
}
