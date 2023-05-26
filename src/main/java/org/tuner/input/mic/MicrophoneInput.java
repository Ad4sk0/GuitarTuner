package org.tuner.input.mic;

import org.tuner.input.AudioStreamConverter;
import org.tuner.input.InputData;
import org.tuner.input.InputDataListener;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MicrophoneInput implements InputData, Runnable {
    private final int windowSize;
    private final AudioFormat audioFormat;
    private final AudioStreamConverter audioStreamConverter;
    private final TargetDataLine microphoneLine;
    private final List<InputDataListener> listeners;
    private final Logger logger = Logger.getLogger(MicrophoneInput.class.getName());
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
            logger.info("Mixer: " + mixer.getMixerInfo());

            Line.Info[] targetLineInfoArray = mixer.getTargetLineInfo();
            for (var lineInfo : targetLineInfoArray) {
                logger.info(() -> String.format("\t(Target Line) %s", lineInfo));
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
            e.printStackTrace();
            throw new IllegalStateException("Unable to get bytes of data " + e.getMessage());
        } finally {
            endMicrophone();
            logger.info("Finished");
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
        logger.info("Stopping Microphone input");
        isRunning = false;
    }
}
