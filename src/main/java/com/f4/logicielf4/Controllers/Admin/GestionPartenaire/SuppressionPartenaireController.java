package com.f4.logicielf4.Controllers.Admin.GestionPartenaire;

import com.f4.logicielf4.Models.Adresse;
import com.f4.logicielf4.Models.Partenaire;
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
 * Contrôleur pour la suppression d'un partenaire.
 * Ce contrôleur affiche les informations du partenaire à supprimer,
 * et permet de confirmer ou annuler la suppression.
 */
public class SuppressionPartenaireController implements Initializable {
    @FXML
    private ComboBox<String> provinceComboBox;
    @FXML
    private TextField nomField;
    @FXML
    private TextField numeroCiviqueField;
    @FXML
    private TextField rueField;
    @FXML
    private TextField villeField;
    @FXML
    private TextField codePostalField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnSuppression;
    @FXML
    private Button btnCancel;

    private Partenaire partenaire;

    /**
     * Constructeur pour le contrôleur de suppression de partenaire.
     *
     * @param partenaire Le partenaire à supprimer.
     */
    public SuppressionPartenaireController(Partenaire partenaire) {
        this.partenaire = partenaire;
    }

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Initialise les actions des boutons "Suppression" et "Cancel",
     * remplit les champs de texte avec les informations du partenaire,
     * et rend les champs en lecture seule.
     *
     * @param url L'URL de la ressource FXML (non utilisé).
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
     * Récupère les informations du partenaire à partir des champs de texte,
     * valide les données, et retourne les informations sous forme de carte (Map).
     *
     * @return Une carte contenant les informations du partenaire si la validation est réussie, sinon null.
     */
    public Map<String, String> retrieveInfos() {
        // Initialize a map to store the retrieved information
        Map<String, String> partnerInfo = new HashMap<>();

        // Retrieve information from each field
        String nom = nomField.getText();
        String numeroCiviqueStr = numeroCiviqueField.getText();
        String rue = rueField.getText();
        String ville = villeField.getText();
        String province = provinceComboBox.getValue();
        String codePostal = codePostalField.getText();
        String telephone = telephoneField.getText();
        String email = emailField.getText();

        // Initialize a flag for validation
        boolean valid = true;

        // Validate numero civique
        int numeroCivique = -1;
        try {
            numeroCivique = Integer.parseInt(numeroCiviqueStr);
        } catch (NumberFormatException e) {
            Dialogs.showMessageDialog("Le numéro civique doit être un nombre!", "ERREUR NUMERO CIVIQUE");
            valid = false;
        }

        // Validate telephone number
        if (telephone == null || !telephone.matches("\\d{10}")) {
            Dialogs.showMessageDialog("Le numéro de téléphone doit contenir exactement 10 chiffres", "ERREUR NUMERO DE TELEPHONE");
            valid = false;
        }

        // Check if all fields are filled
        if (nom == null || nom.isEmpty() ||
                numeroCiviqueStr == null || numeroCiviqueStr.isEmpty() ||
                rue == null || rue.isEmpty() ||
                ville == null || ville.isEmpty() ||
                province == null || province.isEmpty() || // Check if province is null or empty
                codePostal == null || codePostal.isEmpty() ||
                telephone == null || telephone.isEmpty() ||
                email == null || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // If validation fails, return null
        if (!valid) {
            return null;
        }

        // If validation passes, add the retrieved values to the map
        partnerInfo.put("nom", nom);
        partnerInfo.put("numeroCivique", String.valueOf(numeroCivique));
        partnerInfo.put("rue", rue);
        partnerInfo.put("ville", ville);
        partnerInfo.put("province", province);
        partnerInfo.put("codePostal", codePostal);
        partnerInfo.put("telephone", telephone);
        partnerInfo.put("email", email);

        return partnerInfo;
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Suppression".
     * Récupère les informations du partenaire et demande une confirmation pour la suppression.
     * Si la suppression est confirmée, supprime le partenaire de la base de données.
     */
    private void actionBtnSuppression() {
        Map<String, String> infos = retrieveInfos();
        if (infos != null) {
            String message = "Vous êtes sur le point de supprimer " + infos.get("nom") + ". Voulez-vous continuer?";
            if (Dialogs.showConfirmDialog(message, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC")) {
                if (DBUtils.deletePartner(infos)) {
                    String messageConfirmation = infos.get("nom") + " a bel et bien été supprimé.";
                    Dialogs.showMessageDialog(messageConfirmation, "CONFIRMATION SUPPRESSION - F4 SANTÉ INC");
                    Stage stage = (Stage) btnSuppression.getScene().getWindow();
                    stage.close();
                }
            }
        }
    }

    /**
     * Remplit les champs de texte avec les informations du partenaire.
     */
    private void remplirTextFields() {
        if (partenaire != null) {
            nomField.setText(partenaire.getNom());

            Adresse adresse = partenaire.getAdresseObj(); // Obtient l'objet Adresse

            if (adresse != null) {
                numeroCiviqueField.setText(adresse.getNumeroCivique());
                rueField.setText(adresse.getRue());
                villeField.setText(adresse.getVille());
                codePostalField.setText(adresse.getCodePostal());
                provinceComboBox.setValue(adresse.getProvince());
            }
            telephoneField.setText(partenaire.getTelephone());
            emailField.setText(partenaire.getCourriel());
        }
    }

    /**
     * Rend les champs de texte en lecture seule pour empêcher toute modification.
     */
    private void setFieldsReadOnly() {
        nomField.setEditable(false);
        numeroCiviqueField.setEditable(false);
        rueField.setEditable(false);
        villeField.setEditable(false);
        codePostalField.setEditable(false);
        telephoneField.setEditable(false);
        emailField.setEditable(false);
        provinceComboBox.setDisable(true);
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Cancel".
     * Ferme la fenêtre sans effectuer de suppression.
     */
    private void actionBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
