package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure le bouton "Continuer" et initialise les composants de sélection de la date de facture et des partenaires.
     */
    @FXML
    public void initialize() {
        btnContinuer.setOnAction(event -> actionBtnContinuer());
        setupInvoiceDatePicker();
        setupParterComboxBox();
    }

    /**
     * Remplit la comboBox des partenaires avec les noms des partenaires actifs extraits de la base de données.
     */
    private void setupParterComboxBox() {
        List<Partenaire> listePartenaires = DBUtils.fetchAllActivePartners();
        List<String> listeNomsPartenaires = new ArrayList<>();
        for (Partenaire partenaire : listePartenaires) {
            listeNomsPartenaires.add(partenaire.getNom());
        }
        partnerComboBox.getItems().clear();
        partnerComboBox.getItems().setAll(listeNomsPartenaires);
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Continuer".
     * Valide les informations saisies, génère un numéro de facture unique,
     * crée la facture dans la base de données, et propose à l'utilisateur d'ajouter des quarts à la facture.
     */
    private void actionBtnContinuer() {
        System.out.println("btnContinuer appuyé");
        LocalDate invoiceDate = invoiceDatePicker.getValue();
        String partner = partnerComboBox.getValue();

        if (partner != null && !partner.isEmpty()) {
            String numFacture = genererNumFacture();
            if (DBUtils.createNewInvoice(numFacture, partner, invoiceDate, "À compléter")) {
                Stage stage = (Stage) btnContinuer.getScene().getWindow();
                stage.close();
                if (Dialogs.showConfirmDialog("La facture " + numFacture + " a été créée! Voulez-vous y ajouter des quarts ?", "MESSAGE CONFIRMATION + AJOUTS DE QUARTS")) {
                    Model.getInstance().getViewFactory().showPresentationFactureWindow(stage, numFacture, partner);
                }
            } else {
                Dialogs.showConfirmDialog("Facture non créée", "ERREUR CRÉATION FACTURE");
            }
        } else {
            Dialogs.showMessageDialog("Assurez-vous de bien remplir les deux champs", "ERREUR REMPLISSAGE CHAMPS");
        }
    }

    /**
     * Génère un numéro de facture unique basé sur le prochain numéro disponible et la date sélectionnée.
     *
     * @return Le numéro de facture généré au format "num-MM-YYYY".
     */
    private String genererNumFacture() {
        int num = DBUtils.obtenirProchainNumFacture();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String formattedDate = invoiceDatePicker.getValue().format(formatter);
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
                    YearMonth yearMonth = YearMonth.parse(string, formatter);
                    return yearMonth.atDay(1); // Retourne le premier jour du mois sélectionné
                }
                return null;
            }
        });
        invoiceDatePicker.setPromptText("MM/YYYY");
    }
}
