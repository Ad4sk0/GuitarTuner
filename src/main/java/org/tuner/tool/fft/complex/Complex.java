package org.tuner.tool.fft.complex;

public class Complex {
    public final double real;
    public final double imaginary;

    public Complex(double real) {
        this.real = real;
        this.imaginary = 0;
    }

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex add(Complex complex) {
        return new Complex(this.real + complex.real, this.imaginary + complex.imaginary);
    }

    public Complex sub(Complex complex) {
        return new Complex(this.real - complex.real, this.imaginary - complex.imaginary);
    }

    public Complex multiply(Complex complex) {
        return new Complex(this.real * complex.real - this.imaginary * complex.imaginary,
                this.real * complex.imaginary + this.imaginary * complex.real);
    }

    @Override
    public String toString() {
        return String.format("(%f,%fi)", real, imaginary);
    }
}
