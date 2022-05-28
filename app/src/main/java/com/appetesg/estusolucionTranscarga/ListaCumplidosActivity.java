package com.appetesg.estusolucionTranscarga;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AlertDialog;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ListaGuiasDes;
import com.appetesg.estusolucionTranscarga.db.Db;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.modelos.GuiasD;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ListaCumplidosActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    Db usdbh;
    SQLiteDatabase db;
    ArrayList<Estado> estados = new ArrayList<>();
    private static final String METHOD_NAME_ESTADOS = "Estados";

    IntentIntegrator qrScan;
    FloatingActionButton FabGuia;
    EditText etGuiaCo;
    ListaGuiasDes mListaHistoricoAdapter;
    GuiasD resultp;
    ArrayList<GuiasD> listaGuias = new ArrayList<>();
    ArrayList<GuiasD>  arrayaux = new ArrayList<>();
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;
    int idUsuario = 0;
    private static final String ACTION_DATOS_GUIA = "DatosGuia";
    private static final String ACTION_CONCEPTO = "ValorGuiaFiltro";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaCumplidosGuia";
    String BASE_URL, PREFS_NAME;
    //Db usdbh;
    //SQLiteDatabase db;

    @Override
    public void onBackPressed() {
        //NO HACE NADA AL OPRIMIR
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
        setContentView(R.layout.activity_cumplidos_guia);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        //iniciamos la bd
        usdbh = new Db(ListaCumplidosActivity.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        //habilitamos para escritur
        db = usdbh.getWritableDatabase();

        if(NetworkUtil.hayInternet(this))
            new ListarEstadosAsyncTask().execute();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Boton volver atras
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ListaCumplidosActivity.this, MenuLogistica.class);
            startActivity(intent);
            finish();
        });

        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Cumplidos");

        FabGuia = findViewById(R.id.ftbGuia);
        mListView = (ListView) findViewById(R.id.lstGuiasHistorico);
        etGuiaCo = (EditText) findViewById(R.id.edGuiaConsulta);
        idUsuario =  sharedPreferences.getInt("idUsuario",0);

        FabGuia.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                FabGuia.setVisibility(View.GONE);
                FabGuia.setEnabled(false);

                // LLAMAR AL SCAN QR
                qrScan.initiateScan();

                FabGuia.setEnabled(true);
                FabGuia.setVisibility(View.VISIBLE);
            }
        });

        etGuiaCo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etGuiaCo.getRight() - etGuiaCo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        listaGuias.clear();
                        if(!etGuiaCo.getText().toString().equals(""))
                        {
                            if(NetworkUtil.hayInternet(ListaCumplidosActivity.this))
                                new BusquedaGuiasAsyncTask(etGuiaCo.getText().toString().trim()).execute();
                            else
                                Toast.makeText(getBaseContext(),"No hay conexion, vuelva a intentar",Toast.LENGTH_LONG);
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GuiasD objGuia = (GuiasD) mListView.getItemAtPosition(position);
                String strGuia = objGuia.getStrGuia();
                if(strGuia != "No hay guias")
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strGuia", strGuia);
                    editor.putInt("pantalla", 1);
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
                    Intent intent = new Intent(ListaCumplidosActivity.this, EstadoGuiaActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    new AlertDialog.Builder(ListaCumplidosActivity.this)
                            .setTitle("Informacion")
                            .setMessage("No puedes continuar con el proceso ya que no cuenta con guias")
                            .setNegativeButton("Aceptar",null)
                            .show();
                }
            }
        });

    }

    public class BusquedaGuiasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<GuiasD>> {

        String strPedido1;
        ProgressDialog progress;

        public  BusquedaGuiasAsyncTask(String strPedido1) {
            this.strPedido1 = strPedido1;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(ListaCumplidosActivity.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<GuiasD> s)
        {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaHistoricoAdapter = new ListaGuiasDes(ListaCumplidosActivity.this, s);
            mListView.setAdapter(mListaHistoricoAdapter);;

            float intContadorValores = 0;

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<GuiasD> doInBackground(Integer... integers) {

            try{
                SoapObject request = new SoapObject(NAMESPACE, ACTION_DATOS_GUIA);
                request.addProperty("strPedido1", strPedido1);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
                httpTransport.debug = true;

                try {
                    httpTransport.call(NAMESPACE + ACTION_DATOS_GUIA, envelope);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                }

                result = (SoapObject) envelope.bodyIn;

                String strGuia, strProducto, strDestinatario, strValor, strPeso, strDireccion;

                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

                if (DocumentElement.getPropertyCount() > 0) {

                    SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                    for (int i = 0; i < table1.getPropertyCount(); i++) {
                        SoapObject s = (SoapObject) table1.getProperty(i);

                        System.out.println("DATOS QUE TRAE " + s.toString());
                        strGuia = s.getProperty("PEDIDO1").toString();
                        strDestinatario = s.getProperty("DESTINATARIO").toString();
                        strProducto = s.getProperty("DESCON").toString();
                        strValor = s.getProperty("VALPAG").toString();
                        strPeso = s.getProperty("PESPAQ").toString();
                        strDireccion = s.getProperty("DIRDES").toString();


                        listaGuias.add(new GuiasD(strGuia, strProducto, strDestinatario, strValor, strPeso, strDireccion,
                                s.getProperty("REMITENTE").toString(), s.getProperty("TELDES").toString(), s.getProperty("DIRCLI").toString(),
                                s.getProperty("NOMPRD").toString(), s.getProperty("PEDIDO1").toString(), s.getProperty("NOMFORPAG").toString()));

                    }
                } else {

                    strGuia = "No hay guias";
                    strDestinatario = "";
                    strProducto = "";
                    strValor = "";
                    strPeso = "";
                    strDireccion = "";
                    listaGuias.add(new GuiasD(strGuia, strProducto, strDestinatario, strValor, strPeso, strDireccion, "", "", ""));

                }
                return listaGuias;
            }catch (Exception e){
                Toast.makeText(getBaseContext(),"Busqueda fallida, volver a intentar",Toast.LENGTH_LONG);
            }
            return listaGuias;
        }
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No hay resultados", Toast.LENGTH_LONG).show();
            } else {
                String strGuia = result.getContents().toString();
                if(NetworkUtil.hayInternet(ListaCumplidosActivity.this)) {
                    // Invoca el servicio para verificar la guia
                    listaGuias.clear();
                    new BusquedaGuiasAsyncTask(strGuia).execute();
                }
                else
                {
                    FabGuia.setEnabled(true);
                    FabGuia.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Sin conexion a internet..", Toast.LENGTH_SHORT).show();
                }
            }
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
