package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaPreguntasAdapter;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.modelos.Pregunta;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;


//import android.database.sqlite.SQLiteDatabase;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListadoPreguntas extends AppCompatActivity {

    ListView mListView;
    ListaPreguntasAdapter mAdapter;
    //Variables de la DB
    //Db usdbh;
    //SQLiteDatabase db;

    ArrayList<Pregunta> listapregunta = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario, idPlaca;
    Button mButton;
    String res;
    static String TAG = "ListadoPreguntas";
    SharedPreferences sharedPreferences;

    private static final String ACTION_LISTADO_PREGUNTAS = "ListadoPreguntas";
    private static final String ACTION_ADICIONAR_CHECK_LIST = "AdicionarCheckList";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String ACTION_TEMPERATURA = "TemperaturaConductor";
    String BASE_URL, PREFS_NAME;
    EditText edTemp;

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
        setContentView(R.layout.activity_lista_preguntas);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");
        //usdbh = new Db(ListadoPreguntas.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //db = usdbh.getWritableDatabase();

        idUsuario = sharedPreferences.getInt("idUsuario", 0);
        idPlaca = sharedPreferences.getInt("idPlaca", 0);
        String strPlacaTitle = sharedPreferences.getString("strPlacaTitle", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(this, String.valueOf(idPlaca), Toast.LENGTH_SHORT).show();
        mButton = findViewById(R.id.btnEnviarRespuestas);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialofTemp().show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListadoPreguntas.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);

        lblTextoToolbar.setText("Bioseguridad - " + strPlacaTitle + " " +BuildConfig.VERSION_NAME);
        mListView = (ListView) findViewById(R.id.itemsPreguntas);

        if(hasConnection(ListadoPreguntas.this))
        {
            new ListaPruntasAsyncTask(idUsuario, idPlaca).execute();
        }
        else {
            get_bioseguridad_offline();
            mAdapter = new ListaPreguntasAdapter(ListadoPreguntas.this, listapregunta);
            mListView.setAdapter(mAdapter);
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


    public AlertDialog DialofTemp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListadoPreguntas.this);
        builder.setTitle("Información");
        builder.setMessage("Cual es tu temperatura corporal?");
        edTemp = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5,0,5,0);
        edTemp.setLeft(5);
        edTemp.setRight(5);
        edTemp.setInputType(InputType.TYPE_CLASS_NUMBER);
        edTemp.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(2)
        });
        edTemp.setLayoutParams(lp);
        builder.setView(edTemp);
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(edTemp.getText().toString() != "0")
                {
                    if(hasConnection(ListadoPreguntas.this)) {
                        new SendTemperaturaAsyncTask(edTemp.getText().toString(), idUsuario, idPlaca).execute();
                    }
                    else{

                        /*
                        try{
                            db = usdbh.getWritableDatabase();
                            try{
                                System.out.println("paso po aca:" + listapregunta.size());
                                for(int i =0 ; i < listapregunta.size(); i ++)
                                {

                                    boolean BlRespuesta = Boolean.parseBoolean(listapregunta.get(i).getRespuesta());
                                    String strRespuesta = String.valueOf(BlRespuesta);
                                    int idRespuesta = listapregunta.get(i).getId();
                                    System.out.println("Nro: LuisG"+ idRespuesta+", resp:" + BlRespuesta + ", placa:" + idPlaca);
                                    try {
                                        System.out.println("Db1 pregunta: "+ i);
                                        db.execSQL("update bioseguridad set strRespuesta = '" + strRespuesta + "' ," +
                                                " strTemp = '" + edTemp.getText().toString() + "' ,"+
                                                " idUsuario ="+idUsuario+", "+
                                                " idPlaca = "+idPlaca+","+
                                                " pendiente = 1 where intId = '" + listapregunta.get(i).getId() + "'");
                                    }
                                    catch (SQLException db)
                                    {
                                        System.out.println("Error de db: "+db.getMessage());
                                    }
                                }
                                db.close();
                                //finish();

                                MenuPrincipal();

                            }
                            catch (SQLException e)
                            {
                                try{}
                                catch (SQLException sl){}
                            }
                        }
                        catch (Exception ex)
                        {}*/
                    }
                }
            }
        });

        return builder.create();
    }

    public void MenuPrincipal()
    {
        System.out.println("JA PASO POR ACA");
        Intent intent = new Intent(ListadoPreguntas.this, Menuotros.class);
        startActivity(intent);
    }
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

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Su proceso fue exitoso", Toast.LENGTH_LONG).show();

                UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();

                new GuardarListaPruntasAsyncTask(idUsuario, idPlaca).execute();

                Intent intent = new Intent(ListadoPreguntas.this, Menuotros.class);
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



    public class ListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Pregunta>> {
        int idUsuario, idplaca;

        public ListaPruntasAsyncTask(int idUsuario, int idplaca) {
            this.idUsuario = idUsuario;
            this.idplaca = idplaca;
        }

        @Override
        protected void onPostExecute(ArrayList<Pregunta> s) {
            super.onPostExecute(s);
            mAdapter = new ListaPreguntasAdapter(ListadoPreguntas.this, s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<Pregunta> doInBackground(Integer... integers) {

            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_PREGUNTAS);
            SoapObject result;
            request.addProperty("IdUsuario", idUsuario);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_PREGUNTAS, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            int idPregunta;
            String descripcion;
            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0)
            {
                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    idPregunta = Integer.parseInt(s.getProperty("ID").toString());
                    descripcion = s.getProperty("DESCRIPCION").toString();

                    listapregunta.add(new Pregunta(idPregunta, descripcion));
/*
                    try
                    {
                        db = usdbh.getWritableDatabase();
                        if(db != null)
                        {
                            try{
                               System.out.println("Nro Luis insert:" + String.valueOf(idPregunta));
                                String strRespuesta = "false";
                                db.execSQL("Insert into bioseguridad(intId, strDescripcion, strRespuesta, idUsuario, strTemp, pendiente)"+
                                        " values('"+idPregunta+"',"+
                                        "'"+descripcion+"', "+
                                        "'"+strRespuesta+"'"+
                                        "'"+idUsuario+"',"+
                                        "'"+idplaca+"'," +
                                        "'"+null+"',"+
                                        " 0)");
                            }
                            catch (SQLException e){
                                try{}
                                catch (SQLException sl){}
                            }
                        }

                    }
                    catch (SQLException e){}*/
                }
                //db.close();
            }
            else
            {
                idPregunta = 0;
                descripcion = "No hay preguntas";
                listapregunta.add(new Pregunta(idPregunta, descripcion));
            }
            return listapregunta;

        }

    }


    public class GuardarListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario, idPlaca;

        public GuardarListaPruntasAsyncTask(int idUsuario, int idPlaca) {
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            super.onPostExecute(results);
            finish();

            System.out.println("Luis Prueba:" + results);

        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();

            for (int i = 0; i < listapregunta.size(); i++) {

                SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST);
                SoapObject result;
                boolean resCumple = Boolean.parseBoolean(listapregunta.get(i).getRespuesta());
                int idPregunta = listapregunta.get(i).getId();

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

            }

            return results;
        }

    }

    public void get_bioseguridad_offline(){
        /*
        try{
            db = usdbh.getWritableDatabase();
            if(db != null)
            {
                Cursor c = db.rawQuery("select intId, strDescripcion from bioseguridad", null);

                if(c.getCount() > 0)
                {
                    System.out.println("si hay registros");
                    if(c.moveToFirst())
                    {
                        do{
                            listapregunta.add(new Pregunta(c.getInt(0), c.getString(1)));
                        }
                        while(c.moveToNext());
                    }
                }
                else
                {
                    int intId = 0;
                    String strDescripcion = "No hay prehuntas";
                    listapregunta.add(new Pregunta(intId, strDescripcion));
                }

                if(c != null)
                {
                    c.close();;
                    db.close();
                }
            }
        }
        catch (SQLException e)
        {}*/
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
