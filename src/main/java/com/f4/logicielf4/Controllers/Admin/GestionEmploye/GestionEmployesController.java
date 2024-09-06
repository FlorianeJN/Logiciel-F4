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
        setupDepartmentChart();
        setupPositionChart();
    }

    /**
     * Configure le graphique circulaire représentant la répartition des employés par département.
     * Données fictives utilisées pour la démonstration.
     */
    private void setupDepartmentChart() {
        // Créer le graphique circulaire pour les départements
        PieChart departmentChart = new PieChart();

        // Données fictives pour les départements
        PieChart.Data slice1 = new PieChart.Data("Ventes", 30);
        PieChart.Data slice2 = new PieChart.Data("RH", 25);
        PieChart.Data slice3 = new PieChart.Data("IT", 20);
        PieChart.Data slice4 = new PieChart.Data("Finance", 15);
        PieChart.Data slice5 = new PieChart.Data("Marketing", 10);

        // Ajouter les données au graphique
        departmentChart.getData().addAll(slice1, slice2, slice3, slice4, slice5);

        // Ajouter le graphique à la boîte correspondante
        departmentGraphBox.getChildren().add(departmentChart);
    }

    /**
     * Configure le graphique à barres représentant la répartition des employés par poste.
     * Données fictives utilisées pour la démonstration.
     */
    private void setupPositionChart() {
        // Créer le graphique à barres pour les postes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> positionChart = new BarChart<>(xAxis, yAxis);

        // Configurer les axes
        xAxis.setLabel("Poste");
        yAxis.setLabel("Nombre");

        // Créer la série de données pour les postes
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Postes");

        // Données fictives pour les postes
        series.getData().add(new XYChart.Data<>("Manager", 10));
        series.getData().add(new XYChart.Data<>("Développeur", 20));
        series.getData().add(new XYChart.Data<>("Analyste", 15));
        series.getData().add(new XYChart.Data<>("Stagiaire", 5));

        positionChart.getData().add(series);

        positionGraphBox.getChildren().add(positionChart);
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
