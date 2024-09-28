package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.StrategiePrestation;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class PresentationFactureController implements Initializable {

    private String numFacture;
    private String nomPartenaire;

    @FXML
    private Label factureLabel;
    @FXML
    private TableView<Quart> quartsTable;

    @FXML
    private TableColumn<Quart, StrategiePrestation> prestationColumn;
    @FXML
    private TableColumn<Quart, LocalDate> dateColumn;
    @FXML
    private TableColumn<Quart, LocalTime> debutQuartColumn;
    @FXML
    private TableColumn<Quart, LocalTime> finQuartColumn;
    @FXML
    private TableColumn<Quart, LocalTime> pauseColumn;
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
        prestationColumn.setCellValueFactory(new PropertyValueFactory<>("stringPrestation"));
        debutQuartColumn.setCellValueFactory(new PropertyValueFactory<>("debutQuart"));
        finQuartColumn.setCellValueFactory(new PropertyValueFactory<>("finQuart"));
        pauseColumn.setCellValueFactory(new PropertyValueFactory<>("pause"));
        tempsTotalColumn.setCellValueFactory(new PropertyValueFactory<>("tempsTotal"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        tauxHoraireColumn.setCellValueFactory(new PropertyValueFactory<>("tauxHoraire"));
        employeColumn.setCellValueFactory(new PropertyValueFactory<>("nomEmploye"));
    }

    public void updateTable() {
        ArrayList<Quart> quarts = (ArrayList<Quart>) DBUtils.fetchQuartsByNumFacture(numFacture);
        this.quartsTable.getItems().setAll(quarts);
    }

    private void actionBtnAjouter() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAjouterQuartWindow(stage,numFacture,this);
        updateTable();
    }

    private void actionBtnMAJ() {
        Quart quartSelectionne = quartsTable.getSelectionModel().getSelectedItem();
        if(quartSelectionne != null) {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showModifierQuartWindow(stage,quartSelectionne);
            updateTable();
        }
        else
            Dialogs.showMessageDialog("Veuillez sélectionner un quart avant de cliquer sur le bouton Modifier.", "ERREUR MODIFICATION");
    }

    private void actionBtnSupprimer() {
        Quart quartSelectionne = quartsTable.getSelectionModel().getSelectedItem();
        if(quartSelectionne != null){
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Dialogs.showConfirmDialog("Vous êtes sur le point de supprimer un quart. Souhaitez-vous continuer?","CONFIRMATION SUPPRESSION QUART");
            //CALL DBUTIL POUR SUPPRIMER LE QUART
            if(DBUtils.supprimerQuart(quartSelectionne.getId())){
                Dialogs.showMessageDialog("Quart supprimé","CONFIRMATION SUPPRESSION QUART");
                updateTable();
            }else
                Dialogs.showMessageDialog("Problème de suppression","ERREUR SUPPRESSION");

        }
        else
            Dialogs.showMessageDialog("Veuillez sélectionner un quart avant de cliquer sur le bouton Supprimer.", "ERREUR SUPPRESSION");
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
