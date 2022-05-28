package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
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
import java.util.ArrayList;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class AdicionarQRActivity extends AppCompatActivity  implements LocationListener {
    private Button btnScan,btnSubir;
    String edo="0";
    TextView lblContent;
    IntentIntegrator qrScan;
    boolean escaneado=false;
    static String TAG="AdicionarQRActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/AdicionarQR";
    private static final String METHOD_NAME_ESTADOS = "Estados";
    private static final String METHOD_NAME = "AdicionarQR";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<Estado> estados = new ArrayList<>();
    String BASE_URL,PREFS_NAME;
    int idUsuario=0;
    String lat,lng,latActiv="0",lngActiv="0";
    LocationManager locationManager;
    Spinner spEstadosGuia;
    EstadosSpinnerAdapter mAdapterEstados;
    TextView text;
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
        setContentView(R.layout.activity_adicionar_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdicionarQRActivity.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Escanear QR");
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
        btnScan = findViewById(R.id.btnScan);
        btnSubir = findViewById(R.id.btnSubir);
        spEstadosGuia = (Spinner) findViewById(R.id.sprEstadosQr);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
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
                if(escaneado){
                    if(!lat.equalsIgnoreCase("0") || !lng.equalsIgnoreCase("0"))
                        new ActualizarQRAsyncTask(idUsuario,lblContent.getText().toString(),lat,lng).execute();
                    else
                        Toast.makeText(getApplicationContext(),"La localización debe estar activa",Toast.LENGTH_LONG).show();
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


    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer, ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            mAdapterEstados = new EstadosSpinnerAdapter(AdicionarQRActivity.this,s);
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
                   /* String codigo = object.getString("CODEST");
                    String estado = object.getString("NOMEST");

                    estados.add(new Estado(codigo,estado));*/

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

    public class ActualizarQRAsyncTask extends AsyncTask<Integer,Integer,String> {
        int idUsuario;
        String qr;
        String lat,lng;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Código QR subido correctamente",Toast.LENGTH_LONG).show();
            finish();
        }

        public ActualizarQRAsyncTask(int idUsuario, String qr, String lat, String lng) {
            this.idUsuario = idUsuario;
            this.qr = qr;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            request.addProperty("CodUsu", idUsuario);
            request.addProperty("strLatitud", lat);
            request.addProperty("strLongitud", lng);
            request.addProperty("strQR", qr);

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }

            result = (SoapObject) envelope.bodyIn;
            Log.d(TAG, result.toString());
            return result.toString();
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

}
