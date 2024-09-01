package com.f4.logicielf4.Utilitaire;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBUtils {

    public static boolean loginUser(ActionEvent event, String username, String password) {
        boolean loggedIn = false;

        String url = "jdbc:mysql://127.0.0.1:3306/f4santeinc";
        String user = "root";
        String pass = "!Secure!2011";

        String query = "SELECT password FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psLogin = connection.prepareStatement(query)) {

            psLogin.setString(1, username);
            try (ResultSet resultSet = psLogin.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // User does not exist
                   showAlert(Alert.AlertType.INFORMATION, "L'utilisateur n'existe pas. Contactez l'admin pour le rajouter!");
                } else {
                    String retrievedPassword = null;
                    while (resultSet.next()) {
                        retrievedPassword = resultSet.getString("password");
                    }

                    if (retrievedPassword != null) {
                        if (retrievedPassword.equals(password)) {
                            // Successful login
                            loggedIn = true;
                        } else {
                            // Incorrect password
                            showAlert(Alert.AlertType.INFORMATION, "Mot de passe incorrect!");
                        }
                    } else {
                        System.err.println("Password is null (Did not get retrieved)");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loggedIn;
    }

    private static void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }

}
