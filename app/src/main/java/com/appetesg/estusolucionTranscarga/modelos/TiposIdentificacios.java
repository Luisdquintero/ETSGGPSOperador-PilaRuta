package com.appetesg.estusolucionTranscarga.modelos;

public class TiposIdentificacios {

    private int intId;
    private String strDescripcion;

    public TiposIdentificacios(int intId, String strDescripcion)
    {
        this.intId = intId;
        this.strDescripcion = strDescripcion;
    }

    public int getIntId() {
        return intId;
    }

    public void setIntId(int intId) {
        this.intId = intId;
    }

    public String getStrDescripcion() {
        return strDescripcion;
    }

    public void setStrDescripcion(String strDescripcion) {
        this.strDescripcion = strDescripcion;
    }
}
