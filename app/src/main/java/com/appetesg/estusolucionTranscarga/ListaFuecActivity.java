package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


import com.appetesg.estusolucionTranscarga.adapter.FiltroSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaFuecAdapter;
import com.appetesg.estusolucionTranscarga.modelos.ListaFuec;
import com.appetesg.estusolucionTranscarga.modelos.Filtro;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListaFuecActivity extends AppCompatActivity {
    ListView mListView;
    ListaFuecAdapter mAdapter;
    FiltroSpinnerAdapter mFiltroAdapter;
    ArrayList<ListaFuec> listaFuec = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario, idvehiculo;
    int intFiltroParam = 0;
    String res;
    static String TAG="ListaFuec";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaFuec";
    private static final String METHOD_NAME = "ListaFuec";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    private int DIA = 1;
    private int SEMANA = 2;
    private int MES = 3;
    SpinnerAdapter spinnerAdapter;
    Spinner spinnerFiltro;

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
        setContentView(R.layout.activity_lista_fuec);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);

        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        idvehiculo = sharedPreferences.getInt("idPlaca",0);
        mListView = (ListView)findViewById(R.id.lstFuec);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaFuecActivity.this, ListaFiltrosActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista de Fuec- App SisColint "+ getResources().getString(R.string.versionApp));

        spinnerFiltro = (Spinner)findViewById(R.id.sprFiltroDia);

        if(NetworkUtil.hayInternet(this))
            LlenarFiltro();
        else
            Toast.makeText(getApplicationContext(), "Necesita conexion a internet para esta funcionalidad.", Toast.LENGTH_SHORT).show();;


        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = spinnerFiltro.getItemAtPosition(position);
                Filtro filtro = (Filtro)o;
                int idFiltro = filtro.getIdFiltro();
                intFiltroParam = filtro.getIdFiltro();
                listaFuec.clear();
                new ListaFUECAsyncTask(idvehiculo, idFiltro).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaFuec objFuec = (ListaFuec)mListView.getItemAtPosition(position);
                int intFuec = objFuec.getIdContrtato();
                if(intFuec > 0)
                {
                    String urlRutaFuec = sharedPreferences.getString("RutaFuecUrl","");
                    String url = urlRutaFuec+String.valueOf(intFuec)+".pdf";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }

          }
        });
    }

    public void LlenarFiltro()
    {
        ArrayList<Filtro> filtros = new ArrayList<>();
        filtros.add(new Filtro(DIA, "Dia"));
        filtros.add(new Filtro(SEMANA, "Semana"));
        filtros.add(new Filtro(MES, "Mes"));
        mFiltroAdapter = new FiltroSpinnerAdapter(ListaFuecActivity.this,filtros);
        spinnerFiltro.setAdapter(mFiltroAdapter);
    }

    public class ListaFUECAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ListaFuec>> {
        int idplaca, idFiltro;


        public ListaFUECAsyncTask(int idPlaca, int idFiltro) {
            this.idplaca = idPlaca;
            this.idFiltro = idFiltro;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaFuec> s) {
            super.onPostExecute(s);
            mAdapter = new ListaFuecAdapter(ListaFuecActivity.this, s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<ListaFuec> doInBackground(Integer... integers) {

            Calendar c1 = Calendar.getInstance();
             Date f = null;
             Date f2;
             String res = "";
             DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String hoy = "";
            String antes="";
            //Log.d(TAG,hoy+","+Latitud+","+Longitud+","+_velocidad);

            if (idFiltro==DIA){
                Calendar c = Calendar.getInstance();
                //c.set(Calendar.HOUR_OF_DAY,0);
                //c.set(Calendar.MINUTE,0);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            if (idFiltro==SEMANA){
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-7);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            if (idFiltro==MES){
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-30);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("idVehiculo", idplaca);
            request.addProperty("dtFechaInicial",antes);
            request.addProperty("dtFechafin",hoy);


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


            //idContrtato, int idTerceroCliente, int intNumeroContrato, int intContratoMaster, String strObjeto, String strContratante, String strItinerario
            int idContrtato, idTerceroCliente, intNumeroContrato, intContratoMaster;
            String strObjeto,strContratante, strItinerario;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    idContrtato = Integer.parseInt(s.getProperty("ID").toString());
                    idTerceroCliente = Integer.parseInt(s.getProperty("ID_TERCEROCLIENTE").toString());
                    intNumeroContrato = Integer.parseInt(s.getProperty("NUMEROCONTRATO").toString());
                    intContratoMaster = Integer.parseInt(s.getProperty("CONTRATOMASTER").toString());
                    strObjeto = s.getProperty("OBJETOCONTRATO").toString();
                    strContratante = s.getProperty("CONTRATANTE").toString();
                    strItinerario = s.getProperty("ITINERARIO").toString();
                    listaFuec.add(new ListaFuec(idContrtato,idTerceroCliente,intNumeroContrato, intContratoMaster, strObjeto, strContratante, strItinerario));


                }

            }
            else
            {
                idContrtato = 0;
                idTerceroCliente = 0;
                intNumeroContrato = 0;
                intContratoMaster = 0;
                strObjeto = "";
                strContratante ="na";
                strItinerario = "";
                listaFuec.add(new ListaFuec(idContrtato,idTerceroCliente,intNumeroContrato, intContratoMaster, strObjeto, strContratante, strItinerario));
            }
            return listaFuec;

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
