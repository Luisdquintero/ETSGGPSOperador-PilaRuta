package com.appetesg.estusolucionTranscarga;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.RutaAdapter;
import com.appetesg.estusolucionTranscarga.modelos.ListaRutaMonitor;
import com.appetesg.estusolucionTranscarga.servicios.MonitoreoService;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ListaRutaActivity extends AppCompatActivity {
    ListView mListView;
    RutaAdapter mAdapter;

    ArrayList<ListaRutaMonitor> listaRuta = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    String res;
    static String TAG="ListaRutaActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaRutaMonitor";
    private static final String METHOD_NAME = "ListaRutaMonitor";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;

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
        setContentView(R.layout.activity_lista_ruta);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        idUsuario = sharedPreferences.getInt("idUsuario",0);
        Log.d("RUTA",""+idUsuario);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaRutaActivity.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista de Rutas - App SisColint "+ getResources().getString(R.string.versionApp));
        mListView = (ListView) findViewById(R.id.lstRuta);
        new ListarRutaAsyncTask(idUsuario).execute();

        // Inicia el GPS
        IniciarLocalicacion();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaRutaMonitor listaRutaMonitor =
                        (ListaRutaMonitor)mListView.getItemAtPosition(position);
                int idRuta = listaRutaMonitor.getId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idRuta",idRuta);
                editor.commit();

                    Intent intent = new Intent(ListaRutaActivity.this, MenuRutaActivity.class);
                    startActivity(intent);



            }
        });
    }

    public class ListarRutaAsyncTask extends AsyncTask<Integer,Integer,ArrayList<ListaRutaMonitor>> {
        int idUsuario;

        public ListarRutaAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaRutaMonitor> s) {
            super.onPostExecute(s);
            mAdapter = new RutaAdapter(ListaRutaActivity.this,s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<ListaRutaMonitor> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("IdUsuario", idUsuario);

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
            result = (SoapObject) envelope.bodyIn;


            SoapObject getListResponse = (SoapObject)result.getProperty(0);
            SoapObject DocumentElement = (SoapObject)getListResponse.getProperty(1);
            SoapObject table1 = (SoapObject)DocumentElement.getProperty(0);

            for (int i = 0; i < table1.getPropertyCount(); i++) {
                SoapObject s = (SoapObject) table1.getProperty(i);

                int id = Integer.parseInt(s.getProperty("ID").toString());
                String codigo_ruta = s.getProperty("CODIGO_RUTA").toString();
                String descripcion = s.getProperty("DESCRIPCION").toString();
                listaRuta.add(new ListaRutaMonitor(id,codigo_ruta,descripcion));
            }


            return listaRuta;
        }

    }

    /**
     * Inicia el servicio de GPS.
     */
    private void IniciarLocalicacion(){

        boolean estado = svcEjecutando(MonitoreoService.class);
        // Si ya esta transmitiendo no hace nada
        if (!estado) {
            //Para Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(ListaRutaActivity.this, MonitoreoService.class));
            } else {
                startService(new Intent(ListaRutaActivity.this, MonitoreoService.class));
            }
        }
    }

    /**
     * Valida si un servicio esta activo
     * @param serviceClass
     * @return
     */
    private boolean svcEjecutando(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
