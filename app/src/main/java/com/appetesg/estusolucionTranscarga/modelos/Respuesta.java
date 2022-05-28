package com.appetesg.estusolucionTranscarga.modelos;

public class Respuesta {
    private int idPregunta;
    private String respuesta;

    public Respuesta(int idPregunta, String respuesta) {
        this.idPregunta = idPregunta;
        this.respuesta = respuesta;
    }


    public int getIdPregunta() {
        return this.idPregunta;
    }

    public String getRespuesta() {
        return this.respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}