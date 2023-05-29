package org.tuner.tool.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileWriter {

    private FileWriter() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeSignalToFile(double[] signal, String path) {
        try (var fileWriter = new java.io.FileWriter(path)) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (double sample : signal) {
                bufferedWriter.write(sample + ",");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
