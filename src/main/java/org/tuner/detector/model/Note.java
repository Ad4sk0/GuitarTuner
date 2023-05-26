package org.tuner.detector.model;

public enum Note {

    A("A"),
    A_SHARP("A#"),
    B("B"),
    C("C"),
    C_SHARP("C#"),
    D("D"),
    D_SHARP("D#"),
    E("E"),
    F("F"),
    F_SHARP("F#"),
    G("G"),
    G_SHARP("G#");

    private final String sign;

    Note(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String toString() {
        return sign;
    }
}
