package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la suppression des informations d'un employé.
 * Ce contrôleur est responsable de l'affichage des informations de l'employé en mode lecture seule,
 * de la validation des informations, et de la gestion des actions de suppression et d'annulation.
 */
public class SuppressionEmployeController implements Initializable {

    @FXML
    private TextField usernameField;
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
    private String empUsername;

    /**
     * Constructeur pour initialiser le contrôleur avec un employé spécifique.
     *
     * @param employe L'employé dont les informations doivent être supprimées.
     */
    public SuppressionEmployeController(Employe employe) {
        this.employe = employe;
        if (employe != null) {
            empUsername = employe.getUsername();
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
            usernameField.setText(employe.getUsername());
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
        usernameField.setEditable(false);
        nomField.setEditable(false);
        prenomField.setEditable(false);
        telephoneField.setEditable(false);
        emailField.setEditable(false);
    }

    /**
     * Action déclenchée lors du clic sur le bouton de suppression.
     * Récupère le username, affiche une confirmation de suppression,
     * et supprime l'employé de la base de données si l'utilisateur confirme la suppression.
     */
    private void actionBtnSuppression() {
        String username = usernameField.getText().trim();

        // Vérifier si le username est vide
        if (username.isEmpty()) {
            Dialogs.showMessageDialog("Le nom d'utilisateur est vide. Impossible de supprimer.", "ERREUR SUPPRESSION");
            return;
        }

        String message = "Vous êtes sur le point de supprimer l'employé avec le nom d'utilisateur '" + username + "'. Voulez-vous continuer ?";
        boolean confirm = Dialogs.showConfirmDialog(message, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC");

        if (confirm) {
            boolean deleteReussi = DBUtils.deleteEmploye(username);
            if (deleteReussi) {
                String messageConfirmation = "L'employé avec le nom d'utilisateur '" + username + "' a été supprimé avec succès.";
                Dialogs.showMessageDialog(messageConfirmation, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC");
                Stage stage = (Stage) btnSuppression.getScene().getWindow();
                stage.close();
            } else {
                Dialogs.showMessageDialog("Erreur lors de la suppression de l'employé.", "ERREUR SUPPRESSION");
            }
        }
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
