package org.tuner.input;

public interface InputData {

    void addListener(InputDataListener inputDataListener);

    void run();

    void stop();

}
