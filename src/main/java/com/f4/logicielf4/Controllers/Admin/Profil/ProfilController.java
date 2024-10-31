package com.f4.logicielf4.Controllers.Admin.Profil;

import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la page de profil de l'administrateur.
 * Permet d'afficher et de modifier les informations personnelles de l'administrateur.
 */
public class ProfilController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField; // Ajout du TextField pour afficher le mot de passe
    @FXML
    private CheckBox cbShowPassword;
    @FXML
    private Button btnSauvegarder;

    private String adminUsername = "admin"; // À adapter selon la gestion de session

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Récupère les informations de l'administrateur et les affiche dans les champs correspondants.
     *
     * @param url            L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger les informations de l'administrateur
        loadAdminInfo();

        // Synchroniser les champs de mot de passe
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        // Configurer les actions des boutons
        btnSauvegarder.setOnAction(event -> actionBtnSauvegarder());

        // Configurer l'action de la CheckBox pour afficher/masquer le mot de passe
        cbShowPassword.selectedProperty().addListener((observable, oldValue, newValue) -> togglePasswordVisibility(newValue));
    }

    /**
     * Récupère les informations de l'administrateur depuis la base de données et les affiche dans les champs de texte.
     */
    private void loadAdminInfo() {
        Employe admin = DBUtils.getEmployeByUsername(adminUsername);
        if (admin != null) {
            usernameField.setText(admin.getUsername());
            nomField.setText(admin.getNom());
            prenomField.setText(admin.getPrenom());
            emailField.setText(admin.getEmail());
            phoneField.setText(admin.getTelephone());
            passwordField.setText(admin.getPassword()); // Affiche le mot de passe initialement caché
            // Le TextField est déjà lié au PasswordField, pas besoin de le définir séparément
        } else {
            Dialogs.showMessageDialog("Erreur : Administrateur non trouvé.", "ERREUR");
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Sauvegarder les modifications".
     * Valide les informations saisies et met à jour les données de l'administrateur dans la base de données.
     */
    private void actionBtnSauvegarder() {
        // Récupérer et valider les informations saisies
        Map<String, String> employeInfo = retrieveInfos();
        if (employeInfo != null) {
            boolean updateReussi = DBUtils.updateEmploye(employeInfo);
            if (updateReussi) {
                Dialogs.showMessageDialog("Les informations ont été mises à jour avec succès.", "MISE À JOUR RÉUSSIE");
                Stage stage = (Stage) btnSauvegarder.getScene().getWindow();
                stage.close();
            } else {
                Dialogs.showMessageDialog("Erreur lors de la mise à jour des informations.", "ERREUR MISE À JOUR");
            }
        }
    }

    /**
     * Récupère les informations saisies dans les champs de texte, les valide et les retourne sous forme de Map.
     *
     * @return Une Map contenant les informations de l'administrateur si la validation est réussie, sinon null.
     */
    private Map<String, String> retrieveInfos() {
        Map<String, String> employeInfo = new HashMap<>();

        String username = usernameField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

        boolean valid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Vérification des champs obligatoires
        if (username.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            errorMessage.append("Veuillez remplir tous les champs obligatoires.\n");
            valid = false;
        }

        // Validation du format de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorMessage.append("Le format de l'email est invalide.\n");
            valid = false;
        }

        // Validation du numéro de téléphone (exemple : 10 chiffres)
        if (!telephone.matches("\\d{10}")) {
            errorMessage.append("Le numéro de téléphone doit contenir exactement 10 chiffres.\n");
            valid = false;
        }

        // Si un nouveau mot de passe est saisi, valider sa longueur
        if (!password.isEmpty() && password.length() < 6) {
            errorMessage.append("Le mot de passe doit contenir au moins 6 caractères.\n");
            valid = false;
        }

        if (!valid) {
            Dialogs.showMessageDialog(errorMessage.toString(), "ERREUR VALIDATION");
            return null;
        }

        // Remplir la Map avec les informations
        employeInfo.put("username", username);
        employeInfo.put("nom", nom);
        employeInfo.put("prenom", prenom);
        employeInfo.put("email", email);
        employeInfo.put("telephone", telephone);
        employeInfo.put("statut", "Actif"); // Exemple : Vous pouvez adapter selon votre logique

        // Gérer le mot de passe
        if (!password.isEmpty()) {
            employeInfo.put("password", password); // Utiliser le mot de passe tel quel, sans hachage
        }

        return employeInfo;
    }

    /**
     * Bascule l'affichage du mot de passe entre le PasswordField et le TextField.
     *
     * @param show true pour afficher le mot de passe, false pour le masquer.
     */
    private void togglePasswordVisibility(boolean show) {
        passwordField.setVisible(!show);
        passwordVisibleField.setVisible(show);
    }
}
