package com.f4.logicielf4.Controllers;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe contrôleur pour la gestion de la connexion utilisateur.
 * <p>
 * Ce contrôleur gère l'affichage et la logique de connexion, y compris la gestion du champ de mot de passe
 * visible/masqué et l'authentification des utilisateurs.
 * </p>
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordFieldVisible;

    @FXML
    private CheckBox showPasswordCheckBox;

    /**
     * Initialise la classe contrôleur. Cette méthode est appelée après que le fichier FXML a été chargé.
     * <p>
     * Configure les gestionnaires d'événements pour le bouton de connexion et la case à cocher de visibilité du mot de passe.
     * </p>
     *
     * @param url l'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null} si l'emplacement n'est pas connu
     * @param resourceBundle les ressources utilisées pour localiser l'objet racine, ou {@code null} si l'objet racine n'a pas été localisé
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordFieldVisible.textProperty().bind(passwordField.textProperty());
        showPasswordCheckBox.setOnAction(event -> actionCheckBoxShowPassword());
        loginButton.setOnAction(event -> actionBtnLogin());
    }

    /**
     * Gère l'action de la case à cocher pour afficher/masquer le mot de passe.
     * <p>
     * Affiche le mot de passe en clair dans le champ de texte si la case est cochée,
     * sinon masque le mot de passe dans le champ de mot de passe.
     * </p>
     */
    private void actionCheckBoxShowPassword() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setVisible(false);
            passwordFieldVisible.setVisible(true);
        } else {
            passwordField.setVisible(true);
            passwordFieldVisible.setVisible(false);
        }
    }

    /**
     * Gère l'action du bouton de connexion. Vérifie les informations d'identification de l'utilisateur
     * et redirige vers la fenêtre d'administration si les informations sont valides.
     * <p>
     * Si les informations sont incorrectes, les champs de saisie sont réinitialisés.
     * </p>
     */
    private void actionBtnLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (DBUtils.loginUser(username, password)) {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            Model.getInstance().getViewFactory().showAdminWindow();
        } else {
            clearFields();
        }
        username = null;
        password = null;
    }

    /**
     * Réinitialise les champs de saisie de nom d'utilisateur et de mot de passe.
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        passwordFieldVisible.clear();
    }
}
