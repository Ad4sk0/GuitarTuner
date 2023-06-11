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
        double[] signalWithWindow = SignalUtils.applyHannWindow(signal);
        double[] cepstrum = calculateCepstrum(signalWithWindow);
        double frequency = SignalUtils.getHighestFrequencyByPeakIdxTimeDomainFromRange(cepstrum, samplingFrequency,
                minFrequency, maxFrequency);
        var detection = new DetailedPitchDetection(frequency);
        detection.setSignalPower(power);
        return Optional.of(detection);
    }

    private double[] calculateCepstrum(double[] signal) {
        Complex[] fftResult = fft.transform(signal, TransformType.FORWARD);
        double[] fftResultLog = calculateAbsAndLog(fftResult);
        Complex[] ifftResult = fft.transform(fftResultLog, TransformType.INVERSE);
        return SignalUtils.getRealPart(ifftResult);
    }

    private double[] calculateAbsAndLog(Complex[] complexArray) {
        double[] result = new double[complexArray.length];
        for (int i = 0; i < complexArray.length; i++) {
            result[i] = Math.log(complexArray[i].abs());
        }
        return result;
    }
}
