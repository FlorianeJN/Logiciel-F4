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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Contrôleur pour la mise à jour des informations d'un employé.
 * Ce contrôleur gère l'initialisation des champs de texte avec les données d'un employé spécifique,
 * la validation des informations saisies, ainsi que la gestion des actions des boutons de mise à jour et d'annulation.
 */
public class MiseAJourEmployeController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(MiseAJourEmployeController.class.getName());

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
    private Button btnUpdate;
    @FXML
    private Button btnCancel;

    private Employe employe;

    /**
     * Constructeur paramétré pour initialiser le contrôleur avec un employé spécifique.
     *
     * @param employe L'employé dont les informations doivent être mises à jour.
     */
    public MiseAJourEmployeController(Employe employe) {
        this.employe = employe;
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les actions des boutons et remplit les champs de texte avec les informations de l'employé.
     *
     * @param url L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setOnAction(event -> actionBtnUpdate());
        btnCancel.setOnAction(event -> actionBtnCancel());
        remplirTextFields();
        setFieldsReadOnly();
    }

    /**
     * Remplit les champs de texte avec les informations de l'employé à mettre à jour.
     * Cette méthode est utilisée pour pré-remplir les données de l'employé sélectionné.
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
     * Définit les champs de texte comme étant en lecture seule ou éditables.
     * Empêche l'utilisateur de modifier le nom d'utilisateur avant la mise à jour.
     */
    private void setFieldsReadOnly() {
        usernameField.setEditable(false);
        nomField.setEditable(true);
        prenomField.setEditable(true);
        telephoneField.setEditable(true);
        emailField.setEditable(true);
    }

    /**
     * Récupère les informations saisies par l'utilisateur dans les champs de texte,
     * valide ces informations et les retourne sous forme de Map.
     *
     * @return Une Map contenant les informations valides de l'employé, sinon null si la validation échoue.
     */
    public Map<String, String> retrieveInfos() {
        Map<String, String> employeInfo = new HashMap<>();

        String username = usernameField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String email = emailField.getText().trim();

        boolean valid = true;

        // Vérifie si tous les champs sont remplis
        if (username.isEmpty() || nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs.", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // Valide le numéro de téléphone (doit contenir exactement 10 chiffres)
        if (!telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres.", "ERREUR NUMÉRO DE TÉLÉPHONE");
            valid = false;
        }

        // Valide le format de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Dialogs.showMessageDialog("L'email saisi n'est pas valide.", "ERREUR FORMAT EMAIL");
            valid = false;
        }

        // Si la validation échoue, retourne null
        if (!valid) {
            LOGGER.warning("Validation des informations de l'employé échouée.");
            return null;
        }

        // Si la validation réussit, ajoute les valeurs récupérées à la Map
        employeInfo.put("username", username);
        employeInfo.put("nom", nom);
        employeInfo.put("prenom", prenom);
        employeInfo.put("telephone", telephone);
        employeInfo.put("email", email);
        employeInfo.put("statut", employe.getStatut()); // Conserver le statut actuel

        return employeInfo;
    }

    /**
     * Action déclenchée lors du clic sur le bouton de mise à jour.
     * Récupère les informations saisies, met à jour l'employé dans la base de données,
     * et affiche un message de confirmation si la mise à jour est réussie.
     */
    private void actionBtnUpdate() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            boolean updateReussi = DBUtils.updateEmploye(infos);
            if (updateReussi) {
                String message = "Les informations ont été mises à jour pour " + infos.get("nom") + " " + infos.get("prenom");
                Dialogs.showMessageDialog(message, "MISE À JOUR RÉUSSIE - F4 SANTÉ INC");
                Stage stage = (Stage) btnUpdate.getScene().getWindow();
                stage.close();
            } else {
                Dialogs.showMessageDialog("Erreur lors de la mise à jour de l'employé.", "ERREUR MISE À JOUR");
                LOGGER.severe("Mise à jour de l'employé échouée pour le username: " + infos.get("username"));
            }
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton d'annulation.
     * Ferme la fenêtre sans effectuer de mise à jour.
     */
    private void actionBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
