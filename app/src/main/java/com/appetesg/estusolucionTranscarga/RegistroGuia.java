package com.appetesg.estusolucionTranscarga;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaProductosAdapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaEnviosAdater;
import com.appetesg.estusolucionTranscarga.adapter.ListaCiudadesDestinoAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Bono;
import com.appetesg.estusolucionTranscarga.modelos.ClientesR;
import com.appetesg.estusolucionTranscarga.modelos.ListaEnvios;
import com.appetesg.estusolucionTranscarga.modelos.ListaCiudadDestino;
import com.appetesg.estusolucionTranscarga.modelos.ValorTarifa;
import com.appetesg.estusolucionTranscarga.utilidades.ActivarSeccionDB;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class RegistroGuia extends AppCompatActivity {
    public TextView txtVRG, txtSeg;
    FloatingActionButton FabCliCorp;
    IntentIntegrator qrScan;
    Spinner spCiudades,spProductos, spEnvios;
    EditText edPeso, edValorDecla, edCantidad;
    public EditText edValorSeg;
    Button btnContinuar;
    ImageButton imgRegreso;
    ListaProductosAdapter mListaProductos;
    ListaEnviosAdater mListaEnvios;
    ListaCiudadesDestinoAdapter mListaCiudadDestino;
    ArrayList<ListaCiudadDestino> listaCiudadDestinos = new ArrayList<>();
    ArrayList<ValorTarifa> valorTarifas = new ArrayList<>();
    ArrayList<Bono> dtBonos = new ArrayList<>();
    ValorTarifa dtValorTarifas;
    ClientesR objCliente = new ClientesR();
    Bono dtInfoBono;
    int idFilto = 0;
    int intCodtEnv;
    Boolean indCliCon = false, blClienteCorpo = false;
    String strCiudad = null, strProducto = "0", strEnvio = "0", strCiudadDe = null,
            strNombreProducto, strNombreEnvio, strNomTienv, strCodPrd, strCliCorp = "";
    SharedPreferences sharedPreferences;
    ProgressDialog p;
    private static final String ACTION_LISTADO_CORP= "ListaClientesCorporativos";
    private static final String CLIENTE_COD= "ClienteCodigo";
    private static final String ACTION_DATOS_BONO = "DatosBono";
    private static final String ACTION_TARIFA_ENVIO= "TarifaEnvio";
    private static final String ACTION_EXISTE_BONO= "ExisteBono";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaCiudadesDestino";
    String BASE_URL, PREFS_NAME, strOficinaOri;
    public AlertDialog alert_desc;

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
        setContentView(R.layout.activity_registro_guia);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio" +
                "", "");

        //spCiudades = (Spinner) findViewById(R.id.lstCiudadDestino);
        //spProductos = (Spinner) findViewById(R.id.lstProductos);
        //spEnvios = (Spinner) findViewById(R.id.lstTiposEnvio);
        edPeso = (EditText) findViewById(R.id.edPesoEnvio);
        edCantidad = (EditText)findViewById(R.id.edPiezasEnvio);
        edValorDecla = (EditText) findViewById(R.id.edValorDeclarado);
        edValorSeg = (EditText) findViewById(R.id.edValorSeg);
        txtVRG = (TextView) findViewById(R.id.txtVRG);
        txtSeg = (TextView) findViewById(R.id.txtSeg);
        imgRegreso = (ImageButton)findViewById(R.id.btnReturnDesription);
        btnContinuar = (Button) findViewById(R.id.btnContinuarG1);
        FabCliCorp = findViewById(R.id.ftbClienteCorp);

        // Valida la seccion seguro abierto por BD
        seccionHabilitada("11"); // ID 11 ES PARA SEGURO ABIERTO

        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        strCiudadDe = sharedPreferences.getString("strCodCiuDest", "");
        strEnvio = String.valueOf(sharedPreferences.getInt("intCodTienvC", 0));
        strNombreEnvio = sharedPreferences.getString("strNombreEC", "");
        strOficinaOri = sharedPreferences.getString("strOficinaOri", "");
        strNombreProducto = sharedPreferences.getString("strNomPrd", "");;
        strCodPrd = sharedPreferences.getString("strCodPrd", "");

        FabCliCorp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                FabCliCorp.setVisibility(View.GONE);
                FabCliCorp.setEnabled(false);
                if(edCantidad.length() > 0 && edPeso.length() > 0 && edValorDecla.length() > 0)
                {
                    qrScan.initiateScan();
                    // LLAMAR AL SCAN QR
                }
                else
                {
                    FabCliCorp.setEnabled(true);
                    FabCliCorp.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Uno o mas campos incompletos.", Toast.LENGTH_SHORT).show();
                }
                FabCliCorp.setEnabled(true);
                FabCliCorp.setVisibility(View.VISIBLE);
            }
        });

        imgRegreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroGuia.this, ListaEnviosProd.class);
                startActivity(intent);
                finish();
            }
        });

        //ListaProductos();
        //ListaEnvios();
        //setOnTouchListener

        edValorDecla.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edValorDecla.length() > 0) {
                    String vr = VRDecimal(Float.parseFloat(edValorDecla.getText().toString()));
                    txtVRG.setText("$"+vr);
                }
                else{
                    txtVRG.setText("");
                }
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnContinuar.setVisibility(View.GONE);
                btnContinuar.setEnabled(false);
                if(edCantidad.length() > 0 && edPeso.length() > 0 && edValorDecla.length() > 0)
                {
                    String strCantidad = edCantidad.getText().toString();
                    String strPeso = edPeso.getText().toString();
                    String strValor = edValorDecla.getText().toString();

                    if(NetworkUtil.hayInternet(RegistroGuia.this)) {
                        new TarifaEnvioAsyncTask(strOficinaOri, "BOG", "CO", "CO", Integer.parseInt(strCodPrd), Integer.parseInt(strEnvio), edPeso.getText().toString(), edValorDecla.getText().toString(), edValorDecla.getText().toString(), strCiudadDe, strNombreProducto, strNombreEnvio, "0", edCantidad.getText().toString()).execute();
                    }
                    else
                    {
                        btnContinuar.setEnabled(true);
                        btnContinuar.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Sin conexion a internet..", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    btnContinuar.setEnabled(true);
                    btnContinuar.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Uno o mas campos incompletos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No hay resultados", Toast.LENGTH_LONG).show();
            } else {
                strCliCorp = result.getContents().toString();
                if(NetworkUtil.hayInternet(RegistroGuia.this)) {
                    //new ListaClientesCorpAsyncTask(strCliCorp).execute();
                    try {
                        Integer.parseInt(strCliCorp);
                        new ListaClientesAsyncTask(strCliCorp).execute();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Documento no valido", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    FabCliCorp.setEnabled(true);
                    FabCliCorp.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Sin conexion a internet..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String VRDecimal(float valor)
    {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        return format.format(valor);
    }

    public void ListaEnvios()
    {
        ArrayList<ListaEnvios> listaEnvios = new ArrayList<>();
        listaEnvios.add(new ListaEnvios(3,"Carga"));
        mListaEnvios = new ListaEnviosAdater(RegistroGuia.this, listaEnvios);
        spEnvios.setAdapter(mListaEnvios);
    }

    public class TarifaEnvioAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ValorTarifa>> {

        String strOficina, strCiudadOri, strPaisOri, strPaisDes, strCiudadDest, strPeso, strValorDec,
                strValorAse, strNombreP, strNombreE, strBono, strCantidad;
        int intCodProd, intCodTienv;
        Double dbPeso, dbValorDec, dbValorSeg;
        ProgressDialog progress;

        public TarifaEnvioAsyncTask(String strOficina, String strCiudadOri, String strPaisOri, String strPaisDes, int intCodProd, int intCodTienv, String strPeso, String strValorDec, String strValorAse, String strCiudadDest, String strNombreP, String strNombreE, String strBono, String strCantidad) {
            this.strOficina = strOficina;
            this.strCiudadOri = strCiudadOri;
            this.strPaisOri = strPaisOri;
            this.strPaisDes = strPaisDes;
            this.intCodProd = intCodProd;
            this.intCodTienv = intCodTienv;
            this.strPeso = strPeso;
            this.strValorDec = strValorDec;
            this.strValorAse = strValorAse;
            this.strCiudadDest = strCiudadDest;
            this.strNombreP = strNombreP;
            this.strNombreE = strNombreE;
            this.strBono = strBono;
            this.strCantidad = strCantidad;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(RegistroGuia.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<ValorTarifa> s) {
            super.onPostExecute(s);

            String[] strDescuentoArray = {"NO","SI"};
            int checkedItem = 0;
            String strPagarTotal = dtValorTarifas.getStrTotalConBono();
            final int[] seleccionpositionR = {0};
            final String[] strValorD = new String[1];
            String strValor = null;

            AlertDialog.Builder alertCorporativo = new AlertDialog.Builder(RegistroGuia.this);
            alertCorporativo.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Cliente corporativo</span><span>"));

            EditText edTempCorp = new EditText(RegistroGuia.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(35,0,35,0);
            edTempCorp.setLeft(15);
            edTempCorp.setRight(15);
            edTempCorp.setInputType(InputType.TYPE_CLASS_NUMBER);
            edTempCorp.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(12)
            });
            edTempCorp.setLayoutParams(lp);
            edTempCorp.setHint("Codigo o Documento");
            edTempCorp.setVisibility(View.GONE);
            alertCorporativo.setView(edTempCorp);
            alertCorporativo.setSingleChoiceItems(strDescuentoArray, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i)
                    {
                        case 0:
                            seleccionpositionR[0] = 0;
                            strValorD[0] = "valor 0";
                            edTempCorp.setVisibility(View.GONE);
                            break;

                        case 1:
                            edTempCorp.setVisibility(View.VISIBLE);
                            seleccionpositionR[0] = 1;
                            break;
                    }
                }
            });
            alertCorporativo.setCancelable(false);
            alertCorporativo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (seleccionpositionR[0] == 1) {
                        // Si es corporativo, hacer llamado al servicio
                        blClienteCorpo = true;
                        new ListaClientesCorpAsyncTask(edTempCorp.getText().toString()).execute();

                    } else {
                        if(Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) >= 0)
                        {
                            //NO ES CORPORATIVO
                            blClienteCorpo = false;
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
                            builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Cotizacion</span><span>"));

                            if(!edValorSeg.getText().toString().trim().isEmpty()){
                                if(Integer.parseInt(edValorSeg.getText().toString()) > Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn())){

                                    dtValorTarifas.setStrTotalImpuestoEn(edValorSeg.getText().toString().trim());
                                    int total = Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) + Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn());
                                    dtValorTarifas.setStrTotalPagarEn(String.valueOf(total));

                                    DecimalFormat formatter = new DecimalFormat("$#,###.00");
                                    dtValorTarifas.setStrTotalImpuestos(formatter.format(Double.parseDouble(edValorSeg.getText().toString().trim()))); // FORMATO PESOS
                                    dtValorTarifas.setStrTotalPagar(formatter.format(Double.parseDouble(String.valueOf(total)))); // FORMATO PESOS
                                }
                            }

                            builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Seguro: </span><span>" + dtValorTarifas.getStrTotalImpuestos() + "</span>" +
                                    "<p><span style='color:#B22222; font-weight: bold;'>Flete: </span><span>" + dtValorTarifas.getStrSubImpuesto() + "</span>" +
                                    "<p><span style='color:#B22222; font-weight: bold;'>Total: </span><span>" + dtValorTarifas.getStrTotalPagar() + "</span>"));

                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    AlertDialog.Builder alertDescuento = new AlertDialog.Builder(RegistroGuia.this);
                                    alertDescuento.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Quieres aplicar descuento</span><span>"));

                                    EditText edTemp = new EditText(RegistroGuia.this);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    lp.setMargins(35, 0, 35, 0);
                                    edTemp.setLeft(15);
                                    edTemp.setRight(15);
                                    edTemp.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    edTemp.setFilters(new InputFilter[]{
                                            new InputFilter.LengthFilter(10)
                                    });
                                    edTemp.setLayoutParams(lp);
                                    edTemp.setHint("Codigo");
                                    edTemp.setVisibility(View.GONE);
                                    alertDescuento.setView(edTemp);
                                    alertDescuento.setSingleChoiceItems(strDescuentoArray, checkedItem, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i) {
                                                case 0:
                                                    seleccionpositionR[0] = 0;
                                                    strValorD[0] = "valor 0";
                                                    edTemp.setVisibility(View.GONE);
                                                    break;

                                                case 1:
                                                    edTemp.setVisibility(View.VISIBLE);
                                                    seleccionpositionR[0] = 1;
                                                    break;
                                            }
                                        }
                                    });
                                    alertDescuento.setCancelable(false);
                                    alertDescuento.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (seleccionpositionR[0] == 1) {
                                                new ExisteBonoAsyncTask(edTemp.getText().toString(), intCodProd, intCodTienv, strPagarTotal, strOficina, strCantidad).execute();

                                            } else {
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("blClienteCorp", blClienteCorpo);
                                                editor.putBoolean("blClienteCon", false);
                                                editor.putString("strCiudadC", strCiudadDest);
                                                editor.putString("strNombrePC", strNombreP);
                                                editor.putString("strNomTienv", strNombreE);
                                                editor.putInt("intCodProdC", intCodProd);
                                                editor.putInt("intCodTienvC", intCodTienv);
                                                editor.putString("strPesoC", strPeso);
                                                editor.putString("strCantidadC", edCantidad.getText().toString());
                                                editor.putString("strValorC", strValorDec);
                                                editor.putString("strValorFlete", dtValorTarifas.getStrSubImpuestoEn());
                                                editor.putString("strValorS", dtValorTarifas.getStrTotalImpuestos());
                                                editor.putString("strTotalC", dtValorTarifas.getStrTotalPagar());
                                                editor.putString("strTotalSin", dtValorTarifas.getStrTotalPagarEn());
                                                editor.putString("strBonoDescuento", "0");
                                                editor.putString("intPorcentaje", "0");
                                                editor.commit();
                                                Intent intent = new Intent(RegistroGuia.this, listaClintesRemitente.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                    alertDescuento.setNegativeButton("Cancelar", null);
                                    AlertDialog dialog = alertDescuento.show();
                                    dialog.setCanceledOnTouchOutside(false);
                                }
                            });
                            builder.setNegativeButton("Cancelar", null);

                            AlertDialog dialog = builder.show();
                            dialog.setCanceledOnTouchOutside(false);
                            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                            messageView.setTextSize(20);
                        }else{
                            // SI NO TIENE VALORES VALIDOS

                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
                            builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Aviso</span><span>"));

                            builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Tarifas Invalidas, Comuniquese con el administrador. </span><span>"));
                            AlertDialog dialogE = builder.show();
                            dialogE.setCanceledOnTouchOutside(true);
                        }

                    }
                }
            });
            alertCorporativo.setNegativeButton("Cancelar", null);
            progress.dismiss();  // dismiss dialog
            AlertDialog dialog = alertCorporativo.show();
            dialog.setCanceledOnTouchOutside(false);
            btnContinuar.setEnabled(true);
            btnContinuar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<ValorTarifa> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TARIFA_ENVIO);
            SoapObject result;
            request.addProperty("strOficina",strOficina);
            request.addProperty("strCiudadOri",strCiudadOri);
            request.addProperty("strPaisOri",strPaisOri);
            request.addProperty("strPaisDes",strPaisDes);
            request.addProperty("intCodProd",intCodProd);
            request.addProperty("intCodTienv",intCodTienv);
            request.addProperty("strPeso",strPeso);
            request.addProperty("strValorDec",strValorDec);
            request.addProperty("strValorAse",strValorAse);
            request.addProperty("strCiudadDest",strCiudadDest);
            request.addProperty("strBono",strBono);
            request.addProperty("strValorTotalPrimario", "0");
            request.addProperty("intCantidad", Integer.parseInt(strCantidad));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_TARIFA_ENVIO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento,
                    strSubImpuestoEn, strTotalImpuestosEn, strTotalPagarEn, strTotalDescuentoEn;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
/*
                    dtValorTarifas = new ValorTarifa(s.getProperty("SUBIMPUESTOS").toString(), s.getProperty("TOTALIMPUESTOS").toString(),
                            s.getProperty("TOTALPAGAR").toString(), s.getProperty("TOTALCONDESCUENTO").toString());


                    strSubImpuesto = s.getProperty("SUBIMPUESTOS").toString();
                    strTotalImpuestos = s.getProperty("TOTALIMPUESTOS").toString();
                    strTotalPagar = s.getProperty("TOTALPAGAR").toString();
                    strTotalDescuento = s.getProperty("TOTALCONDESCUENTO").toString();
                    valorTarifas.add(new ValorTarifa(strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento));
*/
                    dtValorTarifas = new ValorTarifa(s.getProperty("SUBIMPUESTOS").toString(), s.getProperty("TOTALIMPUESTOS").toString(),
                            s.getProperty("TOTALPAGAR").toString(), s.getProperty("TOTALCONDESCUENTO").toString(),
                            s.getProperty("SUBIMPUESTOSSINSIGNO").toString(), s.getProperty("TOTALIMPUESTOSSINSIGNO").toString(),
                            s.getProperty("TOTALPAGARSINSIGNO").toString(), s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString());


                    strSubImpuesto = s.getProperty("SUBIMPUESTOS").toString();
                    strTotalImpuestos = s.getProperty("TOTALIMPUESTOS").toString();
                    strTotalPagar = s.getProperty("TOTALPAGAR").toString();
                    strTotalDescuento = s.getProperty("TOTALCONDESCUENTO").toString();
                    strSubImpuestoEn = s.getProperty("SUBIMPUESTOSSINSIGNO").toString();
                    strTotalImpuestosEn = s.getProperty("TOTALIMPUESTOSSINSIGNO").toString();
                    strTotalPagarEn = s.getProperty("TOTALPAGARSINSIGNO").toString();
                    strTotalDescuentoEn = s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString();
                    valorTarifas.add(new ValorTarifa(strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento, strSubImpuestoEn, strTotalImpuestosEn,
                            strTotalPagarEn, strTotalDescuentoEn));
                }
            }
            return valorTarifas;

        }
    }

    // Valida si existe el codigo de descuento ingresado
    public class ExisteBonoAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        int intCodPro, intCodTien;
        String strBono, strPagarTotal, strOficina, strCantidad;

        String strVacio = "";

        public ExisteBonoAsyncTask(String strBono, int intCodPro, int intCodTien, String strPagarTotal, String strOficina, String strCantidad)
        {
            this.strBono = strBono;
            this.intCodPro = intCodPro;
            this.intCodTien = intCodTien;
            this.strPagarTotal= strPagarTotal;
            this.strOficina = strOficina;
            this.strCantidad = strCantidad;
        }

        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //p.cancel();
            if(s.equalsIgnoreCase("True") || s.equalsIgnoreCase("true")) {
                System.out.println("Luis Eduardo Guzman Alba:"+strPagarTotal);
                new DatosBonoAsyncTask(strBono, strPagarTotal, strOficina, intCodPro, intCodTien, strCantidad).execute();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Alerta</span><span>"));

                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>"+s+" </span>"));

                builder.setCancelable(false);
                //p.cancel();
                builder.setNegativeButton("Cancelar", null);

                AlertDialog dialog = builder.show();
                dialog.setCanceledOnTouchOutside(false);
                TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                messageView.setTextSize(20);
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_EXISTE_BONO);

            request.addProperty("strBono", strBono);
            request.addProperty("intCodProd",intCodPro);
            request.addProperty("intCodTipEnv", intCodTien);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_EXISTE_BONO, envelope);
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

    // ENVIOCORPORATIVO
    public class EnvioCorporativoAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ValorTarifa>> {

        String strOficina, strCiudadOri, strPaisOri, strPaisDes, strCiudadDest, strPeso, strValorDec, strValorAse, strNombreP, strNombreE, strBono, strCantidad, strNomCli;
        int intCodProd, intCodTienv, intCodCli;

        public EnvioCorporativoAsyncTask(String strOficina, String strCiudadOri, String strPaisOri, String strPaisDes, int intCodProd, int intCodTienv, String strPeso, String strValorDec, String strValorAse, String strCiudadDest, String strNombreP, String strNombreE, String strBono, int intCodCli, String strCantidad, String strNomCli) {
            this.strOficina = strOficina;
            this.strCiudadOri = strCiudadOri;
            this.strPaisOri = strPaisOri;
            this.strPaisDes = strPaisDes;
            this.intCodProd = intCodProd;
            this.intCodTienv = intCodTienv;
            this.strPeso = strPeso;
            this.strValorDec = strValorDec;
            this.strValorAse = strValorAse;
            this.strCiudadDest = strCiudadDest;
            this.strNombreP = strNombreP;
            this.strNombreE = strNombreE;
            this.strBono = strBono;
            this.intCodCli = intCodCli;
            this.strCantidad = strCantidad;
            this.strNomCli = strNomCli;
        }

        @Override
        protected void onPostExecute(ArrayList<ValorTarifa> s) {
            super.onPostExecute(s);

            String[] strDescuentoArray = {"NO","SI"};
            int checkedItem = 0;
            String strPagarTotal = dtValorTarifas.getStrTotalConBono();
            final int[] seleccionpositionR = {0};
            final String[] strValorD = new String[1];

            if(Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) >= 0) {

                if(!edValorSeg.getText().toString().trim().isEmpty()){

                    dtValorTarifas.setStrTotalImpuestoEn(edValorSeg.getText().toString().trim());
                    int total = Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) + Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn());
                    dtValorTarifas.setStrTotalPagarEn(String.valueOf(total));

                    DecimalFormat formatter = new DecimalFormat("$#,###.00");
                    dtValorTarifas.setStrTotalImpuestos(formatter.format(Double.parseDouble(edValorSeg.getText().toString().trim()))); // FORMATO PESOS
                    dtValorTarifas.setStrTotalPagar(formatter.format(Double.parseDouble(String.valueOf(total)))); // FORMATO PESOS

                }
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Cotizacion para </span><span>" + strNomCli + "</span>"));

                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Seguro: </span><span>" + dtValorTarifas.getStrTotalImpuestos() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Flete: </span><span>" + dtValorTarifas.getStrSubImpuesto() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Total: </span><span>" + dtValorTarifas.getStrTotalPagar() + "</span>"));

                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder alertDescuento = new AlertDialog.Builder(RegistroGuia.this);
                        alertDescuento.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Quieres aplicar descuento</span><span>"));

                        EditText edTemp = new EditText(RegistroGuia.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(35, 0, 35, 0);
                        edTemp.setLeft(15);
                        edTemp.setRight(15);
                        edTemp.setInputType(InputType.TYPE_CLASS_NUMBER);
                        edTemp.setFilters(new InputFilter[]{
                                new InputFilter.LengthFilter(14)
                        });
                        edTemp.setLayoutParams(lp);
                        edTemp.setHint("Codigo");
                        edTemp.setVisibility(View.GONE);
                        alertDescuento.setView(edTemp);
                        alertDescuento.setSingleChoiceItems(strDescuentoArray, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        seleccionpositionR[0] = 0;
                                        strValorD[0] = "valor 0";
                                        edTemp.setVisibility(View.GONE);
                                        break;

                                    case 1:
                                        edTemp.setVisibility(View.VISIBLE);
                                        seleccionpositionR[0] = 1;
                                        break;
                                }
                            }
                        });
                        alertDescuento.setCancelable(false);
                        alertDescuento.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (seleccionpositionR[0] == 1) {
                                    new ExisteBonoAsyncTask(edTemp.getText().toString(), intCodProd, intCodTienv, strPagarTotal, strOficina, strCantidad).execute();

                                } else {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //CLIENTE CORP
                                    editor.putString("strNombreC", objCliente.getStrNomCli());
                                    editor.putString("strApellidoC", objCliente.getStrApellido());
                                    editor.putString("strDireccionC", objCliente.getStrDireccion());
                                    editor.putString("strDocumentoC", objCliente.getStrCedula());
                                    editor.putString("strTelefonoC", objCliente.getStrCelCli());
                                    editor.putString("strCompaniaC", objCliente.getStrCompania());

                                    //editor.putString("strNombreC", objCliente.getStrCompania());
                                    //editor.putString("strDireccionC", objCliente.getStrDireccion());
                                    //editor.putString("strDocumentoC", objCliente.getStrCedula());
                                    //editor.putString("strTelefonoC", objCliente.getStrTelcli());
                                    editor.putBoolean("blClienteCorp", blClienteCorpo);
                                    editor.putBoolean("blClienteCon", indCliCon);
                                    editor.putInt("intCodCliN", objCliente.getIntCodCli());

                                    editor.putString("strCiudadC", strCiudadDest);
                                    editor.putString("strNombrePC", strNombreP);
                                    editor.putString("strNomTienv", strNombreE);
                                    editor.putInt("intCodProdC", intCodProd);
                                    editor.putInt("intCodTienvC", intCodTienv);
                                    editor.putString("strPesoC", strPeso);
                                    editor.putString("strCantidadC", edCantidad.getText().toString());
                                    editor.putString("strValorC", strValorDec);
                                    editor.putString("strValorFlete", dtValorTarifas.getStrSubImpuestoEn());
                                    editor.putString("strValorS", dtValorTarifas.getStrTotalImpuestos());
                                    editor.putString("strTotalC", dtValorTarifas.getStrTotalPagar());
                                    editor.putString("strTotalSin", dtValorTarifas.getStrTotalPagarEn());
                                    editor.putString("strBonoDescuento", "0");
                                    editor.putString("intPorcentaje", "0");
                                    editor.commit();
                                    Intent intent = new Intent(RegistroGuia.this, ListaDestinatarios.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                        alertDescuento.setNegativeButton("Cancelar", null);
                        AlertDialog dialog = alertDescuento.show();
                        dialog.setCanceledOnTouchOutside(false);
                    }
                });
                builder.setNegativeButton("Cancelar", null);

                AlertDialog dialog = builder.show();
                dialog.setCanceledOnTouchOutside(false);
                TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                messageView.setTextSize(20);
            }
            else{
                // SI NO TIENE VALORES VALIDOS

                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Aviso</span><span>"));

                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Tarifas Invalidas, Comuniquese con el administrador. </span><span>"));
                AlertDialog dialogE = builder.show();
                dialogE.setCanceledOnTouchOutside(true);
            }
            //messageView.setGravity(Gravity.CENTER);
            //txtVRG.setText("Total a pagar: " + dtValorTarifas.getStrTotalPagar());
            //Toast.makeText(getApplicationContext(),dtValorTarifas.getStrTotalPagar(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected ArrayList<ValorTarifa> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TARIFA_ENVIO);
            SoapObject result;
            request.addProperty("strOficina",strOficina);
            request.addProperty("strCiudadOri",strCiudadOri);
            request.addProperty("strPaisOri",strPaisOri);
            request.addProperty("strPaisDes",strPaisDes);
            request.addProperty("intCodProd",intCodProd);
            request.addProperty("intCodTienv",intCodTienv);
            request.addProperty("strPeso",strPeso);
            request.addProperty("strValorDec",strValorDec);
            request.addProperty("strValorAse",strValorAse);
            request.addProperty("strCiudadDest",strCiudadDest);
            request.addProperty("strBono",strBono);
            request.addProperty("strValorTotalPrimario", "0");
            request.addProperty("intCodCli", intCodCli);
            request.addProperty("intCantidad", Integer.parseInt(strCantidad));
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_TARIFA_ENVIO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento,
                    strSubImpuestoEn, strTotalImpuestosEn, strTotalPagarEn, strTotalDescuentoEn;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    dtValorTarifas = new ValorTarifa(s.getProperty("SUBIMPUESTOS").toString(), s.getProperty("TOTALIMPUESTOS").toString(),
                            s.getProperty("TOTALPAGAR").toString(), s.getProperty("TOTALCONDESCUENTO").toString(),
                            s.getProperty("SUBIMPUESTOSSINSIGNO").toString(), s.getProperty("TOTALIMPUESTOSSINSIGNO").toString(),
                            s.getProperty("TOTALPAGARSINSIGNO").toString(), s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString());


                    strSubImpuesto = s.getProperty("SUBIMPUESTOS").toString();
                    strTotalImpuestos = s.getProperty("TOTALIMPUESTOS").toString();
                    strTotalPagar = s.getProperty("TOTALPAGAR").toString();
                    strTotalDescuento = s.getProperty("TOTALCONDESCUENTO").toString();
                    strSubImpuestoEn = s.getProperty("SUBIMPUESTOSSINSIGNO").toString();
                    strTotalImpuestosEn = s.getProperty("TOTALIMPUESTOSSINSIGNO").toString();
                    strTotalPagarEn = s.getProperty("TOTALPAGARSINSIGNO").toString();
                    strTotalDescuentoEn = s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString();
                    valorTarifas.add(new ValorTarifa(strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento, strSubImpuestoEn, strTotalImpuestosEn,
                            strTotalPagarEn, strTotalDescuentoEn));

                }
            }
            return valorTarifas;

        }
    } //#END ENVIOCORPORATIVO

    public class TarifaEnvioConBonoAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ValorTarifa>> {

        String strOficina, strCiudadOri, strPaisOri, strPaisDes, strCiudadDest, strPeso, strValorDec,
                strValorAse, strNombreP, strNombreE, strBono, strPagarTotal, strCantidad;
        int intCodProd, intCodTienv;
        Double dbPeso, dbValorDec, dbValorSeg;
        Boolean blReutilizble;
        ProgressDialog progress;

        public TarifaEnvioConBonoAsyncTask(String strOficina, String strCiudadOri,
                                           String strPaisOri, String strPaisDes, int intCodProd, int intCodTienv, String strPeso,
                                           String strValorDec, String strValorAse, String strCiudadDest, String strNombreP,
                                           String strNombreE, String strBono, String strPagarTotal, Boolean blReutilizble, String strCantidad) {
            this.strOficina = strOficina;
            this.strCiudadOri = strCiudadOri;
            this.strPaisOri = strPaisOri;
            this.strPaisDes = strPaisDes;
            this.intCodProd = intCodProd;
            this.intCodTienv = intCodTienv;
            this.strPeso = strPeso;
            this.strValorDec = strValorDec;
            this.strValorAse = strValorAse;
            this.strCiudadDest = strCiudadDest;
            this.strNombreP = strNombreP;
            this.strNombreE = strNombreE;
            this.strBono = strBono;
            this.strPagarTotal = strPagarTotal;
            this.blReutilizble = blReutilizble;
            this.strCantidad = strCantidad;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(RegistroGuia.this);
            progress .setMessage("Calculando..");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<ValorTarifa> s) {
            super.onPostExecute(s);

            progress.cancel();
            final int[] checkedItem = {0};
            final int[] seleccionpositionR = {0};
            final String[] strValorD = new String[1];
            String strValor = null;

            if(!edValorSeg.getText().toString().trim().isEmpty()){
                if(!blClienteCorpo){

                    if(Integer.parseInt(edValorSeg.getText().toString()) > Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn())){

                        dtValorTarifas.setStrTotalImpuestoEn(edValorSeg.getText().toString().trim());
                        int total = Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) + Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn());
                        dtValorTarifas.setStrTotalPagarEn(String.valueOf(total));

                        DecimalFormat formatter = new DecimalFormat("$#,###.00");
                        dtValorTarifas.setStrTotalImpuestos(formatter.format(Double.parseDouble(edValorSeg.getText().toString().trim()))); // FORMATO PESOS
                        dtValorTarifas.setStrTotalPagar(formatter.format(Double.parseDouble(String.valueOf(total)))); // FORMATO PESOS
                    }
                }
                else
                {
                    dtValorTarifas.setStrTotalImpuestoEn(edValorSeg.getText().toString().trim());
                    int total = Integer.parseInt(dtValorTarifas.getStrSubImpuestoEn()) + Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn());
                    dtValorTarifas.setStrTotalPagarEn(String.valueOf(total));

                    DecimalFormat formatter = new DecimalFormat("$#,###.00");
                    dtValorTarifas.setStrTotalImpuestos(formatter.format(Double.parseDouble(edValorSeg.getText().toString().trim()))); // FORMATO PESOS
                    dtValorTarifas.setStrTotalPagar(formatter.format(Double.parseDouble(String.valueOf(total)))); // FORMATO PESOS
                }


            }

            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
            //AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
            builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Cotizacion</span><span>"));
            EditText edTemp = new EditText(RegistroGuia.this);
            edTemp.setText("0");
            int total = Integer.parseInt(dtValorTarifas.getStrTotalPagarEn().toString());
            if (blReutilizble == true) {

                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Seguro: $</span><span>" + dtValorTarifas.getStrTotalImpuestoEn() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Flete: $</span><span>" + dtValorTarifas.getStrSubImpuestoEn() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Total: $</span><span>" + dtValorTarifas.getStrTotalPagarEn() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Valor con Descuento:</p>"));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(35, 0, 35, 0);
                edTemp.setLeft(15);
                edTemp.setRight(15);
                edTemp.setInputType(InputType.TYPE_CLASS_NUMBER);
                edTemp.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(10)
                });
                edTemp.setLayoutParams(lp);
                //edTemp.setHint("Codigo");

                edTemp.setVisibility(View.VISIBLE);

                //edTemp.setText(dtValorTarifas.getStrTotalConBonoEn().toString());
                edTemp.setText(dtValorTarifas.getStrTotalPagarEn().toString());
                //int total = Integer.parseInt(dtValorTarifas.getStrTotalPagarEn().toString()) + Integer.parseInt(dtValorTarifas.getStrTotalConBonoEn().toString());
                // + Integer.parseInt(dtValorTarifas.getStrTotalConBonoEn().toString());
                edTemp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        if (edTemp.getText().toString().trim().length() > 0 && edTemp.getText().toString().trim().length() < 8) {
                            int intValorTemp = Integer.parseInt(edTemp.getText().toString());
                            int intValorTotal = total + Integer.parseInt(dtValorTarifas.getStrTotalConBonoEn());
                            if (intValorTemp >= total && intValorTemp <= intValorTotal) {
                                //int resutado =  total - Integer.parseInt(edTemp.getText().toString());
                                int resutado = Integer.parseInt(edTemp.getText().toString());
                                int flet = resutado - Integer.parseInt(dtValorTarifas.getStrTotalImpuestoEn().toString());
                                dtValorTarifas.setStrTotalPagarEn(String.valueOf(resutado));
                                dtValorTarifas.setStrSubImpuestoEn(String.valueOf(flet));

                                alert_desc.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Seguro: $</span><span>" + dtValorTarifas.getStrTotalImpuestoEn() + "</span>" +
                                        "<p><span style='color:#B22222; font-weight: bold;'>Flete: $</span><span>" + dtValorTarifas.getStrSubImpuestoEn() + "</span>" +
                                        "<p><span style='color:#B22222; font-size:45; font-weight: bold;'>Total: $</span><span>" + dtValorTarifas.getStrTotalPagarEn() + "</span>" +
                                        "<p><span style='color:#B22222; font-weight: bold;'>Valor con Descuento:</p>"));
                            } else {
                                if (edTemp.getText().toString().trim().length() < 2) {
                                    edTemp.setMaxLines(dtValorTarifas.getStrTotalConBonoEn().length() + 1);
                                    //Toast.makeText(getApplicationContext(), "No puede ser mayor al descuento recuerda que solo puedes realizar descuento menor este valor "+dtValorTarifas.getStrTotalConBonoEn().toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), "El descuento debe estar entre "+total+ " - "+intValorTotal, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='" + R.color.colorPrimary + "' ><b>" + "El descuento debe estar entre " + total + " - " + intValorTotal + "</b></font>"), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
                builder.setView(edTemp);
            } else {
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Seguro: </span><span>" + dtValorTarifas.getStrTotalImpuestos() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Flete: </span><span>" + dtValorTarifas.getStrSubImpuesto() + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Descuento:<span>" + dtValorTarifas.getStrTotalConBono() + "</spam></p>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Total: </span><span>" + dtValorTarifas.getStrTotalPagar() + "</span>"));
            }

            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int intValorTotal = total + Integer.parseInt(dtValorTarifas.getStrTotalConBonoEn());
                    float FltValorDesc = intValorTotal - Float.parseFloat(edTemp.getText().toString());
                    if (Float.parseFloat(dtValorTarifas.getStrTotalConBonoEn().toString()) >= FltValorDesc && Integer.parseInt(edTemp.getText().toString().trim()) <= intValorTotal) {

                        //float bonoPorcentaje = (Float.parseFloat(edTemp.getText().toString())/
                        //        (Float.parseFloat(edTemp.getText().toString()) + Float.parseFloat(dtValorTarifas.getStrTotalPagarEn())
                        //                - Float.parseFloat(dtValorTarifas.getStrTotalImpuestoEn())))* 100;
                        float bonoPorcentaje = (FltValorDesc /
                                (FltValorDesc + Float.parseFloat(dtValorTarifas.getStrTotalPagarEn())
                                        - Float.parseFloat(dtValorTarifas.getStrTotalImpuestoEn()))) * 100;

                        if (NetworkUtil.hayInternet(RegistroGuia.this)) {
                            DecimalFormat formatter = new DecimalFormat("$#,###.00");
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //CLIENTE CORP
                            editor.putString("strNombreC", objCliente.getStrNomCli());
                            editor.putString("strApellidoC", objCliente.getStrApellido());
                            editor.putString("strDireccionC", objCliente.getStrDireccion());
                            editor.putString("strDocumentoC", objCliente.getStrCedula());
                            editor.putString("strTelefonoC", objCliente.getStrCelCli());
                            editor.putString("strCompaniaC", objCliente.getStrCompania());

                            //editor.putString("strNombreC", objCliente.getStrCompania());
                            //editor.putString("strDireccionC", objCliente.getStrDireccion());
                            //editor.putString("strDocumentoC", objCliente.getStrCedula());
                            //editor.putString("strTelefonoC", objCliente.getStrTelcli());
                            editor.putInt("intCodCliN", objCliente.getIntCodCli());
                            editor.putBoolean("blClienteCorp", blClienteCorpo);
                            editor.putBoolean("blClienteCon", indCliCon);

                            editor.putString("strCiudadC", strCiudadDest);
                            editor.putString("strNombrePC", strNombreP);
                            editor.putString("strNombreEC", strNombreE);
                            editor.putInt("intCodProdC", intCodProd);
                            editor.putInt("intCodTienvC", intCodTienv);
                            editor.putString("strPesoC", strPeso);
                            editor.putString("intPorcentaje", String.valueOf(bonoPorcentaje));
                            editor.putString("strCantidadC", edCantidad.getText().toString());
                            editor.putString("strValorC", strValorDec);
                            editor.putString("strValorFlete", dtValorTarifas.getStrSubImpuestoEn());
                            editor.putString("strValorS", dtValorTarifas.getStrTotalImpuestos());
                            editor.putString("strTotalC", formatter.format(Double.parseDouble(dtValorTarifas.getStrTotalPagarEn())));
                            editor.putString("strTotalSin", dtValorTarifas.getStrTotalPagarEn());
                            editor.putString("strBonoDescuento", strBono);
                            editor.putString("strValorTotalPrimario", strPagarTotal);

                            editor.commit();
                            Intent intent = new Intent(RegistroGuia.this, listaClintesRemitente.class);
                            if (objCliente.getIntCodCli() != 0) {
                                Intent intent2 = new Intent(RegistroGuia.this, ListaDestinatarios.class);
                                startActivity(intent2);
                                finish();
                            } else {
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Sin conexion a internet.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Valor de descuento invalido.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("Cancelar", null);

            alert_desc = builder.create();
            alert_desc.show();
            alert_desc.setCanceledOnTouchOutside(false);
            //AlertDialog dialog = builder.show();
            //dialog.setCanceledOnTouchOutside(false);
            TextView messageView = (TextView) alert_desc.findViewById(android.R.id.message);
            messageView.setTextSize(20);

            //messageView.setGravity(Gravity.CENTER);
            //txtVRG.setText("Total a pagar: " + dtValorTarifas.getStrTotalPagar());
            //Toast.makeText(getApplicationContext(),dtValorTarifas.getStrTotalPagar(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected ArrayList<ValorTarifa> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TARIFA_ENVIO);
            SoapObject result;
            request.addProperty("strOficina",strOficina);
            request.addProperty("strCiudadOri",strCiudadOri);
            request.addProperty("strPaisOri",strPaisOri);
            request.addProperty("strPaisDes",strPaisDes);
            request.addProperty("intCodProd",intCodProd);
            request.addProperty("intCodTienv",intCodTienv);
            request.addProperty("strPeso",strPeso);
            request.addProperty("strValorDec",strValorDec);
            request.addProperty("strValorAse",strValorAse);
            request.addProperty("strCiudadDest",strCiudadDest);
            request.addProperty("strBono",strBono);
            request.addProperty("strValorTotalPrimario",strPagarTotal);
            request.addProperty("intCodCli",objCliente.getIntCodCli());
            request.addProperty("intCantidad", Integer.parseInt(strCantidad));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_TARIFA_ENVIO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento,
                    strSubImpuestoEn, strTotalImpuestosEn, strTotalPagarEn, strTotalDescuentoEn;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    dtValorTarifas = new ValorTarifa(s.getProperty("SUBIMPUESTOS").toString(), s.getProperty("TOTALIMPUESTOS").toString(),
                            s.getProperty("TOTALPAGAR").toString(), s.getProperty("TOTALCONDESCUENTO").toString(),
                            s.getProperty("SUBIMPUESTOSSINSIGNO").toString(), s.getProperty("TOTALIMPUESTOSSINSIGNO").toString(),
                            s.getProperty("TOTALPAGARSINSIGNO").toString(), s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString());


                    strSubImpuesto = s.getProperty("SUBIMPUESTOS").toString();
                    strTotalImpuestos = s.getProperty("TOTALIMPUESTOS").toString();
                    strTotalPagar = s.getProperty("TOTALPAGAR").toString();
                    strTotalDescuento = s.getProperty("TOTALCONDESCUENTO").toString();
                    strSubImpuestoEn = s.getProperty("SUBIMPUESTOSSINSIGNO").toString();
                    strTotalImpuestosEn = s.getProperty("TOTALIMPUESTOSSINSIGNO").toString();
                    strTotalPagarEn = s.getProperty("TOTALPAGARSINSIGNO").toString();
                    strTotalDescuentoEn = s.getProperty("TOTALCONDESCUENTOSINSIGNO").toString();
                    valorTarifas.add(new ValorTarifa(strSubImpuesto, strTotalImpuestos, strTotalPagar, strTotalDescuento, strSubImpuestoEn, strTotalImpuestosEn,
                                                    strTotalPagarEn, strTotalDescuentoEn));
                }
            }
            return valorTarifas;

        }
    }



    //Metodo de consumo de datos
    public class DatosBonoAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Bono>> {

        int intCodPro, intCodTien;
        String strBono, strPagarTotal, strOficina, strCantidad;
        public DatosBonoAsyncTask(String strBono, String strPagarTotal, String strOficina, int intCodPro, int intCodTien, String strCantidad) {
            this.strBono = strBono;
            this.strPagarTotal = strPagarTotal;
            this.strOficina = strOficina;
            this.intCodPro = intCodPro;
            this.intCodTien = intCodTien;
            this.strCantidad = strCantidad;
        }

        @Override
        protected void onPostExecute(ArrayList<Bono> s) {
            super.onPostExecute(s);

            Boolean blEstado = false;

            if(dtInfoBono.getBlReutilizable() == true)
                blEstado = true;

            new TarifaEnvioConBonoAsyncTask(strOficina, "BOG", "CO", "CO", intCodPro, intCodTien, edPeso.getText().toString(), edValorDecla.getText().toString(), edValorDecla.getText().toString(), strCiudadDe, strNombreProducto, strNombreEnvio, strBono, strPagarTotal, blEstado, strCantidad).execute();

        }

        @Override
        protected ArrayList<Bono> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_DATOS_BONO);
            SoapObject result;
            request.addProperty("strBono",strBono);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_DATOS_BONO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strBonoEn;
            Boolean blUtilizable, blReutilizable;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    dtInfoBono = new Bono(s.getProperty("BONO").toString(), Boolean.parseBoolean(s.getProperty("UTILIZADO").toString()),
                                            Boolean.parseBoolean(s.getProperty("REUTILIZABLE").toString()));

                    //strBonoEn = s.getProperty("BONO").toString();
                    //blUtilizable = Boolean.parseBoolean(s.getProperty("UTILIZADO").toString());
                    //blReutilizable = Boolean.parseBoolean(s.getProperty("REUTILIZABLE").toString());

                    dtBonos.add(dtInfoBono);

                }
            }
            return dtBonos;

        }
    }


    public class ListaClientesCorpAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        String intIdeCli;

        public ListaClientesCorpAsyncTask(String intIdeCli) {
            this.intIdeCli = intIdeCli;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
            builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Aviso</span><span>"));

            if(s == -1)
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Cliente Inactivo </span><span>"));
            else
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Documento Incorrecto </span><span>"));

            AlertDialog dialogE = builder.show();
            dialogE.setCanceledOnTouchOutside(true);

            if(s > 0){
                dialogE.cancel();
                String nom;
                if(objCliente.getStrNomCli().equalsIgnoreCase(""))
                    nom = objCliente.getStrCompania();
                else
                    nom = objCliente.getStrNomCli();

                new EnvioCorporativoAsyncTask(strOficinaOri, "BOG", "CO", "CO", Integer.parseInt(strCodPrd), Integer.parseInt(strEnvio), edPeso.getText().toString(), edValorDecla.getText().toString(), edValorDecla.getText().toString(), strCiudadDe, strNombreProducto, strNombreEnvio, "0", s, edCantidad.getText().toString(), nom).execute();
            }else{
                dialogE.show();
            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_CORP);

            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_CORP, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String intIdeCli,intCodCli;
            boolean indCliEst;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    intIdeCli = s.getProperty("IDECLI").toString();
                    intCodCli = s.getProperty("CODCLI").toString();
                    indCliEst = Boolean.parseBoolean(s.getProperty("INDCLIEST").toString());

                    if(intIdeCli.equalsIgnoreCase(this.intIdeCli) || intCodCli.equalsIgnoreCase(this.intIdeCli)){

                        if(indCliEst) {
                            indCliCon = Boolean.parseBoolean(s.getProperty("INDCLICON").toString());
                            objCliente.setIntCodCli(Integer.parseInt(s.getProperty("CODCLI").toString()));
                            //objCliente.setStrFecha(s.getProperty("FECCRECLI").toString());
                            //objCliente.setStrTelcli(s.getProperty("TELCLI").toString());
                            //objCliente.setStrCodCiu(s.getProperty("CODCIU").toString());
                            objCliente.setStrCedula(s.getProperty("IDECLI").toString());
                            objCliente.setStrCompania(NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIA").toString()));
                            objCliente.setStrApellido(NetworkUtil.validarAnytype(s.getProperty("APECLI").toString()));
                            objCliente.setStrNomCli(NetworkUtil.validarAnytype(s.getProperty("NOMCLI").toString()));
                            objCliente.setStrDireccion(s.getProperty("DIRCLI").toString());
                            objCliente.setStrCelCli(s.getProperty("CELCLI").toString());
                            return objCliente.getIntCodCli();
                        }
                        else
                            return -1; // CLIENTE INACTIVO
                    }
                }

            }
            return 0; // NO ENCONTRO EL CLIENTE O NO EXISTE EN LOS RESULTADOS
        }

    }


    public class ListaClientesAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        String intIdeCli;

        public ListaClientesAsyncTask(String intIdeCli) {
            this.intIdeCli = intIdeCli;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroGuia.this);
            builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Aviso</span><span>"));

            if(s == -1)
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Cliente Inactivo </span><span>"));
            else
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'> Documento Incorrecto </span><span>"));

            AlertDialog dialogE = builder.show();
            dialogE.setCanceledOnTouchOutside(true);

            if(s > 0){
                dialogE.cancel();
                String nom;
                if(objCliente.getStrNomCli().equalsIgnoreCase(""))
                    nom = objCliente.getStrCompania();
                else
                    nom = objCliente.getStrNomCli();

                new EnvioCorporativoAsyncTask(strOficinaOri, "BOG", "CO", "CO", Integer.parseInt(strCodPrd), Integer.parseInt(strEnvio), edPeso.getText().toString(), edValorDecla.getText().toString(), edValorDecla.getText().toString(), strCiudadDe, strNombreProducto, strNombreEnvio, "0", s, edCantidad.getText().toString(), nom).execute();
            }else{
                dialogE.show();
            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, CLIENTE_COD);

            SoapObject result;

            request.addProperty("CodCli", Integer.parseInt(intIdeCli));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + CLIENTE_COD, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            boolean indCliEst;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    indCliEst = Boolean.parseBoolean(s.getProperty("INDCLIEST").toString());

                    if(indCliEst) {
                        indCliCon = Boolean.parseBoolean(s.getProperty("INDCLICON").toString());
                        objCliente.setIntCodCli(Integer.parseInt(s.getProperty("CODCLI").toString()));
                        //objCliente.setStrFecha(s.getProperty("FECCRECLI").toString());
                        //objCliente.setStrTelcli(s.getProperty("TELCLI").toString());
                        //objCliente.setStrCodCiu(s.getProperty("CODCIU").toString());
                        objCliente.setStrCedula(s.getProperty("IDECLI").toString());
                        objCliente.setStrCompania(NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIA").toString()));
                        objCliente.setStrApellido(NetworkUtil.validarAnytype(s.getProperty("APECLI").toString()));
                        objCliente.setStrNomCli(NetworkUtil.validarAnytype(s.getProperty("NOMCLI").toString()));
                        objCliente.setStrDireccion(s.getProperty("DIRCLI").toString());
                        objCliente.setStrCelCli(s.getProperty("CELCLI").toString());
                        return objCliente.getIntCodCli();
                    }
                    else
                        return -1; // CLIENTE INACTIVO

                }

            }
            return 0; // NO ENCONTRO EL CLIENTE O NO EXISTE EN LOS RESULTADOS
        }

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

    /**
     * Validar seccion habilitada
     */
    private void seccionHabilitada(String strID) {
        try {
            String s = new ActivarSeccionDB.SeccionDBAsyncTask(strID, this, BASE_URL).execute().get();
            if(!(s.equalsIgnoreCase("True") || s.equalsIgnoreCase("true"))) {
                txtSeg.setVisibility(View.GONE);
                edValorSeg.setVisibility(View.GONE);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
