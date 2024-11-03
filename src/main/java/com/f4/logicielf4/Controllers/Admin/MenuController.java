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
 * Contrôleur pour gérer le menu dans l'interface Admin.
 * <p>
 * Ce contrôleur gère les actions associées aux boutons du menu,
 * permettant de naviguer vers différentes sections comme le tableau de bord,
 * la gestion des factures, des partenaires, des employés,
 * ainsi que les actions de déconnexion.
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
     * Initialise la classe contrôleur après le chargement du FXML.
     * <p>
     * Configure les actions à exécuter lorsque chaque bouton est appuyé.
     * </p>
     *
     * @param url L'URL de la ressource FXML (non utilisé dans ce contexte).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé dans ce contexte).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnFacture.setOnAction(event -> actionBtnFacture());
        btnPartenaire.setOnAction(event -> actionBtnPartenaire());
        btnEmploye.setOnAction(event -> actionBtnEmploye());
        btnMonProfil.setOnAction(event -> actionBtnMonProfil());
        btnDeconnexion.setOnAction(event -> actionBtnDeconnexion());
        btnReport.setOnAction(event -> actionBtnReport());
    }

    /**
     * Gère l'action du bouton "Facture".
     * Sélectionne l'option "Factures" dans le modèle de vue pour afficher la gestion des factures.
     */
    private void actionBtnFacture() {
        System.out.println("Bouton Facture appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Factures");
    }

    /**
     * Gère l'action du bouton "Partenaire".
     * Sélectionne l'option "Partenaires" dans le modèle de vue pour afficher la gestion des partenaires.
     */
    private void actionBtnPartenaire() {
        System.out.println("Bouton Partenaire appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Partenaires");
    }

    /**
     * Gère l'action du bouton "Employé".
     * Sélectionne l'option "Employes" dans le modèle de vue pour afficher la gestion des employés.
     */
    private void actionBtnEmploye() {
        System.out.println("Bouton Employé appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Employes");
    }

    /**
     * Gère l'action du bouton "Profil".
     * Sélectionne l'option "Profil" dans le modèle de vue pour afficher la section du profil de l'utilisateur.
     */
    private void actionBtnMonProfil() {
        System.out.println("Bouton Profil appuyé");
        Model.getInstance().getViewFactory().getOptionSelectionnee().set("Profil");
    }

    /**
     * Gère l'action du bouton "Déconnexion".
     * Affiche une boîte de dialogue pour confirmer la déconnexion. Si confirmée, ferme la fenêtre actuelle
     * et retourne à la fenêtre de connexion.
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
     * Gère l'action du bouton "Report".
     * Cette action n'est pas encore définie et affiche simplement un message.
     */
    private void actionBtnReport() {
        System.out.println("Bouton Report appuyé");
        Stage stage = (Stage) btnReport.getScene().getWindow();
        Model.getInstance().getViewFactory().showReportBugWindow(stage);
    }
}
