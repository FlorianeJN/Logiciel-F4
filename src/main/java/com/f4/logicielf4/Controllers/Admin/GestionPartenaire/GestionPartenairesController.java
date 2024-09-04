package com.f4.logicielf4.Controllers.Admin.GestionPartenaire;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
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
        setupQuartsPieChart();      //MÉTHODE À MODIFIER À LA FIN
        setupRevenuBarChart();      //MÉTHODE À MODIFIER À LA FIN
    }

    private void setupQuartsPieChart() {
        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Employeur 1", 45));
        pieChart.getData().add(new PieChart.Data("Employeur 2", 30));
        pieChart.getData().add(new PieChart.Data("Employeur 3", 25));
        quartsGraphBox.getChildren().add(pieChart);
    }

    private void setupRevenuBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Revenu 2024");
        dataSeries.getData().add(new XYChart.Data<>("Employeur 1", 25000));
        dataSeries.getData().add(new XYChart.Data<>("Employeur 2", 15000));
        dataSeries.getData().add(new XYChart.Data<>("Employeur 3", 10000));

        barChart.getData().add(dataSeries);
        revenuGraphBox.getChildren().add(barChart);
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