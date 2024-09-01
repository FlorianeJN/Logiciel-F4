package com.f4.logicielf4.Views;

import com.f4.logicielf4.Controllers.Admin.AdminController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Dashboard.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getFacturesView(){
        if(facturesView == null ){
            try{
                facturesView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionFactures.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return facturesView;
    }

    public AnchorPane getEmployesView(){
        if(employesView == null ){
            try{
                employesView= new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionEmployes.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return employesView;
    }

    public AnchorPane getPartenairesView() {
        if(partenairesView == null ){
            try{
                partenairesView= new FXMLLoader(getClass().getResource("/Fxml/Admin/Gestionpartenaires.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return partenairesView;
    }

    public AnchorPane getProfilView() {
        if(profilView == null ){
            try{
                profilView= new FXMLLoader(getClass().getResource("/Fxml/Admin/Profil.fxml")).load();
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
        stage.setTitle("Système de gestion de quarts - F4 SANTÉ INC");
        stage.setResizable(false);
        stage.show();
    }

    public void closeStage(Stage stage){
        stage.close();
    }

}
