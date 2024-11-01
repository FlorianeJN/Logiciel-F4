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
     * Affiche une alerte de type spécifié avec un message et un titre.
     *
     * @param alertType Le type d'alerte (INFORMATION, CONFIRMATION, etc.).
     * @param titre     Le titre de l'alerte.
     * @param message   Le message de l'alerte.
     * @return Optional<ButtonType> avec la réponse de l'utilisateur.
     */
    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String titre, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'information avec un message et un titre.
     *
     * @param message Le message à afficher.
     * @param titre   Le titre de la boîte de dialogue.
     */
    public static void showMessageDialog(String message, String titre) {
        Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, titre, message));
    }

    /**
     * Affiche une boîte de dialogue de confirmation avec un message et un titre.
     * Retourne vrai si l'utilisateur clique sur "Oui", sinon retourne faux.
     *
     * @param message Le message de confirmation à afficher.
     * @param titre   Le titre de la boîte de dialogue.
     * @return Vrai si l'utilisateur confirme, sinon faux.
     */
    public static boolean showConfirmDialog(String message, String titre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        alert.getButtonTypes().setAll(btnOui, btnNon);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == btnOui;
    }
}
