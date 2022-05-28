package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
//import android.support.v4.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.modelos.DatosReserva;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class DatosReservaActivity extends AppCompatActivity {
    Context context;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/DatosReservaServicio";
    private static final String METHOD_NAME = "DatosReservaServicio";
    private static final String METHOD_NAME_ESTADOS = "Estados";
    private static final String METHOD_NAME_ESTADOS_SERVICIO = "EstadoServiio";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<DatosReserva> listaServicios = new ArrayList<>();
    ArrayList<Estado> estados = new ArrayList<>();
    DatosReserva dtServicio;
    static String TAG="DatosReservaActivity";
    String BASE_URL,PREFS_NAME;
    TextView txtNumero, txtNombrePa, txtDireccionO, txtDireccionD, txtCelularP, txtHora, txtHoraCi, txtDescripcionR, txtTtitle ;
    EditText txtObs;
    FloatingActionButton btnRegreso;
    CircleImageView clCall, clMesage;
    Spinner spEstados;
    EstadosSpinnerAdapter mAdapterEstados;
    Button btnEnviar;
    int idUsuario;
    String edo="0";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(DatosReservaActivity.this, ListaServiciosActivity.class);
        startActivity(intent);
        finish();
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
        setContentView(R.layout.activity_datos_reserva);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        //txtNumero = (TextView)findViewById(R.id.txtNroService);
        txtNombrePa = (TextView)findViewById(R.id.txtNombrePac);
        txtCelularP = (TextView)findViewById(R.id.txtCelularPacien);
        txtDireccionO = (TextView)findViewById(R.id.txtDireccionOri);
        txtDireccionD = (TextView)findViewById(R.id.txtDireccionDes);
        txtHora = (TextView)findViewById(R.id.txtHoraLle);
        txtHoraCi = (TextView)findViewById(R.id.txtHoraCita);
        txtDescripcionR = (TextView)findViewById(R.id.txtDescripcionR);
        final String strCodido = sharedPreferences.getString("strCodigoR", "");
        final String strTipo = sharedPreferences.getString("strTipo", "");
        //btnRegreso = (FloatingActionButton)findViewById(R.id.btnRegreso);
        clCall = (CircleImageView) findViewById(R.id.circleCall);
        clMesage = (CircleImageView) findViewById(R.id.circleMesage);
        spEstados = (Spinner) findViewById(R.id.sprEstadosSer);
        txtTtitle = (TextView) findViewById(R.id.titlle);
        txtObs = (EditText) findViewById(R.id.txtObs);
        btnEnviar = (Button) findViewById(R.id.btnEnviarSer);

        if(strTipo.equalsIgnoreCase("cierre")){
            spEstados.setVisibility(View.GONE);
            btnEnviar.setVisibility(View.GONE);
            txtObs.setVisibility(View.GONE);
        }

        new ListaDatoReservasAsyncTask(strCodido).execute();

        clCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelularP.getText().toString() != "0") {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtCelularP.getText()));

                    if (ActivityCompat.checkSelfPermission(DatosReservaActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(DatosReservaActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // Se tiene permiso
                            } else {
                                ActivityCompat.requestPermissions(DatosReservaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                                return;
                            }
                        }
                    }
                    startActivity(intent);
                }
            }
        });


        clMesage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelularP.getText().toString() != "0") {
                    sendMessageToWhatsAppContact(txtCelularP.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No puedes enviar un mensaje ya que no contiene un numero de culular",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtCelularP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelularP.getText().toString() != "0") {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtCelularP.getText()));


                    if (ActivityCompat.checkSelfPermission(DatosReservaActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(DatosReservaActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // Se tiene permiso
                            } else {
                                ActivityCompat.requestPermissions(DatosReservaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                                return;
                            }
                        }
                    }
                    startActivity(intent);
                }
            }
        });


        new ListarEstadosAsyncTask().execute();

        spEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = spEstados.getItemAtPosition(position);
                Estado estado = (Estado)o;
                edo = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idUsuario = sharedPreferences.getInt("idUsuario",0);
                new SendEstadosyncTask(strCodido, idUsuario, Integer.parseInt(edo), txtObs.getText().toString()).execute();
            }
        });


    }



    private void sendMessageToWhatsAppContact(String number) {
        //PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String strMensaje = "Buen dia, soy el operador de San Gabriel asignado a su servicio.";
            String url = "https://api.whatsapp.com/send?phone=" + "57"+number + "&text=" + strMensaje;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ListaDatoReservasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<DatosReserva>> {

        String strCodigo;

        public ListaDatoReservasAsyncTask(String strCodigo) {
            this.strCodigo = strCodigo;
        }

        @Override
        protected void onPostExecute(ArrayList<DatosReserva> s) {
            super.onPostExecute(s);
            txtTtitle.setText(txtTtitle.getText()+" Nro: " + dtServicio.getNumServi());
            //txtNumero.setText(dtServicio.getNumServi());
            txtNombrePa.setText(dtServicio.getStrNombreServi());
            txtDireccionO.setText(dtServicio.getStrDireccionOServi());
            txtDireccionD.setText(dtServicio.getStrDireccionDServi());
            txtCelularP.setText(dtServicio.getStrCelularServi());
            txtHora.setText(dtServicio.getHoraServi());
            txtHoraCi.setText(dtServicio.getHoraLlegadaServi());
            txtDescripcionR.setText(dtServicio.getStrDescripcionRServi());
        }

        @Override
        protected ArrayList<DatosReserva> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("strCodigo",strCodigo);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + METHOD_NAME, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;



            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            String numServicio,  strNombreP, strCelularP, strDireccionO,strDireccionD, strhoraServicio, strhoraLlegada, strDescripcion;

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    dtServicio = new DatosReserva(s.getProperty("CODIGO").toString(),
                            s.getProperty("NOMBREPACIENTE").toString(), s.getProperty("CELULAR").toString(),
                            s.getProperty("DirOri").toString(),s.getProperty("DirDest").toString(),
                            s.getProperty("Hora").toString(), s.getProperty("HORACITA").toString(),
                            s.getProperty("Observa").toString());

                    numServicio = s.getProperty("CODIGO").toString();
                    strNombreP = s.getProperty("NOMBREPACIENTE").toString();
                    strCelularP =  s.getProperty("CELULAR").toString();
                    strDireccionO = s.getProperty("DirOri").toString();
                    strDireccionD = s.getProperty("DirDest").toString();
                    strhoraServicio = s.getProperty("Hora").toString();
                    strhoraLlegada = s.getProperty("HORACITA").toString();
                    strDescripcion= s.getProperty("Observa").toString();

                    listaServicios.add(new DatosReserva(numServicio, strNombreP, strCelularP, strDireccionO, strDireccionD, strhoraServicio, strhoraLlegada,strDescripcion));

                }
            }
            return listaServicios;

        }
    }


    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            mAdapterEstados = new EstadosSpinnerAdapter(DatosReservaActivity.this,s);
            spEstados.setAdapter(mAdapterEstados);
        }

        @Override
        protected ArrayList<Estado> doInBackground(Integer... integers) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS);
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS, envelope);
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
                String table = DataSet.getString("ENV_ESTADO");
                JSONArray jsonArray = new JSONArray(table);

                Log.d(TAG,String.valueOf(jsonArray.length()));

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    String codigo = object.getString("CODEST");
                    String estado = object.getString("NOMEST");

                    estados.add(new Estado(codigo,estado));
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
            return estados;

        }
    }


    public class SendEstadosyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strObservaiones, strCodigo;
        int idUsuario, intEstado;
        public SendEstadosyncTask(String strCodigo, int idUsuario, int intEstado, String strObservaiones)
        {
            this.strCodigo = strCodigo;
            this.idUsuario = idUsuario;
            this.intEstado = intEstado;
            this.strObservaiones = strObservaiones;
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True"))
            {
                Toast.makeText(getApplicationContext(), "El proceso fue exitoso", Toast.LENGTH_LONG).show();

                UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();

                Intent intent = new Intent(DatosReservaActivity.this, ListaServiciosActivity.class);
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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS_SERVICIO);
            /*string strCodigo, int intCodusu, int intEstado, string strObs*/
            request.addProperty("strCodigo", strCodigo);
            request.addProperty("intCodusu",idUsuario);
            request.addProperty("intEstado", intEstado);
            request.addProperty("strObs", strObservaiones);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS_SERVICIO, envelope);
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
