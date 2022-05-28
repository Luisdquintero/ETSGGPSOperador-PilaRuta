package com.appetesg.estusolucionTranscarga.modelos;

public class ListaCiudadDestino {

    private String strCodciu;
    private String strNomCiu;

    public ListaCiudadDestino(String strCodciu, String strNomCiu)
    {
        this.strCodciu = strCodciu;
        this.strNomCiu = strNomCiu;
    }

    public String getStrCodciu() {
        return strCodciu;
    }

    public void setStrCodciu(String strCodciu) {
        this.strCodciu = strCodciu;
    }

    public String getStrNomCiu() {
        return strNomCiu;
    }

    public void setStrNomCiu(String strNomCiu) {
        this.strNomCiu = strNomCiu;
    }
}
