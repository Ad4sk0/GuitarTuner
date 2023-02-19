package input;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioStreamConverter {

    public double[] extractSamples(AudioFormat audioFormat, byte[] data) {
        try {
            return extractSamplesFromAudioStreamThrowing(audioFormat, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double[] extractSamplesFromAudioStreamThrowing(AudioFormat audioFormat, byte[] data) throws IOException {

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
            samples[j] = bb.getShort(); // / normValue;
            if (currentBytes.length == 0) {
                break;
            }
            currentIdx = endIdx;
        }
        return samples;
    }

    private void logAudioStreamInfo(AudioFormat audioFormat, long framesNumber) {
        System.out.println("Frame rate: " + audioFormat.getFrameRate() + "Hz");
        System.out.println("Frame size: " + audioFormat.getFrameSize());
        System.out.println("Frames number: " + framesNumber);
    }
}
