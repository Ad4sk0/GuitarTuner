package org.tuner.input;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Logger;

public class AudioStreamConverter {

    private static final Logger logger = Logger.getLogger(AudioStreamConverter.class.getName());

    public double[] extractSamples(AudioFormat audioFormat, byte[] data) {
        return extractSamplesFromAudioStreamThrowing(audioFormat, data);
    }

    private double[] extractSamplesFromAudioStreamThrowing(AudioFormat audioFormat, byte[] data) {

        final int frameSize = audioFormat.getFrameSize();
        final long framesNumber = data.length / frameSize;
        final ByteOrder byteOrder = audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        double[] samples = new double[(int) framesNumber];

        byte[] currentBytes;
        int currentIdx = 0;
        for (int j = 0; j < framesNumber; j++) {
            int endIdx = currentIdx + frameSize;
            currentBytes = Arrays.copyOfRange(data, currentIdx, endIdx);

            // TODO
            ByteBuffer bb;
            if (frameSize == 4) {
                bb = ByteBuffer.wrap(Arrays.copyOfRange(currentBytes, 2, 4)); // Single chanel only
            } else {
                bb = ByteBuffer.wrap(currentBytes);
            }

            bb.order(byteOrder);
            samples[j] = bb.getShort();
            if (currentBytes.length == 0) {
                break;
            }
            currentIdx = endIdx;
        }
        return samples;
    }

    private void logAudioStreamInfo(AudioFormat audioFormat, long framesNumber) {
        logger.info(() -> String.format("Frame rate: %.2fHz", audioFormat.getFrameRate()));
        logger.info(() -> String.format("Frame size: %d", audioFormat.getFrameSize()));
        logger.info(() -> String.format("Frames number: %d", framesNumber));
    }
}
