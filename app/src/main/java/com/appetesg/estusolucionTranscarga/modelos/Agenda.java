package com.appetesg.estusolucionTranscarga.modelos;

public class Agenda {
    private int id;
    private String fechaInicio,fechaFin,observacion;
    private boolean cancelacion;

    public Agenda(int id, String fechaInicio, String fechaFin, String observacion, boolean cancelacion) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.observacion = observacion;
        this.cancelacion = cancelacion;
    }


    public boolean isCancelacion() {
        return cancelacion;
    }

    public void setCancelacion(boolean cancelacion) {
        this.cancelacion = cancelacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
