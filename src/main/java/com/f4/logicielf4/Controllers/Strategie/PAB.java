package com.f4.logicielf4.Controllers.Strategie;

/**
 * Classe représentant la stratégie pour le poste de préposé aux bénéficiaires (PAB).
 * <p>
 * Cette classe implémente l'interface {@link StrategiePrestation}, fournissant des méthodes
 * pour obtenir le nom du poste et le taux horaire associé au poste de préposé aux bénéficiaires.
 * </p>
 */
public class PAB implements StrategiePrestation {

    private String nomPoste;
    private double tauxHoraire;

    /**
     * Constructeur par défaut qui initialise le nom du poste à "PAB"
     * et le taux horaire à 41,96 $.
     */
    public PAB() {
        this.nomPoste = "PAB";
        this.tauxHoraire = 41.96;
    }

    /**
     * Retourne le taux horaire pour le poste de préposé aux bénéficiaires.
     *
     * @return Le taux horaire associé à ce poste (41,96 $).
     */
    @Override
    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    /**
     * Retourne le nom du poste associé à cette stratégie.
     *
     * @return Le nom du poste, qui est "PAB".
     */
    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
