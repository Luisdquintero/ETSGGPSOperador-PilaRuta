package com.appetesg.estusolucionTranscarga.modelos;

public class ListaClientesCorp {

    private int intCodcli;
    private String strNomcli;

    public ListaClientesCorp (int intCodcli, String strNomcli){
        this.intCodcli = intCodcli;
        this.strNomcli = strNomcli;
    }

    public int getIntCodcli() {
        return intCodcli;
    }

    public void setIntCodcli(int intCodcli) {
        this.intCodcli = intCodcli;
    }

    public String getStrNomcli() {
        return strNomcli;
    }

    public void setStrNomcli(String strNomcli) {
        this.strNomcli = strNomcli;
    }
}
