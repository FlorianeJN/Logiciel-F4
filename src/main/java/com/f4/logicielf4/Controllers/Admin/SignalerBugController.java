package com.f4.logicielf4.Controllers.Admin;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.f4.logicielf4.Utilitaire.Dialogs.showAlert;

/**
 * Contrôleur pour la fonctionnalité de signalement de bug.
 * Gère l'entrée de l'utilisateur, l'envoi d'email et les mises à jour de l'interface utilisateur.
 */
public class SignalerBugController implements Initializable {

    @FXML
    private TextArea bugTextField;

    @FXML
    private Button sendButton;

    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Initialise le contrôleur en configurant l'action du bouton d'envoi et en cachant l'indicateur de progression.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }
        sendButton.setOnAction(event -> actionSendButton());
    }

    /**
     * Gère l'événement de clic du bouton "Envoyer".
     * Valide l'entrée, démarre l'envoi de l'email dans un thread en arrière-plan,
     * et gère les éléments de l'interface utilisateur comme l'indicateur de progression.
     */
    private void actionSendButton() {
        String bugDescription = bugTextField.getText().trim();

        if (bugDescription.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer une description du problème.");
        } else {
            sendButton.setDisable(true);
            if (progressIndicator != null) {
                progressIndicator.setVisible(true);
            }

            Task<Boolean> emailTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return sendEmail(bugDescription);
                }
            };

            emailTask.setOnSucceeded(workerStateEvent -> handleEmailResult(emailTask.getValue()));
            emailTask.setOnFailed(workerStateEvent -> handleEmailError());

            Thread emailThread = new Thread(emailTask);
            emailThread.setDaemon(true);
            emailThread.start();
        }
    }

    /**
     * Envoie un email avec la description du bug fournie.
     *
     * @param bugDescription Le contenu du rapport de bug.
     * @return true si l'email a été envoyé avec succès, false sinon.
     */
    private boolean sendEmail(String bugDescription) {
        String to = "Floriane.nikebie.1@etsmtl.net";
        String host = "smtp.gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);

        String username = System.getenv("EMAIL_USERNAME");
        String password = System.getenv("EMAIL_APP_PASSWORD");

        if (username == null || password == null) {
            System.err.println("Les identifiants ne sont pas définis dans les variables d'environnement.");
            return false;
        }

        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Bug Report");
            message.setText(bugDescription);
            Transport.send(message);

            System.out.println("Le message a été envoyé avec succès...");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gère la réussite de l'envoi de l'email.
     *
     * @param emailSent Indique si l'email a été envoyé avec succès.
     */
    private void handleEmailResult(boolean emailSent) {
        sendButton.setDisable(false);
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        if (emailSent) {
            bugTextField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Merci", "Merci pour votre retour !");
            Stage stage = (Stage) bugTextField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le rapport de bug n'a pas pu être envoyé. Veuillez réessayer plus tard.");
        }
    }

    /**
     * Gère les erreurs survenues lors de l'envoi de l'email.
     */
    private void handleEmailError() {
        sendButton.setDisable(false);
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }
        showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'envoi du rapport de bug. Veuillez réessayer plus tard.");
    }

}
