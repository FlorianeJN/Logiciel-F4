package com.f4.logicielf4.Models;

public class Partenaire {
    private String nom;
    private Adresse adresse;
    private String adresseString;
    private String telephone;
    private String courriel;
    private String status;

    // Constructor
    public Partenaire(String nom, String numeroCivique, String rue, String ville, String province, String codePostal, String telephone, String courriel, String status) {
        this.nom = nom;
        this.telephone = telephone;
        this.courriel = courriel;
        this.status = status;
        genererAdresse(numeroCivique, rue, ville, province, codePostal);
    }

    // Method to generate the address string
    private void genererAdresse(String numeroCivique, String rue, String ville, String province, String codePostal) {
        this.adresse = new Adresse(numeroCivique, rue, ville, province, codePostal);
        this.adresseString = adresse.genererStringAdresse();
    }

    // Getters for TableView binding
    public String getNom() {
        return nom;
    }

    public Adresse getAdresseObj() {
        return adresse;
    }

    public String getAdresse() {
        return adresseString;
    }

    public String getStatus(){
        return status;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCourriel() {
        return courriel;
    }
}
