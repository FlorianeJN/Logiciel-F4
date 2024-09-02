package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
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

        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
        setCellValues();
        updateTable();
    }

    private void setCellValues() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("courriel"));
    }

    private void actionBtnAjouter(){
            System.out.println("Bouton ajouter appuyé");
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            Model.getInstance().getViewFactory().showAddPartnerWindow(stage);
            updateTable();
    }

    private void updateTable(){
        List<Partenaire> partenaires = DBUtils.fetchAllPartners();
        partnersTable.getItems().setAll(partenaires);
    }

    private void actionBtnMAJ(){
        System.out.println("Bouton MAJ appuyé");
    }

    private void actionBtnSupprimer(){
        System.out.println("Bouton Supprimer appuyé");
    }
}
