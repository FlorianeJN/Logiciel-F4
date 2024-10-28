module com.f4.logicielf4 {
    // Requires JavaFX modules for UI controls and FXML
    requires javafx.controls;
    requires javafx.fxml;

    // Requires additional libraries for icons and database access
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires com.zaxxer.hikari;
    requires java.sql;

    // Requires PDFBox for PDF operations
   // requires org.apache.pdfbox;

    // Opens packages for reflection access by JavaFX FXML loader
    opens com.f4.logicielf4.Controllers to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin.GestionPartenaire to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin.TableauDeBord to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin.GestionEmploye to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin.GestionFacture to javafx.fxml;
    opens com.f4.logicielf4.Controllers.Admin.Profil to javafx.fxml;

    // Exports packages for use by other modules
    exports com.f4.logicielf4;
    exports com.f4.logicielf4.Controllers;
    exports com.f4.logicielf4.Controllers.Admin;
    exports com.f4.logicielf4.Controllers.Employee;
    exports com.f4.logicielf4.Views;
    exports com.f4.logicielf4.Models;
    exports com.f4.logicielf4.Controllers.Admin.GestionPartenaire;
    exports com.f4.logicielf4.Controllers.Admin.TableauDeBord;
    exports com.f4.logicielf4.Controllers.Admin.GestionEmploye;
    exports com.f4.logicielf4.Controllers.Admin.GestionFacture;
    exports com.f4.logicielf4.Controllers.Admin.Profil;
}
