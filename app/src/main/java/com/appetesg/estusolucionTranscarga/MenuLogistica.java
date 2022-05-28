package com.appetesg.estusolucionTranscarga;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.modelos.MenuPrincipal;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class MenuLogistica extends AppCompatActivity {
    GridView mGridView;
    ArrayList<MenuPrincipal> items = new ArrayList<>();
    Toolbar toolbar;
    static String TAG= Menuotros.class.getName();
    SharedPreferences sharedPreferences;
    private static boolean foreground = true; //valida si se permite mostrar el Dialog.
    //Db usdbh;
    String BASE_URL;
    ProgressDialog p;
    ProgressBar progress;
    //SQLiteDatabase db;
    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;
    private static final String METHOD_NAME_ESTADOS_GUIA = "ActualizarEstado";
    private static final String ACTION_ADICIONAR_CHECK_LIST = "AdicionarCheckList";
    private static final String ACTION_ADICIONAR_CHECK_LIST_PESV = "AdicionarCheckListPESV";
    private static final String ACTION_TEMPERATURA = "TemperaturaConductor";
    private static final String NAMESPACE = "http://tempuri.org/";

    @Override
    public void onDestroy() {
        super.onDestroy();

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
        setContentView(R.layout.activity_menuotros);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //iniciamos la bd
        //usdbh = new Db(MenuLogistica.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //habilitamos para escritur
        //db = usdbh.getWritableDatabase();

        //progress = (ProgressBar)  toolbar.findViewById(R.id.progress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuLogistica.this, MenuActivity.class);
                startActivity(intent);
                finish();

            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);



        lblTextoToolbar.setText("Otros / Logistica - App SisColint "+ getResources().getString(R.string.versionApp));
        mGridView = (GridView) findViewById(R.id.gdvPrincipal);
        lblConnMenu = (TextView) findViewById(R.id.lblConnMenu);
        rlCnn = (RelativeLayout) findViewById(R.id.rlCnn);
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        GPSActivo();



        //0
        BASE_URL = sharedPreferences.getString("urlColegio","");
        //items.add(new MenuPrincipal(1,R.drawable.brujula,"Brujula"));
        //10
        /*items.add(new MenuPrincipal(2,R.drawable.calendario,"Disponibilidad"));
        items.add(new MenuPrincipal(5,R.drawable.bioseguridad,"Bioseguridad"));
        items.add(new MenuPrincipal(6, R.drawable.pesv,"Preoperacional"));
        items.add(new MenuPrincipal(7, R.drawable.documentos,"Documentos"));*/
        items.add(new MenuPrincipal(8, R.drawable.listaenvios,"Guias"));
        items.add(new MenuPrincipal(9, R.drawable.registro,"Registro Guias"));
        items.add(new MenuPrincipal(10, R.drawable.trasmitir,"Transmitir"));
        items.add(new MenuPrincipal(11, R.drawable.carga,"Cotizacion"));
        items.add(new MenuPrincipal(12,R.drawable.qr_code,"Lector QR"));
        items.add(new MenuPrincipal(13,R.drawable.guias,"En Reparto"));
        items.add(new MenuPrincipal(14,R.drawable.guia,"Escaneo"));
        items.add(new MenuPrincipal(15,R.drawable.documentos,"Prueba de Entrega"));
        items.add(new MenuPrincipal(16, R.drawable.camionc,"Cargue"));
        MenuAdapter adapter = new MenuAdapter(this,items);
        mGridView.setAdapter(adapter);

        get_question_online();
        get_question_pesv_online();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = mGridView.getItemAtPosition(position);
                MenuPrincipal m = (MenuPrincipal) object;

                if (m.getId() == 10) //transmite las guias en estatus pendiente = 1 y las borramos de la bd
                {
                    if(hasConnection(MenuLogistica.this))
                    {
                        get_guias_offline();
                    }
                    else
                    {
                        Toast.makeText(MenuLogistica.this,"Conectese a internet para poder transmitir las guias completas",Toast.LENGTH_LONG).show();
                    }

                }
                if (m.getId() == 1) {
                    Intent intent = new Intent(MenuLogistica.this, BrujulaActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (m.getId() == 2) {
                    Intent intent = new Intent(MenuLogistica.this, ListadoAgendaActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (m.getId() == 5) {
                    //if(NetworkUtil.hayInternet(this.getActivity())) {
                    Intent intent = new Intent(MenuLogistica.this, ListadoPreguntas.class);
                    startActivity(intent);
                    finish();
                    /*}
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(Menuotros.this);
                        builder.setTitle("Informacion");
                        builder.setMessage("Por favor verificar tu conexion a internet e intentalo nuevamente.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", null);
                        AlertDialog dialog = builder.show();
                        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                        messageView.setGravity(Gravity.CENTER);
                    }*/
                }
                if (m.getId() == 6) {
                    //if(NetworkUtil.hayInternet(this.getActivity())) {
                    Intent intent = new Intent(MenuLogistica.this, ListadoPreguntasPesv.class);
                    startActivity(intent);
                    finish();
                   /* }
                    else
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Menuotros.this);
                        builder.setTitle("Informacion");
                        builder.setMessage("Por favor verificar tu conexion a internet e intentalo nuevamente.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", null);
                        AlertDialog dialog = builder.show();
                        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                        messageView.setGravity(Gravity.CENTER);

                        //Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT).show();
                    }*/
                }
                if(m.getId() == 7)  {
                    if(NetworkUtil.hayInternet(MenuLogistica.this)) {
                        Intent intent = new Intent(MenuLogistica.this, ListaFiltrosActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuLogistica.this);
                        builder.setTitle("Informacion");
                        builder.setMessage("Por favor verificar tu conexion a internet e intentalo nuevamente.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", null);
                        AlertDialog dialog = builder.show();
                        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                        messageView.setGravity(Gravity.CENTER);
                    }
                }
                if(m.getId() == 8)  {
                    Intent intent = new Intent(MenuLogistica.this, HistoricoGuia.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 9)  {
                    Intent intent = new Intent(MenuLogistica.this,ListaCiudadesD.class);
                    //Intent intent = new Intent(Menuotros.this,ListaEnviosProd.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 11) {
                    Intent intent = new Intent(MenuLogistica.this,ListaCiudadesCotizacion.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 13) {
                    Intent intent = new Intent(MenuLogistica.this,ListaGuiasActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 14) {
                    Intent intent = new Intent(MenuLogistica.this,EscaneoCodigoActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 15) {
                    Intent intent = new Intent(MenuLogistica.this,ListaCumplidosActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(m.getId() == 16) {
                    Intent intent = new Intent(MenuLogistica.this,CargueActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (m.getId()==12){
                    if (NetworkUtil.hayInternet(MenuLogistica.this)) {
                        Intent intent = new Intent(MenuLogistica.this,AdicionarQRActivity.class);
                        startActivity(intent);
                        finish();
                    }else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuLogistica.this);
                        builder.setTitle("Informacion");
                        builder.setMessage("Por favor verificar tu conexion a internet e intentalo nuevamente.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", null);
                        AlertDialog dialog = builder.show();
                        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                        messageView.setGravity(Gravity.CENTER);
                    }
                }


            }
        });


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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    public void get_question_online()
    {/*
        if(hasConnection(MenuLogistica.this)) {
            try {
                db = usdbh.getWritableDatabase();
                if (db != null) {
                    Cursor c = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca, strTemp from bioseguridad where pendiente = 1", null);
                    Cursor cTemp = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca, strTemp from bioseguridad where pendiente = 1", null);
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                new GuardarListaPruntasAsyncTask(c.getInt(3), c.getString(2), c.getInt(0), c.getInt(4)).execute();
                            } while (c.moveToNext());
                        }
                        if (cTemp.moveToFirst()) {
                            new SendTemperaturaAsyncTask(cTemp.getString(5), cTemp.getInt(3), cTemp.getInt(4)).execute();
                        }
                    }
                    if (c != null) {
                        c.close();
                        db.close();
                    }
                }
            } catch (SQLException ex) {
            }
        }*/
    }

    public void get_guias_offline()
    {
/*
        try
        {
            db = usdbh.getWritableDatabase();
            if (db != null) {

                Cursor c = db.rawQuery("select idUsuario,strGuia,Fecha,Estado,Latitud,Longitud,Imagen,srtRecibido from guias where pendiente = 1 ", null);

                if (c.getCount() > 0) {

                    System.out.println("si hay registros");

                    if (c.moveToFirst()) {
                        do {

                            new SendEstadosyncTask(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getString(4), c.getString(5), c.getString(6), c.getString(7) ).execute();

                        } while (c.moveToNext());



                    }
                }

                if(c!=null){
                    c.close();
                    db.close();
                }


            }

        }catch (SQLException e)
        {

        }*/
    }


    public void get_question_pesv_online()
    {/*
        if(hasConnection(MenuLogistica.this)) {

            try {
                db = usdbh.getWritableDatabase();
                if (db != null) {
                    System.out.println("Luis Guzman Paso Por aca:");
                    Cursor c = db.rawQuery("select intId, strDescripcion, strRespuesta, idUsuario, idPlaca from pesv where pendiente = 1", null);
                    // System.out.println("Luis Guzman total2:" + c.getCount());
                    if (c.getCount() > 0) {
                        //   System.out.println("Luis Guzman total:" + c.getCount());
                        if (c.moveToFirst()) {
                            Toast.makeText(MenuLogistica.this,"Espere un momento,la app esta enviando la información al servidor",Toast.LENGTH_LONG).show();
                            do {
                                System.out.println("contar");
                                //int idUsuario, int idPlaca, String strRespuesta, int idPregunta
                                new GuardarListaPruntasPesvAsyncTask(c.getInt(3), c.getInt(4), c.getString(2), c.getInt(0)).execute();
                            } while (c.moveToNext());
                            Toast.makeText(MenuLogistica.this,"Información enviada correctamente",Toast.LENGTH_LONG).show();
                        }
                    }
                    //es en esta

                    if (c != null) {
                        c.close();
                        db.close();
                    }
                }
            } catch (SQLException ex)
            {
                System.out.println("EXCEPTION GENERADA "+ex.getMessage());
            }

        }*/
    }


    //Metodo Pesv
    public class GuardarListaPruntasPesvAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario, idPregunta;
        int idPlaca;
        String strRespuesta;
        public GuardarListaPruntasPesvAsyncTask(int idUsuario, int idPlaca, String strRespuesta, int idPregunta) {
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
            this.strRespuesta = strRespuesta;
            this.idPregunta = idPregunta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data


        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            super.onPostExecute(results);



            System.out.println("Luis Guzman Proceso exitoso de la guardar preguntas");
/*
            try
            {
                db = usdbh.getWritableDatabase();
                if (db != null)
                {
                    try {
                        //insertamos en la base de datos
                        db.execSQL("update  pesv set strRespuesta = '',idUsuario=0,idPlaca=0,pendiente=0 where intId = "+idPregunta+"");

                    } catch (SQLException e) {

                    }
                }
            }catch (SQLException e)
            {
            }*/

            finish();
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();


            SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST_PESV);
            SoapObject result;
            boolean resCumple = Boolean.parseBoolean(strRespuesta);

            Log.i(TAG, idPregunta + " : " + resCumple + " : " + currentDate);

            request.addProperty("intCodusu", idUsuario);
            request.addProperty("intIdpreguta", idPregunta);
            request.addProperty("blCumple", resCumple);
            request.addProperty("dtFechaReg", currentDate);
            request.addProperty("idVehiculo", idPlaca);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_ADICIONAR_CHECK_LIST_PESV, envelope);

                result = (SoapObject) envelope.bodyIn;

                if (result.hasProperty("AdicionarCheckListResult")) {
                    results.add(Boolean.valueOf(result.getPropertyAsString("AdicionarCheckListResult")));
                }

                results.add(false);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

                results.add(false);
            }



            return results;
        }

    }

    //Metodo De Bioseguridad

    public class SendTemperaturaAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strTemp;
        int idUsuario;
        int idPlaca;;
        public SendTemperaturaAsyncTask(String strTemp, int idUsuario, int idPlaca)
        {
            this.strTemp = strTemp;
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(MenuLogistica.this);
            p.show(MenuLogistica.this, "Procesando...", "por esperar un momento.",false);
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            p.cancel();
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                System.out.println("Luis Guzman Proceso exitoso de la temperatura8");
                Toast.makeText(getApplicationContext(), "Se transmitieron correctamente la informacion.", Toast.LENGTH_LONG).show();

                /*UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();*/





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
            SoapObject request = new SoapObject(NAMESPACE, ACTION_TEMPERATURA);
            request.addProperty("steTemperatura", strTemp);
            request.addProperty("idCodusu",idUsuario);
            request.addProperty("idVehiculo",idPlaca);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_TEMPERATURA, envelope);
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

    public class GuardarListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario, idPregunta, idPlaca;
        String strRespuesta;



        public GuardarListaPruntasAsyncTask(int idUsuario, String strRespuesta, int idPregunta, int idPlaca) {
            this.idUsuario = idUsuario;
            this.strRespuesta =strRespuesta;
            this.idPregunta = idPregunta;
            this.idPlaca = idPlaca;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            p = new ProgressDialog(MenuLogistica.this);
            p.show(MenuLogistica.this, "Procesando...", "por esperar un momento.",false);

/*
            try
            {
                db = usdbh.getWritableDatabase();
                if (db != null)
                {
                    try {
                        //insertamos en la base de datos
                        db.execSQL("update bioseguridad set strRespuesta = '', idUsuario = 0, idPlaca = 0, strTemp = '', pendiente = 0 where intId = "+idPregunta+"");
                        //"update  pesv set strRespuesta = '',idUsuario=0,idPlaca=0,pendiente=0 where intId = "+idPregunta+""
                        //db.execSQL("delete from bioseguridad");

                    } catch (SQLException e) {}

                }
            }catch (SQLException e)
            {}*/

        }
        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            p.cancel();
            super.onPostExecute(results);
            System.out.println("Luis Guzman Proceso exitoso de la guardar preguntas");
            finish();
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();


            SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST);
            SoapObject result;
            boolean resCumple = Boolean.parseBoolean(strRespuesta);

            request.addProperty("intCodusu", idUsuario);
            request.addProperty("intIdpreguta", idPregunta);
            request.addProperty("blCumple", resCumple);
            request.addProperty("dtFechaReg", currentDate);
            request.addProperty("idVehiculo", idPlaca);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_ADICIONAR_CHECK_LIST, envelope);

                result = (SoapObject) envelope.bodyIn;

                if (result.hasProperty("AdicionarCheckListResult")) {
                    results.add(Boolean.valueOf(result.getPropertyAsString("AdicionarCheckListResult")));
                }

                results.add(false);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

                results.add(false);
            }



            return results;
        }

    }

    public class SendEstadosyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strPedido, strLatitud, strLongitud, strRecibido, strImagen, strFecha;
        int idUsuario, intEstado;

        public SendEstadosyncTask(int idUsuario,String strPedido, String strFecha, int intEstado, String strLatitud, String strLongitud, String strImagen, String strReibido)
        {
            this.idUsuario = idUsuario;
            this.strPedido = strPedido;
            this.strFecha = strFecha;
            this.intEstado = intEstado;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.strImagen = strImagen;
            this.strRecibido = strReibido;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MenuLogistica.this);
            p.show();
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {



            super.onPostExecute(s);
            p.cancel();
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Guia: " +strPedido+" enviada correctamente", Toast.LENGTH_LONG).show();

/*
                try
                {
                    db = usdbh.getWritableDatabase();
                    if (db != null)
                    {

                        try {
                            //insertamos en la base de datos
                            db.execSQL("delete from guias where strGuia = '"+strPedido+"'");
                        } catch (SQLException e) {
                        }

                    }
                }catch (SQLException e)
                {
                }*/
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
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS_GUIA);

            request.addProperty("IdUsuario", idUsuario);
            request.addProperty("DocumentoReferencia",strPedido);
            request.addProperty("Fecha", strFecha);
            request.addProperty("Estado", intEstado);
            request.addProperty("Latitud", strLatitud);
            request.addProperty("Longitud", strLongitud);
            request.addProperty("Imagen", strImagen);
            request.addProperty("srtRecibido", strRecibido);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS_GUIA, envelope);
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
    public  boolean hasConnection(Context c) {

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;

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
