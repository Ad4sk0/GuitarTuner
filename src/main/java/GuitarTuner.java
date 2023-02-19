import detector.GuitarStringDetector;
import detector.GuitarStringDetectorImpl;
import detector.model.Detection;
import input.InputData;
import input.InputDataListener;
import input.InputDataThread;
import input.mic.MicrophoneInput;

import javax.sound.sampled.AudioFormat;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuitarTuner implements InputDataListener {
    public static final int DETECTION_WINDOW_SIZE = 16384;
    public static final float SAMPLE_RATE = 48000;
    public static final int SAMPLE_SIZE = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;
    private final InputData input;
    private final GuitarStringDetector detector;
    private final ExecutorService executorService;

    public GuitarTuner() {
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
