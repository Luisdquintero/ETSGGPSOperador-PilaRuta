package com.appetesg.estusolucionTranscarga.modelos;

public class ListaPlacas {

    private int id;
    private int idTercero;
    private int idVehiculo;
    private String strPlaca;

    public ListaPlacas(int idVehiculo, String strPlaca)
    {
        this.idVehiculo = idVehiculo;
        this.strPlaca = strPlaca;
    }

    public ListaPlacas(int id, int idTercero, int idVehiculo, String strPlaca)
    {
        this.id = id;
        this.idTercero = idTercero;
        this.idVehiculo = idVehiculo;
        this.strPlaca = strPlaca;
    }

    public int getId(){return  id;}

    public void setId(int id){this.id = id;}

    public int getIdTercero(){return  idTercero;}

    public void setIdTercero(int idTercero){this.idTercero = idTercero;}

    public int getIdVehiculo(){return idVehiculo;}

    public void setIdVehiculo(int idVehiculo){this.idVehiculo = idVehiculo;}

    public String getStrPlaca(){return strPlaca;}

    public void setStrPlaca(String strPlaca){this.strPlaca = strPlaca;}
}
