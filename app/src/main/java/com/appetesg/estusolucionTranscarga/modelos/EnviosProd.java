package com.appetesg.estusolucionTranscarga.modelos;

public class EnviosProd {

    private int intCodTienv;
    private String strNomEnv;

    public EnviosProd(int intCodTienv, String strNomEnv)
    {
        this.intCodTienv = intCodTienv;
        this.strNomEnv = strNomEnv;
    }

    public int getIntCodTienv() {
        return intCodTienv;
    }

    public void setIntCodTienv(int intCodTienv) {
        this.intCodTienv = intCodTienv;
    }

    public String getStrNomEnv() {
        return strNomEnv;
    }

    public void setStrNomEnv(String strNomEnv) {
        this.strNomEnv = strNomEnv;
    }
}


