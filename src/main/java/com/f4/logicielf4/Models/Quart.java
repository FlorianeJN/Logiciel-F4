package com.f4.logicielf4.Models;

import com.f4.logicielf4.Controllers.Strategie.StrategiePrestation;

import java.time.LocalDate;
import java.time.LocalTime;

public class Quart {

    private int numFacture;   // La facture à laquelle le quart est associée
    private LocalDate dateQuart;
    private LocalTime debutQuart;
    private LocalTime finQuart;
    private LocalTime pause;
    private double tempsTotal;
    private StrategiePrestation prestation;
    private double tauxHoraire;
    private double montantTotal;
    private Employe employe;

    public Quart(int numFacture, LocalDate dateQuart, LocalTime debutQuart, LocalTime finQuart, LocalTime pause, StrategiePrestation prestation, Employe employe) {
        this.numFacture = numFacture;
        this.dateQuart = dateQuart;
        this.debutQuart = debutQuart;
        this.finQuart = finQuart;
        this.pause = pause;
        this.prestation = prestation;
        this.employe = employe;
        genererTempsTotal();
        genererTauxHoraire();
        genererMontantTotal();
    }

    private void genererTempsTotal() {
        long durationMinutes = java.time.Duration.between(debutQuart, finQuart).toMinutes();
        long pauseMinutes = java.time.Duration.between(LocalTime.MIDNIGHT, pause).toMinutes();
        tempsTotal = (durationMinutes - pauseMinutes) / 60.0; // Convert minutes to hours
    }

    private void genererTauxHoraire() {
        tauxHoraire = this.prestation.obtenirTauxHoraire();
    }

    private void genererMontantTotal() {
        montantTotal = tempsTotal * tauxHoraire;
    }

    // Getters
    public int getNumFacture() {
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

    public double getTempsTotal() {
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
    public void setNumFacture(int numFacture) {
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

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }
}
