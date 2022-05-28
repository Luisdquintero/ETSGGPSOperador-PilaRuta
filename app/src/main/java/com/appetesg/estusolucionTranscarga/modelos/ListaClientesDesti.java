package com.appetesg.estusolucionTranscarga.modelos;

public class ListaClientesDesti {
    private int intCodDest;
    private String strNombreDest;
    private String strApellidoDest;
    private String strCompaniaDest;
    private String strCedulaDest;
    private String strTelDest;
    private String strDireDest;
    private String strFechaDest;
    private String strCiudad;


    public ListaClientesDesti(int intCodDest, String strNombreDest, String strCedulaDest, String strTelDest, String strDireDest, String strFechaDest ,String strApellidoDest, String strCompaniaDest, String strCiudad) {
        this.intCodDest = intCodDest;
        this.strNombreDest = strNombreDest;
        this.strApellidoDest = strApellidoDest;
        this.strCompaniaDest = strCompaniaDest;
        this.strCedulaDest = strCedulaDest;
        this.strTelDest = strTelDest;
        this.strDireDest = strDireDest;
        this.strFechaDest = strFechaDest;
        this.strCiudad = strCiudad;
    }

    public ListaClientesDesti(int intCodDest, String strNombreDest, String strCedulaDest, String strTelDest, String strDireDest, String strFechaDest)
    {
        this.intCodDest = intCodDest;
        this.strNombreDest = strNombreDest;
        this.strCedulaDest = strCedulaDest;
        this.strTelDest = strTelDest;
        this.strDireDest = strDireDest;
        this.strFechaDest = strFechaDest;
    }

    public String getStrApellidoDest() {
        return strApellidoDest;
    }

    public void setStrApellidoDest(String strApellidoDest) {
        this.strApellidoDest = strApellidoDest;
    }

    public String getStrCompaniaDest() {
        return strCompaniaDest;
    }

    public void setStrCompaniaDest(String strCompaniaDest) {
        this.strCompaniaDest = strCompaniaDest;
    }

    public String getStrCiudad() {
        return strCiudad;
    }

    public void setStrCiudad(String strCiudad) {
        this.strCiudad = strCiudad;
    }

    public int getIntCodDest() {
        return intCodDest;
    }

    public void setIntCodDest(int intCodDest) {
        this.intCodDest = intCodDest;
    }

    public String getStrNombreDest() {
        return strNombreDest;
    }

    public void setStrNombreDest(String strNombreDest) {
        this.strNombreDest = strNombreDest;
    }

    public String getStrCedulaDest() {
        return strCedulaDest;
    }

    public void setStrCedulaDest(String strCedulaDest) {
        this.strCedulaDest = strCedulaDest;
    }

    public String getStrTelDest() {
        return strTelDest;
    }

    public void setStrTelDest(String strTelDest) {
        this.strTelDest = strTelDest;
    }

    public String getStrDireDest() {
        return strDireDest;
    }

    public void setStrDireDest(String strDireDest) {
        this.strDireDest = strDireDest;
    }

    public String getStrFechaDest() {
        return strFechaDest;
    }

    public void setStrFechaDest(String strFechaDest) {
        this.strFechaDest = strFechaDest;
    }
}
