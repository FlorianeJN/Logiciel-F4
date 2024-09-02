package com.f4.logicielf4.Models;

public class Adresse {

    private String numeroCivique;
    private String rue;
    private String ville;
    private String province;
    private String codePostal;

    public Adresse(String numeroCivique, String rue, String ville, String province, String codePostal) {
        this.numeroCivique = numeroCivique;
        this.rue = rue;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
    }

    public String genererStringAdresse() {
        return numeroCivique + " " + rue + ", " + ville + ", " + province + " " + codePostal;
    }

    public String getNumeroCivique() {
        return numeroCivique;
    }

    public void setNumeroCivique(String numeroCivique) {
        this.numeroCivique = numeroCivique;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
}
