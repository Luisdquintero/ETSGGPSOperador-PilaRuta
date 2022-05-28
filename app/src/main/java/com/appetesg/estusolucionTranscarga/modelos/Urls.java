package com.appetesg.estusolucionTranscarga.modelos;

public class Urls {
    int id;
    String nombre,url, RutaFuec;
    Boolean blBluetooth;

    public Urls(int id, String nombre, String url, String RutaFuec, Boolean blBluetooth) {
        this.id = id;
        this.nombre = nombre;
        this.url = url;
        this.RutaFuec = RutaFuec;
        this.blBluetooth = blBluetooth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRutaFuec(){return RutaFuec;}
    public void setRutaFuec(String RutaFuec){this.RutaFuec = RutaFuec;}

    public Boolean getBlBluetooth() {
        return blBluetooth;
    }

    public void setBlBluetooth(Boolean blBluetooth) {
        this.blBluetooth = blBluetooth;
    }
}
