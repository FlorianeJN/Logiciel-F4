package com.f4.logicielf4.Controllers.Admin;

import com.f4.logicielf4.Models.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur principal pour l'administration.
 * Ce contrôleur gère les changements de vue dans la zone centrale du BorderPane
 * en fonction de l'option sélectionnée par l'utilisateur.
 */
public class AdminController implements Initializable {

    /**
     * La zone centrale du BorderPane dans la vue d'administration.
     */
    public BorderPane admin_parent;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     *
     * @param url L'URL de la ressource FXML (non utilisé dans ce contexte).
     * @param resourceBundle Le ResourceBundle associé à la ressource FXML (non utilisé dans ce contexte).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ajouterListeners();
    }

    /**
     * Ajoute un écouteur pour les changements de sélection des options dans le modèle.
     * Met à jour la vue centrale du BorderPane en fonction de l'option sélectionnée.
     * Les options disponibles sont : "Factures", "Partenaires", "Employes", "Profil",
     * et par défaut la vue du tableau de bord d'administration est utilisée.
     */
    private void ajouterListeners() {
        // Obtenez une instance du modèle et de la fabrique de vues
        Model model = Model.getInstance();
        var viewFactory = model.getViewFactory();

        // Ajoutez un écouteur pour les changements de sélection
        viewFactory.getOptionSelectionnee().addListener((ChangeListener<String>) (observableValue, oldVal, newVal) -> {
            if (newVal == null) {
                // Si newVal est null, utilisez la vue par défaut
                admin_parent.setCenter(viewFactory.getAdminDashboardView());
                return;
            }

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
