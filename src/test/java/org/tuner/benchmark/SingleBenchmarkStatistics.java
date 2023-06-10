package org.tuner.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class SingleBenchmarkStatistics {

    private int runs;
    private int detections;
    private int skippedDetections;
    private int correctPitchDetections;
    private int correctNoteDetections;
    private long duration;
    private double absoluteFrequencyDifferenceSum;

    public static List<String> getHeaders() {
        return new ArrayList<>(List.of(
                "Pitch", "Note", "AverageFrequencyDifference", "AverageDuration"
        ));
    }

    public List<String> formatBenchmarkSummaryAsList() {
        return new ArrayList<>(List.of(
                String.format("%.2f%%", getCorrectPitchDetectionsRate() * 100),
                String.format("%.2f%%", getCorrectNoteDetectionsRate() * 100),
                String.format("%.2f", getAverageFrequencyDifference()),
                String.format("%.2fms", getAverageDuration())
        ));
    }

    public void add(SingleBenchmarkStatistics newSingleBenchmarkStatistics) {
        runs += newSingleBenchmarkStatistics.getRuns();
        detections += newSingleBenchmarkStatistics.getDetections();
        skippedDetections += newSingleBenchmarkStatistics.getSkippedDetections();
        correctPitchDetections += newSingleBenchmarkStatistics.getCorrectPitchDetections();
        correctNoteDetections += newSingleBenchmarkStatistics.getCorrectNoteDetections();
        duration += newSingleBenchmarkStatistics.getDuration();
        absoluteFrequencyDifferenceSum += newSingleBenchmarkStatistics.getAbsoluteFrequencyDifferenceSum();
    }

    public String formatBenchmarkSummary() {
        List<String> headers = getHeaders();
        List<String> values = formatBenchmarkSummaryAsList();
        if (headers.size() != values.size()) {
            throw new IllegalStateException("Unable to format benchmark summary");
        }
        StringJoiner stringJoiner = new StringJoiner("\n");
        for (int i = 0; i < headers.size(); i++) {
            stringJoiner.add(String.format("%s: %s", headers.get(i), values.get(i)));
        }
        return stringJoiner.toString();
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getDetections() {
        return detections;
    }

    public void setDetections(int detections) {
        this.detections = detections;
    }

    public int getCorrectPitchDetections() {
        return correctPitchDetections;
    }

    public void setCorrectPitchDetections(int correctPitchDetections) {
        this.correctPitchDetections = correctPitchDetections;
    }

    public int getCorrectNoteDetections() {
        return correctNoteDetections;
    }

    public void setCorrectNoteDetections(int correctNoteDetections) {
        this.correctNoteDetections = correctNoteDetections;
    }

    public int getSkippedDetections() {
        return skippedDetections;
    }

    public void setSkippedDetections(int skippedDetections) {
        this.skippedDetections = skippedDetections;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getAbsoluteFrequencyDifferenceSum() {
        return absoluteFrequencyDifferenceSum;
    }

    public void setAbsoluteFrequencyDifferenceSum(double absoluteFrequencyDifferenceSum) {
        this.absoluteFrequencyDifferenceSum = absoluteFrequencyDifferenceSum;
    }

    public double getCorrectPitchDetectionsRate() {
        return correctPitchDetections / (double) detections;
    }

    public double getCorrectNoteDetectionsRate() {
        return correctNoteDetections / (double) detections;
    }

    public double getAverageFrequencyDifference() {
        return absoluteFrequencyDifferenceSum / (double) detections;
    }

    public double getAverageDuration() {
        return duration / (double) detections;
    }
}
