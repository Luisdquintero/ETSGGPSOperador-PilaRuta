
package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.modelos.DatosUsuario;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class PerfilActivity extends AppCompatActivity {
    String strMensaje;
    ArrayList<DatosUsuario> dtUsuario = new ArrayList<>();
    DatosUsuario datosUsuario;
    EditText edNombre, edApellido, edCorreo, edTelefono, edDocumento;
    TextView txtOficina;
    Toolbar toolbar;
    ProgressDialog p;
    TextView lblUsuario,lblMensaje, lblVersion;
    SharedPreferences sharedPreferences;
    CircleImageView imgPerfil;
    FloatingActionButton btnCamara;
    Button btnActualizar;
    static String TAG="PerfilActivity";
    private static final String SOAP_ACTION = "http://tempuri.org/Calificar_Agenda";
    private static final String METHOD_NAME = "Calificar_Agenda";
    private static final String ACTION_METHOD_ACTUALIZAR = "ActualizarImagen";
    private static final String ACTION_METHOD_DATOS = "TraerDatosImagen";

    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    RatingBar rbCalificar;
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
        setContentView(R.layout.activity_perfil);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        edNombre = (EditText) findViewById(R.id.txtNombreApp);
        edApellido = (EditText) findViewById(R.id.txtApellidoApp);
        edCorreo = (EditText) findViewById(R.id.txtEmailApp);
        edTelefono = (EditText) findViewById(R.id.txtCelularApp);
        edDocumento = (EditText) findViewById(R.id.txtDocumentoApp);
        lblVersion = (TextView) findViewById(R.id.lblVersionP);
        txtOficina = (TextView) findViewById(R.id.txtOficina);

        lblVersion.setText("v. " + BuildConfig.VERSION_NAME);

        int idUsuario = sharedPreferences.getInt("idUsuario",0);
        imgPerfil = (CircleImageView) findViewById(R.id.imageFotoPer);
        btnCamara = (FloatingActionButton) findViewById(R.id.btnCamaraP);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnActualizar = (Button) findViewById(R.id.btnActualizarP);
        //lblVersion = (TextView)findViewById(R.id.textView2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);


        lblTextoToolbar.setText("Perfil -  App SisColint "+ getResources().getString(R.string.versionApp));

        //lblVersion.setText("V"+ BuildConfig.VERSION_NAME);

        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);

       // btnCamara.setOnClickListener(new View.OnClickListener() {
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    } else {
                        TedBottomPicker.with(PerfilActivity.this)
                                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
                                {

                                    @Override
                                    public void onImageSelected(Uri uri) {

                                        final Uri url_picture = Uri.fromFile(new File(uri.getPath()));

                                        Picasso.get().load(url_picture).resize(700,800)




                                                .into(imgPerfil, new Callback()
                                                {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                        displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                    }


                                                });
                                    }

                                    public void onImageError(String message)
                                    {
                                        displayMessage("Intente nuevamente");
                                    }
                                });
                    }
                }
                else {
                    TedBottomPicker.with(PerfilActivity.this)
                            .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
                            {


                                public void onImageSelected(Uri uri) {
                                    // here is selected image uri
                                    final Uri url_picture = Uri.fromFile(new File(uri.getPath()));





                                    Picasso.get().load(url_picture).resize(300,300)
                                            .into(imgPerfil, new Callback()
                                            {
                                                @Override
                                                public void onSuccess() {
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                    displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                }


                                            });
                                }

                                public void onImageError(String message)
                                {
                                    displayMessage("Intente nuevamente");
                                }
                            });
                }
            }
        });


        //lblUsuario.setText("Usuario: "+sharedPreferences.getString("email","No tiene nombre de usuario"));
        /*try {
            new CalificacionAgendaAsyncTask(idUsuario).execute();
        }catch (Exception ex){

        }*/

        new DatosUsuarioAsyncTask(idUsuario).execute();

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edCorreo.getText().toString().length() > 0 && edNombre.getText().toString().length() > 0
                        && edApellido.getText().toString().length() > 0 && edTelefono.getText().toString().length() > 0)
                {
                    Bitmap btFoto;
                    String strFotoP;
                    try
                    {
                        //btFoto = Bitmap.createScaledBitmap(((BitmapDrawable) imgPerfil.getDrawable()).getBitmap(), 100, 100, false);
                        btFoto= ((BitmapDrawable)imgPerfil.getDrawable()).getBitmap();
                        strFotoP = convertirBitmapBase64(btFoto);

                    }
                    catch (Exception ex)
                    {
                        strFotoP = "";
                    }
                    System.out.println("LuisG"+strFotoP);
                    // Toast.makeText(getApplicationContext(), "llego", Toast.LENGTH_LONG).show();
                    new ActualizarPerfilAsyncTask(idUsuario, edNombre.getText().toString(),edApellido.getText().toString(),
                            edCorreo.getText().toString(), edTelefono.getText().toString(), strFotoP, edDocumento.getText().toString()).execute();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {

    }

    public class ActualizarPerfilAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intCodusu;
        String strNombre, strApellido, strCorreo, strTelefono, strImagen, strDocumento, strCiudad;

        public ActualizarPerfilAsyncTask(int intCodusu, String strNombre, String strApellido, String strCorreo, String strTelefono, String strImagen, String strDocumento) {
            this.intCodusu = intCodusu;
            this.strNombre = strNombre;
            this.strApellido = strApellido;
            this.strCorreo = strCorreo;
            this.strTelefono = strTelefono;
            this.strImagen = strImagen;
            this.strDocumento = strDocumento;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(PerfilActivity.this);
            p.show(PerfilActivity.this, "Procesando...", "por esperar un momento.",false);
        }
        @Override
        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            p.cancel();
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Proceso Exitoso", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PerfilActivity.this, MenuActivity.class);
                startActivity(intent);
            }
            else {
                strMensaje = "Hubo una incosistencia con la transmicion de datos, por favor comuniquese con el administrador.";
                dialorInformativo(s).show();
            }
            finish();
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_METHOD_ACTUALIZAR);

            request.addProperty("intCodusu", intCodusu);
            request.addProperty("strNombre",strNombre);
            request.addProperty("strApellido", strApellido);
            request.addProperty("strCorreo", strCorreo);
            request.addProperty("strTelefono", strTelefono);
            request.addProperty("strImagen", strImagen);
            request.addProperty("strDocumento", strDocumento);
            request.addProperty("strCiudad", strCiudad);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_METHOD_ACTUALIZAR, envelope);
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

    public class DatosUsuarioAsyncTask extends AsyncTask<Integer, Integer, ArrayList<DatosUsuario>> {

        int IdUsuario;

        public DatosUsuarioAsyncTask(int IdUsuario) {
            this.IdUsuario = IdUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<DatosUsuario> s) {
            super.onPostExecute(s);

            String strImagenPerfil = datosUsuario.getStrImagen();
            Bitmap bm = StringToBitMap(strImagenPerfil);

            if(bm != null)
            {
                imgPerfil.setImageBitmap(bm);
            }
            else
            {
                if(bm == null)
                {
                    imgPerfil.setImageResource(R.drawable.ic_launcher);
                }
            }

            edNombre.setText(datosUsuario.getStrNombre());
            edApellido.setText(datosUsuario.getStrApellido());
            edCorreo.setText(datosUsuario.getStrCorreo());
            edTelefono.setText(datosUsuario.getStrTelefono());
            edDocumento.setText(datosUsuario.getStrDocumento());
            txtOficina.setText("OFICINA: " + datosUsuario.getStrOficina());

        }

        @Override
        protected ArrayList<DatosUsuario> doInBackground(Integer... integers)
        {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_METHOD_DATOS);
            SoapObject result;
            request.addProperty("intCodusu",IdUsuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_METHOD_DATOS, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            int IdCoudu;
            String strNombre, strApellido, strCorreo, strTelefono, strImagen, strDocumento, strCiudad, strOficina;

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    datosUsuario= new DatosUsuario(Integer.parseInt(s.getProperty("CODUSU").toString()),s.getProperty("NOMUSU").toString(),s.getProperty("APEUSU").toString(),
                            s.getProperty("EMAILUSU").toString(),s.getProperty("TELMOVUSU").toString(), s.getProperty("IMAGENPERFIL").toString(),
                            s.getProperty("DOCUMENTO").toString(),s.getProperty("NOMCIU").toString(), s.getProperty("NOMOFI").toString());

                    IdCoudu = Integer.parseInt(s.getProperty("CODUSU").toString());
                    strNombre = s.getProperty("NOMUSU").toString();
                    strApellido = s.getProperty("APEUSU").toString();
                    strCorreo = s.getProperty("EMAILUSU").toString();
                    strTelefono = s.getProperty("TELMOVUSU").toString();
                    strImagen = s.getProperty("IMAGENPERFIL").toString();
                    strDocumento = s.getProperty("DOCUMENTO").toString();
                    strCiudad = s.getProperty("NOMCIU").toString();
                    strOficina = s.getProperty("NOMOFI").toString();

                    dtUsuario.add(new DatosUsuario(IdCoudu, strNombre, strApellido, strCorreo, strTelefono,
                            strImagen, strDocumento, strCiudad, strOficina));

                }
            }
            return dtUsuario;

        }

    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            if(bitmap == null)
            {
                return  null;
            }
            else {
                return bitmap;
            }
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }


    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(PerfilActivity.this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("Finalizado", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(PerfilActivity.this, Menuotros.class);
                        startActivity(intent);
                    }
                })
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }

    public void displayMessage(String toastString) {

        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(PerfilActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(PerfilActivity.this, android.Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(PerfilActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(PerfilActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fine == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED)
        {
            return true;

        }

        else
        {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(PerfilActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }


    public class CalificacionAgendaAsyncTask extends AsyncTask<Integer,Integer,String> {
        int idUsuario;


        public void parseXml(String str){
            try {

                boolean cancelacion=false;
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                String text="";
                StringBuilder builder = new StringBuilder();
                xpp.setInput( new StringReader(str) ); // pass input whatever xml you have
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tagname = xpp.getName();
                    switch (eventType) {
                        case XmlPullParser.TEXT:
                            text = xpp.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (tagname.equalsIgnoreCase("resultado")) {
                                builder.append("Fecha_Inicio = " + text);
                                Log.d("RESULTADO",text);

                            }



                            break;

                        default:
                            break;
                    }


                    eventType = xpp.next();
                }
                Log.d(TAG,"End document");


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String mensaje = "";
            Log.d("CALIFICAR", s);
            try {
                rbCalificar.setRating(Float.parseFloat(s));
                if (Integer.parseInt(s)==5){mensaje="Excelente Servicio";}
                if (Integer.parseInt(s)==4){mensaje="Buen Servicio";}
                if (Integer.parseInt(s)==3){mensaje="Servicio Regular";}
                if (Integer.parseInt(s)==2){mensaje="Mal Servicio";}
                if (Integer.parseInt(s)==1){mensaje="Servicio Muy Malo";}
                lblMensaje.setText(mensaje);
            }catch(Exception ex){
                lblMensaje.setText("No se pudo recuperar la calificación");
            }
        }

        public CalificacionAgendaAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;

        }

        @Override
        protected String doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Codusu", idUsuario);
            int calif = 0;
            String r2="0";
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 100000);
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

            Object result = null;
            try {
                result = (Object) envelope.getResponse();
                Log.i(TAG, String.valueOf(result)); // see output in the console
                String r = String.valueOf(result).replace("anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{NewDataSet=anyType{Table=anyType{","").replace("; }; }; }; }","");
                r2 = r.split("=")[1];

                try{
                    calif = Integer.parseInt(String.valueOf(r2));
                }catch (Exception ex){
                    Log.d("CALIFICAR",ex.getMessage());
                    calif=-1;
                }


            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());

                e.printStackTrace();
            }

            return String.valueOf(calif);

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
