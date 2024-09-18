package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.Inf;
import com.f4.logicielf4.Controllers.Strategie.InfAux;
import com.f4.logicielf4.Controllers.Strategie.InfClinic;
import com.f4.logicielf4.Controllers.Strategie.PAB;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AjouterQuartController extends AjouterModifierQuartTemplate implements Initializable {

    private String numFacture;

    public AjouterQuartController(String numFacture) {
        this.numFacture = numFacture;
    }

    /*public AjouterModifierQuartController(Quart quart) {
        this.quart = quart;
        this.numFacture = quart.getNumFacture();
    }
*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.ajouterQuartBtn.setOnAction(e -> actionBtnAjouterQuart());
        remplirComboBoxPrestation();
        remplirComboBoxEmployes();
        setReadOnlyTextfields();
        addListeners();
    }

    private void actionBtnAjouterQuart() {
        if (validateFields()) {
            try {
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
                DBUtils.ajouterQuart(numFacture, prestationValue, dateQuartValue, debutQuartValue, finQuartValue, pauseValue, tempsTotalValue, tauxHoraireValue.doubleValue(), montantTotalValue.doubleValue(), notesValue, empName,tempsDouble,tempsDemi);

                Dialogs.showMessageDialog("Succès", "Quart ajouté avec succès.");
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
