module guitarTuner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;

    opens org.tuner to javafx.fxml;
    exports org.tuner;
    opens org.tuner.frontend to javafx.fxml;
}