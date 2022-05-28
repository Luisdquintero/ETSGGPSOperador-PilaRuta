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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaPreguntasPesvAdapter;
import com.appetesg.estusolucionTranscarga.modelos.PreguntaPesv;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

//import android.database.sqlite.SQLiteDatabase;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListadoPreguntasPesv extends AppCompatActivity {

    ListView mListView;
    ListaPreguntasPesvAdapter mAdapter;

    //Db usdbh;
    //SQLiteDatabase db;

    ArrayList<PreguntaPesv> listapregunta = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario, idPlaca;
    Button mButton;
    String res;
    static String TAG = "ListadoPreguntasPesv";
    SharedPreferences sharedPreferences;

    private static final String ACTION_LISTADO_PREGUNTAS = "ListadoPreguntasPESV";
    private static final String ACTION_ADICIONAR_CHECK_LIST = "AdicionarCheckListPESV";
    private static final String NAMESPACE = "http://tempuri.org/";
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
        setContentView(R.layout.activity_lista_preguntas_pesv);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        idUsuario = sharedPreferences.getInt("idUsuario", 0);
        idPlaca = sharedPreferences.getInt("idPlaca", 0);
        String strPlacaTitle = sharedPreferences.getString("strPlacaTitle", "");

        //usdbh = new Db(ListadoPreguntasPesv.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //db = usdbh.getWritableDatabase();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton = findViewById(R.id.btnEnviarRespuestasPesv);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Enviando Preguntas", Toast.LENGTH_SHORT).show();

                if(idPlaca > 0) {
                    if (NetworkUtil.hayInternet(ListadoPreguntasPesv.this)) {
                        if (listapregunta.size() > 0) {
                            new GuardarListaPruntasAsyncTask(idUsuario, idPlaca).execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "No existen pregunats para almacena, por favor comunicarse con el administrador.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        /*
                        try {
                            db = usdbh.getWritableDatabase();
                            try {
                                System.out.println("paso po aca:" + listapregunta.size());
                                for (int i = 0; i < listapregunta.size(); i++) {

                                    boolean BlRespuesta = Boolean.parseBoolean(listapregunta.get(i).getRespuesta());
                                    String strRespuesta = String.valueOf(BlRespuesta);
                                    int idRespuesta = listapregunta.get(i).getId();
                                    System.out.println("Nro: LuisG" + idRespuesta + ", resp:" + BlRespuesta);
                                    try {
                                        System.out.println("Db1 pregunta: " + i);
                                        db.execSQL("update pesv set strRespuesta = '" + strRespuesta + "' ," +
                                                " idUsuario =" + idUsuario + ", " +
                                                " idPlaca = " + idPlaca + ", " +
                                                " pendiente = 1 where intId = '" + listapregunta.get(i).getId() + "'");
                                    } catch (SQLException db) {
                                        System.out.println("Error de db: " + db.getMessage());
                                    }
                                }
                                db.close();
                                //finish();

                                MenuPrincipal();

                            } catch (SQLException e) {
                                try {
                                } catch (SQLException sl) {
                                }
                            }
                        } catch (Exception ex) {
                        }*/
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(ListadoPreguntasPesv.this);
                    builder.setTitle("Informacion");
                    builder.setMessage("Por favor verificar tu conexion a internet e intentalo nuevamente.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", null);
                    AlertDialog dialog = builder.show();
                    TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);*/
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListadoPreguntasPesv.this);
                    builder.setTitle("Informacion");
                    builder.setMessage("No puedes realizar la lista preoperacional sin antes de haber seleccionado una placa.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ListadoPreguntasPesv.this, ListaPlacasActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.show();
                    TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);

                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListadoPreguntasPesv.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);

        lblTextoToolbar.setText("Preoperacional - " + strPlacaTitle + " " + BuildConfig.VERSION_NAME);
        mListView = (ListView) findViewById(R.id.itemsPreguntasPesv);

        if(NetworkUtil.hayInternet(ListadoPreguntasPesv.this)) {
            new ListaPruntasAsyncTask(idUsuario, idPlaca).execute();
            Toast.makeText(ListadoPreguntasPesv.this,"Cargando preguntas de base de datos ONLINE",Toast.LENGTH_LONG).show();
        }

        else {
            Toast.makeText(ListadoPreguntasPesv.this,"Cargando preguntas de base de datos offline",Toast.LENGTH_LONG).show();
            get_pesv_offline();
            mAdapter = new ListaPreguntasPesvAdapter(ListadoPreguntasPesv.this, listapregunta);
            mListView.setAdapter(mAdapter);
        }
    }

    public void MenuPrincipal()
    {
        Intent intent = new Intent(ListadoPreguntasPesv.this, Menuotros.class);
        startActivity(intent);
        finish();
    }

    public class ListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<PreguntaPesv>> {
        int idUsuario, idPlaca;

        public ListaPruntasAsyncTask(int idUsuario, int idPlaca) {
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
        }

        @Override
        protected void onPostExecute(ArrayList<PreguntaPesv> s) {
            super.onPostExecute(s);
            mAdapter = new ListaPreguntasPesvAdapter(ListadoPreguntasPesv.this, s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<PreguntaPesv> doInBackground(Integer... integers) {

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

            int id;
            String descripcion;
            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
            if(DocumentElement.getPropertyCount() > 0) {
                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    id = Integer.parseInt(s.getProperty("ID").toString());
                    descripcion = s.getProperty("DESCRIPCION").toString();

                    listapregunta.add(new PreguntaPesv(id, descripcion));
/*
                    try
                    {
                        db = usdbh.getWritableDatabase();
                        if(db != null)
                        {
                            try{
                                System.out.println("Nro Luis insert:" + String.valueOf(id));
                                String strRespuesta = "false";
                                db.execSQL("Insert into pesv(intId, strDescripcion, strRespuesta, idUsuario, pendiente)"+
                                        " values('"+id+"',"+
                                        "'"+descripcion+"', "+
                                        "'"+strRespuesta+"'"+
                                        "'"+idUsuario+"',"+
                                        "'"+idPlaca+"',"+
                                        " 0)");
                            }
                            catch (SQLException e){
                                try{}
                                catch (SQLException sl){}
                            }
                        }

                    }
                    catch (SQLException e){}
*/
                }
                //db.close();
            }
            else
            {
                id=0;
                descripcion = "na";
                listapregunta.add(new PreguntaPesv(id, descripcion));
            }
            return listapregunta;

        }

    }


    public class GuardarListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario;
        int idPlaca;
        public GuardarListaPruntasAsyncTask(int idUsuario, int idPlaca) {
            this.idUsuario = idUsuario;
            this.idPlaca = idPlaca;
        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            super.onPostExecute(results);
            Toast.makeText(getApplicationContext(), "Preguntas Enviadas", Toast.LENGTH_SHORT).show();
            MenuPrincipal();
            //finish();
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

    public void get_pesv_offline(){
        /*
        try{
            db = usdbh.getWritableDatabase();
            if(db != null)
            {
                Cursor c = db.rawQuery("select intId, strDescripcion from pesv", null);

                if(c.getCount() > 0)
                {
                    System.out.println("si hay registros");
                    if(c.moveToFirst())
                    {
                        do{
                            listapregunta.add(new PreguntaPesv(c.getInt(0), c.getString(1)));
                        }
                        while(c.moveToNext());
                    }
                }
                else
                {
                    int intId = 0;
                    String strDescripcion = "No hay preguntas";
                    listapregunta.add(new PreguntaPesv(intId, strDescripcion));
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
