package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la modification d'un quart de travail.
 * Cette classe permet de charger les informations d'un quart existant, les afficher dans le formulaire,
 * les modifier, et les enregistrer dans la base de données.
 * Elle hérite de {@link AjouterModifierQuartTemplate} pour réutiliser les fonctionnalités liées au formulaire de quart.
 */
public class ModifierQuartController extends AjouterModifierQuartTemplate implements Initializable {

    @FXML
    private Label titreLabel;

    @FXML
    private DatePicker dateQuart;

    @FXML
    private ComboBox<String> prestation;

    @FXML
    private TextField debutQuart;

    @FXML
    private TextField finQuart;

    @FXML
    private TextField pause;

    @FXML
    private TextField tempsTotal;

    @FXML
    private TextField tauxHoraire;

    @FXML
    private TextField montantHT;

    @FXML
    private ComboBox<String> empComboBox;

    @FXML
    private CheckBox associerEmpCheckBox;

    @FXML
    private Button ajouterQuartBtn;

    @FXML
    private CheckBox checkboxPause;

    @FXML
    private CheckBox checkBoxTempsDouble;

    @FXML
    private CheckBox checkBoxTempsDemi;

    @FXML
    private TextArea notesTextArea;

    private String numFacture;
    private Quart quart;
    private PresentationFactureController presentationFactureController;

    /**
     * Constructeur pour initialiser le contrôleur avec un quart spécifique à modifier.
     *
     * @param quart Le quart à modifier.
     */
    public ModifierQuartController(Quart quart) {
        this.quart = quart;
        this.numFacture = quart.getNumFacture();
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Remplit les champs du formulaire avec les données du quart à modifier,
     * configure les composants de l'interface utilisateur, et ajoute des listeners.
     *
     * @param url            L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remplirComboBoxPrestation();
        remplirComboBoxEmployes();
        setReadOnlyTextfields();
        addListeners();
        remplirFormulaire();
        ajouterQuartBtn.setText("Modifier le quart");
        titreLabel.setText("Modifier Quart");
        ajouterQuartBtn.setOnAction(e -> actionBtnMaj());
    }

    /**
     * Action déclenchée lors du clic sur le bouton pour modifier le quart.
     * Valide les champs du formulaire, met à jour le quart dans la base de données, et affiche un message de confirmation.
     * Ferme la fenêtre après succès de l'opération.
     */
    private void actionBtnMaj() {
        if (validateFields()) {
            LocalDate dateQuartValue = dateQuart.getValue();
            String prestationValue = prestation.getValue();
            LocalTime debutQuartValue = parseTime(debutQuart.getText());
            LocalTime finQuartValue = parseTime(finQuart.getText());
            LocalTime pauseValue = parseTime(pause.getText());
            String tempsTotalValue = tempsTotal.getText();
            double tauxHoraireValue = Double.parseDouble(tauxHoraire.getText().replace(',', '.'));
            Double montantTotalValue = Double.parseDouble(montantHT.getText().replace(',', '.'));
            String notesValue = notesTextArea.getText();
            String empName = associerEmpCheckBox.isSelected() ? empComboBox.getValue() : "";
            boolean tempsDouble = checkBoxTempsDouble.isSelected();
            boolean tempsDemi = checkBoxTempsDemi.isSelected();

            try {
                DBUtils.updateQuart(quart.getId(), prestationValue, dateQuartValue, debutQuartValue, finQuartValue, pauseValue,
                        tempsTotalValue, tauxHoraireValue, montantTotalValue, notesValue, empName, tempsDouble, tempsDemi);
                Dialogs.showMessageDialog("Quart modifié avec succès", "SUCCÈS MODIFICATION QUART");
                Stage stage = (Stage) ajouterQuartBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                Dialogs.showMessageDialog(e.getMessage(), "Erreur");
            }
        } else {
            Dialogs.showMessageDialog("Veuillez bien remplir tous les champs", "ERREUR REMPLISSAGE CHAMPS");
        }
    }

    /**
     * Remplit le formulaire avec les informations du quart existant.
     * Cette méthode est utilisée pour pré-remplir les données du quart dans le formulaire.
     */
    private void remplirFormulaire() {
        dateQuart.setValue(quart.getDateQuart());
        debutQuart.setText(quart.getDebutQuart().toString());
        finQuart.setText(quart.getFinQuart().toString());

        if (quart.getPause().toString().equals("00:00"))
            checkboxPause.setSelected(false);
        else
            checkboxPause.setSelected(true);

        pause.setText(quart.getPause().toString());
        notesTextArea.setText(quart.getNotes());
        genererPrestation();

        if (quart.isTempsDemi())
            checkBoxTempsDemi.setSelected(true);
        if (quart.isTempsDouble())
            checkBoxTempsDouble.setSelected(true);

        if(quart.getNomEmploye() != null){
            associerEmpCheckBox.setSelected(true);
            empComboBox.setValue(quart.getNomEmploye());
        }

    }

    /**
     * Sélectionne la prestation correspondante au quart dans la comboBox des prestations.
     */
    private void genererPrestation() {
        switch (quart.getStringPrestation()) {
            case "SOINS INFIRMIERS":
                prestation.setValue("SOINS INFIRMIERS");
                break;
            case "INF AUXILIAIRE":
                prestation.setValue("INF AUXILIAIRE");
                break;
            case "INF CLINICIEN(NE)":
                prestation.setValue("INF CLINICIEN(NE)");
                break;
            case "PAB":
                prestation.setValue("PAB");
                break;
        }
    }
}
