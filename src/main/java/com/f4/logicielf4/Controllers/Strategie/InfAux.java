package com.f4.logicielf4.Controllers.Strategie;

public class InfAux implements StrategiePrestation{

    private String nomPoste;
    private double tauxHoraire;

    public InfAux(){
        this.nomPoste = "Infirmier(e) auxiliaire";
        this.tauxHoraire = 47.65;
    }

    @Override
    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
