package org.tuner;

import org.tuner.detector.GuitarStringDetector;
import org.tuner.detector.GuitarStringDetectorImpl;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.detector.observer.DetectionListener;
import org.tuner.detector.observer.DetectionProducer;
import org.tuner.input.InputData;
import org.tuner.input.InputDataListener;
import org.tuner.input.mic.MicrophoneInput;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class GuitarTuner implements InputDataListener, DetectionProducer {

    public final int detectionWindowSize;
    public final float sampleRate;
    public final int sampleSize;
    public final int channels;
    public final boolean signed;
    public final boolean bigEndian;
    private final Logger logger = Logger.getLogger(GuitarTuner.class.getName());
    private final InputData input;
    private final GuitarStringDetector detector;
    private final List<DetectionListener> detectionListenerList;

    public GuitarTuner() {

        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        detectionWindowSize = propertyService.getInt("detection.window.size", 65536);
        sampleRate = propertyService.getInt("sample.rate", 48000);
        sampleSize = propertyService.getInt("sample.size", 16);
        channels = propertyService.getInt("channels", 1);
        signed = propertyService.getBoolean("bit.signed", true);
        bigEndian = propertyService.getBoolean("bit.big.endian", true);

        input = new MicrophoneInput(createAudioFormat(), detectionWindowSize);
        detector = new GuitarStringDetectorImpl();
        detectionListenerList = new ArrayList<>();
    }

    public void run() {
        input.addListener(this);
        input.run();
    }

    private AudioFormat createAudioFormat() {
        return new AudioFormat(sampleRate, sampleSize, channels, signed, bigEndian);
    }

    @Override
    public void handleWindowOfSamples(double[] samplesWindow) {
        detect(samplesWindow);
    }

    private void detect(double[] samplesWindow) {
        Optional<DetailedPitchDetection> detectionOptional = detector.detect(samplesWindow, sampleRate);
        if (detectionOptional.isEmpty()) {
            return;
        }
        var detection = detectionOptional.get();
        logger.info(detection::toString);
        informListeners(detection);
    }

    public void stop() {
        logger.info("Stopping GuitarTuner");
        input.stop();
    }

    @Override
    public void addListener(DetectionListener detectionListener) {
        detectionListenerList.add(detectionListener);
    }

    @Override
    public void removeListener(DetectionListener detectionListener) {
        detectionListenerList.remove(detectionListener);
    }

    private void informListeners(DetailedPitchDetection detection) {
        for (DetectionListener detectionListener : detectionListenerList) {
            detectionListener.onNewDetection(detection);
        }
    }
}
