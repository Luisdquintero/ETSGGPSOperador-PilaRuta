package com.appetesg.estusolucionTranscarga;

import android.content.DialogInterface;
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
import com.appetesg.estusolucionTranscarga.adapter.ListaPlacasAdapter;
import com.appetesg.estusolucionTranscarga.modelos.DocumentosVehiculo;
import com.appetesg.estusolucionTranscarga.modelos.ListaPlacas;
import com.appetesg.estusolucionTranscarga.servicios.LocationService;
import com.appetesg.estusolucionTranscarga.servicios.MonitoreoService;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ListaPlacasActivity extends AppCompatActivity {
    ListView mListView;
    ListaPlacasAdapter mAdapter;
    DocumentosAdapter mAdapter1;
    ArrayList<ListaPlacas> listaPlacas = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    String res;
    static String TAG="ListaPlacasConductor";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaPlacasConductor";
    private static final String METHOD_NAME = "ListaPlacasConductor";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    ArrayList<DocumentosVehiculo> listaDocumentosVes = new ArrayList<>();

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
        setContentView(R.layout.activity_lista_placas);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);

        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(ListaPlacasActivity.this, ListaDocumentosVe.class);
                startActivity(intent);
                finish();*/
                new AlertDialog.Builder(ListaPlacasActivity.this)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Seguro que desea cerrar la sesión?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Detener los servicios
                                try {

                                    stopService(new Intent(ListaPlacasActivity.this, MonitoreoService.class));
                                    stopService(new Intent(ListaPlacasActivity.this, LocationService.class));
                                }catch (Exception ex){

                                }
                                sharedPreferences = getSharedPreferences(getString(R.string.title_cookies), MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                int recordar = sharedPreferences.getInt("recordar",0);
                                if (recordar==0) {
                                    editor.putString("email", "");
                                    editor.putString("password", "");
                                    editor.putString("cookies", "");
                                    editor.putInt("idUsuario", 0);
                                    editor.putInt("recordar", 0);
                                    editor.putInt("fbTokenRegistrado",0);
                                    editor.commit();
                                }else {
                                    editor.putInt("idUsuario", 0);
                                    editor.putInt("recordar", 1);
                                    editor.putString("cookies", "");
                                    editor.putInt("fbTokenRegistrado",0);
                                    editor.commit();
                                }

                                sharedPreferences = getSharedPreferences(getString(R.string.SPREF), 0);
                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.putInt("idUsuario",0);
                                editor1.commit();

                                Intent intent = new Intent(ListaPlacasActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();


                            }
                        })
                        .setNegativeButton("Cancelar",null)
                        .show();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista de Vehiculos - App SisColint "+ getResources().getString(R.string.versionApp));

        mListView = (ListView) findViewById(R.id.lstPlacas);


       new ListaPlacasAsyncTask(idUsuario).execute();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaPlacas objPlaca = (ListaPlacas)mListView.getItemAtPosition(position);
                int intPlaca = objPlaca.getIdVehiculo();
                int intIdTercero = objPlaca.getIdTercero();
                String strPlacaTitle = objPlaca.getStrPlaca();
               if(intPlaca >0)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("idPlaca", intPlaca);
                    editor.putInt("idTercero", intIdTercero);
                    editor.putString("strPlacaTitle", strPlacaTitle);
                    editor.commit();

                    finish();
                    //Intent intent = new Intent(ListaPlacasActivity.this, InsertarImagenDocumentoActivity.class);
                    Intent intent = new Intent(ListaPlacasActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
                else {
                   new AlertDialog.Builder(ListaPlacasActivity.this)
                           .setTitle("Informacion")
                           .setMessage("No puedes continuar con el proceso por que no contines una placa asociada, por favor comunicarse con el administrador.")
                           .setNegativeButton("Aceptar",null)
                           .show();
                }
            }
        });
    }

    public class ListaPlacasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ListaPlacas>> {
        int idUsuario;

        public ListaPlacasAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaPlacas> s) {
            super.onPostExecute(s);
            mAdapter = new ListaPlacasAdapter(ListaPlacasActivity.this, s);
            mListView.setAdapter(mAdapter);
            ListaPlacas objPlaca = (ListaPlacas)mListView.getItemAtPosition(0);
            if(objPlaca.getIdVehiculo() == 0){
                new AlertDialog.Builder(ListaPlacasActivity.this)
                        .setTitle("Informacion")
                        .setMessage("Auxiliar detectado.")
                        .setCancelable(false)
                        .setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ListaPlacasActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            }
        }

        @Override
        protected ArrayList<ListaPlacas> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("intCodusu", idUsuario);

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

            int id, idTercero, idVehiculo;
            String placa;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    id = Integer.parseInt(s.getProperty("ID").toString());
                    idTercero = Integer.parseInt(s.getProperty("ID_TERCERO").toString());
                    idVehiculo = Integer.parseInt(s.getProperty("ID_VEHICULO").toString());
                    placa = s.getProperty("PLACA").toString();

                    listaPlacas.add(new ListaPlacas(id, idTercero, idVehiculo, placa));
                }

            }
            else
            {
                id = 0;
                idTercero = 0;
                idVehiculo = 0;
                placa = "Contacte Operacion Asocial Placa";
                listaPlacas.add(new ListaPlacas(id, idTercero, idVehiculo, placa));

            }
            return listaPlacas;

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
