package com.f4.logicielf4.Models;

import com.f4.logicielf4.Controllers.Strategie.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

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
    /*
    public Quart(String numFacture, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart, LocalTime pause, StrategiePrestation prestation, String nomEmploye) {
        this.numFacture = numFacture;
        this.dateQuart = dateQuart;
        this.debutQuart = debutQuart;
        this.finQuart = finQuart;
        this.pause = pause;
        this.prestation = prestation;
        this.stringPrestation = prestation.obtenirNomPoste();
        this.nomEmploye = nomEmploye;
        genererNomEmploye();
        genererTempsTotal();
        genererTauxHoraire();
        genererMontantTotal();
    }*/

    /**
     * Constructeur utilisé lors du retrieve de la base de données (On enregistre le id)
     */
    public Quart(int id, String numFactureResult, Date dateQuart, Time debutQuart, Time finQuart, Time pause, String tempsTotal, String prestation, BigDecimal tauxHoraire, BigDecimal montantTotal, String notes,String nomEmploye,boolean tempsDouble,boolean tempsDemi) {
        this.numFacture = numFactureResult;
        this.id = id;
        this.dateQuart = dateQuart.toLocalDate(); // Converting java.sql.Date to java.time.LocalDate
        this.debutQuart = debutQuart.toLocalTime(); // Converting java.sql.Time to java.time.LocalTime
        this.finQuart = finQuart.toLocalTime(); // Converting java.sql.Time to java.time.LocalTime
        this.pause = pause.toLocalTime(); // Converting java.sql.Time to java.time.LocalTime
        this.tempsTotal = tempsTotal; // Converting BigDecimal to double
        genererPrestation(prestation);
        this.nomEmploye = nomEmploye;
        this.stringPrestation = prestation;
        this.tauxHoraire = tauxHoraire.doubleValue(); // Converting BigDecimal to double
        this.montantTotal = montantTotal.doubleValue(); // Converting BigDecimal to double
        this.notes = notes;
        this.tempsDemi = tempsDemi;
        this.tempsDouble = tempsDouble;
    }

    private void genererNomEmploye() {
        if(employe != null){
            this.nomEmploye = employe.getNom();
        }
    }

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

    private void genererTempsTotal() {
        long durationMinutes = java.time.Duration.between(debutQuart, finQuart).toMinutes();
        long pauseMinutes = java.time.Duration.between(LocalTime.MIDNIGHT, pause).toMinutes();
        tempsTotal = String.valueOf((durationMinutes - pauseMinutes) / 60.0); // Convert minutes to hours
    }

    private void genererTauxHoraire() {
        tauxHoraire = this.prestation.obtenirTauxHoraire();
    }

    private void genererMontantTotal() {
        double dTempsTotal = Double.parseDouble(tempsTotal);
        montantTotal =dTempsTotal * tauxHoraire;
    }

    // Getters
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

    // Setters with recalculation logic
    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    public void setDateQuart(LocalDate dateQuart) {
        this.dateQuart = dateQuart;
    }

    public String getNomEmploye() {
        return nomEmploye;
    }

    public String getStringPrestation() {
        return stringPrestation;
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

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public String getNotes(){
        return  this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTempsDemi() {
        return tempsDemi;
    }

    public void setTempsDemi(boolean tempsDemi) {
        this.tempsDemi = tempsDemi;
    }

    public boolean isTempsDouble() {
        return tempsDouble;
    }

    public void setTempsDouble(boolean tempsDouble) {
        this.tempsDouble = tempsDouble;
    }
}
