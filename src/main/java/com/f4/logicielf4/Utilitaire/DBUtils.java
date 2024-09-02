package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.Partenaire;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBUtils {

    private static String url = "jdbc:mysql://127.0.0.1:3306/f4santeinc";
    private static String user = "root";
    private static String pass = "!Secure!2011";

    public static boolean loginUser(String username, String password) {
        boolean loggedIn = false;

        //String url = "jdbc:mysql://127.0.0.1:3306/f4santeinc";
        //String user = "root";
       // String pass = "!Secure!2011";

        String query = "SELECT password FROM user WHERE id = ?";

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

    public static List<Partenaire> fetchAllPartners() {
        List<Partenaire> partners = new ArrayList<>();

        String query = "SELECT * FROM partenaire";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                String numeroCivique = rs.getString("numero_civique");
                String rue = rs.getString("rue");
                String ville = rs.getString("ville");
                String province = rs.getString("province");
                String codePostal = rs.getString("code_postal");
                String telephone = rs.getString("telephone");
                String courriel = rs.getString("courriel");

                Partenaire partner = new Partenaire(nom, numeroCivique, rue, ville, province, codePostal, telephone, courriel);
                partners.add(partner);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return partners;
    }

    public static boolean addPartner(Map<String, String> partnerInfo) {
        String query = "INSERT INTO partenaire (nom, numero_civique, rue, ville, province, code_postal, telephone, courriel) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters
            preparedStatement.setString(1, partnerInfo.get("nom"));
            preparedStatement.setString(2, partnerInfo.get("numseroCivique"));
            preparedStatement.setString(3, partnerInfo.get("rue"));
            preparedStatement.setString(4, partnerInfo.get("ville"));
            preparedStatement.setString(5, partnerInfo.get("province"));
            preparedStatement.setString(6, partnerInfo.get("codePostal"));
            preparedStatement.setString(7, partnerInfo.get("telephone"));
            preparedStatement.setString(8, partnerInfo.get("email"));

            // Execute the insert
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Return true if at least one row was inserted

        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception as needed
            return false; // Return false if there was an error
        }
    }


    public static boolean updatePartner(){
        return false;
    }

    public static boolean deletePartner(){
        return false;
    }

}
