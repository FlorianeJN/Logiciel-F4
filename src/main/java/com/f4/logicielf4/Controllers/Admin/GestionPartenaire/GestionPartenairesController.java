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
 * Contrôleur pour l'écran "Gestion Partenaires" de l'admin, qui permet de gérer les partenaires.
 * Il permet l'ajout, la mise à jour et la suppression de partenaires.
 */
public class GestionPartenairesController implements Initializable {

    @FXML
    private Label activePartnersLabel;

    @FXML
    private Label inactivePartnersLabel;

    @FXML
    private Label totalPartnersLabel;

    @FXML
    private TableView partnersTable;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn addressColumn;

    @FXML
    private TableColumn phoneColumn;

    @FXML
    private TableColumn emailColumn;

    @FXML
    private TableColumn statusColumn;

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
     * Initialise le contrôleur. Définit les actions des boutons, les valeurs des cellules et met à jour le tableau et les étiquettes.
     *
     * @param url L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine, ou null si non connu.
     * @param resourceBundle Les ressources utilisées pour localiser l'objet racine, ou null si non disponible.
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
        setupRevenuBarChart();      //MÉTHODE À MODIFIER À LA FIN
    }

    private void setupQuartsPieChart() {
        // Create a new PieChart
        PieChart pieChart = new PieChart();

        // Create a custom title for the PieChart
        Label pieChartTitle = new Label("Répartition des quarts par partenaire");
        pieChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #004d40;");

        // Fetch all shifts (quarts) from the database
        List<Quart> quarts = DBUtils.fetchAllQuarts();

        // Create a map to store the count of shifts for each partner
        Map<String, Integer> partnerShiftCount = new HashMap<>();

        // Iterate through the list of quarts
        for (Quart quart : quarts) {
            // Retrieve the facture associated with the current quart using num_facture
            Facture facture = DBUtils.fetchFactureByNumFacture(quart.getNumFacture());

            // Check if the facture exists and get the partner's name
            if (facture != null) {
                String partnerName = facture.getNomPartenaire();

                // Increment the count of shifts for this partner
                partnerShiftCount.put(partnerName, partnerShiftCount.getOrDefault(partnerName, 0) + 1);
            }
        }

        // Populate the PieChart with the shift counts
        for (Map.Entry<String, Integer> entry : partnerShiftCount.entrySet()) {
            // Display the number of shifts next to the partner's name
            String label = entry.getKey() + " (" + entry.getValue() + ")";
            PieChart.Data slice = new PieChart.Data(label, entry.getValue());
            pieChart.getData().add(slice);
        }

        // Optionally, set up tooltips for additional details
        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " quarts");
            Tooltip.install(data.getNode(), tooltip);
        }

        // Add the title and the PieChart to the VBox in the FXML layout
        quartsGraphBox.getChildren().clear(); // Clear previous content
        quartsGraphBox.getChildren().addAll(pieChartTitle, pieChart); // Add title and chart
    }
    private void setupRevenuBarChart() {
        // Create axes for the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenus ($)");

        // Create a custom title for the BarChart
        Label barChartTitle = new Label("Répartition des revenus par partenaire");
        barChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#004d40;");

        // Create the BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Create a data series for the bar chart
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        // Fetch all factures from the database
        List<Facture> factures = DBUtils.fetchAllFacture();

        // Create a map to store the total amount for each partner
        Map<String, BigDecimal> partnerTotalAmount = new HashMap<>();

        // Iterate through the list of factures and sum amounts per partner
        for (Facture facture : factures) {
            String partnerName = facture.getNomPartenaire();
            BigDecimal montantApresTaxes = facture.getMontantApresTaxes();

            // Add the montant_apres_taxes to the current total for the partner
            partnerTotalAmount.put(partnerName, partnerTotalAmount.getOrDefault(partnerName, BigDecimal.ZERO).add(montantApresTaxes));
        }

        // Populate the data series with the total amounts
        for (Map.Entry<String, BigDecimal> entry : partnerTotalAmount.entrySet()) {
            dataSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the data series to the bar chart
        barChart.getData().add(dataSeries);

        // Add the title and the BarChart to the VBox in the FXML layout
        revenuGraphBox.getChildren().clear(); // Clear previous content
        revenuGraphBox.getChildren().addAll(barChartTitle, barChart); // Add title and chart
    }

    /**
     * Met à jour les étiquettes des statistiques des partenaires (actifs, inactifs, total) en fonction de la liste actuelle des partenaires.
     */
    private void updateLabels(){
        int partenairesActifs = 0;
        int partenairesInactifs = 0;
        int partenairesTotal = 0;
        List<Partenaire> liste = DBUtils.fetchAllPartners();

        // Compter les partenaires actifs et inactifs
        for (Partenaire p : liste) {
            if(p.getStatus().equals("actif"))
                partenairesActifs++;
        }
        partenairesTotal = liste.size();
        partenairesInactifs = partenairesTotal - partenairesActifs;

        // Définir les étiquettes
        activePartnersLabel.setText(String.valueOf(partenairesActifs));
        inactivePartnersLabel.setText(String.valueOf(partenairesInactifs));
        totalPartnersLabel.setText(String.valueOf(partenairesTotal));
    }

    /**
     * Définit les valeurs pour les colonnes de la TableView, y compris un comparateur pour la colonne de statut
     * afin que les partenaires "actifs" soient affichés avant les partenaires "inactifs".
     */
    private void setCellValues() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("courriel"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Trier la colonne de statut : actif en premier, inactif en dessous
        statusColumn.setComparator((status1, status2) -> {
            if (status1.equals("actif") && status2.equals("inactif")) {
                return -1;
            } else if (status1.equals("inactif") && status2.equals("actif")) {
                return 1;
            } else {
                return 0;
            }
        });
        partnersTable.getSortOrder().add(statusColumn); // S'assurer du tri par statut
    }

    /**
     * Action pour le bouton "Ajouter". Ouvre la fenêtre d'ajout de partenaire et met à jour le tableau et les étiquettes.
     */
    private void actionBtnAjouter(){
        System.out.println("Bouton ajouter appuyé");
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        Model.getInstance().getViewFactory().showAddPartnerWindow(stage);
        updateTable();
        updateLabels();
    }

    /**
     * Met à jour la TableView avec la liste actuelle des partenaires et applique le tri.
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
     * Action pour le bouton "Mise à jour". Ouvre la fenêtre de mise à jour pour le partenaire sélectionné.
     * Si aucun partenaire n'est sélectionné, un message d'erreur s'affiche.
     */
    private void actionBtnMAJ(){
        System.out.println("Bouton MAJ appuyé");
        Partenaire partenaireSelectionné = (Partenaire) partnersTable.getSelectionModel().getSelectedItem();

        if(partenaireSelectionné == null){
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de mise à jour.","ERREUR MAJ");
        } else {
            Stage stage = (Stage) btnMAJ.getScene().getWindow();
            Model.getInstance().getViewFactory().showUpdatePartnerWindow(stage, partenaireSelectionné);
            updateTable();
        }
    }

    /**
     * Action pour le bouton "Supprimer". Ouvre la fenêtre de suppression pour le partenaire sélectionné.
     * Si aucun partenaire n'est sélectionné, un message d'erreur s'affiche.
     */
    private void actionBtnSupprimer(){
        System.out.println("Bouton Supprimer appuyé");
        Partenaire partenaireSelectionné = (Partenaire) partnersTable.getSelectionModel().getSelectedItem();

        if(partenaireSelectionné == null){
            Dialogs.showMessageDialog("Veuillez sélectionner un partenaire avant de cliquer le bouton de suppression.","ERREUR SUPPRESSION");
        } else {
            Stage stage = (Stage) btnSupprimer.getScene().getWindow();
            Model.getInstance().getViewFactory().showDeletePartnerWindow(stage, partenaireSelectionné);
            updateTable();
            updateLabels();
        }
    }
}