package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.FactureInfo;
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

/**
 * Classe utilitaire pour les opérations de base de données.
 */
public class DBUtils {

    private static final String url = "jdbc:mysql://sql5.freesqldatabase.com:3306/sql5729770";
    private static final String user = "sql5729770";
    private static final String pass = "99BunXYQIF";

    /**
     * Vérifie les informations d'identification d'un utilisateur pour se connecter.
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return true si les informations d'identification sont correctes, false sinon
     */
    public static boolean loginUser(String username, String password) {
        boolean loggedIn = false;

        String query = "SELECT password FROM user WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psLogin = connection.prepareStatement(query)) {

            psLogin.setString(1, username);
            try (ResultSet resultSet = psLogin.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // L'utilisateur n'existe pas
                    Dialogs.showMessageDialog("L'utilisateur n'existe pas. Contactez l'admin pour le rajouter!", "ERREUR UTILISATEUR - F4 SANTÉ INC");
                } else {
                    String retrievedPassword = null;
                    while (resultSet.next()) {
                        retrievedPassword = resultSet.getString("password");
                    }

                    if (retrievedPassword != null) {
                        if (retrievedPassword.equals(password)) {
                            // Connexion réussie
                            loggedIn = true;
                        } else {
                            // Mot de passe incorrect
                            Dialogs.showMessageDialog("Mot de passe incorrect!", "ERREUR MOT DE PASSE - F4 SANTÉ INC");
                        }
                    } else {
                        System.err.println("Le mot de passe est null (Non récupéré)");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loggedIn;
    }

    /**
     * Récupère tous les partenaires depuis la base de données.
     *
     * @return Liste de tous les partenaires
     */
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
                String status = rs.getString("status");

                Partenaire partner = new Partenaire(nom, numeroCivique, rue, ville, province, codePostal, telephone, courriel, status);
                partners.add(partner);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return partners;
    }

    /**
     * Ajoute un nouveau partenaire dans la base de données.
     *
     * @param partnerInfo Informations du partenaire à ajouter
     * @return true si le partenaire a été ajouté avec succès, false sinon
     */
    public static boolean addPartner(Map<String, String> partnerInfo) {
        String query = "INSERT INTO partenaire (nom, numero_civique, rue, ville, province, code_postal, telephone, courriel, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir les paramètres
            preparedStatement.setString(1, partnerInfo.get("nom"));
            preparedStatement.setString(2, partnerInfo.get("numeroCivique"));
            preparedStatement.setString(3, partnerInfo.get("rue"));
            preparedStatement.setString(4, partnerInfo.get("ville"));
            preparedStatement.setString(5, partnerInfo.get("province"));
            preparedStatement.setString(6, partnerInfo.get("codePostal"));
            preparedStatement.setString(7, partnerInfo.get("telephone"));
            preparedStatement.setString(8, partnerInfo.get("email"));
            preparedStatement.setString(9, "actif");

            // Exécuter l'insertion
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été insérée

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour les informations d'un partenaire existant dans la base de données.
     *
     * @param partnerInfo Informations du partenaire à mettre à jour
     * @return true si le partenaire a été mis à jour avec succès, false sinon
     */
    public static boolean updatePartner(Map<String, String> partnerInfo) {
        String query = "UPDATE partenaire SET "
                + "numero_civique = ?, "
                + "rue = ?, "
                + "ville = ?, "
                + "province = ?, "
                + "code_postal = ?, "
                + "telephone = ?, "
                + "courriel = ?, "
                + "status = ? "
                + "WHERE nom = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir les paramètres
            preparedStatement.setString(1, partnerInfo.get("numeroCivique"));
            preparedStatement.setString(2, partnerInfo.get("rue"));
            preparedStatement.setString(3, partnerInfo.get("ville"));
            preparedStatement.setString(4, partnerInfo.get("province"));
            preparedStatement.setString(5, partnerInfo.get("codePostal"));
            preparedStatement.setString(6, partnerInfo.get("telephone"));
            preparedStatement.setString(7, partnerInfo.get("email"));
            preparedStatement.setString(8, "actif");
            preparedStatement.setString(9, partnerInfo.get("nom")); // Le record à mettre à jour

            // Exécuter la mise à jour
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été mise à jour

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un partenaire de la base de données en mettant à jour son statut.
     *
     * @param partnerInfo Informations du partenaire à supprimer
     * @return true si le partenaire a été marqué comme inactif avec succès, false sinon
     */
    public static boolean deletePartner(Map<String, String> partnerInfo) {
        String query = "UPDATE partenaire SET status = ? WHERE nom = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir le statut à "inactif"
            preparedStatement.setString(1, "inactif");
            preparedStatement.setString(2, partnerInfo.get("nom"));

            // Exécuter la mise à jour
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été mise à jour

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all employees from the database.
     *
     * @return A list of all employees.
     */
    public static List<Employe> fetchAllEmployees() {
        List<Employe> employes = new ArrayList<>();
        String query = "SELECT * FROM employes"; // Ensure the table name matches

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String telephone = rs.getString("telephone");
                String email = rs.getString("email");
                String statut = rs.getString("statut");

                Employe employe = new Employe(id, nom, prenom, telephone, email, statut);
                employes.add(employe);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employes;
    }

    public static boolean addEmployee(Map<String, String> employeeInfo) {
        String query = "INSERT INTO employes (nom, prenom, telephone, email, statut) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters from the employeeInfo map
            preparedStatement.setString(1, employeeInfo.get("nom"));
            preparedStatement.setString(2, employeeInfo.get("prenom"));
            preparedStatement.setString(3, employeeInfo.get("telephone"));
            preparedStatement.setString(4, employeeInfo.get("email"));
            preparedStatement.setString(5, "Actif"); // Default to 'Actif'

            // Execute the insertion
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Returns true if at least one row has been inserted

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateEmploye(Map<String, String> employeeInfo) {
        // First, retrieve the current status of the employee
        String currentStatut = null;
        String selectQuery = "SELECT statut FROM employes WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setInt(1, Integer.parseInt(employeeInfo.get("id")));
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                currentStatut = resultSet.getString("statut");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Now, construct the update query without changing the statut
        String updateQuery = "UPDATE employes SET "
                + "nom = ?, "
                + "prenom = ?, "
                + "telephone = ?, "
                + "email = ?, "
                + "statut = ? " // Use the current status
                + "WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set parameters from the employeeInfo map
            preparedStatement.setString(1, employeeInfo.get("nom"));
            preparedStatement.setString(2, employeeInfo.get("prenom"));
            preparedStatement.setString(3, employeeInfo.get("telephone"));
            preparedStatement.setString(4, employeeInfo.get("email"));
            preparedStatement.setString(5, currentStatut); // Use the current statut
            preparedStatement.setInt(6, Integer.parseInt(employeeInfo.get("id"))); // The record to update by ID

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Returns true if at least one row has been updated

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteEmploye(Map<String, String> employeeInfo) {
        String query = "UPDATE employes SET statut = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the status to "Inactif"
            preparedStatement.setString(1, "Inactif");
            preparedStatement.setInt(2, Integer.parseInt(employeeInfo.get("id"))); // Assuming ID is provided in the map

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Returns true if at least one row has been updated

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<FactureInfo> fetchAllFactureInfo() {
        List<FactureInfo> factures = new ArrayList<>();
        String query = "SELECT * FROM Info_facture";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                String numeroFacture = rs.getString("numero_facture");
                String nomPartenaire = rs.getString("nom_partenaire");
                String date = rs.getString("date");
                String montant = rs.getString("montant");
                String statut = rs.getString("statut");

                FactureInfo facture = new FactureInfo(numeroFacture, nomPartenaire, date, montant, statut);
                factures.add(facture);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return factures;
    }



}


