package com.appetesg.estusolucionTranscarga.modelos;

public class ListaProductos {
    private int idProducto;
    private String strProducto;

    public  ListaProductos(int idProducto, String strProducto)
    {
        this.idProducto = idProducto;
        this.strProducto = strProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getStrProducto() {
        return strProducto;
    }

    public void setStrProducto(String strProducto) {
        this.strProducto = strProducto;
    }
}
