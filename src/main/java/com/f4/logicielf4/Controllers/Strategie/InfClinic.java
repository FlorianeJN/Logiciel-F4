package com.f4.logicielf4.Controllers.Strategie;

public class InfClinic implements StrategiePrestation{

    private String nomPoste;
    private double tauxHoraire;

    public InfClinic(){
        this.nomPoste = "Inf Clinicien(ne)";
        this.tauxHoraire = 74.36;
    }

    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
