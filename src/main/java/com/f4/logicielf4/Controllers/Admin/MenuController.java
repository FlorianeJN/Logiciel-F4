package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnFacture;

    @FXML
    private Button btnPartenaire;

    @FXML
    private Button btnEmploye;

    @FXML
    private Button btnMonProfil;

    @FXML
    private Button btnDeconnexion;

    @FXML
    private Button btnReport;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDashboard.setOnAction(event -> actionBtnDashboard());
        btnFacture.setOnAction(event -> actionBtnFacture());
        btnPartenaire.setOnAction(event -> actionBtnPartenaire());
        btnEmploye.setOnAction(event -> actionBtnEmploye());
        btnMonProfil.setOnAction(event -> actionBtnMonProfil());
        btnDeconnexion.setOnAction(event -> actionBtnDeconnexion());
        btnReport.setOnAction(event -> actionBtnReport());
    }

    private void actionBtnDashboard(){
        System.out.println("Bouton Dashboard appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Dashboard");
    };

    private void actionBtnFacture(){
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Factures");
        System.out.println("Bouton Facture appuyé");
    };
    private void actionBtnPartenaire(){
        System.out.println("Bouton Partenaire appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Partenaires");
    };
    private void actionBtnEmploye(){
        System.out.println("Bouton Employé appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Employes");
    };
    private void actionBtnMonProfil(){
        System.out.println("Bouton Profil appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Profil");
    };
    private void actionBtnDeconnexion(){
        System.out.println("Bouton deconnexion appuyé");
        boolean deconnexion = Dialogs.showConfirmDialog("Vous êtes sur le point de vous déconnecter. Voulez-vous continuer?","CONFIRMATION DÉCONNEXION - F4 SANTÉ INC");
                if(deconnexion){
                    Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
                    stage.close();
                    Model.getInstance().getViewFactory().showLoginWindow();
                }

    };
    private void actionBtnReport(){
        System.out.println("Bouton report appuyé");
    };
}
