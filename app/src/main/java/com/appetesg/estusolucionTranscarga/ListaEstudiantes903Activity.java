package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.adapter.ListaEstudiantesAdapter;
import com.appetesg.estusolucionTranscarga.modelos.ListaEstudiantes;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ListaEstudiantes903Activity extends AppCompatActivity {
ArrayList<ListaEstudiantes> listaEstudiantes = new ArrayList<>();
ListView mListView;
TextView lblTotalRegistros;
    static String TAG="ListaEstudiantesActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaEstudiantes";
    private static final String METHOD_NAME = "ListaEstudiantes";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    int idUsuario,idRuta;
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
        setContentView(R.layout.activity_lista_estudiantes);
        mListView = (ListView) findViewById(R.id.lstEstudiantes);
        lblTotalRegistros=(TextView)findViewById(R.id.lblTotalRegistros);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        idUsuario = sharedPreferences.getInt("idUsuario",0);
        idRuta = sharedPreferences.getInt("idRuta",0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaEstudiantes903Activity.this, MenuRutaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Dejados (tarde) - App SisColint "+ getResources().getString(R.string.versionApp));


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaEstudiantes listaEstudiantes =
                        (ListaEstudiantes)mListView.getItemAtPosition(position);

                int idEstudiante = listaEstudiantes.getId();
                int codEst = listaEstudiantes.getCodEst();

                Log.d("ESTUDIANTES","idEst: "+idEstudiante+", estado: "+codEst);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idEstCambio",idEstudiante);
                editor.putString("nomEst",listaEstudiantes.getNombreEstudiante());
                editor.commit();


                Intent intent = new Intent(ListaEstudiantes903Activity.this,EstadosDialogoActivity.class);
                intent.putExtra("idEstCambio",idEstudiante);
                intent.putExtra("flagEstado",903);
                intent.putExtra("nomEst",listaEstudiantes.getNombreEstudiante());
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        listaEstudiantes.clear();
        new ListarEstudiantesAsyncTask(idRuta,idUsuario).execute();
    }

    public class ListarEstudiantesAsyncTask extends AsyncTask<Integer,Integer,ArrayList<ListaEstudiantes>> {
        int idRuta;
        int intUsuario;

        public ListarEstudiantesAsyncTask(int idRuta, int intUsuario) {
            this.idRuta = idRuta;
            this.intUsuario = intUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaEstudiantes> s) {
            super.onPostExecute(s);
            ListaEstudiantesAdapter mAdapter = new ListaEstudiantesAdapter(ListaEstudiantes903Activity.this,s);
            mListView.setAdapter(mAdapter);
            mListView.invalidateViews();
            lblTotalRegistros.setText("Total de estudiantes: "+String.valueOf(s.size()));
        }

        @Override
        protected ArrayList<ListaEstudiantes> doInBackground(Integer... integers) {


            listaEstudiantes.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("intUsuario", intUsuario);
            request.addProperty("idRuta", idRuta);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }
            result = (SoapObject) envelope.bodyIn;
            //Log.d(TAG, result.toString());
            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
            SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);
            for (int i = 0; i < table1.getPropertyCount(); i++) {
                try{
                SoapObject s = (SoapObject) table1.getProperty(i);

                int id = Integer.parseInt(s.getProperty("ID").toString());
                String nombre_estudiante = s.getProperty("NOMBRE_ESTUDIANTE").toString();
                int codEst=0;
                try{
                    codEst  = Integer.parseInt(s.getProperty("CODEST").toString());
                    Log.d("CODEST",""+codEst);
                }catch (Exception ex){
                    Log.d("CODEST",nombre_estudiante+" "+ex.getMessage());
                    codEst = -1;
                }




                if(codEst==903) {
                    String estado="";
                    estado="Dejado en ubicación";
                    listaEstudiantes.add(new ListaEstudiantes(nombre_estudiante, id, codEst,estado));
                }
                }catch (Exception ex){}
            }

            return listaEstudiantes;
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
