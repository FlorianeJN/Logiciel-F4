package com.f4.logicielf4.Controllers.Admin.GestionPartenaire;

import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Contrôleur pour l'ajout d'un nouveau partenaire.
 * Ce contrôleur est responsable de la collecte des informations du partenaire,
 * de la validation des informations, de l'ajout du partenaire à la base de données,
 * et de la gestion des actions d'ajout et d'annulation.
 */
public class AjouterPartenaireController implements Initializable {

    @FXML
    private TextField numeroCiviqueField;
    @FXML
    private TextField rueField;
    @FXML
    private TextField villeField;
    @FXML
    private ComboBox<String> provinceComboBox;
    @FXML
    private TextField codePostalField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnAnnuler;
    @FXML
    private TextField nomField;

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Initialise les actions des boutons "Ajouter" et "Annuler".
     *
     * @param url L'URL de la ressource FXML (non utilisé).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnAnnuler.setOnAction(event -> actionBtnAnnuler());
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Ajouter".
     * Récupère les informations du partenaire, valide les données,
     * ajoute le partenaire à la base de données et affiche un message de succès.
     */
    private void actionBtnAjouter() {
        // Gestion de l'action du bouton "Ajouter"
        System.out.println("Bouton Ajouter partenaire cliqué");

        // Récupère les informations du partenaire
        Map<String, String> partnerInfo = retrieveInfos();

        if (partnerInfo != null) {
            if (DBUtils.addPartner(partnerInfo)) {
                Dialogs.showMessageDialog("Le partenaire a été ajouté avec succès!", "AJOUT PARTENAIRE TERMINÉ");
                System.out.println("returned true (addPartner)");
            }
        }
    }

    /**
     * Récupère les informations du partenaire à partir des champs de texte,
     * valide les données, et retourne les informations sous forme de carte (Map).
     *
     * @return Une carte contenant les informations du partenaire si la validation est réussie, sinon null.
     */
    public Map<String, String> retrieveInfos() {
        // Initialisation de la carte pour stocker les informations récupérées
        Map<String, String> partnerInfo = new HashMap<>();

        // Récupère les informations de chaque champ
        String nom = nomField.getText();
        String numeroCiviqueStr = numeroCiviqueField.getText();
        String rue = rueField.getText();
        String ville = villeField.getText();
        String province = provinceComboBox.getValue();
        String codePostal = codePostalField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Initialisation d'un indicateur de validation
        boolean valid = true;

        // Valide le numéro civique
        int numeroCivique = -1;
        try {
            numeroCivique = Integer.parseInt(numeroCiviqueStr);
        } catch (NumberFormatException e) {
            Dialogs.showMessageDialog("Le numéro civique doit être un nombre!", "ERREUR NUMERO CIVIQUE");
            valid = false;
        }

        // Valide le numéro de téléphone
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMERO DE TELEPHONE");
            valid = false;
        }

        // Vérifie si tous les champs sont remplis
        if (nom == null || nom.isEmpty() ||
                numeroCiviqueStr == null || numeroCiviqueStr.isEmpty() ||
                rue == null || rue.isEmpty() ||
                ville == null || ville.isEmpty() ||
                province == null || province.isEmpty() ||
                codePostal == null || codePostal.isEmpty() ||
                telephone == null || telephone.isEmpty() ||
                email == null || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // Si la validation est réussie, ajoute les valeurs récupérées à la carte
        if (valid) {
            partnerInfo.put("nom", nom);
            partnerInfo.put("numeroCivique", String.valueOf(numeroCivique));
            partnerInfo.put("rue", rue);
            partnerInfo.put("ville", ville);
            partnerInfo.put("province", province);
            partnerInfo.put("codePostal", codePostal);
            partnerInfo.put("telephone", telephone);
            partnerInfo.put("email", email);

            // Réinitialise les champs et ferme la fenêtre
            reinitialiserChamps();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

            return partnerInfo; // Retourne la carte contenant les informations
        }

        // Si la validation échoue, retourne null pour indiquer l'échec
        return null;
    }

    /**
     * Réinitialise les champs de texte à leur valeur par défaut.
     */
    private void reinitialiserChamps() {
        nomField.setText("");
        numeroCiviqueField.setText("");
        rueField.setText("");
        villeField.setText("");
        provinceComboBox.getItems().clear();
        codePostalField.setText("");
        telephoneField.setText("");
        emailField.setText("");
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Annuler".
     * Ferme la fenêtre sans effectuer d'ajout.
     */
    private void actionBtnAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}
