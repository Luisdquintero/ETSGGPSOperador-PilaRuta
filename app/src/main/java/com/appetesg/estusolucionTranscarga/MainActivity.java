package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.receiver.NetworkChangeReceiver;
import com.appetesg.estusolucionTranscarga.servicios.LocationService;
import com.appetesg.estusolucionTranscarga.servicios.MonitoreoService;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    FloatingActionButton fabTx;
    Button btnGPS;
    String BASE_URL;
    //Db usdbh;
    //SQLiteDatabase db;
    ProgressDialog p;
    int idUsuario;
    SharedPreferences sharedPreferences;
    String TAG=MainActivity.class.getName();
    LocationManager locationManager;
    public static TextView txtVelocidad,txtDistancia,lblConectividad;
    public static RelativeLayout rlGPS;
    long startTime;
    public static ProgressDialog mProgressDialog;
    LocationService mLocationService;
    Toolbar toolbar;
    private BroadcastReceiver mNetworkReceiver,mGPSReceiver;
    private static final String ACTION_ADICIONAR_CHECK_LIST = "AdicionarCheckList";
    private static final String ACTION_TEMPERATURA = "TemperaturaConductor";
    private static final String NAMESPACE = "http://tempuri.org/";
boolean status;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mLocationService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Mapa de ubicación - App SisColint "+ getResources().getString(R.string.versionApp));
        mNetworkReceiver = new NetworkChangeReceiver();
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        //usdbh = new Db(MainActivity.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //habilitamos para escritur
        //db = usdbh.getWritableDatabase();

        Log.d(TAG,""+idUsuario);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        txtVelocidad = (TextView)findViewById(R.id.textView);
        txtDistancia = (TextView)findViewById(R.id.txtDistancia);
        lblConectividad = (TextView)findViewById(R.id.lblConectividad);
        rlGPS = (RelativeLayout) findViewById(R.id.rlGPS);
        fabTx = (FloatingActionButton) findViewById(R.id.fabTransmitir);
        btnGPS = (Button) findViewById(R.id.btnGPS);
        mapFragment.getMapAsync(this);

        get_question_online();
        get_question_pesv_online();



        fabTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean estado = svcEjecutando(MonitoreoService.class);
                //Se está ejecutando, preguntar si quiere apagarlo
                if (estado)

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Transmitir localización")
                            .setMessage("¿Desea detener la transmisión de su ubicación?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stopService(new Intent(MainActivity.this, MonitoreoService.class));
                                    Toast.makeText(getApplicationContext(),"El servicio de localización se ha desactivado",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancelar",null)
                            .show();
                else

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Transmitir localización")
                            .setMessage("¿Desea comenzar la transmisión de su ubicación?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(new Intent(MainActivity.this, MonitoreoService.class));
                                    } else {
                                        startService(new Intent(MainActivity.this, MonitoreoService.class));
                                    }
                                    Toast.makeText(getApplicationContext(),"El servicio de localización se ha activado",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancelar",null)
                            .show();


            }
        });


        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

    }


    public void get_question_online()
    {/*
        if(hasConnection(MainActivity.this)) {
            try {
                db = usdbh.getWritableDatabase();
                if (db != null) {
                    Cursor c = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca, strTemp from bioseguridad where pendiente = 1", null);
                    Cursor cTemp = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca, strTemp from bioseguridad where pendiente = 1", null);
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                new GuardarListaPruntasAsyncTask(c.getInt(3), c.getString(2), c.getInt(0), c.getInt(4)).execute();
                            } while (c.moveToNext());
                        }
                        if (cTemp.moveToFirst()) {
                            new SendTemperaturaAsyncTask(cTemp.getString(5), cTemp.getInt(3), cTemp.getInt(4)).execute();
                        }
                    }
                    if (c != null) {
                        c.close();
                        db.close();
                    }
                }
            } catch (SQLException ex) {
            }
        }*/
    }

    public void get_question_pesv_online()
    {/*
        if(hasConnection(MainActivity.this)) {
            try {
                db = usdbh.getWritableDatabase();
                if (db != null) {
                    Cursor c = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca from pesv where pendiente = 1", null);
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                new GuardarListaPruntasPesvAsyncTask(c.getInt(3), c.getInt(4), c.getString(2), c.getInt(0)).execute();
                            } while (c.moveToNext());
                        }
                    }
                    if (c != null) {
                        c.close();
                        db.close();
                    }
                }
            } catch (SQLException ex) {
            }
        }*/
    }


    public  boolean hasConnection(Context c) {

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;

    }

    private boolean svcEjecutando(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {

    }

    private android.location.LocationListener listener = new android.location.LocationListener() {




        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG,"La velocidad ha cambiado");
            double dSpeed = location.getSpeed() * 3.6;
            Log.d(TAG,String.valueOf(dSpeed));
            float bearing = location.getBearing();

            txtVelocidad.setText(String.valueOf(dSpeed)+" Km/h");

            if(location.hasSpeed()) {
                //velocidad = (int)(dSpeed*3.6);
                //lblVelocidad.setText(String.valueOf((int)dSpeed));
                //lblGrados.setText(String.valueOf(bearing));
            } else {
                //lblVelocidad.setText("0");
            }

            //lblCoordenadas.setText(String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()));

            mMap.clear();
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("strLatitude", String.valueOf(location.getLatitude()));
            editor.putString("strLongitude", String.valueOf(location.getLongitude()));
            editor.commit();

            mMap.addMarker(new MarkerOptions().position(latLng).title("Mi ubicación actual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
            Toast.makeText(getApplicationContext(), String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }



        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch(status)
            {
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("DEBUG", provider + " out of service");
                    //Toast.makeText(this, provider + " fuera de servicio", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("DEBUG", provider + " temp. unavailable");
                    //Toast.makeText(this, provider + " no disponible", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Log.d("DEBUG", provider + " available");
                    //Toast.makeText(this, provider + " disponible", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }

    };



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService();
        unregisterNetworkChanges();
    }

    private void GPSActivo(){
        try{
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
            if(gpsSignal==0){
                showGPSAlert();
            }
        }catch (Settings.SettingNotFoundException ex){
            ex.printStackTrace();
        }
    }



    private void showGPSAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Señal GPS")
                .setMessage("No tiene activo el GPS")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        GPSActivo();
        //mGPSReceiver = new GPSReceiver();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }





        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return;
        }


        if (status == false)
            //Here, the Location Service gets bound and the GPS Speedometer gets Active.
            bindService();
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Obteniendo ubicación...");
        mProgressDialog.show();




        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);



        /*
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 0, listener);
*/
        BASE_URL = this.getString(R.string.BASE_URL);
        int idUsuario=0;
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        Log.d(TAG,""+idUsuario);

    }

    //Metodo Pesv
    public class GuardarListaPruntasPesvAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario, idPregunta;
        int idPlaca;
        String strRespuesta;
        public GuardarListaPruntasPesvAsyncTask(int idUsuario, int idPlaca, String strRespuesta, int idPregunta) {
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
            this.strRespuesta = strRespuesta;
            this.idPregunta = idPregunta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(MainActivity.this);
            p.show(MainActivity.this, "Procesando...", "por esperar un momento.",false);
        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            super.onPostExecute(results);
            p.cancel();
            System.out.println("Luis Guzman Proceso exitoso de la guardar preguntas");
            finish();
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();


            SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST);
            SoapObject result;
            boolean resCumple = Boolean.parseBoolean(strRespuesta);

            Log.i(TAG, idPregunta + " : " + resCumple + " : " + currentDate);

            request.addProperty("intCodusu", idUsuario);
            request.addProperty("intIdpreguta", idPregunta);
            request.addProperty("blCumple", resCumple);
            request.addProperty("dtFechaReg", currentDate);
            request.addProperty("idVehiculo", idPlaca);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_ADICIONAR_CHECK_LIST, envelope);

                result = (SoapObject) envelope.bodyIn;

                if (result.hasProperty("AdicionarCheckListResult")) {
                    results.add(Boolean.valueOf(result.getPropertyAsString("AdicionarCheckListResult")));
                }

                results.add(false);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

                results.add(false);
            }



            return results;
        }

    }

    //Metodo De Bioseguridad
    public class GuardarListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario, idPregunta, idPlaca;
        String strRespuesta;



        public GuardarListaPruntasAsyncTask(int idUsuario, String strRespuesta, int idPregunta, int idPlaca) {
            this.idUsuario = idUsuario;
            this.strRespuesta =strRespuesta;
            this.idPregunta = idPregunta;
            this.idPlaca = idPlaca;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(MainActivity.this);
            p.show(MainActivity.this, "Procesando...", "por esperar un momento.",false);
        }
        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            p.cancel();
            super.onPostExecute(results);
            System.out.println("Luis Guzman Proceso exitoso de la guardar preguntas");
            finish();
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();


            SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST);
            SoapObject result;
            boolean resCumple = Boolean.parseBoolean(strRespuesta);

            request.addProperty("intCodusu", idUsuario);
            request.addProperty("intIdpreguta", idPregunta);
            request.addProperty("blCumple", resCumple);
            request.addProperty("dtFechaReg", currentDate);
            request.addProperty("idVehiculo", idPlaca);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_ADICIONAR_CHECK_LIST, envelope);

                result = (SoapObject) envelope.bodyIn;

                if (result.hasProperty("AdicionarCheckListResult")) {
                    results.add(Boolean.valueOf(result.getPropertyAsString("AdicionarCheckListResult")));
                }

                results.add(false);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

                results.add(false);
            }



            return results;
        }

    }

    public class SendTemperaturaAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strTemp;
        int idUsuario;
        int idPlaca;;
        public SendTemperaturaAsyncTask(String strTemp, int idUsuario, int idPlaca)
        {
            this.strTemp = strTemp;
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(MainActivity.this);
            p.show(MainActivity.this, "Procesando...", "por esperar un momento.",false);
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            p.cancel();
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                System.out.println("Luis Guzman Proceso exitoso de la temperatura8");
                Toast.makeText(getApplicationContext(), "Se transmitieron correctamente la informacion.", Toast.LENGTH_LONG).show();

                /*UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();*/

/*
                try
                {
                    db = usdbh.getWritableDatabase();
                    if (db != null)
                    {
                        try {
                            //insertamos en la base de datos
                            db.execSQL("delete from bioseguridad");

                        } catch (SQLException e) {}

                    }
                }catch (SQLException e)
                {

                }*/


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
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TEMPERATURA);
            request.addProperty("steTemperatura", strTemp);
            request.addProperty("idCodusu",idUsuario);
            request.addProperty("idVehiculo",idPlaca);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_TEMPERATURA, envelope);
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
