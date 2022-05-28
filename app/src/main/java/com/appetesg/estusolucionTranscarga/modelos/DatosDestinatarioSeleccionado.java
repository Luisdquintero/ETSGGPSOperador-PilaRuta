package com.appetesg.estusolucionTranscarga.modelos;

public class DatosDestinatarioSeleccionado {

    private String strNombreDest;
    private String strApellidoDest;
    private String strDireccionDest;
    private String strEmailDest;
    private String strCelularDest;

    public DatosDestinatarioSeleccionado(String strNombreDest, String strApellidoDest, String strDireccionDest, String strEmailDest,
                                         String strCelularDest)
    {
        this.strNombreDest = strNombreDest;
        this.strApellidoDest = strApellidoDest;
        this.strDireccionDest = strDireccionDest;
        this.strEmailDest = strEmailDest;
        this.strCelularDest = strCelularDest;
    }

    public String getStrNombreDest() {
        return strNombreDest;
    }

    public void setStrNombreDest(String strNombreDest) {
        this.strNombreDest = strNombreDest;
    }

    public String getStrApellidoDest() {
        return strApellidoDest;
    }

    public void setStrApellidoDest(String strApellidoDest) {
        this.strApellidoDest = strApellidoDest;
    }

    public String getStrDireccionDest() {
        return strDireccionDest;
    }

    public void setStrDireccionDest(String strDireccionDest) {
        this.strDireccionDest = strDireccionDest;
    }

    public String getStrEmailDest() {
        return strEmailDest;
    }

    public void setStrEmailDest(String strEmailDest) {
        this.strEmailDest = strEmailDest;
    }

    public String getStrCelularDest() {
        return strCelularDest;
    }

    public void setStrCelularDest(String strCelularDest) {
        this.strCelularDest = strCelularDest;
    }
}
