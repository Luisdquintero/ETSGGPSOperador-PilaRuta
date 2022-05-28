package com.appetesg.estusolucionTranscarga.modelos;

import java.util.Date;

public class ListaRutaMonitor {
    private int id;
    private String codigoRuta,descripcion;
    private String estado;
    private Date fechaRegistro;
    private int idTercero;
    private String monitor;

    public ListaRutaMonitor(int id, String codigoRuta, String descripcion, String estado,
                            Date fechaRegistro, int idTercero, String monitor) {
        this.id = id;
        this.codigoRuta = codigoRuta;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.idTercero = idTercero;
        this.monitor = monitor;
    }

    public ListaRutaMonitor(int id, String codigoRuta, String descripcion) {
        this.id = id;
        this.codigoRuta = codigoRuta;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdTercero() {
        return idTercero;
    }

    public void setIdTercero(int idTercero) {
        this.idTercero = idTercero;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }
}
