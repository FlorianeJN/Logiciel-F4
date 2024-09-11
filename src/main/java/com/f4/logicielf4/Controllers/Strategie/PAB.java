package com.f4.logicielf4.Controllers.Strategie;

public class PAB implements StrategiePrestation{

    private String nomPoste;
    private double tauxHoraire;

    public PAB(){
        this.nomPoste = "PAB";
        this.tauxHoraire = 41.96;
    }

    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    public String obtenirNomPoste() {
        return this.nomPoste;
    }

}
