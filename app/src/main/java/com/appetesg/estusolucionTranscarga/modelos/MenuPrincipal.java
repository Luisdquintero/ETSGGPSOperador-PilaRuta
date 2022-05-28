package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 6/10/17.
 */

public class MenuPrincipal {
    int id, icono;
    String nombre;

    public MenuPrincipal(int id, int icono, String nombre) {
        this.icono = icono;
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
