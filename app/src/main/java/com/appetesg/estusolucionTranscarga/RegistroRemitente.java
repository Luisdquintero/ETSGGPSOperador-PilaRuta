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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.CiudadesDesApapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaClientesCorpAdapter;
import com.appetesg.estusolucionTranscarga.adapter.TiposIdentificacionAdapter;
import com.appetesg.estusolucionTranscarga.modelos.CiudadesD;
import com.appetesg.estusolucionTranscarga.modelos.TiposIdentificacios;
import com.appetesg.estusolucionTranscarga.modelos.ListaClientesCorp;
import com.appetesg.estusolucionTranscarga.modelos.DatosClienteSeleccionado;
import com.appetesg.estusolucionTranscarga.modelos.ValorTarifa;
import com.appetesg.estusolucionTranscarga.modelos.ClientesR;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class RegistroRemitente extends AppCompatActivity {

    TiposIdentificacionAdapter mTiposIdentidicacion;
    ArrayList<CiudadesD> listCiudad = new ArrayList<>();
    ArrayList<CiudadesD>  arrayaux = new ArrayList<>();
    CiudadesDesApapter mListaCiudDest;
    CiudadesD resultp;
    ListView mListView;
    int idUsuario;
    RadioGroup radioGroup;
    Spinner spClientes;
    EditText edCompania, edNombreCli, edApellidoCli, edDirCli, edCoreleCli, edCelCli, edDocumento, etCiudadDe;
    Button btnContinuar2, btnActualizar;
    TextView edTitleCliente, edTitleTotal;
    ImageButton imgButton;
    ListaClientesCorpAdapter mListaClientesCorp;
    ArrayList<ListaClientesCorp> listaClientesCorps = new ArrayList<>();
    ArrayList<DatosClienteSeleccionado> ClienteSeleccionado =  new ArrayList<>();
    DatosClienteSeleccionado dtCliente;
    ArrayList<ValorTarifa> valorTarifas = new ArrayList<>();
    ValorTarifa dtValorTarifas;
    ArrayList<ClientesR> dtClientesR = new ArrayList<>();
    ClientesR clientesR;
    SharedPreferences sharedPreferences;
    String strVacio = null, strCompanhiaCli, strCiudadGuia, strNomCiuCambio = "", strCodCiuCambio, strTipo, strProductoGuia, strEnvio, strPesoGuia, strCantidadGuia, strValorGuia, strMensaje;
    int intCodcliGeneral = 0;
    int intCodusu = 0;
    private static final String ACTION_LISTADO_CIUDADES = "ListaCiudadesDestinoBusqueda";
    private static final String ACTION_DATOS_CLIENTE = "DatosClienteCorp";
    private static final String ACTION_CREAR_CLIENTE= "CrearCliente";
    private static final String ACTION_VALIDAR_DOCUMENTO= "ValidarDocumento";
    private static final String ACTION_ACTUALIZAR_CLIENTE = "ActualizarCliente";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaClientesCorporativos";
    String BASE_URL, PREFS_NAME;

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
        setContentView(R.layout.activity_registro_remitente);

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        intCodcliGeneral = sharedPreferences.getInt("intCodCliN", 0);
        strNomCiuCambio = sharedPreferences.getString("strCodCiuElect", "");
        intCodusu = sharedPreferences.getInt("idUsuario",0);
        edNombreCli = (EditText)findViewById(R.id.txtNombreCli);
        edApellidoCli = (EditText)findViewById(R.id.txtApellidoCli);
        edDirCli = (EditText)findViewById(R.id.txtDirCli);
        edCoreleCli = (EditText)findViewById(R.id.txtEmailCliente);
        edCelCli = (EditText)findViewById(R.id.txtCelularCliente);
        edDocumento = (EditText)findViewById(R.id.txtDocumento);
        spClientes = (Spinner)findViewById(R.id.lstDocumento);
        edCompania = (EditText)findViewById(R.id.txtCompaniaCli);
        btnContinuar2 = (Button)findViewById(R.id.btnContinuarG2);
        btnActualizar= (Button)findViewById(R.id.btnActualizarC);

        mListView = (ListView) findViewById(R.id.lstCiudadDest);
        etCiudadDe = (EditText) findViewById(R.id.etCiudadDe4);

        imgButton = (ImageButton)findViewById(R.id.btnReturnDesription);

        strCiudadGuia = sharedPreferences.getString("strCodCiuDest", "");
        strProductoGuia = sharedPreferences.getString("strProductoGuia", "");
        strEnvio = sharedPreferences.getString("strEnvio", "") ;
        strPesoGuia =sharedPreferences.getString("strPesoGuia", "");
        strCantidadGuia = sharedPreferences.getString("strCantidadGuia", "");
        strValorGuia = sharedPreferences.getString("strValorGuia", "");

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(RegistroRemitente.this, listaClintesRemitente.class);
                startActivity(intent);
            }
        });

        edDocumento.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override public void onFocusChange(View v, boolean hasFocus)
            { /* When focus is lost check that the text field * has valid values. */
                if (!hasFocus && !edDocumento.getText().toString().isEmpty())
                {
                    new ValidarDocumento(edDocumento.getText().toString().trim()).execute();
                }
            }
        });

        TiposIdentificacion();

        spClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = spClientes.getItemAtPosition(i);
                TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                strTipo = String.valueOf(ListaIndentificaciones.getIntId());
                if(strTipo.equalsIgnoreCase("1")){ //TIPO DOC. CEDULA

                    edCompania.setVisibility(View.GONE);
                    edNombreCli.setVisibility(View.VISIBLE);
                    edApellidoCli.setVisibility(View.VISIBLE);
                }else{ // TIPO DOC. NIT

                    edNombreCli.setVisibility(View.GONE);
                    edApellidoCli.setVisibility(View.GONE);
                    edCompania.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new ListaCiudadDestAsyncTask(etCiudadDe.getText().toString(), intCodusu).execute();

        etCiudadDe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etCiudadDe.getRight() - etCiudadDe.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etCiudadDe.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listCiudad.size();i++)
                            {

                                resultp = listCiudad.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();

                                //System.out.println("nombre_ciudad "+ resultp.getStrNomCiuDe() + " cod "+ resultp.getStrOficina());

                                if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadDe.getText().toString().toUpperCase()))
                                {

                                    arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe(), resultp.getStrOficina()));

                                }
                            }

                            if(arrayaux != null)
                            {

                                mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, arrayaux);
                                mListView.setAdapter(mListaCiudDest);
                                mListaCiudDest.notifyDataSetChanged();

                            }
                            else
                            {

                                mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, arrayaux);
                                mListView.setAdapter(mListaCiudDest);
                                mListaCiudDest.notifyDataSetChanged();
                            }
                        }
                        else
                        {


                            mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, listCiudad);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etCiudadDe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null)
                    return;
                String str = s.toString().substring(start, start + count);
                if (str.equals("\n"))
                {
                    etCiudadDe.setText(s.toString().replaceFirst("\n", ""));

                    etCiudadDe.setText(etCiudadDe.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etCiudadDe.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listCiudad.size();i++)
                        {

                            resultp = listCiudad.get(i);

                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomCiuDe());

                            if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadDe.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe(), resultp.getStrOficina()));

                            }

                        }

                        if(arrayaux != null)
                        {

                            mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, arrayaux);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, arrayaux);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, listCiudad);
                        mListView.setAdapter(mListaCiudDest);
                        mListaCiudDest.notifyDataSetChanged();
                    }

                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(NetworkUtil.hayInternet(RegistroRemitente.this)) {
                    CiudadesD objCiudades = (CiudadesD) mListView.getItemAtPosition(position);
                    strCodCiuCambio = objCiudades.getStrCodCiuDe();
                    etCiudadDe.setText(objCiudades.getStrNomCiuDe());
                    mListView.setSelection(position);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No hay conexion en internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etCiudadDe.setText(strNomCiuCambio);

        if(intCodcliGeneral > 0)
        {
            btnActualizar.setVisibility(View.VISIBLE);
            btnContinuar2.setVisibility(View.GONE);
            etCiudadDe.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            new DatosClientesAsyncTask(intCodcliGeneral).execute();
        }
        else
        {
            btnActualizar.setVisibility(View.GONE);
            btnContinuar2.setVisibility(View.VISIBLE);
            etCiudadDe.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }


        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((strTipo.equalsIgnoreCase("1") && edNombreCli.getText().length() > 2 && edApellidoCli.getText().length() > 2
                        && edCelCli.getText().length() > 0 && edDirCli.getText().length() > 0) ||
                        (strTipo.equalsIgnoreCase("4") && edCompania.getText().length() > 2
                                && edCelCli.getText().length() > 0 && edDirCli.getText().length() > 0)) {

                    if(NetworkUtil.hayInternet(RegistroRemitente.this))
                    {
                        if(edDocumento.getText().toString().trim().length()==0)
                            edDocumento.setText("0");
                        Object o = spClientes.getSelectedItem();
                        TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                        new ActualizarClienteAsyncTask(ListaIndentificaciones.getIntId(), edNombreCli.getText().toString().toUpperCase().trim(), edApellidoCli.getText().toString().toUpperCase().trim(),
                                edDirCli.getText().toString().toUpperCase().trim(), edDocumento.getText().toString().toUpperCase().trim(), edCelCli.getText().toString().toUpperCase().trim(), edCoreleCli.getText().toString().toUpperCase().trim(),
                                intCodcliGeneral, edCompania.getText().toString().toUpperCase().trim()).execute();
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

                if((strTipo.equalsIgnoreCase("1") && edNombreCli.getText().length() > 2 && edApellidoCli.getText().length() > 2
                        && edCelCli.getText().length() > 0 && edDirCli.getText().length() > 0) ||
                        (strTipo.equalsIgnoreCase("4") && edCompania.getText().length() > 2
                        && edCelCli.getText().length() > 0 && edDirCli.getText().length() > 0)) {

                    if(NetworkUtil.hayInternet(RegistroRemitente.this))
                    {
                        if(edDocumento.getText().toString().trim().length()==0)
                            edDocumento.setText("0");
                        Object o = spClientes.getSelectedItem();
                        TiposIdentificacios ListaIndentificaciones = (TiposIdentificacios)o;
                        new CrearClienteAsyncTask(ListaIndentificaciones.getIntId(), edNombreCli.getText().toString().toUpperCase().trim(), edApellidoCli.getText().toString().toUpperCase().trim(),
                                edDirCli.getText().toString().toUpperCase().trim(), edDocumento.getText().toString().toUpperCase().trim(), edCelCli.getText().toString().toUpperCase().trim(), edCoreleCli.getText().toString().toUpperCase().trim(),
                                intCodusu, strCiudadGuia, edCompania.getText().toString().toUpperCase().trim()).execute();
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
        mTiposIdentidicacion = new TiposIdentificacionAdapter(RegistroRemitente.this, aTiposIde);
        spClientes.setAdapter(mTiposIdentidicacion);
    }

    public AlertDialog dialorInformativo(String strMensaje, int intProceso)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(RegistroRemitente.this);
        if(intProceso == 1) {
            buldier.setTitle("Felicitaciones")
                    .setMessage(strMensaje)
                    //.setNegativeButton("Cancelar", null)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            Intent intent = new Intent(RegistroRemitente.this, ListaDestinatarios.class);
                            startActivity(intent);
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
                        .setNegativeButton("Lista Remitentes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                Intent intent = new Intent(RegistroRemitente.this, listaClintesRemitente.class);
                                startActivity(intent);
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

    public class CrearClienteAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intTipoIdentificacion, intCodusu;
        String strNombre, strApellido, strDireccion, strDocumento, strTelefono, strEmail, strCodCiu, strCompania;
        public CrearClienteAsyncTask(int intTipoIdentificacion, String strNombre, String strApellido, String strDireccion, String strDocumento,
                                      String strTelefono, String strEmail, int intCodusu, String strCodCiu, String strCompania) {

            this.intTipoIdentificacion = intTipoIdentificacion;
            this.strNombre = strNombre;
            this.strApellido = strApellido;
            this.strDireccion= strDireccion;
            this.strDocumento = strDocumento;
            this.strTelefono = strTelefono;
            this.strEmail = strEmail;
            this.intCodusu = intCodusu;
            this.strCodCiu = strCodCiu;
            this.strCompania = strCompania;
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
           super.onPostExecute(s);

           int a = Integer.parseInt(s);
           if(a == -2)
           {
               strMensaje = "El número de documento identificación ya existe, el registro no fue realizado.";
               dialorInformativo(strMensaje, 0).show();
           }
           else
           {
               strMensaje = " Proceso exitoso, codigo del cliente: " + s;
               SharedPreferences.Editor editor = sharedPreferences.edit();
               /*
               if(strTipo.equalsIgnoreCase("1"))
                   editor.putString("strNombreC", (strNombre+" "+strApellido));
               else
                   editor.putString("strNombreC", (strCompania));*/
               editor.putString("strNombreC", strNombre);
               editor.putString("strApellidoC", strApellido);
               editor.putString("strDireccionC", strDireccion);
               editor.putString("strDocumentoC", strDocumento);
               editor.putString("strTelefonoC", strTelefono);
               editor.putString("strCompaniaC",strCompania);
               editor.putInt("intCodCliN", Integer.parseInt(s));
               editor.commit();
               dialorInformativo(strMensaje, 1).show();
           }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_CREAR_CLIENTE);

            request.addProperty("intTipoIdentificacion", intTipoIdentificacion);
            request.addProperty("strNombre",strNombre);
            request.addProperty("strApellido", strApellido);
            request.addProperty("strDocumento", strDocumento);
            request.addProperty("strTelefono", strTelefono);
            request.addProperty("strEmail", strEmail);
            request.addProperty("strDireccion", strDireccion);
            request.addProperty("intCodusu", intCodusu);
            request.addProperty("strCodCiu", strCodCiu);
            request.addProperty("strCompania",strCompania);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_CREAR_CLIENTE, envelope);
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
                strMensaje = " Proceso exitoso, acabas de actualizar el cliente: ";
                SharedPreferences.Editor editor = sharedPreferences.edit();
                /*if(strTipo.equalsIgnoreCase("1"))
                    editor.putString("strNombreC", (strNombre+" "+strApellido));
                else
                    editor.putString("strNombreC", (strCompania));*/
                editor.putString("strNombreC", strNombre);
                editor.putString("strApellidoC", strApellido);
                editor.putString("strDireccionC", strDireccion);
                editor.putString("strDocumentoC", strDocumento);
                editor.putString("strTelefonoC", strTelefono);
                editor.putString("strApellidoC", strApellido);
                editor.putInt("intCodCliN", intCodCli);
                editor.commit();
                dialorInformativo(strMensaje, 1).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_ACTUALIZAR_CLIENTE);

            request.addProperty("codcli", intCodCli);
            request.addProperty("strNombre",strNombre);
            request.addProperty("strApellido", strApellido);
            request.addProperty("strDocumento", strDocumento);
            request.addProperty("strTelefono", strTelefono);
            request.addProperty("strCorreo", strEmail);
            request.addProperty("strDireccion", strDireccion);
            request.addProperty("strCompania", strCompania);
            request.addProperty("intTipoId", intTipoIdentificacion);
            request.addProperty("strCodCiu", strCodCiuCambio);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_ACTUALIZAR_CLIENTE, envelope);
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


    public class DatosClientesAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ClientesR>> {

        int idCodcli;

        public DatosClientesAsyncTask(int idCodcli) {
            this.idCodcli = idCodcli;
        }

        @Override
        protected void onPostExecute(ArrayList<ClientesR> s) {
            super.onPostExecute(s);

            if(clientesR.getIntCorp()==1)
                spClientes.setSelection(0);
            else
                spClientes.setSelection(1);
            edNombreCli.setText(clientesR.getStrNomCli());
            edApellidoCli.setText(clientesR.getStrApellido());
            edDocumento.setText(clientesR.getStrCedula());
            edCelCli.setText(clientesR.getStrTelcli());
            edCoreleCli.setText(clientesR.getStrCorreo());
            edDirCli.setText(clientesR.getStrDireccion());
            edCompania.setText(clientesR.getStrCompania());

            System.out.println("Celular:" + clientesR.getStrTelcli());
        }

        @Override
        protected ArrayList<ClientesR> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_DATOS_CLIENTE);
            SoapObject result;
            request.addProperty("CodCli",idCodcli);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_DATOS_CLIENTE, envelope);
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

                    IdCodcli = Integer.parseInt(s.getProperty("CODCLI").toString());
                    IdIdentificacionCli = Integer.parseInt(s.getProperty("ID_IDENTIFICACION").toString());
                    strNombre = NetworkUtil.validarAnytype(s.getProperty("NOMCLI").toString());
                    strApellido = NetworkUtil.validarAnytype(s.getProperty("APECLI").toString());
                    strCorreo = NetworkUtil.validarAnytype(s.getProperty("CORELECLI").toString());
                    strTelefono = s.getProperty("CELCLI").toString();
                    strDocumento = s.getProperty("IDECLI").toString();
                    strDireccion = s.getProperty("DIRCLI").toString();
                    strCompania = NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIA").toString());

                    clientesR = new ClientesR(IdIdentificacionCli, strNombre, IdCodcli, strDocumento, strDireccion, strTelefono, strCorreo,
                            strCompania, strApellido);

                    dtClientesR.add(new ClientesR(IdIdentificacionCli, strNombre, IdCodcli, strDocumento, strDireccion, strTelefono, strCorreo,
                            strCompania, strApellido));

                }
            }
            return dtClientesR;

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
                //dialorInformativo(s.toString(), 0).show();
            }
            else
            {
                dialorInformativo("No. de documento ya existe, Busquelo en la lista de remitentes.", 2).show();
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
        if(edNombreCli.getText().length() < 3 || edApellidoCli.getText().length() < 3
                || edCelCli.getText().length() < 3 || edDirCli.getText().length() < 3 || edCompania.getText().length() < 3 )
        {
            dialorInformativo("Los campos deben contener minimo 3 caracteres").show();
        }

        if(edNombreCli.getText().length() == 0 && edApellidoCli.getText().length() == 0
                && edCelCli.getText().length() == 0 && edDirCli.getText().length() == 0 && edCompania.getText().length() == 0)
        {
            edNombreCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edNombreCli.setHint("Nombre Obligatorio*");
            edNombreCli.setHintTextColor(Color.parseColor("#f8b068"));

            edApellidoCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edApellidoCli.setHint("Apellido Obligatorio*");
            edApellidoCli.setHintTextColor(Color.parseColor("#f8b068"));

            edCelCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCelCli.setHint("Celular Obligatorio*");
            edCelCli.setHintTextColor(Color.parseColor("#f8b068"));

            edDirCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edDirCli.setHint("Direccion Obligatorio*");
            edDirCli.setHintTextColor(Color.parseColor("#f8b068"));

            edCompania.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCompania.setHint("Compañia Obligatorio*");
            edCompania.setHintTextColor(Color.parseColor("#f8b068"));

        }
        else if(edDirCli.getText().length() == 0)
        {
            edDirCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edDirCli.setHint("Direccion Obligatorio*");
            edDirCli.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edCelCli.getText().length() == 0)
        {
            edCelCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCelCli.setHint("Celular Obligatorio*");
            edCelCli.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edNombreCli.getText().length() == 0)
        {
            edNombreCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edNombreCli.setHint("Nombre Obligatorio*");
            edNombreCli.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edApellidoCli.getText().length() == 0)
        {
            edApellidoCli.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edApellidoCli.setHint("Apellido Obligatorio*");
            edApellidoCli.setHintTextColor(Color.parseColor("#f8b068"));
        }
        else if(edCompania.getText().length() == 0)
        {
            edCompania.getBackground().setColorFilter(Color.parseColor("#f8b068"), PorterDuff.Mode.SRC_ATOP);
            edCompania.setHint("Compañia Obligatorio*");
            edCompania.setHintTextColor(Color.parseColor("#f8b068"));
        }

    }

    public class ListaCiudadDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<CiudadesD>> {

        String strNomciu;
        int idUsuario;

        public ListaCiudadDestAsyncTask(String strNomciu, int idUsuario) {
            this.strNomciu = strNomciu;
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<CiudadesD> s) {
            super.onPostExecute(s);
            mListaCiudDest = new CiudadesDesApapter( RegistroRemitente.this, s);
            mListView.setAdapter(mListaCiudDest);
        }

        @Override
        protected ArrayList<CiudadesD> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_CIUDADES);
            request.addProperty("strNomciu", strNomciu);
            request.addProperty("intIdUsuario", idUsuario);
            SoapObject result;


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_CIUDADES, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;


            String strCodCiud, strNomCiu, strOficina;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
                    strCodCiud = s.getProperty("CODCIU").toString();
                    strNomCiu = s.getProperty("NOMCIU").toString();
                    strOficina = s.getProperty("CODOFIORI").toString();
                    if(strNomCiu.equalsIgnoreCase(strNomCiuCambio))
                        strCodCiuCambio = strCodCiud;
                    listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, strOficina));
                }

            }
            else
            {
                strCodCiud = "";
                strNomCiu = "No hay ciudades disponibles.";
                listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, ""));

            }
            return listCiudad;

        }

    }

    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(RegistroRemitente.this);

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
