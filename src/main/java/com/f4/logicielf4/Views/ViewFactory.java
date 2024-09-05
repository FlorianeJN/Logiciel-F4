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

public class ViewFactory {

    private AnchorPane dashboardView;
    private AnchorPane facturesView;
    private AnchorPane employesView;
    private AnchorPane partenairesView;
    private AnchorPane profilView;
    private final StringProperty optionSelectionnee;

    public ViewFactory(){
        this.optionSelectionnee = new SimpleStringProperty("");
    }

    public StringProperty getOptionSelectionnee () {
        return this.optionSelectionnee;
    }

    public AnchorPane getAdminDashboardView(){
        if(dashboardView == null){
            try{
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/TableauDeBord/Dashboard.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getFacturesView(){
        if(facturesView == null ){
            try{
                facturesView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionFacture/GestionFactures.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return facturesView;
    }

    public void showUpdatePartnerWindow(Stage stage, Partenaire partenaire){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/MiseAJourPartenaire.fxml"));
       MiseAJourPartenaireController controller = new MiseAJourPartenaireController(partenaire);
       loader.setController(controller);
        createStageShowAndWait(loader,stage,"MISE A JOUR INFORMATIONS PARTENAIRE");
    }

    public void showUpdateEmployeeWindow(Stage stage, Employe employe){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/MiseAJourEmploye.fxml"));
        MiseAJourEmployeController controller = new MiseAJourEmployeController(employe);
        loader.setController(controller);
        createStageShowAndWait(loader,stage,"MISE A JOUR INFORMATIONS PARTENAIRE");
    }

    public AnchorPane getEmployesView(){
        if(employesView == null ){
            try{
                employesView= new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/GestionEmployes.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return employesView;
    }

    public AnchorPane getPartenairesView() {
        if(partenairesView == null ){
            try{
                partenairesView= new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/GestionPartenaires.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return partenairesView;
    }

    public AnchorPane getProfilView() {
        if(profilView == null ){
            try{
                profilView= new FXMLLoader(getClass().getResource("/Fxml/Admin/Profil/Profil.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return profilView;
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        }catch(Exception e){
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Système de gestion - F4 SANTÉ INC");
        stage.setResizable(false);

        rajouterImage(stage);

        stage.show();
    }

    public void showAddPartnerWindow(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/AjouterPartenaire.fxml"));
        createStageShowAndWait(loader,stage,"Ajouter un Partenaire - F4 SANTÉ INC");
    }

    public void showAddEmployeeWindow(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/AjouterEmploye.fxml"));
        AjouterEmployeController controller = new AjouterEmployeController();
        loader.setController(controller);
        createStageShowAndWait(loader,stage,"Ajouter un Employé - F4 SANTÉ INC");
    }

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

    public void showDeletePartnerWindow(Stage stage, Partenaire partenaire) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionPartenaire/SuppressionPartenaire.fxml"));
        SuppressionPartenaireController controller = new SuppressionPartenaireController(partenaire);
        loader.setController(controller);
        createStageShowAndWait(loader,stage,"Supprimer un partenaire - F4 SANTÉ INC");
    }

    public void showDeleteEmployeeWindow(Stage stage, Employe employe){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmploye/SuppressionEmploye.fxml"));
        SuppressionEmployeController controller = new SuppressionEmployeController(employe);
        loader.setController(controller);
        createStageShowAndWait(loader,stage,"Supprimer un employé - F4 SANTÉ INC");
    }


    private void rajouterImage(Stage stage) {
        Image icon = new Image(getClass().getResourceAsStream("/Images/logo.jpg"));
        stage.getIcons().add(icon);
    }

}
