package util;

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

}
