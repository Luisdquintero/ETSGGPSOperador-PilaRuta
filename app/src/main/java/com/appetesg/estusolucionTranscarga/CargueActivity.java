package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appetesg.estusolucionTranscarga.adapter.CiudadesDesApapter;
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


public class CargueActivity extends AppCompatActivity  implements LocationListener {
    private Button btnScan,btnSubir, btnBuscar;
    String edo="0";
    String lblGuia;
    EditText lblContent;
    TextView lblCantRotulos;
    ListView lstGuias;
    IntentIntegrator qrScan;
    boolean escaneado=false;
    static String TAG="AdicionarQRActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/AdicionarQR";
    private static final String METHOD_NAME_REGIONALES = "ListaRegionales";
    private static final String METHOD_NAME_CARGUES = "ListaCarguesDestino";
    private static final String METHOD_NAME_GUIAS_DISP = "ListaGuiasDestino";
    private static final String METHOD_NAME_CARGAR_GUIA = "CargarGuia";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<Estado> estados = new ArrayList<>();
    String BASE_URL,PREFS_NAME;
    int idUsuario=0, intPiezasTotal = 0;
    String lat,lng,latActiv="0",lngActiv="0";
    LocationManager locationManager;
    Spinner sprDestinos, sprCargue;
    CiudadesDesApapter mListaCiudDest;
    ArrayList<RotulosGuia> listaRotulos = new ArrayList<>();
    Map<String, Integer> mapRotulos = new HashMap<String, Integer>();
    Map<String, Integer> mapRotulosPiezas = new HashMap<String, Integer>();
    String txtGuia = "";
    ListaRotulosAdapter mAdapter;
    ArrayList<CiudadesD> listCiudad = new ArrayList<>();
    ArrayList<String> listCargue = new ArrayList<>();
    ArrayList<String> listGuiasDisp = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CargueActivity.this, MenuLogistica.class);
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
        setContentView(R.layout.activity_cargue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CargueActivity.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Cargues");
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
        btnBuscar = findViewById(R.id.btnBuscar);
        btnSubir = findViewById(R.id.btnSubir);
        sprDestinos = (Spinner) findViewById(R.id.sprDestinos);
        sprCargue = (Spinner) findViewById(R.id.sprCargue);

        lblContent.requestFocus();

        //  Limpia la lista de guias escaneadas
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaRotulos.clear();
                intPiezasTotal = 0;
                lblCantRotulos.setText("Guias leidas: " + listaRotulos.size() + " Piezas: " + intPiezasTotal);
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

        @RequiresApi(api = Build.VERSION_CODES.N)
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
                    // Siempre tener el focus para escanear
                    lblContent.requestFocus();

                    // Valida vacios
                    if(!s.toString().isEmpty() && !s.toString().equalsIgnoreCase(""))
                    {
                        // Valida el input guia sea valido con la ultima version que incluye las piezas en el QR
                        if(s.toString().contains("/") || s.toString().contains("&"))
                        {
                            String[] strGuia = s.toString().trim().toUpperCase().split("&|/");

                            // NUMERO DE LA GUIA
                            lblGuia = strGuia[0];
                            // PIEZAS DE LA GUIA
                            String strCantidad = strGuia[1];
                            // Piezas para validar si enviar
                            int Npiezas = 0;

                            // Verifica si ya se ha escaneado la guia
                            if (mapRotulos.containsKey(lblGuia)) {
                                int cant = mapRotulos.get(lblGuia);
                                cant++;

                                // Valida que no exceda las piezas de la guia
                                if (cant <= Integer.parseInt(strCantidad)) {
                                    mapRotulos.replace(lblGuia, cant);
                                    mapRotulosPiezas.replace(lblGuia, Integer.parseInt(strCantidad));
                                    Npiezas = cant;

                                } else {
                                    // MENSAJE QUE LA GUIA NO TIENE TANTAS PIEZAS
                                    Toast.makeText(getBaseContext(), "Las piezas se han excedido.", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                // Valida que la guia sea valida en el destino
                                if(listGuiasDisp.contains(lblGuia)){

                                    // Agrega la guia por primera vez
                                    mapRotulos.put(lblGuia, 1);
                                    mapRotulosPiezas.put(lblGuia, Integer.parseInt(strCantidad));
                                    Npiezas = 1;
                                }
                                else
                                {
                                    dialorInformativo("La guia no se puede agregar a este cargue, verifique la disponibilidad de la guia o la regional seleccionada. ").show();
                                }
                            }

                            // Valida para enviar la guia recien escaneada
                            if(Npiezas == Integer.parseInt(strCantidad)){
                                //dialorInformativo("Guia completa enviando...").show();
                                new SendGuiaCargueAsyncTask(idUsuario ,lblGuia, sprCargue.getSelectedItem().toString()).execute();
                            }

                            listaRotulos.clear();
                            //listaRotulos.remove(lblGuia);
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
                    // Se carga en pantalla el listado
                    mAdapter = new ListaRotulosAdapter(CargueActivity.this, listaRotulos);
                    lstGuias.setAdapter(mAdapter);

                    // Vacia el campo visual de la ultima guia escaneada
                    lblContent.setText("");
                    timer.cancel();
                }
            }.start();

            // Contador de las piezas escaneadas
            intPiezasTotal = 0;
            mapRotulos.forEach((k,v) -> intPiezasTotal = intPiezasTotal + v);

            // Contador de guias escaneadas
            lblCantRotulos.setText("Guias leidas: " + listaRotulos.size() + " Piezas: " + intPiezasTotal);
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
        });

        // Trae el listado de cargues CREADOS
        new ListaCarguesDestAsyncTask(idUsuario).execute();

        // Trae el listado de regionales
        new ListaRegionalDestAsyncTask().execute();

        // Desplegable de ciudades destino
        sprDestinos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Object o = sprDestinos.getItemAtPosition(i);
//                Estado estado = (Estado)o;
//                edo = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CiudadesD x = (CiudadesD) sprDestinos.getSelectedItem();
                new ListaGuiasDestAsyncTask(idUsuario, x.getStrCodCiuDe()).execute();
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

    // Servicio para traer la lista de regionales
    public class ListaRegionalDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<CiudadesD>> {

        ProgressDialog progress;

        public ListaRegionalDestAsyncTask() {
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(CargueActivity.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<CiudadesD> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaCiudDest = new CiudadesDesApapter( CargueActivity.this, s);
            sprDestinos.setAdapter(mListaCiudDest);
        }

        @Override
        protected ArrayList<CiudadesD> doInBackground(Integer... integers) {
            try{
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_REGIONALES);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
                httpTransport.debug = true;

                try {
                    httpTransport.call(NAMESPACE + METHOD_NAME_REGIONALES, envelope);
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
                        strCodCiud = s.getProperty("CODREG").toString();
                        strNomCiu = s.getProperty("NOMREG").toString();
                        strOficina = s.getProperty("PUERTA_EMBARQUE").toString();
                        listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, strOficina));
                    }
                }
                else
                {
                    strCodCiud = "";
                    strNomCiu = "No hay ciudades disponibles o tarifas registradas.";
                    listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, ""));

                }
                return listCiudad;
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return listCiudad;
        }

    }
    // # END SERVICIO

    // Servicio para traer lista de cargues CREADOS
    public class ListaCarguesDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<String>> {

        int idUsuario;
        ProgressDialog progress;

        public ListaCarguesDestAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            try{
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_CARGUES);
                request.addProperty("intIdUsuario", idUsuario);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
                httpTransport.debug = true;

                try {
                    httpTransport.call(NAMESPACE + METHOD_NAME_CARGUES, envelope);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                result = (SoapObject) envelope.bodyIn;

                String strCodCargue;

                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

                if(DocumentElement.getPropertyCount() > 0) {

                    SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                    for (int i = 0; i < table1.getPropertyCount(); i++) {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        strCodCargue = s.getProperty("CODCARGUE").toString();
                        listCargue.add(strCodCargue);
                    }
                }
                else
                {
                    strCodCargue = "No hay cargues disponibles.";
                    listCargue.add(strCodCargue);

                }
                return listCargue;
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return listCargue;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(CargueActivity.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            adapter = new ArrayAdapter<String>(CargueActivity.this, android.R.layout.simple_list_item_1, s);
            sprCargue.setAdapter(adapter);
        }
    }
    // # END SERVICIO

    // Servicio para traer lista de guias disponibles segun REG
    public class ListaGuiasDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<String>> {

        int idUsuario;
        String strCodReg;
        ProgressDialog progress;

        public ListaGuiasDestAsyncTask(int idUsuario, String strCodReg) {
            this.idUsuario = idUsuario;
            this.strCodReg = strCodReg;
        }

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            try{
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GUIAS_DISP);
                request.addProperty("intIdUsuario", idUsuario);
                request.addProperty("strCodReg", strCodReg);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
                httpTransport.debug = true;

                try {
                    httpTransport.call(NAMESPACE + METHOD_NAME_GUIAS_DISP, envelope);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                result = (SoapObject) envelope.bodyIn;

                String strPedido1;

                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

                listGuiasDisp.clear();

                if(DocumentElement.getPropertyCount() > 0) {

                    SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                    for (int i = 0; i < table1.getPropertyCount(); i++) {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        strPedido1 = s.getProperty("PEDIDO1").toString();
                        listGuiasDisp.add(strPedido1);
                    }
                }
                else
                {
                    strPedido1 = "";
                    listGuiasDisp.add(strPedido1);

                }
                return listGuiasDisp;
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return listGuiasDisp;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(CargueActivity.this);
            progress .setMessage("Buscando...");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            String msg = "";

            // Valida que si haya guias disponibles
            if(s.get(0).equalsIgnoreCase("") || s.get(0).isEmpty())
                msg = "No hay guias disponibles, confirme la regional.";
            else
                msg = "Guias disponibles: " + s.size() + ", puede realizar el escaneo.";

            dialorInformativo(msg).show();
            //adapter = new ArrayAdapter<String>(CargueActivity.this, android.R.layout.simple_list_item_1, s);
            //sprCargue.setAdapter(adapter);
        }
    }
    // # END SERVICIO

    // Servicio para subir guia a un cargue
    public class SendGuiaCargueAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strPedido, strCodCargue;
        int idUsuario;

        public SendGuiaCargueAsyncTask(int idUsuario, String strPedido, String strCodCargue)
        {
            this.idUsuario = idUsuario;
            this.strPedido = strPedido;
            this.strCodCargue = strCodCargue;
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            // Validacion si fue exitoso el cargue de la guia
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                // Se elimina la guia.
                mapRotulos.remove(lblGuia);
                mapRotulosPiezas.remove(lblGuia);

                // Mensaje de enviada
                Toast.makeText(getApplicationContext(), "Guia enviada con exito.", Toast.LENGTH_LONG).show();
                //dialorInformativo("Guia enviada con exito.").show();

                // Se recarga la lista interna de guias para vaciar la recien enviada.
                CiudadesD x = (CiudadesD) sprDestinos.getSelectedItem();
                new ListaGuiasDestAsyncTask(idUsuario, x.getStrCodCiuDe()).execute();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();

            }
            else
            {
                // Mensaje en caso de fallo al enviar
                dialorInformativo(s).show();
                //Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_CARGAR_GUIA);

            request.addProperty("IdUsuario", idUsuario);
            request.addProperty("DocumentoReferencia",strPedido);
            request.addProperty("strCodCargue", strCodCargue);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME_CARGAR_GUIA, envelope);
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
    // # END SERVICIO

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
