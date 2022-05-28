package com.appetesg.estusolucionTranscarga.modelos;

public class ListaFiltro {

    private int intIdNota;
    private String strDescripcion;
    private int intCodigo;

    public ListaFiltro(int intIdNota, String strDescripcion, int intCodigo)
    {
        this.intIdNota = intIdNota;
        this.strDescripcion = strDescripcion;
        this.intCodigo = intCodigo;
    }

    public int getIntIdNota(){return intIdNota;}
    public void setIntIdNota(int intIdNota){this.intIdNota = intIdNota;}


    public String getStrDescripcion(){return strDescripcion;}
    public void setStrDescripcion(String strDescripcion){this.strDescripcion = strDescripcion;}

    public int getIntCodigo(){return  intCodigo;}
    public void setIntCodigo(int intCodigo){this.intCodigo = intCodigo;}


}
