package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import android.support.design.widget.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.adapter.AgendaAdapter;
import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Agenda;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListadoAgendaActivity extends AppCompatActivity {
    ListView mListView;
    static String TAG="ListadoAgendaActivity";
    Spinner sprFiltro;
    static String HOY="1";
    static String SEMANA="2";
    static String MES="3";
    String fini,ffin;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListadoAgenda";
    private static final String METHOD_NAME = "ListadoAgenda";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    int idUsuario=0;
    FloatingActionButton fabAgenda;
    ArrayList<Agenda> items = new ArrayList<>();
    ArrayList<Estado> estados = new ArrayList<>();
    ArrayList<String> strFechaInicio = new ArrayList<>();
    ArrayList<String> strFechaFin = new ArrayList<>();
    ArrayList<Boolean> blCancelacion = new ArrayList<>();
    ArrayList<Integer> Ids = new ArrayList<>();
    ArrayList<String> strObservacion = new ArrayList<>();
    // items.add(new Agenda(id,convertirFecha(fechaInicio),convertirFecha(fechaFin),observ,cancelacion));

    EstadosSpinnerAdapter filtroAdapter = null;
    AgendaAdapter agendaAdapter = null;
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
        setContentView(R.layout.activity_listado_agenda);
        mListView = (ListView) findViewById(R.id.lstAgenda);
        sprFiltro = findViewById(R.id.sprFiltro);
        fabAgenda = (FloatingActionButton)findViewById(R.id.fabAgenda);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListadoAgendaActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Mi Agenda");

        llenaSpinner();

        sprFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Estado filtro = (Estado)sprFiltro.getItemAtPosition(position);

                /*
                String f1 = "2019-05-15";
                String f2 = "2019-05-16";
                DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date1 = format1.parse(f1);
                    Date date2 = format1.parse(f2);
                    fini = format1.format(date1);
                    ffin = format2.format(date2);

                    new ListarAgendaAsyncTask(idUsuario, fini, ffin).execute();
                    Toast.makeText(getApplicationContext(),fini + " " + ffin,Toast.LENGTH_LONG).show();
                }catch (Exception ex){

                }
*/


                if(filtro.getIdEstado().equalsIgnoreCase(HOY)){
                    Calendar cManana = Calendar.getInstance();
                    cManana.add(Calendar.DATE,1);
                    Date d = Calendar.getInstance().getTime();
                    Date dManana = cManana.getTime();
                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    String hoy = f.format(d);
                    String manana = f.format(dManana);
                    try {
                        fini = hoy;
                        ffin = manana;

                        new ListarAgendaAsyncTask(idUsuario, fini, ffin).execute();
                        //Toast.makeText(getApplicationContext(),fini + " " + ffin,Toast.LENGTH_LONG).show();
                    }catch (Exception ex){

                    }
                }

                if(filtro.getIdEstado().equalsIgnoreCase(SEMANA)){

                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");

                    Calendar cLunes = Calendar.getInstance();
                    cLunes.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    String lunes = f.format(cLunes.getTime());

                    Calendar cDomingo = Calendar.getInstance();
                    cDomingo.add(Calendar.DATE,6);
                    cDomingo.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    String domingo = f.format(cDomingo.getTime());

                    fini = lunes;
                    ffin = domingo;
                    new ListarAgendaAsyncTask(idUsuario,fini,ffin).execute();
                }

                if(filtro.getIdEstado().equalsIgnoreCase(MES)){


                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String ultimo = f.format(c.getTime());

                    Calendar c1 = Calendar.getInstance();
                    c1.set(Calendar.DAY_OF_MONTH, c1.getActualMinimum(Calendar.DAY_OF_MONTH));
                    String inicio = f.format(c1.getTime());

                    fini = inicio;
                    ffin = ultimo;
                    new ListarAgendaAsyncTask(idUsuario,fini,ffin).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fabAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ListadoAgendaActivity.this, AgendarHorarioActivity.class);
                intent.putExtra("edit",0);
                startActivity(intent);
            }
        });



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Agenda agenda = (Agenda)mListView.getItemAtPosition(position);
                int idAgenda = agenda.getId();
                String fechaInicio = agenda.getFechaInicio();
                String fechaFin = agenda.getFechaFin();
                String observ = agenda.getObservacion();
                boolean cancelacion = agenda.isCancelacion();
                Intent intent = new Intent(ListadoAgendaActivity.this,AgendarHorarioActivity.class);
                intent.putExtra("edit",1);
                intent.putExtra("fechaInicio",fechaInicio);
                intent.putExtra("fechaFin",fechaFin);
                intent.putExtra("observ",observ);
                intent.putExtra("cancelacion",cancelacion);
                intent.putExtra("idAgenda",idAgenda);
                startActivity(intent);
                items.clear();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        items.clear();
    }

    @Override
    protected void onResume(){
        super.onResume();
        new ListarAgendaAsyncTask(idUsuario, fini, ffin).execute();
    }


    private void llenaSpinner(){
        estados.add(new Estado("1","Hoy"));
        estados.add(new Estado("2","Semana"));
        estados.add(new Estado("3","Mes"));
        filtroAdapter = new EstadosSpinnerAdapter(ListadoAgendaActivity.this,estados);
        sprFiltro.setAdapter(filtroAdapter);
    }


    public class ListarAgendaAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Agenda>> {

        int idUsuario=0;
        String dtFechaInicio;
        String dtFechaFinal;
        /*
        public void parseXml(String str){
            try {

                boolean cancelacion=false;
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                String text="";
                StringBuilder builder = new StringBuilder();
                xpp.setInput( new StringReader(str) ); // pass input whatever xml you have
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tagname = xpp.getName();
                    switch (eventType) {
                        case XmlPullParser.TEXT:
                            text = xpp.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (tagname.equalsIgnoreCase("fecha_Inicio")) {
                                builder.append("Fecha_Inicio = " + text);
                                //Log.d(TAG+" Fecha_Inicio = ",text);
                                strFechaInicio.add(text);
                            } else if (tagname.equalsIgnoreCase("Fecha_Fin")) {
                                builder.append("\nFecha_Fin = " + text);
                                //Log.d(TAG+" Fecha_Fin = ",text);

                                strFechaFin.add(text);
                            }else if (tagname.equalsIgnoreCase("Cancelacion")) {
                                builder.append("\nCancelacion = " + text);
                                cancelacion = Boolean.parseBoolean(text);
                                blCancelacion.add(cancelacion);
                            }else if (tagname.equalsIgnoreCase("Id")) {
                                builder.append("\nId = " + text);
                                int id = Integer.parseInt(text);
                                Ids.add(id);
                            }
                            else if (tagname.equalsIgnoreCase("Observaciones")) {
                                strObservacion.add(text);
                            }



                            break;

                        default:
                            break;
                    }

*/
                /*    eventType = xpp.next();
                }
                Log.d(TAG,"End document");


                for(int i=0; i<strFechaFin.size(); i++){
                    try {
                        items.add(new Agenda(Ids.get(i), convertirFecha(strFechaInicio.get(i)), convertirFecha(strFechaFin.get(i)), strObservacion.get(i), blCancelacion.get(i)));
                    }catch (ParseException p){
                        Log.e(TAG,p.getMessage());
                    }
                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        private String convertirFecha(String fecha) throws ParseException {
            String fConv;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
            Date nuevaFecha = formatter.parse(fecha);
            formatter =  new SimpleDateFormat("dd/MM/yyyy HH:mm");
            fConv = formatter.format(nuevaFecha);
            return fConv;
        }

        public ListarAgendaAsyncTask(int idUsuario, String dtFechaInicio, String dtFechaFinal) {
            this.idUsuario = idUsuario;
            this.dtFechaInicio = dtFechaInicio;
            this.dtFechaFinal = dtFechaFinal;
        }

        @Override
        protected void onPostExecute(ArrayList<Agenda> a) {
            super.onPostExecute(a);
            agendaAdapter = new AgendaAdapter(ListadoAgendaActivity.this,a);
            agendaAdapter.notifyDataSetChanged();
            mListView.setAdapter(agendaAdapter);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            items.clear();
        }

        @Override
        protected ArrayList<Agenda> doInBackground(Integer... integers) {


            items.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("IdUsuario", String.valueOf(idUsuario));
            request.addProperty("dtFechaInicio", dtFechaInicio);
            request.addProperty("dtFechaFinal", dtFechaFinal);
            Log.d(TAG, dtFechaInicio + " " + dtFechaFinal);
            int retVal=0;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,100000);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            SoapObject result;
            try {
                result = (SoapObject) envelope.bodyIn;
                Log.i(TAG, result.toString().replace("ListadoAgendaResponse{ListadoAgendaResult=<NewDataSet>","").replace("</NewDataSet>; }",""));
                //parseXml(result.toString().replace("ListadoAgendaResponse{ListadoAgendaResult=<NewDataSet>","").replace("</NewDataSet>; }",""));

                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    try {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        int id = Integer.parseInt(s.getProperty("Id").toString());
                        String fechaInicio="01/01/0001";
                        try{
                            fechaInicio = s.getProperty("fecha_Inicio").toString();
                        }catch(Exception ex){

                        }
                        String fechaFin="01/01/0001";
                        try{
                            fechaFin = s.getProperty("Fecha_Fin").toString();
                        }catch (Exception ex){

                        }
                        String observ = "";
                        try{
                            observ = s.getProperty("Observaciones").toString();
                        }catch (Exception ex){

                        }
                        Boolean cancelacion =  false;
                        try{
                            cancelacion = Boolean.parseBoolean(s.getProperty("Cancelacion").toString());
                        }catch (Exception ex){

                        }
                        items.add(new Agenda(id,convertirFecha(fechaInicio),convertirFecha(fechaFin),observ,cancelacion));
                    }catch (Exception ex){
                    }
                }


            }catch (Exception ex){
                Log.e(TAG, ex.getMessage());
                //SoapFault error = (SoapFault)envelope.bodyIn;
                //Log.i(TAG, error.toString());
            }


            return items;
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
