package org.tuner.tool.util;

import java.util.List;
import java.util.Optional;

public class ArrayUtils {

    private ArrayUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<Integer> findIndexOfMaxValue(double[] array) {
        if (array.length == 0) {
            return Optional.empty();
        }
        int idx = 0;
        double maxValue = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                idx = i;
                maxValue = array[i];
            }
        }
        return Optional.of(idx);
    }

    public static double calculateAbSum(List<Double> list) {
        double sum = 0;
        for (double value : list) {
            sum += Math.abs(value);
        }
        return sum;
    }

    public static String[][] transpose(String[][] originalArray) {
        if (originalArray.length == 0) {
            return new String[0][0];
        }
        String[][] result = new String[originalArray[0].length][originalArray.length];
        for (int i = 0; i < originalArray[0].length; i++) {
            for (int j = 0; j < originalArray.length; j++) {
                result[i][j] = originalArray[j][i];
            }
        }
        return result;
    }

}
