package com.appetesg.estusolucionTranscarga;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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


import com.appetesg.estusolucionTranscarga.adapter.CiudadesDesApapter;
import com.appetesg.estusolucionTranscarga.modelos.CiudadesD;
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

public class ListaCiudadesD extends AppCompatActivity {

    EditText etCiudadDe;
    CiudadesDesApapter mListaCiudDest;
    CiudadesD resultp;
    ArrayList<CiudadesD> listCiudad = new ArrayList<>();
    ArrayList<CiudadesD>  arrayaux = new ArrayList<>();
    ArrayList<CiudadesD>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;
    String strCiudadDe;
    int idUsuario;

    private static final String ACTION_LISTADO_CIUDADES = "ListaCiudadesDestinoBusqueda";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaCiudadesDestinoBusqueda";
    String BASE_URL, PREFS_NAME;

    @Override
    public void onBackPressed()
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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
        setContentView(R.layout.activity_lista_ciudades_d);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaCiudadesD.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Busqueda De Ciudades");

        mListView = (ListView) findViewById(R.id.lstCiudadDest);
        etCiudadDe = (EditText) findViewById(R.id.etCiudadDe);
        idUsuario = sharedPreferences.getInt("idUsuario", 0);
        if(NetworkUtil.hayInternet(this)) {
            new ListaCiudadDestAsyncTask(etCiudadDe.getText().toString(), idUsuario).execute();
        }else{
            Toast.makeText(getBaseContext(),"Sin conexion a internet",Toast.LENGTH_LONG).show();
        }

        etCiudadDe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etCiudadDe.getRight() - etCiudadDe.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etCiudadDe.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listCiudad.size();i++)
                            {

                                resultp = listCiudad.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();

                                //System.out.println("nombre_ciudad "+ resultp.getStrNomCiuDe() + " cod "+ resultp.getStrOficina());

                                if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadDe.getText().toString().toUpperCase()))
                                {

                                    arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe(), resultp.getStrOficina()));

                                }
                            }

                            if(arrayaux != null)
                            {

                                mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, arrayaux);
                                mListView.setAdapter(mListaCiudDest);
                                mListaCiudDest.notifyDataSetChanged();

                            }
                            else
                            {

                                mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, arrayaux);
                                mListView.setAdapter(mListaCiudDest);
                                mListaCiudDest.notifyDataSetChanged();
                            }
                        }
                        else
                        {


                            mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, listCiudad);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etCiudadDe.addTextChangedListener(new TextWatcher() {
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
                    etCiudadDe.setText(s.toString().replaceFirst("\n", ""));

                    etCiudadDe.setText(etCiudadDe.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etCiudadDe.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listCiudad.size();i++)
                        {

                            resultp = listCiudad.get(i);

                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomCiuDe());

                            if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadDe.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe(), resultp.getStrOficina()));

                            }

                        }

                        if(arrayaux != null)
                        {

                            mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, arrayaux);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, arrayaux);
                            mListView.setAdapter(mListaCiudDest);
                            mListaCiudDest.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, listCiudad);
                        mListView.setAdapter(mListaCiudDest);
                        mListaCiudDest.notifyDataSetChanged();
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
                if(NetworkUtil.hayInternet(ListaCiudadesD.this)) {
                CiudadesD objCiudades = (CiudadesD) mListView.getItemAtPosition(position);
                String strCodCiud = objCiudades.getStrCodCiuDe();
                String strNomCiud = objCiudades.getStrNomCiuDe();
                String strOficinaOri = objCiudades.getStrOficina();


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strCodCiuDest", strCodCiud);
                    //editor.putString("strCiudOrigeb", strNomCiud);
                    editor.putString("strNomciuDest", strNomCiud);
                    editor.putString("strOficinaOri", strOficinaOri);
                    editor.commit();

                    Intent intent = new Intent(ListaCiudadesD.this, ListaProductos.class);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(getApplicationContext(), strCodCiud, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No hay conexion en internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public class ListaCiudadDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<CiudadesD>> {

        String strNomciu;
        int idUsuario;
        ProgressDialog progress;

        public ListaCiudadDestAsyncTask(String strNomciu, int idUsuario) {
            this.strNomciu = strNomciu;
            this.idUsuario = idUsuario;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(ListaCiudadesD.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<CiudadesD> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaCiudDest = new CiudadesDesApapter( ListaCiudadesD.this, s);
            mListView.setAdapter(mListaCiudDest);
        }

        @Override
        protected ArrayList<CiudadesD> doInBackground(Integer... integers) {
            try{
                SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_CIUDADES);
                request.addProperty("strNomciu", strNomciu);
                request.addProperty("intIdUsuario", idUsuario);
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

                String strCodCiud, strNomCiu, strOficina;

                SoapObject getListResponse = (SoapObject) result.getProperty(0);
                SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

                if(DocumentElement.getPropertyCount() > 0) {

                    SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                    for (int i = 0; i < table1.getPropertyCount(); i++) {
                        SoapObject s = (SoapObject) table1.getProperty(i);
                        strCodCiud = s.getProperty("CODCIU").toString();
                        strNomCiu = s.getProperty("NOMCIU").toString();
                        strOficina = s.getProperty("CODOFIORI").toString();
                        listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, strOficina));
                    }
                }
                else
                {
                    strCodCiud = "";
                    strNomCiu = "No hay ciudades disponibles o tarifas registradas.";
                    listCiudad.add(new CiudadesD(strCodCiud, strNomCiu, ""));

                }
                return listCiudad;
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return listCiudad;
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
