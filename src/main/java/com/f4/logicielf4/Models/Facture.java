package com.f4.logicielf4.Models;

import com.f4.logicielf4.Utilitaire.DBUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class Facture {
    private String numFacture;
    private Partenaire partenaire;
    private LocalDate dateFacture;
    private BigDecimal montantAvantTaxes;
    private BigDecimal tps;
    private BigDecimal tvq;
    private BigDecimal montantApresTaxes;
    private String statut;
    private String nomPartenaire;

    public Facture(String numFacture, Partenaire partenaire, LocalDate dateFacture, String statut) {
        this.numFacture = numFacture;
        this.partenaire = partenaire;
        this.dateFacture = dateFacture;
        this.statut = statut;
        this.nomPartenaire = partenaire.getNom();
    }

    public Facture(String numFacture, Partenaire partenaire, LocalDate dateFacture,
                   BigDecimal montantAvantTaxes, BigDecimal tps, BigDecimal tvq,
                   BigDecimal montantApresTaxes, String statut) {
        this.numFacture = numFacture;
        this.partenaire = partenaire;
        this.dateFacture = dateFacture;
        this.montantAvantTaxes = setTwoDecimalPlaces(montantAvantTaxes);
        this.tps = setTwoDecimalPlaces(tps);
        this.tvq = setTwoDecimalPlaces(tvq);
        this.montantApresTaxes = setTwoDecimalPlaces(montantApresTaxes);
        this.statut = statut;
        this.nomPartenaire = partenaire.getNom();
    }

    // Getter and Setter Methods
    public String getNumFacture() {
        return numFacture;
    }

    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    public BigDecimal getMontantAvantTaxes() {
        return montantAvantTaxes;
    }

    public void setMontantAvantTaxes(BigDecimal montantAvantTaxes) {
        this.montantAvantTaxes = setTwoDecimalPlaces(montantAvantTaxes);
        updateTaxesAndTotal();
        mettreAJourDB();
    }

    public BigDecimal getTps() {
        return tps;
    }

    public void setTps(BigDecimal tps) {
        this.tps = setTwoDecimalPlaces(tps);
    }

    public BigDecimal getTvq() {
        return tvq;
    }

    public void setTvq(BigDecimal tvq) {
        this.tvq = setTwoDecimalPlaces(tvq);
    }

    public BigDecimal getMontantApresTaxes() {
        return montantApresTaxes;
    }

    public void setMontantApresTaxes(BigDecimal montantApresTaxes) {
        this.montantApresTaxes = setTwoDecimalPlaces(montantApresTaxes);
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Quart> getListeQuarts(){
        return DBUtils.fetchQuartsByNumFacture(numFacture);
    }

    private BigDecimal setTwoDecimalPlaces(BigDecimal value) {
        if (value != null) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2);
    }

    private void updateTaxesAndTotal() {
        double montantHT = montantAvantTaxes.doubleValue();
        this.tps = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT * 0.05));
        this.tvq = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT * 0.09975));
        this.montantApresTaxes = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT + tps.doubleValue() + tvq.doubleValue()));
    }

    private void mettreAJourDB() {
        DBUtils.mettreAJourFacture(this);
    }

    public Partenaire getPartenaire() {
        return partenaire;
    }

    public void setPartenaire(Partenaire partenaire) {
        this.partenaire = partenaire;
    }

    @Override
    public String toString() {
        return "Facture{" +
                "numFacture=" + numFacture +
                ", nomPartenaire='" +partenaire.getNom()+ '\'' +
                ", dateFacture=" + dateFacture +
                ", montantAvantTaxes=" + montantAvantTaxes +
                ", tps=" + tps +
                ", tvq=" + tvq +
                ", montantApresTaxes=" + montantApresTaxes +
                ", statut='" + statut + '\'' +
                '}';
    }

    public String getNomPartenaire() {
        return nomPartenaire;
    }

}
