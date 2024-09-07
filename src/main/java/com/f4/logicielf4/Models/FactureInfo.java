package com.f4.logicielf4.Models;

public class FactureInfo {
    private String numeroFacture;
    private String nomPartenaire;
    private String date;
    private String montant;
    private String statut;

    // Constructor
    public FactureInfo(String numeroFacture, String nomPartenaire, String date, String montant, String statut) {
        this.numeroFacture = numeroFacture;
        this.nomPartenaire = nomPartenaire;
        this.date = date;
        this.montant = montant;
        this.statut = statut;
    }

    // Getters and Setters
    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public String getNomPartenaire() {
        return nomPartenaire;
    }

    public void setNomPartenaire(String nomPartenaire) {
        this.nomPartenaire = nomPartenaire;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // ToString method for easy display
    @Override
    public String toString() {
        return "FactureInfo{" +
                "numeroFacture='" + numeroFacture + '\'' +
                ", nomPartenaire='" + nomPartenaire + '\'' +
                ", date='" + date + '\'' +
                ", montant='" + montant + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
