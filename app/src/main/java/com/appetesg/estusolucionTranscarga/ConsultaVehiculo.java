package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConsultaVehiculo extends AppCompatActivity {

    int idUsuario;
    Toolbar toolbar;
    RadioGroup radioGroup;
    String BASE_URL,PREFS_NAME;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6;
    Button btnValidar;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ConsultaVehiculo";
    private static final String METHOD_NAME = "ConsultaVehiculo";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG=ConsultaVehiculo.class.getName();
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
        setContentView(R.layout.activity_menuotros);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        radioGroup = (RadioGroup) findViewById(R.id.rgRadio);
        radioButton1 = (RadioButton) findViewById(R.id.rbSi);
        radioButton2 = (RadioButton) findViewById(R.id.rbNo);
        radioButton3 = (RadioButton) findViewById(R.id.rbPreventivaSi);
        radioButton4 = (RadioButton) findViewById(R.id.rbSoatSi);
        radioButton5 = (RadioButton) findViewById(R.id.rbLlantasSi);
        radioButton6 = (RadioButton) findViewById(R.id.rbCarreteraSi);
        btnValidar = (Button) findViewById(R.id.btnValidar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultaVehiculo.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextotoolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);
        lblTextotoolbar.setText("Consulta Vehiculo - App SisColint "+ getResources().getString(R.string.versionApp));

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtil.hayInternet(ConsultaVehiculo.this))
                {
                    int idUsuario = sharedPreferences.getInt("idUsuario",0);
                    int intTecnomecanica = 0;
                    int intPreventiva = 0;
                    int intSoat = 0;
                    int intLlantas = 0;
                    int intCarretera = 0;
                    if(radioButton1.isChecked() != false)
                    {
                        intTecnomecanica = 1;
                    }
                    if(radioButton3.isChecked()!= false)
                    {
                        intPreventiva = 1;
                    }
                    if(radioButton4.isChecked() != false)
                    {
                        intSoat = 1;
                    }

                    if(radioButton5.isChecked() != false)
                    {
                        intLlantas = 1;
                    }
                    if (radioButton6.isChecked() != false)
                    {
                        intCarretera = 1;
                    }

                    String StrTecnomecanica = Integer.toString(intTecnomecanica);
                    String StrPreventiva = Integer.toString(intPreventiva);
                    String StrSoat = Integer.toString(intSoat);
                    String StrLlantas = Integer.toString(intLlantas);
                    String StrCarretera = Integer.toString(intCarretera);

                    Log.d(TAG, StrTecnomecanica);
                    Log.d(TAG, StrPreventiva);
                    Log.d(TAG, StrSoat);
                    Log.d(TAG, StrLlantas);
                    Log.d(TAG, StrCarretera);
                    //Toast.makeText(getApplicationContext(), StrTecnomecanica, Toast.LENGTH_LONG).show();
                    new ServiciosAsyncTask(idUsuario, StrTecnomecanica, StrPreventiva, StrSoat, StrLlantas, StrCarretera).execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Necesita conexión a internet para esta funcionalidad", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public  class ServiciosAsyncTask extends AsyncTask<Integer,Integer,String> {
        int IdUsuario;
        String Tecnomecanica, Preventiva, Soat, Llantas, Carretera;


        public ServiciosAsyncTask(int idUsuario, String Strtecnomecanica, String StrPreventiva, String StrSoat, String StrLlantas,
                                  String StrCarretera) {
            IdUsuario = idUsuario;
            this.Tecnomecanica = Strtecnomecanica;
            this.Preventiva = StrPreventiva;
            this.Soat = StrSoat;
            this.Llantas = StrLlantas;
            this.Carretera = StrCarretera;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("True") || s.equalsIgnoreCase("true")){
                Toast.makeText(getApplicationContext(),"Proceso exitoso", Toast.LENGTH_LONG).show();
                btnValidar.setEnabled(false);
                Intent intent = new Intent(ConsultaVehiculo.this,MenuActivity.class);

                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Proceso fallido", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers) {
            Date f = Calendar.getInstance().getTime();
            String res = "";
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String hoy = formatter.format(f);
            //Log.d(TAG,hoy+","+Latitud+","+Longitud+","+_velocidad);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("IdUsuario", IdUsuario);
            request.addProperty("intTecnomecanica", Tecnomecanica);
            request.addProperty("intPreventiva", Preventiva);
            request.addProperty("intSoat", Soat);
            request.addProperty("intLlantas", Llantas);
            request.addProperty("intEquipoCarretera", Carretera);



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

            Object result = null;
            try {
                result = (Object)envelope.getResponse();
                Log.i(TAG, String.valueOf(result)); // see output in the console
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
