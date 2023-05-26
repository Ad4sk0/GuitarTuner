package org.tuner.input;

public interface InputDataListener {
    void handleWindowOfSamples(double[] samplesWindow);
}
