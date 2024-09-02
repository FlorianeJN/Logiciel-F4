package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    public BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       ajouterListeners();
    }

    private void ajouterListeners(){
        Model.getInstance().getViewFactory().getOptionSelectionnee().addListener(((observableValue, oldVal, newVal) -> {
            switch (newVal){
                case "Factures" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getFacturesView());
                case "Partenaires" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getPartenairesView());
                case "Employes" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getEmployesView());
                case "Profil" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getProfilView());
                default ->  admin_parent.setCenter(Model.getInstance().getViewFactory().getAdminDashboardView());
            }
        } ));
    }
}
