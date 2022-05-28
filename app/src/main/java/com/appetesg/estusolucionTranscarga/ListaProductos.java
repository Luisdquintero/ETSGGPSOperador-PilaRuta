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

import com.appetesg.estusolucionTranscarga.adapter.ProductoAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Producto;
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

public class ListaProductos extends AppCompatActivity {

    EditText etProductos;
    ProductoAdapter mListaProductos;
    Producto resultp;
    ArrayList<Producto> listProductos = new ArrayList<>();
    ArrayList<Producto>  arrayaux = new ArrayList<>();
    ArrayList<Producto>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;

    private static final String ACTION_LISTADO_PRODUCTOS = "ListaProductos";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaProductos";
    String BASE_URL, PREFS_NAME;
    String strCiudadDe, strOficinaOri;

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
        setContentView(R.layout.activity_lista_productos);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaProductos.this, ListaCiudadesD.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Lista De Productos");

        mListView = (ListView) findViewById(R.id.lstEnviosProd);
        etProductos = (EditText) findViewById(R.id.etEnviosProd);
        strCiudadDe = sharedPreferences.getString("strCodCiuDest", "");
        strOficinaOri = sharedPreferences.getString("strOficinaOri", "");

        new ListaProductosAsyncTask("INDESTACT = 1").execute();

        etProductos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etProductos.getRight() - etProductos.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        View view2 = getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }

                        arrayaux.clear();
                        if(!etProductos.getText().toString().equals(""))
                        {
                            //buscamos en el array el nombre y sus datos
                            for(int i=0;i<listProductos.size();i++)
                            {
                                resultp = listProductos.get(i);
                                HashMap<String, String> map = new HashMap<String, String>();

                                System.out.println("nombre_ciudad "+ resultp.getStrNomPrd());

                                if(resultp.getStrNomPrd().toUpperCase().contains(etProductos.getText().toString().toUpperCase()))
                                {

                                    arrayaux.add(new Producto(resultp.getIntCodPrd(), resultp.getStrNomPrd()));

                                }


                            }

                            if(arrayaux != null)
                            {

                                mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                                mListView.setAdapter(mListaProductos);
                                mListaProductos.notifyDataSetChanged();

                            }
                            else
                            {

                                mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                                mListView.setAdapter(mListaProductos);
                                mListaProductos.notifyDataSetChanged();
                            }

                        }
                        else
                        {

                            mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                            mListView.setAdapter(mListaProductos);
                            mListaProductos.notifyDataSetChanged();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        etProductos.addTextChangedListener(new TextWatcher() {
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
                    etProductos.setText(s.toString().replaceFirst("\n", ""));

                    etProductos.setText(etProductos.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etProductos.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<listProductos.size();i++)
                        {
                            resultp = listProductos.get(i);

                            System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomPrd());

                            if(resultp.getStrNomPrd().toUpperCase().contains(etProductos.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new Producto(resultp.getIntCodPrd(), resultp.getStrNomPrd()));

                            }

                        }

                        if(arrayaux != null)
                        {

                            mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                            mListView.setAdapter(mListaProductos);
                            mListaProductos.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                            mListView.setAdapter(mListaProductos);
                            mListaProductos.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        mListaProductos = new ProductoAdapter( ListaProductos.this, arrayaux);
                        mListView.setAdapter(mListaProductos);
                        mListaProductos.notifyDataSetChanged();
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
                if(NetworkUtil.hayInternet(ListaProductos.this)) {
                    Producto objProducto = (Producto) mListView.getItemAtPosition(position);

                    String strCodPrd = String.valueOf(objProducto.getIntCodPrd());
                    String strNomPrd = objProducto.getStrNomPrd();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("strCodPrd", strCodPrd);
                    editor.putString("strNomPrd", strNomPrd);
                    editor.commit();

                    finish();
                    Intent intent = new Intent(ListaProductos.this, ListaEnviosProd.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), strCodCiud, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public class ListaProductosAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Producto>> {

        String strFiltro;
        ProgressDialog progress;

        public ListaProductosAsyncTask(String strFiltro) {
            this.strFiltro = strFiltro;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(ListaProductos.this);
            progress .setMessage("Cargando");
            progress .show();
        }

        @Override
        protected void onPostExecute(ArrayList<Producto> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaProductos = new ProductoAdapter(ListaProductos.this, s);
            mListView.setAdapter(mListaProductos);
        }

        @Override
        protected ArrayList<Producto> doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_LISTADO_PRODUCTOS);

            request.addProperty("strFiltro", strFiltro);

            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_LISTADO_PRODUCTOS, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            String strCodPrd, strNomPrd;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);
                    strCodPrd = s.getProperty("CODPRD").toString();
                    strNomPrd = s.getProperty("NOMPRD").toString();

                    listProductos.add(new Producto(Integer.parseInt(strCodPrd), strNomPrd ));
                }

            }
            else
            {
                strCodPrd = "";
                strNomPrd = "No hay productos disponibles"; // error al consumir el WS
                listProductos.add(new Producto(0, strNomPrd));

            }
            return listProductos;

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
