package org.tuner.detector.frequency;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.tuner.detector.dto.DetailedPitchDetection;
import org.tuner.tool.properties.PropertyService;
import org.tuner.tool.properties.PropertyServiceImpl;
import org.tuner.tool.util.SignalUtils;

import java.util.Optional;


public class FrequencyDetectorCepstrum implements FrequencyDetector {
    private final FastFourierTransformer fft;
    private final int minFrequency;
    private final int maxFrequency;
    private final int minSignalPower;

    public FrequencyDetectorCepstrum() {
        PropertyService propertyService = PropertyServiceImpl.INSTANCE;
        minFrequency = propertyService.getInt("min.frequency", 60);
        maxFrequency = propertyService.getInt("max.frequency", 500);
        minSignalPower = propertyService.getInt("min.signal.power", 100);
        this.fft = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    @Override
    public Optional<DetailedPitchDetection> detectFrequency(double[] signal, float samplingFrequency) {
        double power = SignalUtils.calculatePower(signal);
        if (power < minSignalPower) {
            return Optional.empty();
        }
        double[] signalHanning = SignalUtils.applyHanningWindow(signal);
        Complex[] fftResult = fft.transform(signalHanning, TransformType.FORWARD);
        double[] fftResultLog = calculateAbsAndLog(fftResult);
        Complex[] ifftResult = fft.transform(fftResultLog, TransformType.FORWARD);
        double frequency = getFrequencyFromComplex(ifftResult, samplingFrequency);
        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }

    private double[] calculateAbsAndLog(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = Math.log(complexArray[i].abs());
        }
        return result;
    }

    private double getFrequencyFromComplex(Complex[] fftResult, float samplingFrequency) {
        int minIdx = (int) (samplingFrequency / maxFrequency);
        int maxIdx = (int) (samplingFrequency / minFrequency);

        int resultIdx = minIdx;
        double maxValue = fftResult[minIdx].getReal();
        for (int i = minIdx + 1; i <= maxIdx; i++) {
            if (fftResult[i].getReal() > maxValue) {
                resultIdx = i;
                maxValue = fftResult[i].getReal();
            }
        }
        return samplingFrequency / resultIdx;
    }
}
