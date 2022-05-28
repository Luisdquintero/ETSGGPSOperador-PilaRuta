package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


public class cambio_clave extends AppCompatActivity {

    TextView txtNuevo, txtConfirmacion, txtToken;
    String strMensaje, strNuevo, strConfirmacion, strToken, strCorreo, BASE_URL,PREFS_NAME;;
    Toolbar toolbar;
    Button btnResetear;
    SharedPreferences sharedPreferences;
    int idUsuario;
    static String TAG=MenuActivity.class.getName();
    private static final String SOAP_ACTION = "http://tempuri.org/CambiarClave";
    private static final String METHOD_NAME = "CambiarClave";
    private static final String NAMESPACE = "http://tempuri.org/";
    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;
    ProgressDialog p;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(cambio_clave.this, LoginActivity.class);
        startActivity(intent);
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
        setContentView(R.layout.activity_cambio_clave);

        txtNuevo = (TextView) findViewById(R.id.etClaveNue);
        txtConfirmacion = (TextView) findViewById(R.id.etClaveConfi);
        txtToken = (TextView) findViewById(R.id.etToken);

        btnResetear = (Button) findViewById(R.id.btnResetear);

        toolbar = (Toolbar) findViewById(R.id.toolbarRes);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cambio_clave.this, recordar_clave.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        //idUsuario = sharedPreferences.getInt("idUsuario",0);
        int intValor = sharedPreferences.getInt("CambioClave",0);

        if(String.valueOf(intValor).equalsIgnoreCase("1"))
        {
            strCorreo = sharedPreferences.getString("email","");
        }
        else
        {
            txtToken.setText("00000");
            strCorreo = sharedPreferences.getString("EmailRegistro","");
        }

        TextView lblToolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);

        lblToolbar.setText("Olvidaste tu clave?");

        btnResetear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.hayInternet(cambio_clave.this))
                {
                    String Correo = String.valueOf(strCorreo);
                    strToken = txtToken.getText().toString();
                    strNuevo = txtNuevo.getText().toString();
                    strConfirmacion = txtConfirmacion.getText().toString();
                    //Toast.makeText(getApplicationContext(),Correo,Toast.LENGTH_LONG).show();

                    if(strConfirmacion.equals(strNuevo))
                        new PasswordAsyncTask(Correo, strToken, strNuevo, strConfirmacion).execute();
                    else{
                        Toast.makeText(getApplicationContext(), "Claves diferentes.", Toast.LENGTH_LONG);
                        p.cancel();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_LONG);
                    p.cancel();
                }
            }
        });

    }


    public class PasswordAsyncTask extends  AsyncTask<Integer, Integer, String>{

        String strCorreo, strToken,  strNuevaClave, strConfirmacionClave;

        public PasswordAsyncTask(String strCorreo, String strToken, String strNuevaClave, String strConfirmacionClave)
        {
            this.strCorreo = strCorreo;
            this.strToken = strToken;
            this.strNuevaClave = strNuevaClave;
            this.strConfirmacionClave = strConfirmacionClave;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(cambio_clave.this);
            p.setCanceledOnTouchOutside(true);
            //p.show(cambio_clave.this, "Procesando.../", "por favor esperar un momento.",false);
        }


        @Override
        protected  void onPostExecute(String s)
        {
            super.onPostExecute(s);
            btnResetear.setEnabled(true);
            p.cancel();

            if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("True") )
            {
                Toast.makeText(getApplicationContext(),"El cambio de clave fue exitoso", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(cambio_clave.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                p.cancel();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("strCorreo",strCorreo);
            request.addProperty("strToken", strToken );
            request.addProperty("strNuevaClave" , strNuevaClave);
            request.addProperty("strCofirmacionClave", strConfirmacionClave);

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
