package com.appetesg.estusolucionTranscarga.modelos;

public class DatosReserva {

    private String numServi,tipoServi,horaServi, horaLlegadaServi, strFechaServi,
            strNombreServi, strCelularServi, strDireccionOServi, strDireccionDServi, strDescripcionRServi;
    private double latitudServi,longitudServi;
    private int intTiposervi;

    public DatosReserva(String numServi, String strNombreServi, String strCelularServi, String strDireccionOServi,
                               String strDireccionDServi, String horaServi, String horaLlegadaServi, String strDescripcionRServi) {
        this.numServi = numServi;
        this.strNombreServi = strNombreServi;
        this.strCelularServi =strCelularServi;
        this.strDireccionOServi = strDireccionOServi;
        this.strDireccionDServi = strDireccionDServi;
        this.horaLlegadaServi = horaLlegadaServi;
        this.horaServi = horaServi;
        this.strDescripcionRServi = strDescripcionRServi;
    }

    public String getNumServi() {
        return numServi;
    }

    public void setNumServi(String numServi) {
        this.numServi = numServi;
    }

    public String getTipoServi() {
        return tipoServi;
    }

    public void setTipoServi(String tipoServi) {
        this.tipoServi = tipoServi;
    }

    public String getHoraServi() {
        return horaServi;
    }

    public void setHoraServi(String horaServi) {
        this.horaServi = horaServi;
    }

    public String getHoraLlegadaServi() {
        return horaLlegadaServi;
    }

    public void setHoraLlegadaServi(String horaLlegadaServi) {
        this.horaLlegadaServi = horaLlegadaServi;
    }

    public String getStrFechaServi() {
        return strFechaServi;
    }

    public void setStrFechaServi(String strFechaServi) {
        this.strFechaServi = strFechaServi;
    }

    public String getStrNombreServi() {
        return strNombreServi;
    }

    public void setStrNombreServi(String strNombreServi) {
        this.strNombreServi = strNombreServi;
    }

    public String getStrCelularServi() {
        return strCelularServi;
    }

    public void setStrCelularServi(String strCelularServi) {
        this.strCelularServi = strCelularServi;
    }

    public String getStrDireccionOServi() {
        return strDireccionOServi;
    }

    public void setStrDireccionOServi(String strDireccionOServi) {
        this.strDireccionOServi = strDireccionOServi;
    }

    public String getStrDireccionDServi() {
        return strDireccionDServi;
    }

    public void setStrDireccionDServi(String strDireccionDServi) {
        this.strDireccionDServi = strDireccionDServi;
    }

    public String getStrDescripcionRServi() {
        return strDescripcionRServi;
    }

    public void setStrDescripcionRServi(String strDescripcionRServi) {
        this.strDescripcionRServi = strDescripcionRServi;
    }

    public double getLatitudServi() {
        return latitudServi;
    }

    public void setLatitudServi(double latitudServi) {
        this.latitudServi = latitudServi;
    }

    public double getLongitudServi() {
        return longitudServi;
    }

    public void setLongitudServi(double longitudServi) {
        this.longitudServi = longitudServi;
    }

    public int getIntTiposervi() {
        return intTiposervi;
    }

    public void setIntTiposervi(int intTiposervi) {
        this.intTiposervi = intTiposervi;
    }
}
