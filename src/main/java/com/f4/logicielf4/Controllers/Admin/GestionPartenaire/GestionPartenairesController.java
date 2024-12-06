package com.f4.logicielf4.Controllers.Admin.GestionPartenaire;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

/**
 * Contrôleur pour l'écran de gestion des partenaires dans l'interface d'administration.
 * Il permet d'ajouter, mettre à jour et supprimer des partenaires, ainsi que d'afficher
 * des graphiques représentant la répartition des quarts et des revenus par partenaire.
 */
public class GestionPartenairesController implements Initializable {

    @FXML
    private Label activePartnersLabel, inactivePartnersLabel;

    @FXML
    private Label totalPartnersLabel;

    @FXML
    private TableView<Partenaire> partnersTable;

    @FXML
    private TableColumn<Partenaire, String> nameColumn, addressColumn, phoneColumn, emailColumn, statusColumn;

    @FXML
    private TextField nameField, adressField, phoneField, emailField;

    @FXML
    private Button btnAjouter, btnMAJ, btnSupprimer;

    @FXML
    private VBox quartsGraphBox, revenuGraphBox;

    private static final Logger LOGGER = Logger.getLogger(GestionPartenairesController.class.getName());

    /**
     * Initialise le contrôleur. Définit les actions des boutons, les valeurs des cellules,
     * et met à jour le tableau des partenaires et les étiquettes affichant les statistiques.
     *
     * @param url L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine.
     * @param resourceBundle Les ressources utilisées pour localiser l'objet racine.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setOnAction(event -> actionBtnAjouter());
        btnMAJ.setOnAction(event -> actionBtnMAJ());
        btnSupprimer.setOnAction(event -> actionBtnSupprimer());
        setCellValues();
        updateTable();
        updateLabels();
        setupQuartsPieChart();
        setupRevenuBarChart();  // Affiche les graphiques
    }

    /**
     * Configure et affiche un graphique en secteurs (PieChart) montrant la répartition des quarts par partenaire.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void setupQuartsPieChart() {
        Task<PieChart> task = new Task<PieChart>() {
            @Override
            protected PieChart call() throws Exception {
                PieChart pieChart = new PieChart();
                List<Quart> quarts = DBUtils.fetchAllQuarts();
                Map<String, Integer> partnerShiftCount = new HashMap<>();

                // Fetch all factures in a single call to avoid N+1 problem
                Map<String, Facture> factureMap = DBUtils.fetchAllFacturesAsMap();

                for (Quart quart : quarts) {
                    Facture facture = factureMap.get(quart.getNumFacture());
                    if (facture != null) {
                        String partnerName = facture.getNomPartenaire();
                        partnerShiftCount.put(partnerName, partnerShiftCount.getOrDefault(partnerName, 0) + 1);
                    }
                }

                for (Map.Entry<String, Integer> entry : partnerShiftCount.entrySet()) {
                    String label = entry.getKey() + " (" + entry.getValue() + ")";
                    PieChart.Data slice = new PieChart.Data(label, entry.getValue());
                    pieChart.getData().add(slice);
                }

                return pieChart;
            }
        };

        task.setOnSucceeded(event -> {
            PieChart pieChart = task.getValue();
            Label pieChartTitle = new Label("Répartition des quarts par partenaire");
            pieChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");

            for (PieChart.Data data : pieChart.getData()) {
                Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
                Tooltip.install(data.getNode(), tooltip);
            }

            quartsGraphBox.getChildren().clear();
            quartsGraphBox.getChildren().addAll(pieChartTitle, pieChart);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la création du graphique des quarts.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du graphique des quarts", ex);
        });

        new Thread(task).start();
    }

    /**
     * Configure et affiche un graphique à barres (BarChart) montrant la répartition des revenus par partenaire.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void setupRevenuBarChart() {
        Task<BarChart<String, Number>> task = new Task<BarChart<String, Number>>() {
            @Override
            protected BarChart<String, Number> call() throws Exception {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Revenus ($)");

                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

                List<Facture> factures = DBUtils.fetchAllFacture();
                Map<String, BigDecimal> partnerTotalAmount = new HashMap<>();

                for (Facture facture : factures) {
                    String partnerName = facture.getNomPartenaire();
                    BigDecimal montantApresTaxes = facture.getMontantApresTaxes();
                    partnerTotalAmount.put(partnerName, partnerTotalAmount.getOrDefault(partnerName, BigDecimal.ZERO).add(montantApresTaxes));
                }

                for (Map.Entry<String, BigDecimal> entry : partnerTotalAmount.entrySet()) {
                    dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                barChart.getData().add(dataSeries);

                return barChart;
            }
        };

        task.setOnSucceeded(event -> {
            BarChart<String, Number> barChart = task.getValue();
            Label barChartTitle = new Label("Répartition des revenus par partenaire");
            barChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#004d40;");

            revenuGraphBox.getChildren().clear();
            revenuGraphBox.getChildren().addAll(barChartTitle, barChart);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la création du graphique des revenus.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du graphique des revenus", ex);
        });

        new Thread(task).start();
    }

    /**
     * Met à jour les étiquettes affichant les statistiques des partenaires (actifs, inactifs, total).
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void updateLabels() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Partenaire> liste = DBUtils.fetchAllPartners();
                int partenairesActifs;
                int partenairesTotal = liste.size();

                partenairesActifs = (int) liste.stream().filter(p -> "actif".equalsIgnoreCase(p.getStatus())).count();

                int partenairesInactifs = partenairesTotal - partenairesActifs;

                Platform.runLater(() -> {
                    activePartnersLabel.setText(String.valueOf(partenairesActifs));
                    inactivePartnersLabel.setText(String.valueOf(partenairesInactifs));
                    totalPartnersLabel.setText(String.valueOf(partenairesTotal));
                });

                return null;
            }
        };

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des statistiques des partenaires.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques des partenaires", ex);
        });

        new Thread(task).start();
    }

    /**
     * Configure les valeurs des colonnes de la TableView pour les partenaires.
     * Définit un comparateur pour la colonne de statut afin de trier par statut (actif en premier).
     */
    private void setCellValues() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("courriel"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusColumn.setComparator((status1, status2) -> {
            if ("actif".equalsIgnoreCase(status1) && "inactif".equalsIgnoreCase(status2)) {
                return -1;
            } else if ("inactif".equalsIgnoreCase(status1) && "actif".equalsIgnoreCase(status2)) {
                return 1;
            } else {
                return 0;
            }
        });
        partnersTable.getSortOrder().add(statusColumn); // Tri par statut
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Ajouter".
     * Ouvre la fenêtre d'ajout d'un partenaire et met à jour le tableau et les étiquettes après l'ajout.
     */
    private void actionBtnAjouter() {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddPartnerWindow(stage);
        updateTable();
        updateLabels();
        setupQuartsPieChart();
        setupRevenuBarChart();
    }

    /**
     * Met à jour la TableView avec la liste des partenaires actuelle.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private void updateTable() {
        Task<ObservableList<Partenaire>> task = new Task<ObservableList<Partenaire>>() {
            @Override
            protected ObservableList<Partenaire> call() throws Exception {
                List<Partenaire> partenaires = DBUtils.fetchAllPartners();
                return FXCollections.observableArrayList(partenaires);
            }
        };

        task.setOnSucceeded(event -> {
            partnersTable.getItems().setAll(task.getValue());
            partnersTable.sort();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des partenaires.", "ERREUR");
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des partenaires", ex);
        });

        new Thread(task).start();
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Mise à jour".
     * Ouvre la fenêtre de mise à jour pour le partenaire sélectionné.
     * Affiche un message d'erreur si aucun partenaire n'est sélectionné.
     */
    private void actionBtnMAJ() {
        Partenaire partenaireSelectionne = partnersTable.getSelectionModel().getSelectedItem();

        if (partenaireSelectionne == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de mise à jour.", "ERREUR MAJ");
        } else {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showUpdatePartnerWindow(stage, partenaireSelectionne);
            updateTable();
            updateLabels();
            setupQuartsPieChart();
            setupRevenuBarChart();
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Supprimer".
     * Ouvre la fenêtre de suppression pour le partenaire sélectionné.
     * Affiche un message d'erreur si aucun partenaire n'est sélectionné.
     */
    private void actionBtnSupprimer() {
        Partenaire partenaireSelectionne = partnersTable.getSelectionModel().getSelectedItem();

        if (partenaireSelectionne == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de suppression.", "ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Model.getInstance().getViewFactory().showDeletePartnerWindow(stage, partenaireSelectionne);
            updateTable();
            updateLabels();
            setupQuartsPieChart();
            setupRevenuBarChart();
        }
    }
}
