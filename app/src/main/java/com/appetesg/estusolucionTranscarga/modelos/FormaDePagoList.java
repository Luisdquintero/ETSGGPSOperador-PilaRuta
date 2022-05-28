package com.appetesg.estusolucionTranscarga.modelos;

public class FormaDePagoList {

    private int intCodigoF;
    private String strNombreF;

    public FormaDePagoList(int intCodigoF, String strNombreF)
    {
        this.intCodigoF = intCodigoF;
        this.strNombreF = strNombreF;
    }

    public int getIntCodigoF() {
        return intCodigoF;
    }

    public void setIntCodigoF(int intCodigoF) {
        this.intCodigoF = intCodigoF;
    }

    public String getStrNombreF() {
        return strNombreF;
    }

    public void setStrNombreF(String strNombreF) {
        this.strNombreF = strNombreF;
    }
}
