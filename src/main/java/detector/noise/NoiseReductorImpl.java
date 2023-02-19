package detector.noise;

import java.util.Arrays;

public class NoiseReductorImpl implements NoiseReductor {

    @Override
    public double[] removeNoise(double[] signal, float samplingFrequency, int minFrequency) {
        double[] result = Arrays.copyOf(signal, signal.length);
        int minFrequencyIdx = (int) (minFrequency / (samplingFrequency / signal.length));
        for (int i = 0; i < minFrequencyIdx; i++) {
            result[i] = 0;
        }
        return result;
    }
}
