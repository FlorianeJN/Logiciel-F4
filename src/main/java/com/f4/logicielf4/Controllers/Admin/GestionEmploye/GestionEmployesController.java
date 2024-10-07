package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion des employés dans l'interface d'administration.
 * Il permet d'ajouter, mettre à jour et supprimer des employés, ainsi que de visualiser des graphiques
 * concernant la répartition des quarts de travail et des revenus parmi les employés.
 *
 * Fonctionnalités principales :
 * - Ajouter un nouvel employé
 * - Mettre à jour les informations d'un employé sélectionné
 * - Supprimer un employé sélectionné
 * - Afficher des graphiques sur la répartition des quarts et des revenus par employé
 * - Mettre à jour les étiquettes concernant le nombre d'employés actifs, inactifs et total
 */
public class GestionEmployesController implements Initializable {

    @FXML
    private Label activeEmployeesLabel;

    @FXML
    private Label inactiveEmployeesLabel;

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private TableView<Employe> employeesTable;

    @FXML
    private TableColumn<Employe, Integer> idColumn;

    @FXML
    private TableColumn<Employe, String> nomColumn;

    @FXML
    private TableColumn<Employe, String> prenomColumn;

    @FXML
    private TableColumn<Employe, String> phoneColumn;

    @FXML
    private TableColumn<Employe, String> emailColumn;

    @FXML
    private TableColumn<Employe, String> statusColumn;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnMAJ;

    @FXML
    private Button btnSupprimer;

    @FXML
    private VBox departmentGraphBox;

    @FXML
    private VBox positionGraphBox;

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Configure les actions des boutons et initialise les tableaux et graphiques.
     *
     * @param url L'URL utilisée pour initialiser l'objet.
     * @param resourceBundle Les ressources utilisées pour internationaliser l'interface.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
        setCellValues();
        updateTable();
        updateLabels();
        setupGraphiqueRepartitionQuarts();
        setupGraphiqueRepartitionRevenus();
    }

    /**
     * Configure le graphique circulaire représentant la répartition des quarts parmi les employés.
     * Les quarts sont extraits de la base de données et sont regroupés par employé.
     */
    private void setupGraphiqueRepartitionQuarts() {
        PieChart pieChart = new PieChart();

        Label titleLabelQuarts = new Label("Répartition des quarts par employé");
        titleLabelQuarts.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        titleLabelQuarts.setAlignment(Pos.BASELINE_LEFT);

        List<Quart> quarts = DBUtils.fetchAllQuarts();
        Map<String, Integer> employeeShiftCount = new HashMap<>();

        for (Quart quart : quarts) {
            String employeeName = quart.getNomEmploye();

            // Vérifie si le nom de l'employé est nul ou vide
            if (employeeName == null || employeeName.trim().isEmpty()) {
                continue; // Ignorer les employés sans nom
            }

            employeeShiftCount.put(employeeName, employeeShiftCount.getOrDefault(employeeName, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : employeeShiftCount.entrySet()) {
            String label = entry.getKey() + " (" + entry.getValue() + ")";
            PieChart.Data slice = new PieChart.Data(label, entry.getValue());
            pieChart.getData().add(slice);
        }

        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
            Tooltip.install(data.getNode(), tooltip);
        }

        pieChart.setPrefSize(600, 400);

        departmentGraphBox.getChildren().clear();
        departmentGraphBox.getChildren().addAll(titleLabelQuarts, pieChart);
    }

    /**
     * Configure le graphique à barres représentant la répartition des revenus parmi les employés.
     * Les montants totaux des quarts par employé sont extraits et affichés sur le graphique.
     */
    private void setupGraphiqueRepartitionRevenus() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenus ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        Label titleLabelRevenus = new Label("Répartition des revenus par employé");
        titleLabelRevenus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        titleLabelRevenus.setAlignment(Pos.BASELINE_LEFT);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Montant total");

        List<Quart> quarts = DBUtils.fetchAllQuarts();
        Map<String, BigDecimal> employeeTotalAmount = new HashMap<>();

        for (Quart quart : quarts) {
            String employeeName = quart.getNomEmploye();

            // Vérifie si le nom de l'employé est nul ou vide
            if (employeeName == null || employeeName.trim().isEmpty()) {
                continue; // Ignorer les employés sans nom
            }

            BigDecimal montantTotal = BigDecimal.valueOf(quart.getMontantTotal());
            employeeTotalAmount.put(employeeName, employeeTotalAmount.getOrDefault(employeeName, BigDecimal.ZERO).add(montantTotal));
        }

        for (Map.Entry<String, BigDecimal> entry : employeeTotalAmount.entrySet()) {
            dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(dataSeries);
        barChart.setPrefSize(800, 600);

        positionGraphBox.getChildren().clear();
        positionGraphBox.getChildren().addAll(titleLabelRevenus, barChart);
    }

    /**
     * Met à jour les étiquettes affichant le nombre d'employés actifs, inactifs et total.
     * Ces valeurs sont extraites de la base de données.
     */
    private void updateLabels() {
        int employeesActifs = 0;
        int employeesInactifs = 0;
        int employeesTotal = 0;
        List<Employe> liste = DBUtils.fetchAllEmployees(); // Récupérer tous les employés

        for (Employe e : liste) {
            if (e.getStatut().equals("Actif")) employeesActifs++;
        }
        employeesTotal = liste.size();
        employeesInactifs = employeesTotal - employeesActifs;

        activeEmployeesLabel.setText(String.valueOf(employeesActifs));
        inactiveEmployeesLabel.setText(String.valueOf(employeesInactifs));
        totalEmployeesLabel.setText(String.valueOf(employeesTotal));
    }

    /**
     * Définit les valeurs des colonnes du tableau en fonction des propriétés des objets Employe.
     * Utilise des PropertyValueFactory pour mapper les colonnes aux attributs correspondants des employés.
     */
    private void setCellValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Comparateur pour trier par statut (Actif/Inactif)
        statusColumn.setComparator((status1, status2) -> {
            if (status1.equals("Actif") && status2.equals("Inactif")) {
                return -1;
            } else if (status1.equals("Inactif") && status2.equals("Actif")) {
                return 1;
            } else {
                return 0;
            }
        });
        employeesTable.getSortOrder().add(statusColumn); // Tri par statut
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Ajouter".
     * Ouvre une nouvelle fenêtre pour ajouter un employé.
     */
    private void actionBtnAjouter() {
        System.out.println("Bouton ajouter appuyé");
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddEmployeeWindow(stage);
        updateTable();
        updateLabels();
    }

    /**
     * Met à jour la table des employés avec les données actuelles depuis la base de données.
     * Affiche un message d'erreur en cas de problème lors de la mise à jour.
     */
    private void updateTable() {
        try {
            List<Employe> employees = DBUtils.fetchAllEmployees();
            employeesTable.getItems().setAll(employees);
            employeesTable.sort();
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des employés : " + e.getMessage(), "ERREUR");
        }
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Mise à Jour".
     * Ouvre une nouvelle fenêtre pour mettre à jour les informations de l'employé sélectionné.
     * Affiche un message d'erreur si aucun employé n'est sélectionné.
     */
    private void actionBtnMAJ() {
        System.out.println("Bouton MAJ appuyé");
        Employe employeSelectionné = employeesTable.getSelectionModel().getSelectedItem();

        if (employeSelectionné == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un employé avant de cliquer le bouton de mise à jour.", "ERREUR MAJ");
        } else {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showUpdateEmployeeWindow(stage, employeSelectionné);
            updateTable();
        }
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Supprimer".
     * Ouvre une nouvelle fenêtre pour supprimer l'employé sélectionné.
     * Affiche un message d'erreur si aucun employé n'est sélectionné.
     */
    private void actionBtnSupprimer() {
        System.out.println("Bouton Supprimer appuyé");
        Employe employeSelectionné = employeesTable.getSelectionModel().getSelectedItem();

        if (employeSelectionné == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un employé avant de cliquer le bouton de suppression.", "ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Model.getInstance().getViewFactory().showDeleteEmployeeWindow(stage, employeSelectionné);
            updateTable();
            updateLabels();
        }
    }
}
