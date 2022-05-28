package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.adapter.AdpaterClientesR;
import com.appetesg.estusolucionTranscarga.modelos.ClientesR;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.github.clans.fab.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class listaClintesRemitente extends AppCompatActivity {

    EditText etClientesR;
    AdpaterClientesR mListaClientesAdap;
    ClientesR resultp;
    ArrayList<ClientesR> listaClietes = new ArrayList<>();
    ArrayList<ClientesR>  arrayaux = new ArrayList<>();
    ArrayList<ClientesR>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;
    FloatingActionButton btnNuevo;
    String strCodCiu;
    private static final String ACTION_LISTADO_CIUDADES = "ListaClientesParametros";
    private static final String ACTION_COUNT = "ContadorDestinatarios";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaClientesBusqueda";
    String BASE_URL, PREFS_NAME;
    boolean connected = false;

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
        setContentView(R.layout.activity_lista_clintes_remitente);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listaClintesRemitente.this, RegistroGuia.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Busqueda De Clientes");

        strCodCiu = sharedPreferences.getString("strCodCiuDest", "");

        mListView = (ListView) findViewById(R.id.lstClientesRemitemte);
        etClientesR = (EditText) findViewById(R.id.etRemitente);
        btnNuevo = (FloatingActionButton) findViewById(R.id.btnNuevoCli);

        //new internetAsyncTask().execute();

        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("intCodCliN", 0);
                editor.commit();
                Intent intent = new Intent(listaClintesRemitente.this, RegistroRemitente.class);
                startActivity(intent);
                finish();
            }
        });
        /*
    if(NetworkUtil.hayInternet(this.getActivity())) {
        new ListaClientesRAsyncTask(etClientesR.getText().toString(), strCodCiu).execute();
    }
    else
    {
        Toast.makeText(getApplicationContext(), "Sin conexion a internet.", Toast.LENGTH_SHORT).show();
    }*/

        etClientesR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etClientesR.getRight() - etClientesR.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        //arrayaux.clear();
                        if(!etClientesR.getText().toString().equals(""))
                        {
                            //new internetAsyncTask().execute();
                            if(NetworkUtil.hayInternet(listaClintesRemitente.this)) {
                                new ListaClientesRAsyncTask(etClientesR.getText().toString().trim()).execute();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Sin conexion a internet.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {

                            mListaClientesAdap = new AdpaterClientesR( listaClintesRemitente.this, listaClietes);
                            mListView.setAdapter(mListaClientesAdap);
                            mListaClientesAdap.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ClientesR objClientes = (ClientesR) mListView.getItemAtPosition(position);
                int intCodCli = objClientes.getIntCodCli();
                String codCiuElect = objClientes.getStrCodCiu();

                if(intCodCli > 0) {

                    new AlertDialog.Builder(listaClintesRemitente.this)
                            .setTitle("Informacion")
                            .setMessage("Antes de continuar con el proceso ten encuenta si los datos estan correctos o si debes realizar algun "+
                                "tipo de modificacion de lo contrario omita el mensaje y dele continuar.")
                            .setNegativeButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //new internetAsyncTask().execute();
                                    if(NetworkUtil.hayInternet(listaClintesRemitente.this)) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("intCodCliN", intCodCli);
                                        editor.putString("strCodCiuElect", codCiuElect);
                                        editor.commit();
                                        Intent intent = new Intent(listaClintesRemitente.this, RegistroRemitente.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Sin Conexion a Internet.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("strNombreC", objClientes.getStrNomCli());
                                    editor.putString("strApellidoC", objClientes.getStrApellido());
                                    editor.putString("strDireccionC", objClientes.getStrDireccion());
                                    editor.putString("strDocumentoC", objClientes.getStrCedula());
                                    editor.putString("strTelefonoC", objClientes.getStrCelCli());
                                    editor.putString("strCompaniaC", objClientes.getStrCompania());
                                    editor.putInt("intCodCliN", intCodCli);
                                    editor.commit();

                                    new ContadorAsyncTask(intCodCli).execute();
                                }
                            })
                            .show();

                }
                else
                {
                    new AlertDialog.Builder(listaClintesRemitente.this)
                            .setTitle("Informacion")
                            .setMessage("No puedes continuar con el proceso por que no existe un cliente creado para esta ciudad.")
                            .setNegativeButton("Aceptar",null)
                            .show();
                }
                //Toast.makeText(getApplicationContext(), String.valueOf(intCodCli), Toast.LENGTH_LONG).show();
            }
        });

    }

    public class ListaClientesRAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ClientesR>> {

        String strParametroCli;
        ProgressDialog progress;

        public ListaClientesRAsyncTask(String strParametroCli) {
            this.strParametroCli = strParametroCli;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(listaClintesRemitente.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<ClientesR> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaClientesAdap = new AdpaterClientesR(listaClintesRemitente.this, s);
            mListView.setAdapter(mListaClientesAdap);
        }

        @Override
        protected ArrayList<ClientesR> doInBackground(Integer... integers) {
            listaClietes.clear();
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_CIUDADES);
            request.addProperty("strParametroCli", strParametroCli);

            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_CIUDADES, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            int intCodCli;
            String strNomCli, strFecha, strCedula, strDireccion, strTelcli, strCelCli, strCodCiu,
            strNomCompania, strApeCli;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    intCodCli = Integer.parseInt(s.getProperty("CODCLI").toString());
                    strNomCompania =  NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIA").toString());
                    strApeCli =  NetworkUtil.validarAnytype(s.getProperty("APECLI").toString());
                    strNomCli =  NetworkUtil.validarAnytype(s.getProperty("NOMCLI").toString());
                    strFecha = s.getProperty("FECCRECLI").toString();
                    strCedula = s.getProperty("IDECLI").toString();
                    strDireccion = s.getProperty("DIRCLI").toString();
                    strTelcli =  NetworkUtil.validarAnytype(s.getProperty("TELCLI").toString());
                    strCelCli =  NetworkUtil.validarAnytype(s.getProperty("CELCLI").toString());
                    strCodCiu = s.getProperty("CODCIU").toString();
                    listaClietes.add(new ClientesR(strNomCli, intCodCli, strCedula, strFecha, strDireccion,
                            strTelcli, strCelCli, strCodCiu, 0, strApeCli, strNomCompania));
                }
            }
            else
            {
                intCodCli = -1;
                strNomCli = "No contiene ningun cliente con estos datos.";
                strFecha = "";
                strCedula ="";
                strDireccion ="";
                strTelcli = "";
                strCelCli = "";
                listaClietes.add(new ClientesR(strNomCli, intCodCli, strCedula, strFecha, strDireccion, strTelcli, strCelCli, 0));

            }
            return listaClietes;

        }

    }

    //Contador de destinatarios por cliente seleccionado
    public class ContadorAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        int intCodCliS;
        public ContadorAsyncTask(int intCodCliS) {

            this.intCodCliS = intCodCliS;
        }
        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            int count = Integer.parseInt(s);
            //new internetAsyncTask().execute();
            if(NetworkUtil.hayInternet(listaClintesRemitente.this)) {
                if (count > 0) {
                    Intent intent = new Intent(listaClintesRemitente.this, ListaDestinatarios.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(listaClintesRemitente.this, RegistroDestinatario.class);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sin conexion a internet", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_COUNT);

            request.addProperty("intCodcli", intCodCliS);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_COUNT, envelope);
            }


            catch (Exception ex)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,ex.getMessage());
                ex.printStackTrace();
            }
            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                Log.i(TAG,String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            return res;
        }

    }

    //Contador de destinatarios por cliente seleccionado
    public class internetAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        public internetAsyncTask() {

        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            connected = Boolean.parseBoolean(s);
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            boolean connected = false;

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();





            RunnableFuture<Boolean> futureRun = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if ((networkInfo.isAvailable()) && (networkInfo.isConnected())) {
                        try {
                            HttpURLConnection urlc = (HttpURLConnection) (new URL("https://clients3.google.com/generate_204").openConnection());
                            urlc.setRequestProperty("User-Agent", "Test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(1000);
                            urlc.setReadTimeout(1000);
                            urlc.setRequestMethod("HEAD");
                            //urlc.connect();

                            if (urlc.getResponseCode() == 204 &&
                                    urlc.getContentLength() == 0)
                                return true;
                            else
                                return  false;
                        } catch (IOException e) {
                            Log.e(TAG, "Error checking internet connection", e);
                        }
                    } else {
                        Log.d(TAG, "No network available!");
                    }
                    return false;
                }
            });

            new Thread(futureRun).start();

            try {
                return futureRun.get().toString();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return "false";
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
