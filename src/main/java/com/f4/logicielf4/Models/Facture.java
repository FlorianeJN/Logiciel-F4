package com.f4.logicielf4.Models;

import com.f4.logicielf4.Utilitaire.DBUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe représentant une facture dans le système.
 * <p>
 * La classe contient des informations sur le numéro de facture, le partenaire, les montants avant et après taxes,
 * les taux de TPS/TVQ, le statut et la date de création.
 * </p>
 */
public class Facture {
    private String numFacture;
    private Partenaire partenaire;
    private LocalDate dateFacture;
    private BigDecimal montantAvantTaxes;
    private BigDecimal tps;
    private BigDecimal tvq;
    private BigDecimal montantApresTaxes;
    private String statut;
    private String nomPartenaire;

    /**
     * Constructeur sans montant avant taxes, TPS et TVQ.
     *
     * @param numFacture   Le numéro de la facture.
     * @param partenaire   Le partenaire associé à la facture.
     * @param dateFacture  La date de création de la facture.
     * @param statut       Le statut de la facture (ex: "payée", "en attente").
     */
    public Facture(String numFacture, Partenaire partenaire, LocalDate dateFacture, String statut) {
        this.numFacture = numFacture;
        this.partenaire = partenaire;
        this.dateFacture = dateFacture;
        this.statut = statut;
        this.nomPartenaire = partenaire.getNom();
    }

    /**
     * Constructeur avec montants avant et après taxes, TPS et TVQ.
     *
     * @param numFacture         Le numéro de la facture.
     * @param partenaire         Le partenaire associé à la facture.
     * @param dateFacture        La date de création de la facture.
     * @param montantAvantTaxes  Le montant avant taxes.
     * @param tps                Le montant de la TPS (5%).
     * @param tvq                Le montant de la TVQ (9.975%).
     * @param montantApresTaxes  Le montant total après application des taxes.
     * @param statut             Le statut de la facture.
     */
    public Facture(String numFacture, Partenaire partenaire, LocalDate dateFacture,
                   BigDecimal montantAvantTaxes, BigDecimal tps, BigDecimal tvq,
                   BigDecimal montantApresTaxes, String statut) {
        this.numFacture = numFacture;
        this.partenaire = partenaire;
        this.dateFacture = dateFacture;
        this.montantAvantTaxes = setTwoDecimalPlaces(montantAvantTaxes);
        this.tps = setTwoDecimalPlaces(tps);
        this.tvq = setTwoDecimalPlaces(tvq);
        this.montantApresTaxes = setTwoDecimalPlaces(montantApresTaxes);
        this.statut = statut;
        this.nomPartenaire = partenaire.getNom();
    }

    /**
     * Retourne le numéro de la facture.
     *
     * @return Le numéro de la facture.
     */
    public String getNumFacture() {
        return numFacture;
    }

    /**
     * Définit le numéro de la facture.
     *
     * @param numFacture Le numéro de la facture.
     */
    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    /**
     * Retourne la date de création de la facture.
     *
     * @return La date de la facture.
     */
    public LocalDate getDateFacture() {
        return dateFacture;
    }

    /**
     * Définit la date de création de la facture.
     *
     * @param dateFacture La date de la facture.
     */
    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    /**
     * Retourne le montant avant taxes de la facture.
     *
     * @return Le montant avant taxes.
     */
    public BigDecimal getMontantAvantTaxes() {
        return montantAvantTaxes;
    }

    /**
     * Définit le montant avant taxes de la facture et met à jour les taxes ainsi que le montant total.
     *
     * @param montantAvantTaxes Le montant avant taxes.
     */
    public void setMontantAvantTaxes(BigDecimal montantAvantTaxes) {
        this.montantAvantTaxes = setTwoDecimalPlaces(montantAvantTaxes);
        updateTaxesAndTotal();
        mettreAJourDB();
    }

    /**
     * Retourne le montant de la TPS (5%) appliqué à la facture.
     *
     * @return Le montant de la TPS.
     */
    public BigDecimal getTps() {
        return tps;
    }

    /**
     * Définit le montant de la TPS (5%) de la facture.
     *
     * @param tps Le montant de la TPS.
     */
    public void setTps(BigDecimal tps) {
        this.tps = setTwoDecimalPlaces(tps);
    }

    /**
     * Retourne le montant de la TVQ (9.975%) appliqué à la facture.
     *
     * @return Le montant de la TVQ.
     */
    public BigDecimal getTvq() {
        return tvq;
    }

    /**
     * Définit le montant de la TVQ (9.975%) de la facture.
     *
     * @param tvq Le montant de la TVQ.
     */
    public void setTvq(BigDecimal tvq) {
        this.tvq = setTwoDecimalPlaces(tvq);
    }

    /**
     * Retourne le montant total après taxes de la facture.
     *
     * @return Le montant total après taxes.
     */
    public BigDecimal getMontantApresTaxes() {
        return montantApresTaxes;
    }

    /**
     * Définit le montant total après taxes de la facture.
     *
     * @param montantApresTaxes Le montant total après taxes.
     */
    public void setMontantApresTaxes(BigDecimal montantApresTaxes) {
        this.montantApresTaxes = setTwoDecimalPlaces(montantApresTaxes);
    }

    /**
     * Retourne le statut de la facture.
     *
     * @return Le statut de la facture.
     */
    public String getStatut() {
        return statut;
    }

    /**
     * Définit le statut de la facture.
     *
     * @param statut Le statut de la facture.
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Retourne la liste des quarts associés à cette facture.
     *
     * @return Une liste de quarts.
     */
    public List<Quart> getListeQuarts() {
        return DBUtils.fetchQuartsByNumFacture(numFacture);
    }

    /**
     * Retourne le partenaire associé à la facture.
     *
     * @return Le partenaire associé à la facture.
     */
    public Partenaire getPartenaire() {
        return partenaire;
    }

    /**
     * Définit le partenaire associé à la facture.
     *
     * @param partenaire Le partenaire à associer à la facture.
     */
    public void setPartenaire(Partenaire partenaire) {
        this.partenaire = partenaire;
    }

    /**
     * Arrondit une valeur à deux décimales.
     *
     * @param value La valeur à arrondir.
     * @return La valeur arrondie à deux décimales.
     */
    private BigDecimal setTwoDecimalPlaces(BigDecimal value) {
        if (value != null) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2);
    }

    /**
     * Met à jour les montants des taxes (TPS/TVQ) et le montant total après taxes en fonction du montant avant taxes.
     */
    private void updateTaxesAndTotal() {
        double montantHT = montantAvantTaxes.doubleValue();
        this.tps = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT * 0.05));
        this.tvq = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT * 0.09975));
        this.montantApresTaxes = setTwoDecimalPlaces(BigDecimal.valueOf(montantHT + tps.doubleValue() + tvq.doubleValue()));
    }

    /**
     * Met à jour la facture dans la base de données en utilisant la méthode correspondante dans `DBUtils`.
     */
    private void mettreAJourDB() {
        DBUtils.mettreAJourFacture(this);
    }

    /**
     * Retourne le nom du partenaire associé à la facture.
     *
     * @return Le nom du partenaire.
     */
    public String getNomPartenaire() {
        return nomPartenaire;
    }

    /**
     * Retourne une chaîne de caractères représentant la facture.
     *
     * @return Une chaîne de caractères contenant les informations de la facture.
     */
    @Override
    public String toString() {
        return "Facture{" +
                "numFacture=" + numFacture +
                ", nomPartenaire='" + partenaire.getNom() + '\'' +
                ", dateFacture=" + dateFacture +
                ", montantAvantTaxes=" + montantAvantTaxes +
                ", tps=" + tps +
                ", tvq=" + tvq +
                ", montantApresTaxes=" + montantApresTaxes +
                ", statut='" + statut + '\'' +
                '}';
    }
}
