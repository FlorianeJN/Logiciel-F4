package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.Inf;
import com.f4.logicielf4.Controllers.Strategie.InfAux;
import com.f4.logicielf4.Controllers.Strategie.InfClinic;
import com.f4.logicielf4.Controllers.Strategie.PAB;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Template pour l'ajout et la modification d'un quart de travail.
 * Cette classe gère l'interface utilisateur pour l'ajout ou la modification des informations d'un quart de travail,
 * telles que la date, les heures de début et de fin, la prestation, les pauses, le taux horaire, etc.
 * Elle calcule également les montants et les durées basés sur les informations fournies.
 */
public class AjouterModifierQuartTemplate {

    @FXML
    protected Label titreLabel;

    @FXML
    protected DatePicker dateQuart;

    @FXML
    protected ComboBox<String> prestation;

    @FXML
    protected TextField debutQuart;

    @FXML
    protected TextField finQuart;

    @FXML
    protected TextField pause;

    @FXML
    protected TextField tempsTotal;

    @FXML
    protected TextField tauxHoraire;

    @FXML
    protected TextField montantHT;

    @FXML
    protected ComboBox<String> empComboBox;

    @FXML
    protected CheckBox associerEmpCheckBox;

    @FXML
    protected Button ajouterQuartBtn;

    @FXML
    protected CheckBox checkboxPause;

    @FXML
    protected CheckBox checkBoxTempsDouble;

    @FXML
    protected CheckBox checkBoxTempsDemi;

    @FXML
    protected TextArea notesTextArea;

    private static final Logger LOGGER = Logger.getLogger(AjouterModifierQuartTemplate.class.getName());

    /**
     * Définit les champs de texte comme étant en lecture seule.
     * Utilisé pour empêcher la modification des champs calculés automatiquement (temps total, taux horaire, montant HT).
     */
    protected void setReadOnlyTextfields() {
        tempsTotal.setEditable(false);
        tauxHoraire.setEditable(false);
        montantHT.setEditable(false);
        pause.setDisable(true);
    }

    /**
     * Ajoute des listeners pour réagir aux changements dans les champs de texte et les cases à cocher,
     * et met à jour les valeurs dépendantes telles que le temps total, le taux horaire et le montant HT.
     */
    protected void addListeners() {
        associerEmpCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> empComboBox.setDisable(!isSelected));

        prestation.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateTauxHoraire(newValue));
        checkBoxTempsDouble.selectedProperty().addListener((obs, oldValue, isSelected) -> updateTauxHoraire(prestation.getValue()));
        checkBoxTempsDemi.selectedProperty().addListener((obs, oldValue, isSelected) -> updateTauxHoraire(prestation.getValue()));

        debutQuart.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());
        finQuart.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());
        pause.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());

        tempsTotal.textProperty().addListener((obs, oldValue, newValue) -> updateMontantHT());
        tauxHoraire.textProperty().addListener((obs, oldValue, newValue) -> updateMontantHT());

        checkboxPause.selectedProperty().addListener((obs, oldValue, newValue) -> {
            pause.setDisable(!newValue);
            if (pause.isDisable()) {
                pause.setText("00:00");
            }
        });

        if (pause.isDisable()) {
            pause.setText("00:00");
        }
    }

    /**
     * Met à jour le montant total hors taxes (montant HT) basé sur le temps total et le taux horaire.
     * Effectue les calculs nécessaires et affiche le montant HT dans le champ de texte correspondant.
     */
    protected void updateMontantHT() {
        try {
            BigDecimal totalMinutes = parseMinutes(tempsTotal.getText());
            BigDecimal tauxHoraireValue = new BigDecimal(tauxHoraire.getText().replace(',', '.'));

            if (totalMinutes != null && tauxHoraireValue != null) {
                BigDecimal montantHTValue = totalMinutes.multiply(tauxHoraireValue).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

                montantHT.setText(decimalFormat.format(montantHTValue));
            } else {
                montantHT.setText("Données incomplètes");
            }
        } catch (NumberFormatException e) {
            montantHT.setText("Erreur de calcul");
            LOGGER.log(Level.WARNING, "Erreur de calcul du montant HT", e);
        }
    }

    /**
     * Calcule le temps total de travail en fonction de l'heure de début, de fin et de la durée de la pause.
     * Les résultats sont affichés dans le champ de texte "temps total".
     */
    protected void calculateTempsTotal() {
        try {
            LocalTime debut = parseTime(debutQuart.getText());
            LocalTime fin = parseTime(finQuart.getText());
            Duration pauseDuration = parseDuration(pause.getText());

            if (debut != null && fin != null && pauseDuration != null) {
                Duration workDuration = fin.isBefore(debut) ?
                        Duration.between(debut, LocalTime.MAX).plus(Duration.between(LocalTime.MIN, fin)).minus(pauseDuration) :
                        Duration.between(debut, fin).minus(pauseDuration);

                if (workDuration.isNegative()) {
                    tempsTotal.setText("Entrées de temps invalides!");
                } else {
                    workDuration = workDuration.plus(Duration.ofSeconds(30));
                    tempsTotal.setText(formatDuration(workDuration));
                }
            } else {
                tempsTotal.setText("Entrées de temps invalides");
            }
        } catch (Exception e) {
            tempsTotal.setText("Erreur de calcul");
            LOGGER.log(Level.WARNING, "Erreur de calcul du temps total", e);
        }
    }

    /**
     * Formate une durée en chaîne de caractères au format HH:mm.
     *
     * @param duration La durée à formater.
     * @return Une chaîne de caractères représentant la durée au format HH:mm.
     */
    protected String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Analyse une chaîne de caractères représentant l'heure et la convertit en objet LocalTime.
     *
     * @param timeText La chaîne de caractères à analyser.
     * @return Un objet LocalTime ou null si la chaîne est invalide.
     */
    protected LocalTime parseTime(String timeText) {
        if (timeText != null && !timeText.isEmpty()) {
            try {
                return LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Format d'heure invalide: " + timeText, e);
            }
        }
        return null;
    }

    /**
     * Analyse une chaîne de caractères représentant une durée au format HH:mm et la convertit en objet Duration.
     *
     * @param durationText La chaîne de caractères à analyser.
     * @return Un objet Duration représentant la durée ou Duration.ZERO si la chaîne est invalide.
     */
    protected Duration parseDuration(String durationText) {
        if (durationText != null && !durationText.isEmpty()) {
            try {
                LocalTime duration = LocalTime.parse(durationText, DateTimeFormatter.ofPattern("HH:mm"));
                return Duration.ofHours(duration.getHour()).plusMinutes(duration.getMinute());
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Format de durée invalide: " + durationText, e);
            }
        }
        return Duration.ZERO;
    }

    /**
     * Analyse une chaîne de caractères représentant une durée au format HH:mm et la convertit en minutes.
     *
     * @param timeText La chaîne de caractères à analyser.
     * @return Un BigDecimal représentant la durée en minutes ou null si la chaîne est invalide.
     */
    protected BigDecimal parseMinutes(String timeText) {
        Duration duration = parseDuration(timeText);
        return duration != null ? BigDecimal.valueOf(duration.toMinutes()) : null;
    }

    /**
     * Met à jour le taux horaire basé sur la prestation sélectionnée.
     * Applique également un ajustement pour le temps double ou demi-temps si les cases correspondantes sont cochées.
     *
     * @param prestation La prestation sélectionnée.
     */
    protected void updateTauxHoraire(String prestation) {
        if (prestation != null) {
            double tauxHoraireValue = switch (prestation) {
                case "SOINS INFIRMIERS" -> new Inf().obtenirTauxHoraire();
                case "INF AUXILIAIRE" -> new InfAux().obtenirTauxHoraire();
                case "INF CLINICIEN(NE)" -> new InfClinic().obtenirTauxHoraire();
                case "PAB" -> new PAB().obtenirTauxHoraire();
                default -> 0.0;
            };

            tauxHoraireValue *= checkBoxTempsDouble.isSelected() ? 2 :
                    checkBoxTempsDemi.isSelected() ? 1.5 : 1;

            tauxHoraire.setText(String.format(Locale.US, "%.2f", tauxHoraireValue));
        }
    }

    /**
     * Remplit la comboBox des employés avec les noms des employés extraits de la base de données.
     * Exécute l'opération dans un thread d'arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    protected void remplirComboBoxEmployes() {
        Task<List<String>> task = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                List<Employe> listeEmployes = DBUtils.fetchAllEmployees();
                List<String> listeNomsEmployes = new ArrayList<>();
                for (Employe employe : listeEmployes) {
                    listeNomsEmployes.add(employe.getPrenom());
                }
                return listeNomsEmployes;
            }
        };

        task.setOnSucceeded(event -> {
            empComboBox.getItems().setAll(task.getValue());
            empComboBox.setDisable(true);
        });

        task.setOnFailed(event -> {
            Throwable e = task.getException();
            LOGGER.log(Level.SEVERE, "Erreur lors du remplissage de la ComboBox des employés", e);
            empComboBox.setDisable(true);
        });

        new Thread(task).start();
    }

    /**
     * Remplit la comboBox des prestations avec les prestations disponibles.
     * Puisque les données sont locales, pas besoin de thread séparé.
     */
    protected void remplirComboBoxPrestation() {
        List<String> listeNomsPrestations = Arrays.asList("SOINS INFIRMIERS", "INF AUXILIAIRE", "INF CLINICIEN(NE)", "PAB");
        prestation.getItems().setAll(listeNomsPrestations);
    }

    /**
     * Valide que les champs essentiels du formulaire sont correctement remplis.
     *
     * @return true si les champs sont valides, false sinon.
     */
    protected boolean validateFields() {
        return !tempsTotal.getText().equals("Entrées de temps invalides") &&
                !montantHT.getText().equals("Données incomplètes") &&
                !montantHT.getText().equals("Erreur de calcul") &&
                dateQuart.getValue() != null &&
                prestation.getValue() != null &&
                !debutQuart.getText().isEmpty() &&
                !finQuart.getText().isEmpty();
    }

    /**
     * Réinitialise tous les champs du formulaire à leur état initial.
     */
    protected void clearFields() {
        dateQuart.setValue(null);
        prestation.getSelectionModel().clearSelection();
        debutQuart.clear();
        finQuart.clear();
        tempsTotal.clear();
        tauxHoraire.clear();
        montantHT.clear();
        notesTextArea.clear();
        associerEmpCheckBox.setSelected(false);
        empComboBox.getSelectionModel().clearSelection();
        checkboxPause.setSelected(false);
        pause.setText("00:00");
    }
}
