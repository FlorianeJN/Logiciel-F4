package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Controllers.Strategie.Inf;
import com.f4.logicielf4.Controllers.Strategie.InfAux;
import com.f4.logicielf4.Controllers.Strategie.InfClinic;
import com.f4.logicielf4.Controllers.Strategie.PAB;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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


    protected void setReadOnlyTextfields() {
        tempsTotal.setEditable(false);
        tauxHoraire.setEditable(false);
        montantHT.setEditable(false);
        pause.setDisable(true);
    }

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

    protected void updateMontantHT() {
        try {
            BigDecimal totalMinutes = parseMinutes(tempsTotal.getText());
            BigDecimal tauxHoraireValue = new BigDecimal(tauxHoraire.getText().replace(',', '.'));

            if (totalMinutes != null && tauxHoraireValue != null) {
                BigDecimal montantHTValue = totalMinutes.multiply(tauxHoraireValue).divide(BigDecimal.valueOf(60), BigDecimal.ROUND_HALF_UP);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

                montantHT.setText(decimalFormat.format(montantHTValue));
            } else {
                montantHT.setText("Données incomplètes");
            }
        } catch (NumberFormatException e) {
            montantHT.setText("Erreur de calcul");
        }
    }

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
        }
    }

    protected String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    protected LocalTime parseTime(String timeText) {
        if (timeText != null && !timeText.isEmpty()) {
            try {
                return LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                // Handle parsing error
            }
        }
        return null;
    }

    protected Duration parseDuration(String durationText) {
        if (durationText != null && !durationText.isEmpty()) {
            try {
                LocalTime duration = LocalTime.parse(durationText, DateTimeFormatter.ofPattern("HH:mm"));
                return Duration.ofHours(duration.getHour()).plusMinutes(duration.getMinute());
            } catch (DateTimeParseException e) {
                // Handle parsing error
            }
        }
        return Duration.ZERO;
    }

    protected BigDecimal parseMinutes(String timeText) {
        Duration duration = parseDuration(timeText);
        return duration != null ? BigDecimal.valueOf(duration.toMinutes()) : null;
    }

    protected void updateTauxHoraire(String prestation) {
        if (prestation != null) {
            double tauxHoraireValue = switch (prestation) {
                case "INF" -> new Inf().obtenirTauxHoraire();
                case "INF AUX" -> new InfAux().obtenirTauxHoraire();
                case "INF CL" -> new InfClinic().obtenirTauxHoraire();
                case "PAB" -> new PAB().obtenirTauxHoraire();
                default -> 0.0;
            };

            tauxHoraireValue *= checkBoxTempsDouble.isSelected() ? 2 :
                    checkBoxTempsDemi.isSelected() ? 1.5 : 1;

            tauxHoraire.setText(String.format(Locale.US, "%.2f", tauxHoraireValue));
        }
    }

    protected void remplirComboBoxEmployes() {
        List<Employe> listeEmployes = DBUtils.fetchAllEmployees();
        List<String> listeNomsEmployes = new ArrayList<>();
        for (Employe employe : listeEmployes) {
            listeNomsEmployes.add(employe.getNom());
        }
        empComboBox.getItems().setAll(listeNomsEmployes);
        empComboBox.setDisable(true);
    }

    protected void remplirComboBoxPrestation() {
        List<String> listeNomsPrestations = Arrays.asList("INF", "INF AUX", "INF CL", "PAB");
        prestation.getItems().setAll(listeNomsPrestations);
    }

    protected boolean validateFields() {
        return !tempsTotal.getText().equals("Entrées de temps invalides") &&
                !montantHT.getText().equals("Données incomplètes");
    }

    protected void clearFields() {
        dateQuart.setValue(null);
        prestation.getSelectionModel().clearSelection();
        debutQuart.clear();
        finQuart.clear();
        pause.clear();
        tempsTotal.clear();
        tauxHoraire.clear();
        montantHT.clear();
        notesTextArea.clear();
        associerEmpCheckBox.setSelected(false);
        empComboBox.getSelectionModel().clearSelection();
    }


}
