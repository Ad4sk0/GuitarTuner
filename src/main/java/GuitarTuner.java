import detector.GuitarStringDetector;
import detector.GuitarStringDetectorImpl;
import detector.dto.*;
import frontend.*;
import input.InputData;
import input.InputDataListener;
import input.InputDataThread;
import input.mic.MicrophoneInput;
import util.properties.*;

import javax.sound.sampled.AudioFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuitarTuner implements InputDataListener {
    public final int DETECTION_WINDOW_SIZE;
    public final float SAMPLE_RATE;
    public final int SAMPLE_SIZE;
    public final int CHANNELS;
    public final boolean SIGNED;
    public final boolean BIG_ENDIAN;
    private final InputData input;
    private final GuitarStringDetector detector;
    private final ExecutorService executorService;
    private final TestGUI testGUI;
    public GuitarTuner() {
        // Properties
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        DETECTION_WINDOW_SIZE = propertyService.getInt("detection.window.size", 65536);
        SAMPLE_RATE = propertyService.getInt("sample.rate", 48000);
        SAMPLE_SIZE = propertyService.getInt("sample.size", 16);
        CHANNELS = propertyService.getInt("channels", 1);
        SIGNED = propertyService.getBoolean("bit.signed", true);
        BIG_ENDIAN = propertyService.getBoolean("bit.big.endian", true);

        input = new MicrophoneInput(createAudioFormat(), DETECTION_WINDOW_SIZE);
        detector = new GuitarStringDetectorImpl();
        executorService = Executors.newSingleThreadExecutor();
        testGUI = new TestGUI();
    }

    public void run() {
        input.addListener(this);
        executorService.submit(new InputDataThread(input));
    }

    private AudioFormat createAudioFormat() {
        return new AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
        );
    }

    @Override
    public void handleWindowOfSamples(double[] samplesWindow) {
        detect(samplesWindow);
    }

    private void detect(double[] samplesWindow) {
        testGUI.updateSignalChart(ChartUtils.prepareXAxisSignalIndices(samplesWindow), samplesWindow, 1500);
        Optional<DetailedPitchDetection> detectionOptional = detector.detect(samplesWindow, SAMPLE_RATE);
        if (detectionOptional.isEmpty()) {
            return;
        }
        var detection = detectionOptional.get();
        updateDetectionCharts(detection);
        updateTunerPanel(detection);
        System.out.println(detection);
    }

    private void updateTunerPanel(DetailedPitchDetection detection) {
        testGUI.setDetection(detection);
    }

    private void updateDetectionCharts(DetailedPitchDetection detection) {
        double fftMaxValue = Arrays.stream(detection.getFftResult()).max().getAsDouble();
        double hpsMaxValue = Arrays.stream(detection.getHpsResult()).max().getAsDouble();
        double[] xAxisFftIndices = ChartUtils.prepareXAxisFftIndices(detection.getFftResult(), SAMPLE_RATE, DETECTION_WINDOW_SIZE);
        int targetSize = detection.getFftResult().length;
        testGUI.updateFftChart(xAxisFftIndices, detection.getFftResult(), fftMaxValue);
        testGUI.updateHpsChart(xAxisFftIndices, detection.getHpsResult(), hpsMaxValue, targetSize);
        testGUI.updateDownSampledCharts(xAxisFftIndices, detection.getDownSampledSignals(), fftMaxValue, targetSize);
    }
}
