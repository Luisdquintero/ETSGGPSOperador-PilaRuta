package com.appetesg.estusolucionTranscarga.modelos;

public class Pregunta {
    private int id;
    private String descripcion;
    private String respuesta;

    public Pregunta(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.respuesta = "false";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String strDescripcion) {
        this.descripcion = strDescripcion;
    }

    public String getRespuesta() {
        return this.respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

}
