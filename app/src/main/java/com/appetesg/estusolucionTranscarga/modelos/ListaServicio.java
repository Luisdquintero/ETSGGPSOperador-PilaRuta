package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 12/14/18.
 */

public class ListaServicio {
    private String numServicio,tipoServicio,horaServicio, horaLlegada, strFecha, strNombreP, strCelularP, strDireccionO, strDireccionD, strDescripcionR;
    private double latitud,longitud;
    private int intTiposervicio;
    public ListaServicio(String numServicio, String tipoServicio, String strFecha) {
        this.numServicio = numServicio;
        this.tipoServicio = tipoServicio;
        this.strFecha = strFecha;
    }

    public ListaServicio(String tipoServicio, double latitud, double longitud, String strFecha) {
        this.tipoServicio = tipoServicio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.strFecha = strFecha;
    }

    public ListaServicio(String numServicio, String strNombreP, String strCelularP, String strDireccionO,
                         String strDireccionD, String horaServicio, String horaLlegada, String strDescripcionR) {
        this.numServicio = numServicio;
        this.strNombreP = strNombreP;
        this.strCelularP =strCelularP;
        this.strDireccionO = strDireccionO;
        this.strDireccionD = strDireccionD;
        this.horaLlegada = horaLlegada;
        this.horaServicio = horaServicio;
        this.strDescripcionR = strDescripcionR;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNumServicio() {
        return numServicio;
    }

    public void setNumServicio(String numServicio) {
        this.numServicio = numServicio;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getHoraServicio() {
        return horaServicio;
    }

    public void setHoraServicio(String horaServicio) {
        this.horaServicio = horaServicio;
    }

    public String getHoraLlegada(){return  horaLlegada;}

    public void setHoraLlegada(String horaLlegada){this.horaLlegada = horaLlegada;}

    public String getStrFecha() {
        return strFecha;
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }

    public int getIntTiposervicio() {
        return intTiposervicio;
    }

    public void setIntTiposervicio(int intTiposervicio) {
        this.intTiposervicio = intTiposervicio;
    }

    public String getStrNombreP() {
        return strNombreP;
    }

    public void setStrNombreP(String strNombreP) {
        this.strNombreP = strNombreP;
    }

    public String getStrCelularP() {
        return strCelularP;
    }

    public void setStrCelularP(String strCelularP) {
        this.strCelularP = strCelularP;
    }

    public String getStrDireccionO() {
        return strDireccionO;
    }

    public void setStrDireccionO(String strDireccionO) {
        this.strDireccionO = strDireccionO;
    }

    public String getStrDireccionD() {
        return strDireccionD;
    }

    public void setStrDireccionD(String strDireccionD) {
        this.strDireccionD = strDireccionD;
    }

    public String getStrDescripcionR() {
        return strDescripcionR;
    }

    public void setStrDescripcionR(String strDescripcionR) {
        this.strDescripcionR = strDescripcionR;
    }
}


