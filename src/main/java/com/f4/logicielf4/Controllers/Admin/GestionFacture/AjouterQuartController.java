package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.Initializable;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Contrôleur pour l'ajout d'un nouveau quart de travail à une facture.
 * Cette classe hérite de {@link AjouterModifierQuartTemplate} pour gérer l'interface et les fonctionnalités de base.
 * Elle permet d'ajouter un quart à la base de données en validant les informations saisies et en les envoyant via {@link DBUtils}.
 */
public class AjouterQuartController extends AjouterModifierQuartTemplate implements Initializable {

    private String numFacture;
    private PresentationFactureController presentationFactureController;

    /**
     * Constructeur pour initialiser le contrôleur avec le numéro de la facture et le contrôleur parent.
     *
     * @param numFacture Le numéro de la facture à laquelle le quart sera ajouté.
     * @param presentationFactureController Le contrôleur de la présentation de la facture.
     */
    public AjouterQuartController(String numFacture, PresentationFactureController presentationFactureController) {
        this.numFacture = numFacture;
        this.presentationFactureController = presentationFactureController;
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure le bouton d'ajout, remplit les comboBox, rend certains champs en lecture seule, et ajoute des listeners.
     *
     * @param url L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.ajouterQuartBtn.setOnAction(e -> actionBtnAjouterQuart());
        remplirComboBoxPrestation();
        remplirComboBoxEmployes();
        setReadOnlyTextfields();
        addListeners();
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Ajouter Quart".
     * Valide les champs du formulaire, récupère les informations saisies,
     * et ajoute un nouveau quart de travail à la base de données.
     * En cas de succès, met à jour l'interface de la facture et réinitialise le formulaire.
     */
    private void actionBtnAjouterQuart() {
        if (validateFields()) {
            try {
                // Récupération des valeurs du formulaire
                LocalDate dateQuartValue = dateQuart.getValue();
                String prestationValue = prestation.getValue();
                LocalTime debutQuartValue = parseTime(debutQuart.getText());
                LocalTime finQuartValue = parseTime(finQuart.getText());
                LocalTime pauseValue = parseTime(pause.getText());
                String tempsTotalValue = tempsTotal.getText();
                BigDecimal tauxHoraireValue = new BigDecimal(tauxHoraire.getText().replace(',', '.'));
                BigDecimal montantTotalValue = new BigDecimal(montantHT.getText().replace(',', '.'));
                String notesValue = notesTextArea.getText();
                String empName = associerEmpCheckBox.isSelected() ? empComboBox.getValue() : "";
                boolean tempsDouble = checkBoxTempsDouble.isSelected();
                boolean tempsDemi = checkBoxTempsDemi.isSelected();

                // Ajout du quart via DBUtils
                DBUtils.ajouterQuart(numFacture, prestationValue, dateQuartValue, debutQuartValue, finQuartValue, pauseValue,
                        tempsTotalValue, tauxHoraireValue.doubleValue(), montantTotalValue.doubleValue(), notesValue, empName, tempsDouble, tempsDemi);

                Dialogs.showMessageDialog("Succès", "Quart ajouté avec succès.");

                // Mise à jour de l'interface de la facture et réinitialisation du formulaire
                presentationFactureController.updateTable();
                clearFields();
            } catch (Exception e) {
                Dialogs.showMessageDialog("Erreur", "Une erreur s'est produite lors de l'ajout du quart.");
                e.printStackTrace();
            }
        } else {
            Dialogs.showMessageDialog("Erreur de validation", "Veuillez vérifier les champs du formulaire.");
        }
    }
}
