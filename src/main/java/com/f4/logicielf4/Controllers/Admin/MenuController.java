package com.f4.logicielf4.Controllers.Admin;

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
    private Button btnQuart;

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
        actionBtnQuart();
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
            }
        });
    };

    private void actionBtnQuart(){
        btnQuart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Quart appuyé");
            }
        });
    };
    private void actionBtnPartenaire(){
        btnPartenaire.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Partenaire appuyé");
            }
        });
    };
    private void actionBtnEmploye(){
        btnEmploye.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Employé appuyé");
            }
        });
    };
    private void actionBtnMonProfil(){
        btnMonProfil.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton Profil appuyé");
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
