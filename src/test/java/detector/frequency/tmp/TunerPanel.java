//package org.tuner.frontend;
//
//import org.tuner.detector.dto.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//
//public class TunerPanel extends JPanel {
//
//    private DetailedPitchDetection detection;
//    private JLabel detectedFrequencyLabel;
//    private JLabel closestPitchLabel;
//    private JLabel differenceLabel;
//    private final Timer clearDetectionTimer;
//
//    public TunerPanel() {
//
//        this.setLayout(new GridLayout(3,0));
//
//        detectedFrequencyLabel = new JLabel();
//        closestPitchLabel = new JLabel();
//        differenceLabel = new JLabel();
//
//        detectedFrequencyLabel.setHorizontalAlignment(JLabel.CENTER);
//        detectedFrequencyLabel.setVerticalAlignment(JLabel.CENTER);
//        closestPitchLabel.setHorizontalAlignment(JLabel.CENTER);
//        closestPitchLabel.setVerticalAlignment(JLabel.CENTER);
//        differenceLabel.setHorizontalAlignment(JLabel.CENTER);
//        differenceLabel.setVerticalAlignment(JLabel.CENTER);
//
//        this.add(detectedFrequencyLabel);
//        this.add(closestPitchLabel);
//        this.add(differenceLabel);
//
//        this.setVisible(true);
//        clearDetectionTimer = new Timer(3000, clearDetectionTask);
//        clearDetectionTimer.setRepeats(false);
//    }
//
//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        if (detection == null) {
//            return;
//        }
//        detectedFrequencyLabel.setText( String.format("Detected frequency: %.2f", detection.getDetectedFrequency()));
//        closestPitchLabel.setText( String.format("Closest pitch: %s (%.2f)", detection.getClosestPitch(), detection.getClosestPitch().getFrequency()));
//        differenceLabel.setText(String.format("Difference: %.2f", detection.getDifference()));
//    }
//
//    public DetailedPitchDetection getDetection() {
//        return detection;
//    }
//
//    public void setDetection(DetailedPitchDetection detection) {
//        this.detection = detection;
//        repaint();
//        clearDetectionTimer.restart();
//    }
//
//    private final ActionListener clearDetectionTask = evt -> {
//        detection = null;
//        if (detectedFrequencyLabel != null) {
//            detectedFrequencyLabel.setText("");
//        }
//        if (closestPitchLabel != null) {
//            closestPitchLabel.setText("");
//        }
//        if (differenceLabel != null) {
//            differenceLabel.setText("");
//        }
//    };
//}
