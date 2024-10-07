package com.f4.logicielf4.Controllers.Strategie;

/**
 * Classe représentant la stratégie pour le poste d'infirmier/infirmière clinicien(ne) (InfClinic).
 * <p>
 * Cette classe implémente l'interface {@link StrategiePrestation}, fournissant des méthodes
 * pour obtenir le nom du poste et le taux horaire associé au poste d'infirmier/infirmière clinicien(ne).
 * </p>
 */
public class InfClinic implements StrategiePrestation {

    private String nomPoste;
    private double tauxHoraire;

    /**
     * Constructeur par défaut qui initialise le nom du poste à "Inf Clinicien(ne)"
     * et le taux horaire à 74,36 $.
     */
    public InfClinic() {
        this.nomPoste = "Inf Clinicien(ne)";
        this.tauxHoraire = 74.36;
    }

    /**
     * Retourne le taux horaire pour le poste d'infirmier/infirmière clinicien(ne).
     *
     * @return Le taux horaire associé à ce poste (74,36 $).
     */
    @Override
    public double obtenirTauxHoraire() {
        return this.tauxHoraire;
    }

    /**
     * Retourne le nom du poste associé à cette stratégie.
     *
     * @return Le nom du poste, qui est "Inf Clinicien(ne)".
     */
    @Override
    public String obtenirNomPoste() {
        return this.nomPoste;
    }
}
