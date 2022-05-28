package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.adapter.CiudadesDesApapter;
import com.appetesg.estusolucionTranscarga.modelos.CiudadesD;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaCiudadesCotizacion extends AppCompatActivity {

    EditText etCiudadCo;
    CiudadesDesApapter mListaCiudCo;
    CiudadesD resultp;
    ArrayList<CiudadesD> listCiudadCo = new ArrayList<>();
    ArrayList<CiudadesD>  arrayaux = new ArrayList<>();
    ArrayList<CiudadesD>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;


    private static final String ACTION_LISTADO_CIUDADES = "ListaCiudadesDestinoBusqueda";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaCiudadesDestinoBusqueda";
    String BASE_URL, PREFS_NAME;

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
        setContentView(R.layout.activity_lista_ciudades_cotizacion);


        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaCiudadesCotizacion.this, MenuLogistica.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Busqueda De Ciudades");

        mListView = (ListView) findViewById(R.id.lstCiudadCo);
        etCiudadCo = (EditText) findViewById(R.id.etCiudadCo);

        new ListaCiudadDestAsyncTask(etCiudadCo.getText().toString()).execute();


        etCiudadCo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etCiudadCo.getRight() - etCiudadCo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etCiudadCo.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listCiudadCo.size();i++)
                            {

                                resultp = listCiudadCo.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();




                                System.out.println("nombre_ciudad "+ resultp.getStrNomCiuDe());


                                if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadCo.getText().toString().toUpperCase()))
                                {


                                    arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe()));

                                }


                            }



                            if(arrayaux != null)
                            {

                                mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, arrayaux);
                                mListView.setAdapter(mListaCiudCo);
                                mListaCiudCo.notifyDataSetChanged();



                            }
                            else
                            {

                                mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, arrayaux);
                                mListView.setAdapter(mListaCiudCo);
                                mListaCiudCo.notifyDataSetChanged();
                            }






                        }
                        else
                        {


                            mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, listCiudadCo);
                            mListView.setAdapter(mListaCiudCo);
                            mListaCiudCo.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etCiudadCo.addTextChangedListener(new TextWatcher() {
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
                    etCiudadCo.setText(s.toString().replaceFirst("\n", ""));

                    etCiudadCo.setText(etCiudadCo.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etCiudadCo.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listCiudadCo.size();i++)
                        {

                            resultp = listCiudadCo.get(i);



                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomCiuDe());



                            if(resultp.getStrNomCiuDe().toUpperCase().contains(etCiudadCo.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new CiudadesD(resultp.getStrCodCiuDe(), resultp.getStrNomCiuDe()));


                            }


                        }



                        if(arrayaux != null)
                        {

                            mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, arrayaux);
                            mListView.setAdapter(mListaCiudCo);
                            mListaCiudCo.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, arrayaux);
                            mListView.setAdapter(mListaCiudCo);
                            mListaCiudCo.notifyDataSetChanged();
                        }






                    }
                    else
                    {
                        mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, listCiudadCo);
                        mListView.setAdapter(mListaCiudCo);
                        mListaCiudCo.notifyDataSetChanged();
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
                CiudadesD objCiudades = (CiudadesD) mListView.getItemAtPosition(position);
                String strCodCiud = objCiudades.getStrCodCiuDe();
                String strNomCiud = objCiudades.getStrNomCiuDe();

                if(!strCodCiud.equalsIgnoreCase("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strCodCiuCO", strCodCiud);
                    editor.putString("strNomciuCO", strNomCiud);
                    editor.commit();

                    Intent intent = new Intent(ListaCiudadesCotizacion.this, TasaVolumetrica.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), strCodCiud, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "NO HAY COTIZACIONES", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public class ListaCiudadDestAsyncTask extends AsyncTask<Integer, Integer, ArrayList<CiudadesD>> {

        String strNomciu;

        public ListaCiudadDestAsyncTask(String strNomciu) {
            this.strNomciu = strNomciu;
        }

        @Override
        protected void onPostExecute(ArrayList<CiudadesD> s) {
            super.onPostExecute(s);
            mListaCiudCo = new CiudadesDesApapter( ListaCiudadesCotizacion.this, s);
            mListView.setAdapter(mListaCiudCo);
        }

        @Override
        protected ArrayList<CiudadesD> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_CIUDADES);
            request.addProperty("strNomciu", strNomciu);
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


            String strCodCiud, strNomCiu;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
                    strCodCiud = s.getProperty("CODCIU").toString();
                    strNomCiu = s.getProperty("NOMCIU").toString();
                    listCiudadCo.add(new CiudadesD(strCodCiud, strNomCiu));
                }

            }
            else
            {
                strCodCiud = "";
                strNomCiu = "No hay cotizaciones disponibles.";
                listCiudadCo.add(new CiudadesD(strCodCiud, strNomCiu));

            }
            return listCiudadCo;

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
