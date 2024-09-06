package com.f4.logicielf4.Models;

/**
 * Représente un partenaire (Partenaire) dans le système.
 */
public class Partenaire {
    private String nom;
    private Adresse adresse;
    private String adresseString;
    private String telephone;
    private String courriel;
    private String status;

    /**
     * Constructeur pour initialiser un partenaire avec ses détails.
     *
     * @param nom           le nom du partenaire
     * @param numeroCivique le numéro civique de l'adresse
     * @param rue           la rue de l'adresse
     * @param ville         la ville de l'adresse
     * @param province      la province de l'adresse
     * @param codePostal    le code postal de l'adresse
     * @param telephone     le numéro de téléphone du partenaire
     * @param courriel      l'adresse email du partenaire
     * @param status        le statut du partenaire
     */
    public Partenaire(String nom, String numeroCivique, String rue, String ville, String province, String codePostal, String telephone, String courriel, String status) {
        this.nom = nom;
        this.telephone = telephone;
        this.courriel = courriel;
        this.status = status;
        genererAdresse(numeroCivique, rue, ville, province, codePostal);
    }

    /**
     * Génère l'objet `Adresse` et la chaîne d'adresse correspondante.
     *
     * @param numeroCivique le numéro civique de l'adresse
     * @param rue           la rue de l'adresse
     * @param ville         la ville de l'adresse
     * @param province      la province de l'adresse
     * @param codePostal    le code postal de l'adresse
     */
    private void genererAdresse(String numeroCivique, String rue, String ville, String province, String codePostal) {
        this.adresse = new Adresse(numeroCivique, rue, ville, province, codePostal);
        this.adresseString = adresse.genererStringAdresse();
    }


    public String getNom() {
        return nom;
    }

    public Adresse getAdresseObj() {
        return adresse;
    }

    public String getAdresse() {
        return adresseString;
    }

    public String getStatus() {
        return status;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCourriel() {
        return courriel;
    }
}
