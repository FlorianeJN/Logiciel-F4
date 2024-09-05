package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AjouterEmployeController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnAnnuler.setOnAction(event -> actionBtnAnnuler());
    }

    private void actionBtnAjouter() {
        Map<String, String> employeeInfo = retrieveInfos();

        if (employeeInfo != null) {
            if (DBUtils.addEmployee(employeeInfo)) { // Assuming you have an addEmployee method in DBUtils
                Dialogs.showMessageDialog("L'employé a été ajouté avec succès!", "AJOUT EMPLOYÉ TERMINÉ");
            }
        }
    }

    public Map<String, String> retrieveInfos() {
        Map<String, String> employeeInfo = new HashMap<>();

        // Retrieve information from each field
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Initialize a flag for validation
        boolean valid = true;

        // Validate telephone number
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMERO DE TELEPHONE");
            valid = false;
        }

        // Check if all fields are filled
        if (nom == null || nom.isEmpty() ||
                prenom == null || prenom.isEmpty() ||
                telephone == null || telephone.isEmpty() ||
                email == null || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // If validation passes, add the retrieved values to the map
        if (valid) {
            employeeInfo.put("nom", nom);
            employeeInfo.put("prenom", prenom);
            employeeInfo.put("telephone", telephone);
            employeeInfo.put("email", email);

            // Reset fields and close window
            reinitialiserChamps();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

            return employeeInfo; // Return the map with all the information
        }

        // If validation fails, return null to indicate failure
        return null;
    }

    private void reinitialiserChamps() {
        nomField.setText("");
        prenomField.setText("");
        telephoneField.setText("");
        emailField.setText("");
    }

    private void actionBtnAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}
