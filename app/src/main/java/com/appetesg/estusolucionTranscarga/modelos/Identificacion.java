package com.appetesg.estusolucionTranscarga.modelos;

public class Identificacion {

    private int idTipoIdentificacion;
    private String strIdentificacion;

    public Identificacion(int idTipoIdentificacion, String strIdentificacion)
    {
        this.idTipoIdentificacion= idTipoIdentificacion;
        this.strIdentificacion = strIdentificacion;
    }


    public int getIdTipoIdentificacion() {
        return idTipoIdentificacion;
    }

    public void setIdTipoIdentificacion(int idTipoIdentificacion) {
        this.idTipoIdentificacion = idTipoIdentificacion;
    }

    public String getStrIdentificacion() {
        return strIdentificacion;
    }

    public void setStrIdentificacion(String strIdentificacion) {
        this.strIdentificacion = strIdentificacion;
    }
}
