package com.f4.logicielf4.Models;

/**
 * Représente un partenaire (Partenaire) dans le système.
 * <p>
 * Cette classe contient les informations relatives à un partenaire, telles que son nom, son adresse,
 * son téléphone, son email et son statut.
 * </p>
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
     * <p>
     * Cette méthode crée une instance de l'objet `Adresse` à partir des détails fournis
     * (numéro civique, rue, ville, province et code postal) et génère la chaîne de l'adresse formatée.
     * </p>
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

    /**
     * Retourne le nom du partenaire.
     *
     * @return le nom du partenaire
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne l'objet `Adresse` associé au partenaire.
     *
     * @return l'objet `Adresse` du partenaire
     */
    public Adresse getAdresseObj() {
        return adresse;
    }

    /**
     * Retourne la chaîne d'adresse formatée du partenaire.
     * <p>
     * Cette méthode retourne l'adresse sous forme de chaîne dans le format suivant :
     * "numéro civique rue, ville, province code postal".
     * </p>
     *
     * @return l'adresse du partenaire sous forme de chaîne
     */
    public String getAdresse() {
        return adresseString;
    }

    /**
     * Retourne le statut du partenaire.
     *
     * @return le statut du partenaire
     */
    public String getStatus() {
        return status;
    }

    /**
     * Retourne le numéro de téléphone du partenaire.
     *
     * @return le numéro de téléphone du partenaire
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Retourne l'adresse email du partenaire.
     *
     * @return l'adresse email du partenaire
     */
    public String getCourriel() {
        return courriel;
    }
}
