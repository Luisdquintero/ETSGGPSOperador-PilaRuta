package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.AsyncTask;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class recordar_clave extends AppCompatActivity {
    Toolbar toolbar;
    Button btnEnviar, btnEnviarPas;
    TextView txtEnviar, txtEnviarPas;
    String strTexto, BASE_URL,PREFS_NAME;
    static String TAG=MenuActivity.class.getName();
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/RecuperarClave";
    private static final String METHOD_NAME = "RecuperarClave";
    private static final String METHOD_NAME_TIME_TOKEN = "UltimoTokenContrasena";
    private static final String NAMESPACE = "http://tempuri.org/";
    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;
    ProgressDialog p;

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(recordar_clave.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordar_clave);
        toolbar = (Toolbar)findViewById(R.id.toolbarpas);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recordar_clave.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        TextView lblToolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);
        txtEnviarPas = (TextView) findViewById(R.id.EtEmailPass);
        btnEnviarPas = (Button) findViewById(R.id.btnEnviar);
        lblToolbar.setText("Olvidaste tu clave?");

        btnEnviarPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(NetworkUtil.hayInternet(recordar_clave.this))
                {
                    strTexto = txtEnviarPas.getText().toString();
                    if(strTexto.length() > 0)
                    {
                        //String strCorreo_p = sharedPreferences.getString("strCorreo_p", "0");
                        String strCorreo_p = txtEnviarPas.getText().toString();
                        //Toast.makeText(getApplicationContext(),strCorreo_p, Toast.LENGTH_LONG).show();
                        new timePasswordToken(strCorreo_p).execute();
                        //new SendEmailAsyncTask(strCorreo_p).execute();
                        //Toast.makeText(getApplicationContext(),BASE_URL, Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Debes ingresar un correo", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Sin conexion hay internet", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(recordar_clave.this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(recordar_clave.this, cambio_clave.class);
                        startActivity(intent);
                        finish();
                    }
                })
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }

    public class SendEmailAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strEmail;

        public  SendEmailAsyncTask(String strEmail)
        {
            this.strEmail = strEmail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(recordar_clave.this);
            p.show(recordar_clave.this, "Procesando...", "por favor espera. Esto puede tardar un momento.", false, true);

        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
          super.onPostExecute(s);
          btnEnviarPas.setEnabled(true);
          p.cancel();
          if(s.equalsIgnoreCase("True"))
            {
                //Toast.makeText(getApplicationContext(), "Se envio exitosamente la clave", Toast.LENGTH_LONG).show();

                //UsuariosColegio usuarioColegio = new UsuariosColegio();
                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                //usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", strEmail);
                editor.putInt("CambioClave",1);
                editor.commit();
                btnEnviarPas.setEnabled(false);
                dialorInformativo("Se envio exitosamente el token, por favor " +
                        "revisar en sus SMS e ingresarlo en el campo correspondiente").show();
                //Intent intent = new Intent(recordar_clave.this, LoginActivity.class);
                //Intent intent = new Intent(recordar_clave.this, cambio_clave.class);
                //startActivity(intent);
                //finish();
            }
            else
            {
               Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("strCorreo_p", strEmail);

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

    public class timePasswordToken extends AsyncTask<Integer, Integer, String>
    {
        String strEmail;

        public  timePasswordToken(String strEmail)
        {
            this.strEmail = strEmail;
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            btnEnviarPas.setEnabled(true);
            if(s.equalsIgnoreCase("True"))
            {
               new SendEmailAsyncTask(strEmail).execute();
            }
            else if(!s.equalsIgnoreCase("false"))
            {
                Toast.makeText(getApplicationContext(), "TOKEN ENVIADO HACE "+s+" MIN, ESPERE PARA GENERAR OTRO.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_TIME_TOKEN);
            request.addProperty("strCorreo_p", strEmail);

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
                if(Integer.parseInt(res) > 8) // tiempo para recuperar clave en minutos
                    return "true";
                else
                    return res;
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            return res;
        }
    }
}
