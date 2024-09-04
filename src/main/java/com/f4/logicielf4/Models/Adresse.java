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

    public String getRue() {
        return rue;
    }

    public String getVille() {
        return ville;
    }

    public String getProvince() {
        return province;
    }

    public String getCodePostal() {
        return codePostal;
    }
}
