package com.appetesg.estusolucionTranscarga;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.servicios.MonitoreoService;
import com.appetesg.estusolucionTranscarga.utilidades.DrawingView;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ServiciosActivity extends AppCompatActivity {
ToggleButton tglEstado,tglVer;
TextView txtCodigo;
Button btnEnviar;
int estado=0;
Spinner sprEstados;
LinearLayout linearFirma;
ImageView imgFoto;
ImageButton imbFoto,imbFotoBorrar,imbLimpiarFirma;
EstadosSpinnerAdapter mAdapter;
ArrayList<Estado> estados = new ArrayList<>();
String edo="0";
RelativeLayout rlMas;
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    private static final String SOAP_ACTION = "http://tempuri.org/Servicios";
    private static final String METHOD_NAME = "Servicios";

    private static final String SOAP_ACTION_ESTADOS = "http://tempuri.org/Estados";
    private static final String METHOD_NAME_ESTADOS = "Estados";

    private static final String NAMESPACE = "http://tempuri.org/";
    static String BASE_URL,PREFS_NAME;
    static String TAG=ServiciosActivity.class.getName();
    SharedPreferences sharedpreferences;
    Toolbar toolbar;
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
        setContentView(R.layout.activity_servicios);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearFirma = (LinearLayout)findViewById(R.id.linearFirma);
        imgFoto = (ImageView)findViewById(R.id.imgFoto);
        imbFoto = (ImageButton)findViewById(R.id.imbFoto);
        imbFotoBorrar = (ImageButton)findViewById(R.id.imbFotoBorrar);
        imbLimpiarFirma = (ImageButton)findViewById(R.id.imbLimpiarFirma);
        sprEstados = (Spinner)findViewById(R.id.sprEstados);
        rlMas = (RelativeLayout)findViewById(R.id.rlMas);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiciosActivity.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });
        //Area para firmar
        DrawingView mDrawingView=new DrawingView(this);
        linearFirma.addView(mDrawingView);


        TextView lblTextoToolbar = (TextView)toolbar.findViewById(R.id.
                lblTextoToolbar);

        lblTextoToolbar.setText("Control Pago Servicios - App SisColint "+ getResources().getString(R.string.versionApp));

        //BASE_URL = this.getString(R.string.BASE_URL);
        PREFS_NAME = this.getString(R.string.SPREF);
        if(NetworkUtil.hayInternet(ServiciosActivity.this))
            new ListarEstadosAsyncTask().execute();

        sprEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = sprEstados.getItemAtPosition(position);
                Estado estado = (Estado)o;
                edo = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sharedpreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedpreferences.getString("urlColegio","");

        tglEstado = (ToggleButton) findViewById(R.id.tglEstado);
        tglVer = (ToggleButton) findViewById(R.id.tglVer);
        txtCodigo = (TextView)findViewById(R.id.txtCodigo);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);

        tglEstado.setChecked(true);
        tglVer.setChecked(true);
        tglEstado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    estado = 1;
                else
                    estado = 2;
            }
        });

        tglVer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    rlMas.setVisibility(View.GONE);
                else
                    rlMas.setVisibility(View.VISIBLE);
            }
        });

        imbLimpiarFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearFirma.removeAllViews();
                DrawingView mDrawingView=new DrawingView(ServiciosActivity.this);
                linearFirma.addView(mDrawingView);
            }
        });

        imbFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        imbFotoBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFoto.setImageResource(0);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.hayInternet(ServiciosActivity.this)){
                    if (svcEjecutando(MonitoreoService.class)){

                        String strFoto,strFirma;
                        Bitmap bmpFoto,bmpFirma;
                        //Nueva solicitud de servicios
                        linearFirma.setDrawingCacheEnabled(true);
                        linearFirma.buildDrawingCache();
                        try{
                            bmpFirma = linearFirma.getDrawingCache();
                            bmpFoto = ((BitmapDrawable)imgFoto.getDrawable()).getBitmap();
                            strFoto = convertirBitmapBase64(bmpFoto);
                            strFirma = convertirBitmapBase64(bmpFirma);
                        }catch (Exception ex){
                            strFoto = "";
                            strFirma = "";
                        }

                        //Convertir el bitmap a base 64
                        //Esto debe ser opcional



                        String codigo = txtCodigo.getText().toString();
                        if (codigo.length()>0){
                            int idUsuario = sharedpreferences.getInt("idUsuario",0);
                            String latitud = sharedpreferences.getString("latitud","0");
                            String longitud = sharedpreferences.getString("longitud","0");

                            Log.d(TAG,strFirma);
                            Log.d(TAG,strFoto);

                            if (estado!=2){
                                estado=1;
                            }
                            new ServiciosAsyncTask(idUsuario,latitud,longitud,String.valueOf(estado),codigo,Integer.parseInt(edo),strFirma,strFoto).execute();
                        }else{
                            Toast.makeText(getApplicationContext(),"El código no puede estar vacío",Toast.LENGTH_LONG).show();
                        }


                    }else{
                        Toast.makeText(getApplicationContext(),"El monitoreo debe estar activo para utilizar esta opción",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Necesita conexión a internet para esta funcionalidad",Toast.LENGTH_LONG).show();

                }
                //Revisar si el monitoreo está activo

            }
        });



    }

    private boolean svcEjecutando(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }


    public class ServiciosAsyncTask extends AsyncTask<Integer,Integer,String> {

        int IdUsuario;
        String Latitud,Longitud,estado,codigo;
        int estados;
        String srtRecibido,srtImagen;

        public ServiciosAsyncTask(int idUsuario, String latitud, String longitud, String estado, String codigo) {
            IdUsuario = idUsuario;
            Latitud = latitud;
            Longitud = longitud;
            this.estado = estado;
            this.codigo = codigo;
        }


        public ServiciosAsyncTask(int idUsuario, String latitud, String longitud, String estado, String codigo,
                                  int estados, String srtRecibido, String srtImagen) {
            IdUsuario = idUsuario;
            Latitud = latitud;
            Longitud = longitud;
            this.estado = estado;
            this.codigo = codigo;
            this.estados = estados;
            this.srtRecibido = srtRecibido;
            this.srtImagen = srtImagen;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnEnviar.setEnabled(false);
            Toast.makeText(getApplicationContext(),"Enviando información",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnEnviar.setEnabled(true);
            if (s.equalsIgnoreCase("True") || s.equalsIgnoreCase("true")){
                Toast.makeText(getApplicationContext(),"Proceso exitoso",Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(false);
                Intent intent = new Intent(ServiciosActivity.this,MenuActivity.class);

                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Proceso fallido",Toast.LENGTH_LONG).show();
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
            request.addProperty("Fecha", hoy);
            request.addProperty("Latitud", Latitud);
            request.addProperty("Longitud", Longitud);
            request.addProperty("Estado", estado);
            request.addProperty("Codigo", codigo);
            //nuevos
            request.addProperty("estados", estados);
            request.addProperty("srtRecibido", srtRecibido);
            request.addProperty("srtImagen", srtImagen);

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


    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imgFoto.setImageBitmap(imageBitmap);
            }
        }
    }
        public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Estado>> {


            public ListarEstadosAsyncTask() {
            }

            @Override
            protected void onPostExecute(ArrayList<Estado> s) {
                super.onPostExecute(s);
                mAdapter = new EstadosSpinnerAdapter(ServiciosActivity.this,s);
                sprEstados.setAdapter(mAdapter);
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
                    httpTransport.call(SOAP_ACTION_ESTADOS, envelope);
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

    /**
     * Capturar el error
     * @param thread
     * @param e
     */
    private void excepcionCapturada(Thread thread, Throwable e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        //LogErrorDB.LogError(sharedPreferences.getInt("idUsuario",0),errors.toString(), this.getClass().getCanonicalName(), BASE_URL, this);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }
}
