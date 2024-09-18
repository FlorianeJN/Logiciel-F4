package com.f4.logicielf4.Controllers.Admin.GestionFacture;

import com.f4.logicielf4.Models.Model;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Utilitaire.DBUtils;
import com.f4.logicielf4.Utilitaire.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommencerFacture {

    @FXML
    private Button btnContinuer;
    @FXML
    private ComboBox<String> partnerComboBox;
    @FXML
    private DatePicker invoiceDatePicker;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

    @FXML
    public void initialize() {
        btnContinuer.setOnAction(event -> actionBtnContinuer());
        setupInvoiceDatePicker();
        setupParterComboxBox();
    }

    private void setupParterComboxBox() {
        List<Partenaire> listePartentaires = DBUtils.fetchAllActivePartners();
        List<String> listeNomsPartenaires = new ArrayList<>();
        for(Partenaire partenaire : listePartentaires) {
            listeNomsPartenaires.add(partenaire.getNom());
        }
        partnerComboBox.getItems().clear();
        partnerComboBox.getItems().setAll(listeNomsPartenaires);
    }

    private void actionBtnContinuer() {
        System.out.println("btnContinuer appuyé");
        LocalDate invoiceDate = invoiceDatePicker.getValue();
        String partner = partnerComboBox.getValue();
        if(partner != null && !partner.isEmpty()) {
            String numFacture = genererNumFacture();
            if(DBUtils.createNewInvoice(numFacture,partner,invoiceDate,"à compléter"))
            {
                Stage stage = (Stage) btnContinuer.getScene().getWindow();
                stage.close();
                if(Dialogs.showConfirmDialog("La facture " + numFacture + " a été créée! Voulez-vous y ajouter des quarts? ", "MESSAGE CONFIRMATION + AJOUTS DE QUARTS")){

                    Model.getInstance().getViewFactory().showPresentationFactureWindow(stage,numFacture,partner);
                }
            }
            else
                Dialogs.showConfirmDialog("Facture non crée","ERREUR CRÉATION FACTURE");
        }else
            Dialogs.showMessageDialog("Assurez-vous de bien remplir les deux champs","ERREUR REMPLISSAGE CHAMPS");
    }

    private String genererNumFacture(){
        int num = DBUtils.obtenirProchainNumFacture();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String formattedDate = invoiceDatePicker.getValue().format(formatter);
        String numFacture = num + "-"+formattedDate;
        return numFacture;
    }

    private void setupInvoiceDatePicker() {
        // Customize the DatePicker to accept only month and year
        invoiceDatePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    YearMonth yearMonth = YearMonth.from(date);
                    return yearMonth.format(formatter);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    YearMonth yearMonth = YearMonth.parse(string, formatter);
                    return yearMonth.atDay(1); // Return the first day of the selected month
                }
                return null;
            }
        });
        invoiceDatePicker.setPromptText("MM/YYYY");
    }
}
