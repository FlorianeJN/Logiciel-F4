package com.f4.logicielf4.Controllers.Admin.GestionEmploye;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des employés dans l'interface d'administration.
 * Il permet d'ajouter, mettre à jour et supprimer des employés, ainsi que de visualiser des graphiques
 * concernant la répartition des quarts de travail et des revenus parmi les employés.
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

    private static final Logger LOGGER = Logger.getLogger(GestionEmployesController.class.getName());

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
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void setupGraphiqueRepartitionQuarts() {
        Task<PieChart> task = new Task<PieChart>() {
            @Override
            protected PieChart call() throws Exception {
                List<Quart> quarts = DBUtils.fetchAllQuarts();

                Map<String, Long> employeeShiftCount = quarts.stream()
                        .filter(quart -> quart.getNomEmploye() != null && !quart.getNomEmploye().trim().isEmpty())
                        .collect(Collectors.groupingBy(Quart::getNomEmploye, Collectors.counting()));

                PieChart pieChart = new PieChart();
                for (Map.Entry<String, Long> entry : employeeShiftCount.entrySet()) {
                    String label = entry.getKey() + " (" + entry.getValue() + ")";
                    PieChart.Data slice = new PieChart.Data(label, entry.getValue());
                    pieChart.getData().add(slice);
                }

                return pieChart;
            }
        };

        task.setOnSucceeded(event -> {
            PieChart pieChart = task.getValue();
            Label titleLabelQuarts = new Label("Répartition des quarts par employé");
            titleLabelQuarts.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
            titleLabelQuarts.setAlignment(Pos.BASELINE_LEFT);

            for (PieChart.Data data : pieChart.getData()) {
                Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
                Tooltip.install(data.getNode(), tooltip);
            }

            pieChart.setPrefSize(600, 400);

            departmentGraphBox.getChildren().clear();
            departmentGraphBox.getChildren().addAll(titleLabelQuarts, pieChart);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la création du graphique des quarts.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du graphique des quarts", ex);
        });

        new Thread(task).start();
    }

    /**
     * Configure le graphique à barres représentant la répartition des revenus parmi les employés.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void setupGraphiqueRepartitionRevenus() {
        Task<BarChart<String, Number>> task = new Task<BarChart<String, Number>>() {
            @Override
            protected BarChart<String, Number> call() throws Exception {
                List<Quart> quarts = DBUtils.fetchAllQuarts();

                Map<String, BigDecimal> employeeTotalAmount = quarts.stream()
                        .filter(quart -> quart.getNomEmploye() != null && !quart.getNomEmploye().trim().isEmpty())
                        .collect(Collectors.groupingBy(Quart::getNomEmploye,
                                Collectors.mapping(
                                        quart -> BigDecimal.valueOf(quart.getMontantTotal()),
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                                )
                        ));

                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Revenus ($)");

                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
                dataSeries.setName("Montant total");

                for (Map.Entry<String, BigDecimal> entry : employeeTotalAmount.entrySet()) {
                    dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                barChart.getData().add(dataSeries);
                barChart.setPrefSize(800, 600);

                return barChart;
            }
        };

        task.setOnSucceeded(event -> {
            BarChart<String, Number> barChart = task.getValue();
            Label titleLabelRevenus = new Label("Répartition des revenus par employé");
            titleLabelRevenus.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");
            titleLabelRevenus.setAlignment(Pos.BASELINE_LEFT);

            positionGraphBox.getChildren().clear();
            positionGraphBox.getChildren().addAll(titleLabelRevenus, barChart);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la création du graphique des revenus.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du graphique des revenus", ex);
        });

        new Thread(task).start();
    }

    /**
     * Met à jour les étiquettes affichant le nombre d'employés actifs, inactifs et total.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void updateLabels() {
        Task<Void> task = new Task<Void>() {
            private int employeesActifs;
            private int employeesInactifs;
            private int employeesTotal;

            @Override
            protected Void call() throws Exception {
                List<Employe> liste = DBUtils.fetchAllEmployees();

                employeesActifs = (int) liste.stream()
                        .filter(e -> "Actif".equalsIgnoreCase(e.getStatut()))
                        .count();

                employeesTotal = liste.size();
                employeesInactifs = employeesTotal - employeesActifs;

                return null;
            }

            @Override
            protected void succeeded() {
                activeEmployeesLabel.setText(String.valueOf(employeesActifs));
                inactiveEmployeesLabel.setText(String.valueOf(employeesInactifs));
                totalEmployeesLabel.setText(String.valueOf(employeesTotal));
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                Dialogs.showMessageDialog("Erreur lors de la mise à jour des statistiques des employés.", "ERREUR");
                LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques des employés", ex);
            }
        };

        new Thread(task).start();
    }

    /**
     * Définit les valeurs des colonnes du tableau en fonction des propriétés des objets Employe.
     * Utilise des PropertyValueFactory pour mapper les colonnes aux attributs correspondants des employés.
     */
    private void setCellValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Comparateur pour trier par statut (Actif/Inactif)
        statusColumn.setComparator((status1, status2) -> {
            if ("Actif".equalsIgnoreCase(status1) && "Inactif".equalsIgnoreCase(status2)) {
                return -1;
            } else if ("Inactif".equalsIgnoreCase(status1) && "Actif".equalsIgnoreCase(status2)) {
                return 1;
            } else {
                return status1.compareToIgnoreCase(status2);
            }
        });
        employeesTable.getSortOrder().add(statusColumn); // Tri par statut
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Ajouter".
     * Ouvre une nouvelle fenêtre pour ajouter un employé.
     */
    private void actionBtnAjouter() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddEmployeeWindow(stage);
        updateTable();
        updateLabels();
        setupGraphiqueRepartitionQuarts();
        setupGraphiqueRepartitionRevenus();
    }

    /**
     * Met à jour la table des employés avec les données actuelles depuis la base de données.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void updateTable() {
        Task<ObservableList<Employe>> task = new Task<ObservableList<Employe>>() {
            @Override
            protected ObservableList<Employe> call() throws Exception {
                List<Employe> employees = DBUtils.fetchAllEmployees();
                return FXCollections.observableArrayList(employees);
            }
        };

        task.setOnSucceeded(event -> {
            employeesTable.getItems().setAll(task.getValue());
            employeesTable.sort();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des employés.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des employés", ex);
        });

        new Thread(task).start();
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Mise à Jour".
     * Ouvre une nouvelle fenêtre pour mettre à jour les informations de l'employé sélectionné.
     * Affiche un message d'erreur si aucun employé n'est sélectionné.
     */
    private void actionBtnMAJ() {
        Employe employeSelectionne = employeesTable.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un employé avant de cliquer le bouton de mise à jour.", "ERREUR MAJ");
        } else {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showUpdateEmployeeWindow(stage, employeSelectionne);
            updateTable();
            updateLabels();
            setupGraphiqueRepartitionQuarts();
            setupGraphiqueRepartitionRevenus();
        }
    }

    /**
     * Action déclenchée lorsque l'utilisateur clique sur le bouton "Supprimer".
     * Ouvre une nouvelle fenêtre pour supprimer l'employé sélectionné.
     * Affiche un message d'erreur si aucun employé n'est sélectionné.
     */
    private void actionBtnSupprimer() {
        Employe employeSelectionne = employeesTable.getSelectionModel().getSelectedItem();

        if (employeSelectionne == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un employé avant de cliquer le bouton de suppression.", "ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Model.getInstance().getViewFactory().showDeleteEmployeeWindow(stage, employeSelectionne);
            updateTable();
            updateLabels();
            setupGraphiqueRepartitionQuarts();
            setupGraphiqueRepartitionRevenus();
        }
    }
}
