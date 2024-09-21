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

    public ModifierQuartController(Quart quart){
        this.quart = quart;
        this.numFacture = quart.getNumFacture();
    }

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

    private void actionBtnMaj() {
        if(validateFields()){
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
            try{
                DBUtils.updateQuart(quart.getId(),prestationValue,dateQuartValue,debutQuartValue,finQuartValue,pauseValue,tempsTotalValue,tauxHoraireValue,montantTotalValue,notesValue,empName,tempsDouble,tempsDemi);
                Dialogs.showMessageDialog("Quart modifié avec succès","SUCCÈS MODIFICATION QUART");
                Stage stage = (Stage) ajouterQuartBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                Dialogs.showMessageDialog(e.getMessage(),"Erreur");
            }
        }
        else
            Dialogs.showMessageDialog("Veuillez bien remplir tous les champs","ERREUR REMPLISSAGE CHAMPS");
    }

    private void remplirFormulaire(){
        dateQuart.setValue(quart.getDateQuart());
        debutQuart.setText(quart.getDebutQuart().toString());
        finQuart.setText(quart.getFinQuart().toString());
        pause.setText(quart.getPause().toString());
        notesTextArea.setText(quart.getNotes());
        genererPrestation();
        if(quart.isTempsDemi())
            checkBoxTempsDemi.setSelected(true);
        if (quart.isTempsDouble())
            checkBoxTempsDouble.setSelected(true);
    }

    private void genererPrestation(){
        switch (quart.getStringPrestation()){
            case "INF":
                prestation.setValue("INF");
                break;
            case "INF AUX":
                prestation.setValue("INF AUX");
                break;
            case "INF CL":
                prestation.setValue("INF CL");
                break;
            case "PAB":
                prestation.setValue("PAB");
                break;
        }
    }
}
