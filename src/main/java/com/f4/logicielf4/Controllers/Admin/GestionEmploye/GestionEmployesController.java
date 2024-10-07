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
 * Contrôleur pour l'écran de gestion des employés ("Gestion Employés").
 * Il permet d'ajouter, mettre à jour et supprimer des employés.
 *
 * Fonctionnalités principales :
 * - Ajout d'un nouvel employé
 * - Mise à jour d'un employé sélectionné
 * - Suppression d'un employé sélectionné
 * - Affichage de graphiques illustrant la répartition des employés par département et poste
 * - Mise à jour des étiquettes concernant les employés actifs/inactifs et le nombre total d'employés
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
     */
    private void setupGraphiqueRepartitionQuarts() {
        // Create a new PieChart
        PieChart pieChart = new PieChart();

        // Create a title label for the chart and align it to the left
        Label titleLabelQuarts = new Label("Répartition des quarts par employé");
        titleLabelQuarts.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        titleLabelQuarts.setAlignment(Pos.BASELINE_LEFT);

        // Fetch all shifts (quarts) from the database
        List<Quart> quarts = DBUtils.fetchAllQuarts();

        // Create a map to store the count of shifts for each employee
        Map<String, Integer> employeeShiftCount = new HashMap<>();

        // Iterate through the list of quarts
        for (Quart quart : quarts) {
            String employeeName = quart.getNomEmploye();

            // Increment the count of shifts for this employee
            employeeShiftCount.put(employeeName, employeeShiftCount.getOrDefault(employeeName, 0) + 1);
        }

        // Populate the PieChart with the shift counts
        for (Map.Entry<String, Integer> entry : employeeShiftCount.entrySet()) {
            // Display the number of shifts next to the employee's name
            String label = entry.getKey() + " (" + entry.getValue() + ")";
            PieChart.Data slice = new PieChart.Data(label, entry.getValue());
            pieChart.getData().add(slice);
        }

        // Optionally, set up tooltips for additional details
        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
            Tooltip.install(data.getNode(), tooltip);
        }

        // Optionally, you can set the size of the pie chart here
        pieChart.setPrefSize(600, 400);

        // Clear any previous content and add the title and the chart to the VBox
        departmentGraphBox.getChildren().clear();
        departmentGraphBox.getChildren().addAll(titleLabelQuarts, pieChart);
    }
    /**
     * Configure le graphique à barres représentant la répartition des revenus parmi les employés.
     */
    private void setupGraphiqueRepartitionRevenus() {
        // Create axes for the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Montant Total ($)"); // Label for y-axis indicating the total amount

        // Create the BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Create a title label for the chart and align it to the left
        Label titleLabelRevenus = new Label("Répartition des revenus par employé");
        titleLabelRevenus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
        titleLabelRevenus.setAlignment(Pos.BASELINE_LEFT);

        // Create a data series for the bar chart
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Montant total");

        // Fetch all shifts (quarts) from the database
        List<Quart> quarts = DBUtils.fetchAllQuarts();

        // Create a map to store the total revenue for each employee
        Map<String, BigDecimal> employeeTotalAmount = new HashMap<>();

        // Iterate through the list of quarts and sum amounts per employee
        for (Quart quart : quarts) {
            String employeeName = quart.getNomEmploye();
            BigDecimal montantTotal = BigDecimal.valueOf(quart.getMontantTotal());

            // Add the total amount to the current total for the employee
            employeeTotalAmount.put(employeeName, employeeTotalAmount.getOrDefault(employeeName, BigDecimal.ZERO).add(montantTotal));
        }

        // Populate the data series with the total amounts
        for (Map.Entry<String, BigDecimal> entry : employeeTotalAmount.entrySet()) {
            dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the data series to the bar chart
        barChart.getData().add(dataSeries);

        // Optionally, you can set the size of the bar chart here
        barChart.setPrefSize(800, 600);

        // Clear any previous content and add the title and the chart to the VBox
        positionGraphBox.getChildren().clear();
        positionGraphBox.getChildren().addAll(titleLabelRevenus, barChart);
    }

    /**
     * Met à jour les étiquettes affichant le nombre d'employés actifs, inactifs et total.
     */
    private void updateLabels() {
        int employeesActifs = 0;
        int employeesInactifs = 0;
        int employeesTotal = 0;
        List<Employe> liste = DBUtils.fetchAllEmployees(); // Supposons qu'il existe une méthode pour récupérer tous les employés

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
     * Définit les colonnes de la table avec les valeurs correspondantes des objets Employé.
     */
    private void setCellValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

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
     * Action effectuée lors de l'appui sur le bouton "Ajouter".
     * Ouvre la fenêtre d'ajout d'un nouvel employé.
     */
    private void actionBtnAjouter() {
        System.out.println("Bouton ajouter appuyé");
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddEmployeeWindow(stage);
        updateTable();
        updateLabels();
    }

    /**
     * Met à jour la table des employés avec les données actuelles.
     * Affiche un message d'erreur en cas de problème de mise à jour.
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
     * Action effectuée lors de l'appui sur le bouton "Mise à Jour".
     * Ouvre la fenêtre de mise à jour de l'employé sélectionné.
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
     * Action effectuée lors de l'appui sur le bouton "Supprimer".
     * Ouvre la fenêtre de suppression de l'employé sélectionné.
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
