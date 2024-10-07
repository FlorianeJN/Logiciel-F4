package com.f4.logicielf4.Models;

/**
 * Représente une adresse composée de divers attributs géographiques.
 * <p>
 * La classe contient des informations telles que le numéro civique, la rue, la ville, la province et le code postal.
 * Elle permet de manipuler et récupérer les détails d'une adresse complète.
 * </p>
 */
public class Adresse {

    private String numeroCivique;
    private String rue;
    private String ville;
    private String province;
    private String codePostal;

    /**
     * Constructeur de la classe Adresse.
     * <p>
     * Initialise une adresse avec ses attributs.
     * </p>
     *
     * @param numeroCivique le numéro civique de l'adresse
     * @param rue la rue de l'adresse
     * @param ville la ville de l'adresse
     * @param province la province de l'adresse
     * @param codePostal le code postal de l'adresse
     */
    public Adresse(String numeroCivique, String rue, String ville, String province, String codePostal) {
        this.numeroCivique = numeroCivique;
        this.rue = rue;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
    }

    /**
     * Génère une chaîne de caractères représentant l'adresse complète au format :
     * "numéro civique rue, ville, province code postal".
     *
     * @return une chaîne de caractères représentant l'adresse complète
     */
    public String genererStringAdresse() {
        return numeroCivique + " " + rue + ", " + ville + ", " + province + " " + codePostal;
    }

    /**
     * Obtient le numéro civique de l'adresse.
     *
     * @return le numéro civique sous forme de chaîne de caractères
     */
    public String getNumeroCivique() {
        return numeroCivique;
    }

    /**
     * Obtient la rue de l'adresse.
     *
     * @return la rue sous forme de chaîne de caractères
     */
    public String getRue() {
        return rue;
    }

    /**
     * Obtient la ville de l'adresse.
     *
     * @return la ville sous forme de chaîne de caractères
     */
    public String getVille() {
        return ville;
    }

    /**
     * Obtient la province de l'adresse.
     *
     * @return la province sous forme de chaîne de caractères
     */
    public String getProvince() {
        return province;
    }

    /**
     * Obtient le code postal de l'adresse.
     *
     * @return le code postal sous forme de chaîne de caractères
     */
    public String getCodePostal() {
        return codePostal;
    }
}
