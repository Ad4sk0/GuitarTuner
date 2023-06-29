package org.tuner.benchmark;

import org.tuner.detector.model.Pitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class SingleBenchmarkStatistics {

    private int runs;
    private int detections;
    private int skippedDetections;
    private int correctPitchDetections;
    private int correctNoteDetections;
    private long duration;
    private double absoluteFrequencyDifferenceSum;
    private List<DetectionStatistic> detectionStatisticList = new ArrayList<>();

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
        detectionStatisticList.addAll(newSingleBenchmarkStatistics.getDetectionStatisticList());
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

    public List<DetectionStatistic> getDetectionStatisticList() {
        return detectionStatisticList;
    }

    public void setDetectionStatisticList(List<DetectionStatistic> detectionStatisticList) {
        this.detectionStatisticList = detectionStatisticList;
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

    public Map<Pitch, Integer> getCorrectNotesDetections() {
        Map<Pitch, Integer> result = new HashMap<>();
        for (var detectionStatistic : detectionStatisticList) {
            result.putIfAbsent(detectionStatistic.expectedPitch(), 0);

            if (detectionStatistic.expectedPitch() == detectionStatistic.actualPitch()) {
                result.put(detectionStatistic.expectedPitch(), result.get(detectionStatistic.expectedPitch()) + 1);
            }
        }
        return result;
    }

    public Map<Pitch, Integer> getExpectedNotesDetections() {
        Map<Pitch, Integer> result = new HashMap<>();
        for (var detectionStatistic : detectionStatisticList) {
            result.putIfAbsent(detectionStatistic.expectedPitch(), 0);
            result.put(detectionStatistic.expectedPitch(), result.get(detectionStatistic.expectedPitch()) + 1);
        }
        return result;
    }

    public Map<Pitch, Double> getAverageNotesDetections() {
        Map<Pitch, Integer> expected = getExpectedNotesDetections();
        Map<Pitch, Integer> correct = getCorrectNotesDetections();
        Map<Pitch, Double> result = new HashMap<>();

        for (var key : expected.keySet()) {
            double avg = correct.get(key) / (double) expected.get(key);
            result.put(key, avg);
        }
        return result;
    }
}
