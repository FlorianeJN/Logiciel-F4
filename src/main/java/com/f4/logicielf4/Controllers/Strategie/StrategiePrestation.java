package com.f4.logicielf4.Controllers.Strategie;

/**
 * Interface pour la stratégie de prestation de services.
 * <p>
 * Cette interface définit les méthodes que chaque classe de stratégie doit implémenter
 * afin de fournir des informations sur un poste spécifique, telles que le taux horaire
 * et le nom du poste.
 * </p>
 * <p>
 * Les classes implémentant cette interface doivent fournir une implémentation des méthodes suivantes :
 * - {@link #obtenirTauxHoraire()}: Retourne le taux horaire pour le poste.
 * - {@link #obtenirNomPoste()}: Retourne le nom du poste.
 * </p>
 */
public interface StrategiePrestation {

    /**
     * Retourne le taux horaire associé au poste.
     *
     * @return Le taux horaire en tant que double.
     */
    public double obtenirTauxHoraire();

    /**
     * Retourne le nom du poste associé à la stratégie.
     *
     * @return Le nom du poste en tant que String.
     */
    public String obtenirNomPoste();
}
