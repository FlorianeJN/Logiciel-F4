package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionPartenairesController implements Initializable {
    @FXML
    private Label activePartnersLabel;
    @FXML
    private Label inactivePartnersLabel;
    @FXML
    private Label totalPartnersLabel;
    @FXML
    private TableView partnersTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn addressColumn;
    @FXML
    private TableColumn phoneColumn;
    @FXML
    private TableColumn emailColumn;
    @FXML
    private TableColumn resourcePersonColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnMAJ;

    @FXML
    private Button btnSupprimer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionBtnAjouter();
        actionBtnMAJ();
        actionBtnSupprimer();
    }

    private void actionBtnAjouter(){
        btnAjouter.setOnAction(event -> {
            System.out.println("Bouton ajouter appuyé");
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            Model.getInstance().getViewFactory().showAddPartnerWindow(stage);
        });
    }

    private void actionBtnMAJ(){
        btnMAJ.setOnAction(event -> {
            System.out.println("Bouton MAJ appuyé");
        });
    }

    private void actionBtnSupprimer(){
        btnSupprimer.setOnAction(event -> {
            System.out.println("Bouton supprimer appuyé");
        });
    }
}
