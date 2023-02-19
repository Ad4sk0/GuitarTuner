package detector.noise;

public interface NoiseReductor {

    double[] removeNoise(double[] signal, float samplingFrequency, int minFrequency);

}
