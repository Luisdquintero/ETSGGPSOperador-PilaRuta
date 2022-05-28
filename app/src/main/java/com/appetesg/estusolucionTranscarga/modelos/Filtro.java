package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 12/15/18.
 */

public class Filtro {
    private int idFiltro;
    private String filtroNombre;

    public Filtro(int idFiltro, String filtroNombre) {
        this.idFiltro = idFiltro;
        this.filtroNombre = filtroNombre;
    }

    public int getIdFiltro() {
        return idFiltro;
    }

    public void setIdFiltro(int idFiltro) {
        this.idFiltro = idFiltro;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }
}
