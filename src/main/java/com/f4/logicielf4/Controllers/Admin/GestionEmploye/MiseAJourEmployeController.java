package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MiseAJourEmployeController implements Initializable {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;

    private Employe employe;
    private int employeId;

    public MiseAJourEmployeController(Employe employe) {
        this.employe = employe;
        if(employe != null) {
            employeId = employe.getId();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setOnAction(event -> actionBtnUpdate());
        btnCancel.setOnAction(event -> actionBtnCancel());
        remplirTextFields();
    }

    private void remplirTextFields() {
        if (employe != null) {
            nomField.setText(employe.getNom());
            prenomField.setText(employe.getPrenom());
            telephoneField.setText(employe.getTelephone());
            emailField.setText(employe.getEmail());
        }
    }

    public Map<String, String> retrieveInfos() {
        Map<String, String> employeInfo = new HashMap<>();

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();
        // String statut = (String) statutComboBox.getValue(); // Optional

        boolean valid = true;

        // Validate telephone number
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMERO DE TELEPHONE");
            valid = false;
        }

        // Check if all fields are filled
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // If validation fails, return null
        if (!valid) {
            return null;
        }

        // If validation passes, add the retrieved values to the map
        employeInfo.put("nom", nom);
        employeInfo.put("prenom", prenom);
        employeInfo.put("telephone", telephone);
        employeInfo.put("email", email);
        employeInfo.put("id", String.valueOf(employeId)); // Add the employee ID to the map

        Stage stage = (Stage) btnUpdate.getScene().getWindow();
        stage.close();

        return employeInfo;
    }


    private void actionBtnUpdate() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            if (DBUtils.updateEmploye(infos)) {
                String message = "Les informations ont été mises à jour pour " + infos.get("nom");
                Dialogs.showMessageDialog(message, "MISE À JOUR RÉUSSIE - F4 SANTÉ INC");
            }
        }
    }

    private void actionBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
