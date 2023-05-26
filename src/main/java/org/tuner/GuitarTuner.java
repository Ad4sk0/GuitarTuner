package org.tuner;

import org.tuner.detector.GuitarStringDetector;
import org.tuner.detector.GuitarStringDetectorImpl;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.frontend.MainWindowController;
import org.tuner.input.InputData;
import org.tuner.input.InputDataListener;
import org.tuner.input.mic.MicrophoneInput;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;

import javax.sound.sampled.AudioFormat;
import java.util.Optional;


public class GuitarTuner implements InputDataListener {
    public final int DETECTION_WINDOW_SIZE;
    public final float SAMPLE_RATE;
    public final int SAMPLE_SIZE;
    public final int CHANNELS;
    public final boolean SIGNED;
    public final boolean BIG_ENDIAN;
    private final InputData input;
    private final GuitarStringDetector detector;

    MainWindowController tunerController;

    public GuitarTuner(MainWindowController tunerController) {

        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        DETECTION_WINDOW_SIZE = propertyService.getInt("detection.window.size", 65536);
        SAMPLE_RATE = propertyService.getInt("sample.rate", 48000);
        SAMPLE_SIZE = propertyService.getInt("sample.size", 16);
        CHANNELS = propertyService.getInt("channels", 1);
        SIGNED = propertyService.getBoolean("bit.signed", true);
        BIG_ENDIAN = propertyService.getBoolean("bit.big.endian", true);

        input = new MicrophoneInput(createAudioFormat(), DETECTION_WINDOW_SIZE);
        detector = new GuitarStringDetectorImpl();

        this.tunerController = tunerController;
    }

    public void run() {
        input.addListener(this);
        input.run();
    }

    private AudioFormat createAudioFormat() {
        return new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIG_ENDIAN);
    }

    @Override
    public void handleWindowOfSamples(double[] samplesWindow) {
        detect(samplesWindow);
    }

    private void detect(double[] samplesWindow) {
        Optional<DetailedPitchDetection> detectionOptional = detector.detect(samplesWindow, SAMPLE_RATE);
        if (detectionOptional.isEmpty()) {
            return;
        }
        var detection = detectionOptional.get();
        System.out.println(detection);
        tunerController.onNewFrequencyAction(detection.getDetectedFrequency());
    }

    public void stop() {
        System.out.println("Stopping GuitarTuner");
        input.stop();
    }
}
