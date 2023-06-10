package org.tuner.detector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tuner.benchmark.DetectorsBenchmark;
import org.tuner.detector.model.Pitch;
import org.tuner.input.file.wav.WavReader;

import java.net.URL;

public class SingleDetectionTest {

    public static final int DETECTION_WINDOW_SIZE = 2048;

    @Test
    void testSingleDetection() {
        String note = "E4";
        String fileName = String.format("%s.wav", note);
        String resourceFilePath = String.format("%s/%s", DetectorsBenchmark.BENCHMARK_SAMPLES_DIRECTORY, fileName);

        URL fileResource = getClass().getClassLoader().getResource(resourceFilePath);
        if (fileResource == null) {
            throw new IllegalStateException(String.format("%s resource file not present", resourceFilePath));
        }

        WavReader wavReader = WavReader.initWithFile(fileResource.getPath());
        double[] samples = wavReader.copySamplesFromRange(
                (int) wavReader.getFramesNumber() / 2, DETECTION_WINDOW_SIZE);

        GuitarStringDetectorImpl guitarStringDetector = new GuitarStringDetectorImpl();
        var detection = guitarStringDetector.detect(samples, wavReader.getFrameRate());
        if (detection.isEmpty()) {
            Assertions.fail("Unable to detect frequency from test sample");
        }
        Assertions.assertEquals(Pitch.E4, detection.get().getClosestPitch());
    }
}
