package com.appetesg.estusolucionTranscarga;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appetesg.estusolucionTranscarga.modelos.MenuPrincipal;
import com.appetesg.estusolucionTranscarga.servicios.LocationService;
import com.appetesg.estusolucionTranscarga.servicios.MonitoreoService;
import com.appetesg.estusolucionTranscarga.utilidades.ActivarSeccionDB;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.Toolbar;
//import android.util.MonthDisplayHelper;

public class MenuActivity extends AppCompatActivity {
    GridView mGridView;
    ArrayList<MenuPrincipal> items = new ArrayList<>();
    Toolbar toolbar;
    static String TAG=MenuActivity.class.getName();
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesC;
    private static final String SOAP_ACTION = "http://tempuri.org/AdicionarToken";
    private static final String METHOD_NAME = "AdicionarToken";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME,NAME_URL;
    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;
    int idUsuario=0;
    int fbTokenRegistrado=0;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VersionActualizada("12");

        // Timer para expirar la sesion
        try {
            startCountdownTimer();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // controlar las excepciones cuando se cierra la app
        Thread.setDefaultUncaughtExceptionHandler(this::excepcionCapturada);
        setContentView(R.layout.activity_menu);
        toolbar = findViewById(R.id.toolbar);
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        NAME_URL = sharedPreferences.getString("nomColegio","");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ListaPlacasActivity.class);
            startActivity(intent);
            finish();
        });

        // Valida la version de la aplicacion instalada con la publicada en BD
        boolean versionApp = VersionActualizada("12");

        // Timer para expirar la sesion
        try {
            startCountdownTimer();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView lblTextoToolbar = toolbar.findViewById(R.id.
                lblTextoToolbar);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        lblTextoToolbar.setText("Menú Principal - App SisColint "+ getResources().getString(R.string.versionApp) + " - " + NAME_URL);
        mGridView = findViewById(R.id.gdvPrincipal);
        lblConnMenu = findViewById(R.id.lblConnMenu);
        rlCnn = findViewById(R.id.rlCnn);
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        sharedPreferencesC = getSharedPreferences(getString(R.string.title_cookies), MODE_PRIVATE);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MenuActivity.this, NotificacionService.class));
        } else {
            startService(new Intent(MenuActivity.this, NotificacionService.class));
        }
*/
        //Registrar el token, solo 1 vez
        fbTokenRegistrado = sharedPreferences.getInt("fbTokenRegistrado",0);
        if(fbTokenRegistrado==0)
            new ActualizarTokenAsyncTask(idUsuario,sharedPreferences.getString("firebaseToken","---")).execute();

        GPSActivo();
        items.add(new MenuPrincipal(1,R.drawable.maps,"Mapa"));
        items.add(new MenuPrincipal(2,R.drawable.satellite,"Transmitir"));
        items.add(new MenuPrincipal(12,R.drawable.otros, "Transporte"));
        items.add(new MenuPrincipal(8, R.drawable.carga,"Logistica"));
        //items.add(new MenuPrincipal(3,R.drawable.brujula,"Brújula"));
        items.add(new MenuPrincipal(4,R.drawable.user1,"Perfil e Información"));
        items.add(new MenuPrincipal(11,R.drawable.cerrar,"Cerrar Sesión"));
        /*items.add(new MenuPrincipal(5,R.drawable.icono_carro,"Servicios"));
        items.add(new MenuPrincipal(6,R.drawable.interview,"Lista de servicios"));
        items.add(new MenuPrincipal(7,R.drawable.maps,"Rutas"));
        //items.add(new MenuPrincipal(8,R.drawable.notebook,"Agenda"));
        items.add(new MenuPrincipal(9,R.drawable.conversation,"Chat"));
        items.add(new MenuPrincipal(10,R.drawable.qr_code,"Lector QR"));*/


        //startService(new Intent(this, MonitoreoService.class));
        MenuAdapter adapter = new MenuAdapter(this,items);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            if(versionApp){
            Object object = mGridView.getItemAtPosition(position);
            MenuPrincipal m = (MenuPrincipal)object;
            if (m.getId()==1){
                Intent intent = new Intent(MenuActivity.this,MainActivity.class);
                startActivity(intent);
            }
           /* if (m.getId()==3){
                Intent intent = new Intent(MenuActivity.this,BrujulaActivity.class);
                startActivity(intent);
            }*/
            if (m.getId()==4){
                if (NetworkUtil.hayInternet(MenuActivity.this)) {
                    Intent intent = new Intent(MenuActivity.this, PerfilActivity.class);
                    finish();
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Necesita una conexión a internet para utilizar esta opción",Toast.LENGTH_LONG).show();
                }
            }
            /*if (m.getId()==5){
                Intent intent = new Intent(MenuActivity.this,ServiciosActivity.class);
                startActivity(intent);
            }

            if (m.getId()==6){
                Intent intent = new Intent(MenuActivity.this,ListaServiciosActivity.class);
                startActivity(intent);
            }*/

            if(m.getId() == 12)
            {
                Intent intent = new Intent(MenuActivity.this,Menuotros.class);
                finish();
                startActivity(intent);
            }
            if(m.getId() == 8)
            {
                finish();
                Intent intent = new Intent(MenuActivity.this, MenuLogistica.class);
                startActivity(intent);
            }
           /*if (m.getId()==7){
                if(NetworkUtil.hayInternet(this.getActivity())){
             Intent intent = new Intent(MenuActivity.this,ListaRutaActivity.class);
             startActivity(intent);
             }else{
                  Toast.makeText(getApplicationContext(),"Necesita una conexión a internet para utilizar esta opción",Toast.LENGTH_LONG).show();
                }
            }*/

            //if (m.getId()==8){
               // Intent intent = new Intent(MenuActivity.this, ListadoAgendaActivity.class);
                //startActivity(intent);
            //}


            //if (m.getId()==9){
               /* Intent intent = new Intent(MenuActivity.this,ChatActivity.class);
                startActivity(intent);*/
          /*     Toast.makeText(getApplicationContext(),"En construcción",Toast.LENGTH_LONG).show();
            }
            if (m.getId()==10){
                if (NetworkUtil.hayInternet(this.getActivity())) {
                Intent intent = new Intent(MenuActivity.this,AdicionarQRActivity.class);
                startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Necesita una conexión a internet para utilizar esta opción",Toast.LENGTH_LONG).show();
                }
            }*/

            if (m.getId()==11){

                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Seguro que desea cerrar la sesión?")
                        .setPositiveButton("OK", (dialog, which) -> {

                            //Detener los servicios
                            try {
                                stopService(new Intent(MenuActivity.this, MonitoreoService.class));
                                stopService(new Intent(MenuActivity.this, LocationService.class));
                            }catch (Exception ex){

                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int recordar = sharedPreferences.getInt("recordar",0);
                            if (recordar==0) {
                                editor.putString("email", "");
                                editor.putString("password", "");
                                editor.putInt("idUsuario", 0);
                                editor.putInt("recordar", 0);
                                editor.putInt("fbTokenRegistrado",0);
                                editor.commit();
                            }else {
                                editor.putInt("idUsuario", 0);
                                editor.putInt("recordar", 1);
                                editor.putInt("fbTokenRegistrado",0);
                                editor.commit();
                            }

                            //sharedPreferencesC.getString("cookies", "");
                            SharedPreferences.Editor edit = sharedPreferencesC.edit();
                            edit.putString("cookies", "0");
                            edit.commit();
                            finish();
                            Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            startActivity(intent);
                            //clearCookies(MenuActivity.this);

                        })
                        .setNegativeButton("Cancelar",null)
                        .show();

            }

            if (m.getId()==2){

                boolean estado = svcEjecutando(MonitoreoService.class);
                //Se está ejecutando, preguntar si quiere apagarlo
                if (estado) {
                    new AlertDialog.Builder(MenuActivity.this)
                            .setTitle("Transmitir localización")
                            .setMessage("¿Desea detener la transmisión de su ubicación?")
                            .setPositiveButton("OK", (dialog, which) -> {
                                stopService(new Intent(MenuActivity.this, MonitoreoService.class));
                                Toast.makeText(getApplicationContext(), "El servicio de localización se ha desactivado", Toast.LENGTH_LONG).show();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
                else {
                    new AlertDialog.Builder(MenuActivity.this)
                            .setTitle("Transmitir localización")
                            .setMessage("¿Desea comenzar la transmisión de su ubicación?")
                            .setPositiveButton("OK", (dialog, which) -> {

                                //Para Android 8
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(new Intent(MenuActivity.this, MonitoreoService.class));
                                } else {
                                    startService(new Intent(MenuActivity.this, MonitoreoService.class));
                                }

                                Toast.makeText(getApplicationContext(), "El servicio de localización se ha activado", Toast.LENGTH_LONG).show();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
            }
        }});


    }

    @Override
    public void onBackPressed() {
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

    private void GPSActivo(){
        try{
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
            if(gpsSignal==0){
                showGPSAlert();
            }
        }catch (Settings.SettingNotFoundException ex){
            ex.printStackTrace();
        }
    }

    private void showGPSAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Señal GPS")
                .setMessage("No tiene activo el GPS")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }


    public class ActualizarTokenAsyncTask extends AsyncTask<Integer,Integer,String> {
        int idUsuario;
        String tk;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.length()>5){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("fbTokenRegistrado",1);
                editor.commit();
            }
        }

        public ActualizarTokenAsyncTask(int idUsuario, String tk) {
            this.idUsuario = idUsuario;
            this.tk = tk;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            request.addProperty("CodUsu", idUsuario);
            request.addProperty("strToken", tk);
            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }
            String r;
            try{
                result = (SoapObject) envelope.bodyIn;
                Log.d(TAG, result.toString());
                r=result.toString();
            }catch (Exception ex){
                r="";
            }

            return r;
        }
    }

    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            //Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
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
                        .setPositiveButton("Actualizar", (dialog, which) -> {
                            // Redirecciona al link con la ultima APK disponible
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.instalacionApp)));
                            startActivity(intent);
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

    private void startCountdownTimer() throws ExecutionException, InterruptedException {
        // Se consume el servicio que trae el tiempo de expirar la sesion
        String s = new ActivarSeccionDB.SeccionDBAsyncTask("13", this, BASE_URL).execute().get();
        new CountDownTimer(Long.parseLong(s), 1000) {

            public void onTick(long millisUntilFinished) {

                // Avisos en pantalla que la sesion se cerrara.
                if(millisUntilFinished/1000 == 60){
                    Toast.makeText(getBaseContext(),"Su sesion se cerrara en 1 minuto.", Toast.LENGTH_LONG).show();
                }

                if(millisUntilFinished/1000 == 2){
                    Toast.makeText(getBaseContext(),"Su sesion se cerrara en 10 segundos.", Toast.LENGTH_LONG).show();
                }

            }

            public void onFinish() {
                SharedPreferences pref = getBaseContext().getSharedPreferences(getBaseContext().getString(R.string.SPREF), 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                intent.putExtra("expiro",true);
                startActivity(intent);

            }
        }.start();
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

    }
}
