package com.appetesg.estusolucionTranscarga.modelos;

public class ValorTarifa {

    private String strSubImpuesto;
    private String strTotalImpuestos;
    private String strTotalPagar;
    private String strTotalConBono;
    private String strSubImpuestoEn;
    private String strTotalImpuestoEn;
    private String strTotalPagarEn;
    private String strTotalConBonoEn;

    public ValorTarifa(String strSubImpuesto, String strTotalImpuestos, String strTotalPagar, String strTotalConBono){
        this.strSubImpuesto = strSubImpuesto;
        this.strTotalImpuestos = strTotalImpuestos;
        this.strTotalPagar = strTotalPagar;
        this.strTotalConBono = strTotalConBono;
    }


    public ValorTarifa(String strSubImpuesto, String strTotalImpuestos, String strTotalPagar, String strTotalConBono,
                       String strSubImpuestoEn, String strTotalImpuestoEn, String strTotalPagarEn, String strTotalConBonoEn){
        this.strSubImpuesto = strSubImpuesto;
        this.strTotalImpuestos = strTotalImpuestos;
        this.strTotalPagar = strTotalPagar;
        this.strTotalConBono = strTotalConBono;
        this.strSubImpuestoEn = strSubImpuestoEn;
        this.strTotalImpuestoEn = strTotalImpuestoEn;
        this.strTotalPagarEn = strTotalPagarEn;
        this.strTotalConBonoEn = strTotalConBonoEn;
    }

    public String getStrTotalConBono() {
        return strTotalConBono;
    }

    public void setStrTotalConBono(String strTotalConBono) {
        this.strTotalConBono = strTotalConBono;
    }

    public String getStrSubImpuesto() {
        return strSubImpuesto;
    }

    public void setStrSubImpuesto(String strSubImpuesto) {
        this.strSubImpuesto = strSubImpuesto;
    }

    public String getStrTotalImpuestos() {
        return strTotalImpuestos;
    }

    public void setStrTotalImpuestos(String strTotalImpuestos) {
        this.strTotalImpuestos = strTotalImpuestos;
    }

    public String getStrTotalPagar() {
        return strTotalPagar;
    }

    public void setStrTotalPagar(String strTotalPagar) {
        this.strTotalPagar = strTotalPagar;
    }

    public String getStrSubImpuestoEn() {
        return strSubImpuestoEn;
    }

    public void setStrSubImpuestoEn(String strSubImpuestoEn) {
        this.strSubImpuestoEn = strSubImpuestoEn;
    }

    public String getStrTotalImpuestoEn() {
        return strTotalImpuestoEn;
    }

    public void setStrTotalImpuestoEn(String strTotalImpuestoEn) {
        this.strTotalImpuestoEn = strTotalImpuestoEn;
    }

    public String getStrTotalPagarEn() {
        return strTotalPagarEn;
    }

    public void setStrTotalPagarEn(String strTotalPagarEn) {
        this.strTotalPagarEn = strTotalPagarEn;
    }

    public String getStrTotalConBonoEn() {
        return strTotalConBonoEn;
    }

    public void setStrTotalConBonoEn(String strTotalConBonoEn) {
        this.strTotalConBonoEn = strTotalConBonoEn;
    }
}
