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

/**
 * Contrôleur pour l'écran d'ajout d'un nouvel employé.
 * Permet de saisir les informations d'un employé et de les ajouter à la base de données.
 */
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

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les actions des boutons.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnAnnuler.setOnAction(event -> actionBtnAnnuler());
    }

    /**
     * Action effectuée lors de l'appui sur le bouton "Ajouter".
     * Récupère les informations de l'employé, les valide, et les ajoute à la base de données si elles sont valides.
     */
    private void actionBtnAjouter() {
        Map<String, String> employeeInfo = retrieveInfos();

        // Si les informations sont valides, ajout à la base de données
        if (employeeInfo != null) {
            if (DBUtils.addEmployee(employeeInfo)) { // Supposons qu'il existe une méthode addEmployee dans DBUtils
                Dialogs.showMessageDialog("L'employé a été ajouté avec succès!", "AJOUT EMPLOYÉ TERMINÉ");
            }
        }
    }

    /**
     * Récupère les informations des champs de saisie et les valide.
     * Si les informations sont valides, elles sont renvoyées sous forme de Map.
     *
     * @return Une Map contenant les informations de l'employé si la validation est réussie, sinon null.
     */
    public Map<String, String> retrieveInfos() {
        Map<String, String> employeeInfo = new HashMap<>();

        // Récupération des informations des champs
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Initialisation d'un indicateur de validation
        boolean valid = true;

        // Validation du numéro de téléphone (doit contenir exactement 10 chiffres)
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMÉRO DE TÉLÉPHONE");
            valid = false;
        }

        // Vérification que tous les champs sont remplis
        if (nom == null || nom.isEmpty() ||
                prenom == null || prenom.isEmpty() ||
                telephone == null || telephone.isEmpty() ||
                email == null || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // Si la validation est réussie, les informations sont ajoutées à la Map
        if (valid) {
            employeeInfo.put("nom", nom);
            employeeInfo.put("prenom", prenom);
            employeeInfo.put("telephone", telephone);
            employeeInfo.put("email", email);

            // Réinitialiser les champs et fermer la fenêtre
            reinitialiserChamps();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

            return employeeInfo; // Retourne la Map contenant les informations
        }

        // Si la validation échoue, retourne null
        return null;
    }

    /**
     * Réinitialise les champs de saisie à leur état vide.
     */
    private void reinitialiserChamps() {
        nomField.setText("");
        prenomField.setText("");
        telephoneField.setText("");
        emailField.setText("");
    }

    /**
     * Action effectuée lors de l'appui sur le bouton "Annuler".
     * Ferme la fenêtre sans effectuer d'action.
     */
    private void actionBtnAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}
