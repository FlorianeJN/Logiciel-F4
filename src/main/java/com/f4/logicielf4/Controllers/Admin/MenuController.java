package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

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
        actionBtnDashboard();
        actionBtnFacture();
        actionBtnPartenaire();
        actionBtnEmploye();
        actionBtnMonProfil();
        actionBtnDeconnexion();
        actionBtnReport();
    }

    private void actionBtnDashboard(){
        btnDashboard.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Dashboard appuyé");
                Model.getInstance().getViewFactory().getOptionSelectionnee().set("Dashboard");
            }
        });
    };

    private void actionBtnFacture(){
        btnFacture.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Model.getInstance().getViewFactory().getOptionSelectionnee().set("Factures");
                System.out.println("Bouton Facture appuyé");
            }
        });
    };
    private void actionBtnPartenaire(){
        btnPartenaire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Partenaire appuyé");
                Model.getInstance().getViewFactory().getOptionSelectionnee().set("Partenaires");
            }
        });
    };
    private void actionBtnEmploye(){
        btnEmploye.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Employé appuyé");
                Model.getInstance().getViewFactory().getOptionSelectionnee().set("Employes");
            }
        });
    };
    private void actionBtnMonProfil(){
        btnMonProfil.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Profil appuyé");
                Model.getInstance().getViewFactory().getOptionSelectionnee().set("Profil");
            }
        });
    };
    private void actionBtnDeconnexion(){
        btnDeconnexion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Deconnexion appuyé");
            }
        });
    };
    private void actionBtnReport(){
        btnReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Report appuyé");
            }
        });
    };

}
