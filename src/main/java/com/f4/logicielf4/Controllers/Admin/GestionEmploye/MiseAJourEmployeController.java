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

/**
 * Contrôleur pour la mise à jour des informations d'un employé.
 * Ce contrôleur est responsable de l'initialisation des champs de texte avec les données de l'employé,
 * de la validation des informations saisies, et de la gestion des actions des boutons de mise à jour et d'annulation.
 */
public class MiseAJourEmployeController implements Initializable {

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
    private int employeId;

    /**
     * Constructeur pour initialiser le contrôleur avec un employé spécifique.
     *
     * @param employe L'employé dont les informations doivent être mises à jour.
     */
    public MiseAJourEmployeController(Employe employe) {
        this.employe = employe;
        if(employe != null) {
            employeId = employe.getId();
        }
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Initialise les actions des boutons et remplit les champs de texte avec les informations de l'employé.
     *
     * @param url L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setOnAction(event -> actionBtnUpdate());
        btnCancel.setOnAction(event -> actionBtnCancel());
        remplirTextFields();
    }

    /**
     * Remplit les champs de texte avec les informations de l'employé.
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
     * Récupère les informations saisies par l'utilisateur dans les champs de texte,
     * valide ces informations et les retourne sous forme de carte (Map).
     *
     * @return Une carte contenant les informations de l'employé si la validation est réussie, sinon null.
     */
    public Map<String, String> retrieveInfos() {
        Map<String, String> employeInfo = new HashMap<>();

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        boolean valid = true;

        // Valide le numéro de téléphone
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMERO DE TELEPHONE");
            valid = false;
        }

        // Vérifie si tous les champs sont remplis
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // Si la validation échoue, retourne null
        if (!valid) {
            return null;
        }

        // Si la validation réussit, ajoute les valeurs récupérées à la carte
        employeInfo.put("nom", nom);
        employeInfo.put("prenom", prenom);
        employeInfo.put("telephone", telephone);
        employeInfo.put("email", email);
        employeInfo.put("id", String.valueOf(employeId)); // Ajoute l'ID de l'employé à la carte

        Stage stage = (Stage) btnUpdate.getScene().getWindow();
        stage.close();

        return employeInfo;
    }

    /**
     * Action déclenchée lors du clic sur le bouton de mise à jour.
     * Récupère les informations de l'employé, met à jour l'employé dans la base de données
     * et affiche un message de succès si la mise à jour est réussie.
     */
    private void actionBtnUpdate() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            if (DBUtils.updateEmploye(infos)) {
                String message = "Les informations ont été mises à jour pour " + infos.get("nom");
                Dialogs.showMessageDialog(message, "MISE À JOUR RÉUSSIE - F4 SANTÉ INC");
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
