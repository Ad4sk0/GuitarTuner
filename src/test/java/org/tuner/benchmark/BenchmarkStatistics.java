package org.tuner.benchmark;

import org.tuner.detector.frequency.DetectorAlgorithm;
import org.tuner.tool.util.ArrayUtils;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

class BenchmarkStatistics {

    private final int windowSize;
    private final Map<DetectorAlgorithm, SingleBenchmarkStatistics> detectorsResults;

    public BenchmarkStatistics(int windowSize) {
        this.windowSize = windowSize;
        detectorsResults = new TreeMap<>();
    }


    public void addDetectorAlgorithm(DetectorAlgorithm detectorAlgorithm) {
        detectorsResults.putIfAbsent(detectorAlgorithm, new SingleBenchmarkStatistics());
    }

    public void addNewResults(DetectorAlgorithm detectorAlgorithm, SingleBenchmarkStatistics newSingleBenchmarkStatistics) {
        SingleBenchmarkStatistics oldSingleBenchmarkStatistics = detectorsResults.get(detectorAlgorithm);
        oldSingleBenchmarkStatistics.add(newSingleBenchmarkStatistics);
    }

    public String formatResultsToPrint() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(String.format("Benchmark results for window size: %d", windowSize));

        for (var entry : detectorsResults.entrySet()) {
            String algorithmName = entry.getKey().name();
            SingleBenchmarkStatistics results = entry.getValue();
            stringJoiner.add(algorithmName);
            stringJoiner.add(results.formatBenchmarkSummary());
        }
        return stringJoiner.toString();
    }

    public String formatResultsAsTable(String delimiter, boolean algorithmsAsColumns) {
        String[][] data = prepareResultDataAsTable(algorithmsAsColumns);
        StringJoiner lineStringJoiner = new StringJoiner("\n");
        for (String[] row : data) {
            String rowString = String.join(delimiter, row);
            lineStringJoiner.add(rowString);
        }
        return lineStringJoiner.toString();
    }

    private String[][] prepareResultDataAsTable(boolean algorithmsAsColumns) {
        List<String> headers = SingleBenchmarkStatistics.getHeaders();
        final int detectorsN = detectorsResults.size();
        final int statisticsN = headers.size();

        String[][] result = new String[detectorsN + 1][statisticsN + 1];
        result[0][0] = " ";

        // Prepare header
        for (int i = 0; i < headers.size(); i++) {
            result[0][i + 1] = headers.get(i);
        }

        // Fill rows
        int detectorIdx = 1;
        for (var entry : detectorsResults.entrySet()) {
            String algorithmName = entry.getKey().name();
            SingleBenchmarkStatistics singleBenchmarkStatistics = entry.getValue();

            List<String> statistics = singleBenchmarkStatistics.formatBenchmarkSummaryAsList();
            result[detectorIdx][0] = algorithmName;
            for (int i = 0; i < statistics.size(); i++) {
                result[detectorIdx][i + 1] = statistics.get(i);
            }
            detectorIdx++;
        }
        if (algorithmsAsColumns) {
            return ArrayUtils.transpose(result);
        }
        return result;
    }

    public int getWindowSize() {
        return windowSize;
    }
}
