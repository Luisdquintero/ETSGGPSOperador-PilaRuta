package com.appetesg.estusolucionTranscarga.modelos;

public class ClientesR {

    private String strNomCli;
    private int intCodCli;
    private String strCedula;
    private String strFecha;
    private String strDireccion;
    private String strTelcli;
    private String strCelCli;
    private String strApellido;
    private String strCompania;
    private String strCorreo;
    private String strCodCiu;
    private int intCorp;

    public ClientesR(String strNomCli, int intCodCli, String strCedula, String strFecha,String strDireccion, String strTelCli, String strCelCli, int intCorp)
    {
        this.strNomCli = strNomCli;
        this.intCodCli = intCodCli;
        this.strCedula = strCedula;
        this.strFecha = strFecha;
        this.strDireccion = strDireccion;
        this.strTelcli = strTelCli;
        this.strCelCli = strCelCli;
        this.intCorp = intCorp;
    }

    public ClientesR(int inCodId, String strNomCli, int intCodCli, String strCedula, String strDireccion,
                     String strTelcli, String strCorreo, String strCompania, String strApellido)
    {
        this.intCorp = inCodId;
        this.strNomCli = strNomCli;
        this.intCodCli = intCodCli;
        this.strCedula = strCedula;
        this.strDireccion = strDireccion;
        this.strTelcli = strTelcli;
        this.strCorreo = strCorreo;
        this.strCompania = strCompania;
        this.strApellido = strApellido;
    }

    public  ClientesR(String strNomCli, int intCodCli)
    {
        this.strNomCli = strNomCli;
        this.intCodCli = intCodCli;
    }

    public  ClientesR()
    {
        this.intCodCli = 0;
        this.strCompania = "";
        this.strCelCli = "";
        this.strDireccion = "";
        this.strCedula = "";
    }

    public ClientesR(String strNomCli, int intCodCli, String strCedula, String strFecha,
                     String strDireccion, String strTelCli, String strCelCli, String strCodCiu,
                     int intCorp, String strApellido, String strCompania){
        this.strNomCli = strNomCli;
        this.intCodCli = intCodCli;
        this.strCedula = strCedula;
        this.strFecha = strFecha;
        this.strDireccion = strDireccion;
        this.strTelcli = strTelCli;
        this.strCelCli = strCelCli;
        this.strCodCiu = strCodCiu;
        this.intCorp = intCorp;
        this.strApellido = strApellido;
        this.strCompania = strCompania;
    }

    public String getStrCodCiu() {
        return strCodCiu;
    }

    public void setStrCodCiu(String strCodCiu) {
        this.strCodCiu = strCodCiu;
    }

    public String getStrNomCli() {
        return strNomCli;
    }

    public void setStrNomCli(String strNomCli) {
        this.strNomCli = strNomCli;
    }

    public int getIntCodCli() {
        return intCodCli;
    }

    public void setIntCodCli(int intCodCli) {
        this.intCodCli = intCodCli;
    }

    public String getStrCedula() {
        return strCedula;
    }

    public void setStrCedula(String strCedula) {
        this.strCedula = strCedula;
    }

    public String getStrFecha() {
        return strFecha;
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }

    public String getStrDireccion() {
        return strDireccion;
    }

    public void setStrDireccion(String strDireccion) {
        this.strDireccion = strDireccion;
    }

    public String getStrTelcli() {
        return strTelcli;
    }

    public void setStrTelcli(String strTelcli) {
        this.strTelcli = strTelcli;
    }

    public String getStrCelCli() {
        return strCelCli;
    }

    public void setStrCelCli(String strCelCli) {
        this.strCelCli = strCelCli;
    }

    public String getStrApellido() {
        return strApellido;
    }

    public void setStrApellido(String strApellido) {
        this.strApellido = strApellido;
    }

    public String getStrCompania() {
        return strCompania;
    }

    public void setStrCompania(String strCompania) {
        this.strCompania = strCompania;
    }

    public String getStrCorreo() {
        return strCorreo;
    }

    public void setStrCorreo(String strCorreo) {
        this.strCorreo = strCorreo;
    }

    public int getIntCorp() {
        return intCorp;
    }

    public void setIntCorp(int intCorp) {
        this.intCorp = intCorp;
    }
}
