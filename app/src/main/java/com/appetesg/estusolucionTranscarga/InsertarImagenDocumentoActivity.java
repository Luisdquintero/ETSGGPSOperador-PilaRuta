package com.appetesg.estusolucionTranscarga;


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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class InsertarImagenDocumentoActivity extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 20;

    SharedPreferences sharedPreferences;
    int idUsuario, idDocumento, idPlaca,intTipoNotaq, intIdTercero;
    ImageButton btnImageCrear, btnImageDelete;
    ImageView imgFotoDocumento;

    Button btnEnviar;

    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG=InsertarImagenDocumentoActivity.class.getName();
    //SharedPreferences sharedpreferences;

    private static final String SOAP_ACTION = "http://tempuri.org/InsertarNotas";
    private static final String METHOD_NAME = "InsertarNotas";
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
        setContentView(R.layout.activity_insertar_imagen_documento);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgFotoDocumento = (ImageView)findViewById(R.id.imgFotoDocumento);
        btnImageCrear = (ImageButton)findViewById(R.id.imbFotoDocumento);
        btnImageDelete = (ImageButton)findViewById(R.id.imbFotoBorrarDocumento);
        btnEnviar = (Button)findViewById(R.id.btnEnviarfoto);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertarImagenDocumentoActivity.this, ListaDocumentosVe.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Insertar Imagen");
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = sharedpreferences.getString("urlColegio","");
        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        idDocumento = sharedPreferences.getInt("idDocumento",0);
        idPlaca = sharedPreferences.getInt("idPlaca",0);
        intTipoNotaq = sharedPreferences.getInt("intCodigoFiltro",0);
        btnImageCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        btnImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFotoDocumento.setImageResource(0);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.hayInternet(InsertarImagenDocumentoActivity.this))
                {
                    String strImagen;
                    Bitmap bmFoto;

                    try
                    {
                        bmFoto = ((BitmapDrawable)imgFotoDocumento.getDrawable()).getBitmap();
                        strImagen = convertirBitmapBase64(bmFoto);;
                    }
                    catch (Exception ex)
                    {
                        strImagen = "";
                    }

                    String strLatirud = sharedPreferences.getString("latitud","0");
                    String strLongitud = sharedPreferences.getString("longitud","0");
                    //int intTipoNotaq = 2;
                    String strObservacionq = "Cargue desde el app";

                    System.out.print("Identida:"+idPlaca);
                    System.out.print("Doc:"+idDocumento);
                    System.out.print("TipoN:" + intTipoNotaq);
                    System.out.print("Obse:"+ strObservacionq);
                    System.out.print("Latitud:" + strLatirud);
                    System.out.print("Longitud:" + strLongitud);
                    System.out.print("Imagen:" + strImagen);

                    Log.d(TAG,strImagen);
                    if(strImagen.length() > 0) {
                        if (intTipoNotaq == 2) {
                            //intIdentidad, int intTipoNota, string strObservacion, int intCodusu, string Latitud, string Longitud, int intTipoProceso, string Imagen
                            new DocumentoAsyncTask(idPlaca, intTipoNotaq, strObservacionq, idUsuario, strLatirud, strLongitud, idDocumento, strImagen).execute();
                        } else {
                            intIdTercero = sharedPreferences.getInt("idTercero", 0);
                            new DocumentoAsyncTask(intIdTercero, intTipoNotaq, strObservacionq, idUsuario, strLatirud, strLongitud, idDocumento, strImagen).execute();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No has cargado una imagen para continuar con el proceso.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Sin conexion a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openCameraIntent()
    {
        Intent pictureItem = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if(pictureItem.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(pictureItem, REQUEST_CAPTURE_IMAGE);
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
                imgFotoDocumento.setImageBitmap(imageBitmap);
            }
        }
    }

    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public class DocumentoAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intIdentidad, intTipoNota, intCodusu, intTipoProceso;
        String strObservacion, strLatitud, strLongitud, strImagen;

        public DocumentoAsyncTask(int intIdentidad, int intTipoNota, String strObservacion, int intCodusu, String strLatitud, String strLongitud, int intTipoProceso, String strImagen )
        {
            this.intIdentidad = intIdentidad;
            this.intTipoNota = intTipoNota;
            this.strObservacion = strObservacion;
            this.intCodusu = intCodusu;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.intTipoProceso = intTipoProceso;
            this.strImagen = strImagen;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            btnEnviar.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Enviando Proceso", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            btnEnviar.setEnabled(true);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Proceso Exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InsertarImagenDocumentoActivity.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Proceso Fallido", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            //intIdentidad, int intTipoNota, string strObservacion, int intCodusu, string Latitud, string Longitud, int intTipoProceso, string Imagen
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("intIdentidad",intIdentidad);
            request.addProperty("intTipoNota",intTipoNota);
            request.addProperty("strObservacion",strObservacion);
            request.addProperty("intCodusu",intCodusu);
            request.addProperty("Latitud",strLatitud);
            request.addProperty("Longitud",strLongitud);
            request.addProperty("intTipoProceso",intTipoProceso);
            request.addProperty("Imagen",strImagen);

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
