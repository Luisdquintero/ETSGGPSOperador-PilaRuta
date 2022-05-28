package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaFiltroAdapter;
import com.appetesg.estusolucionTranscarga.modelos.ListaFiltro;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ListaFiltrosActivity extends AppCompatActivity {
    ListView mListView;
    ListaFiltroAdapter mAdapter;
    ArrayList<ListaFiltro> listaFiltro = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    String res;
    static String TAG="ListaFiltrosActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaNotasTransporte";
    private static final String METHOD_NAME = "ListaNotasTransporte";
    private static final String NAMESPACE = "http://tempuri.org/";
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
        setContentView(R.layout.activity_lista_filtro);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);

        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        String strPlacaTitle = sharedPreferences.getString("strPlacaTitle","");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaFiltrosActivity.this, Menuotros.class);
                startActivity(intent);
                finish();

        }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista Filtro - " + strPlacaTitle);

        mListView = (ListView) findViewById(R.id.lstFiltroNotas);


       new ListaFiltroAsyncTask().execute();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaFiltro objFiltro = (ListaFiltro)mListView.getItemAtPosition(position);
                int intIdFiltro = objFiltro.getIntIdNota();
                int intCodigoFiltro = objFiltro.getIntCodigo();
                if(intCodigoFiltro == 2 || intCodigoFiltro == 5)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("intCodigoFiltro",intCodigoFiltro);
                    editor.commit();

                    Intent intent = new Intent(ListaFiltrosActivity.this, ListaDocumentosVe.class);
                    startActivity(intent);
                    finish();
                }
                else if(intCodigoFiltro == 7)
                {
                    Intent intent = new Intent(ListaFiltrosActivity.this, KilometrajeConductor.class);
                    startActivity(intent);
                    finish();
                }
                else if(intCodigoFiltro == 8)
                {
                    Intent intent = new Intent(ListaFiltrosActivity.this, ListaFuecActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    new AlertDialog.Builder(ListaFiltrosActivity.this)
                    .setTitle("Alerta")
                    .setMessage("No cumple con los requisitos por favor comunicarse con el administrador.")
                    .setNegativeButton("Ok",null)
                    .show();
                }
            }
        });
    }

    public class ListaFiltroAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ListaFiltro>> {

        public ListaFiltroAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<ListaFiltro> s) {
            super.onPostExecute(s);
            mAdapter = new ListaFiltroAdapter(ListaFiltrosActivity.this, s);
            mListView.setAdapter(mAdapter);
            Toast.makeText(getApplicationContext(), NAMESPACE+METHOD_NAME, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList<ListaFiltro> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;

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

            int id, intCodigo;
            String strDescripcion;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    id = Integer.parseInt(s.getProperty("ID").toString());
                    strDescripcion = s.getProperty("DESCRIPCION").toString();
                    intCodigo = Integer.parseInt(s.getProperty("CODIGO").toString());

                    listaFiltro.add(new ListaFiltro(id,strDescripcion, intCodigo));
                }

            }
            else
            {
                id = 0;
                strDescripcion = "NA";
                intCodigo = 0;
                listaFiltro.add(new ListaFiltro(id,strDescripcion, intCodigo));

            }
            return listaFiltro;

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
