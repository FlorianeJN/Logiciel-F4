package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the "Gestion Employés" screen for managing employees.
 * It allows adding, updating, and deleting employees.
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
        setCellValues();
        updateTable();
        updateLabels();
        setupDepartmentChart();
        setupPositionChart();
    }

    /*RANDOM DATA GRAPHS JUST TO SEE WHAT IT WOULD LOOK LIKE */
    private void setupDepartmentChart() {
        // Create the department pie chart
        PieChart departmentChart = new PieChart();

        // Sample data for departments
        PieChart.Data slice1 = new PieChart.Data("Sales", 30);
        PieChart.Data slice2 = new PieChart.Data("HR", 25);
        PieChart.Data slice3 = new PieChart.Data("IT", 20);
        PieChart.Data slice4 = new PieChart.Data("Finance", 15);
        PieChart.Data slice5 = new PieChart.Data("Marketing", 10);

        // Add slices to the pie chart
        departmentChart.getData().addAll(slice1, slice2, slice3, slice4, slice5);

        // Add the pie chart to the departmentGraphBox
        departmentGraphBox.getChildren().add(departmentChart);
    }

    /*RANDOM DATA GRAPHS JUST TO SEE WHAT IT WOULD LOOK LIKE */
    private void setupPositionChart() {
        // Create the position bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> positionChart = new BarChart<>(xAxis, yAxis);

        // Configure the axes
        xAxis.setLabel("Position");
        yAxis.setLabel("Count");

        // Create the data series for positions
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Positions");

        // Sample data for positions
        series.getData().add(new XYChart.Data<>("Manager", 10));
        series.getData().add(new XYChart.Data<>("Developer", 20));
        series.getData().add(new XYChart.Data<>("Analyst", 15));
        series.getData().add(new XYChart.Data<>("Intern", 5));

        // Add the series to the bar chart
        positionChart.getData().add(series);

        // Add the bar chart to the positionGraphBox
        positionGraphBox.getChildren().add(positionChart);
    }


    private void updateLabels() {
        int employeesActifs = 0;
        int employeesInactifs = 0;
        int employeesTotal = 0;
        List<Employe> liste = DBUtils.fetchAllEmployees(); // Assuming you have a method to fetch all employees

        for (Employe e : liste) {
            if (e.getStatut().equals("Actif")) employeesActifs++;
        }
        employeesTotal = liste.size();
        employeesInactifs = employeesTotal - employeesActifs;

        activeEmployeesLabel.setText(String.valueOf(employeesActifs));
        inactiveEmployeesLabel.setText(String.valueOf(employeesInactifs));
        totalEmployeesLabel.setText(String.valueOf(employeesTotal));
    }

    private void setCellValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Optionally, add sorting by status
        statusColumn.setComparator((status1, status2) -> {
            if (status1.equals("Actif") && status2.equals("Inactif")) {
                return -1;
            } else if (status1.equals("Inactif") && status2.equals("Actif")) {
                return 1;
            } else {
                return 0;
            }
        });
        employeesTable.getSortOrder().add(statusColumn); // Ensure sorting by status
    }

    private void actionBtnAjouter() {
        System.out.println("Bouton ajouter appuyé");
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddEmployeeWindow(stage);
        updateTable();
        updateLabels();
    }

    private void updateTable() {
        try {
            List<Employe> employees = DBUtils.fetchAllEmployees();
            employeesTable.getItems().setAll(employees);
            employeesTable.sort();
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des employés : " + e.getMessage(), "ERREUR");
        }
    }

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
