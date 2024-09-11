package com.f4.logicielf4.Controllers.Strategie;

public class Inf implements StrategiePrestation {

    private String nomPoste;
    private double tauxHoraire;

    public Inf(){
        this.nomPoste = "Infirmier(e)";
        this.tauxHoraire = 71.87;
    }


    public  double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
