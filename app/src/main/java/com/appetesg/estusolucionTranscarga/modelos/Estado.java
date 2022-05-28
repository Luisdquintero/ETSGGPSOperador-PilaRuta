package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 12/16/18.
 */

public class Estado {
    private String idEstado,nomEstado;

    public Estado(String idEstado, String nomEstado) {
        this.idEstado = idEstado;
        this.nomEstado = nomEstado;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getNomEstado() {
        return nomEstado;
    }

    public void setNomEstado(String nomEstado) {
        this.nomEstado = nomEstado;
    }
}
