package com.appetesg.estusolucionTranscarga.modelos;

public class Bono {

    private String strBono;
    private Boolean blUtilizado;
    private Boolean blReutilizable;

    public Bono(String strBono, Boolean blUtilizado, Boolean blReutilizable)
    {
        this.strBono = strBono;
        this.blUtilizado = blUtilizado;
        this.blReutilizable = blReutilizable;
    }

    public String getStrBono() {
        return strBono;
    }

    public void setStrBono(String strBono) {
        this.strBono = strBono;
    }

    public Boolean getBlUtilizado() {
        return blUtilizado;
    }

    public void setBlUtilizado(Boolean blUtilizado) {
        this.blUtilizado = blUtilizado;
    }

    public Boolean getBlReutilizable() {
        return blReutilizable;
    }

    public void setBlReutilizable(Boolean blReutilizable) {
        this.blReutilizable = blReutilizable;
    }
}
