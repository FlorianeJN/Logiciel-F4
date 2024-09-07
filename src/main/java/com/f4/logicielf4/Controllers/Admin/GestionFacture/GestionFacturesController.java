package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.FactureInfo;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GestionFacturesController implements Initializable {

    @FXML
    private Label facturesCreesLabel;

    @FXML
    private Label facturesPayeesLabel;

    @FXML
    private Label factureAttentePaiementLabel;

    @FXML
    private TableView<FactureInfo> factureTable;

    @FXML
    private TableColumn<FactureInfo, Integer> numFactureColumn;

    @FXML
    private TableColumn<FactureInfo, String> partenaireColumn;

    @FXML
    private TableColumn<FactureInfo, String> dateColumn;

    @FXML
    private TableColumn<FactureInfo, Double> montantColumn;

    @FXML
    private TableColumn<FactureInfo, String> statutColumn;

    @FXML
    private Button btnCommencer;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCommencer.setOnAction(event -> actionBtnCommencer());
        btnModifier.setOnAction(event -> actionBtnModifier());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());

        setCellValues();
        updateTable();
        updateLabels();
    }

    private void setCellValues() {
        numFactureColumn.setCellValueFactory(new PropertyValueFactory<>("numeroFacture"));
        partenaireColumn.setCellValueFactory(new PropertyValueFactory<>("partenaire"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        factureTable.getSortOrder().add(statutColumn); // Sorting by status
    }

    private void updateTable() {
        try {
            List<FactureInfo> factures = DBUtils.fetchAllFactureInfo();
            factureTable.getItems().setAll(factures);
            factureTable.sort();
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des factures : " + e.getMessage(), "ERREUR");
        }
    }

    private void updateLabels() {
        int facturesCrees = 0;
        int facturesPayees = 0;
        int facturesAttente = 0;
        List<FactureInfo> facturesInfo = DBUtils.fetchAllFactureInfo();

        for (FactureInfo f : facturesInfo) {
           if (f.getStatut().equals("Payée")) facturesPayees++;
            else if (f.getStatut().equals("En attente")) facturesAttente++;
        }
       facturesCrees = facturesInfo.size();

        facturesCreesLabel.setText(String.valueOf(facturesCrees));
        facturesPayeesLabel.setText(String.valueOf(facturesPayees));
        factureAttentePaiementLabel.setText(String.valueOf(facturesAttente));
    }

    private void actionBtnCommencer() {
        System.out.println("Commencer nouvelle facture");
        Stage stage = (Stage) btnCommencer.getScene().getWindow();

        /*
        *   UTILISER VIEWFACTORY POUR AFFICHER FENETRE COMMENCER FACTURE
        * */

        updateTable();
        updateLabels();
    }

    private void actionBtnModifier() {
        System.out.println("Modifier facture");
        FactureInfo factureSelectionnee = factureTable.getSelectionModel().getSelectedItem();

        if (factureSelectionnee == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner une facture avant de modifier.", "ERREUR MODIFICATION");
        } else {
            /*
             *   UTILISER VIEWFACTORY POUR AFFICHER FENETRE MODIFIER FACTURE
             * */
            updateTable();
            updateLabels();
        }
    }

    private void actionBtnSupprimer() {
        System.out.println("Supprimer facture");
        FactureInfo factureSelectionnee = factureTable.getSelectionModel().getSelectedItem();

        if (factureSelectionnee == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner une facture avant de supprimer.", "ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            /*
             *   UTILISER VIEWFACTORY POUR AFFICHER FENETRE SUPPRIMER FACTURE
             * */
            updateTable();
            updateLabels();
        }
    }
}
