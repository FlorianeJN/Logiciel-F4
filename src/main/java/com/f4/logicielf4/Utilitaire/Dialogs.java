package com.f4.logicielf4.Utilitaire;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Classe utilitaire pour afficher des dialogues et des alertes dans l'application.
 */
public class Dialogs {

    /**
     * Affiche une boîte de dialogue d'information avec un message et un titre.
     *
     * @param message le message à afficher dans la boîte de dialogue
     * @param titre   le titre de la boîte de dialogue
     */
    public static void showMessageDialog(String message, String titre) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Affiche une boîte de dialogue de confirmation avec un message et un titre.
     * Retourne vrai si l'utilisateur clique sur "Oui", sinon retourne faux.
     *
     * @param message le message à afficher dans la boîte de dialogue
     * @param titre   le titre de la boîte de dialogue
     * @return vrai si l'utilisateur confirme, sinon faux
     */
    public static boolean showConfirmDialog(String message, String titre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Ajouter des boutons à la boîte de dialogue
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == btnOui;
    }
}
