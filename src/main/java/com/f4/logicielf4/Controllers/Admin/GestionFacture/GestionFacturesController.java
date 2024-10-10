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
        montantColumn.setCellFactory(column -> {
            return new TableCell<Facture, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f $", item));
                    }
                }
            };
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
            // Gérer l'exception si le format n'est pas valide
        }
        return 0; // Valeur par défaut si l'extraction échoue
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
            factureTable.sort(); // Appliquer le tri
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des factures : " + e.getMessage(), "ERREUR");
        }
    }

    /**
     * Met à jour les labels contenant les informations sur les factures.
     * Affiche également le montant total des paiements en attente.
     */
    private void updateLabels(){
        int facturesACompleter = DBUtils.ObtenirNombreDeFactureParStatut("À compléter");
        int facturesPrete = DBUtils.ObtenirNombreDeFactureParStatut("Prête");
        int facturesEnvoyee  = DBUtils.ObtenirNombreDeFactureParStatut("Envoyée");

        labelValueFactureACompleter.setText(String.valueOf(facturesACompleter));
        labelValueFacturePrete.setText(String.valueOf(facturesPrete));
        labelValueFactureEnvoyee.setText(String.valueOf(facturesEnvoyee ));

        BigDecimal montantPaiementEnAttente = DBUtils.getMontantPaiementEnAttente();
        labelValuePaiement.setText(String.valueOf(montantPaiementEnAttente) + " $");
    }

    /**
     * Génère le montant total d'une facture en additionnant les montants de tous les quarts associés à la facture.
     * Calcule également le montant après taxes.
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
        updateLabels();
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
