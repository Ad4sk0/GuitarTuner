package detector.pitch;

import detector.model.Pitch;

import java.util.Arrays;

public class PitchDetectorImpl implements PitchDetector {

    private final static Pitch[] pitchList = Pitch.values();
    private final static Double[] frequencies = Arrays.stream(pitchList).map(Pitch::getFrequency).toArray(Double[]::new);

    @Override
    public Pitch detectPitch(double frequency) {
        return findClosestPitch(frequency);
    }

    Pitch findClosestPitch(double frequency) {
        int binarySearchResult = Arrays.binarySearch(frequencies, frequency);

        // Exact match
        if (binarySearchResult > 0) {
            return pitchList[binarySearchResult];
        }

        int pos = Math.abs(binarySearchResult);

        // Smaller than any element
        if (pos == 1) {
            return pitchList[0];
        }

        // Bigger than any element
        if (pos == pitchList.length + 1) {
            return pitchList[pitchList.length - 1];
        }

        Pitch smaller = pitchList[pos - 2];
        Pitch bigger = pitchList[pos - 1];
        double diffToSmaller = frequency - smaller.getFrequency();
        double diffToBigger = bigger.getFrequency() - frequency;

        if (diffToSmaller < diffToBigger) {
            return smaller;
        } else {
            return bigger;
        }
    }
}
