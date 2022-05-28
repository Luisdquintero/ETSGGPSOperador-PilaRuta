package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Estado;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class EstadosDialogoActivity extends AppCompatActivity implements LocationListener {
    private static final String SOAP_ACTION_ESTADOS = "http://tempuri.org/Estados";
    private static final String METHOD_NAME_ESTADOS = "Estados";

    private static final String SOAP_ACTION_ACTUALIZAR = "http://tempuri.org/ActualizarEstadoEstudiante";
    private static final String METHOD_NAME_ACTUALIZAR = "ActualizarEstadoEstudiante";

    public int idEstudiante;
    public String nomEst;
    static String TAG="EstadosDialogoActivity";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String BASE_URL,PREFS_NAME;
    EstadosSpinnerAdapter mAdapter;
    ArrayList<Estado> estados = new ArrayList<>();
    Spinner sprEstados;
    Button btnCambiarEstado;
    SharedPreferences sharedpreferences;
    TextView lblTexto;
    String codEst;
    String lat,lng,latActiv="0",lngActiv="0";
    LocationManager locationManager;
    int flagEstado;
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
        setContentView(R.layout.activity_estados_dialogo);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedpreferences = this.getSharedPreferences(PREFS_NAME, 0);
        sprEstados = (Spinner)findViewById(R.id.sprEstados);
        lat = sharedpreferences.getString("latEst","0");
        lng = sharedpreferences.getString("lngEst","0");
        flagEstado = getIntent().getIntExtra("flagEstado",0);
        getLocation();
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedpreferences.getString("urlColegio","");

        new ListarEstadosAsyncTask().execute();
        idEstudiante = getIntent().getIntExtra("idEstCambio",0);
        nomEst = getIntent().getStringExtra("nomEst");
           Log.d("IDEST",""+idEstudiante);

        //nomEst = sharedpreferences.getString("nomEst","estudiante");
        btnCambiarEstado = (Button)findViewById(R.id.btnCambiarEstado);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CURRENT",lat+","+lng);
                if (latActiv!="0"){
                    lat=latActiv;
                }
                if (lngActiv!="0"){
                    lng=lngActiv;
                }
                if(!lat.equals("0") && !lng.equals("0") && estados.size()>0)
                    new ActualizarEstudianteAsyncTask(idEstudiante,codEst,lat,lng).execute();
                else
                    Toast.makeText(getApplicationContext(),"No se puede obtener su localización. Cierre esta ventana e intente de nuevo",Toast.LENGTH_LONG).show();
            }
        });

        sprEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Estado estado = (Estado)sprEstados.getItemAtPosition(position);
                codEst = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lblTexto = (TextView)findViewById(R.id.lblTexto);

        if (flagEstado==0 || flagEstado==901){
            lblTexto.setText("Recoger a "+nomEst+" en:");
            btnCambiarEstado.setText("Recoger");
        }
        if (flagEstado==10 || flagEstado==903){
            lblTexto.setText("Entregar a "+nomEst+" en:");
            btnCambiarEstado.setText("Entregar");
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


    public class ActualizarEstudianteAsyncTask extends AsyncTask<Integer,Integer,Void>{
        int idEstudiante;
        String estado;
        String lat,lng;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (flagEstado==0){
                Intent intent = new Intent(EstadosDialogoActivity.this, ListaEstudiantes0Activity.class);
                startActivity(intent);
            }
            if (flagEstado==10){
                Intent intent = new Intent(EstadosDialogoActivity.this, ListaEstudiantes10Activity.class);
                startActivity(intent);
            }
            if (flagEstado==901){
                Intent intent = new Intent(EstadosDialogoActivity.this, ListaEstudiantes901Activity.class);
                startActivity(intent);
            }
            if (flagEstado==903){
                Intent intent = new Intent(EstadosDialogoActivity.this, ListaEstudiantes903Activity.class);
                startActivity(intent);
            }
            finish();
        }

        public ActualizarEstudianteAsyncTask(int idEstudiante, String estado, String lat, String lng) {
            this.idEstudiante = idEstudiante;
            this.estado = estado;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            Date f = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String hoy = formatter.format(f);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ACTUALIZAR);
            SoapObject result;
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            request.addProperty("intTerceroEstudiante", idEstudiante);
            request.addProperty("Estado", estado);
            request.addProperty("Fecha", hoy);
            request.addProperty("Latitud", lat);
            request.addProperty("Longitud", lng);
            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION_ACTUALIZAR, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }

            result = (SoapObject) envelope.bodyIn;
            Log.d(TAG, result.toString());
            return null;
        }
    }


    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            mAdapter = new EstadosSpinnerAdapter(EstadosDialogoActivity.this,s);
            sprEstados.setAdapter(mAdapter);
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
                httpTransport.call(SOAP_ACTION_ESTADOS, envelope);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG,e.getMessage());
                e.printStackTrace();

            }

            catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.d(TAG,e.getMessage());
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
                JSONObject DataSet = jsonObject.getJSONObject("NewDataSet");
                String table = DataSet.getString("ENV_ESTADO");
                JSONArray jsonArray = new JSONArray(table);

                Log.d(TAG,String.valueOf(jsonArray.length()));
                Calendar now = Calendar.getInstance();
                int a = now.get(Calendar.AM_PM);

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);

                    String codigo = object.getString("CODEST");
                    String estado = object.getString("NOMEST");

                        if (flagEstado==903){
                            if(codigo.equals("0"))
                            estados.add(new Estado(codigo,estado));
                        }
                    if (flagEstado==0){
                        if(codigo.equals("10"))
                            estados.add(new Estado(codigo,estado));
                    }

                    if (flagEstado==10){
                        if(codigo.equals("901"))
                            estados.add(new Estado(codigo,estado));
                    }
                    if (flagEstado==901){
                        if(codigo.equals("903"))
                            estados.add(new Estado(codigo,estado));
                    }

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

    /**
     * Capturar el error
     * @param thread
     * @param e
     */
    private void excepcionCapturada(Thread thread, Throwable e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        //LogErrorDB.LogError(sharedPreferences.getInt("idUsuario",0),errors.toString(), this.getClass().getCanonicalName(), BASE_URL, this);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }
}
