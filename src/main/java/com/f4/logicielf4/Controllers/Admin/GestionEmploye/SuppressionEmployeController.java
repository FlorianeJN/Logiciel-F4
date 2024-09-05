package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import com.f4.logicielf4.Models.Employe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SuppressionEmployeController implements Initializable {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnSuppression;
    @FXML
    private Button btnCancel;

    private Employe employe; // Assuming you have an Employe model
    private int employeId;

    public SuppressionEmployeController(Employe employe) {
        this.employe = employe;
        if(employe != null) {
            employeId = employe.getId();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSuppression.setOnAction(event -> actionBtnSuppression());
        btnCancel.setOnAction(event -> actionBtnCancel());
        remplirTextFields();
        setFieldsReadOnly();
    }

    private void remplirTextFields() {
        if (employe != null) {
            nomField.setText(employe.getNom());
            prenomField.setText(employe.getPrenom());
            telephoneField.setText(employe.getTelephone());
            emailField.setText(employe.getEmail());
        }
    }

    private void setFieldsReadOnly() {
        nomField.setEditable(false);
        prenomField.setEditable(false);
        telephoneField.setEditable(false);
        emailField.setEditable(false);
    }

    private void actionBtnSuppression() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            String message = "Vous êtes sur le point de supprimer " + infos.get("nom") + " " + infos.get("prenom") + ". Voulez-vous continuer?";
            if (Dialogs.showConfirmDialog(message, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC")) {
                if (DBUtils.deleteEmploye(infos)) { // Assume this method handles employee deletion
                    String messageConfirmation = infos.get("nom") + " " + infos.get("prenom") + " a été supprimé avec succès.";
                    Dialogs.showMessageDialog(messageConfirmation, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC");
                    Stage stage = (Stage) btnSuppression.getScene().getWindow();
                    stage.close();
                } else {
                    Dialogs.showMessageDialog("Erreur lors de la suppression de l'employé.", "ERREUR SUPPRESSION");
                }
            }
        }
    }

    private Map<String, String> retrieveInfos() {
        Map<String, String> employeeInfo = new HashMap<>();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            return null;
        }

        employeeInfo.put("nom", nom);
        employeeInfo.put("prenom", prenom);
        employeeInfo.put("telephone", telephone);
        employeeInfo.put("email", email);
        employeeInfo.put("id", String.valueOf(employeId));

        return employeeInfo;
    }

    private void actionBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public int getEmployeId() {
        return employeId;
    }
}
