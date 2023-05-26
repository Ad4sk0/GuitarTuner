package org.tuner.detector.model;

public enum Pitch {
    C1(32.70, Note.C, 1),
    C_SHARP_1(34.65, Note.C_SHARP, 1),
    D1(36.71, Note.D, 1),
    D_SHARP_1(38.89, Note.D_SHARP, 1),
    E1(41.20, Note.E, 1),
    F1(43.65, Note.F, 1),
    F_SHARP_1(46.25, Note.F_SHARP, 1),
    G1(49.00, Note.G, 1),
    G_SHARP_1(51.91, Note.G_SHARP, 1),
    A1(55.00, Note.A, 1),
    A_SHARP_1(58.27, Note.A_SHARP, 1),
    B1(61.74, Note.B, 1),

    C2(65.41, Note.C, 2),
    C_SHARP_2(69.30, Note.C_SHARP, 2),
    D2(73.42, Note.D, 2),
    D_SHARP_2(77.78, Note.D_SHARP, 2),
    E2(82.41, Note.E, 2),
    F2(87.31, Note.F, 2),
    F_SHARP_2(92.50, Note.F_SHARP, 2),
    G2(98.00, Note.G, 2),
    G_SHARP_2(103.83, Note.G_SHARP, 2),
    A2(110.00, Note.A, 2),
    A_SHARP_2(116.54, Note.A_SHARP, 2),
    B2(123.47, Note.B, 2),

    C3(130.81, Note.C, 3),
    C_SHARP_3(138.59, Note.C_SHARP, 3),
    D3(146.83, Note.D, 3),
    D_SHARP_3(155.56, Note.D_SHARP, 3),
    E3(164.81, Note.E, 3),
    F3(174.61, Note.F, 3),
    F_SHARP_3(185.00, Note.F_SHARP, 3),
    G3(196.00, Note.G, 3),
    G_SHARP_3(207.65, Note.G_SHARP, 3),
    A3(220.00, Note.A, 3),
    A_SHARP_3(233.08, Note.A_SHARP, 3),
    B3(246.94, Note.B, 3),

    C4(261.63, Note.C, 4),
    C_SHARP_4(277.18, Note.C_SHARP, 4),
    D4(293.66, Note.D, 4),
    D_SHARP_4(311.13, Note.D_SHARP, 4),
    E4(329.63, Note.E, 4),
    F4(349.23, Note.F, 4),
    F_SHARP_4(369.99, Note.F_SHARP, 4),
    G4(392.00, Note.G, 4),
    G_SHARP_4(415.30, Note.G_SHARP, 4),
    A4(440.00, Note.A, 4),
    A_SHARP_4(466.16, Note.A_SHARP, 4),
    B4(493.88, Note.B, 4),

    C5(523.25, Note.C, 5),
    C_SHARP_5(554.37, Note.C_SHARP, 5),
    D5(587.33, Note.D, 5),
    D_SHARP_5(622.25, Note.D_SHARP, 5),
    E5(659.25, Note.E, 5),
    F5(698.46, Note.F, 5),
    F_SHARP_5(739.99, Note.F_SHARP, 5),
    G5(783.99, Note.G, 5),
    G_SHARP_5(830.61, Note.G_SHARP, 5),
    A5(880.00, Note.A, 5),
    A_SHARP_5(932.33, Note.A_SHARP, 5),
    B5(987.77, Note.B, 5),

    C6(1046.50, Note.C, 6),
    C_SHARP_6(1108.73, Note.C_SHARP, 6),
    D6(1174.66, Note.D, 6),
    D_SHARP_6(1244.51, Note.D_SHARP, 6),
    E6(1318.51, Note.E, 6),
    F6(1396.91, Note.F, 6),
    F_SHARP_6(1479.98, Note.F_SHARP, 6),
    G6(1567.98, Note.G, 6),
    G_SHARP_6(1661.22, Note.G_SHARP, 6),
    A6(1760.00, Note.A, 6),
    A_SHARP_6(1864.66, Note.A_SHARP, 6),
    B6(1975.53, Note.B, 6);

    private final double frequency;
    private final Note note;
    private final int octave;

    Pitch(double frequency, Note note, int octave) {
        this.frequency = frequency;
        this.note = note;
        this.octave = octave;
    }

    public double getFrequency() {
        return frequency;
    }

    public Note getNote() {
        return note;
    }

    public int getOctave() {
        return octave;
    }

    @Override
    public String toString() {
        return note.getSign() + octave;
    }
}
