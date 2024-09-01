package com.f4.logicielf4.Controllers;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordFieldVisible;

    @FXML
    private CheckBox showPasswordCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Bind password fields (one-way)
        passwordFieldVisible.textProperty().bind(passwordField.textProperty());

        // Handle show/hide password functionality
        showPasswordCheckBox.setOnAction(event -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setVisible(false);
                passwordFieldVisible.setVisible(true);
            } else {
                passwordField.setVisible(true);
                passwordFieldVisible.setVisible(false);
            }
        });

        // When login button is pressed, validate the username and password
        loginButton.setOnAction(actionEvent -> {
            String username = usernameField.getText();
            String password = passwordField.getText();  // Only using the PasswordField's content

            if (DBUtils.loginUser(actionEvent, username, password)) {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showAdminWindow();
            } else {
                clearFields();
            }

            // Clear sensitive data
            username = null;
            password = null;
        });
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        passwordFieldVisible.clear();
    }
}
