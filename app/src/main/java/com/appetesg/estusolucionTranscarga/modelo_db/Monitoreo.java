package com.appetesg.estusolucionTranscarga.modelo_db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by RafaelCastro on 11/18/18.
 */
/*
* request.addProperty("IdUsuario", IdUsuario);
            request.addProperty("Fecha", hoy);
            request.addProperty("Latitud", Latitud);
            request.addProperty("Longitud", Longitud);
            request.addProperty("Velocidad", Velocidad);
            request.addProperty("Distancia", Distancia);
* */
@Table(name="monitoreo", id="id")
public class Monitoreo extends Model {
    @Column(name="idUsuario")
    public int idUsuario;

    @Column(name="Fecha")
    public String Fecha;

    @Column(name="Latitud")
    public String Latitud;

    @Column(name="Longitud")
    public String Longitud;

    @Column(name="Velocidad")
    public String Velocidad;

    @Column(name="Distancia")
    public String Distancia;

    @Column(name="Enviado")
    public int Enviado;


}
