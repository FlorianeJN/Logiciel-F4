package com.f4.logicielf4.Controllers.Admin.GestionPartenaire;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Models.Quart;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
 * Contrôleur pour l'écran de gestion des partenaires dans l'interface d'administration.
 * Il permet d'ajouter, mettre à jour et supprimer des partenaires, ainsi que d'afficher
 * des graphiques représentant la répartition des quarts et des revenus par partenaire.
 */
public class GestionPartenairesController implements Initializable {

    @FXML
    private Label activePartnersLabel;

    @FXML
    private Label inactivePartnersLabel;

    @FXML
    private Label totalPartnersLabel;

    @FXML
    private TableView<Partenaire> partnersTable;

    @FXML
    private TableColumn<Partenaire, String> nameColumn;

    @FXML
    private TableColumn<Partenaire, String> addressColumn;

    @FXML
    private TableColumn<Partenaire, String> phoneColumn;

    @FXML
    private TableColumn<Partenaire, String> emailColumn;

    @FXML
    private TableColumn<Partenaire, String> statusColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnMAJ;

    @FXML
    private Button btnSupprimer;

    @FXML
    private VBox quartsGraphBox;

    @FXML
    private VBox revenuGraphBox;

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
     */
    private void setupQuartsPieChart() {
        PieChart pieChart = new PieChart();
        Label pieChartTitle = new Label("Répartition des quarts par partenaire");
        pieChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");

        List<Quart> quarts = DBUtils.fetchAllQuarts();
        Map<String, Integer> partnerShiftCount = new HashMap<>();

        for (Quart quart : quarts) {
            Facture facture = DBUtils.fetchFactureByNumFacture(quart.getNumFacture());
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

        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
            Tooltip.install(data.getNode(), tooltip);
        }

        quartsGraphBox.getChildren().clear();
        quartsGraphBox.getChildren().addAll(pieChartTitle, pieChart);
    }

    /**
     * Configure et affiche un graphique à barres (BarChart) montrant la répartition des revenus par partenaire.
     */
    private void setupRevenuBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenus ($)");

        Label barChartTitle = new Label("Répartition des revenus par partenaire");
        barChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#004d40;");

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

        revenuGraphBox.getChildren().clear();
        revenuGraphBox.getChildren().addAll(barChartTitle, barChart);
    }

    /**
     * Met à jour les étiquettes affichant les statistiques des partenaires (actifs, inactifs, total).
     */
    private void updateLabels() {
        int partenairesActifs = 0;
        int partenairesInactifs = 0;
        int partenairesTotal = 0;
        List<Partenaire> liste = DBUtils.fetchAllPartners();

        for (Partenaire p : liste) {
            if (p.getStatus().equals("actif"))
                partenairesActifs++;
        }

        partenairesTotal = liste.size();
        partenairesInactifs = partenairesTotal - partenairesActifs;

        activePartnersLabel.setText(String.valueOf(partenairesActifs));
        inactivePartnersLabel.setText(String.valueOf(partenairesInactifs));
        totalPartnersLabel.setText(String.valueOf(partenairesTotal));
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
            if (status1.equals("actif") && status2.equals("inactif")) {
                return -1;
            } else if (status1.equals("inactif") && status2.equals("actif")) {
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
        System.out.println("Bouton ajouter appuyé");
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddPartnerWindow(stage);
        updateTable();
        updateLabels();
    }

    /**
     * Met à jour la TableView avec la liste des partenaires actuelle.
     */
    private void updateTable() {
        try {
            List<Partenaire> partenaires = DBUtils.fetchAllPartners();
            partnersTable.getItems().setAll(partenaires);
            partnersTable.sort();
        } catch (Exception e) {
            Dialogs.showMessageDialog("Erreur lors de la mise à jour des partenaires : " + e.getMessage(), "ERREUR");
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Mise à jour".
     * Ouvre la fenêtre de mise à jour pour le partenaire sélectionné.
     * Affiche un message d'erreur si aucun partenaire n'est sélectionné.
     */
    private void actionBtnMAJ() {
        System.out.println("Bouton MAJ appuyé");
        Partenaire partenaireSelectionné = partnersTable.getSelectionModel().getSelectedItem();

        if (partenaireSelectionné == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de mise à jour.", "ERREUR MAJ");
        } else {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showUpdatePartnerWindow(stage, partenaireSelectionné);
            updateTable();
        }
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Supprimer".
     * Ouvre la fenêtre de suppression pour le partenaire sélectionné.
     * Affiche un message d'erreur si aucun partenaire n'est sélectionné.
     */
    private void actionBtnSupprimer() {
        System.out.println("Bouton Supprimer appuyé");
        Partenaire partenaireSelectionné = partnersTable.getSelectionModel().getSelectedItem();

        if (partenaireSelectionné == null) {
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de suppression.", "ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Model.getInstance().getViewFactory().showDeletePartnerWindow(stage, partenaireSelectionné);
            updateTable();
            updateLabels();
        }
    }
}
