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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnAnnuler.setOnAction(event -> actionBtnAnnuler());
    }

    private void actionBtnAjouter(){
        // Handle the "Ajouter" button action here
        System.out.println("Add Partner button clicked");

        // Retrieve the information
        Map<String, String> partnerInfo = retrieveInfos();

        if(partnerInfo != null) {
            if(DBUtils.addPartner(partnerInfo)){
                Dialogs.showMessageDialog("Le partenaire a été ajouté avec succès!","AJOUT PARTENAIRE TERMINÉ");
                System.out.println("returned true (addPartner)");
            }
        }
    }

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
                province == null || province.isEmpty() ||
                codePostal == null || codePostal.isEmpty() ||
                telephone == null || telephone.isEmpty() ||
                email == null || email.isEmpty()) {
            Dialogs.showMessageDialog("Veuillez remplir tous les champs", "ERREUR REMPLISSAGE DES CHAMPS");
            valid = false;
        }

        // If validation passes, add the retrieved values to the map
        if (valid) {
            partnerInfo.put("nom", nom);
            partnerInfo.put("numseroCivique", String.valueOf(numeroCivique));
            partnerInfo.put("rue", rue);
            partnerInfo.put("ville", ville);
            partnerInfo.put("province", province);
            partnerInfo.put("codePostal", codePostal);
            partnerInfo.put("telephone", telephone);
            partnerInfo.put("email", email);

            // Reset fields and close window
            reinitialiserChamps();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();

            return partnerInfo; // Return the map with all the information
        }

        // If validation fails, return null to indicate failure
        return null;
    }



    private void reinitialiserChamps(){
        nomField.setText("");
        numeroCiviqueField.setText("");
        rueField.setText("");
        villeField.setText("");
        provinceComboBox.getItems().clear();
        codePostalField.setText("");
        telephoneField.setText("");
        emailField.setText("");
    }

    private void actionBtnAnnuler(){
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}

