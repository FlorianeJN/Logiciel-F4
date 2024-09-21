package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Models.Quart;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitaire pour les opérations de base de données.
 */
public class DBUtils {

    private static final String url = "jdbc:mysql://localhost:3306/f4santeinc";
    private static final String user = "root";
    private static final String pass = "!Secure!2011";

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

    public static List<Partenaire> fetchAllActivePartners() {
        List<Partenaire> partners = new ArrayList<>();

        String query = "SELECT * FROM partenaire WHERE status = 'actif'";

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
        String query = "DELETE FROM partenaire WHERE nom = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir le nom du partenaire à supprimer
            preparedStatement.setString(1, partnerInfo.get("nom"));

            // Exécuter la suppression
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été supprimée

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
        String query = "DELETE FROM employes WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the employee ID from the employeeInfo map
            preparedStatement.setInt(1, Integer.parseInt(employeeInfo.get("id"))); // Assuming ID is provided in the map

            // Execute the delete
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Returns true if at least one row has been deleted

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<Facture> fetchAllFacture() {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM Facture";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                String numeroFacture = rs.getString("num_facture"); // Integer type for num_facture
                String nomPartenaire = rs.getString("nom_partenaire");
                LocalDate dateFacture = rs.getDate("date").toLocalDate();
                BigDecimal montantAvantTaxes = rs.getBigDecimal("montant_avant_taxes"); // Handle decimal fields as BigDecimal
                BigDecimal tps = rs.getBigDecimal("tps");
                BigDecimal tvq = rs.getBigDecimal("tvq");
                BigDecimal montantApresTaxes = rs.getBigDecimal("montant_apres_taxes");
                String statut = rs.getString("statut");

                // Assuming your Facture class has an appropriate constructor for these parameters
                Facture facture = new Facture(numeroFacture, nomPartenaire, dateFacture, montantAvantTaxes, tps, tvq, montantApresTaxes, statut);
                factures.add(facture);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return factures;
    }

    public static int obtenirProchainNumFacture() {
        int num = 0;
        String selectSql = "SELECT MAX(CAST(SUBSTRING_INDEX(num_facture, '-', 1) AS UNSIGNED)) AS maxNum FROM Facture";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement statement = connection.prepareStatement(selectSql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                // Get the maximum number from the result set
                num = rs.getInt("maxNum");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Increment the number for the next invoice
        return num + 1;
    }

    public static boolean createNewInvoice(String numFacture, String partner, LocalDate invoiceDate, String status) {
        String insertSql = "INSERT INTO Facture (num_facture, nom_partenaire, date, montant_avant_taxes, tps, tvq, montant_apres_taxes, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement statement = connection.prepareStatement(insertSql)) {

            // Set parameters for the SQL statement
            statement.setString(1, numFacture);  // num_facture
            statement.setString(2, partner);     // nom_partenaire
            statement.setDate(3, java.sql.Date.valueOf(invoiceDate)); // date

            // Set the remaining parameters to null
            statement.setNull(4, java.sql.Types.DECIMAL); // montant_avant_taxes
            statement.setNull(5, java.sql.Types.DECIMAL); // tps
            statement.setNull(6, java.sql.Types.DECIMAL); // tvq
            statement.setNull(7, java.sql.Types.DECIMAL); // montant_apres_taxes
            statement.setString(8, status);    // statut

            // Execute the update
            int rowsAffected = statement.executeUpdate();

            // Return true if at least one row was affected
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    public static void ajouterQuart(String numFacture, String prestation, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart,
                                    LocalTime pause, String tempsTotal, double tauxHoraire, double montantTotal, String notes, String empName,
                                    boolean tempsDouble, boolean tempsDemi) throws SQLException {
        String sql = "INSERT INTO Quart (num_facture, date_quart, debut_quart, fin_quart, pause, temps_total, prestation, taux_horaire, montant_total, notes, emp_name, tempsDemi, tempsDouble) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numFacture);
            pstmt.setDate(2, java.sql.Date.valueOf(dateQuart));
            pstmt.setTime(3, java.sql.Time.valueOf(debutQuart));
            pstmt.setTime(4, java.sql.Time.valueOf(finQuart));
            pstmt.setTime(5, java.sql.Time.valueOf(pause));
            pstmt.setString(6, tempsTotal);
            pstmt.setString(7, prestation);
            pstmt.setDouble(8, tauxHoraire);
            pstmt.setDouble(9, montantTotal);
            pstmt.setString(10, notes);
            pstmt.setString(11, empName);
            pstmt.setInt(12, tempsDemi ? 1 : 0);
            pstmt.setInt(13, tempsDouble ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    public static List<Quart> fetchQuartsByNumFacture(String numFacture) {
        List<Quart> quarts = new ArrayList<>();

        String query = "SELECT * FROM Quart WHERE num_facture = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, numFacture);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String numFactureResult = rs.getString("num_facture");
                Date dateQuart = rs.getDate("date_quart");
                Time debutQuart = rs.getTime("debut_quart");
                Time finQuart = rs.getTime("fin_quart");
                Time pause = rs.getTime("pause");
                String tempsTotal = rs.getString("temps_total"); // Changed from BigDecimal to double
                String prestation = rs.getString("prestation");
                BigDecimal tauxHoraire = rs.getBigDecimal("taux_horaire");
                BigDecimal montantTotal = rs.getBigDecimal("montant_total");
                String notes = rs.getString("notes");
                String nomEmp = rs.getString("emp_name");
                boolean tempsDemi = rs.getInt("tempsDemi") == 1;  // Converts 1 to true and 0 to false
                boolean tempsDouble = rs.getInt("tempsDouble") == 1;  // Converts 1 to true and 0 to false

                Quart quart = new Quart(id, numFactureResult, dateQuart, debutQuart, finQuart, pause,
                        tempsTotal, prestation, tauxHoraire, montantTotal, notes,nomEmp,tempsDouble,tempsDemi);
                quarts.add(quart);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
        }

        return quarts;
    }

    public static void mettreAJourFacture(Facture facture){
// SQL query to update the Facture table
        String updateQuery = "UPDATE Facture SET montant_avant_taxes = ?, tps = ?, tvq = ?, montant_apres_taxes = ? WHERE num_facture = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            // Set parameters from the Facture object
            pstmt.setBigDecimal(1, facture.getMontantAvantTaxes());
            pstmt.setBigDecimal(2, facture.getTps());
            pstmt.setBigDecimal(3, facture.getTvq());
            pstmt.setBigDecimal(4, facture.getMontantApresTaxes());
            pstmt.setString(5, facture.getNumFacture());

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
        }
    }

    public static boolean supprimerQuart(int id) {
        String sql = "DELETE FROM Quart WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);  // Set the ID of the quart to be deleted
            int rowsAffected = pstmt.executeUpdate(); // Execute the delete operation

            // Return true if at least one row was deleted
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Log the exception (you could use a logging framework)
            System.err.println("Error while deleting quart: " + e.getMessage());
            return false; // Return false if an exception occurs
        }
    }

    public static void updateQuart(int id, String prestation, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart,
                                   LocalTime pause, String tempsTotal, double tauxHoraire, double montantTotal, String notes, String empName,
                                   boolean tempsDouble, boolean tempsDemi) throws SQLException {
        String sql = "UPDATE Quart SET prestation = ?, debut_quart = ?, fin_quart = ?, pause = ?, temps_total = ?, " +
                "taux_horaire = ?, montant_total = ?, notes = ?, emp_name = ?, tempsDemi = ?, tempsDouble = ? " +
                "WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, prestation);
            pstmt.setTime(2, java.sql.Time.valueOf(debutQuart));
            pstmt.setTime(3, java.sql.Time.valueOf(finQuart));
            pstmt.setTime(4, java.sql.Time.valueOf(pause));
            pstmt.setString(5, tempsTotal);
            pstmt.setDouble(6, tauxHoraire);
            pstmt.setDouble(7, montantTotal);
            pstmt.setString(8, notes);
            pstmt.setString(9, empName);
            pstmt.setInt(10, tempsDemi ? 1 : 0);
            pstmt.setInt(11, tempsDouble ? 1 : 0);

            // Use the id to find the correct record to update
            pstmt.setInt(12, id);

            pstmt.executeUpdate();
        }
    }



}


