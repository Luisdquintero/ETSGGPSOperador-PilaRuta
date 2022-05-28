package com.appetesg.estusolucionTranscarga.servicios;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.appetesg.estusolucionTranscarga.BuildConfig;
import com.appetesg.estusolucionTranscarga.MenuActivity;
import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelo_db.Monitoreo;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by RafaelCastro on 11/4/17.
 */

public class MonitoreoService extends Service implements GpsStatus.Listener {
    Context context;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    String imei;
    String distancia;
    boolean monitoreoOffline = false;
    double dist;
    int intervalo = 1;
    //eventos
    private static final String SOAP_ACTION = "http://tempuri.org/Geolocalizacion";
    private static final String METHOD_NAME = "Geolocalizacion";
    private static final String NAMESPACE = "http://tempuri.org/";

    private static final String SOAP_ACTION_TIEMPO = "http://tempuri.org/TiempoGeolocalizacion";
    private static final String METHOD_NAME_TIEMPO = "TiempoGeolocalizacion";


    private Timer timer = new Timer();
    double lat, lng;
    int _velocidad, _altitud, idUsuario;
    String BASE_URL, PREFS_NAME;
    Location lStart, lEnd;

    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    int grados;
    int totalSatelites = 0;
    LocationManager locationManager;
    SharedPreferences sharedpreferences;
    //String PREFS_NAME;
    private static String TAG = "MonitoreoService";
    Location net_loc;
    int precision = 5;
    int NOTIF_RASTREO = 998;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Proceso iniciado...");
        try {
            new TiempoGeoAsyncTask().execute();
        } catch (Exception ex) {
            intervalo = 1;
        }
        return START_STICKY;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        Log.d(TAG, "Proceso detenido...");
        super.onDestroy();

        //Cancelar la notificacion
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIF_RASTREO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //BASE_URL = this.getString(R.string.BASE_URL);
        PREFS_NAME = this.getString(R.string.SPREF);


        sharedpreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedpreferences.getString("urlColegio", "");
        idUsuario = sharedpreferences.getInt("idUsuario", 0);
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // We don't have permission so prompt the user
        if (permission != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(
                (Activity) context,
                PERMISSIONS_LOCATION,
                1);


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, precision * 1000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, precision * 1000, 0, listener);
        locationManager.addGpsStatusListener(this);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationManager.requestSingleUpdate(criteria, listener, null);


        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps_enabled && network_enabled) {
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (NetworkUtil.hayInternet(MonitoreoService.this)) {
                    Log.d("COORDENADAS", "" + sharedpreferences.getString("latEst", "0") + "," + sharedpreferences.getString("lngEst", "0"));
                    lat = Double.parseDouble(sharedpreferences.getString("latEst", "0"));
                    lng = Double.parseDouble(sharedpreferences.getString("lngEst", "0"));
                    if (lat != 0 && lng != 0) {
                        Date d = new Date();
                        Log.d(TAG, "" + idUsuario);
                        //Para el dummy, comentar
                        new LocalizarAsyncTask(idUsuario, d, String.valueOf(lat), String.valueOf(lng), BASE_URL).execute();
                        //Revisar que existan monitoreos offline
                        try {
                            List<Monitoreo> m = new Select().from(Monitoreo.class).execute();
                            if (m.size() > 0) {
                                //Hay por lo menos uno offline
                                //Para el dummy, comentar
                                new SubirDataAsyncTask().execute();
                            }

                        } catch (Exception ex) {

                        }

                    } else {
                        Log.d(TAG, "Data no enviada, coordenadas 0,0");
                    }

                } else {

                    //No hay internet, guardar la data en la base
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date f = Calendar.getInstance().getTime();
                    String hoy = formatter.format(f);
                    Monitoreo monitoreo = new Monitoreo();
                    monitoreo.idUsuario = idUsuario;
                    monitoreo.Fecha = hoy;
                    monitoreo.Latitud = String.valueOf(lat);
                    monitoreo.Longitud = String.valueOf(lng);
                    monitoreo.Distancia = distancia;
                    monitoreo.Velocidad = String.valueOf(_velocidad);
                    monitoreo.Enviado = 0;
                    Log.d("PROCESO", "Guardando data..." + String.valueOf(monitoreo.save()));

                }
            }
        }, 0, intervalo * 60 * 1000);
        mostrarNotificacion(NOTIF_RASTREO, context, "Su posición está siendo monitoreada");
    }

    //Si se quiere que la app lance una notificación en la barra superior
    //habilitar esta función.
    private void mostrarNotificacion(int numero, Context context, String mensaje) {

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "com.appetesg.estusolucionTranscarga";
            String CHANNEL_NAME = "Foreground Service Canal Monitoreo";
            String CHANNEL_DESCRIPTION = "Canal de monitoreo";

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.setShowBadge(false);

            mNotificationManager.createNotificationChannel(mChannel);


            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.satellite)
                    .setContentTitle("El monitoreo está activo")
                    .setContentText(mensaje)
                    .setOngoing(true)
                    .build();

            // Issue the notification.
            mNotificationManager.notify(numero, notification);

            startForeground(1, notification);

        } else {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MenuActivity.class), 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.satellite)
                            .setContentTitle("El monitoreo está activo")

                            .setContentText(mensaje);

            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();


            mBuilder.setContentIntent(contentIntent);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            mBuilder.setAutoCancel(false);

            mNotificationManager.notify(numero, mBuilder.build());
        }


    }


    @Override
    public void onGpsStatusChanged(int event) {
        int satellites = 0;
        int satellitesInFix = 0;

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    PERMISSIONS_LOCATION,
                    1);

        }


        int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
        //Log.i(TAG, "Time to first fix = " + timetofix);
        for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
            if (sat.usedInFix()) {
                satellitesInFix++;
            }
            satellites++;
        }

        totalSatelites = satellitesInFix;
        //Log.d(TAG, String.valueOf(totalSatelites));

    }


    private android.location.LocationListener listener = new android.location.LocationListener() {

        private Location mLastLocation;


        @Override
        public void onLocationChanged(Location pCurrentLocation) {
            lat = pCurrentLocation.getLatitude();
            lng = pCurrentLocation.getLongitude();

            //Log.d("CURRENTL",""+pCurrentLocation.getLatitude()+","+pCurrentLocation.getLongitude());


            _altitud = (int) pCurrentLocation.getAltitude();
            double speed = 0;
            if (this.mLastLocation != null)
                if (mLastLocation.getLatitude() != 0) {
                    Log.d("CURRENTL LOC", "" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                    Location loc1 = new Location("");
                    loc1.setLatitude(mLastLocation.getLatitude());
                    loc1.setLongitude(mLastLocation.getLongitude());
                    lat = mLastLocation.getLatitude();
                    lng = mLastLocation.getLongitude();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("latEst", String.valueOf(mLastLocation.getLatitude()));
                    editor.putString("lngEst", String.valueOf(mLastLocation.getLongitude()));
                    editor.commit();
                    Location loc2 = new Location("");
                    loc2.setLatitude(pCurrentLocation.getLatitude());
                    loc2.setLongitude(pCurrentLocation.getLongitude());

                    dist = loc1.distanceTo(loc2);

                    speed = Math.sqrt(
                            Math.pow(pCurrentLocation.getLongitude() - mLastLocation.getLongitude(), 2)
                                    + Math.pow(pCurrentLocation.getLatitude() - mLastLocation.getLatitude(), 2)

                    ) / (pCurrentLocation.getTime() - this.mLastLocation.getTime());


                }


            //if there is speed from location
            if (pCurrentLocation.hasSpeed())
                //get location speed
                speed = pCurrentLocation.getSpeed();
            //Log.d("CURRENTL V",""+speed);

            this.mLastLocation = pCurrentLocation;
            Location loc1 = new Location("");
            loc1.setLatitude(mLastLocation.getLatitude());
            loc1.setLongitude(mLastLocation.getLongitude());

            Location loc2 = new Location("");
            loc2.setLatitude(pCurrentLocation.getLatitude());
            loc2.setLongitude(pCurrentLocation.getLongitude());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("latEst", String.valueOf(pCurrentLocation.getLatitude()));
            editor.putString("lngEst", String.valueOf(pCurrentLocation.getLongitude()));

            float distanceInMeters = loc1.distanceTo(loc2);
            //Log.d("CURRENTL L",""+distanceInMeters);


            distancia = String.valueOf(dist);
            //velocidad = (int)(3.6*(distanceInMeters/precision));

            float v = Math.round(3.6 * speed);
            //float v1 = Math.round(3.6*(distanceInMeters/precision));

            editor.putString("velocidad", String.valueOf((int) v));
            editor.putString("distancia", distancia);
            editor.putString("latitud", "" + pCurrentLocation.getLatitude());
            editor.putString("longitud", "" + pCurrentLocation.getLongitude());
            editor.commit();

            _velocidad = (int) v;
            Log.d("MONITOREO", "" + _velocidad);
            //Toast.makeText(getApplicationContext(), ""+distanceInMeters, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.d(TAG, "Provider disabled");

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.d(TAG, "Status changed:" + status);

        }

    };


    public class LocalizarAsyncTask extends AsyncTask<Integer, Integer, Void> {

        int IdUsuario;
        Date Fecha;
        String Latitud, Longitud;
        String addr;

        String os = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String app_version = BuildConfig.VERSION_NAME;


        public LocalizarAsyncTask(int idUsuario, Date fecha, String latitud, String longitud, String addr) {
            IdUsuario = idUsuario;
            Fecha = fecha;
            Latitud = latitud;
            Longitud = longitud;
            addr = addr;
        }


        @Override
        protected Void doInBackground(Integer... integers) {

            Date f = Calendar.getInstance().getTime();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String hoy = formatter.format(f);
            //Log.d(TAG,hoy+","+Latitud+","+Longitud+","+_velocidad);
            String Velocidad = String.valueOf(_velocidad);
            String Distancia = distancia;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("IdUsuario", IdUsuario);
            request.addProperty("Fecha", hoy);
            request.addProperty("Latitud", Latitud);
            request.addProperty("Longitud", Longitud);
            request.addProperty("Velocidad", Velocidad);
            request.addProperty("Distancia", Distancia);
            request.addProperty("Observaciones", "{\"V1.2.3:\",\"version\":\"" + app_version + "\",\"platform\":\"Android\",\"version\":\"" + os + "\",\"model\":\"" + model + " - " + manufacturer + "\"}");
            //request.addProperty("Observaciones", "");
            Log.d(TAG, "{\"version\":\"" + app_version + "\",\"platform\":\"Android\",\"version\":\"" + os + "\",\"model\":\"" + model + " - " + manufacturer + "\"}");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }

            Object result = null;
            try {
                result = (Object) envelope.getResponse();
                Log.i(TAG, String.valueOf(result)); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }
    }

    //Clase para enviar datos de monitoreo guardados offline.
    public class SubirDataAsyncTask extends AsyncTask<Integer, Void, Integer> {

        ArrayList<Monitoreo> monitoreos;

        @Override
        protected Integer doInBackground(Integer... params) {
            llenaDatosMonitoreo();
            Log.d(TAG, "Total de monitoreos offline: " + String.valueOf(monitoreos.size()));

            return null;
        }


        public void llenaDatosMonitoreo() {
            monitoreos = new ArrayList<>();
            monitoreos.clear();
            //Seleccionar todos los datos del monitoreo guardados en la db
            List<Monitoreo> m = new Select().from(Monitoreo.class).execute();
            m.add(new Monitoreo());
            monitoreos.addAll(m);
            llamar();
        }

        public int llamar() {
            int r = 0;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            try {
                for (int i = 0; i < monitoreos.size(); i++) {
                    long id = monitoreos.get(i).getId();

                    String os = Build.VERSION.RELEASE;
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    String app_version = BuildConfig.VERSION_NAME;

                    request.addProperty("IdUsuario", monitoreos.get(i).idUsuario);
                    request.addProperty("Fecha", monitoreos.get(i).Fecha);
                    request.addProperty("Latitud", monitoreos.get(i).Latitud);
                    request.addProperty("Longitud", monitoreos.get(i).Longitud);
                    request.addProperty("Velocidad", monitoreos.get(i).Velocidad);
                    request.addProperty("Distancia", monitoreos.get(i).Distancia);
                    request.addProperty("Observaciones", "{\"v1.1.4: version\":\"" + app_version + "\",\"platform\":\"Android\",\"version\":\"" + os + "\",\"model\":\"" + model + " - " + manufacturer + "\"}");
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                    envelope.implicitTypes = true;
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;

                    HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
                    httpTransport.debug = true;

                    try {
                        httpTransport.call(SOAP_ACTION, envelope);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, e.getMessage());
                        e.printStackTrace();

                    }

                    Object result = null;
                    try {
                        result = (Object) envelope.getResponse();
                        Log.i(TAG, String.valueOf(result)); // see output in the console
                        if (String.valueOf(result).equalsIgnoreCase("true") ||
                                String.valueOf(result).equalsIgnoreCase("True")) {
                            new Delete().from(Monitoreo.class).where("id=" + id).execute();
                        }


                    } catch (SoapFault e) {
                        // TODO Auto-generated catch block
                        Log.e("SOAPLOG", e.getMessage());
                        e.printStackTrace();
                    }


                }

            } catch (Exception ex) {

            }

            return r;
        }

    }

    public class TiempoGeoAsyncTask extends AsyncTask<Integer, Integer, Integer> {


        @Override
        protected Integer doInBackground(Integer... integers) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_TIEMPO);
            //request.addProperty("Usuario", Usuario);
            int tiempo = 0;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 100000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION_TIEMPO, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            Object result = null;
            try {
                result = (Object) envelope.getResponse();
                Log.i(TAG, String.valueOf(result)); // see output in the console
                tiempo = Integer.parseInt(String.valueOf(result));


            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                tiempo = 0;
                intervalo = 1;
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                intervalo = 1;
            }


            return tiempo;
        }


        @Override
        protected void onPostExecute(Integer t) {
            super.onPostExecute(t);
            intervalo = t;
        }
    }
}
