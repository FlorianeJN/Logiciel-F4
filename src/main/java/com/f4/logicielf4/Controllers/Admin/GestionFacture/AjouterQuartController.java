package com.f4.logicielf4.Controllers.Admin.GestionFacture;
/*
*               GÉRER TEXTAREA NOTES ET AJOUTER QUART À LA FIN
*
* */
import com.f4.logicielf4.Controllers.Strategie.Inf;
import com.f4.logicielf4.Controllers.Strategie.InfAux;
import com.f4.logicielf4.Controllers.Strategie.InfClinic;
import com.f4.logicielf4.Controllers.Strategie.PAB;
import com.f4.logicielf4.Models.Employe;
import com.f4.logicielf4.Utilitaire.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterQuartController implements Initializable {
    @FXML
    private Label titreLabel;

    @FXML
    private DatePicker dateQuart;

    @FXML
    private ComboBox<String> prestation;

    @FXML
    private TextField debutQuart;

    @FXML
    private TextField finQuart;

    @FXML
    private TextField pause;

    @FXML
    private TextField tempsTotal;

    @FXML
    private TextField tauxHoraire;

    @FXML
    private TextField montantHT;

    @FXML
    private ComboBox<String> empComboBox;

    @FXML
    private CheckBox associerEmpCheckBox;

    @FXML
    private Button ajouterQuartBtn;
    @FXML
    private CheckBox checkboxPause;
    @FXML
    private CheckBox checkBoxTempsDouble;
    @FXML
    private CheckBox checkBoxTempsDemi;
    @FXML
    private TextArea notesTextArea;

    private String numFacture;

    public AjouterQuartController(String numFacture)  {
        this.numFacture = numFacture;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ajouterQuartBtn.setOnAction(e -> actionBtnAjouterQuart());
        remplirComboBoxPrestation();
        remplirComboBoxEmployes();
        setReadOnlyTextfields();
        addListeners();
    }
    /**
     * Certains textfields sont en read-only et se mettent à jours automatiquement.
     * Cette méthode gère ces textfields.
     */
    private void setReadOnlyTextfields() {
        tempsTotal.setEditable(false);
        tauxHoraire.setEditable(false);
        montantHT.setEditable(false);
        pause.setDisable(true);
    }

    private void addListeners() {
        // Listener to enable/disable empComboBox based on associerEmpCheckBox
        associerEmpCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            empComboBox.setDisable(!isSelected);
        });

        // Listener to update tauxHoraire based on selected prestation
        prestation.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            updateTauxHoraire(prestation.getValue());
        });

        checkBoxTempsDouble.selectedProperty().addListener((obs, oldValue, isSelected) -> updateTauxHoraire(prestation.getValue()));
        checkBoxTempsDemi.selectedProperty().addListener((obs, oldValue, isSelected) -> updateTauxHoraire(prestation.getValue()));

        // Listener for debutQuart
        debutQuart.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());

        // Listener for finQuart
        finQuart.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());

        // Listener for pause
        pause.textProperty().addListener((obs, oldText, newText) -> calculateTempsTotal());

        tempsTotal.textProperty().addListener((obs, oldValue, newValue) -> updateMontantHT());
        tauxHoraire.textProperty().addListener((obs, oldValue, newValue) -> updateMontantHT());

        // Listener to handle checkboxPause state change
        checkboxPause.selectedProperty().addListener((obs, oldValue, newValue) -> {
            pause.setDisable(!newValue);
            if (pause.isDisable()) {
                pause.setText("00:00");
            }
        });
        //Le field commence disabled
        if (pause.isDisable()) {
            pause.setText("00:00");
        }

    }

    private void updateMontantHT() {
        try {
            String tempsTotalText = tempsTotal.getText();
            String tauxHoraireText = tauxHoraire.getText();

            if (tempsTotalText != null && tauxHoraireText != null && !tempsTotalText.isEmpty() && !tauxHoraireText.isEmpty()) {
                Duration duration = parseDuration(tempsTotalText);
                double tauxHoraireValue = Double.parseDouble(tauxHoraireText);

                if (duration != null) {
                    long totalMinutes = duration.toMinutes();
                    double montantHTValue = totalMinutes / 60.0 * tauxHoraireValue;
                    montantHT.setText(String.format("%.2f", montantHTValue));
                } else {
                    montantHT.setText("Format temps invalide");
                }
            } else {
                montantHT.setText("Données incomplètes");
            }
        } catch (NumberFormatException e) {
            montantHT.setText("Error calculating amount");
        }
    }

    private void calculateTempsTotal() {
        try {
            LocalTime debut = parseTime(debutQuart.getText());
            LocalTime fin = parseTime(finQuart.getText());
            Duration pauseDuration = parseDuration(pause.getText());

            if (debut != null && fin != null && pauseDuration != null) {
                Duration workDuration;
                if (fin.isBefore(debut)) {
                    // End time is on the next day
                    workDuration = Duration.between(debut, LocalTime.MAX).plus(Duration.between(LocalTime.MIN, fin)).minus(pauseDuration);
                } else {
                    workDuration = Duration.between(debut, fin).minus(pauseDuration);
                }

                if (workDuration.isNegative()) {
                    tempsTotal.setText("Entrées de temps invalides!");
                } else {
                    // Round up to the nearest minute
                    workDuration = workDuration.plus(Duration.ofSeconds(30)); // Add 30 seconds to round up
                    tempsTotal.setText(formatDuration(workDuration));
                }
            } else {
                tempsTotal.setText("Entrées de temps invalides");
            }
        } catch (Exception e) {
            tempsTotal.setText("Error calculating time");
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    private LocalTime parseTime(String timeText) {
        if (timeText != null && !timeText.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return LocalTime.parse(timeText, formatter);
            } catch (DateTimeParseException e) {
                // Handle parsing error
            }
        }
        return null;
    }

    private Duration parseDuration(String durationText) {
        if (durationText != null && !durationText.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime duration = LocalTime.parse(durationText, formatter);
                return Duration.ofHours(duration.getHour()).plusMinutes(duration.getMinute());
            } catch (DateTimeParseException e) {
                // Handle parsing error
            }
        }
        return null;
    }

    private void updateTauxHoraire(String prestation) {
        switch (prestation){
            case "INF":
                Inf inf = new Inf();
                tauxHoraire.setText(Double.toString(inf.obtenirTauxHoraire()));
                if(checkBoxTempsDouble.isSelected()) {
                    tauxHoraire.setText(Double.toString(inf.obtenirTauxHoraire()*2));
                }
                if(checkBoxTempsDemi.isSelected()) {
                    tauxHoraire.setText(Double.toString(inf.obtenirTauxHoraire()*1.5));
                }
                break;

            case "INF AUX":
                InfAux infAux = new InfAux();
                tauxHoraire.setText(Double.toString(infAux.obtenirTauxHoraire()));
                if(checkBoxTempsDouble.isSelected()) {
                    tauxHoraire.setText(Double.toString(infAux.obtenirTauxHoraire()*2));
                }
                if(checkBoxTempsDemi.isSelected()) {
                    tauxHoraire.setText(Double.toString(infAux.obtenirTauxHoraire()*1.5));
                }
                break;

            case "INF CL":
                InfClinic infCL = new InfClinic();
                tauxHoraire.setText(Double.toString(infCL.obtenirTauxHoraire()));
                if(checkBoxTempsDouble.isSelected()) {
                    tauxHoraire.setText(Double.toString(infCL.obtenirTauxHoraire()*2));
                }
                if(checkBoxTempsDemi.isSelected()) {
                    tauxHoraire.setText(Double.toString(infCL.obtenirTauxHoraire()*1.5));
                }
                break;

            case "PAB":
                PAB pab = new PAB();
                tauxHoraire.setText(Double.toString(pab.obtenirTauxHoraire()));
                if(checkBoxTempsDouble.isSelected()) {
                    tauxHoraire.setText(Double.toString(pab.obtenirTauxHoraire()*2));
                }
                if(checkBoxTempsDemi.isSelected()) {
                    tauxHoraire.setText(Double.toString(pab.obtenirTauxHoraire()*1.5));
                }
                break;
            default:
                tauxHoraire.setText("");
        }
    }

    private void remplirComboBoxEmployes() {
        List<Employe> listeEmployes = DBUtils.fetchAllEmployees();
        List<String> listeNomsEmployes = new ArrayList<>();

        for (Employe employe : listeEmployes) {
            listeNomsEmployes.add(employe.getNom());
        }
        empComboBox.getItems().setAll(listeNomsEmployes);
        empComboBox.setDisable(true);
    }

    private void remplirComboBoxPrestation() {
        List<String> listeNomsPrestations = new ArrayList<>();
        listeNomsPrestations.add("INF");
        listeNomsPrestations.add("INF AUX");
        listeNomsPrestations.add("INF CL");
        listeNomsPrestations.add("PAB");
        prestation.getItems().setAll(listeNomsPrestations);
    }

    private void actionBtnAjouterQuart() {
    }
}
