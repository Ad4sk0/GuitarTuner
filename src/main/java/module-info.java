module guitarTuner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires commons.math3;

    opens org.tuner to javafx.fxml;
    exports org.tuner;
    exports org.tuner.detector.frequency;
    exports org.tuner.detector.model;
    exports org.tuner.detector.dto;
    opens org.tuner.frontend to javafx.fxml;
}