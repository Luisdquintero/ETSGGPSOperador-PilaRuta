package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.adapter.ListaUrlsAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Urls;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ConfiguracionActivity extends AppCompatActivity {
TextView lblMsj;
ListView lstUrls;

    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/TraerUrls";
    private static final String METHOD_NAME = "TraerUrls";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    ArrayList<Urls> items = new ArrayList<>();
    static String TAG="ConfiguracionActivity";
    int configurado=0;
    int idUsuario =0;
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
        setContentView(R.layout.activity_configuracion);
        PREFS_NAME = this.getString(R.string.SPREF);
        BASE_URL = "https://sisconet.co/zonacliente/Transcarga/WebService/General/Servicios/Mobile/Mobile.asmx";
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        configurado = sharedPreferences.getInt("sharedPreferences",0);

        lstUrls = (ListView) findViewById(R.id.lstUrl);
        lblMsj = (TextView)findViewById(R.id.lblMsj);
        lblMsj.setText("Seleccione la empresa");
        if(configurado>0){
            lblMsj.setText("Empresa seleccionado: "+sharedPreferences.getString("nomColegio",""));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idUsuario>0) {
                    Intent intent = new Intent(ConfiguracionActivity.this, MenuActivity.class);
                    startActivity(intent);
                }else{
                    finish();
                }
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Configuración - Seleccione Ruta");
        new ListarUrlsAsyncTask().execute();

        lstUrls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Urls urls = (Urls)lstUrls.getItemAtPosition(position);
                int _id = urls.getId();
                String _url = urls.getUrl();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("urlColegio",_url);

                editor.putString("nomColegio",urls.getNombre());
                editor.putString("RutaFuecUrl",urls.getRutaFuec());
                editor.putBoolean("BluetoothAct", urls.getBlBluetooth());
                editor.putInt("configurado",1);
                editor.commit();
                lblMsj.setText("Colegio seleccionado: "+urls.getNombre());
                if (idUsuario>0) {
                    Intent mainIntent = new Intent().setClass(ConfiguracionActivity.this, MenuActivity.class);
                    startActivity(mainIntent);
                }else {
                    Intent mainIntent = new Intent().setClass(ConfiguracionActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                }

            }
        });

    }



    public class ListarUrlsAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Urls>> {
        @Override
        protected void onPostExecute(ArrayList<Urls> u) {
            super.onPostExecute(u);
            ListaUrlsAdapter listaUrlsAdapter = new ListaUrlsAdapter(ConfiguracionActivity.this,u);
            lstUrls.setAdapter(listaUrlsAdapter);
        }



        @Override
        protected ArrayList<Urls> doInBackground(Integer... integers) {



            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            int retVal=0;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,100000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
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

            SoapObject result;
            try {
                result = (SoapObject) envelope.bodyIn;
                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
                Log.i(TAG, DocumentElement.toString());

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);
                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    try {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        int id = Integer.parseInt(s.getProperty("Id").toString());
                        String nombre = s.getProperty("Nombre").toString();
                        String url = s.getProperty("Url").toString();
                        String RutaFuec = s.getProperty("RutaFuec").toString();
                        Boolean BlBluetooth = Boolean.getBoolean(s.getProperty("Bluetooth").toString());
                        items.add(new Urls(id,nombre,url,RutaFuec, BlBluetooth));
                    }catch (Exception ex){
                    }
                }


            }catch (Exception ex){
                Log.i(TAG, ex.getMessage());
            }


            return items;
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
