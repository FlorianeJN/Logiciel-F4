package com.f4.logicielf4.Controllers;

import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Quand on appuie sur le bouton se connecter, on vérifie l'identifiant et le pw (accès à la base de données).
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Bouton login appuyé!");

                String username = usernameField.getText();
                String password = passwordField.getText();
                DBUtils.loginUser(actionEvent,username,password);
                //DBUtils.changeScene(actionEvent,"/Fxml/Login.fxml","Admin");
            }
        });
    }
}
