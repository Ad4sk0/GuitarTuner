module guitarTuner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.tuner to javafx.fxml;
    exports org.tuner;
    opens org.tuner.frontend to javafx.fxml;
}