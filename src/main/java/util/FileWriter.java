package util;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileWriter {

    private FileWriter() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeSignalToFile(double[] signal, String path) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new java.io.FileWriter(path));
            for (double sample : signal) {
                bufferedWriter.write(sample + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeSignalToFile(double[] signal, double[] x, String path) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new java.io.FileWriter(path));
            for (int i = 0; i < signal.length; i++) {
                bufferedWriter.write(x[i] + " " + signal[i] + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
