package com.f4.logicielf4.Models;

/**
 * Représente un employé dans le système.
 * <p>
 * Cette classe contient les informations relatives à un employé, telles que l'identifiant, le nom, le prénom, le téléphone, l'email et le statut.
 * </p>
 */
public class Employe {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String statut;

    /**
     * Constructeur de la classe Employe.
     * <p>
     * Ce constructeur permet de créer un employé avec tous les attributs nécessaires.
     * </p>
     *
     * @param id l'identifiant de l'employé
     * @param nom le nom de l'employé
     * @param prenom le prénom de l'employé
     * @param telephone le numéro de téléphone de l'employé
     * @param email l'email de l'employé
     * @param statut le statut de l'employé (par exemple, actif/inactif)
     */
    public Employe(int id, String nom, String prenom, String telephone, String email, String statut) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.statut = statut;
    }

    /**
     * Obtient l'identifiant de l'employé.
     *
     * @return l'identifiant de l'employé
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant de l'employé.
     *
     * @param id l'identifiant de l'employé
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le nom de l'employé.
     *
     * @return le nom de l'employé
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de l'employé.
     *
     * @param nom le nom de l'employé
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient le prénom de l'employé.
     *
     * @return le prénom de l'employé
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom de l'employé.
     *
     * @param prenom le prénom de l'employé
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Obtient le numéro de téléphone de l'employé.
     *
     * @return le numéro de téléphone de l'employé
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Définit le numéro de téléphone de l'employé.
     *
     * @param telephone le numéro de téléphone de l'employé
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Obtient l'email de l'employé.
     *
     * @return l'email de l'employé
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'email de l'employé.
     *
     * @param email l'email de l'employé
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtient le statut de l'employé (par exemple, actif ou inactif).
     *
     * @return le statut de l'employé
     */
    public String getStatut() {
        return statut;
    }

    /**
     * Définit le statut de l'employé.
     *
     * @param statut le statut de l'employé
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Retourne une chaîne de caractères représentant l'employé.
     * <p>
     * Cette méthode est utilisée pour obtenir une vue d'ensemble des informations de l'employé sous forme de chaîne.
     * </p>
     *
     * @return une chaîne de caractères représentant l'employé
     */
    @Override
    public String toString() {
        return "Employe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
