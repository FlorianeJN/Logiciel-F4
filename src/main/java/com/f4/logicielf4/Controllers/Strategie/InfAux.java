package com.f4.logicielf4.Controllers.Strategie;

/**
 * Classe représentant la stratégie pour le poste d'infirmier/infirmière auxiliaire (InfAux).
 * <p>
 * Cette classe implémente l'interface {@link StrategiePrestation}, fournissant des méthodes
 * pour obtenir le nom du poste et le taux horaire associé au poste d'infirmier/infirmière auxiliaire.
 * </p>
 */
public class InfAux implements StrategiePrestation {

    private String nomPoste;
    private double tauxHoraire;

    /**
     * Constructeur par défaut qui initialise le nom du poste à "Infirmier(e) auxiliaire"
     * et le taux horaire à 47,65 $.
     */
    public InfAux() {
        this.nomPoste = "Infirmier(e) auxiliaire";
        this.tauxHoraire = 47.65;
    }

    /**
     * Retourne le taux horaire pour le poste d'infirmier/infirmière auxiliaire.
     *
     * @return Le taux horaire associé à ce poste (47,65 $).
     */
    @Override
    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    /**
     * Retourne le nom du poste associé à cette stratégie.
     *
     * @return Le nom du poste, qui est "Infirmier(e) auxiliaire".
     */
    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
