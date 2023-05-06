package input.file.wav;

import input.*;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class WavReader {
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
            throw new RuntimeException(e);
        }
    }

    private static AudioInputStream readFile(String filePath) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(new File(filePath));
    }

    public static WavReader initWithFile(String filePath) {
        try (AudioInputStream audioInputStream = readFile(filePath)) {
            return new WavReader(audioInputStream);
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    public void logContent() {
        System.out.println("Frame rate: " + frameRate + "Hz");
        System.out.println("Frame size: " + frameSize);
        System.out.println("Frames number: " + framesNumber);
        System.out.println("Byte order: " + byteOrder);
        System.out.println("Duration in sec: " + durationInSec);
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
        int samplesToReadEnd = (int) (endMs * framesPerMs);
        if (samplesToReadEnd >= samples.length) {
            samplesToReadEnd = samples.length - 1;
            System.out.println("End ms out of range");
        }
        System.out.println("startMs: " + startMs + " frameIdx: " + samplesToReadStart);
        System.out.println("endMs: " + endMs + " frameIdx: " + samplesToReadEnd);
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
