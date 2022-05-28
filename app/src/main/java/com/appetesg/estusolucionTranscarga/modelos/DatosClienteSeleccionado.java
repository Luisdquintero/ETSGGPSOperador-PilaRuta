package com.appetesg.estusolucionTranscarga.modelos;

public class DatosClienteSeleccionado {

    private String strNombreCli;
    private String strApellidoCli;
    private String strDirCli;
    private String strEmailCli;
    private String strCelCli;

    public DatosClienteSeleccionado(String strNombreCli, String strApellidoCli, String strDirCli, String strEmailCli,
                                    String strCelCli)
    {
        this.strNombreCli = strNombreCli;
        this.strApellidoCli = strApellidoCli;
        this.strDirCli = strDirCli;
        this.strEmailCli = strEmailCli;
        this.strCelCli = strCelCli;
    }

    public String getStrNombreCli() {
        return strNombreCli;
    }

    public void setStrNombreCli(String strNombreCli) {
        this.strNombreCli = strNombreCli;
    }

    public String getStrApellidoCli() {
        return strApellidoCli;
    }

    public void setStrApellidoCli(String strApellidoCli) {
        this.strApellidoCli = strApellidoCli;
    }

    public String getStrDirCli() {
        return strDirCli;
    }

    public void setStrDirCli(String strDirCli) {
        this.strDirCli = strDirCli;
    }

    public String getStrEmailCli() {
        return strEmailCli;
    }

    public void setStrEmailCli(String strEmailCli) {
        this.strEmailCli = strEmailCli;
    }

    public String getStrCelCli() {
        return strCelCli;
    }

    public void setStrCelCli(String strCelCli) {
        this.strCelCli = strCelCli;
    }
}
