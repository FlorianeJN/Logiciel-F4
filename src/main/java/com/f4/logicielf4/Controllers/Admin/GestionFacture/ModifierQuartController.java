package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
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
        Dialogs.showMessageDialog("BTN MAJ","BTN MAJ");
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
