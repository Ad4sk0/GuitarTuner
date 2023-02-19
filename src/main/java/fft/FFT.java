package fft;

import fft.complex.Complex;
import fft.complex.ComplexUtils;

public class FFT {

    /**
     * The returned result is of size padded to closest bigger power of 2.
     *
     * @param signal
     * @return
     */
    public double[] calculateFFT(double[] signal) {
        double[] signalWithCorrectLength;
        if (isPowerOf2(signal.length)) {
            signalWithCorrectLength = signal;
        } else {
            int targetLength = getClosestBiggerPowerOf2(signal.length);
            signalWithCorrectLength = padWithZeros(signal, targetLength);
        }
        Complex[] fftResult = getFFTResult(signalWithCorrectLength);
        return calculateMagnitude(fftResult);
    }

    private double[] padWithZeros(double[] signal, int targetLength) {
        double[] result = new double[targetLength];
        System.arraycopy(signal, 0, result, 0, signal.length);
        for (int i = 0; i < signal.length; i++) {
            if (signal[i] != result[i]) {
                throw new RuntimeException();
            }
        }
        return result;
    }

    private Complex[] getFFTResult(double[] signal) {
        if (!isPowerOf2(signal.length)) {
            throw new IllegalArgumentException("The array size has to be power of 2. Actual size: " + signal.length);
        }
        return calculateFFT(ComplexUtils.convertDoublesToComplex(signal));
    }

    private boolean isPowerOf2(int value) {
        if (value == 0) {
            return false;
        }
        return (value & (value - 1)) == 0;
    }

    private int getClosestBiggerPowerOf2(int value) {
        int exponent = (int) Math.ceil(Math.log(value) / Math.log(2));
        return (int) Math.pow(2, exponent);
    }

    private Complex[] calculateFFT(Complex[] signal) {
        int n = signal.length;
        if (n == 1) {
            return signal;
        }
        Complex[] evens = calculateFFT(getEvens(signal));
        Complex[] odds = calculateFFT(getOdds(signal));

        Complex[] result = new Complex[n];
        for (int i = 0; i < n / 2; i++) {

            double t = (-2 * Math.PI * i) / (double) n;
            Complex exp = (new Complex(Math.cos(t), Math.sin(t)).multiply(odds[i]));

            result[i] = evens[i].add(exp);
            result[i + n / 2] = evens[i].sub(exp);
        }
        return result;
    }

    private double[] calculateMagnitude(Complex[] fftResult) {
        double[] result = new double[fftResult.length];
        for (int i = 0; i < fftResult.length; i++) {
            result[i] = Math.sqrt(Math.pow(fftResult[i].real, 2) + Math.pow(fftResult[i].imaginary, 2));
        }
        return result;
    }

    private Complex[] getEvens(Complex[] bigArray) {
        return getEvenOrOddsIndices(bigArray, false);
    }

    private Complex[] getOdds(Complex[] bigArray) {
        return getEvenOrOddsIndices(bigArray, true);
    }

    private Complex[] getEvenOrOddsIndices(Complex[] bigArray, boolean odds) {
        int bigN = bigArray.length;
        int smallN = bigN / 2;
        if (bigN % 2 != 0) {
            throw new RuntimeException("Array size must be even: " + bigN);
        }
        int start = 0;
        if (odds) {
            start = 1;
        }
        Complex[] smallArray = new Complex[smallN];
        for (int i = start; i < bigN; i += 2) {
            smallArray[i / 2] = bigArray[i];
        }
        return smallArray;
    }
}
