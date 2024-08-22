module com.f4.logicielf4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.f4.logicielf4 to javafx.fxml;
    exports com.f4.logicielf4;
}