package com.appetesg.estusolucionTranscarga.modelos;

public class ListaEnvios {

    private int idEnvio;
    private String strEnvio;

    public ListaEnvios(int idEnvio, String strEnvio){
        this.idEnvio = idEnvio;
        this.strEnvio = strEnvio;
    }

    public int getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getStrEnvio() {
        return strEnvio;
    }

    public void setStrEnvio(String strEnvio) {
        this.strEnvio = strEnvio;
    }
}
