package com.appetesg.estusolucionTranscarga.modelos;

public class ListaFuec {

    private int idContrtato;
    private int idTerceroCliente;
    private int intNumeroContrato;
    private int intContratoMaster;
    private String strObjeto;
    private String strContratante;
    private String strItinerario;

    public ListaFuec(int idContrtato, int idTerceroCliente, int intNumeroContrato, int intContratoMaster, String strObjeto, String strContratante, String strItinerario) {
        this.idContrtato = idContrtato;
        this.idTerceroCliente = idTerceroCliente;
        this.intNumeroContrato = intNumeroContrato;
        this.intContratoMaster = intContratoMaster;
        this.strObjeto = strObjeto;
        this.strContratante = strContratante;
        this.strItinerario = strItinerario;
    }

    public int getIdContrtato(){return idContrtato;}
    public void setIdContrtato(int idContrtato){this.idContrtato = idContrtato;}

    public int getIdTerceroCliente(){return  idTerceroCliente;}
    public void setIdTerceroCliente(int idTerceroCliente){this.idTerceroCliente = idTerceroCliente;}

    public int getIntNumeroContrato(){return  intNumeroContrato;}
    public void setIntNumeroContrato(int intNumeroContrato){this.intNumeroContrato = intNumeroContrato;}

    public int getIntContratoMaster(){return  intContratoMaster;}
    public void setIntContratoMaster(int intContratoMaster){this.intContratoMaster = intContratoMaster;}

    public String getStrObjeto(){return  strObjeto;}
    public void setStrObjeto(String strObjeto){this.strObjeto =strObjeto;}

    public String getStrContratante(){return  strContratante;}
    public void setStrContratante(String strContratante){this.strContratante = strContratante;}

    public String getStrItinerario(){return  strItinerario;}
    public void setStrItinerario(String strItinerario){this.strItinerario = strItinerario;}

}
