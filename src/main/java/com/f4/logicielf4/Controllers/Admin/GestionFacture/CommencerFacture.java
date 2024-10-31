package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contrôleur pour la création d'une nouvelle facture.
 * Ce contrôleur permet à l'utilisateur de sélectionner un partenaire et de choisir une date de facture (mois/année).
 * Une fois les informations saisies, il génère un numéro de facture unique et crée une nouvelle facture dans la base de données.
 */
public class CommencerFacture {

    @FXML
    private Button btnContinuer;
    @FXML
    private ComboBox<String> partnerComboBox;
    @FXML
    private DatePicker invoiceDatePicker;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final Logger LOGGER = Logger.getLogger(CommencerFacture.class.getName());

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure le bouton "Continuer" et initialise les composants de sélection de la date de facture et des partenaires.
     */
    @FXML
    public void initialize() {
        btnContinuer.setOnAction(event -> actionBtnContinuer());
        setupInvoiceDatePicker();
        setupPartnerComboBox();
    }

    /**
     * Remplit la comboBox des partenaires avec les noms des partenaires actifs extraits de la base de données.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void setupPartnerComboBox() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                List<Partenaire> listePartenaires = DBUtils.fetchAllActivePartners();
                return listePartenaires.stream()
                        .map(Partenaire::getNom)
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            List<String> listeNomsPartenaires = task.getValue();
            partnerComboBox.getItems().clear();
            partnerComboBox.getItems().addAll(listeNomsPartenaires);
        });

        task.setOnFailed(event -> {
            Throwable e = task.getException();
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des partenaires", e);
            Dialogs.showMessageDialog("Erreur lors du chargement des partenaires.", "ERREUR");
        });

        new Thread(task).start();
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Continuer".
     * Valide les informations saisies, génère un numéro de facture unique,
     * crée la facture dans la base de données, et propose à l'utilisateur d'ajouter des quarts à la facture.
     */
    private void actionBtnContinuer() {
        LocalDate invoiceDate = invoiceDatePicker.getValue();
        String partner = partnerComboBox.getValue();

        if (validateFields(invoiceDate, partner)) {
            Task<Boolean> task = new Task<>() {
                private String numFacture;

                @Override
                protected Boolean call() throws Exception {
                    numFacture = genererNumFacture(invoiceDate);
                    return DBUtils.createNewInvoice(numFacture, partner, invoiceDate, "À compléter");
                }

                @Override
                protected void succeeded() {
                    boolean success = getValue();
                    if (success) {
                        Stage stage = (Stage) btnContinuer.getScene().getWindow();
                        stage.close();

                        Platform.runLater(() -> {
                            boolean addQuarts = Dialogs.showConfirmDialog(
                                    "La facture " + numFacture + " a été créée! Voulez-vous y ajouter des quarts ?",
                                    "MESSAGE CONFIRMATION + AJOUTS DE QUARTS"
                            );

                            if (addQuarts) {
                                Model.getInstance().getViewFactory().showPresentationFactureWindow(stage, numFacture, partner);
                            }
                        });
                    } else {
                        Platform.runLater(() -> Dialogs.showMessageDialog(
                                "La facture n'a pas pu être créée.",
                                "ERREUR CRÉATION FACTURE"
                        ));
                    }
                }


                @Override
                protected void failed() {
                    Throwable e = getException();
                    LOGGER.log(Level.SEVERE, "Erreur lors de la création de la facture", e);
                    Dialogs.showMessageDialog("Erreur lors de la création de la facture.", "ERREUR");
                }
            };

            new Thread(task).start();
        } else {
            Dialogs.showMessageDialog("Assurez-vous de bien remplir tous les champs.", "ERREUR REMPLISSAGE CHAMPS");
        }
    }

    /**
     * Valide que les champs essentiels sont correctement remplis.
     *
     * @param invoiceDate La date de la facture.
     * @param partner     Le nom du partenaire.
     * @return true si les champs sont valides, false sinon.
     */
    private boolean validateFields(LocalDate invoiceDate, String partner) {
        return invoiceDate != null && partner != null && !partner.isEmpty();
    }

    /**
     * Génère un numéro de facture unique basé sur le prochain numéro disponible et la date sélectionnée.
     *
     * @param invoiceDate La date de la facture.
     * @return Le numéro de facture généré au format "num-MM-YYYY".
     */
    private String genererNumFacture(LocalDate invoiceDate) {
        int num = DBUtils.obtenirProchainNumFacture();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String formattedDate = invoiceDate.format(formatter);
        return num + "-" + formattedDate;
    }

    /**
     * Configure le DatePicker pour accepter uniquement une sélection de mois et d'année.
     * La sélection renvoie le premier jour du mois pour garantir la compatibilité avec LocalDate.
     */
    private void setupInvoiceDatePicker() {
        invoiceDatePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    YearMonth yearMonth = YearMonth.from(date);
                    return yearMonth.format(formatter);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        YearMonth yearMonth = YearMonth.parse(string, formatter);
                        return yearMonth.atDay(1); // Retourne le premier jour du mois sélectionné
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Format de date invalide: " + string, e);
                    }
                }
                return null;
            }
        });

        // Restreindre la sélection du DatePicker pour qu'il n'affiche que les mois et années
        invoiceDatePicker.setShowWeekNumbers(false);
        invoiceDatePicker.setPromptText("MM/YYYY");
        invoiceDatePicker.getEditor().setDisable(true);
        invoiceDatePicker.getEditor().setOpacity(1);
    }
}
