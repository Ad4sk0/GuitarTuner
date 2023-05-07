package frontend;

import util.*;

public class ChartUtils {

    private ChartUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static double[] prepareXAxisSignalIndices(double[] signal) {
        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            result[i] = i;
        }
        return result;
    }

    public static double[] prepareXAxisFftIndices(double[] signal, float sampleRate, int windowSize) {
        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            result[i] = SignalUtils.convertFftIndexToFrequency(i, sampleRate, windowSize);
        }
        return result;
    }
}
