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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.adapter.AdapterDestinatarios;
import com.appetesg.estusolucionTranscarga.modelos.ListaClientesDesti;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.github.clans.fab.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class ListaDestinatarios extends AppCompatActivity {
    int intCodCli;
    EditText etClientesD;
    AdapterDestinatarios mListaDestinatirosAdap;
    ListaClientesDesti resultp;
    ArrayList<ListaClientesDesti> listaDestinatarios = new ArrayList<>();
    ArrayList<ListaClientesDesti>  arrayaux = new ArrayList<>();
    ArrayList<ListaClientesDesti>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;
    FloatingActionButton btnNuevo;

    private static final String ACTION_LISTADO_DESTINATARIOS = "ListaClientesDestinatarioCorporativos";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaClientesDestinatarioCorporativos";
    String BASE_URL, PREFS_NAME, codCiuDes, strNomCiuDes;
    boolean blClienteCorp;

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
        setContentView(R.layout.activity_lista_destinatarios);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");
        codCiuDes = sharedPreferences.getString("strCodCiuDest", "");
        strNomCiuDes = sharedPreferences.getString("strNomciuDest", "");
        blClienteCorp = sharedPreferences.getBoolean( "blClienteCorp", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blClienteCorp){

                    finish();
                    Intent intent = new Intent(ListaDestinatarios.this, RegistroGuia.class);
                    startActivity(intent);
                }else {

                    finish();
                    Intent intent = new Intent(ListaDestinatarios.this, listaClintesRemitente.class);
                    startActivity(intent);
                }
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Busqueda De Destinatarios");

        mListView = (ListView) findViewById(R.id.lstDestinarios);
        etClientesD = (EditText) findViewById(R.id.etDestinario);
        btnNuevo = (FloatingActionButton) findViewById(R.id.btnNuevoDest);
        intCodCli = sharedPreferences.getInt("intCodCliN", 0);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("intCodDestinatario", 0);
                editor.commit();
                Intent intent = new Intent(ListaDestinatarios.this, RegistroDestinatario.class);
                startActivity(intent);
                finish();
            }
        });

        new ListaDestinatariosAsyncTask(intCodCli, codCiuDes).execute();
        //new ListaDestinatariosAsyncTask(intCodCli, etClientesD.getText().toString()).execute();


        etClientesD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etClientesD.getRight() - etClientesD.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etClientesD.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listaDestinatarios.size();i++)
                            {

                                resultp = listaDestinatarios.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();

                                System.out.println("nombre_cliente "+ resultp.getStrNombreDest());


                                if(resultp.getStrNombreDest().toUpperCase().contains(etClientesD.getText().toString().toUpperCase()))
                                {
                                    arrayaux.add(new ListaClientesDesti(resultp.getIntCodDest(), resultp.getStrNombreDest(), resultp.getStrCedulaDest(), resultp.getStrTelDest(), resultp.getStrDireDest(), resultp.getStrFechaDest()));

                                }
                                else if(resultp.getStrTelDest().contains(etClientesD.getText().toString()))
                                {
                                    arrayaux.add(new ListaClientesDesti(resultp.getIntCodDest(), resultp.getStrNombreDest(), resultp.getStrCedulaDest(), resultp.getStrTelDest(), resultp.getStrDireDest(), resultp.getStrFechaDest()));

                                }
                                else if(resultp.getStrCedulaDest().contains(etClientesD.getText().toString()))
                                {
                                    arrayaux.add(new ListaClientesDesti(resultp.getIntCodDest(), resultp.getStrNombreDest(), resultp.getStrCedulaDest(), resultp.getStrTelDest(), resultp.getStrDireDest(), resultp.getStrFechaDest()));
                                }

                            }

                            if(arrayaux != null)
                            {

                                mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, arrayaux);
                                mListView.setAdapter(mListaDestinatirosAdap);
                                mListaDestinatirosAdap.notifyDataSetChanged();

                            }
                            else
                            {

                                mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, arrayaux);
                                mListView.setAdapter(mListaDestinatirosAdap);
                                mListaDestinatirosAdap.notifyDataSetChanged();
                            }


                        }
                        else
                        {


                            mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, listaDestinatarios);
                            mListView.setAdapter(mListaDestinatirosAdap);
                            mListaDestinatirosAdap.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etClientesD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null)
                    return;
                String str = s.toString().substring(start, start + count);
                if (str.equals("\n"))
                {
                    etClientesD.setText(s.toString().replaceFirst("\n", ""));

                    etClientesD.setText(etClientesD.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etClientesD.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listaDestinatarios.size();i++)
                        {

                            resultp = listaDestinatarios.get(i);



                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNombreDest());


                            if(resultp.getStrNombreDest().toUpperCase().contains(etClientesD.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new ListaClientesDesti(resultp.getIntCodDest(), resultp.getStrNombreDest(), resultp.getStrCedulaDest(), resultp.getStrTelDest(), resultp.getStrDireDest(), resultp.getStrFechaDest()));

                            }

                        }



                        if(arrayaux != null)
                        {

                            mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, listaDestinatarios);
                            mListView.setAdapter(mListaDestinatirosAdap);
                            mListaDestinatirosAdap.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, arrayaux);
                            mListView.setAdapter(mListaDestinatirosAdap);
                            mListaDestinatirosAdap.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        mListaDestinatirosAdap = new AdapterDestinatarios( ListaDestinatarios.this, listaDestinatarios);
                        mListView.setAdapter(mListaDestinatirosAdap);
                        mListaDestinatirosAdap.notifyDataSetChanged();
                    }


                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListaClientesDesti objDestinatario = (ListaClientesDesti) mListView.getItemAtPosition(position);
                int intCodDest = objDestinatario.getIntCodDest();

                if(intCodDest > 0) {
                    new AlertDialog.Builder(ListaDestinatarios.this)
                            .setTitle("Informacion")
                            .setMessage("Antes de continuar con el proceso ten encuenta si los datos estan correctos o si debes realizar algun " +
                                    "tipo de modificacion de lo contrario omita el mensaje y dele continuar.")
                            .setNegativeButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (NetworkUtil.hayInternet(ListaDestinatarios.this)) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("intCodDestinatario", intCodDest);
                                        editor.commit();
                                        Intent intent = new Intent(ListaDestinatarios.this, RegistroDestinatario.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Sin Conexion a Internet.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (NetworkUtil.hayInternet(ListaDestinatarios.this)) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("strNombreDe", objDestinatario.getStrNombreDest());
                                        editor.putString("strApellidoDe", objDestinatario.getStrApellidoDest());
                                        editor.putString("strCompaniaDe", objDestinatario.getStrCompaniaDest());
                                        editor.putString("strDocumentoDe", objDestinatario.getStrCedulaDest());
                                        editor.putString("strDireccionDe", objDestinatario.getStrDireDest());
                                        editor.putString("strTelefonoDe", objDestinatario.getStrTelDest());
                                        editor.putInt("intCodDestN", intCodDest);
                                        editor.commit();

                                        Intent intent = new Intent(ListaDestinatarios.this, GeneracionGuia.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Sin Conexion A Internet", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();
                }
                else
                {
                    new AlertDialog.Builder(ListaDestinatarios.this)
                            .setTitle("Informacion")
                            .setMessage("No puedes continuar con el proceso por que no existe un destinatario creado para esta ciudad.")
                            .setNegativeButton("Aceptar",null)
                            .show();
                }

            }
        });

    }

    public class ListaDestinatariosAsyncTask extends AsyncTask<Integer, Integer, ArrayList<ListaClientesDesti>> {

        int intCodCli;
        String strNomDest;
        ProgressDialog progress;

        public ListaDestinatariosAsyncTask(int intCodCli, String strNomDest) {
            this.intCodCli = intCodCli;
            this.strNomDest = strNomDest;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(ListaDestinatarios.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<ListaClientesDesti> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaDestinatirosAdap = new AdapterDestinatarios(ListaDestinatarios.this, s);
            mListView.setAdapter(mListaDestinatirosAdap);
        }

        @Override
        protected ArrayList<ListaClientesDesti> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_DESTINATARIOS);
            request.addProperty("intCodCli", intCodCli);
            request.addProperty("strNomDest", strNomDest);
            SoapObject result;


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_DESTINATARIOS, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            int intCodDest;
            String strNomDest, strCedulaDest, strFechaRegDest, strDireccionDest, strCelDest, strApeDest, strCompaniaDest;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
                    intCodDest = Integer.parseInt(s.getProperty("CODDES").toString());
                    strNomDest = NetworkUtil.validarAnytype(s.getProperty("NOMDES").toString());
                    strApeDest = NetworkUtil.validarAnytype(s.getProperty("APEDES").toString());
                    strCompaniaDest = NetworkUtil.validarAnytype(s.getProperty("NOMBRE_COMPANIADES").toString());
                    strCedulaDest = s.getProperty("IDEDES").toString();
                    strFechaRegDest = s.getProperty("FECCREDES").toString();
                    strDireccionDest = NetworkUtil.validarAnytype(s.getProperty("DIRDES").toString());
                    strCelDest = NetworkUtil.validarAnytype(s.getProperty("TELDES").toString());

                    listaDestinatarios.add(new ListaClientesDesti(intCodDest, strNomDest, strCedulaDest, strCelDest, strDireccionDest, strFechaRegDest, strApeDest, strCompaniaDest, strNomCiuDes));

                }

            }
            else
            {

                intCodDest = -1;
                strNomDest = "No hay destinarios creados hacia esta ciudad destino.";
                strCedulaDest = "";
                strFechaRegDest ="";
                strDireccionDest ="";
                strCelDest = "";
                listaDestinatarios.add(new ListaClientesDesti(intCodDest, strNomDest, strCedulaDest, strCelDest, strDireccionDest, strFechaRegDest));


            }
            return listaDestinatarios;

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
