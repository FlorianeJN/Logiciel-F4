package com.f4.logicielf4.Controllers.Strategie;

/**
 * Classe représentant la stratégie pour le poste d'infirmier/infirmière (Inf).
 * <p>
 * Cette classe implémente l'interface {@link StrategiePrestation}, fournissant des méthodes
 * pour obtenir le nom du poste et le taux horaire associé au poste d'infirmier/infirmière.
 * </p>
 */
public class Inf implements StrategiePrestation {

    private String nomPoste;
    private double tauxHoraire;

    /**
     * Constructeur par défaut qui initialise le nom du poste à "Infirmier(e)"
     * et le taux horaire à 71,87 $.
     */
    public Inf() {
        this.nomPoste = "Infirmier(e)";
        this.tauxHoraire = 71.87;
    }

    /**
     * Retourne le taux horaire pour le poste d'infirmier/infirmière.
     *
     * @return Le taux horaire associé à ce poste (71,87 $).
     */
    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    /**
     * Retourne le nom du poste associé à cette stratégie.
     *
     * @return Le nom du poste, qui est "Infirmier(e)".
     */
    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
