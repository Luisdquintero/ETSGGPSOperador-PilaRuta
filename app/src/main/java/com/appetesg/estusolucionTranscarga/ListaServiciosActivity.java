package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.FiltroSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaServicioAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Filtro;
import com.appetesg.estusolucionTranscarga.modelos.ListaServiciosDatos;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

//import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ListaServiciosActivity extends AppCompatActivity {

    ListView mListView;
    FloatingActionButton fabRuta;
    ListaServicioAdapter mAdapter;
    FiltroSpinnerAdapter mFiltroAdapter;
    ArrayList<ListaServiciosDatos> listaServicios = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaServicios";
    private static final String METHOD_NAME = "ListaServicios";
    private static final String NAMESPACE = "http://tempuri.org/";
    static int DIA=1;
    static int SEMANA=2;
    static int MES=3;
    String BASE_URL,PREFS_NAME;
    int filtroParam=0;
    static String TAG="ListaServiciosActivity";
    SpinnerAdapter spinnerAdapter;
    Spinner sprFiltro;

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
        setContentView(R.layout.activity_lista_servicios);
        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaServiciosActivity.this, Menuotros.class);
                startActivity(intent);
                finish();
            }
        });
        //518919
        //julio.castro2017@gmail.com
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista de Servicios - App SisColint "+ getResources().getString(R.string.versionApp));
        mListView = (ListView) findViewById(R.id.lstServicios);
        fabRuta = (FloatingActionButton)findViewById(R.id.fabRuta);
        sprFiltro = (Spinner)findViewById(R.id.sprFiltro);
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        if (NetworkUtil.hayInternet(this))
            llenaFiltro();
        else
            Toast.makeText(getApplicationContext(),"Necesita conexión a internet para esta funcionalidad",Toast.LENGTH_LONG).show();

        sprFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Object o = sprFiltro.getItemAtPosition(position);
                Filtro filtro = (Filtro)o;
                int idFiltro = filtro.getIdFiltro();
                filtroParam = filtro.getIdFiltro();
                listaServicios.clear();
                new ListarServiciosAsyncTask(idUsuario,idFiltro).execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fabRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaServiciosActivity.this,MapaServiciosActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("filtro",filtroParam);
                editor.commit();
                startActivity(intent);

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaServiciosDatos dtServicio = (ListaServiciosDatos)mListView.getItemAtPosition(position);
                String strCodigoR = dtServicio.getNumServi();
                String strTipo = dtServicio.getTipoServi();
                if(strCodigoR != "0")
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strCodigoR", strCodigoR);
                    editor.putString("strTipo", strTipo);
                    editor.commit();

                    Intent intent = new Intent(ListaServiciosActivity.this, DatosReservaActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    dialogMsg("No puedes continuar con el proceso, por favor comunicarse con administrador");
            }
        });

    }

    public void dialogMsg(String msg){
        new AlertDialog.Builder( ListaServiciosActivity.this)
                .setTitle("Informacion")
                .setMessage(msg)
                .setNegativeButton("OK",null)
                .show();
    }


    public void llenaFiltro(){
        ArrayList<Filtro> filtros = new ArrayList<>();
        //filtros.add(new Filtro(0,"Seleccione el filtro"));
        filtros.add(new Filtro(DIA,"Dia"));
        filtros.add(new Filtro(SEMANA,"Semana"));
        filtros.add(new Filtro(MES,"Mes"));
        mFiltroAdapter = new FiltroSpinnerAdapter(ListaServiciosActivity.this,filtros);
        sprFiltro.setAdapter(mFiltroAdapter);
    }

    public class ListarServiciosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<ListaServiciosDatos>> {
        int idUsuario;
        int  filtro;

        public ListarServiciosAsyncTask(int idUsuario, int filtro) {
            this.idUsuario = idUsuario;
            this.filtro = filtro;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaServiciosDatos> s) {
            super.onPostExecute(s);
            mAdapter = new ListaServicioAdapter(ListaServiciosActivity.this,s);
            mListView.setAdapter(mAdapter);
        }

        @Override
        protected ArrayList<ListaServiciosDatos> doInBackground(Integer... integers) {

            Calendar c1 = Calendar.getInstance();
            Date f = null;
            Date f2;
            String res = "";
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String hoy = "";
            String antes="";
            //Log.d(TAG,hoy+","+Latitud+","+Longitud+","+_velocidad);

            if (filtro==DIA){
                Calendar c = Calendar.getInstance();
                //c.set(Calendar.HOUR_OF_DAY,0);
                //c.set(Calendar.MINUTE,0);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);

            }

            if (filtro==SEMANA){
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-7);
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                f2 = c.getTime();
                hoy = formatter.format(f);
                antes = formatter.format(f2);
            }

            if (filtro==MES){
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
            request.addProperty("intUsuario", idUsuario);
            request.addProperty("DtFechaServer",antes);
            request.addProperty("DtFechaServerfin",hoy);


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


            String strServicio,strTipo, strFecha;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    strServicio = s.getProperty("CODIGO").toString();
                    strTipo = s.getProperty("ESTADO").toString();
                    strFecha = s.getProperty("FECHA").toString();

                    listaServicios.add(new ListaServiciosDatos(strServicio, strTipo, strFecha));

                    //listaFuec.add(new ListaFuec(idContrtato,idTerceroCliente,intNumeroContrato, intContratoMaster, strObjeto, strContratante, strItinerario));

                }

            }
            else
            {
                strServicio = "0";
                strTipo = "";
                strFecha = "";
                listaServicios.add(new ListaServiciosDatos(strServicio, strTipo, strFecha));
            }
            return listaServicios;

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
