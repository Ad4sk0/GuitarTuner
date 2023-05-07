import detector.GuitarStringDetector;
import detector.GuitarStringDetectorImpl;
import detector.model.Detection;
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
        Optional<Detection> detectionOptional = detector.detect(samplesWindow, SAMPLE_RATE);
        if (detectionOptional.isEmpty()) {
            return;
        }
        Detection detection = detectionOptional.get();
        System.out.println(detection.getClosestPitch());
    }
}
