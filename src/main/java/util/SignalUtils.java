package util;

public class SignalUtils {

    private SignalUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static double calculatePower(double[] signal) {
        double sum = 0;
        for (double value : signal) {
            sum += Math.abs(value);
        }
        return sum / signal.length;
    }

    public static double[] applyHanningWindow(double[] array) {
        int n = array.length;
        double[] result = new double[n];

        double[] h = new double[n];
        int startX = 1 - n;
        int step = 2;
        for (int i = 0; i < n; i++) {
            h[i] = startX + i * step;
        }

        for (int i = 0; i < n; i++) {
            h[i] = 0.5 + 0.5 * Math.cos(Math.PI * h[i] / (n - 1));
        }

        for (int i = 0; i < n; i++) {
            result[i] = array[i] * h[i];
        }
        return result;
    }

    public static double[] applyHps(double[] fftResult, int n) {
        double[][] hpsResult = new double[n][];
        for (int i = 0; i < n; i++) {
            int divisor = 2 + i;
            int currentLen = fftResult.length / (2 + i);
            hpsResult[i] = new double[currentLen];
            for (int j = 0; j < currentLen; j++) {
                int originalIdx = j * divisor;
                hpsResult[i][j] = fftResult[originalIdx];
            }
        }
        int finalLen = hpsResult[n - 1].length;
        double[] result = new double[finalLen];
        for (int i = 0; i < finalLen; i++) {
            result[i] = 1;
            for (int j = 0; j < n; j++) {
                result[i] *= hpsResult[j][i];
            }
        }
        return result;
    }

}
