package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe contrôleur pour gérer le menu dans l'interface Admin.
 * <p>
 * Ce contrôleur gère les actions pour divers boutons dans le menu, tels que la navigation vers différentes vues,
 * la gestion de la déconnexion de l'utilisateur, et d'autres actions.
 * </p>
 */
public class MenuController implements Initializable {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnFacture;

    @FXML
    private Button btnPartenaire;

    @FXML
    private Button btnEmploye;

    @FXML
    private Button btnMonProfil;

    @FXML
    private Button btnDeconnexion;

    @FXML
    private Button btnReport;

    /**
     * Initialise la classe contrôleur. Cette méthode est appelée après que le fichier FXML a été chargé.
     * <p>
     * Configure les gestionnaires d'événements pour les boutons du menu.
     * </p>
     *
     * @param url l'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou {@code null} si l'emplacement n'est pas connu
     * @param resourceBundle les ressources utilisées pour localiser l'objet racine, ou {@code null} si l'objet racine n'a pas été localisé
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDashboard.setOnAction(event -> actionBtnDashboard());
        btnFacture.setOnAction(event -> actionBtnFacture());
        btnPartenaire.setOnAction(event -> actionBtnPartenaire());
        btnEmploye.setOnAction(event -> actionBtnEmploye());
        btnMonProfil.setOnAction(event -> actionBtnMonProfil());
        btnDeconnexion.setOnAction(event -> actionBtnDeconnexion());
        btnReport.setOnAction(event -> actionBtnReport());
    }

    /**
     * Gère l'action du bouton Dashboard. Définit l'option sélectionnée dans le modèle de vue sur "Dashboard".
     */
    private void actionBtnDashboard() {
        System.out.println("Bouton Dashboard appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Dashboard");
    }

    /**
     * Gère l'action du bouton Facture. Définit l'option sélectionnée dans le modèle de vue sur "Factures".
     */
    private void actionBtnFacture() {
        System.out.println("Bouton Facture appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Factures");
    }

    /**
     * Gère l'action du bouton Partenaire. Définit l'option sélectionnée dans le modèle de vue sur "Partenaires".
     */
    private void actionBtnPartenaire() {
        System.out.println("Bouton Partenaire appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Partenaires");
    }

    /**
     * Gère l'action du bouton Employé. Définit l'option sélectionnée dans le modèle de vue sur "Employes".
     */
    private void actionBtnEmploye() {
        System.out.println("Bouton Employé appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Employes");
    }

    /**
     * Gère l'action du bouton Profil. Définit l'option sélectionnée dans le modèle de vue sur "Profil".
     */
    private void actionBtnMonProfil() {
        System.out.println("Bouton Profil appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Profil");
    }

    /**
     * Gère l'action du bouton Déconnexion. Affiche une boîte de dialogue de confirmation avant de fermer la fenêtre
     * actuelle et de montrer la fenêtre de connexion.
     */
    private void actionBtnDeconnexion() {
        System.out.println("Bouton Déconnexion appuyé");
        boolean deconnexion = Dialogs.showConfirmDialog("Vous êtes sur le point de vous déconnecter. Voulez-vous continuer?", "CONFIRMATION DÉCONNEXION - F4 SANTÉ INC");
        if (deconnexion) {
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            stage.close();
            Model.getInstance().getViewFactory().showLoginWindow();
        }
    }

    /**
     * Gère l'action du bouton Report. Actuellement, cette action est non définie.
     */
    private void actionBtnReport() {
        System.out.println("Bouton Report appuyé");
    }
}
