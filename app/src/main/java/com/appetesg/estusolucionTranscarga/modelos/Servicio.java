package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 12/14/18.
 */

public class Servicio {
    private int idUsuario;
    private String fecha,lat,lng,estado,codigo;
    private int estados;
    private String srtRecibido,srtImagen;

    public Servicio(int idUsuario, String fecha, String lat, String lng, String estado,
                    String codigo, int estados, String srtRecibido, String srtImagen) {
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.lat = lat;
        this.lng = lng;
        this.estado = estado;
        this.codigo = codigo;
        this.estados = estados;
        this.srtRecibido = srtRecibido;
        this.srtImagen = srtImagen;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getEstados() {
        return estados;
    }

    public void setEstados(int estados) {
        this.estados = estados;
    }

    public String getSrtRecibido() {
        return srtRecibido;
    }

    public void setSrtRecibido(String srtRecibido) {
        this.srtRecibido = srtRecibido;
    }

    public String getSrtImagen() {
        return srtImagen;
    }

    public void setSrtImagen(String srtImagen) {
        this.srtImagen = srtImagen;
    }
}
