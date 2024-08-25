module com.f4.logicielf4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;

    opens com.f4.logicielf4.Controllers to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin to javafx.fxml;

    exports com.f4.logicielf4;
    exports com.f4.logicielf4.Controllers;
    exports com.f4.logicielf4.Controllers.Admin;
    exports com.f4.logicielf4.Controllers.Employee;
    exports com.f4.logicielf4.Views;
    exports com.f4.logicielf4.Models;
}
