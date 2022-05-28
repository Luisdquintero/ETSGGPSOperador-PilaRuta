package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.adapter.TiposIdentificacionAdapter;
import com.appetesg.estusolucionTranscarga.modelos.ClientesR;
import com.appetesg.estusolucionTranscarga.modelos.TiposIdentificacios;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class RegistroDestinatario extends AppCompatActivity {

    TiposIdentificacionAdapter mTiposIdentidicacion;
    EditText edCompania, edNombreDest, edApellidoDest, edDirDest, edCoreleDest, edCelDest, edDocumentoDest;
    Button btnContinuar2, btnActualizar;
    TextView edTitleCliente, edTitleTotal;
    Spinner spClientes;
    ImageButton imgButton;
    SharedPreferences sharedPreferences;
    String strVacio = null, strCompanhiaCli, strCiudadDest, strMensaje;
    ArrayList<ClientesR> dtClientesDest = new ArrayList<>();
    ClientesR clientesDest;
    int intCodcli = 0;
    int intCodusu = 0;
    int intCodDestinario = 0;
    private static final String ACTION_CREAR_DESTINATARIO= "CrearDestinatario";
    private static final String ACTION_DATOS_DESTINATARIO = "DatosClienteCorpDestinatario";
    private static final String ACTION_ACTUALIZAR_DESTINATARIO = "ActualizarClienteDestinatario";
    private static final String ACTION_VALIDAR_DOCUMENTO= "ValidarDocumento";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "CrearDestinatario";
    String BASE_URL, PREFS_NAME, strTipo;

    @Override
    public void onBackPressed() {
        //NO HACE NADA AL OPRIMIR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // controlar las excepciones cuando se cierra la app
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread thread, Throwable e){
                excepcionCapturada(thread, e);
            }
        });
        setContentView(R.layout.activity_registro_destinatario);

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        intCodusu = sharedPreferences.getInt("idUsuario",0);
        intCodcli = sharedPreferences.getInt("intCodCliN", 0);
        intCodDestinario = sharedPreferences.getInt("intCodDestinatario", 0);
        edNombreDest = (EditText)findViewById(R.id.txtNombreDest);
        edApellidoDest = (EditText)findViewById(R.id.txtApellidoDest);
        edDirDest = (EditText)findViewById(R.id.txtDirDest);
        edCoreleDest = (EditText)findViewById(R.id.txtEmailDest);
        edCelDest = (EditText)findViewById(R.id.txtCelularDest);
        edDocumentoDest = (EditText)findViewById(R.id.txtDocumentoDest);
        edCompania = (EditText)findViewById(R.id.txtCompaniaCli2);
        btnContinuar2 = (Button)findViewById(R.id.btnContinuarDest);
        btnActualizar= (Button)findViewById(R.id.btnActualizarC2);
        spClientes = (Spinner)findViewById(R.id.lstDocumento2);

        imgButton = (ImageButton)findViewById(R.id.btnReturnDesription);
        strCiudadDest = sharedPreferences.getString("strCodCiuDest", "");

        TiposIdentificacion();

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroDestinatario.this, ListaDestinatarios.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        edDocumentoDest.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override public void onFocusChange(View v, boolean hasFocus)
            { // When focus is lost check that the text field * has valid values.
                if (!hasFocus && !edDocumentoDest.getText().toString().isEmpty())
                {
                    new ValidarDocumento(edDocumentoDest.getText().toString().trim()).execute();
                }
            }
        });*/

        spClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = spClientes.getItemAtPosition(i);
                TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                strTipo = String.valueOf(ListaIndentificaciones.getIntId());
                if(strTipo.equalsIgnoreCase("1")){ //TIPO DOC. CEDULA

                    edCompania.setVisibility(View.GONE);
                    edNombreDest.setVisibility(View.VISIBLE);
                    edApellidoDest.setVisibility(View.VISIBLE);
                }else{ // TIPO DOC. NIT

                    edNombreDest.setVisibility(View.GONE);
                    edApellidoDest.setVisibility(View.GONE);
                    edCompania.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(intCodDestinario > 0)
        {
            btnActualizar.setVisibility(View.VISIBLE);
            btnContinuar2.setVisibility(View.GONE);
            new DatosClientesAsyncTask(intCodDestinario).execute();
        }
        else
        {
            btnActualizar.setVisibility(View.GONE);
            btnContinuar2.setVisibility(View.VISIBLE);
        }


        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((strTipo.equalsIgnoreCase("1") && edNombreDest.getText().length() > 2 && edApellidoDest.getText().length() > 2
                        && edCelDest.getText().length() > 0 && edDirDest.getText().length() > 0) ||
                        (strTipo.equalsIgnoreCase("4") && edCompania.getText().length() > 2
                                && edCelDest.getText().length() > 0 && edDirDest.getText().length() > 0)) {

                    if(NetworkUtil.hayInternet(RegistroDestinatario.this))
                    {
                        if(edDocumentoDest.getText().toString().trim().length()==0)
                            edDocumentoDest.setText("0");
                        Object o = spClientes.getSelectedItem();
                        TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                        new ActualizarClienteAsyncTask(ListaIndentificaciones.getIntId(), edNombreDest.getText().toString().toUpperCase().trim(), edApellidoDest.getText().toString().toUpperCase().trim(),
                                edDirDest.getText().toString().toUpperCase().trim(), edDocumentoDest.getText().toString().toUpperCase().trim(), edCelDest.getText().toString().toUpperCase().trim(),
                                edCoreleDest.getText().toString().toUpperCase().trim(), intCodDestinario, edCompania.getText().toString().toUpperCase().trim()).execute();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT).show();
                }
                else
                    validacionCampos();
            }
        });

        btnContinuar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((strTipo.equalsIgnoreCase("1") && edNombreDest.getText().length() > 2 && edApellidoDest.getText().length() > 2
                        && edCelDest.getText().length() > 0 && edDirDest.getText().length() > 0) ||
                        (strTipo.equalsIgnoreCase("4") && edCompania.getText().length() > 2
                                && edCelDest.getText().length() > 0 && edDirDest.getText().length() > 0)) {

                    if(NetworkUtil.hayInternet(RegistroDestinatario.this))
                    {
                        if(edDocumentoDest.getText().toString().trim().length()==0)
                            edDocumentoDest.setText("0");
                        Object o = spClientes.getSelectedItem();
                        TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                        new CrearDestinatarioAsyncTask(intCodcli, edNombreDest.getText().toString().toUpperCase().trim(), edApellidoDest.getText().toString().toUpperCase().trim(),
                                edDirDest.getText().toString().toUpperCase().trim(), edDocumentoDest.getText().toString().toUpperCase().trim(), edCelDest.getText().toString().toUpperCase().trim(),
                                edCoreleDest.getText().toString().toUpperCase().trim(), strCiudadDest, edCompania.getText().toString().toUpperCase().trim(),ListaIndentificaciones.getIntId()).execute();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT).show();
                }
                else
                    validacionCampos();
            }
        });

    }

    public void TiposIdentificacion()
    {
        ArrayList<TiposIdentificacios> aTiposIde = new ArrayList<>();
        aTiposIde.add(new TiposIdentificacios(1,"Cedula"));
        aTiposIde.add(new TiposIdentificacios(4,"NIT"));
        mTiposIdentidicacion = new TiposIdentificacionAdapter(RegistroDestinatario.this, aTiposIde);
        spClientes.setAdapter(mTiposIdentidicacion);
    }

    public AlertDialog dialogInformativo(String strMensaje, int intProceso)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(RegistroDestinatario.this);
        if(intProceso == 1) {
            buldier.setTitle("Felicitaciones")
                    .setMessage(strMensaje)
                    //.setNegativeButton("Cancelar", null)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(RegistroDestinatario.this, GeneracionGuia.class);
                            startActivity(intent);
                            finish();
                        }
                    })
            ;
            return buldier.create();
        }
        else
        {
            if(intProceso == 2) {
                buldier.setTitle("Informacion")
                        .setMessage(strMensaje)
                        .setNegativeButton("Lista Destinatarios", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(RegistroDestinatario.this, ListaDestinatarios.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setPositiveButton("Aceptar", null)
                ;
                return buldier.create();
            }else{
                buldier.setTitle("Informacion")
                        .setMessage(strMensaje)
                        .setNegativeButton("Cancelar", null)
                ;
                return buldier.create();
            }

        }
    }

    //Metodo De Lista Cliestes Corp.

    public class CrearDestinatarioAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intCodCli, intTipoIdentificacion;
        String strNombre, strApellido, strDireccion, strDocumento, strTelefono, strEmail, strCodCiu, strCompania;
        public CrearDestinatarioAsyncTask(int intCodCli, String strNombre, String strApellido, String strDireccion, String strDocumento,
                                     String strTelefono, String strEmail, String strCodCiu, String strCompania, int intTipoIdentificacion) {

            this.intCodCli = intCodCli;
            this.strNombre = strNombre;
            this.strApellido = strApellido;
            this.strDireccion= strDireccion;
            this.strDocumento = strDocumento;
            this.strTelefono = strTelefono;
            this.strEmail = strEmail;
            this.strCodCiu = strCodCiu;
            this.strCompania = strCompania;
            this.intTipoIdentificacion = intTipoIdentificacion;

        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            if(!strDireccion.isEmpty()) {
                super.onPostExecute(s);
                int a = Integer.parseInt(s);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString("strNombreDe", (strNombre) + " " + (strApellido));
                editor.putString("strNombreDe", strNombre);
                editor.putString("strApellidoDe", strApellido);
                editor.putString("strCompaniaDe", strCompania);
                editor.putString("strDireccionDe", strDireccion);
                editor.putString("strDocumentoDe", strDocumento);
                editor.putString("strTelefonoDe", strTelefono);
                editor.putInt("intCodDestN", Integer.parseInt(s));
                editor.commit();

                Intent intent = new Intent(RegistroDestinatario.this, GeneracionGuia.class);
                startActivity(intent);
                finish();
            }
        }
        @Override
        protected String doInBackground(Integer... integers) {
            if (!strDireccion.isEmpty())
            {
                String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_CREAR_DESTINATARIO);

            request.addProperty("intTipoIdentificacion", intTipoIdentificacion);
            request.addProperty("intCodCli", intCodCli);
            request.addProperty("strNombre", strNombre);
            request.addProperty("strApellido", strApellido);
            request.addProperty("strCelular", strTelefono);
            request.addProperty("strCedula", strDocumento);
            request.addProperty("strDireccion", strDireccion);
            request.addProperty("strEmail", strEmail);
            request.addProperty("strCodiCiu", strCiudadDest);
            request.addProperty("strCompania",strCompania);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try {
                httpTransport.call(NAMESPACE + ACTION_CREAR_DESTINATARIO, envelope);
            } catch (Exception ex) {
                // TODO Auto-generated catch block
                Log.d(TAG, ex.getMessage());
                ex.printStackTrace();
            }
            Object result = null;
            try {
                result = (Object) envelope.getResponse();
                Log.i(TAG, String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }
            return res;
            }
            else
            {
                dialogInformativo("Datos incompletos.",0);
            }
        return "false";
        }

    }
    public class ValidarDocumento extends AsyncTask<Integer, Integer, String>
    {
        String strDocumento;
        public ValidarDocumento( String strDocumento) {

            this.strDocumento = strDocumento;
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("Disponible"))
            {
                //dialogInformativo(s.toString(), 0).show();
            }
            else
            {
                dialogInformativo("No. de documento ya existe, Busquelo en la lista de destinatarios.", 2).show();
            }
        }

        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_VALIDAR_DOCUMENTO);

            request.addProperty("strDocumento", strDocumento);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_VALIDAR_DOCUMENTO, envelope);
            }

            catch (Exception ex)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,ex.getMessage());
                ex.printStackTrace();
            }
            Object  result = null;
            try {

                result = (Object)envelope.getResponse();
                Log.i(TAG,String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {

                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            return res;
        }
    }
    public void validacionCampos()
    {
        if(edNombreDest.getText().length() < 3 || edApellidoDest.getText().length() < 3
                && edCelDest.getText().length() < 3 || edDirDest.getText().length() < 3 || edCompania.getText().length() < 3)
        {
            dialorInformativo("Los campos deben contener minimo 3 caracteres").show();
        }

        if(edNombreDest.getText().length() == 0 && edApellidoDest.getText().length() == 0
                && edCelDest.getText().length() == 0 && edDirDest.getText().length() == 0 && edCompania.getText().length() == 0)
        {
            edNombreDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edNombreDest.setHint("Nombre Obligatorio*");
            edNombreDest.setHintTextColor(Color.parseColor("#f8b068"));

            edApellidoDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edApellidoDest.setHint("Apellido Obligatorio*");
            edApellidoDest.setHintTextColor(Color.parseColor("#f8b068"));

            edCelDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCelDest.setHint("Celular Obligatorio*");
            edCelDest.setHintTextColor(Color.parseColor("#f8b068"));

            edDirDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edDirDest.setHint("Direccion Obligatorio*");
            edDirDest.setHintTextColor(Color.parseColor("#f8b068"));

            edCompania.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCompania.setHint("Compañia Obligatorio*");
            edCompania.setHintTextColor(Color.parseColor("#f8b068"));

        }
        else if(edDirDest.getText().length() == 0)
        {
            edDirDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edDirDest.setHint("Direccion Obligatorio*");
            edDirDest.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edCelDest.getText().length() == 0)
        {
            edCelDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCelDest.setHint("Celular Obligatorio*");
            edCelDest.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edNombreDest.getText().length() == 0)
        {
            edNombreDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edNombreDest.setHint("Nombre Obligatorio*");
            edNombreDest.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edApellidoDest.getText().length() == 0)
        {
            edApellidoDest.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edApellidoDest.setHint("Apellido Obligatorio*");
            edApellidoDest.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edCompania.getText().length() == 0)
        {
            edCompania.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCompania.setHint("Compañia Obligatorio*");
            edCompania.setHintTextColor(Color.parseColor("#f8b068"));
        }

    }

    public class DatosClientesAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ClientesR>> {

        int idCodcli;

        public DatosClientesAsyncTask(int idCodcli) {
            this.idCodcli = idCodcli;
        }

        @Override
        protected void onPostExecute(ArrayList<ClientesR> s) {
            super.onPostExecute(s);

            if(clientesDest.getIntCorp()==1)
                spClientes.setSelection(0);
            else
                spClientes.setSelection(1);
            edNombreDest.setText(clientesDest.getStrNomCli());
            edApellidoDest.setText(clientesDest.getStrApellido());
            edDocumentoDest.setText(clientesDest.getStrCedula());
            edCelDest.setText(clientesDest.getStrTelcli());
            edCoreleDest.setText(clientesDest.getStrCorreo());
            edDirDest.setText(clientesDest.getStrDireccion());
            edCompania.setText(clientesDest.getStrCompania());
        }

        @Override
        protected ArrayList<ClientesR> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_DATOS_DESTINATARIO);
            SoapObject result;
            request.addProperty("CodCli",idCodcli);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_DATOS_DESTINATARIO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            int IdCodcli, IdIdentificacionCli;
            String strNombre, strApellido, strCorreo, strTelefono, strDocumento, strDireccion, strCompania;

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    IdCodcli = Integer.parseInt(s.getProperty("CODDES").toString());
                    IdIdentificacionCli = Integer.parseInt(s.getProperty("ID_IDENTIFICACION").toString());
                    strNombre = NetworkUtil.validarAnytype(s.getProperty("NOMDES").toString());
                    strApellido = NetworkUtil.validarAnytype(s.getProperty("APEDES").toString());
                    strCorreo = NetworkUtil.validarAnytype(s.getProperty("MAILDES").toString());
                    strTelefono = s.getProperty("TELDES").toString();
                    strDocumento = s.getProperty("IDEDES").toString();
                    strDireccion = s.getProperty("DIRDES").toString();
                    strCompania = NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIADES").toString());

                    clientesDest = new ClientesR(IdIdentificacionCli, strNombre, IdCodcli, strDocumento, strDireccion, strTelefono, strCorreo,
                            strCompania, strApellido);

                    dtClientesDest.add(new ClientesR(IdIdentificacionCli, strNombre, IdCodcli, strDocumento, strDireccion, strTelefono, strCorreo,
                            strCompania, strApellido));

                }
            }
            return dtClientesDest;

        }

    }

    public class ActualizarClienteAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intTipoIdentificacion, intCodCli;
        String strNombre, strApellido, strDireccion, strDocumento, strTelefono, strEmail, strCompania;
        public ActualizarClienteAsyncTask(int intTipoIdentificacion, String strNombre, String strApellido, String strDireccion, String strDocumento,
                                          String strTelefono, String strEmail, int intCodCli, String strCompania) {

            this.intTipoIdentificacion = intTipoIdentificacion;
            this.strNombre = strNombre;
            this.strApellido = strApellido;
            this.strDireccion= strDireccion;
            this.strDocumento = strDocumento;
            this.strTelefono = strTelefono;
            this.strEmail = strEmail;
            this.intCodCli = intCodCli;
            this.strCompania = strCompania;
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                strMensaje = " Proceso exitoso, acabas de actualizar el destinatario: ";
                SharedPreferences.Editor editor = sharedPreferences.edit();/*
                if(strTipo.equalsIgnoreCase("1"))
                    editor.putString("strNombreDe", (strNombre+" "+strApellido));
                else
                    editor.putString("strNombreDe", (strCompania));*/
                editor.putString("strNombreDe", strNombre);
                editor.putString("strCompaniaDe", strCompania);
                editor.putString("strApellidoDe", strApellido);
                editor.putString("strDireccionDe", strDireccion);
                editor.putString("strDocumentoDe", strDocumento);
                editor.putString("strTelefonoDe", strTelefono);
                editor.putInt("intCodDestN", intCodCli);

                editor.commit();
                dialogInformativo(strMensaje, 1).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_ACTUALIZAR_DESTINATARIO);

            request.addProperty("codcli", intCodCli);
            request.addProperty("intCodIde", intTipoIdentificacion);
            request.addProperty("strNombre",strNombre);
            request.addProperty("strApellido", strApellido);
            request.addProperty("strDocumento", strDocumento);
            request.addProperty("strTelefono", strTelefono);
            request.addProperty("strCorreo", strEmail);
            request.addProperty("strDireccion", strDireccion);
            request.addProperty("strCompania", strCompania);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_ACTUALIZAR_DESTINATARIO, envelope);
            }
            catch (Exception ex)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,ex.getMessage());
                ex.printStackTrace();
            }
            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                Log.i(TAG,String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            return res;
        }

    }

    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(RegistroDestinatario.this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("OK", null)
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }
    /**
     * Capturar el error
     * @param thread
     * @param e
     */
    private void excepcionCapturada(Thread thread, Throwable e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        LogErrorDB.LogError(sharedPreferences.getInt("idUsuario",0),errors.toString(), this.getClass().getCanonicalName(), BASE_URL, this);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }

}
