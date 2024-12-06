package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import com.f4.logicielf4.Utilitaire.IOUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des factures dans l'interface d'administration.
 * Ce contrôleur permet de lister, modifier, exporter les factures et d'en commencer de nouvelles.
 */
public class GestionFacturesController implements Initializable {

    @FXML
    private Button btnExporter;
    @FXML
    private Label labelValueFactureACompleter;

    @FXML
    private Label labelValueFacturePrete;

    @FXML
    private Label labelValueFactureEnvoyee;

    @FXML
    private Label labelValuePaiement;

    @FXML
    private TableView<Facture> factureTable;

    @FXML
    private TableColumn<Facture, String> numFactureColumn;

    @FXML
    private TableColumn<Facture, String> partenaireColumn;

    @FXML
    private TableColumn<Facture, String> dateColumn;

    @FXML
    private TableColumn<Facture, BigDecimal> montantColumn;

    @FXML
    private TableColumn<Facture, String> statutColumn;

    @FXML
    private Button btnCommencer;

    @FXML
    private Button btnModifier;

    private static final Logger LOGGER = Logger.getLogger(GestionFacturesController.class.getName());

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les actions des boutons et initialise les colonnes de la table des factures.
     *
     * @param url            L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCommencer.setOnAction(event -> actionBtnCommencer());
        btnModifier.setOnAction(event -> actionBtnModifier());
        btnExporter.setOnAction(event -> actionBtnExporter());

        setCellValues();
        updateTable();
        updateLabels();
    }

    /**
     * Définit les valeurs des colonnes de la table en fonction des propriétés des objets Facture.
     * Utilise des PropertyValueFactory pour mapper les colonnes aux attributs correspondants des factures.
     * Configure également un comparateur personnalisé pour trier les factures par numéro décroissant.
     * Personnalise l'affichage du montant avec le symbole "$".
     */
    private void setCellValues() {
        numFactureColumn.setCellValueFactory(new PropertyValueFactory<>("numFacture"));
        // Comparateur personnalisé pour trier en fonction du numéro avant le premier tiret
        numFactureColumn.setComparator((s1, s2) -> {
            int num1 = extractNumFromNumFacture(s1);
            int num2 = extractNumFromNumFacture(s2);
            // Comparaison en ordre naturel (ascendant)
            return Integer.compare(num1, num2);
        });
        numFactureColumn.setSortType(TableColumn.SortType.DESCENDING); // Tri en ordre décroissant

        partenaireColumn.setCellValueFactory(new PropertyValueFactory<>("nomPartenaire"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));

        // Mettre à jour pour utiliser montantApresTaxes
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montantApresTaxes"));
        // Personnalisation de l'affichage du montant avec le symbole "$"
        montantColumn.setCellFactory(column -> new TableCell<Facture, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f $", item));
                }
            }
        });

        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Ajouter numFactureColumn à l'ordre de tri
        factureTable.getSortOrder().clear();
        factureTable.getSortOrder().add(numFactureColumn);
    }

    /**
     * Extrait le numéro de facture avant le premier tiret pour le tri personnalisé.
     *
     * @param numFacture Le numéro de facture sous la forme "num-MM-YYYY".
     * @return Le numéro de facture en tant qu'entier.
     */
    private int extractNumFromNumFacture(String numFacture) {
        try {
            String[] parts = numFacture.split("-");
            if (parts.length > 0) {
                return Integer.parseInt(parts[0]);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Numéro de facture invalide: " + numFacture, e);
        }
        return 0; // Valeur par défaut si l'extraction échoue
    }

    /**
     * Met à jour la table des factures avec les données actuelles depuis la base de données.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     * Calcule également le montant total des factures avant de les afficher.
     */
    private void updateTable() {
        Task<ObservableList<Facture>> task = new Task<ObservableList<Facture>>() {
            @Override
            protected ObservableList<Facture> call() throws Exception {
                List<Facture> factures = DBUtils.fetchAllFacture();
                for (Facture facture : factures) {
                    genererMontantTotal(facture);
                }
                return FXCollections.observableArrayList(factures);
            }
        };

        task.setOnSucceeded(event -> {
            factureTable.getItems().setAll(task.getValue());
            factureTable.sort(); // Appliquer le tri
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des factures.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des factures", ex);
        });

        new Thread(task).start();
    }

    /**
     * Met à jour les labels contenant les informations sur les factures.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void updateLabels() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int facturesACompleter = DBUtils.ObtenirNombreDeFactureParStatut("À compléter");
                int facturesPrete = DBUtils.ObtenirNombreDeFactureParStatut("Prête");
                int facturesEnvoyee = DBUtils.ObtenirNombreDeFactureParStatut("Envoyée");
                BigDecimal montantPaiementEnAttente = DBUtils.getMontantPaiementEnAttente();

                // Mise à jour des labels sur le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    labelValueFactureACompleter.setText(String.valueOf(facturesACompleter));
                    labelValueFacturePrete.setText(String.valueOf(facturesPrete));
                    labelValueFactureEnvoyee.setText(String.valueOf(facturesEnvoyee));
                    labelValuePaiement.setText(String.format("%.2f $", montantPaiementEnAttente));
                });

                return null;
            }
        };

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des statistiques.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques", ex);
        });

        new Thread(task).start();
    }

    /**
     * Génère le montant total d'une facture en additionnant les montants de tous les quarts associés à la facture.
     * Calcule également le montant après taxes.
     *
     * @param facture La facture pour laquelle calculer le montant total.
     */
    private void genererMontantTotal(Facture facture) {
        try {
            double montant = 0;
            String numFacture = facture.getNumFacture();

            List<Quart> listeQuarts = DBUtils.fetchQuartsByNumFacture(numFacture);
            for (Quart quart : listeQuarts) {
                montant += quart.getMontantTotal();
            }
            facture.setMontantAvantTaxes(BigDecimal.valueOf(montant));

        } catch (Exception e) {
           // LOGGER.log(Level.SEVERE, "Erreur lors du calcul du montant total pour la facture " + facture.getNumFacture(), e);
            System.out.println("Erreur lors du calcul du montant total pour la facture ");
        }
    }


    /**
     * Action déclenchée lors du clic sur le bouton "Commencer".
     * Ouvre la fenêtre pour créer une nouvelle facture via le ViewFactory.
     */
    private void actionBtnCommencer() {
        Stage stage = (Stage) btnCommencer.getScene().getWindow();

        // Utilisation de ViewFactory pour afficher la fenêtre de création de facture
        Model.getInstance().getViewFactory().showCommencerFactureWindow(stage);

        updateTable();
        updateLabels();
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Modifier".
     * Ouvre la fenêtre de modification de la facture sélectionnée.
     * Affiche un message d'erreur si aucune facture n'est sélectionnée.
     */
    private void actionBtnModifier() {
        Facture factureSelectionnee = factureTable.getSelectionModel().getSelectedItem();

        if (factureSelectionnee == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner une facture avant de modifier.", "ERREUR MODIFICATION");
        } else {
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            Model.getInstance().getViewFactory().showPresentationFactureWindow(stage, factureSelectionnee.getNumFacture(), factureSelectionnee.getNomPartenaire());
            updateTable();
            updateLabels();
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
