package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;

public class KilometrajeConductor extends AppCompatActivity {

    Toolbar toolbar;
    Button btnEnviar;
    TextView txtKilometraje;
    String strTexto, strTokenId, BASE_URL,PREFS_NAME;
    int idUsuario = 0;
    int intPlaca = 0;
    static String TAG=MenuActivity.class.getName();
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/KilometrajeConductor";
    private static final String METHOD_NAME = "KilometrajeConductor";
    private static final String NAMESPACE = "http://tempuri.org/";
    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;

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
        setContentView(R.layout.activity_kilometraje_conductor);

        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF),0);
        idUsuario = sharedPreferences.getInt("idUsuario", 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        txtKilometraje = (TextView) findViewById(R.id.EdKilometraje);
        btnEnviar = (Button) findViewById(R.id.btnEnviarKil);
        intPlaca = sharedPreferences.getInt("idPlaca",0);

        toolbar = (Toolbar) findViewById(R.id.toolbarpas);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KilometrajeConductor.this, ListaFiltrosActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Kilometraje - App SisColint "+ getResources().getString(R.string.versionApp));


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(NetworkUtil.hayInternet(KilometrajeConductor.this))
                {
                    strTexto = txtKilometraje.getText().toString();
                    if(strTexto.length() > 0)
                    {
                        String strKilometraje = txtKilometraje.getText().toString();
                        String latitud = sharedPreferences.getString("latEst","01");
                        String longitud = sharedPreferences.getString("lngEst","01");
                        strTokenId = FirebaseInstanceId.getInstance().getToken();

                        String os = Build.VERSION.RELEASE;
                        String manufacturer = Build.MANUFACTURER;
                        String model = Build.MODEL;
                        String app_version = BuildConfig.VERSION_NAME;
                        String Cadena = "{\"V1.2.3:\",\"version\":\"" + app_version + "\",\"platform\":\"Android\",\"version\":\"" + os + "\",\"model\":\"" + model + " - " + manufacturer + "\"}";
                        //Toast.makeText(getApplicationContext(), strTokenId, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Lat:"+latitud +",Lon:" + longitud , Toast.LENGTH_SHORT).show();
                        new SendKilometrajeAsyncTask(intPlaca,strKilometraje, idUsuario, latitud, longitud,strTokenId, Cadena).execute();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Para continuar con el proceso el campo no debe de estar vacio",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Sin conexion hay internet",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class SendKilometrajeAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strKilometraje, strLatitud, strLongitud, strTokenId, strObservacion;
        int intIdentidad;
        int idUsuario;




        public SendKilometrajeAsyncTask(int intIdentidad, String strKilometraje, int idUsuario, String strLatitud, String strLongitud, String strTokenId, String strObservacion)
        {
            this.strKilometraje = strKilometraje;
            this.intIdentidad = intIdentidad;
            this.idUsuario = idUsuario;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.strTokenId = strTokenId;
            this.strObservacion = strObservacion;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            btnEnviar.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Enviando Informacion", Toast.LENGTH_LONG).show();
        }




        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            btnEnviar.setEnabled(true);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Se proceso exitosamente", Toast.LENGTH_LONG).show();

                UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);

                btnEnviar.setEnabled(false);
                //Intent intent = new Intent(recordar_clave.this, LoginActivity.class);
                Intent intent = new Intent(KilometrajeConductor.this, Menuotros.class);
                startActivity(intent);
                finish();
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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("strDetalleCelular", strObservacion);
            request.addProperty("intIdentidad",intIdentidad);
            request.addProperty("strObservacion", strKilometraje);
            request.addProperty("idCodusu",idUsuario);
            request.addProperty("strLatitud",strLatitud);
            request.addProperty("strLongitud",strLongitud);
            request.addProperty("strIdentificarionId", strTokenId);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(SOAP_ACTION, envelope);
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
