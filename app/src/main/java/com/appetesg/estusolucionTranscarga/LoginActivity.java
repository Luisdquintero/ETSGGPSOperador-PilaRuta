package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.activeandroid.query.Select;
import com.appetesg.estusolucionTranscarga.adapter.ListaUrlsAdapter;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.modelos.Urls;
import com.appetesg.estusolucionTranscarga.utilidades.ActivarSeccionDB;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    ProgressBar progress;
    String email, password;
    TextView txtEmail, txtPasword, lblVersion, lblRecordad;
    Button btnLogin, btnRegistro;
    CheckBox chkRecordar;
    Spinner sprColegios;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesC;
    ProgressDialog progressDialog;
    ProgressDialog progresso;
    int idColegio = 0;
    private static final String SOAP_ACTION = "http://tempuri.org/Login";
    private static final String METHOD_NAME = "Login";
    private static final String SOAP_ACTION_URLS = "http://tempuri.org/TraerUrls";
    private static final String METHOD_NAME_URLS = "TraerUrls";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL_COLEGIOS = "https://sisconet.co/zonacliente/Transcarga/WebService/General/Servicios/Mobile/Mobile.asmx";
    Timer timer;
    ArrayList<Urls> items = new ArrayList<>();
    String BASE_URL, NAME_URL;
    String TAG = "LoginActivity";
    //JJCM LuzM, Luz2019
    //Luis, Luis2019
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            android.Manifest.permission.CAMERA
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("pasando pantallado");

        //timer.cancel();
        //System.exit(0);
        //finish();
    }

    public static void verificaPermisos(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionLocationB = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED  || permissionLocationB != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }

    @Override
    public void onBackPressed() {
        //progressDialog.dismiss();
        //timer.cancel();

        System.exit(0);
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
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        verificaPermisos(this);

        progress = (ProgressBar) findViewById(R.id.progress);
        int idUsuario = 0;

        GPSActivo();
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        sharedPreferencesC = getSharedPreferences(getString(R.string.title_cookies), MODE_PRIVATE);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio", "");
        NAME_URL = sharedPreferences.getString("nomColegio", "");
        idUsuario = sharedPreferences.getInt("idUsuario", 0);
        int recordar = sharedPreferences.getInt("recordar", 0);

        sprColegios = (Spinner) findViewById(R.id.sprColegios);
        chkRecordar = (CheckBox) findViewById(R.id.chkRecordar);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPasword = (TextView) findViewById(R.id.txtPassword);
        lblVersion = (TextView) findViewById(R.id.lblVersion);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        lblRecordad = (TextView)findViewById(R.id.lblRecordar);
        btnRegistro = (Button)findViewById(R.id.btnRegistro);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.getBoolean("expiro")){
                dialorInformativo("Por seguridad su sesion ha expirado, Porfavor inicie nuevamente.").show();
            }
        }

        lblVersion.setText("v."+(getResources().getString(R.string.versionApp)));
        if(NetworkUtil.hayInternet(this))
        {
            new ListarUrlsAsyncTask(URL_COLEGIOS).execute();
        }
        else
        {
            Toast.makeText(LoginActivity.this,"Conectese a internet, para poder usar la App",Toast.LENGTH_LONG).show();

            timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                    //For 3G check
                    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .isConnectedOrConnecting();
                    //For WiFi Check
                    boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnectedOrConnecting();


                    if (!is3g && !isWifi)
                    {
                        //Toast.makeText(getApplicationContext(),"Network Connection is OFF", Toast.LENGTH_LONG).show();
                        System.out.println("NO HAY INTERNET");
                    }
                    else
                    {
                        System.out.println("SI HAY INTERNET");
                        timer.cancel();
                        new ListarUrlsAsyncTask(URL_COLEGIOS).execute();

                    }
                }
            }, 0, 3000);

        }


        sprColegios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Urls urls = (Urls) sprColegios.getItemAtPosition(position);
                idColegio = urls.getId();
                String _url = urls.getUrl();
                BASE_URL = _url;
                Log.d(TAG, BASE_URL);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("urlColegio", _url);
                editor.putString("nomColegio", urls.getNombre());
                editor.putInt("configurado", 1);
                editor.putString("RutaFuecUrl", urls.getRutaFuec());
                editor.putBoolean("BluetoothAct", urls.getBlBluetooth());
                editor.commit();
                //Recuperar usuario y clave
                try {
                    if (idColegio > 0) {
                        List<UsuariosColegio> u = new Select().from(UsuariosColegio.class).where("idColegio=?", idColegio).execute();
                        if (u.size() > 0) {
                            txtEmail.setText(u.get(0).usuario);
                            txtPasword.setText(u.get(0).clave);
                        } else {
                            txtEmail.setText("");
                            txtPasword.setText("");
                        }
                    }

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Error combo:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (recordar == 1) {
            chkRecordar.setChecked(true);
            //Version anterior
            txtEmail.setText(sharedPreferences.getString("email", ""));
            txtPasword.setText(sharedPreferences.getString("clave", ""));
            //Recordar, usuario, clave y colegio
            if (idUsuario > 0) {
                List<UsuariosColegio> u = new Select().from(UsuariosColegio.class).where("idUsuario=?", idUsuario).execute();
                //Si no existe, crearlo
                if (u.size() <= 0) {
                    //txtEmail.setText(u.get(0).usuario);
                    //txtPasword.setText(u.get(0).clave);
                }
            }
        }


        chkRecordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        lblRecordad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.hayInternet(LoginActivity.this))
                {
                    if(idColegio > 0)
                    {
                        Log.d(TAG, BASE_URL);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        new AutenticarAsyncTask(BASE_URL, email, password,2).execute();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Debes seleccionar una empresa", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //new CheckInternetAsyncTask(getApplicationContext()).execute();
                    if (NetworkUtil.hayInternet(LoginActivity.this)) {

//                    progresso = new ProgressDialog(LoginActivity.this);
//                    progresso .setMessage("Cargando");
//                    progresso .show();
                        //progress.setVisibility(View.VISIBLE);
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(
                                android.R.color.transparent
                        );

                        email = txtEmail.getText().toString();
                        password = txtPasword.getText().toString();
                        if (email.length() > 0 && password.length() > 0 && idColegio > 0) {

                            // Valida la version del APP instalada contra BD
                            if (VersionActualizada("12")){

                                //Para el dummy, comentar
                                Log.d(TAG, BASE_URL);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (chkRecordar.isChecked()) {
                                editor.putInt("recordar", 1);
                                editor.putString("email", email);
                                editor.putString("clave", password);
                            } else {
                                editor.putInt("recordar", 0);
                            }
                            editor.commit();
                            //new AutenticarAsyncTask(BASE_URL, email, password,1).execute();
                            int intResultado = 1;
                            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                            request.addProperty("strUsuario", txtEmail.getText().toString());
                            request.addProperty("strClave", txtPasword.getText().toString());
                            int retVal = 0;
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.implicitTypes = true;
                            envelope.setOutputSoapObject(request);
                            envelope.dotNet = true;

                            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 10000);
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
                                int idUsuario = 0;
                                try {
                                    idUsuario = Integer.parseInt(String.valueOf(result));
                                } catch (Exception ex) {
                                    idUsuario = -1;
                                    retVal = -1;
                                }
                                //progresso.cancel();
                                if (intResultado == 1) {
                                    if (idUsuario > 0) {
                                        retVal = idUsuario;
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putInt("idUsuario", idUsuario);
                                        editor1.putString("email", email);
                                        editor1.putString("password", password);
                                        Log.d(TAG, "" + idUsuario);

                                        editor1.commit();
                                        //Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                                        sharedPreferencesC = getSharedPreferences(getString(R.string.title_cookies), MODE_PRIVATE);
                                        SharedPreferences.Editor ed = sharedPreferencesC.edit();
                                        ed.putString("cookies", "1");
                                        ed.commit();
                                        progressDialog.cancel();

                                        // Timer para expirar la sesion
                                        startCountdownTimer();

                                        // Login completado, llama la siguiente pantalla
                                        Intent intent = new Intent(LoginActivity.this, ListaPlacasActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {

                                        retVal = -1;
                                        Toast.makeText(LoginActivity.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                                        //progress.setVisibility(View.GONE);
                                        progressDialog.cancel();
                                    }
                                } else {

                                    retVal = 0;
                                    progressDialog.cancel();
                                    //Intent intent = new Intent(LoginActivity.this, recordar_clave.class);
                                    Intent intent = new Intent(LoginActivity.this, recordar_clave.class);
                                    startActivity(intent);
                                    finish();

                                }


                            } catch (SoapFault e) {
                                // TODO Auto-generated catch block
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(getApplicationContext(), "Error de servidor", Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }


                                //Para el dummy comentar el siguiente bloque

                            //Que el combo del colegio recuerde la contraseña
                            //encabezado de agenda


                            //*****

                        /*
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("idUsuario",1);
                        editor.putString("email",email);
                        editor.putString("password",password);

                        if (chkRecordar.isChecked()){
                            editor.putInt("recordar",1);
                        }else{
                            editor.putInt("recordar",0);
                        }

                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                        startActivity(intent);

                        //*****
*/
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Ambos campos son obligatorios", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No hay acceso a internet", Toast.LENGTH_LONG).show();
                    }
            }
        });
    }

    public void login(String _URL, String strUsuario, String strClave, int intResultado)
    {

    }
    public class AutenticarAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        String _URL, strUsuario, strClave;
        int intResultado;
        public AutenticarAsyncTask(String _URL, String strUsuario, String strClave, int intResultado) {
            this._URL = _URL;
            this.strUsuario = strUsuario;
            this.strClave = strClave;
            this.intResultado = intResultado;
        }
        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);

            pDialog.setTitle("Iniciando sesión");
            pDialog.setMessage("Espere un momento");
            pDialog.setIndeterminate(false);
            pDialog.setIcon(R.drawable.ic_launcher);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Integer r) {
            super.onPostExecute(r);
            if (r < 0) {
                Toast.makeText(getApplicationContext(), "Combinación usuario/clave incorrecta", Toast.LENGTH_LONG).show();
                txtEmail.setText("");
                txtPasword.setText("");
            } else {
                try {
                    List<UsuariosColegio> u = new Select().from(UsuariosColegio.class).where("idUsuario=?", r).execute();
                    //Si no existe, crearlo
                    if (u.size() <= 0) {
                        if (txtEmail.getText().toString().length() > 0 && txtPasword.getText().toString().length() > 0) {
                            UsuariosColegio usuariosColegio = new UsuariosColegio();
                            usuariosColegio.idColegio = idColegio;
                            usuariosColegio.usuario = txtEmail.getText().toString();
                            usuariosColegio.clave = txtPasword.getText().toString();
                            usuariosColegio.idUsuario = r;
                            usuariosColegio.save();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("strUsuario", strUsuario);
            request.addProperty("strClave", strClave);
            int retVal = 0;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(_URL, 10000);
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
                int idUsuario = 0;
                try {
                    idUsuario = Integer.parseInt(String.valueOf(result));
                } catch (Exception ex) {
                    idUsuario = -1;
                    retVal = -1;
                }
                if(intResultado == 1)
                {
                    if (idUsuario > 0) {
                        retVal = idUsuario;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("idUsuario", idUsuario);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        Log.d(TAG, "" + idUsuario);


                        editor.commit();
                        //Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                        sharedPreferencesC = getSharedPreferences(getString(R.string.title_cookies), MODE_PRIVATE);
                        SharedPreferences.Editor ed = sharedPreferencesC.edit();
                        ed.putString("cookies","1");
                        ed.commit();

                        Intent intent = new Intent(LoginActivity.this, ListaPlacasActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        retVal = -1;
                    }
                }
                else
                {

                    retVal = 0;
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    editor.commit();
                    //Intent intent = new Intent(LoginActivity.this, recordar_clave.class);
                    Intent intent = new Intent(LoginActivity.this, recordar_clave.class);
                    startActivity(intent);
                    finish();

                }


            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                Toast.makeText(getApplicationContext(), "Error de servidor", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }

            return retVal;
        }
    }

    public void onStart(){
        super.onStart();
    }

    private void GPSActivo() {
        try {
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsSignal == 0) {
                showGPSAlert();
            }
        } catch (Settings.SettingNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void showGPSAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Señal GPS")
                .setMessage("No tiene activo el GPS")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public class ListarUrlsAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Urls>> {

        String _url;

        public ListarUrlsAsyncTask(String _url) {
            this._url = _url;
        }

        @Override
        protected void onPostExecute(ArrayList<Urls> u) {
            super.onPostExecute(u);
            ListaUrlsAdapter listaUrlsAdapter = new ListaUrlsAdapter(LoginActivity.this, u);
            sprColegios.setAdapter(listaUrlsAdapter);
            if (u.size() > 0)
                sprColegios.setSelection(1);
        }


        @Override
        protected ArrayList<Urls> doInBackground(Integer... integers) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_URLS);

            int retVal = 0;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(_url, 100000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION_URLS, envelope);
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

            SoapObject result;
            try {
                result = (SoapObject) envelope.bodyIn;
                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
                Log.i(TAG, DocumentElement.toString());

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);
                //Primer item
                items.add(new Urls(0, "Seleccion...", "","", false));

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    try {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        int id = Integer.parseInt(s.getProperty("Id").toString());
                        String nombre = s.getProperty("Nombre").toString();
                        String url = s.getProperty("Url").toString();
                        String RutaFuec = s.getProperty("RutaFuec").toString();
                        Boolean BlBluetooth = Boolean.getBoolean(s.getProperty("Bluetooth").toString());
                        Log.d(TAG, url);
                        items.add(new Urls(id, nombre, url,RutaFuec, BlBluetooth));
                    } catch (Exception ex) {
                    }
                }


            } catch (Exception ex) {
                Log.i(TAG, ex.getMessage());
            }
            return items;
        }
    }

    /**
     * Validar version app
     */
    private boolean VersionActualizada(String strID) {// ID 12
        try {
            // Consume el servicio que trae la version de BD
            String s = new ActivarSeccionDB.SeccionDBAsyncTask(strID, this, BASE_URL).execute().get();

            // Compara version instalada con la version publicada en BD
            if(!s.equalsIgnoreCase(getResources().getString(R.string.versionApp))){

                // Mensaje para bloquear acceso
                AlertDialog.Builder buldier = new AlertDialog.Builder(this);
                buldier.setTitle("Actualizacion")
                        .setMessage("Nuevas version disponible: " + s + ", Porfavor actualizar para continuar.")
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Redirecciona al link con la ultima APK disponible
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.instalacionApp)));
                                startActivity(intent);
                            }
                        })
                        // Bloquea la alerta en pantalla para no continuar en el APP
                        .setCancelable(false)
                        .create().show();
                return false;
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Timer para expirar la sesion del usuario
    private void startCountdownTimer() throws ExecutionException, InterruptedException {
        // Se consume el servicio que trae el tiempo de expirar la sesion
        String s = new ActivarSeccionDB.SeccionDBAsyncTask("13", this, BASE_URL).execute().get();

        // Estructura del contador
        new CountDownTimer(Long.parseLong(s), 1000) {

            // en cada disminucion del contador
            public void onTick(long millisUntilFinished) {

                // Avisos en pantalla que la sesion se cerrara.
                if(millisUntilFinished/1000 == 60){
                    Toast.makeText(getBaseContext(),"Su sesion se cerrara en 1 minuto.", Toast.LENGTH_LONG).show();
                }

                if(millisUntilFinished/1000 == 10){
                    Toast.makeText(getBaseContext(),"Su sesion se cerrara en 10 segundos.", Toast.LENGTH_LONG).show();
                }
            }

            // Al contador llegar a 0
            public void onFinish() {
                // Limpia variables para evitar accesos falsos
                SharedPreferences pref = getBaseContext().getSharedPreferences(getBaseContext().getString(R.string.SPREF), 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                // LLamada la pantalla
                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                // Parametro para msg en pantalla
                intent.putExtra("expiro",true);
                startActivity(intent);

            }
        }.start();
    }
    // fin timer sesion

    // Mensajes en pantalla genericos
    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("OK", null)
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }
    // fin mensaje generico

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
