package com.appetesg.estusolucionTranscarga;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class TasaVolumetrica extends AppCompatActivity {
    TextView txtVRG;
    EditText edAlto, edLargo, edAncho, edValorDecla, edCantidad;
    Button btnContinuar;
    ImageButton imgRegreso;
    int intIdUsuario = 0;
    String strCiudadDest = null, strNomCiuCo;
    SharedPreferences sharedPreferences;

    private static final String ACTION_TASA_VOLUMETRICA = "TasaVolumetrica";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaCiudadesDestino";
    String BASE_URL, PREFS_NAME;

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
        setContentView(R.layout.activity_tasa_volumetrica);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio" +
                "", "");


        //spCiudades = (Spinner) findViewById(R.id.lstCiudadDestino);
        intIdUsuario = sharedPreferences.getInt("idUsuario",0);
        edAlto = (EditText) findViewById(R.id.edAlto);
        edLargo = (EditText) findViewById(R.id.edLargo);
        edAncho = (EditText) findViewById(R.id.edAncho);
        edCantidad = (EditText)findViewById(R.id.edPiezasCo);
        edValorDecla = (EditText) findViewById(R.id.edValorDeclaradoCo);
        txtVRG = (TextView) findViewById(R.id.txtVRGCO);
        imgRegreso = (ImageButton)findViewById(R.id.btnReturnDesription);
        btnContinuar = (Button) findViewById(R.id.btnContinuarG1);
        strCiudadDest = sharedPreferences.getString("strCodCiuCO", "");
        strNomCiuCo = sharedPreferences.getString("strNomciuCO", "");

        imgRegreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasaVolumetrica.this, Menuotros.class);
                startActivity(intent);
            }
        });

        //setOnTouchListener

        edValorDecla.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edValorDecla.length() > 0) {
                    String vr = VRDecimal(Float.parseFloat(edValorDecla.getText().toString()));
                    txtVRG.setText("$"+vr);
                }
                else{
                    txtVRG.setText("");
                }
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edCantidad.length() > 0 && edAlto.length() > 0 && edValorDecla.length() > 0
                    && edLargo.length() > 0 && edAncho.length() > 0)
                {
                    String strCantidad = edCantidad.getText().toString();
                    String strAlto = edAlto.getText().toString();
                    String strLargo = edLargo.getText().toString();
                    String strAncho  = edAncho.getText().toString();
                    String strValor = edValorDecla.getText().toString();

                    new SendDatosTasaVolumenAyncTask(intIdUsuario, strAlto, strLargo, strAncho, strCantidad, strValor, strCiudadDest, strNomCiuCo).execute();

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Uno o mas campos incompletos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String VRDecimal(float valor)
    {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        return format.format(valor);
    }




    public class SendDatosTasaVolumenAyncTask extends AsyncTask<Integer, Integer, String>
    {


        int intCodusu;
        String strAlto, strAncho, strLargo, strCantidad, strValorDe, strCodCiuDest, strNomCiuCo;

        String strVacio = "";

        public SendDatosTasaVolumenAyncTask(int intCodusu, String strAlto, String strLargo, String strAncho, String strCantidad,
                                            String strValorDe, String strCodCiuDest, String strNomCiuCo)
        {
            this.intCodusu = intCodusu;
            this.strAlto = strAlto;
            this.strLargo = strLargo;
            this.strAncho = strAncho;
            this.strCantidad = strCantidad;
            this.strValorDe = strValorDe;
            this.strCodCiuDest = strCodCiuDest;
            this.strNomCiuCo = strNomCiuCo;
        }
        //Metodo en string


        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //progress.setVisibility(View.GONE);
            if(s.length() > 0) {
                //Toast.makeText(getApplicationContext(), "Su proceso fue exitoso Nro de guia: " + s, Toast.LENGTH_LONG).show();

                String strValor = null;
                String vr = VRDecimal(Float.parseFloat(s));
                AlertDialog.Builder builder = new AlertDialog.Builder(TasaVolumetrica.this);
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Cotizacion</span><span>"));
                builder.setMessage(Html.fromHtml("<p><h4><span style='color:#B22222; font-weight: bold;'>Ciudad Del Envio: </span></h4></p><p><span>"+strNomCiuCo+"</span></p>"
                        +"<p><h4><span style='color:#B22222; font-weight: bold;'>Valor Del Envio: </span></h4></p><p><span>"+"$"+vr+"</span></p>"                ));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(TasaVolumetrica.this, Menuotros.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.show();
                TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                messageView.setTextSize(20);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"El proceso de generar de guia no se pudo realizar", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TASA_VOLUMETRICA);

            request.addProperty("strAlto", strAlto);
            request.addProperty("strAncho",strAncho);
            request.addProperty("strLargo", strLargo);
            request.addProperty("strCantidad", strCantidad);
            request.addProperty("intCodusu", intCodusu);
            request.addProperty("strValorDe", strValorDe);
            request.addProperty("strCodCiuDest", strCodCiuDest);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_TASA_VOLUMETRICA, envelope);
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
