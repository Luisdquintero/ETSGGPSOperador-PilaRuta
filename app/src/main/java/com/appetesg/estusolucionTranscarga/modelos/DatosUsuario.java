package com.appetesg.estusolucionTranscarga.modelos;

public class DatosUsuario {

    private int intCodusu;
    private String strNombre;
    private String strApellido;
    private String strCorreo;
    private String strTelefono;
    private String strImagen;
    private String strDocumento;
    private String strCiudad;
    private String strOficina;

    public DatosUsuario(int intCodusu, String strNombre, String strApellido, String strCorreo, String strTelefono,
                        String strImagen, String strDocumento, String strCiudad, String strOficina)
    {
        this.intCodusu = intCodusu;
        this.strNombre = strNombre;
        this.strApellido = strApellido;
        this.strCorreo = strCorreo;
        this.strTelefono = strTelefono;
        this.strImagen = strImagen;
        this.strDocumento = strDocumento;
        this.strCiudad = strCiudad;
        this.strOficina = strOficina;
    }

    public String getStrCiudad() {
        return strCiudad;
    }

    public void setStrCiudad(String strCiudad) {
        this.strCiudad = strCiudad;
    }

    public int getIntCodusu() {
        return intCodusu;
    }

    public void setIntCodusu(int intCodusu) {
        this.intCodusu = intCodusu;
    }

    public String getStrNombre() {
        return strNombre;
    }

    public void setStrNombre(String strNombre) {
        this.strNombre = strNombre;
    }

    public String getStrApellido() {
        return strApellido;
    }

    public void setStrApellido(String strApellido) {
        this.strApellido = strApellido;
    }

    public String getStrCorreo() {
        return strCorreo;
    }

    public void setStrCorreo(String strCorreo) {
        this.strCorreo = strCorreo;
    }

    public String getStrTelefono() {
        return strTelefono;
    }

    public void setStrTelefono(String strTelefono) {
        this.strTelefono = strTelefono;
    }

    public String getStrImagen() {
        return strImagen;
    }

    public void setStrImagen(String strImagen) {
        this.strImagen = strImagen;
    }

    public String getStrDocumento() {
        return strDocumento;
    }

    public void setStrDocumento(String strDocumento) {
        this.strDocumento = strDocumento;
    }

    public String getStrOficina() {
        return strOficina;
    }

    public void setStrOficina(String strOficina) {
        this.strOficina = strOficina;
    }
}
