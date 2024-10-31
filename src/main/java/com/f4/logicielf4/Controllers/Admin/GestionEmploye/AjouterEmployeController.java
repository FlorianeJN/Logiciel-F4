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
import java.util.logging.Logger;

/**
 * Contrôleur pour l'interface d'ajout d'un nouvel employé.
 * Ce contrôleur gère l'interaction avec les champs de saisie d'informations de l'employé,
 * valide les données et ajoute un employé dans la base de données si les informations sont valides.
 */
public class AjouterEmployeController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(AjouterEmployeController.class.getName());

    @FXML
    private TextField nomField;

    @FXML
    private TextField usernameField;

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
     * Initialise le contrôleur et configure les actions des boutons lors de l'initialisation.
     *
     * @param url L'URL utilisée pour localiser le fichier FXML.
     * @param resourceBundle Les ressources à utiliser pour l'internationalisation de l'interface.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnAnnuler.setOnAction(event -> actionBtnAnnuler());
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Ajouter".
     * Récupère les informations des champs, les valide et, si elles sont correctes,
     * ajoute un nouvel employé dans la base de données.
     */
    private void actionBtnAjouter() {
        Map<String, String> employeeInfo = retrieveInfos();

        // Si les informations sont valides, ajout à la base de données
        if (employeeInfo != null) {
            LOGGER.info("Attempting to add employee with username: " + employeeInfo.get("username"));
            boolean ajoutReussi = DBUtils.addEmployee(employeeInfo);
            if (ajoutReussi) {
                LOGGER.info("Employee added successfully.");
                Dialogs.showMessageDialog("L'employé a été ajouté avec succès!\nLe mot de passe par défaut est 'Password@123'.", "AJOUT EMPLOYÉ TERMINÉ");
                // Réinitialiser les champs après un ajout réussi
                reinitialiserChamps();
                // Fermer la fenêtre après un ajout réussi (si souhaité)
                Stage stage = (Stage) btnAjouter.getScene().getWindow();
                stage.close();
            } else {
                LOGGER.warning("Failed to add employee.");
                Dialogs.showMessageDialog("Erreur lors de l'ajout de l'employé.", "ERREUR AJOUT");
            }
        }
    }

    /**
     * Récupère et valide les informations saisies par l'utilisateur.
     * Cette méthode vérifie que tous les champs sont remplis et que le numéro de téléphone est valide.
     *
     * @return Une Map contenant les informations valides de l'employé (username, nom, prenom, telephone, email, statut).
     *         Si la validation échoue, retourne null.
     */
    public Map<String, String> retrieveInfos() {
        Map<String, String> employeeInfo = new HashMap<>();

        // Récupération des informations des champs
        String username = usernameField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();

        // Initialisation d'un indicateur de validation
        boolean valid = true;

        // Validation du nom d'utilisateur (doit être non vide)
        if (username.isEmpty()) {
            Dialogs.showMessageDialog("Le nom d'utilisateur ne peut pas être vide.", "ERREUR NOM D'UTILISATEUR");
            valid = false;
        }

        // Validation du numéro de téléphone (doit contenir exactement 10 chiffres)
        if (telephone.isEmpty() || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres.", "ERREUR NUMÉRO DE TÉLÉPHONE");
            valid = false;
        }

        // Vérification que tous les champs sont remplis
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs.", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // Vérification de l'unicité du nom d'utilisateur (facultatif mais recommandé)
        if (DBUtils.usernameExists(username)) {
            Dialogs.showMessageDialog("Le nom d'utilisateur existe déjà. Veuillez en choisir un autre.", "ERREUR NOM D'UTILISATEUR");
            valid = false;
        }

        // Si la validation échoue, retourne null
        if (!valid) {
            LOGGER.warning("Validation failed for employee information.");
            return null;
        }

        // Si la validation réussit, ajoute les valeurs récupérées à la Map
        employeeInfo.put("username", username);
        employeeInfo.put("nom", nom);
        employeeInfo.put("prenom", prenom);
        employeeInfo.put("telephone", telephone);
        employeeInfo.put("email", email);
        employeeInfo.put("statut", "Actif"); // Défaut au statut "Actif"

        LOGGER.info("Validated employee information: " + employeeInfo);

        return employeeInfo;
    }

    /**
     * Réinitialise les champs de saisie après l'ajout d'un employé.
     * Les champs sont vidés afin de permettre la saisie d'un nouvel employé.
     */
    private void reinitialiserChamps() {
        nomField.setText("");
        usernameField.setText("");
        prenomField.setText("");
        telephoneField.setText("");
        emailField.setText("");
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Annuler".
     * Ferme la fenêtre d'ajout sans enregistrer d'informations.
     */
    private void actionBtnAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}
