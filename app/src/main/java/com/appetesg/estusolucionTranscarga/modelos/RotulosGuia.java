package com.appetesg.estusolucionTranscarga.modelos;

public class RotulosGuia {

    private int intCodusu;
    private int intCantidad;
    private int intCodCli;
    private String strPedido1;
    private String strCiudadDestino;
    private String strCiudadOrigen;
    private String strNomCli;
    private String strNomDest;
    private String strNomForPag;
    private String strDirOri;
    private String strDirDest;
    private String strValorEnvio;
    private String strValorGeneral;
    private String strFecha;
    private String strCelCli;
    private String strCelDes;
    private String strNomPrd;
    private String strContenido;
    private String strPesoPaq;
    private String strValDec;
    private String ImgEmbarque;
    private String strQR;
    private String strValorFlete;

    public RotulosGuia(int intCodusu, int intCantidad, String strPedido1, String strCiudadDestino, String strCiudadOrigen,
                       String strNomCli, String strNomDest, String strNomForPag, String strDirOri, String strDirDest, String strValorEnvio,
                       String strValorGeneral, String strFecha, String strCelcli, String strNomPrd, String strContenido, String strPesoPaq,
                       String strValDec, String ImgEmbarque, int intCodCli, String strCelDes, String strQR, String strValorFlete)
    {
        this.intCodusu = intCodusu;
        this.intCodCli = intCodCli;
        this.intCantidad =intCantidad;
        this.strPedido1 = strPedido1;
        this.strCiudadDestino= strCiudadDestino;
        this.strCiudadOrigen = strCiudadOrigen;
        this.strNomCli = strNomCli;
        this.strNomDest = strNomDest;
        this.strNomForPag = strNomForPag;
        this.strDirOri = strDirOri;
        this.strDirDest = strDirDest;
        this.strValorEnvio = strValorEnvio;
        this.strValorGeneral = strValorGeneral;
        this.strFecha = strFecha;
        this.strCelCli = strCelcli;
        this.strCelDes = strCelDes;
        this.strNomPrd = strNomPrd;
        this.strContenido = strContenido;
        this.strPesoPaq = strPesoPaq;
        this.strValDec = strValDec;
        this.ImgEmbarque = ImgEmbarque;
        this.strQR = strQR;
        this.strValorFlete = strValorFlete;
    }

    public String getStrValorFlete() {
        return strValorFlete;
    }

    public void setStrValorFlete(String strValorFlete) {
        this.strValorFlete = strValorFlete;
    }

    public String getStrQR() {
        return strQR;
    }

    public void setStrQR(String strQR) {
        this.strQR = strQR;
    }

    public RotulosGuia(String strContenido, String strPedido1) {
        this.strContenido = strContenido;
        this.strPedido1 = strPedido1;
    }

    public String getStrCelDes() {
        return strCelDes;
    }

    public void setStrCelDes(String strCelDes) {
        this.strCelDes = strCelDes;
    }

    public int getIntCodCli() {
        return intCodCli;
    }

    public void setIntCodCli(int intCodCli) {
        this.intCodCli = intCodCli;
    }

    public String getImgEmbarque() {
        return ImgEmbarque;
    }

    public void setImgEmbarque(String imgEmbarque) {
        ImgEmbarque = imgEmbarque;
    }

    public int getIntCodusu() {
        return intCodusu;
    }

    public void setIntCodusu(int intCodusu) {
        this.intCodusu = intCodusu;
    }

    public int getIntCantidad() {
        return intCantidad;
    }

    public void setIntCantidad(int intCantidad) {
        this.intCantidad = intCantidad;
    }

    public String getStrPedido1() {
        return strPedido1;
    }

    public void setStrPedido1(String strPedido1) {
        this.strPedido1 = strPedido1;
    }

    public String getStrCiudadDestino() {
        return strCiudadDestino;
    }

    public void setStrCiudadDestino(String strCiudadDestino) {
        this.strCiudadDestino = strCiudadDestino;
    }

    public String getStrCiudadOrigen() {
        return strCiudadOrigen;
    }

    public void setStrCiudadOrigen(String strCiudadOrigen) {
        this.strCiudadOrigen = strCiudadOrigen;
    }

    public String getStrNomCli() {
        return strNomCli;
    }

    public void setStrNomCli(String strNomCli) {
        this.strNomCli = strNomCli;
    }

    public String getStrNomDest() {
        return strNomDest;
    }

    public void setStrNomDest(String strNomDest) {
        this.strNomDest = strNomDest;
    }

    public String getStrNomForPag() {
        return strNomForPag;
    }

    public void setStrNomForPag(String strNomForPag) {
        this.strNomForPag = strNomForPag;
    }

    public String getStrDirOri() {
        return strDirOri;
    }

    public void setStrDirOri(String strDirOri) {
        this.strDirOri = strDirOri;
    }

    public String getStrDirDest() {
        return strDirDest;
    }

    public void setStrDirDest(String strDirDest) {
        this.strDirDest = strDirDest;
    }


    public String getStrValorEnvio() {
        return strValorEnvio;
    }

    public void setStrValorEnvio(String strValorEnvio) {
        this.strValorEnvio = strValorEnvio;
    }

    public String getStrValorGeneral() {
        return strValorGeneral;
    }

    public void setStrValorGeneral(String strValorGeneral) {
        this.strValorGeneral = strValorGeneral;
    }

    public String getStrFecha() {
        return strFecha;
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }

    public String getStrCelCli() {
        return strCelCli;
    }

    public void setStrCelCli(String strCelCli) {
        this.strCelCli = strCelCli;
    }

    public String getStrNomPrd() {
        return strNomPrd;
    }

    public void setStrNomPrd(String strNomPrd) {
        this.strNomPrd = strNomPrd;
    }

    public String getStrContenido() {
        return strContenido;
    }

    public void setStrContenido(String strContenido) {
        this.strContenido = strContenido;
    }

    public String getStrPesoPaq() {
        return strPesoPaq;
    }

    public void setStrPesoPaq(String strPesoPaq) {
        this.strPesoPaq = strPesoPaq;
    }

    public String getStrValDec() {
        return strValDec;
    }

    public void setStrValDec(String strValDec) {
        this.strValDec = strValDec;
    }
}
