package com.appetesg.estusolucionTranscarga.modelo_db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="usuariosColegio", id="id")
public class UsuariosColegio extends Model {
    @Column(name="idUsuario")
    public int idUsuario;
    @Column(name="idColegio")
    public int idColegio;
    @Column(name="usuario")
    public String usuario;
    @Column(name="clave")
    public String clave;
}
