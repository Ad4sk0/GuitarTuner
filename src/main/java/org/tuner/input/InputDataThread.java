package org.tuner.input;

public class InputDataThread implements Runnable {

    private final InputData inputData;

    public InputDataThread(InputData inputData) {
        this.inputData = inputData;
    }

    @Override
    public void run() {
        inputData.run();
    }
}
