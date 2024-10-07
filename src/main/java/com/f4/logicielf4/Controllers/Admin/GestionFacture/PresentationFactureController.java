package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.StrategiePrestation;
import com.f4.logicielf4.Models.Employe;
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
import java.util.ResourceBundle;

/**
 * Contrôleur pour l'affichage des détails d'une facture spécifique, y compris la gestion des quarts associés.
 * Ce contrôleur permet d'ajouter, de modifier et de supprimer des quarts associés à une facture donnée.
 */
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

    /**
     * Constructeur pour initialiser le contrôleur avec une facture spécifique.
     *
     * @param numFacture    Le numéro de la facture.
     * @param nomPartenaire Le nom du partenaire associé à la facture.
     */
    public PresentationFactureController(String numFacture, String nomPartenaire) {
        this.numFacture = numFacture;
        this.nomPartenaire = nomPartenaire;
    }

    /**
     * Configure les colonnes de la table des quarts en fonction des propriétés des objets Quart.
     * Utilise des PropertyValueFactory pour lier les colonnes aux attributs correspondants des quarts.
     */
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

    /**
     * Met à jour la table des quarts avec les données actuelles pour la facture donnée.
     * Récupère les quarts associés à la facture à partir de la base de données.
     */
    public void updateTable() {
        ArrayList<Quart> quarts = (ArrayList<Quart>) DBUtils.fetchQuartsByNumFacture(numFacture);
        this.quartsTable.getItems().setAll(quarts);
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Ajouter".
     * Ouvre la fenêtre pour ajouter un nouveau quart à la facture actuelle et met à jour la table après l'ajout.
     */
    private void actionBtnAjouter() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAjouterQuartWindow(stage, numFacture, this);
        updateTable();
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Modifier".
     * Ouvre la fenêtre pour modifier le quart sélectionné.
     * Affiche un message d'erreur si aucun quart n'est sélectionné.
     */
    private void actionBtnMAJ() {
        Quart quartSelectionne = quartsTable.getSelectionModel().getSelectedItem();
        if (quartSelectionne != null) {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showModifierQuartWindow(stage, quartSelectionne);
            updateTable();
        } else {
            Dialogs.showMessageDialog("Veuillez sélectionner un quart avant de cliquer sur le bouton Modifier.", "ERREUR MODIFICATION");
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Supprimer".
     * Supprime le quart sélectionné de la base de données après confirmation de l'utilisateur.
     * Affiche un message d'erreur si aucun quart n'est sélectionné.
     */
    private void actionBtnSupprimer() {
        Quart quartSelectionne = quartsTable.getSelectionModel().getSelectedItem();
        if (quartSelectionne != null) {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Dialogs.showConfirmDialog("Vous êtes sur le point de supprimer un quart. Souhaitez-vous continuer?", "CONFIRMATION SUPPRESSION QUART");
            if (DBUtils.supprimerQuart(quartSelectionne.getId())) {
                Dialogs.showMessageDialog("Quart supprimé", "CONFIRMATION SUPPRESSION QUART");
                updateTable();
            } else {
                Dialogs.showMessageDialog("Problème de suppression", "ERREUR SUPPRESSION");
            }
        } else {
            Dialogs.showMessageDialog("Veuillez sélectionner un quart avant de cliquer sur le bouton Supprimer.", "ERREUR SUPPRESSION");
        }
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les colonnes de la table, met à jour la table des quarts, et lie les actions des boutons.
     *
     * @param url            L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        factureLabel.setText("Facture # " + numFacture + " - " + nomPartenaire);
        setCellValues();
        updateTable();

        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
    }
}
