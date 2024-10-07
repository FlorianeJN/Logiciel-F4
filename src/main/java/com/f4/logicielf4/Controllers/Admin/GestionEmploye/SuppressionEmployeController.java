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

/**
 * Contrôleur pour la suppression des informations d'un employé.
 * Ce contrôleur est responsable de l'affichage des informations de l'employé en mode lecture seule,
 * de la validation des informations, et de la gestion des actions de suppression et d'annulation.
 */
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

    private Employe employe; // Modèle Employe
    private int employeId;

    /**
     * Constructeur pour initialiser le contrôleur avec un employé spécifique.
     *
     * @param employe L'employé dont les informations doivent être supprimées.
     */
    public SuppressionEmployeController(Employe employe) {
        this.employe = employe;
        if (employe != null) {
            employeId = employe.getId();
        }
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Initialise les actions des boutons, remplit les champs de texte avec les informations de l'employé,
     * et définit les champs comme en lecture seule.
     *
     * @param url            L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSuppression.setOnAction(event -> actionBtnSuppression());
        btnCancel.setOnAction(event -> actionBtnCancel());
        remplirTextFields();
        setFieldsReadOnly();
    }

    /**
     * Remplit les champs de texte avec les informations de l'employé.
     * Cette méthode est utilisée pour afficher les informations de l'employé en mode lecture seule.
     */
    private void remplirTextFields() {
        if (employe != null) {
            nomField.setText(employe.getNom());
            prenomField.setText(employe.getPrenom());
            telephoneField.setText(employe.getTelephone());
            emailField.setText(employe.getEmail());
        }
    }

    /**
     * Définit les champs de texte comme étant en lecture seule.
     * Empêche l'utilisateur de modifier les informations affichées avant la suppression.
     */
    private void setFieldsReadOnly() {
        nomField.setEditable(false);
        prenomField.setEditable(false);
        telephoneField.setEditable(false);
        emailField.setEditable(false);
    }

    /**
     * Action déclenchée lors du clic sur le bouton de suppression.
     * Récupère les informations de l'employé, affiche une confirmation de suppression,
     * et supprime l'employé de la base de données si l'utilisateur confirme la suppression.
     */
    private void actionBtnSuppression() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            String message = "Vous êtes sur le point de supprimer " + infos.get("nom") + " " + infos.get("prenom") + ". Voulez-vous continuer ?";
            if (Dialogs.showConfirmDialog(message, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC")) {
                if (DBUtils.deleteEmploye(infos)) {
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

    /**
     * Récupère les informations de l'employé à partir des champs de texte,
     * et les retourne sous forme de Map après validation.
     *
     * @return Une Map contenant les informations de l'employé si la validation est réussie, sinon null.
     */
    private Map<String, String> retrieveInfos() {
        Map<String, String> employeeInfo = new HashMap<>();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Validation des champs
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

    /**
     * Action déclenchée lors du clic sur le bouton d'annulation.
     * Ferme la fenêtre sans effectuer de suppression.
     */
    private void actionBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
