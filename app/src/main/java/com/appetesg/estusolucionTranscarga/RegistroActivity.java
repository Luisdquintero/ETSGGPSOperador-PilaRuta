package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.IdentificacionSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Identificacion;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.github.clans.fab.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class RegistroActivity extends AppCompatActivity {


    Context context;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/DatosReservaServicio";
    private static final String METHOD_NAME = "Registro";
    private static final String METHOD_NAME_IDENTIFICACION = "TiposDeIdentificacion";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<Identificacion> listaDocumentos = new ArrayList<>();

    static String TAG="RegistroActivity";
    String BASE_URL,PREFS_NAME;
    EditText txtNombre, txtApellido, txtCedula, txtCelular;
    FloatingActionButton btnRegreso;
    CircleImageView clCall, clMesage;
    Spinner spIdentificacion;
    IdentificacionSpinnerAdapter mAdapterIdentificacion;
    Button btnRegistro;
    int idUsuario;
    String edo="0";

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
        setContentView(R.layout.activity_registro);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        txtNombre = (EditText)findViewById(R.id.txtNombreR);
        txtApellido = (EditText)findViewById(R.id.txtApellidoR);
        txtCedula = (EditText)findViewById(R.id.txtDocumento);
        txtCelular = (EditText)findViewById(R.id.txtCelularR);
        btnRegistro = (Button) findViewById(R.id.btnRegitroU);
        spIdentificacion = (Spinner) findViewById(R.id.spIdentificacion);

        new ListarIdentificacionAsyncTask().execute();


        spIdentificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = spIdentificacion.getItemAtPosition(position);
                Identificacion identificacion = (Identificacion)o;
                edo = String.valueOf(identificacion.getIdTipoIdentificacion());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(txtCedula.getText().toString().length() > 0 &&
                    txtNombre.getText().toString().length() > 0 && txtApellido.getText().toString().length() > 0
                    && txtCelular.getText().toString().length() > 0)
            {
                //String strCorreo_p = sharedPreferences.getString("strCorreo_p", "0");
                //String strCorreo_p = txtEnviarPas.getText().toString();
                //Toast.makeText(getApplicationContext(),strCorreo_p, Toast.LENGTH_LONG).show();
                //new SendEmailAsyncTask(strCorreo_p).execute();
                //Toast.makeText(getApplicationContext(),BASE_URL, Toast.LENGTH_LONG).show();
                new SendRegistroAsyncTask(txtNombre.getText().toString(), txtApellido.getText().toString(), txtCedula.getText().toString(), txtCelular.getText().toString()).execute();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Uno o mas campos incompletos", Toast.LENGTH_LONG).show();
            }


            }
        });



    }


    public class ListarIdentificacionAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Identificacion>> {


        public ListarIdentificacionAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Identificacion> s) {
            super.onPostExecute(s);
            mAdapterIdentificacion = new IdentificacionSpinnerAdapter(RegistroActivity.this,s);
            spIdentificacion.setAdapter(mAdapterIdentificacion);
        }

        @Override
        protected ArrayList<Identificacion> doInBackground(Integer... integers) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_IDENTIFICACION);
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE+METHOD_NAME_IDENTIFICACION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //Log.d(TAG,e.getMessage());
                e.printStackTrace();

            }

            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                // see output in the console
                Log.i(TAG,String.valueOf(envelope.getResponse()));

                res = String.valueOf(result);

                XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
                JSONObject jsonObject = xmlToJson.toJson();
                JSONObject DataSet = jsonObject.getJSONObject("NewDataSet");
                String table = DataSet.getString("TRP_TIPOIDENTIFICACION");
                JSONArray jsonArray = new JSONArray(table);

                Log.d(TAG,String.valueOf(jsonArray.length()));

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    String idIdentificacion = object.getString("ID");
                    String descipcion = object.getString("DESCRIPCION");

                    listaDocumentos.add(new Identificacion(Integer.parseInt(idIdentificacion),descipcion));
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
            return listaDocumentos;

        }
    }


    /*Enviar Informacion - Registro*/

    public class SendRegistroAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strNombre, strApellido, strCedula, strCelular;

        public SendRegistroAsyncTask(String strNombre, String strApellido, String strCedula, String strCelular)
        {
            this.strNombre = strNombre;
            this.strApellido = strApellido;
            this.strCedula = strCedula;
            this.strCelular = strCelular;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            btnRegistro.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Enviando Informacion", Toast.LENGTH_LONG).show();
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            btnRegistro.setEnabled(true);
            String[] strInformacion = s.toString().split(",");
            if(strInformacion[0].equalsIgnoreCase("True")|| strInformacion[0].equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Su proceso fue exitoso", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("EmailRegistro", strInformacion[1].toString());
                editor.putInt("CambioClave", 2);
                editor.commit();

                btnRegistro.setEnabled(false);
                //Toast.makeText(getApplicationContext(), strInformacion[1], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroActivity.this, cambio_clave.class);
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
            request.addProperty("strDocumento",strCedula);
            request.addProperty("strNombre", strNombre);
            request.addProperty("strApellido",strApellido);
            request.addProperty("strCelular",strCelular);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME, envelope);
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
