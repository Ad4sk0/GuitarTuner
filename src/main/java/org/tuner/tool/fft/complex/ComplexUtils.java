package org.tuner.tool.fft.complex;

public class ComplexUtils {

    public static Complex[] convertDoublesToComplex(double[] doubleArray) {
        Complex[] result = new Complex[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            result[i] = new Complex(doubleArray[i]);
        }
        return result;
    }

    public static double[] convertComplexRealToDoubles(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = complexArray[i].real;
        }
        return result;
    }

    public static double[] convertComplexImaginaryToDoubles(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = complexArray[i].imaginary;
        }
        return result;
    }
}
