package com.appetesg.estusolucionTranscarga.modelos;

public class CiudadesD {

    private String strCodCiuDe;
    private String strNomCiuDe;
    private String strOficina;

    public  CiudadesD(String strCodCiuDe, String strNomCiuDe)
    {
        this.strCodCiuDe = strCodCiuDe;
        this.strNomCiuDe = strNomCiuDe;
    }

    public  CiudadesD(String strCodCiuDe, String strNomCiuDe, String strOficina)
    {
        this.strCodCiuDe = strCodCiuDe;
        this.strNomCiuDe = strNomCiuDe;
        this.strOficina = strOficina;
    }

    public String getStrCodCiuDe() {
        return strCodCiuDe;
    }

    public void setStrCodCiuDe(String strCodCiuDe) {
        this.strCodCiuDe = strCodCiuDe;
    }

    public String getStrNomCiuDe() {
        return strNomCiuDe;
    }

    public void setStrNomCiuDe(String strNomCiuDe) {
        this.strNomCiuDe = strNomCiuDe;
    }

    public String getStrOficina() {
        return strOficina;
    }

    public void setStrOficina(String strOficina) {
        this.strOficina = strOficina;
    }
}
