package com.f4.logicielf4.Models;

import com.f4.logicielf4.Controllers.Strategie.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente un quart de travail associé à une facture dans le système.
 * <p>
 * Cette classe contient des informations telles que la date, les heures de début et de fin,
 * la pause, la prestation, le taux horaire, le montant total et d'autres détails pertinents.
 * </p>
 */
public class Quart {

    private int id;
    private String numFacture;
    private LocalDate dateQuart;
    private LocalTime debutQuart;
    private LocalTime finQuart;
    private LocalTime pause;
    private String tempsTotal;
    private StrategiePrestation prestation;
    private double tauxHoraire;
    private double montantTotal;
    private Employe employe;
    private String nomEmploye;
    private String notes;
    private String stringPrestation;
    private boolean tempsDouble;
    private boolean tempsDemi;
    private String lieu;

    /**
     * Constructeur utilisé lors de la récupération d'un quart de travail à partir de la base de données.
     *
     * @param id              l'identifiant unique du quart
     * @param numFactureResult le numéro de la facture associée
     * @param dateQuart       la date du quart
     * @param debutQuart      l'heure de début du quart
     * @param finQuart        l'heure de fin du quart
     * @param pause           la durée de la pause
     * @param tempsTotal      la durée totale du quart (sous forme de chaîne de caractères)
     * @param prestation      le type de prestation associé au quart
     * @param tauxHoraire     le taux horaire appliqué
     * @param montantTotal    le montant total calculé pour le quart
     * @param notes           les notes supplémentaires sur le quart
     * @param nomEmploye      le nom de l'employé associé au quart
     * @param tempsDouble     indique si le quart est payé en double temps
     * @param tempsDemi       indique si le quart est payé en demi-temps
     */
    public Quart(int id, String numFactureResult, Date dateQuart, Time debutQuart, Time finQuart, Time pause, String tempsTotal, String prestation, BigDecimal tauxHoraire, BigDecimal montantTotal, String notes, String nomEmploye, boolean tempsDouble, boolean tempsDemi) {
        this.numFacture = numFactureResult;
        this.id = id;
        this.dateQuart = dateQuart.toLocalDate(); // Conversion java.sql.Date en java.time.LocalDate
        this.debutQuart = debutQuart.toLocalTime(); // Conversion java.sql.Time en java.time.LocalTime
        this.finQuart = finQuart.toLocalTime(); // Conversion java.sql.Time en java.time.LocalTime
        this.pause = pause.toLocalTime(); // Conversion java.sql.Time en java.time.LocalTime
        this.tempsTotal = tempsTotal;
        genererPrestation(prestation);
        this.nomEmploye = nomEmploye;
        this.stringPrestation = prestation;
        this.tauxHoraire = tauxHoraire.doubleValue(); // Conversion BigDecimal en double
        this.montantTotal = montantTotal.doubleValue(); // Conversion BigDecimal en double
        this.notes = notes;
        this.tempsDemi = tempsDemi;
        this.tempsDouble = tempsDouble;
    }

    /**
     * Génère le nom de l'employé à partir de l'objet Employe.
     */
    private void genererNomEmploye() {
        if (employe != null) {
            this.nomEmploye = employe.getNom();
        }
    }

    /**
     * Génère l'objet prestation en fonction de la chaîne de caractères reçue.
     *
     * @param prestation le type de prestation sous forme de chaîne (INF, INF AUX, etc.)
     */
    private void genererPrestation(String prestation) {
        if (prestation != null) {
            switch (prestation) {
                case "INF" -> this.prestation = new Inf();
                case "INF AUX" -> this.prestation = new InfAux();
                case "INF CL" -> this.prestation = new InfClinic();
                case "PAB" -> this.prestation = new PAB();
            }
        }
    }

    /**
     * Calcule la durée totale du quart en soustrayant la durée de la pause de la durée du quart.
     * <p>
     * Convertit la durée totale en heures sous forme de chaîne de caractères.
     * </p>
     */
    private void genererTempsTotal() {
        long durationMinutes = java.time.Duration.between(debutQuart, finQuart).toMinutes();
        long pauseMinutes = java.time.Duration.between(LocalTime.MIDNIGHT, pause).toMinutes();
        tempsTotal = String.valueOf((durationMinutes - pauseMinutes) / 60.0); // Conversion des minutes en heures
    }

    /**
     * Génère le taux horaire basé sur la prestation sélectionnée.
     */
    private void genererTauxHoraire() {
        tauxHoraire = this.prestation.obtenirTauxHoraire();
    }

    /**
     * Calcule le montant total du quart en fonction du temps total et du taux horaire.
     */
    private void genererMontantTotal() {
        double dTempsTotal = Double.parseDouble(tempsTotal);
        montantTotal = dTempsTotal * tauxHoraire;
    }

    // Getters et Setters avec recalcul logique

    public String getNumFacture() {
        return numFacture;
    }

    public LocalDate getDateQuart() {
        return dateQuart;
    }

    public LocalTime getDebutQuart() {
        return debutQuart;
    }

    public LocalTime getFinQuart() {
        return finQuart;
    }

    public LocalTime getPause() {
        return pause;
    }

    public String getTempsTotal() {
        return tempsTotal;
    }

    public StrategiePrestation getPrestation() {
        return prestation;
    }

    public double getTauxHoraire() {
        return tauxHoraire;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public String getNomEmploye() {
        return nomEmploye;
    }

    public String getStringPrestation() {
        return stringPrestation;
    }

    public Employe getEmploye() {
        return employe;
    }

    public String getNotes() {
        return this.notes;
    }

    public int getId() {
        return id;
    }

    public boolean isTempsDemi() {
        return tempsDemi;
    }

    public boolean isTempsDouble() {
        return tempsDouble;
    }

    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    public void setDateQuart(LocalDate dateQuart) {
        this.dateQuart = dateQuart;
    }

    public void setDebutQuart(LocalTime debutQuart) {
        this.debutQuart = debutQuart;
        genererTempsTotal();
        genererMontantTotal();
    }

    public void setFinQuart(LocalTime finQuart) {
        this.finQuart = finQuart;
        genererTempsTotal();
        genererMontantTotal();
    }

    public void setPause(LocalTime pause) {
        this.pause = pause;
        genererTempsTotal();
        genererMontantTotal();
    }

    public void setPrestation(StrategiePrestation prestation) {
        this.prestation = prestation;
        genererTauxHoraire();
        genererMontantTotal();
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTempsDemi(boolean tempsDemi) {
        this.tempsDemi = tempsDemi;
    }

    public void setTempsDouble(boolean tempsDouble) {
        this.tempsDouble = tempsDouble;
    }
}
