package org.tuner.tool.util;

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
                bufferedWriter.write(sample + ",");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
