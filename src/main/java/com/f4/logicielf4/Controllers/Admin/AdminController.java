package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur principal pour la vue d'administration.
 * Ce contrôleur gère la mise à jour de la zone centrale du BorderPane
 * en fonction de l'option sélectionnée par l'utilisateur dans le menu d'administration.
 */
public class AdminController implements Initializable {

    /**
     * Le BorderPane contenant la zone centrale qui sera mise à jour
     * en fonction de l'option sélectionnée par l'utilisateur.
     */
    public BorderPane admin_parent;

    /**
     * Méthode appelée automatiquement après le chargement du fichier FXML.
     * Elle initialise le contrôleur et ajoute les écouteurs nécessaires pour
     * détecter les changements d'option sélectionnée par l'utilisateur.
     *
     * @param url L'URL de la ressource FXML utilisée pour résoudre les chemins relatifs.
     * @param resourceBundle Le ResourceBundle contenant les ressources localisées.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ajouterListeners();
    }

    /**
     * Ajoute un écouteur pour surveiller les changements de sélection des options dans le modèle.
     * La vue centrale du BorderPane sera mise à jour en fonction de l'option choisie parmi :
     * "Factures", "Partenaires", "Employes", "Profil", ou par défaut, le tableau de bord d'administration.
     * L'écouteur permet de gérer dynamiquement la mise à jour des vues sans recharger l'ensemble de l'interface.
     */
    private void ajouterListeners() {
        // Obtenir une instance du modèle et de la fabrique de vues associée
        Model model = Model.getInstance();
        var viewFactory = model.getViewFactory();

        // Ajouter un écouteur sur la sélection d'options
        viewFactory.getOptionSelectionnee().addListener((ChangeListener<String>) (observableValue, oldVal, newVal) -> {
            if (newVal == null) {
                // Si aucune option n'est sélectionnée, affiche le tableau de bord par défaut
                admin_parent.setCenter(viewFactory.getAdminDashboardView());
                return;
            }

            // Mise à jour de la vue centrale selon l'option sélectionnée
            switch (newVal) {
                case "Factures" -> admin_parent.setCenter(viewFactory.getFacturesView());
                case "Partenaires" -> admin_parent.setCenter(viewFactory.getPartenairesView());
                case "Employes" -> admin_parent.setCenter(viewFactory.getEmployesView());
                case "Profil" -> admin_parent.setCenter(viewFactory.getProfilView());
                default -> admin_parent.setCenter(viewFactory.getAdminDashboardView());
            }
        });
    }
}
