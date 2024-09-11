package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.StrategiePrestation;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Quart;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;

public class PresentationFactureController implements Initializable {

    private String numFacture;
    private String nomPartenaire;

    @FXML
    private Label factureLabel;
    @FXML
    private TableView<Quart> employeesTable;
    @FXML
    private TableColumn<Quart, Date> dateColumn;
    @FXML
    private TableColumn<Quart, StrategiePrestation> prestationColumn;
    @FXML
    private TableColumn<Quart, Time> debutQuartColumn;
    @FXML
    private TableColumn<Quart, Time> finQuartColumn;
    @FXML
    private TableColumn<Quart, Time> pauseColumn;
    @FXML
    private TableColumn<Quart, Double> tempsTotalColumn;
    @FXML
    private TableColumn<Quart, Double> montantTotalColumn;
    @FXML
    private TableColumn<Quart, Double> tauxHoraireColumn;
    @FXML
    private TableColumn<Quart, Employe> employeColumn;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnMAJ;
    @FXML
    private Button btnSupprimer;

    public PresentationFactureController(String numFacture,String nomPartenaire) {
        this.numFacture = numFacture;
        this.nomPartenaire = nomPartenaire;
    }

    private void setCellValues() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateQuart"));
        prestationColumn.setCellValueFactory(new PropertyValueFactory<>("prestation"));
        debutQuartColumn.setCellValueFactory(new PropertyValueFactory<>("debutQuart"));
        finQuartColumn.setCellValueFactory(new PropertyValueFactory<>("finQuart"));
        pauseColumn.setCellValueFactory(new PropertyValueFactory<>("pause"));
        tempsTotalColumn.setCellValueFactory(new PropertyValueFactory<>("tempsTotal"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        tauxHoraireColumn.setCellValueFactory(new PropertyValueFactory<>("tauxHoraire"));
        employeColumn.setCellValueFactory(new PropertyValueFactory<>("employe"));
    }

    private void updateTable() {
        // Implement table update logic
    }

    private void actionBtnAjouter() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAjouterQuartWindow(stage,numFacture);
    }

    private void actionBtnMAJ() {
        // Implement update action
    }

    private void actionBtnSupprimer() {
        // Implement delete action
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        factureLabel.setText("Facture # " + numFacture+" - " + nomPartenaire);
        setCellValues();
        updateTable();

        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
    }
}
