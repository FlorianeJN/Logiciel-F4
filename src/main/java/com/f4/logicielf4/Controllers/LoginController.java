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
 * Ce contrôleur gère l'authentification de l'utilisateur, ainsi que l'affichage ou le masquage du mot de passe.
 * Il permet à l'utilisateur de se connecter en fournissant un nom d'utilisateur et un mot de passe,
 * et propose une fonctionnalité de visibilité du mot de passe.
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
     * Méthode d'initialisation appelée après le chargement du fichier FXML.
     * Configure les actions pour le bouton de connexion et la case à cocher pour la visibilité du mot de passe.
     *
     * @param url L'URL de la ressource FXML (non utilisée).
     * @param resourceBundle Le ResourceBundle utilisé pour localiser les ressources (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Masquer le champ de mot de passe visible au départ
        passwordFieldVisible.setVisible(false);

        // Gérer l'affichage/masquage du mot de passe
        showPasswordCheckBox.setOnAction(event -> actionCheckBoxShowPassword());

        // Synchroniser les contenus des champs de mot de passe
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            if (!showPasswordCheckBox.isSelected()) {
                passwordFieldVisible.setText(newText);
            }
        });

        passwordFieldVisible.textProperty().addListener((obs, oldText, newText) -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setText(newText);
            }
        });

        // Action du bouton de connexion
        loginButton.setOnAction(event -> actionBtnLogin());
    }

    /**
     * Gère l'affichage ou le masquage du mot de passe en fonction de l'état de la case à cocher.
     * <p>
     * Si la case est cochée, le mot de passe est affiché en clair, sinon il est masqué.
     * </p>
     */
    private void actionCheckBoxShowPassword() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setVisible(false);
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setText(passwordField.getText());
        } else {
            passwordField.setVisible(true);
            passwordFieldVisible.setVisible(false);
            passwordField.setText(passwordFieldVisible.getText());
        }
    }

    /**
     * Gère l'action du bouton de connexion. Vérifie les informations d'identification de l'utilisateur.
     * <p>
     * Si les informations sont correctes, l'utilisateur est redirigé vers la fenêtre d'administration.
     * Sinon, les champs de saisie sont réinitialisés.
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
    }

    /**
     * Réinitialise les champs de saisie pour le nom d'utilisateur et le mot de passe.
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        passwordFieldVisible.clear();
    }
}
