package com.f4.logicielf4.Models;

import com.f4.logicielf4.Utilitaire.DBUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Facture {
    private String numFacture;
    private String nomPartenaire;
    private LocalDate dateFacture;
    private BigDecimal montantAvantTaxes;
    private BigDecimal tps;
    private BigDecimal tvq;
    private BigDecimal montantApresTaxes;
    private String statut;

    // Constructor
    public Facture(String numFacture, String nomPartenaire, LocalDate dateFacture,String statut) {
        this.numFacture = numFacture;
        this.nomPartenaire = nomPartenaire;
        this.dateFacture = dateFacture;
      //  this.montantAvantTaxes = montantAvantTaxes;
       // this.tps = tps;
       // this.tvq = tvq;
       // this.montantApresTaxes = montantApresTaxes;
        this.statut = statut;
    }

    public Facture(String numFacture, String nomPartenaire, LocalDate dateFacture,
                   BigDecimal montantAvantTaxes, BigDecimal tps, BigDecimal tvq,
                   BigDecimal montantApresTaxes, String statut) {
        this.numFacture = numFacture;
        this.nomPartenaire = nomPartenaire;
        this.dateFacture = dateFacture;
        this.montantAvantTaxes = montantAvantTaxes;
        this.tps = tps;
        this.tvq = tvq;
        this.montantApresTaxes = montantApresTaxes;
        this.statut = statut;
    }

    public String getNumFacture() {
        return numFacture;
    }

    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    public String getNomPartenaire() {
        return nomPartenaire;
    }

    public void setNomPartenaire(String nomPartenaire) {
        this.nomPartenaire = nomPartenaire;
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
        this.montantAvantTaxes = montantAvantTaxes;
        double montantHT = montantAvantTaxes.doubleValue();
        this.tps = BigDecimal.valueOf(montantHT * 0.05);
        this.tvq = BigDecimal.valueOf(montantHT * 0.09975);
        this.montantApresTaxes = BigDecimal.valueOf(montantHT+tps.doubleValue()+tvq.doubleValue());
        mettreAJourDB();
    }

    private void mettreAJourDB() {
        DBUtils.mettreAJourFacture(this);
    }

    public BigDecimal getTps() {
        return tps;
    }

    public void setTps(BigDecimal tps) {
        this.tps = tps;
    }

    public BigDecimal getTvq() {
        return tvq;
    }

    public void setTvq(BigDecimal tvq) {
        this.tvq = tvq;
    }

    public BigDecimal getMontantApresTaxes() {
        return montantApresTaxes;
    }

    public void setMontantApresTaxes(BigDecimal montantApresTaxes) {
        this.montantApresTaxes = montantApresTaxes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Facture{" +
                "numFacture=" + numFacture +
                ", nomPartenaire='" + nomPartenaire + '\'' +
                ", dateFacture=" + dateFacture +
                ", montantAvantTaxes=" + montantAvantTaxes +
                ", tps=" + tps +
                ", tvq=" + tvq +
                ", montantApresTaxes=" + montantApresTaxes +
                ", statut='" + statut + '\'' +
                '}';
    }

}
