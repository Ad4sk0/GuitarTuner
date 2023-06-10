package org.tuner.benchmark;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tuner.GuitarTuner;
import org.tuner.detector.GuitarStringDetectorImpl;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.frequency.DetectorAlgorithm;
import org.tuner.detector.model.Pitch;
import org.tuner.input.file.wav.WavReader;
import org.tuner.tool.util.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Runs full detectors benchmark for all algorithms. The files to test should be under
 * {@value #BENCHMARK_SAMPLES_DIRECTORY} directory. The files should be in wav format and start with pitch notation
 * following by dot, like A2.wav (any characters are allowed after first dot, for example "A2.test1.wav"). The pitch
 * must be one of {@link Pitch} enum.
 */
class DetectorsBenchmark {

    private final static String BENCHMARK_SAMPLES_DIRECTORY = "testSamples/benchmarkSamplesTmp";
    private final Logger logger = Logger.getLogger(GuitarTuner.class.getName());

    @Test
    void benchmarkDetectorAlgorithmsOnRealData() {

        List<DetectorAlgorithm> algorithmsToTest = List.of(DetectorAlgorithm.AUTOCORRELATION, DetectorAlgorithm.CEPSTRUM, DetectorAlgorithm.HPS);

        int detectionWindowSize = 2048;

        URL benchmarkSamplesResource = getClass().getClassLoader().getResource(BENCHMARK_SAMPLES_DIRECTORY);
        if (benchmarkSamplesResource == null) {
            throw new IllegalStateException(String.format("%s resource directory not present", BENCHMARK_SAMPLES_DIRECTORY));
        }
        String benchmarkSamplesDirectory = benchmarkSamplesResource.getFile();

        BenchmarkStatistics benchmarkStatistics = runBenchmark(algorithmsToTest, benchmarkSamplesDirectory, detectionWindowSize);

        printResults(benchmarkStatistics);
        try {
            writeResults(benchmarkStatistics);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save benchmark results");
        }
    }

    /**
     * Runs benchmark for list of detection algorithms and list of sample files with given window size.
     * The result are stored inside {@link BenchmarkStatistics} class responsible for formatting results of whole
     * benchmark test.
     *
     * @param algorithmsToTest    List of algorithms to test.
     * @param testFilesDirectory  List of sample files to test.
     * @param detectionWindowSize Size of detection window.
     * @return BenchmarkStatistics object with results for whole benchmark.
     */
    private BenchmarkStatistics runBenchmark(List<DetectorAlgorithm> algorithmsToTest, String testFilesDirectory,
                                             int detectionWindowSize) {
        BenchmarkStatistics benchmarkStatistics = new BenchmarkStatistics(detectionWindowSize);

        // Iterate files
        for (File sampleFile : Objects.requireNonNull(new File(testFilesDirectory).listFiles())) {
            logger.info(() -> String.format("Running benchmark for file: %s", sampleFile));
            String guitarString = sampleFile.getName().split("\\.")[0];
            Assertions.assertEquals(2, guitarString.length(),
                    "File name should start with pitch notation following by dot, like 'A2.wav'");
            Pitch expectedPitch = Pitch.valueOf(guitarString);

            // Iterate detector algorithms
            for (DetectorAlgorithm detectorAlgorithm : algorithmsToTest) {
                benchmarkStatistics.addDetectorAlgorithm(detectorAlgorithm);
                SingleBenchmarkStatistics singleBenchmarkStatistics = runSingleBenchmark(detectorAlgorithm,
                        sampleFile.getPath(), detectionWindowSize, expectedPitch);
                benchmarkStatistics.addNewResults(detectorAlgorithm, singleBenchmarkStatistics);
            }
        }
        return benchmarkStatistics;
    }

    /**
     * Runs benchmark for single detection algorithm and single sample file with given window size.
     * The result are stored inside {@link SingleBenchmarkStatistics} class responsible for formatting results of single
     * benchmark test.
     *
     * @param detectorAlgorithm   Algorithm to test.
     * @param testFilePath        Sample file to test.
     * @param detectionWindowSize Size of detection window.
     * @param expectedPitch       Expected pitch for given sample test file.
     * @return SingleBenchmarkStatistics object with results for single benchmark.
     */
    private SingleBenchmarkStatistics runSingleBenchmark(DetectorAlgorithm detectorAlgorithm, String testFilePath, int detectionWindowSize, Pitch expectedPitch) {
        WavReader wavReader = WavReader.initWithFile(testFilePath);
        GuitarStringDetectorImpl guitarStringDetector = new GuitarStringDetectorImpl();
        guitarStringDetector.setFrequencyDetector(detectorAlgorithm.getImplementationSupplier().get());

        // Temporal results
        int runs = 0;
        int detections = 0;
        int skippedDetections = 0;
        int correctPitchDetections = 0;
        int correctNoteDetections = 0;
        long duration = 0;
        List<Double> frequencyDifferenceList = new ArrayList<>();

        // Window size
        int startSample = 0;
        int endSample = startSample + detectionWindowSize;
        int step = 1000;

        // Run detection window with given step
        while (endSample < wavReader.getFramesNumber()) {

            double[] samplesToTest = wavReader.copySamplesByRange(startSample, endSample);

            // Run detector
            Instant start = Instant.now();
            Optional<DetailedPitchDetection> detectionOptional = guitarStringDetector.detect(samplesToTest, wavReader.getFrameRate());
            long windowDuration = Duration.between(start, Instant.now()).toMillis();

            if (detectionOptional.isEmpty()) {
                skippedDetections++;
            } else {
                var detection = detectionOptional.get();
                var detectedPitch = detection.getClosestPitch();

                // Judge the detection
                if (expectedPitch.equals(detectedPitch)) {
                    correctPitchDetections++;
                    correctNoteDetections++;
                } else if (expectedPitch.getNote().equals(detectedPitch.getNote())) {
                    correctNoteDetections++;
                }

                frequencyDifferenceList.add(Math.abs(expectedPitch.getFrequency() - detectedPitch.getFrequency()));
                detections++;
                duration += windowDuration;
            }

            startSample += step;
            endSample = startSample + detectionWindowSize;
            runs++;
        }

        // Collect results
        SingleBenchmarkStatistics singleBenchmarkStatistics = new SingleBenchmarkStatistics();
        singleBenchmarkStatistics.setRuns(runs);
        singleBenchmarkStatistics.setDetections(detections);
        singleBenchmarkStatistics.setSkippedDetections(skippedDetections);
        singleBenchmarkStatistics.setCorrectPitchDetections(correctPitchDetections);
        singleBenchmarkStatistics.setCorrectNoteDetections(correctNoteDetections);
        singleBenchmarkStatistics.setDuration(duration);
        singleBenchmarkStatistics.setAbsoluteFrequencyDifferenceSum(ArrayUtils.calculateAbSum(frequencyDifferenceList));
        return singleBenchmarkStatistics;
    }


    private void printResults(BenchmarkStatistics benchmarkStatistics) {
        System.out.println(benchmarkStatistics.formatResultsToPrint());
    }

    private void writeResults(BenchmarkStatistics benchmarkStatistics) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
        String fileName = String.format("benchmark_results_%s_%s.txt", benchmarkStatistics.getWindowSize(),
                LocalDateTime.now().format(dateTimeFormatter));
        Path resultDirectory = Paths.get("target/results");
        Files.createDirectories(resultDirectory);
        Path filePath = Paths.get(resultDirectory.toString(), fileName);
        BufferedWriter bufferedWriter;
        try (var fileWriter = new java.io.FileWriter(filePath.toFile())) {
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(benchmarkStatistics.formatResultsAsTable("\t", true));
            bufferedWriter.flush();
        }
    }
}
