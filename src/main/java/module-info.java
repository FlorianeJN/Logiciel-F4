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
    exports com.f4.logicielf4.Controllers.Admin.GestionPartenaire;
    opens com.f4.logicielf4.Controllers.Admin.GestionPartenaire to javafx.fxml;
    exports com.f4.logicielf4.Controllers.Admin.TableauDeBord;
    opens com.f4.logicielf4.Controllers.Admin.TableauDeBord to javafx.fxml;
    exports com.f4.logicielf4.Controllers.Admin.GestionEmploye;
    opens com.f4.logicielf4.Controllers.Admin.GestionEmploye to javafx.fxml;
    exports com.f4.logicielf4.Controllers.Admin.GestionFacture;
    opens com.f4.logicielf4.Controllers.Admin.GestionFacture to javafx.fxml;
    exports com.f4.logicielf4.Controllers.Admin.Profil;
    opens com.f4.logicielf4.Controllers.Admin.Profil to javafx.fxml;
}
