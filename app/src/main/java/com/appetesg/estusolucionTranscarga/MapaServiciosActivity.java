package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.modelos.ListaServicio;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MapaServiciosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaServicios";
    private static final String METHOD_NAME = "ListaServicios";
    private static final String NAMESPACE = "http://tempuri.org/";
    static int DIA=1;
    static int SEMANA=2;
    static int MES=3;
    static String TAG=MapaServiciosActivity.class.getName();
    String BASE_URL,PREFS_NAME;
    ArrayList<ListaServicio> listaServicios = new ArrayList<>();
    int idUsuario,filtro;
    SharedPreferences sharedPreferences;
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
        setContentView(R.layout.activity_maps);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        idUsuario = sharedPreferences.getInt("idUsuario",0);
        filtro = sharedPreferences.getInt("filtro",1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (NetworkUtil.hayInternet(this))
            new GraficarListaServiciosAsyncTask(idUsuario,filtro).execute();
        else
            Toast.makeText(getApplicationContext(),"Debes tener internet para utilizar esta función",Toast.LENGTH_LONG).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }






    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                        .width(2)
                        .color(Color.BLUE).geodesic(true));
            }

        }
        catch (JSONException e) {
            Log.d(TAG,e.getMessage());
        }
    }



    public class GraficarListaServiciosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<ListaServicio>> {
        int idUsuario;
        int  filtro;

        public GraficarListaServiciosAsyncTask(int idUsuario, int filtro) {
            this.idUsuario = idUsuario;
            this.filtro = filtro;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaServicio> svc) {
            super.onPostExecute(svc);
            for(int i=0; i<svc.size(); i++){
                Log.d(TAG,svc.get(i).getTipoServicio());

                LatLng mLatLng = new LatLng(svc.get(i).getLatitud(),svc.get(i).getLongitud());

                if(svc.get(i).getTipoServicio().equalsIgnoreCase("1"))
                mMap.addMarker(new MarkerOptions()
                        .position(mLatLng)
                        .title("Solicitado: "+svc.get(i).getHoraServicio())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );

                if(svc.get(i).getTipoServicio().equalsIgnoreCase("2"))
                    mMap.addMarker(new MarkerOptions()
                            .position(mLatLng)
                            .title("Cierre: "+svc.get(i).getHoraServicio())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    );

/*
                mMap.addMarker(new MarkerOptions()
                        .position(mLatLng)
                        .title("Solicitado: "+svc.get(i).getHoraServicio())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );*/


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,17f));
            }

        }

        @Override
        protected ArrayList<ListaServicio> doInBackground(Integer... integers) {
            Calendar c1 = Calendar.getInstance();
            //c1.set(Calendar.HOUR_OF_DAY,23);
            //c1.set(Calendar.MINUTE,59);

            Date f = null;
            Date f2;
            String res = "";
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String hoy = "";
            String antes="";
            //Log.d(TAG,hoy+","+Latitud+","+Longitud+","+_velocidad);

            if (filtro==DIA){
                Calendar c = Calendar.getInstance();
                //c.set(Calendar.HOUR_OF_DAY,0);
                //c.set(Calendar.MINUTE,0);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            if (filtro==SEMANA){
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-7);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            if (filtro==MES){
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-30);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            //Toast.makeText(getApplicationContext(),hoy+" "+antes,Toast.LENGTH_LONG).show();
            Log.d(TAG,hoy+","+antes);
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("intUsuario", idUsuario);
            request.addProperty("DtFechaServer", antes);
            request.addProperty("DtFechaServerfin", hoy);
            Log.d(TAG,hoy);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG,e.getMessage());
                e.printStackTrace();

            }

            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                // see output in the console
                //Log.i(TAG,String.valueOf(envelope.getResponse()));
                res = String.valueOf(result);

                XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
                JSONObject jsonObject = xmlToJson.toJson();
                JSONObject DataSet = jsonObject.getJSONObject("NewDataSet");
                try{
                    String table = DataSet.getString("Table");
                    JSONArray jsonArray = new JSONArray(table);

                    Log.d(TAG,String.valueOf(jsonArray.length()));

                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String lat = object.getString("LATITUD");
                        String lng = object.getString("LONGITUD");
                        String tipo = object.getString("ESTADO");
                        String fecha = object.getString("FECHA");

                        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String fechHora;
                        try {
                            Date convertedDate = sourceFormat.parse(fecha);
                            fechHora = destFormat.format(convertedDate);
                        }catch (Exception ex){
                            fechHora = "";
                        }

                        listaServicios.add(new ListaServicio(tipo,Double.parseDouble(lat),Double.parseDouble(lng),fechHora));
                    }
                }catch (Exception ex){
                    String table = DataSet.getString("Table");
                    Log.d(TAG,table);
                    JSONObject jsonObject1 = new JSONObject(table);
                    String lat = jsonObject1.getString("LATITUD");
                    String lng = jsonObject1.getString("LONGITUD");
                    String tipo = jsonObject1.getString("ESTADO");
                    String fecha = jsonObject1.getString("FECHA");

                    SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String fechHora;
                    try {
                        Date convertedDate = sourceFormat.parse(fecha);
                        fechHora = destFormat.format(convertedDate);
                    }catch (Exception e){
                        fechHora = "";
                    }

                    listaServicios.add(new ListaServicio(tipo,Double.parseDouble(lat),Double.parseDouble(lng),fechHora));

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
            return listaServicios;

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
