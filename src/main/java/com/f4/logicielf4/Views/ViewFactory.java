package com.f4.logicielf4.Views;

import com.f4.logicielf4.Controllers.Admin.AdminController;
import com.f4.logicielf4.Controllers.Admin.GestionEmploye.AjouterEmployeController;
import com.f4.logicielf4.Controllers.Admin.GestionEmploye.MiseAJourEmployeController;
import com.f4.logicielf4.Controllers.Admin.GestionEmploye.SuppressionEmployeController;
import com.f4.logicielf4.Controllers.Admin.GestionPartenaire.MiseAJourPartenaireController;
import com.f4.logicielf4.Controllers.Admin.GestionPartenaire.SuppressionPartenaireController;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Partenaire;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe de fabrique pour créer et gérer les différentes vues de l'application.
 * Gère le chargement des fichiers FXML et l'affichage des fenêtres diverses à l'utilisateur.
 */
public class ViewFactory {

    private AnchorPane dashboardView;
    private AnchorPane facturesView;
    private AnchorPane employesView;
    private AnchorPane partenairesView;
    private AnchorPane profilView;
    private final StringProperty optionSelectionnee;

    /**
     * Construit une instance de ViewFactory.
     */
    public ViewFactory() {
        this.optionSelectionnee = new SimpleStringProperty("");
    }

    /**
     * Obtient la propriété représentant l'option sélectionnée.
     *
     * @return la propriété représentant l'option sélectionnée
     */
    public StringProperty getOptionSelectionnee() {
        return this.optionSelectionnee;
    }

    /**
     * Récupère la vue du tableau de bord administrateur, la charge si nécessaire.
     *
     * @return la vue du tableau de bord administrateur
     */
    public AnchorPane getAdminDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/TableauDeBord/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    /**
     * Récupère la vue des factures, la charge si nécessaire.
     *
     * @return la vue des factures
     */
    public AnchorPane getFacturesView() {
        if (facturesView == null) {
            try {
                facturesView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionFacture/GestionFactures.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return facturesView;
    }

    /**
     * Affiche la fenêtre de mise à jour des informations d'un partenaire.
     *
     * @param stage      le stage parent
     * @param partenaire le partenaire dont les informations doivent être mises à jour
     */
    public void showUpdatePartnerWindow(Stage stage, Partenaire partenaire) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/MiseAJourPartenaire.fxml"));
        MiseAJourPartenaireController controller = new MiseAJourPartenaireController(partenaire);
        loader.setController(controller);
        createStageShowAndWait(loader, stage, "MISE À JOUR INFORMATIONS PARTENAIRE");
    }

    /**
     * Affiche la fenêtre de mise à jour des informations d'un employé.
     *
     * @param stage    le stage parent
     * @param employe  l'employé dont les informations doivent être mises à jour
     */
    public void showUpdateEmployeeWindow(Stage stage, Employe employe) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/MiseAJourEmploye.fxml"));
        MiseAJourEmployeController controller = new MiseAJourEmployeController(employe);
        loader.setController(controller);
        createStageShowAndWait(loader, stage, "MISE À JOUR INFORMATIONS EMPLOYÉ");
    }

    /**
     * Récupère la vue des employés, la charge si nécessaire.
     *
     * @return la vue des employés
     */
    public AnchorPane getEmployesView() {
        if (employesView == null) {
            try {
                employesView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/GestionEmployes.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return employesView;
    }

    /**
     * Récupère la vue des partenaires, la charge si nécessaire.
     *
     * @return la vue des partenaires
     */
    public AnchorPane getPartenairesView() {
        if (partenairesView == null) {
            try {
                partenairesView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/GestionPartenaires.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return partenairesView;
    }

    /**
     * Récupère la vue du profil, la charge si nécessaire.
     *
     * @return la vue du profil
     */
    public AnchorPane getProfilView() {
        if (profilView == null) {
            try {
                profilView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Profil/Profil.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profilView;
    }

    /**
     * Affiche la fenêtre de connexion.
     */
    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    /**
     * Affiche la fenêtre principale de l'administrateur.
     */
    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader);
    }

    /**
     * Crée et affiche un stage avec le contenu du loader donné.
     *
     * @param loader le loader FXML
     */
    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Système de gestion - F4 SANTÉ INC");
        stage.setResizable(false);

        rajouterImage(stage);

        stage.show();
    }

    /**
     * Affiche la fenêtre pour ajouter un partenaire.
     *
     * @param stage le stage parent
     */
    public void showAddPartnerWindow(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/AjouterPartenaire.fxml"));
        createStageShowAndWait(loader, stage, "Ajouter un Partenaire - F4 SANTÉ INC");
    }

    /**
     * Affiche la fenêtre pour ajouter un employé.
     *
     * @param stage le stage parent
     */
    public void showAddEmployeeWindow(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/AjouterEmploye.fxml"));
        AjouterEmployeController controller = new AjouterEmployeController();
        loader.setController(controller);
        createStageShowAndWait(loader, stage, "Ajouter un Employé - F4 SANTÉ INC");
    }

    /**
     * Crée et affiche un stage en mode modal avec le contenu du loader donné.
     *
     * @param loader le loader FXML
     * @param originalStage le stage parent
     * @param titre le titre de la fenêtre
     */
    private void createStageShowAndWait(FXMLLoader loader, Stage originalStage, String titre) {
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle(titre);
            rajouterImage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(originalStage);
            stage.showAndWait();  // Bloque l'interaction avec d'autres fenêtres jusqu'à la fermeture de cette fenêtre
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre de suppression d'un partenaire.
     *
     * @param stage le stage parent
     * @param partenaire le partenaire à supprimer
     */
    public void showDeletePartnerWindow(Stage stage, Partenaire partenaire) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/SuppressionPartenaire.fxml"));
        SuppressionPartenaireController controller = new SuppressionPartenaireController(partenaire);
        loader.setController(controller);
        createStageShowAndWait(loader, stage, "Supprimer un partenaire - F4 SANTÉ INC");
    }

    /**
     * Affiche la fenêtre de suppression d'un employé.
     *
     * @param stage le stage parent
     * @param employe l'employé à supprimer
     */
    public void showDeleteEmployeeWindow(Stage stage, Employe employe) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/SuppressionEmploye.fxml"));
        SuppressionEmployeController controller = new SuppressionEmployeController(employe);
        loader.setController(controller);
        createStageShowAndWait(loader, stage, "Supprimer un employé - F4 SANTÉ INC");
    }

    /**
     * Ajoute une image d'icône au stage.
     *
     * @param stage le stage auquel ajouter l'image
     */
    private void rajouterImage(Stage stage) {
        Image icon = new Image(getClass().getResourceAsStream("/Images/logo.jpg"));
        stage.getIcons().add(icon);
    }

}
