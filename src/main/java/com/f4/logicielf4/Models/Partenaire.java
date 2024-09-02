package com.f4.logicielf4.Models;

public class Partenaire {
    private String nom;
    private String adresse;
    private String telephone;
    private String courriel;

    // Constructor
    public Partenaire(String nom, String numeroCivique, String rue, String ville, String province, String codePostal, String telephone, String courriel) {
        this.nom = nom;
        this.telephone = telephone;
        this.courriel = courriel;
        genererAdresse(numeroCivique, rue, ville, province, codePostal);
    }

    // Method to generate the address string
    private void genererAdresse(String numeroCivique, String rue, String ville, String province, String codePostal) {
        this.adresse = new Adresse(numeroCivique, rue, ville, province, codePostal).genererStringAdresse();
    }

    // Getters for TableView binding
    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCourriel() {
        return courriel;
    }
}
