package com.f4.logicielf4.Models;

/**
 * Représente un employé dans le système.
 * <p>
 * Cette classe contient les informations relatives à un employé, telles que le nom d'utilisateur, le nom, le prénom, le téléphone, l'email, le statut et le mot de passe.
 * </p>
 */
public class Employe {
    private String username;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String statut;
    private String password; // Ajout du champ mot de passe

    /**
     * Constructeur de la classe Employe.
     * <p>
     * Ce constructeur permet de créer un employé avec tous les attributs nécessaires.
     * </p>
     *
     * @param username  le nom d'utilisateur de l'employé
     * @param nom       le nom de l'employé
     * @param prenom    le prénom de l'employé
     * @param telephone le numéro de téléphone de l'employé
     * @param email     l'email de l'employé
     * @param statut    le statut de l'employé (par exemple, Actif ou Inactif)
     * @param password  le mot de passe de l'employé
     */
    public Employe(String username, String nom, String prenom, String telephone, String email, String statut, String password) {
        this.username = username;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.statut = statut;
        this.password = password;
    }

    // Getters et Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                "username='" + username + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", statut='" + statut + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
