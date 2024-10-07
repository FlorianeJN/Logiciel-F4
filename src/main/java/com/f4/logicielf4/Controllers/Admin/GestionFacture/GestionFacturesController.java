package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import com.f4.logicielf4.Utilitaire.IOUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion des factures dans l'interface d'administration.
 * Ce contrôleur permet de lister, modifier, exporter les factures et d'en commencer de nouvelles.
 */
public class GestionFacturesController implements Initializable {

    @FXML
    private Button btnExporter;
    @FXML
    private Label facturesCreesLabel;

    @FXML
    private Label facturesPayeesLabel;

    @FXML
    private Label factureAttentePaiementLabel;

    @FXML
    private TableView<Facture> factureTable;

    @FXML
    private TableColumn<Facture, String> numFactureColumn;

    @FXML
    private TableColumn<Facture, String> partenaireColumn;

    @FXML
    private TableColumn<Facture, String> dateColumn;

    @FXML
    private TableColumn<Facture, Double> montantColumn;

    @FXML
    private TableColumn<Facture, String> statutColumn;

    @FXML
    private Button btnCommencer;

    @FXML
    private Button btnModifier;

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les actions des boutons et initialise les colonnes de la table des factures.
     *
     * @param url L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCommencer.setOnAction(event -> actionBtnCommencer());
        btnModifier.setOnAction(event -> actionBtnModifier());
        btnExporter.setOnAction(event -> actionBtnExporter());

        setCellValues();
        updateTable();
        // updateLabels();
    }

    /**
     * Définit les valeurs des colonnes de la table en fonction des propriétés des objets Facture.
     * Utilise des PropertyValueFactory pour mapper les colonnes aux attributs correspondants des factures.
     */
    private void setCellValues() {
        numFactureColumn.setCellValueFactory(new PropertyValueFactory<>("numFacture"));
        partenaireColumn.setCellValueFactory(new PropertyValueFactory<>("nomPartenaire"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montantApresTaxes"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        factureTable.getSortOrder().add(statutColumn); // Tri par statut
    }

    /**
     * Met à jour la table des factures avec les données actuelles depuis la base de données.
     * Calcule également le montant total des factures avant de les afficher.
     */
    private void updateTable() {
        try {
            List<Facture> factures = DBUtils.fetchAllFacture();
            for (Facture facture : factures) {
                genererMontantTotal(facture);
            }
            factureTable.getItems().setAll(factures);
            factureTable.sort();
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des factures : " + e.getMessage(), "ERREUR");
        }
    }

    /**
     * Génère le montant total d'une facture en additionnant les montants de tous les quarts associés à la facture.
     *
     * @param facture La facture pour laquelle calculer le montant total.
     */
    private void genererMontantTotal(Facture facture) {
        double montant = 0;
        String numFacture = facture.getNumFacture();
        List<Quart> listeQuarts = DBUtils.fetchQuartsByNumFacture(numFacture);
        for (Quart quart : listeQuarts) {
            montant += quart.getMontantTotal();
        }
        facture.setMontantAvantTaxes(BigDecimal.valueOf(montant));
    }

    /* private void updateLabels() {
        // Méthode pour mettre à jour les labels des factures (créées, payées, en attente).
    } */

    /**
     * Action déclenchée lors du clic sur le bouton "Commencer".
     * Ouvre la fenêtre pour créer une nouvelle facture via le ViewFactory.
     */
    private void actionBtnCommencer() {
        System.out.println("Commencer nouvelle facture");
        Stage stage = (Stage) btnCommencer.getScene().getWindow();

        // Utilisation de ViewFactory pour afficher la fenêtre de création de facture
        Model.getInstance().getViewFactory().showCommencerFactureWindow(stage);

        updateTable();
        //   updateLabels();
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Modifier".
     * Ouvre la fenêtre de modification de la facture sélectionnée.
     * Affiche un message d'erreur si aucune facture n'est sélectionnée.
     */
    private void actionBtnModifier() {
        System.out.println("Modifier facture");
        Facture factureSelectionnee = factureTable.getSelectionModel().getSelectedItem();

        if (factureSelectionnee == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner une facture avant de modifier.", "ERREUR MODIFICATION");
        } else {
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            Model.getInstance().getViewFactory().showPresentationFactureWindow(stage, factureSelectionnee.getNumFacture(), factureSelectionnee.getNomPartenaire());
            updateTable();
            //  updateLabels();
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Exporter".
     * Exporte les données de la facture sélectionnée dans un fichier.
     * Affiche un message d'erreur si aucune facture n'est sélectionnée.
     */
    private void actionBtnExporter() {
        Facture factureSelectionnee = factureTable.getSelectionModel().getSelectedItem();

        if (factureSelectionnee == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner une facture avant de cliquer sur le bouton Exporter.", "ERREUR EXPORTATION");
        } else {
            Stage stage = (Stage) btnExporter.getScene().getWindow();
            IOUtils.commencerSauvegarde(factureSelectionnee);
        }
    }
}
