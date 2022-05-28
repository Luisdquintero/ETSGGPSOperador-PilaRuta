package com.appetesg.estusolucionTranscarga.modelos;

/**
 * Created by RafaelCastro on 8/4/18.
 */

public class Chat {
    String enviado,mensaje,hora;

    public Chat(String enviado, String mensaje, String hora) {
        this.enviado = enviado;
        this.mensaje = mensaje;
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
