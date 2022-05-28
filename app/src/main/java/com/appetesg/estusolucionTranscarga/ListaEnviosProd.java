package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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


import com.appetesg.estusolucionTranscarga.adapter.EnviosProdAdapter;
import com.appetesg.estusolucionTranscarga.modelos.EnviosProd;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;

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

public class ListaEnviosProd extends AppCompatActivity {

    EditText etEnviosProd;
    EnviosProdAdapter mListaEnvios;
    EnviosProd resultp;
    ArrayList<EnviosProd> listEnvios = new ArrayList<>();
    ArrayList<EnviosProd>  arrayaux = new ArrayList<>();
    ArrayList<EnviosProd>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {
        //NO HACE NADA AL OPRIMIR
    }

    private static final String ACTION_LISTADO_ENVIOS = "ListaEnvios";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaEnvios";
    String BASE_URL, PREFS_NAME;
    String strCiudadDe, strOficinaOri, strCodPrd, strNomPrd;

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
        setContentView(R.layout.activity_lista_envios_prod);


        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaEnviosProd.this, ListaProductos.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista De Envios");

        mListView = (ListView) findViewById(R.id.lstEnviosProd);
        etEnviosProd = (EditText) findViewById(R.id.etEnviosProd);
        strCiudadDe = sharedPreferences.getString("strCodCiuDest", "");
        strOficinaOri = sharedPreferences.getString("strOficinaOri", "");
        strCodPrd = sharedPreferences.getString("strCodPrd", "");
        strNomPrd = sharedPreferences.getString("strNomPrd", "");
        new ListaEnviosProdAsyncTask(strOficinaOri,strCiudadDe ,"CO", strCodPrd,etEnviosProd.getText().toString() ).execute();
        //new ListaEnviosProdAsyncTask(strOficinaOri,strCiudadDe ,"CO", "22",etEnviosProd.getText().toString() ).execute();

        etEnviosProd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etEnviosProd.getRight() - etEnviosProd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etEnviosProd.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listEnvios.size();i++)
                            {

                                resultp = listEnvios.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();

                                System.out.println("nombre_ciudad "+ resultp.getStrNomEnv());


                                if(resultp.getStrNomEnv().toUpperCase().contains(etEnviosProd.getText().toString().toUpperCase()))
                                {

                                    arrayaux.add(new EnviosProd(resultp.getIntCodTienv(), resultp.getStrNomEnv()));

                                }


                            }

                            if(arrayaux != null)
                            {

                                mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                                mListView.setAdapter(mListaEnvios);
                                mListaEnvios.notifyDataSetChanged();

                            }
                            else
                            {

                                mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                                mListView.setAdapter(mListaEnvios);
                                mListaEnvios.notifyDataSetChanged();
                            }

                        }
                        else
                        {

                            mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                            mListView.setAdapter(mListaEnvios);
                            mListaEnvios.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etEnviosProd.addTextChangedListener(new TextWatcher() {
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
                    etEnviosProd.setText(s.toString().replaceFirst("\n", ""));

                    etEnviosProd.setText(etEnviosProd.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etEnviosProd.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listEnvios.size();i++)
                        {
                            resultp = listEnvios.get(i);

                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomEnv());

                            if(resultp.getStrNomEnv().toUpperCase().contains(etEnviosProd.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new EnviosProd(resultp.getIntCodTienv(), resultp.getStrNomEnv()));

                            }

                        }

                        if(arrayaux != null)
                        {

                            mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                            mListView.setAdapter(mListaEnvios);
                            mListaEnvios.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                            mListView.setAdapter(mListaEnvios);
                            mListaEnvios.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        mListaEnvios = new EnviosProdAdapter( ListaEnviosProd.this, arrayaux);
                        mListView.setAdapter(mListaEnvios);
                        mListaEnvios.notifyDataSetChanged();
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
                if(NetworkUtil.hayInternet(ListaEnviosProd.this)) {
                    EnviosProd objEnviosProd = (EnviosProd) mListView.getItemAtPosition(position);

                    int intCodTienv = objEnviosProd.getIntCodTienv();
                    String strNomEnv = objEnviosProd.getStrNomEnv();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("intCodTienvC", intCodTienv);
                    editor.putString("strNombreEC", strNomEnv);
                    editor.putString("strCodPrd",strCodPrd);
                    editor.putString("strNomPrd",strNomPrd);
                    editor.commit();

                    finish();
                    Intent intent = new Intent(ListaEnviosProd.this, RegistroGuia.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), strCodCiud, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public class ListaEnviosProdAsyncTask extends AsyncTask<Integer, Integer, ArrayList<EnviosProd>> {

        String strOficina, strCodCiud, strCodPai, strCodProd, strNomTien;
        ProgressDialog progress;

        public ListaEnviosProdAsyncTask(String strOficina, String strCodCiud, String strCodPai, String strCodProd, String strNomTien) {
            this.strOficina = strOficina;
            this.strCodCiud = strCodCiud;
            this.strCodPai = strCodPai;
            this.strCodProd = strCodProd;
            this.strNomTien = strNomTien;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(ListaEnviosProd.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<EnviosProd> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaEnvios = new EnviosProdAdapter(ListaEnviosProd.this, s);
            mListView.setAdapter(mListaEnvios);
        }

        @Override
        protected ArrayList<EnviosProd> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_ENVIOS);

            request.addProperty("strOficina", strOficina);
            request.addProperty("strPais", strCodPai);
            request.addProperty("strCiudad", strCodCiud);
            request.addProperty("strCodPrdo", strCodProd);
            request.addProperty("strNomTien", strNomTien);

            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_ENVIOS, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strCodEnv, strNomEnv;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);
            String strNomTipo;

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
                    strCodEnv = s.getProperty("CODTIPENV").toString();
                    strNomEnv = s.getProperty("NOMTIPENV").toString();

                    listEnvios.add(new EnviosProd(Integer.parseInt(strCodEnv),  strNomEnv ));
                }

            }
            else
            {
                strCodEnv = "";
                strNomEnv = "No hay envios disponibles"; // error al consumir el WS
                listEnvios.add(new EnviosProd(0, strNomEnv));

            }
            return listEnvios;

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
