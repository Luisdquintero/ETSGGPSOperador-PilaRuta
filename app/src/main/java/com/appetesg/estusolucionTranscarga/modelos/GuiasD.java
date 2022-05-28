package com.appetesg.estusolucionTranscarga.modelos;

public class GuiasD {

    private String strGuia;
    private String strDescripcionP;
    private String strDestinatario;
    private String strValor;
    private String strPeso;
    private String strDireccionDe;
    private String strRemitente;
    private String strProducto;
    private String strDireccionRe;
    private String strCelular;
    private String remitente;
    private String teldes;
    private String dircli;
    private String nomprd;
    private String pedido1;
    private String strNomForPag;


    public GuiasD(String strGuia, String strDescripcionP, String strDestinatario, String strValor, String strPeso, String strDireccionDe,String remitente,String teldes,String dircli,String nomprd,String pedido1, String nomforpag)
    {
        this.remitente = remitente;
        this.pedido1 = pedido1;
        this.teldes = teldes;
        this.dircli = dircli;
        this.nomprd = nomprd;
        this.strGuia = strGuia;
        this.strDescripcionP = strDescripcionP;
        this.strDestinatario = strDestinatario;
        this.strValor = strValor;
        this.strPeso = strPeso;
        this.strDireccionDe = strDireccionDe;
        this.strNomForPag = nomforpag;
    }

    public GuiasD(String strGuia, String strDescripcionP, String strDestinatario, String strValor, String strPeso, String strDireccionDe,String remitente,String teldes,String dircli)
    {
        this.remitente = remitente;
        this.pedido1 = "";
        this.teldes = teldes;
        this.dircli = dircli;
        this.nomprd = "";
        this.strGuia = strGuia;
        this.strDescripcionP = strDescripcionP;
        this.strDestinatario = strDestinatario;
        this.strValor = strValor;
        this.strPeso = strPeso;
        this.strDireccionDe = strDireccionDe;
    }

    public GuiasD(String strGuia, String strDescripcionP, String strDestinatario, String strValor, String strPeso, String strDireccionDe, String remitente, String teldes, String dircli,String Producto,String nomprd)
    {
        this.remitente = remitente;
        this.strProducto = Producto;
        this.strCelular = teldes;
        this.dircli = dircli;
        this.strNomForPag = nomprd;
        this.strGuia = strGuia;
        this.strDescripcionP = strDescripcionP;
        this.strDestinatario = strDestinatario;
        this.strValor = strValor;
        this.strPeso = strPeso;
        this.strDireccionDe = strDireccionDe;
    }

    public GuiasD(String strGuia, String strDescripcionP, String strDestinatario, String strValor, String strPeso,
                  String strDireccionDe, String strRemitente, String strCelular, String strProducto, String strDireccionRe)
    {
        this.strGuia = strGuia;
        this.strDescripcionP = strDescripcionP;
        this.strDestinatario = strDestinatario;
        this.strValor = strValor;
        this.strPeso = strPeso;
        this.strDireccionDe = strDireccionDe;
        this.strRemitente = strRemitente;
        this.strCelular = strCelular;
        this.strProducto = strProducto;
        this.strDireccionRe = strDireccionRe;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getTeldes() {
        return teldes;
    }

    public void setTeldes(String teldes) {
        this.teldes = teldes;
    }

    public String getDircli() {
        return dircli;
    }

    public void setDircli(String dircli) {
        this.dircli = dircli;
    }

    public String getNomprd() {
        return nomprd;
    }

    public void setNomprd(String nomprd) {
        this.nomprd = nomprd;
    }

    public String getPedido1() {
        return pedido1;
    }

    public void setPedido1(String pedido1) {
        this.pedido1 = pedido1;
    }

    public String getStrNomForPag() {
        return strNomForPag;
    }

    public void setStrNomForPag(String strNomForPag) {
        this.strNomForPag = strNomForPag;
    }

    public String getpedido1() {
        return pedido1;
    }

    public void setpedido1(String pedido1) {
        this.pedido1 = pedido1;
    }

    public String getStrDireccionDe() {
        return strDireccionDe;
    }

    public void setStrDireccionDe(String strDireccionDe) {
        this.strDireccionDe = strDireccionDe;
    }

    public String getStrGuia() {
        return strGuia;
    }

    public void setStrGuia(String strGuia) {
        this.strGuia = strGuia;
    }

    public String getStrDescripcionP() {
        return strDescripcionP;
    }

    public void setStrDescripcionP(String strDescripcionP) {
        this.strDescripcionP = strDescripcionP;
    }

    public String getStrDestinatario() {
        return strDestinatario;
    }

    public void setStrDestinatario(String strDestinatario) {
        this.strDestinatario = strDestinatario;
    }

    public String getStrValor() {
        return strValor;
    }

    public void setStrValor(String strValor) {
        this.strValor = strValor;
    }

    public String getStrPeso() {
        return strPeso;
    }

    public void setStrPeso(String strPeso) {
        this.strPeso = strPeso;
    }

    public String getStrRemitente() {
        return strRemitente;
    }

    public void setStrRemitente(String strRemitente) {
        this.strRemitente = strRemitente;
    }

    public String getStrProducto() {
        return strProducto;
    }

    public void setStrProducto(String strProducto) {
        this.strProducto = strProducto;
    }

    public String getStrDireccionRe() {
        return strDireccionRe;
    }

    public void setStrDireccionRe(String strDireccionRe) {
        this.strDireccionRe = strDireccionRe;
    }

    public String getStrCelular() {
        return strCelular;
    }

    public void setStrCelular(String strCelular) {
        this.strCelular = strCelular;
    }

    public String getremitente() {
        return remitente;
    }

    public void setremitente(String remitente) {
        this.remitente = remitente;
    }

    public String getteldes() {
        return teldes;
    }

    public void setteldes(String teldes) {
        this.teldes = teldes;
    }
    public String getdircli() {
        return dircli;
    }

    public void setdircli(String dircli) {
        this.dircli = dircli;
    }

    public String getnomprd() {
        return nomprd;
    }

    public void setnomprd(String nomprd) {
        this.nomprd = nomprd;
    }
}
