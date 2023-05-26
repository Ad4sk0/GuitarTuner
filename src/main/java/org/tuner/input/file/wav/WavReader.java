package org.tuner.input.file.wav;

import org.tuner.input.AudioStreamConverter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Logger;

public class WavReader {

    private final Logger logger = Logger.getLogger(WavReader.class.getName());
    private final ByteOrder byteOrder;
    private final int frameSize;
    private final long framesNumber;
    private final float frameRate;
    private final float durationInSec;
    private final float durationInMillis;
    private final double[] samples;
    private int samplesRead = 0;

    public WavReader(AudioInputStream audioInputStream) {
        AudioFormat audioFormat = audioInputStream.getFormat();
        frameSize = audioFormat.getFrameSize();
        frameRate = audioFormat.getFrameRate();
        byteOrder = audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        framesNumber = audioInputStream.getFrameLength();
        durationInSec = framesNumber / frameRate;
        durationInMillis = 1000 * durationInSec;
        AudioStreamConverter audioStreamConverter = new AudioStreamConverter();
        try {
            samples = audioStreamConverter.extractSamples(audioFormat, audioInputStream.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static AudioInputStream readFile(String filePath) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(new File(filePath));
    }

    public static WavReader initWithFile(String filePath) {
        try (AudioInputStream audioInputStream = readFile(filePath)) {
            return new WavReader(audioInputStream);
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new IllegalStateException(e);
        }
    }

    public void logContent() {
        logger.info(() -> String.format("Frame rate: %.2f Hz", frameRate));
        logger.info(() -> String.format("Frame size: %d", frameSize));
        logger.info(() -> String.format("Frames number: %d", framesNumber));
        logger.info(() -> String.format("Byte order: %s", byteOrder.toString()));
        logger.info(() -> String.format("Duration in sec: %.2f", durationInSec));
    }

    public double[] getSamples(int samplesNumber) {
        int samplesToReadStart = samplesRead;
        int samplesToReadEnd = samplesToReadStart + samplesNumber;
        if (samplesToReadEnd >= samples.length) {
            samplesToReadEnd = samples.length - 1;
        }
        samplesRead = samplesToReadEnd;
        return Arrays.copyOfRange(samples, samplesToReadStart, samplesToReadEnd);
    }

    public double[] copySamplesFromRange(int startSample, int samplesNumber) {
        int endSample = startSample + samplesNumber;
        if (endSample >= samples.length) {
            throw new IllegalStateException(String.format("Unable to copy samples from %d to %d. Total samples length: %d", startSample, endSample, samples.length));
        }
        return Arrays.copyOfRange(samples, startSample, endSample);
    }


    public double[] getSamplesByDurationInMs(int startMs, int endMs) {
        float framesPerMs = frameRate / 1000;
        int samplesToReadStart = (int) (startMs * framesPerMs);
        int samplesToReadEnd;
        int end = (int) (endMs * framesPerMs);
        if (end >= samples.length) {
            samplesToReadEnd = samples.length - 1;
            logger.info("End ms out of range");
        } else {
            samplesToReadEnd = end;
        }
        logger.info(() -> String.format("startMs: %d frameIdx: %d", startMs, samplesToReadStart));
        logger.info(() -> String.format("endMs: %d frameIdx: %d", endMs, samplesToReadEnd));
        return Arrays.copyOfRange(samples, samplesToReadStart, samplesToReadEnd);
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public long getFramesNumber() {
        return framesNumber;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public float getDurationInSec() {
        return durationInSec;
    }

    public float getDurationInMillis() {
        return durationInMillis;
    }

    public double[] getSamples() {
        return samples;
    }
}
