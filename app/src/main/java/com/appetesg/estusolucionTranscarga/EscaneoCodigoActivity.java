package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaRotulosAdapter;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.modelos.CiudadesD;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.modelos.RotulosGuia;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class EscaneoCodigoActivity extends AppCompatActivity  implements LocationListener {
    private Button btnScan,btnSubir;
    String edo="0";
    EditText lblContent;
    TextView lblCantRotulos;
    ListView lstGuias;
    IntentIntegrator qrScan;
    boolean escaneado=false;
    static String TAG="AdicionarQRActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/AdicionarQR";
    private static final String METHOD_NAME_ESTADOS = "EstadosEscaneo";
    private static final String METHOD_NAME_ESTADOS_GUIA = "ActualizarEstado";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<Estado> estados = new ArrayList<>();
    String BASE_URL,PREFS_NAME;
    int idUsuario=0;
    String lat,lng,latActiv="0",lngActiv="0";
    LocationManager locationManager;
    Spinner spEstadosGuia, spCiudad;
    EstadosSpinnerAdapter mAdapterEstados;
    ArrayList<RotulosGuia> listaRotulos = new ArrayList<>();
    Map<String, Integer> mapRotulos = new HashMap<String, Integer>();
    Map<String, Integer> mapRotulosPiezas = new HashMap<String, Integer>();
    String txtGuia = "";
    ListaRotulosAdapter mAdapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EscaneoCodigoActivity.this, MenuLogistica.class);
        startActivity(intent);
        finish();
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
        setContentView(R.layout.activity_escaneo_codigo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EscaneoCodigoActivity.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });
        ciudadUsu();

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Escaneo Codigo");
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        lat = sharedPreferences.getString("latEst","0");
        lng = sharedPreferences.getString("lngEst","0");
        getLocation();
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        lblContent = findViewById(R.id.lblContent);
        lblCantRotulos = findViewById(R.id.lblCantRotulos);
        lstGuias = findViewById(R.id.lstGuias);
        lblContent.setInputType(InputType.TYPE_NULL);
        btnScan = findViewById(R.id.btnScan);
        btnSubir = findViewById(R.id.btnSubir);
        spEstadosGuia = (Spinner) findViewById(R.id.sprEstadosQr);
        spCiudad = (Spinner) findViewById(R.id.sprCiudadQr);

        lblContent.requestFocus();

      /*ArrayList<String> ciu = new ArrayList();
        ciu.add("BOGOTA");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ciu);
        spCiudad.setAdapter(adapter);
        spCiudad.setEnabled(false);*/

        //  Limpia la lista de guias escaneadas
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaRotulos.clear();
                lblCantRotulos.setText("Guias leidas: " + listaRotulos.size());
                mapRotulos.clear();
                mapRotulosPiezas.clear();
                lstGuias.setAdapter(null);
            }
        });

        lblContent.addTextChangedListener(new TextWatcher()
        {
            CountDownTimer timer = null;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (timer != null)
            {
                timer.cancel();
            }

            timer = new CountDownTimer(500, 1000) {

                public void onTick(long millisUntilFinished)
                { }

                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onFinish()
                {
                    lblContent.requestFocus();

                    if(!s.toString().isEmpty() && !s.toString().equalsIgnoreCase(""))
                    {

                        if(s.toString().contains("/") || s.toString().contains("&")) {

                            String[] strGuia = s.toString().trim().toUpperCase().split("&|/");
                            String lblGuia = strGuia[0];  // NUMERO DE LA GUIA
                            String strCantidad = strGuia[1]; // PIEZAS DE LA GUIA

                            if (mapRotulos.containsKey(lblGuia)) {
                                int cant = mapRotulos.get(lblGuia);
                                cant++;
                                if (cant <= Integer.parseInt(strCantidad)) {
                                    mapRotulos.replace(lblGuia, cant);
                                    mapRotulosPiezas.replace(lblGuia, Integer.parseInt(strCantidad));
                                } else {
                                    // MENSAJE QUE LA GUIA NO TIENE TANTAS PIEZAS
                                    Toast.makeText(getBaseContext(), "Las piezas se han excedido.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                mapRotulos.put(lblGuia, 1);
                                mapRotulosPiezas.put(lblGuia, Integer.parseInt(strCantidad));
                            }

                            listaRotulos.clear();

                            // Imprimimos el Map con un Iterador
                            Iterator it = mapRotulos.keySet().iterator();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                listaRotulos.add(new RotulosGuia(mapRotulos.get(key) + " / " + mapRotulosPiezas.get(key),key));
                            }
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Numeracion de guia incorrecta.", Toast.LENGTH_LONG).show();
                        }

                    }
                    mAdapter = new ListaRotulosAdapter(EscaneoCodigoActivity.this, listaRotulos);
                    lstGuias.setAdapter(mAdapter);

                    lblContent.setText("");
                    timer.cancel();
                }
            }.start();
            lblCantRotulos.setText("Guias leidas: " + listaRotulos.size());
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
        });

        new ListarEstadosAsyncTask().execute();

        spEstadosGuia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = spEstadosGuia.getItemAtPosition(i);
                Estado estado = (Estado)o;
                edo = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialorInformativo("Recuerda reportar novedad en las guias incompletas.").show();

                Iterator it = mapRotulos.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String strLatitud = "0";
                    String strLongitud = "0";
                    String strFoto = "", strFirma = "";
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    new SendEstadosyncTask(idUsuario, key, date, Integer.parseInt(edo), strLatitud, strLongitud, strFirma, strFoto, "").execute();
                }
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No hay resultados", Toast.LENGTH_LONG).show();
                escaneado = false;
            } else {
                escaneado = true;
                lblContent.setText(result.getContents());
            }
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latActiv = String.valueOf(location.getLatitude());
        lngActiv = String.valueOf(location.getLongitude());
        Log.d("ESTADOS",latActiv+","+lngActiv);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void ciudadUsu(){
        ArrayList<String> ciu = new ArrayList();

        //String codigo = "BOG";
        String nomCiu = "BOGOTA";

        ciu.add(nomCiu);
        //mAdapterCiudad = new EstadosSpinnerAdapter(EscaneoCodigoActivity.this,ciu);
        //spCiudad.setAdapter(mAdapterCiudad);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ciu);
        //spCiudad.setAdapter(adapter);
        //spCiudad.setEnabled(false);
        //spCiudad.setVisibility(View.GONE);
    }


    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer, ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            mAdapterEstados = new EstadosSpinnerAdapter(EscaneoCodigoActivity.this,s);
            spEstadosGuia.setAdapter(mAdapterEstados);
        }

        @Override
        protected ArrayList<Estado> doInBackground(Integer... integers) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS);
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //Log.d(TAG,e.getMessage());
                e.printStackTrace();

            }

            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                // see output in the console
                Log.i(TAG,String.valueOf(envelope.getResponse()));

                res = String.valueOf(result);

                XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
                JSONObject jsonObject = xmlToJson.toJson();
                JSONObject DataSet = null;
                try {
                    DataSet = jsonObject.getJSONObject("NewDataSet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String table = DataSet.getString("ENV_ESTADO");
                JSONArray jsonArray = new JSONArray(table);

                Log.d(TAG,String.valueOf(jsonArray.length()));

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    String codigo = object.getString("CODEST");
                    String estado = object.getString("NOMEST");

                    estados.add(new Estado(codigo,estado));

                    //insertamos en la bd
                }


            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            catch (JSONException je){
                Log.e(TAG, je.getMessage());
            }
            return estados;

        }
    }


    public class SendEstadosyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strPedido, strLatitud, strLongitud, strRecibido, strImagen, strImagenGuia, strFecha;
        int idUsuario, intEstado;

        public SendEstadosyncTask(int idUsuario,String strPedido, String strFecha, int intEstado, String strLatitud, String strLongitud, String strImagen, String strImagenGuia,
                                  String strReibido)
        {
            this.idUsuario = idUsuario;
            this.strPedido = strPedido;
            this.strFecha = strFecha;
            this.intEstado = intEstado;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.strImagen = strImagen; // firma
            this.strRecibido = strReibido;
            this.strImagenGuia = strImagenGuia; // foto
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                // Mensaje de enviada
                Toast.makeText(getApplicationContext(), "Proceso exitoso.", Toast.LENGTH_LONG).show();

                // Se limpia la pantalla
                listaRotulos.clear();
                lblCantRotulos.setText("Guias leidas: " + listaRotulos.size());
                mapRotulos.clear();
                mapRotulosPiezas.clear();
                lstGuias.setAdapter(null);

                UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();
            }
            else
            {
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS_GUIA);

            request.addProperty("IdUsuario", idUsuario);
            request.addProperty("DocumentoReferencia",strPedido);
            request.addProperty("Fecha", strFecha);
            request.addProperty("Estado", intEstado);
            request.addProperty("Latitud", strLatitud);
            request.addProperty("Longitud", strLongitud);
            request.addProperty("Imagen", strImagen);
            request.addProperty("ImgenGuia",strImagenGuia);
            request.addProperty("srtRecibido", strRecibido);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS_GUIA, envelope);
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
                res = "Envio Fallido.";
            }

            return res;
        }

    }

    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(this);

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
