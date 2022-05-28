package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.appetesg.estusolucionTranscarga.adapter.DocumentosAdapter;
import com.appetesg.estusolucionTranscarga.adapter.ListaGuiasDes;
import com.appetesg.estusolucionTranscarga.db.Db;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.modelos.GuiasD;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ListaGuiasActivity extends AppCompatActivity {
    ListView mListView;
    ListaGuiasDes mAdapter;
    DocumentosAdapter mAdapter1;
    ArrayList<GuiasD> listaGuias = new ArrayList<>();
    Toolbar toolbar;
    int idUsuario;
    String res;
    Db usdbh;
    SQLiteDatabase db;
    ArrayList<Estado> estados = new ArrayList<>();
    private static final String METHOD_NAME_ESTADOS = "Estados";
    static String TAG="ListaGuiasLogistic";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/ListaGuiasLogistic";
    private static final String METHOD_NAME = "ListaGuiasLogistic";
    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;

    @Override
    public void onBackPressed()
    {
        // nada
    }

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
        setContentView(R.layout.activity_lista_guias);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);

        //iniciamos la bd
        usdbh = new Db(ListaGuiasActivity.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //habilitamos para escritur
        db = usdbh.getWritableDatabase();

        BASE_URL = sharedPreferences.getString("urlColegio","");
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaGuiasActivity.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("En Reparto - App SisColint "+ getResources().getString(R.string.versionApp));

        mListView = (ListView) findViewById(R.id.lstGuiasL);

        //evaluamos si hay internet
        if(hasConnection(ListaGuiasActivity.this))
        {
            new ListaGuiasAsyncTask(idUsuario).execute();
            new ListarEstadosAsyncTask().execute();
        }
        else
        {
            //get_guias_offline();
            //mAdapter = new ListaGuiasDes(ListaGuiasActivity.this, listaGuias);
            //mListView.setAdapter(mAdapter);
        }


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GuiasD objGuia = (GuiasD) mListView.getItemAtPosition(position);
                String strGuia = objGuia.getStrGuia();
                if(strGuia != "No hay guias")
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strGuia", strGuia);
                    editor.putInt("pantalla", 0);
                    editor.putString("PEDIDO1", objGuia.getpedido1());
                    editor.putString("DESCON", objGuia.getStrDescripcionP());
                    editor.putString("DESTINATARIO", objGuia.getStrDestinatario());
                    editor.putString("VALPAG", objGuia.getStrValor());
                    editor.putString("PESPAQ", objGuia.getStrPeso());
                    editor.putString("DIRDES", objGuia.getStrDireccionDe());
                    editor.putString("REMITENTE", objGuia.getremitente());
                    editor.putString("TELDES", objGuia.getteldes());
                    editor.putString("NOMPRD", objGuia.getnomprd());
                    editor.putString("DIRCLI", objGuia.getdircli());
                    editor.putString("NOMFORPAG", objGuia.getStrNomForPag());

                    editor.commit();

                    //Intent intent = new Intent(ListaPlacasActivity.this, InsertarImagenDocumentoActivity.class);
                    Intent intent = new Intent(ListaGuiasActivity.this, EstadoGuiaActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    new AlertDialog.Builder(ListaGuiasActivity.this)
                            .setTitle("Informacion")
                            .setMessage("No puedes continuar con el proceso ya que no cuenta con guias")
                            .setNegativeButton("Aceptar",null)
                            .show();
                }
            }
        });
    }

    public class ListaGuiasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<GuiasD>> {
        int idUsuario;

        public ListaGuiasAsyncTask(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        @Override
        protected void onPostExecute(ArrayList<GuiasD> s) {
            super.onPostExecute(s);
            mAdapter = new ListaGuiasDes(ListaGuiasActivity.this, s);
            mListView.setAdapter(mAdapter);;

            new ListarEstadosAsyncTask().execute();
        }

        @Override
        protected ArrayList<GuiasD> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapObject result;
            request.addProperty("idUsuario", idUsuario);

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

            String strGuia, strProducto, strDestinatario, strValor, strPeso, strDireccion;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    System.out.println("DATOS QUE TRAE "+s.toString());
                    strGuia = s.getProperty("GUIA").toString();
                    strDestinatario = s.getProperty("DESTINATARIO").toString();
                    strProducto = s.getProperty("DESCON").toString();
                    strValor = s.getProperty("VALPAG").toString();
                    strPeso = s.getProperty("PESPAQ").toString();
                    strDireccion = s.getProperty("DIRDES").toString();


                    listaGuias.add(new GuiasD(strGuia, strProducto,strDestinatario, strValor, strPeso, strDireccion,
                            s.getProperty("REMITENTE").toString(),s.getProperty("TELDES").toString(),s.getProperty("DIRCLI").toString(),
                            s.getProperty("NOMPRD").toString(),s.getProperty("GUIA").toString(), s.getProperty("NOMFORPAG").toString()));
                    /*
                    try
                    {
                        db = usdbh.getWritableDatabase();
                        if (db != null)
                        {
                            try {
                                //insertamos en la base de datos
                                db.execSQL("Insert into guias(strGuia,strProducto,strDestinatario,strValor,strPeso,strDireccion,remitente,teldes,dircli,nomprd,pedido1,pendiente) "+
                                        " values ('" + strGuia + "','"+strProducto+
                                        "','"+strDestinatario+"','" +
                                        strValor + "','" +
                                        strPeso + "'," +
                                        "'"+strDireccion+"',"+
                                        "'"+ s.getProperty("REMITENTE").toString()+"',"+
                                        "'"+ s.getProperty("TELDES").toString()+"',"+
                                        "'"+ s.getProperty("DIRCLI").toString()+"',"+
                                        "'"+ s.getProperty("NOMPRD").toString()+"',"+
                                        "'"+ s.getProperty("GUIA").toString()+"',"+
                                        "0)");
                            } catch (SQLException e) {

                                try
                                {


                                }catch (SQLException sl)
                                {

                                }

                            }

                        }
                    }catch (SQLException e)
                    {

                    }*/


                }
                //db.close();

            }
            else
            {

                strGuia = "No hay guias";
                strDestinatario = "";
                strProducto = "";
                strValor = "";
                strPeso = "";
                strDireccion = "";
                listaGuias.add(new GuiasD(strGuia, strProducto,strDestinatario, strValor, strPeso, strDireccion,"","",""));

            }
            return listaGuias;

        }

    }

    public void get_guias_offline()
    {
/*
        try
        {
            db = usdbh.getWritableDatabase();
            if (db != null) {

                Cursor c = db.rawQuery("select strGuia,strProducto,strDestinatario,strValor,strPeso,strDireccion,remitente,teldes,dircli,nomprd,pedido1 from guias where pendiente = 0 ", null);

                if (c.getCount() > 0) {

                    System.out.println("si hay registros");

                    if (c.moveToFirst()) {
                        do {


                            listaGuias.add(new GuiasD(c.getString(0), c.getString(1),c.getString(2),c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8)));
                        } while (c.moveToNext());

                    }
                } else {
                    String strGuia = "No hay guias";
                    String  strDestinatario = "";
                    String  strProducto = "";
                    String strValor = "";
                    String strPeso = "";
                    String strDireccion = "";
                    listaGuias.add(new GuiasD(strGuia, strProducto,strDestinatario, strValor, strPeso, strDireccion,"","",""));
                }

                if(c!=null){
                    c.close();
                    db.close();
                }
            }

        }catch (SQLException e)
        {

        }*/
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
    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            //mAdapterEstados = new EstadosSpinnerAdapter(EstadoGuiaActivity.this,s);
            //spEstadosGuia.setAdapter(mAdapterEstados);
        }

        @Override
        protected ArrayList<Estado> doInBackground(Integer... integers) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS);
            String res;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS, envelope);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //Log.d(TAG,e.getMessage());
                e.printStackTrace();

            }

            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                // see output in the console
                Log.i(TAG,String.valueOf(envelope.getResponse()));

                res = String.valueOf(result);

                XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
                JSONObject jsonObject = xmlToJson.toJson();
                JSONObject DataSet = null;
                try {
                    DataSet = jsonObject.getJSONObject("NewDataSet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String table = DataSet.getString("ENV_ESTADO");
                JSONArray jsonArray = new JSONArray(table);

                Log.d(TAG,String.valueOf(jsonArray.length()));

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                   /* String codigo = object.getString("CODEST");
                    String estado = object.getString("NOMEST");

                    estados.add(new Estado(codigo,estado));*/

                    //insertamos en la bd

                    try
                    {
                        db = usdbh.getWritableDatabase();
                        if (db != null)
                        {



                            try {
                                //insertamos en la base de datos
                                db.execSQL("Insert into estados "+
                                        " values ('" + object.getString("CODEST") + "','"+object.getString("NOMEST")+
                                        "')");

                            } catch (SQLException e) {

                                System.out.println("HAY UNA EXCEPTION "+e.getMessage());

                            }

                        }
                    }catch (SQLException e)
                    {

                    }
                }

                //db.close();

            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }
            catch (JSONException je){
                Log.e(TAG, je.getMessage());
            }
            return estados;
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
