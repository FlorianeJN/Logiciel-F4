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
 * Contrôleur pour l'interface d'ajout d'un nouvel employé.
 * Ce contrôleur gère l'interaction avec les champs de saisie d'informations de l'employé,
 * valide les données et ajoute un employé dans la base de données si les informations sont valides.
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
            if (DBUtils.addEmployee(employeeInfo)) {
                Dialogs.showMessageDialog("L'employé a été ajouté avec succès!", "AJOUT EMPLOYÉ TERMINÉ");
            }
        }
    }

    /**
     * Récupère et valide les informations saisies par l'utilisateur.
     * Cette méthode vérifie que tous les champs sont remplis et que le numéro de téléphone est valide.
     *
     * @return Une Map contenant les informations valides de l'employé (nom, prénom, téléphone, email).
     *         Si la validation échoue, retourne null.
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

            return employeeInfo;
        }

        // Si la validation échoue, retourne null
        return null;
    }

    /**
     * Réinitialise les champs de saisie après l'ajout d'un employé.
     * Les champs sont vidés afin de permettre la saisie d'un nouvel employé.
     */
    private void reinitialiserChamps() {
        nomField.setText("");
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
