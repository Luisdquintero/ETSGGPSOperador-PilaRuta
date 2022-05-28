package com.appetesg.estusolucionTranscarga.modelos;

public class Producto {

    private int intCodPrd;
    private String strNomPrd;

    public Producto(int intCodPrd, String strNomPrd)
    {
        this.intCodPrd = intCodPrd;
        this.strNomPrd = strNomPrd;
    }

    public int getIntCodPrd() {
        return intCodPrd;
    }

    public void setIntCodPrd(int intCodPrd) {
        this.intCodPrd = intCodPrd;
    }

    public String getStrNomPrd() {
        return strNomPrd;
    }

    public void setStrNomPrd(String strNomPrd) {
        this.strNomPrd = strNomPrd;
    }
}


