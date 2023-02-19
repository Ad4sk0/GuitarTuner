package input.mic;

import input.AudioStreamConverter;
import input.InputData;
import input.InputDataListener;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class MicrophoneInput implements InputData, Runnable {

    private final int windowSize;
    private final AudioFormat audioFormat;
    private final AudioStreamConverter audioStreamConverter;
    private final TargetDataLine microphoneLine;
    private final List<InputDataListener> listeners;
    private boolean isRunning = false;

    public MicrophoneInput(AudioFormat audioFormat, int windowSize) {
        this.audioFormat = audioFormat;
        this.windowSize = windowSize;
        microphoneLine = setupMicrophone();
        listeners = new ArrayList<>();
        audioStreamConverter = new AudioStreamConverter();
    }

    public void addListener(InputDataListener inputDataListener) {
        listeners.add(inputDataListener);
    }

    void transmitData(double[] samplesWindow) {
        for (var listener : listeners) {
            listener.handleWindowOfSamples(samplesWindow);
        }
    }

    public List<Line.Info> listAvailableTargetLines() {
        List<Line.Info> result = new ArrayList<>();
        Mixer.Info[] availableMixersArray = AudioSystem.getMixerInfo();

        for (var mixerInfo : availableMixersArray) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            System.out.println("Mixer: " + mixer.getMixerInfo());

            Line.Info[] targetLineInfoArray = mixer.getTargetLineInfo();
            for (var lineInfo : targetLineInfoArray) {
                System.out.println("\t(Target Line) " + lineInfo);
                result.add(lineInfo);
            }
        }
        return result;
    }

    private TargetDataLine setupMicrophone() {
        DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(targetDataLineInfo)) {
            throw new IllegalStateException("targetDataLine not supported");
        }
        try {
            return (TargetDataLine) AudioSystem.getLine(targetDataLineInfo);
        } catch (LineUnavailableException e) {
            throw new IllegalStateException("Unable to get TargetDataLine");
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected Exception: " + e.getMessage());
        }
    }

    private void startMicrophone() {
        try {
            microphoneLine.open(audioFormat);
            microphoneLine.start();
        } catch (LineUnavailableException e) {
            throw new IllegalStateException("Unable to start microphone: " + e);
        }
    }

    private void endMicrophone() {
        microphoneLine.stop();
        microphoneLine.close();
    }

    public void run() {
        isRunning = true;

        startMicrophone();
        var audioInputStream = new AudioInputStream(microphoneLine);
        double[] detectionWindow = new double[windowSize];
        boolean initialized = false;

        try {
            while (isRunning) {
                int bytesToRead;
                if (!initialized) {
                    bytesToRead = windowSize * audioInputStream.getFormat().getFrameSize();
                    initialized = true;
                } else {
                    bytesToRead = audioInputStream.available();
                }
                byte[] bytes = audioInputStream.readNBytes(bytesToRead);
                double[] samples = audioStreamConverter.extractSamples(audioInputStream.getFormat(), bytes);
                detectionWindow = adjustWindow(detectionWindow, samples);
                transmitData(detectionWindow);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to get bytes of data " + e.getMessage());
        } finally {
            endMicrophone();
            System.out.println("Finished");
        }
    }

    private double[] adjustWindow(double[] window, double[] newData) {
        double[] result = new double[window.length];
        if (newData.length > window.length) {
            int newDataStartIdx = newData.length - window.length;
            System.arraycopy(newData, newDataStartIdx, result, 0, window.length);
            return result;
        }
        int windowNewDataStartIdx = window.length - newData.length;
        System.arraycopy(window, newData.length, result, 0, windowNewDataStartIdx);
        System.arraycopy(newData, 0, result, windowNewDataStartIdx, newData.length);
        return result;
    }


    public void stop() {
        isRunning = false;
    }
}
