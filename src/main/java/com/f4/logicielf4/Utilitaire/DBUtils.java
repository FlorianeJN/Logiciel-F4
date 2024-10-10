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
 * Classe utilitaire pour effectuer des opérations sur la base de données relatives aux utilisateurs, partenaires, employés, factures et quarts.
 */
public class DBUtils {

    private static final String url = System.getenv("DB_URL");
    private static final String user = System.getenv("DB_USER");
    private static final String pass = System.getenv("DB_PASS");

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
     * Récupère tous les partenaires actifs depuis la base de données.
     *
     * @return Liste de tous les partenaires actifs
     */
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
     * Supprime un partenaire et toutes les factures associées de la base de données.
     *
     * @param partnerInfo Informations du partenaire à supprimer
     * @return true si le partenaire et les factures associées ont été supprimés avec succès, false sinon
     */
    public static boolean deletePartner(Map<String, String> partnerInfo) {
        String deleteFactureQuery = "DELETE FROM Facture WHERE nom_partenaire = ?";
        String deletePartnerQuery = "DELETE FROM partenaire WHERE nom = ?";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, pass);

            // Démarrer une transaction
            connection.setAutoCommit(false);

            // Supprimer toutes les factures associées d'abord
            try (PreparedStatement psDeleteFacture = connection.prepareStatement(deleteFactureQuery)) {
                psDeleteFacture.setString(1, partnerInfo.get("nom"));
                psDeleteFacture.executeUpdate();
            }

            // Maintenant supprimer le partenaire
            try (PreparedStatement psDeletePartner = connection.prepareStatement(deletePartnerQuery)) {
                psDeletePartner.setString(1, partnerInfo.get("nom"));
                int rowsAffected = psDeletePartner.executeUpdate();

                // Valider la transaction
                connection.commit();

                return rowsAffected > 0; // Retourne true si au moins une ligne a été supprimée
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    // Annuler en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    // Restaurer le mode auto-commit
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Récupère tous les employés depuis la base de données.
     *
     * @return Liste de tous les employés
     */
    public static List<Employe> fetchAllEmployees() {
        List<Employe> employes = new ArrayList<>();
        String query = "SELECT * FROM employes"; // Assurez-vous que le nom de la table correspond

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

    /**
     * Ajoute un nouvel employé dans la base de données.
     *
     * @param employeeInfo Informations de l'employé à ajouter
     * @return true si l'employé a été ajouté avec succès, false sinon
     */
    public static boolean addEmployee(Map<String, String> employeeInfo) {
        String query = "INSERT INTO employes (nom, prenom, telephone, email, statut) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir les paramètres
            preparedStatement.setString(1, employeeInfo.get("nom"));
            preparedStatement.setString(2, employeeInfo.get("prenom"));
            preparedStatement.setString(3, employeeInfo.get("telephone"));
            preparedStatement.setString(4, employeeInfo.get("email"));
            preparedStatement.setString(5, "Actif"); // Par défaut 'Actif'

            // Exécuter l'insertion
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été insérée

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour les informations d'un employé existant dans la base de données.
     *
     * @param employeeInfo Informations de l'employé à mettre à jour
     * @return true si l'employé a été mis à jour avec succès, false sinon
     */
    public static boolean updateEmploye(Map<String, String> employeeInfo) {
        // D'abord, récupérer le statut actuel de l'employé
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

        // Construire la requête de mise à jour sans changer le statut
        String updateQuery = "UPDATE employes SET "
                + "nom = ?, "
                + "prenom = ?, "
                + "telephone = ?, "
                + "email = ?, "
                + "statut = ? "
                + "WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Définir les paramètres
            preparedStatement.setString(1, employeeInfo.get("nom"));
            preparedStatement.setString(2, employeeInfo.get("prenom"));
            preparedStatement.setString(3, employeeInfo.get("telephone"));
            preparedStatement.setString(4, employeeInfo.get("email"));
            preparedStatement.setString(5, currentStatut); // Utiliser le statut actuel
            preparedStatement.setInt(6, Integer.parseInt(employeeInfo.get("id"))); // Le record à mettre à jour par ID

            // Exécuter la mise à jour
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été mise à jour

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un employé de la base de données.
     *
     * @param employeeInfo Informations de l'employé à supprimer
     * @return true si l'employé a été supprimé avec succès, false sinon
     */
    public static boolean deleteEmploye(Map<String, String> employeeInfo) {
        String query = "DELETE FROM employes WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir l'ID de l'employé
            preparedStatement.setInt(1, Integer.parseInt(employeeInfo.get("id")));

            // Exécuter la suppression
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Retourne true si au moins une ligne a été supprimée

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère toutes les factures depuis la base de données.
     *
     * @return Liste de toutes les factures
     */
    public static List<Facture> fetchAllFacture() {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM Facture";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                String numeroFacture = rs.getString("num_facture");
                String nomPartenaire = rs.getString("nom_partenaire");
                LocalDate dateFacture = rs.getDate("date").toLocalDate();
                BigDecimal montantAvantTaxes = rs.getBigDecimal("montant_avant_taxes");
                BigDecimal tps = rs.getBigDecimal("tps");
                BigDecimal tvq = rs.getBigDecimal("tvq");
                BigDecimal montantApresTaxes = rs.getBigDecimal("montant_apres_taxes");
                String statut = rs.getString("statut");

                Facture facture = new Facture(numeroFacture, fetchPartner(nomPartenaire), dateFacture, montantAvantTaxes, tps, tvq, montantApresTaxes, statut);
                factures.add(facture);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return factures;
    }

    /**
     * Récupère un partenaire depuis la base de données par son nom.
     *
     * @param nomPartenaire Nom du partenaire à récupérer
     * @return Un objet Partenaire si trouvé, sinon null
     */
    public static Partenaire fetchPartner(String nomPartenaire) {
        Partenaire partner = null;

        String query = "SELECT * FROM partenaire WHERE nom = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query)) {

            psFetch.setString(1, nomPartenaire);

            try (ResultSet rs = psFetch.executeQuery()) {
                if (rs.next()) {
                    String numeroCivique = rs.getString("numero_civique");
                    String rue = rs.getString("rue");
                    String ville = rs.getString("ville");
                    String province = rs.getString("province");
                    String codePostal = rs.getString("code_postal");
                    String telephone = rs.getString("telephone");
                    String courriel = rs.getString("courriel");
                    String status = rs.getString("status");

                    partner = new Partenaire(nomPartenaire, numeroCivique, rue, ville, province, codePostal, telephone, courriel, status);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partner;
    }

    /**
     * Obtient le prochain numéro de facture en incrémentant le maximum existant.
     *
     * @return Le prochain numéro de facture
     */
    public static int obtenirProchainNumFacture() {
        int num = 0;
        String selectSql = "SELECT MAX(CAST(SUBSTRING_INDEX(num_facture, '-', 1) AS UNSIGNED)) AS maxNum FROM Facture";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement statement = connection.prepareStatement(selectSql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                num = rs.getInt("maxNum");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return num + 1;
    }

    /**
     * Crée une nouvelle facture dans la base de données.
     *
     * @param numFacture  Numéro de la facture
     * @param partner     Nom du partenaire associé à la facture
     * @param invoiceDate Date de la facture
     * @param status      Statut de la facture
     * @return true si la facture a été créée avec succès, false sinon
     */
    public static boolean createNewInvoice(String numFacture, String partner, LocalDate invoiceDate, String status) {
        String insertSql = "INSERT INTO Facture (num_facture, nom_partenaire, date, montant_avant_taxes, tps, tvq, montant_apres_taxes, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement statement = connection.prepareStatement(insertSql)) {

            // Définir les paramètres
            statement.setString(1, numFacture);
            statement.setString(2, partner);
            statement.setDate(3, java.sql.Date.valueOf(invoiceDate));

            // Définir les autres paramètres à null
            statement.setNull(4, java.sql.Types.DECIMAL);
            statement.setNull(5, java.sql.Types.DECIMAL);
            statement.setNull(6, java.sql.Types.DECIMAL);
            statement.setNull(7, java.sql.Types.DECIMAL);
            statement.setString(8, status);

            // Exécuter la mise à jour
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute un nouveau quart dans la base de données.
     *
     * @param numFacture   Numéro de facture associé au quart
     * @param prestation   Prestation effectuée pendant le quart
     * @param dateQuart    Date du quart
     * @param debutQuart   Heure de début du quart
     * @param finQuart     Heure de fin du quart
     * @param pause        Durée de la pause pendant le quart
     * @param tempsTotal   Temps total travaillé pendant le quart
     * @param tauxHoraire  Taux horaire
     * @param montantTotal Montant total pour le quart
     * @param notes        Notes associées au quart
     * @param empName      Nom de l'employé ayant travaillé le quart
     * @param tempsDouble  Indique si le quart est payé en double
     * @param tempsDemi    Indique si le quart est payé en temps et demi
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
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

    /**
     * Récupère tous les quarts associés à un numéro de facture spécifique.
     *
     * @param numFacture Numéro de facture
     * @return Liste des quarts associés à la facture
     */
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
                String tempsTotal = rs.getString("temps_total");
                String prestation = rs.getString("prestation");
                BigDecimal tauxHoraire = rs.getBigDecimal("taux_horaire");
                BigDecimal montantTotal = rs.getBigDecimal("montant_total");
                String notes = rs.getString("notes");
                String nomEmp = rs.getString("emp_name");
                boolean tempsDemi = rs.getInt("tempsDemi") == 1;
                boolean tempsDouble = rs.getInt("tempsDouble") == 1;

                Quart quart = new Quart(id, numFactureResult, dateQuart, debutQuart, finQuart, pause,
                        tempsTotal, prestation, tauxHoraire, montantTotal, notes, nomEmp, tempsDouble, tempsDemi);
                quarts.add(quart);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quarts;
    }

    /**
     * Met à jour une facture dans la base de données avec les informations fournies.
     *
     * @param facture Objet Facture contenant les informations mises à jour
     */
    public static void mettreAJourFacture(Facture facture) {
        String updateQuery = "UPDATE Facture SET montant_avant_taxes = ?, tps = ?, tvq = ?, montant_apres_taxes = ? WHERE num_facture = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setBigDecimal(1, facture.getMontantAvantTaxes());
            pstmt.setBigDecimal(2, facture.getTps());
            pstmt.setBigDecimal(3, facture.getTvq());
            pstmt.setBigDecimal(4, facture.getMontantApresTaxes());
            pstmt.setString(5, facture.getNumFacture());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un quart de la base de données par son identifiant.
     *
     * @param id Identifiant du quart à supprimer
     * @return true si le quart a été supprimé avec succès, false sinon
     */
    public static boolean supprimerQuart(int id) {
        String sql = "DELETE FROM Quart WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error while deleting quart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour un quart dans la base de données avec les informations fournies.
     *
     * @param id            Identifiant du quart à mettre à jour
     * @param prestation    Prestation mise à jour effectuée pendant le quart
     * @param dateQuart     Date mise à jour du quart
     * @param debutQuart    Heure de début mise à jour du quart
     * @param finQuart      Heure de fin mise à jour du quart
     * @param pause         Durée de la pause mise à jour pendant le quart
     * @param tempsTotal    Temps total travaillé mis à jour pendant le quart
     * @param tauxHoraire   Taux horaire mis à jour
     * @param montantTotal  Montant total mis à jour pour le quart
     * @param notes         Notes mises à jour associées au quart
     * @param empName       Nom mis à jour de l'employé ayant travaillé le quart
     * @param tempsDouble   Indique si le quart est payé en double
     * @param tempsDemi     Indique si le quart est payé en temps et demi
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
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
            pstmt.setInt(12, id);

            pstmt.executeUpdate();
        }
    }

    /**
     * Récupère tous les quarts depuis la base de données.
     *
     * @return Liste de tous les quarts
     */
    public static List<Quart> fetchAllQuarts() {
        List<Quart> quarts = new ArrayList<>();
        String query = "SELECT * FROM Quart";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String numFacture = rs.getString("num_facture");

                Date dateQuart = rs.getDate("date_quart");
                Time debutQuart = rs.getTime("debut_quart");
                Time finQuart = rs.getTime("fin_quart");
                Time pause = rs.getTime("pause");

                String tempsTotal = rs.getString("temps_total");
                String prestation = rs.getString("prestation");
                BigDecimal tauxHoraire = rs.getBigDecimal("taux_horaire");
                BigDecimal montantTotal = rs.getBigDecimal("montant_total");
                String notes = rs.getString("notes");
                String nomEmploye = rs.getString("emp_name");
                boolean tempsDemi = rs.getInt("tempsDemi") == 1;
                boolean tempsDouble = rs.getInt("tempsDouble") == 1;

                if (dateQuart == null) {
                    dateQuart = Date.valueOf(LocalDate.now());
                }
                if (debutQuart == null) {
                    debutQuart = Time.valueOf(LocalTime.of(0, 0));
                }
                if (finQuart == null) {
                    finQuart = Time.valueOf(LocalTime.of(0, 0));
                }
                if (pause == null) {
                    pause = Time.valueOf(LocalTime.of(0, 0));
                }

                Quart quart = new Quart(
                        id,
                        numFacture,
                        dateQuart,
                        debutQuart,
                        finQuart,
                        pause,
                        tempsTotal,
                        prestation,
                        tauxHoraire,
                        montantTotal,
                        notes,
                        nomEmploye,
                        tempsDouble,
                        tempsDemi
                );

                quarts.add(quart);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quarts;
    }

    /**
     * Récupère une facture depuis la base de données par son numéro de facture.
     *
     * @param numFacture Numéro de la facture
     * @return Un objet Facture si trouvé, sinon null
     */
    public static Facture fetchFactureByNumFacture(String numFacture) {
        Facture facture = null;
        String query = "SELECT * FROM Facture WHERE num_facture = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query)) {

            psFetch.setString(1, numFacture);

            ResultSet rs = psFetch.executeQuery();

            if (rs.next()) {
                String num = rs.getString("num_facture");
                String nomPartenaire = rs.getString("nom_partenaire");
                LocalDate date = rs.getDate("date").toLocalDate();
                BigDecimal montantAvantTaxes = rs.getBigDecimal("montant_avant_taxes");
                BigDecimal tps = rs.getBigDecimal("tps");
                BigDecimal tvq = rs.getBigDecimal("tvq");
                BigDecimal montantApresTaxes = rs.getBigDecimal("montant_apres_taxes");
                String statut = rs.getString("statut");

                Partenaire partenaire = fetchPartnerByName(nomPartenaire);

                facture = new Facture(num, partenaire, date, montantAvantTaxes, tps, tvq, montantApresTaxes, statut);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return facture;
    }

    /**
     * Récupère un partenaire depuis la base de données par son nom.
     *
     * @param nomPartenaire Nom du partenaire à récupérer
     * @return Un objet Partenaire si trouvé, sinon null
     */
    public static Partenaire fetchPartnerByName(String nomPartenaire) {
        Partenaire partner = null;
        String query = "SELECT * FROM partenaire WHERE nom = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psFetch = connection.prepareStatement(query)) {

            psFetch.setString(1, nomPartenaire);

            ResultSet rs = psFetch.executeQuery();

            if (rs.next()) {
                String numeroCivique = rs.getString("numero_civique");
                String rue = rs.getString("rue");
                String ville = rs.getString("ville");
                String province = rs.getString("province");
                String codePostal = rs.getString("code_postal");
                String telephone = rs.getString("telephone");
                String courriel = rs.getString("courriel");
                String status = rs.getString("status");

                partner = new Partenaire(nomPartenaire, numeroCivique, rue, ville, province, codePostal, telephone, courriel, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partner;
    }

    /**
     * Met à jour le statut d'une facture dans la base de données.
     *
     * @param numFacture Numéro de la facture à mettre à jour
     * @param statut Nouveau statut de la facture
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateStatus(String numFacture, String statut) {
        String updateQuery = "UPDATE Facture SET statut = ? WHERE num_facture = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {

            psUpdate.setString(1, statut);  // Nouveau statut
            psUpdate.setString(2, numFacture);  // Numéro de facture à mettre à jour

            int rowsAffected = psUpdate.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère le nombre total de factures ayant un statut spécifique.
     *
     * @param statut Le statut des factures à compter (par exemple, "payée", "en attente", etc.).
     * @return Le nombre de factures avec le statut donné.
     */
    public static int ObtenirNombreDeFactureParStatut(String statut) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Facture WHERE statut = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Calcule le montant total des factures en attente de paiement (statut "Envoyée").
     *
     * @return Le montant total des factures avec le statut "Envoyée".
     */
    public static BigDecimal getMontantPaiementEnAttente() {
        BigDecimal totalMontant = BigDecimal.ZERO;
        String query = "SELECT SUM(montant_apres_taxes) AS total FROM Facture WHERE statut = 'Envoyée'";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalMontant = rs.getBigDecimal("total");
                if (totalMontant == null) {
                    totalMontant = BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalMontant;
    }

}
