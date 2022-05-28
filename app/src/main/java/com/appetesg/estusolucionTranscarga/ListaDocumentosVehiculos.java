package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaDocumentosVehiculoAdapter;
import com.appetesg.estusolucionTranscarga.modelos.DocumentosVehiculo;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListaDocumentosVehiculos extends AppCompatActivity {

    ListView mListView;
    ListaDocumentosVehiculoAdapter mAdapter;

    ArrayList<DocumentosVehiculo> DocumentosVehiculo = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    Button mButton;
    String res;
    static String TAG = "ListaDocumentos";
    SharedPreferences sharedPreferences;

    private static final String ACTION_LISTADO_PREGUNTAS = "ListaDocumentos";
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
        setContentView(R.layout.activity_lista_documentos_vencidos);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        idUsuario = sharedPreferences.getInt("idUsuario", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton = findViewById(R.id.btnEnviarRespuestasPesv);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Enviando Preguntas", Toast.LENGTH_SHORT).show();
                new GuardarListaPruntasAsyncTask(idUsuario).execute();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaDocumentosVehiculos.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.lblTextoToolbar);

        lblTextoToolbar.setText("Documentos Vehiculo");
        mListView = (ListView) findViewById(R.id.itemsPreguntasPesv);
        new ListaPruntasAsyncTask(idUsuario).execute();
    }

    public class ListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<DocumentosVehiculo>> {
        int idUsuario;

        public ListaPruntasAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<DocumentosVehiculo> s) {
            super.onPostExecute(s);
            mAdapter = new ListaDocumentosVehiculoAdapter(ListaDocumentosVehiculos.this, s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<DocumentosVehiculo> doInBackground(Integer... integers) {

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

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
            SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

            for (int i = 0; i < table1.getPropertyCount(); i++) {
                SoapObject s = (SoapObject) table1.getProperty(i);

                int id = Integer.parseInt(s.getProperty("ID").toString());
                String descripcion = s.getProperty("DESCRIPCION").toString();

                DocumentosVehiculo.add(new DocumentosVehiculo(id, descripcion));
            }
            return DocumentosVehiculo;

        }

    }


    public class GuardarListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Boolean>> {
        int idUsuario;

        public GuardarListaPruntasAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> results) {
            super.onPostExecute(results);
            finish();
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Integer... integers) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            ArrayList<Boolean> results = new ArrayList<>();

            for (int i = 0; i < DocumentosVehiculo.size(); i++) {

                SoapObject request = new SoapObject(NAMESPACE, ACTION_ADICIONAR_CHECK_LIST);
                SoapObject result;
                boolean resCumple = Boolean.parseBoolean(DocumentosVehiculo.get(i).getRespuesta());
                int idPregunta = DocumentosVehiculo.get(i).getId();

                Log.i(TAG, idPregunta + " : " + resCumple + " : " + currentDate);

                request.addProperty("intCodusu", idUsuario);
                request.addProperty("intIdpreguta", idPregunta);
                request.addProperty("blCumple", resCumple);
                request.addProperty("dtFechaReg", currentDate);

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
