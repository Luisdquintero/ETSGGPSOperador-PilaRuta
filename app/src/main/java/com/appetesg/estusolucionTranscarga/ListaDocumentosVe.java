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


import com.appetesg.estusolucionTranscarga.adapter.DocumentosAdapter;
import com.appetesg.estusolucionTranscarga.modelos.DocumentosVehiculo;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ListaDocumentosVe extends AppCompatActivity {
    ListView mListView;
    DocumentosAdapter mAdapter;

    ArrayList<DocumentosVehiculo> listaDocumentosVes = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario, intCodigoNota;
    String res;
    static String TAG="ListaDocumentos";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaDocumentos";
    private static final String METHOD_NAME = "ListaDocumentos";
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
        setContentView(R.layout.activity_lista_documentos_ve);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);

        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        intCodigoNota = sharedPreferences.getInt("intCodigoFiltro",0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaDocumentosVe.this, ListaFiltrosActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista de Documentos - App SisColint "+ getResources().getString(R.string.versionApp));
        mListView = (ListView) findViewById(R.id.lstDocumentosVe);


        new ListaPruntasAsyncTask(intCodigoNota).execute();

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentosVehiculo objDocumento = (DocumentosVehiculo)mListView.getItemAtPosition(position);
                int intDocumento = objDocumento.getId();

                if(intDocumento > 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("idDocumento", intDocumento);
                    editor.commit();
                    Intent intent = new Intent(ListaDocumentosVe.this, InsertarImagenDocumentoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    new AlertDialog.Builder( ListaDocumentosVe.this)
                    .setTitle("Inforacion")
                    .setMessage("No puedes continuar con el proceso, por favor comunicarse con administrador")
                    .setNegativeButton("OK",null)
                    .show();

                }
            }
        });
    }


    public class ListaPruntasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<DocumentosVehiculo>> {
        int intCodigoNota;

        public ListaPruntasAsyncTask(int intCodigoNota) {
            this.intCodigoNota = intCodigoNota;
        }

        @Override
        protected void onPostExecute(ArrayList<DocumentosVehiculo> s) {
            super.onPostExecute(s);
            mAdapter = new DocumentosAdapter(ListaDocumentosVe.this, s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<DocumentosVehiculo> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            //request.addProperty("IdUsuario", idUsuario);
            request.addProperty("intIdNotaTransporte", intCodigoNota);

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

                    listaDocumentosVes.add(new DocumentosVehiculo(id, descripcion));
                }
            }
            else
            {
                id = 0;
                descripcion = "Na";
                listaDocumentosVes.add(new DocumentosVehiculo(id, descripcion));
            }
            return listaDocumentosVes;

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
