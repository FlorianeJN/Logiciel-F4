package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour effectuer des opérations sur la base de données relatives aux utilisateurs, partenaires, factures et quarts.
 */
public class DBUtils {

    private static final String url = System.getenv("DB_URL");
    private static final String user = System.getenv("DB_USER");
    private static final String pass = System.getenv("DB_PASS");

    private static final Logger LOGGER = Logger.getLogger(DBUtils.class.getName());

    private static DataSource dataSource;

    static {
        try {
            // Initialisation du pool de connexions
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);

            // Paramètres optionnels
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation du pool de connexions", e);
        }
    }

    /**
     * Vérifie les informations d'identification d'un utilisateur pour se connecter.
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return true si les informations d'identification sont correctes, false sinon
     */
    public static boolean loginUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement psLogin = connection.prepareStatement(query)) {

            psLogin.setString(1, username);
            try (ResultSet resultSet = psLogin.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    if (retrievedPassword != null && retrievedPassword.equals(password)) {
                        // Connexion réussie
                        return true;
                    } else {
                        // Mot de passe incorrect
                        Dialogs.showMessageDialog("Mot de passe incorrect!", "ERREUR MOT DE PASSE - F4 SANTÉ INC");
                    }
                } else {
                    // L'utilisateur n'existe pas
                    Dialogs.showMessageDialog("L'utilisateur n'existe pas. Contactez l'admin pour le rajouter!", "ERREUR UTILISATEUR - F4 SANTÉ INC");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion de l'utilisateur", e);
        }
        return false;
    }

    /**
     * Récupère tous les utilisateurs depuis la base de données.
     *
     * @return Liste de tous les utilisateurs
     */
    public static List<Partenaire> fetchAllPartners() {
        List<Partenaire> partners = new ArrayList<>();
        String query = "SELECT * FROM partenaire";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Partenaire partner = extractPartnerFromResultSet(rs);
                partners.add(partner);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des partenaires", e);
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Partenaire partner = extractPartnerFromResultSet(rs);
                partners.add(partner);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des partenaires actifs", e);
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setPartnerParameters(preparedStatement, partnerInfo);
            preparedStatement.setString(9, "actif");

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du partenaire", e);
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setPartnerParameters(preparedStatement, partnerInfo);
            preparedStatement.setString(8, "actif");
            preparedStatement.setString(9, partnerInfo.get("nom"));

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du partenaire", e);
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

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement psDeleteFacture = connection.prepareStatement(deleteFactureQuery)) {
                psDeleteFacture.setString(1, partnerInfo.get("nom"));
                psDeleteFacture.executeUpdate();
            }

            try (PreparedStatement psDeletePartner = connection.prepareStatement(deletePartnerQuery)) {
                psDeletePartner.setString(1, partnerInfo.get("nom"));
                int rowsAffected = psDeletePartner.executeUpdate();

                connection.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du partenaire", e);
            return false;
        }
    }

    /**
     * Récupère tous les employés depuis la base de données.
     *
     * @return Liste de tous les employés
     */
    public static List<Employe> fetchAllEmployees() {  // Kept original method name
        List<Employe> employes = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Employe employe = extractEmployeFromResultSet(rs);
                employes.add(employe);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des employés", e);
        }
        return employes;
    }


    /**
     * Ajoute un nouvel employé dans la base de données avec un mot de passe par défaut.
     *
     * @param employeeInfo Informations de l'employé à ajouter (username, nom, prenom, telephone, email, statut).
     * @return true si l'employé a été ajouté avec succès, false sinon
     */
    public static boolean addEmployee(Map<String, String> employeeInfo) {
        String defaultPassword = "Password@123"; // Default password

        String query = "INSERT INTO users (username, nom, prenom, telephone, email, statut, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employeeInfo.get("username"));
            preparedStatement.setString(2, employeeInfo.get("nom"));
            preparedStatement.setString(3, employeeInfo.get("prenom"));
            preparedStatement.setString(4, employeeInfo.get("telephone"));
            preparedStatement.setString(5, employeeInfo.get("email"));
            preparedStatement.setString(6, employeeInfo.get("statut"));
            preparedStatement.setString(7, defaultPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de l'employé", e);
            return false;
        }
    }

    /**
     * Vérifie si un nom d'utilisateur existe déjà dans la base de données.
     *
     * @param username Le nom d'utilisateur à vérifier.
     * @return true si le nom d'utilisateur existe, false sinon.
     */
    public static boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Définir le paramètre de requête
            preparedStatement.setString(1, username);

            // Exécuter la requête
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    // Si le compte est supérieur à 0, le nom d'utilisateur existe
                    return count > 0;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'existence du nom d'utilisateur", e);
        }

        // Par défaut, retourner false si une exception se produit
        return false;
    }

    /**
     * Met à jour les informations d'un employé dans la base de données.
     *
     * @param employeeInfo Informations de l'employé à mettre à jour (username, nom, prenom, telephone, email, statut).
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateEmploye(Map<String, String> employeeInfo) {
        String query = "UPDATE users SET nom = ?, prenom = ?, telephone = ?, email = ?, statut = ? WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employeeInfo.get("nom"));
            preparedStatement.setString(2, employeeInfo.get("prenom"));
            preparedStatement.setString(3, employeeInfo.get("telephone"));
            preparedStatement.setString(4, employeeInfo.get("email"));
            preparedStatement.setString(5, employeeInfo.get("statut"));
            preparedStatement.setString(6, employeeInfo.get("username"));

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'employé", e);
            return false;
        }
    }

    /**
     * Supprime un employé de la base de données en utilisant le nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'employé à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public static boolean deleteEmploye(String username) {
        String query = "DELETE FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Employé supprimé avec succès : " + username);
                return true;
            } else {
                LOGGER.warning("Aucun employé trouvé avec le nom d'utilisateur : " + username);
                return false;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'employé : " + username, e);
            return false;
        }
    }

    /**
     * Récupère un employé à partir de la base de données en utilisant le nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'employé.
     * @return Un objet Employe contenant les informations de l'employé, ou null si non trouvé.
     */
    public static Employe getEmployeByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // Supposons que la table 'users' contient les colonnes : username, nom, prenom, telephone, email, statut, password, etc.
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String telephone = rs.getString("telephone");
                    String email = rs.getString("email");
                    String statut = rs.getString("statut");
                    String password = rs.getString("password"); // Récupération du mot de passe

                    return new Employe(username, nom, prenom, telephone, email, statut, password);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'employé : " + username, e);
        }
        return null;
    }

    /**
     * Récupère toutes les factures depuis la base de données.
     *
     * @return Liste de toutes les factures
     */
    public static List<Facture> fetchAllFacture() {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT f.*, p.* FROM Facture f JOIN partenaire p ON f.nom_partenaire = p.nom";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Facture facture = extractFactureFromResultSet(rs);
                factures.add(facture);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des factures", e);
        }
        return factures;
    }

    /**
     * Obtient le prochain numéro de facture en incrémentant le maximum existant.
     *
     * @return Le prochain numéro de facture
     */
    public static int obtenirProchainNumFacture() {
        int num = 0;
        String selectSql = "SELECT MAX(CAST(SUBSTRING_INDEX(num_facture, '-', 1) AS UNSIGNED)) AS maxNum FROM Facture";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                num = rs.getInt("maxNum");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'obtention du prochain numéro de facture", e);
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
                + "VALUES (?, ?, ?, NULL, NULL, NULL, NULL, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {

            statement.setString(1, numFacture);
            statement.setString(2, partner);
            statement.setDate(3, java.sql.Date.valueOf(invoiceDate));
            statement.setString(4, status);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la nouvelle facture", e);
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setQuartInsertParameters(pstmt, numFacture, dateQuart, debutQuart, finQuart, pause,
                    tempsTotal, prestation, tauxHoraire, montantTotal, notes, empName, tempsDemi, tempsDouble);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du quart", e);
            throw e;
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, numFacture);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Quart quart = extractQuartFromResultSet(rs);
                    quarts.add(quart);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des quarts par numéro de facture", e);
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setBigDecimal(1, facture.getMontantAvantTaxes());
            pstmt.setBigDecimal(2, facture.getTps());
            pstmt.setBigDecimal(3, facture.getTvq());
            pstmt.setBigDecimal(4, facture.getMontantApresTaxes());
            pstmt.setString(5, facture.getNumFacture());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la facture", e);
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du quart", e);
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
        String sql = "UPDATE Quart SET prestation = ?, date_quart = ?, debut_quart = ?, fin_quart = ?, pause = ?, temps_total = ?, " +
                "taux_horaire = ?, montant_total = ?, notes = ?, emp_name = ?, tempsDemi = ?, tempsDouble = ? " +
                "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setQuartUpdateParameters(pstmt, prestation, dateQuart, debutQuart, finQuart, pause,
                    tempsTotal, tauxHoraire, montantTotal, notes, empName, tempsDemi, tempsDouble);
            pstmt.setInt(13, id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du quart", e);
            throw e;
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Quart quart = extractQuartFromResultSet(rs);
                quarts.add(quart);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les quarts", e);
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
        String query = "SELECT f.*, p.* FROM Facture f JOIN partenaire p ON f.nom_partenaire = p.nom WHERE f.num_facture = ?";
        Facture facture = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query)) {

            psFetch.setString(1, numFacture);
            try (ResultSet rs = psFetch.executeQuery()) {
                if (rs.next()) {
                    facture = extractFactureFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la facture par numéro", e);
        }
        return facture;
    }

    /**
     * Met à jour le statut d'une facture dans la base de données.
     *
     * @param numFacture Numéro de la facture à mettre à jour
     * @param statut     Nouveau statut de la facture
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateStatus(String numFacture, String statut) {
        String updateQuery = "UPDATE Facture SET statut = ? WHERE num_facture = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {

            psUpdate.setString(1, statut);
            psUpdate.setString(2, numFacture);

            int rowsAffected = psUpdate.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du statut de la facture", e);
            return false;
        }
    }

    /**
     * Récupère le nombre total de factures ayant un statut spécifique.
     *
     * @param statut Le statut des factures à compter.
     * @return Le nombre de factures avec le statut donné.
     */
    public static int ObtenirNombreDeFactureParStatut(String statut) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Facture WHERE statut = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'obtention du nombre de factures par statut", e);
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalMontant = rs.getBigDecimal("total");
                if (totalMontant == null) {
                    totalMontant = BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du montant des paiements en attente", e);
        }
        return totalMontant;
    }

    // Helper methods to reduce code duplication

    /**
     * Extrait un objet Employe depuis un ResultSet.
     *
     * @param rs ResultSet contenant les données de l'employé
     * @return Un objet Employe
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static Employe extractEmployeFromResultSet(ResultSet rs) throws SQLException {  // Kept original method name
        String username = rs.getString("username");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String telephone = rs.getString("telephone");
        String email = rs.getString("email");
        String statut = rs.getString("statut");
        Timestamp createdAt = rs.getTimestamp("created_at");
        String password = rs.getString("password");
        return new Employe(username,nom,prenom,telephone,email,statut,password);
    }

    /**
     * Extrait un objet Partenaire depuis un ResultSet.
     *
     * @param rs ResultSet contenant les données du partenaire
     * @return Un objet Partenaire
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static Partenaire extractPartnerFromResultSet(ResultSet rs) throws SQLException {
        String nom = rs.getString("nom");
        String numeroCivique = rs.getString("numero_civique");
        String rue = rs.getString("rue");
        String ville = rs.getString("ville");
        String province = rs.getString("province");
        String codePostal = rs.getString("code_postal");
        String telephone = rs.getString("telephone");
        String courriel = rs.getString("courriel");
        String status = rs.getString("status");

        return new Partenaire(nom, numeroCivique, rue, ville, province, codePostal, telephone, courriel, status);
    }

    /**
     * Extrait un objet Facture depuis un ResultSet.
     *
     * @param rs ResultSet contenant les données de la facture
     * @return Un objet Facture
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static Facture extractFactureFromResultSet(ResultSet rs) throws SQLException {
        String numFacture = rs.getString("num_facture");
        LocalDate dateFacture = rs.getDate("date").toLocalDate();
        BigDecimal montantAvantTaxes = rs.getBigDecimal("montant_avant_taxes");
        BigDecimal tps = rs.getBigDecimal("tps");
        BigDecimal tvq = rs.getBigDecimal("tvq");
        BigDecimal montantApresTaxes = rs.getBigDecimal("montant_apres_taxes");
        String statut = rs.getString("statut");

        Partenaire partenaire = extractPartnerFromResultSet(rs);

        return new Facture(numFacture, partenaire, dateFacture, montantAvantTaxes, tps, tvq, montantApresTaxes, statut);
    }

    /**
     * Extrait un objet Quart depuis un ResultSet.
     *
     * @param rs ResultSet contenant les données du quart
     * @return Un objet Quart
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static Quart extractQuartFromResultSet(ResultSet rs) throws SQLException {
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
        String nomEmp = rs.getString("emp_name");
        boolean tempsDemi = rs.getInt("tempsDemi") == 1;
        boolean tempsDouble = rs.getInt("tempsDouble") == 1;

        return new Quart(id, numFacture, dateQuart, debutQuart, finQuart, pause,
                tempsTotal, prestation, tauxHoraire, montantTotal, notes, nomEmp, tempsDouble, tempsDemi);
    }

    /**
     * Définit les paramètres pour une requête d'ajout ou de mise à jour d'un employé.
     *
     * @param ps          PreparedStatement à configurer
     * @param employeeInfo Map contenant les informations de l'employé
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static void setEmployeeParameters(PreparedStatement ps, Map<String, String> employeeInfo) throws SQLException {  // Kept original method name
        ps.setString(1, employeeInfo.get("username"));
        ps.setString(2, employeeInfo.get("nom"));
        ps.setString(3, employeeInfo.get("prenom"));
        ps.setString(4, employeeInfo.get("telephone"));
        ps.setString(5, employeeInfo.get("email"));
        ps.setString(6, employeeInfo.get("statut"));
        ps.setString(7, employeeInfo.get("password"));
    }

    /**
     * Définit les paramètres pour une requête d'ajout ou de mise à jour d'un partenaire.
     *
     * @param ps          PreparedStatement à configurer
     * @param partnerInfo Map contenant les informations du partenaire
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static void setPartnerParameters(PreparedStatement ps, Map<String, String> partnerInfo) throws SQLException {
        ps.setString(1, partnerInfo.get("numero_civique"));
        ps.setString(2, partnerInfo.get("rue"));
        ps.setString(3, partnerInfo.get("ville"));
        ps.setString(4, partnerInfo.get("province"));
        ps.setString(5, partnerInfo.get("code_postal"));
        ps.setString(6, partnerInfo.get("telephone"));
        ps.setString(7, partnerInfo.get("email"));
    }

    /**
     * Définit les paramètres pour une requête d'ajout d'un quart.
     *
     * @param ps            PreparedStatement à configurer
     * @param numFacture    Numéro de facture associé au quart
     * @param dateQuart     Date du quart
     * @param debutQuart    Heure de début du quart
     * @param finQuart      Heure de fin du quart
     * @param pause         Durée de la pause pendant le quart
     * @param tempsTotal    Temps total travaillé pendant le quart
     * @param prestation    Prestation effectuée pendant le quart
     * @param tauxHoraire   Taux horaire
     * @param montantTotal  Montant total pour le quart
     * @param notes         Notes associées au quart
     * @param empName       Nom de l'employé ayant travaillé le quart
     * @param tempsDemi     Indique si le quart est payé en temps et demi
     * @param tempsDouble   Indique si le quart est payé en double
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static void setQuartInsertParameters(PreparedStatement ps, String numFacture, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart,
                                                 LocalTime pause, String tempsTotal, String prestation, double tauxHoraire, double montantTotal, String notes,
                                                 String empName, boolean tempsDemi, boolean tempsDouble) throws SQLException {
        int index = 1;
        ps.setString(index++, numFacture);
        ps.setDate(index++, java.sql.Date.valueOf(dateQuart));
        ps.setTime(index++, java.sql.Time.valueOf(debutQuart));
        ps.setTime(index++, java.sql.Time.valueOf(finQuart));
        ps.setTime(index++, java.sql.Time.valueOf(pause));
        ps.setString(index++, tempsTotal);
        ps.setString(index++, prestation);
        ps.setDouble(index++, tauxHoraire);
        ps.setDouble(index++, montantTotal);
        ps.setString(index++, notes);
        ps.setString(index++, empName);
        ps.setInt(index++, tempsDemi ? 1 : 0);
        ps.setInt(index++, tempsDouble ? 1 : 0);
    }

    /**
     * Définit les paramètres pour une requête de mise à jour d'un quart.
     *
     * @param ps            PreparedStatement à configurer
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
     * @param tempsDemi     Indique si le quart est payé en temps et demi
     * @param tempsDouble   Indique si le quart est payé en double
     * @throws SQLException si une erreur d'accès à la base de données survient
     */
    private static void setQuartUpdateParameters(PreparedStatement ps, String prestation, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart,
                                                 LocalTime pause, String tempsTotal, double tauxHoraire, double montantTotal, String notes, String empName,
                                                 boolean tempsDemi, boolean tempsDouble) throws SQLException {
        int index = 1;
        ps.setString(index++, prestation);
        ps.setDate(index++, java.sql.Date.valueOf(dateQuart));
        ps.setTime(index++, java.sql.Time.valueOf(debutQuart));
        ps.setTime(index++, java.sql.Time.valueOf(finQuart));
        ps.setTime(index++, java.sql.Time.valueOf(pause));
        ps.setString(index++, tempsTotal);
        ps.setDouble(index++, tauxHoraire);
        ps.setDouble(index++, montantTotal);
        ps.setString(index++, notes);
        ps.setString(index++, empName);
        ps.setInt(index++, tempsDemi ? 1 : 0);
        ps.setInt(index++, tempsDouble ? 1 : 0);
    }

    /**
     * Récupère toutes les factures sous forme de Map pour éviter le problème N+1.
     *
     * @return Map des factures avec le numéro de facture comme clé
     */
    public static Map<String, Facture> fetchAllFacturesAsMap() {
        Map<String, Facture> factureMap = new HashMap<>();
        String query = "SELECT f.*, p.* FROM Facture f JOIN partenaire p ON f.nom_partenaire = p.nom";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement psFetch = connection.prepareStatement(query);
             ResultSet rs = psFetch.executeQuery()) {

            while (rs.next()) {
                Facture facture = extractFactureFromResultSet(rs);
                factureMap.put(facture.getNumFacture(), facture);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des factures en map", e);
        }
        return factureMap;
    }

    // Additional helper methods or updates can be added here as needed

}
