package com.appetesg.estusolucionTranscarga.modelos;

public class ListaEstudiantes {
    String nombreEstudiante,nomEstado;
    int id,codEst;

    public ListaEstudiantes(String nombreEstudiante, int id, int codEst, String nomEstado) {
        this.nombreEstudiante = nombreEstudiante;
        this.id = id;
        this.codEst = codEst;
        this.nomEstado = nomEstado;
    }

    public String getNomEstado() {
        return nomEstado;
    }

    public void setNomEstado(String nomEstado) {
        this.nomEstado = nomEstado;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodEst() {
        return codEst;
    }

    public void setCodEst(int codEst) {
        this.codEst = codEst;
    }
}
