package com.appetesg.estusolucionTranscarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AlertDialog;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.appetesg.estusolucionTranscarga.adapter.AdapterHistoricoGuia;
import com.appetesg.estusolucionTranscarga.db.Db;
import com.appetesg.estusolucionTranscarga.modelos.RotulosGuia;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.github.clans.fab.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoricoGuia extends AppCompatActivity {

    ImageView imgLogo, imgAlfa;

    EditText etGuiaCo;
    AdapterHistoricoGuia mListaHistoricoAdapter;
    RotulosGuia resultp;
    RotulosGuia dtRotulo;
    ArrayList<RotulosGuia> rotulosGuias = new ArrayList<>();
    ArrayList<RotulosGuia>  arrayaux = new ArrayList<>();
    ArrayList<RotulosGuia>items;
    Toolbar toolbar;
    ListView mListView;
    SharedPreferences sharedPreferences;
    FloatingActionButton btnNuevo;
    int idUsuario = 0;
    TextView txtTotalPagoGuias;
    private static final String ACTION_HISTORICO = "HistoricoOperario";
    private static final String ACTION_CONCEPTO = "ValorGuiaFiltro";
    private static final String ACTION_COUNT = "ContadorDestinatarios";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "ListaClientesBusqueda";
    String BASE_URL, PREFS_NAME;

    Db usdbh;
    SQLiteDatabase db;

    @Override
    public void onBackPressed() {
        //NO HACE NADA AL OPRIMIR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // controlar las excepciones cuando se cierra la app
        Thread.setDefaultUncaughtExceptionHandler(this::excepcionCapturada);
        setContentView(R.layout.activity_historico_guia);

        PREFS_NAME = this.getString(R.string.SPREF);

        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        usdbh = new Db(HistoricoGuia.this, "historial"/*getResources().getString(R.string.name_bd)*/, null, Integer.parseInt(getResources().getString(R.string.version_database)));
        db = usdbh.getWritableDatabase();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(HistoricoGuia.this, MenuLogistica.class);
            startActivity(intent);
            finish();
        });

        TextView lblTextoToolbar = toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Historico De Guias");

        mListView = findViewById(R.id.lstGuiasHistorico);
        etGuiaCo = findViewById(R.id.edGuiaConsulta);
        btnNuevo = findViewById(R.id.btnNuevoCli);
        idUsuario =  sharedPreferences.getInt("idUsuario",0);
        txtTotalPagoGuias = findViewById(R.id.txtTotalPagoGuias);

        btnNuevo.setOnClickListener(view -> {
            Intent intent = new Intent(HistoricoGuia.this, ListaCiudadesD.class);
            finish();
            startActivity(intent);
        });

        if(hasConnection(HistoricoGuia.this))
        {
            new HistoricoGuiasAsyncTask(idUsuario).execute();
        }
        else
        {
            get_guias_historico_offine();
            mListaHistoricoAdapter = new AdapterHistoricoGuia(HistoricoGuia.this, rotulosGuias);
            mListView.setAdapter(mListaHistoricoAdapter);
        }

        etGuiaCo.setOnTouchListener((v, event) -> {
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

                    arrayaux.clear();
                    if(!etGuiaCo.getText().toString().equals(""))
                    {
                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<rotulosGuias.size();i++)
                        {

                            resultp = rotulosGuias.get(i);
                            HashMap<String, String> map = new HashMap<String, String>();

                            System.out.println("nombre_cliente "+ resultp.getStrNomCli());

                            if(resultp.getStrPedido1().toUpperCase().contains(etGuiaCo.getText().toString().toUpperCase()))
                            {

                                arrayaux.add(new RotulosGuia(resultp.getIntCodusu(), resultp.getIntCantidad(), resultp.getStrPedido1(), resultp.getStrCiudadDestino(), resultp.getStrCiudadOrigen(), resultp.getStrNomCli(), resultp.getStrNomDest(),
                                        resultp.getStrNomForPag(), resultp.getStrDirOri(), resultp.getStrDirDest(), resultp.getStrValorEnvio(), resultp.getStrValorGeneral(), resultp.getStrFecha(), resultp.getStrCelCli(), resultp.getStrNomPrd(),
                                        resultp.getStrContenido(), resultp.getStrPesoPaq(), resultp.getStrValDec(), resultp.getImgEmbarque(), resultp.getIntCodCli(), resultp.getStrCelDes(), resultp.getStrQR(), resultp.getStrValorFlete()));

                            }

                        }

                        if(arrayaux != null)
                        {
                            mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, arrayaux);
                            mListView.setAdapter(mListaHistoricoAdapter);
                            mListaHistoricoAdapter.notifyDataSetChanged();

                        }
                        else
                        {
                            mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, arrayaux);
                            mListView.setAdapter(mListaHistoricoAdapter);
                            mListaHistoricoAdapter.notifyDataSetChanged();
                        }

                    }
                    else
                    {

                        mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, rotulosGuias);
                        mListView.setAdapter(mListaHistoricoAdapter);
                        mListaHistoricoAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            }
            return false;
        });
        etGuiaCo.addTextChangedListener(new TextWatcher() {
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
                    etGuiaCo.setText(s.toString().replaceFirst("\n", ""));

                    etGuiaCo.setText(etGuiaCo.getText().toString().trim());

                    View view2 = getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                    }

                    if(!etGuiaCo.getText().toString().equals(""))
                    {
                        arrayaux.clear();

                        //buscamos en el array el nombre y sus datos
                        for(int i=0;i<rotulosGuias.size();i++)
                        {
                            resultp = rotulosGuias.get(i);

                            //System.out.println("NOMBRE DE LA CIUDAD "+resultp.getStrNomCli());

                            if(resultp.getStrPedido1().toUpperCase().contains(etGuiaCo.getText().toString().toUpperCase()))
                            {
                                arrayaux.add(new RotulosGuia(resultp.getIntCodusu(), resultp.getIntCantidad(), resultp.getStrPedido1(), resultp.getStrCiudadDestino(), resultp.getStrCiudadOrigen(), resultp.getStrNomCli(), resultp.getStrNomDest(),
                                        resultp.getStrNomForPag(), resultp.getStrDirOri(), resultp.getStrDirDest(),resultp.getStrValorEnvio(), resultp.getStrValorGeneral(), resultp.getStrFecha(), resultp.getStrCelCli(), resultp.getStrNomPrd(),
                                        resultp.getStrContenido(), resultp.getStrPesoPaq(), resultp.getStrValDec(), resultp.getImgEmbarque(), resultp.getIntCodCli(), resultp.getStrCelDes(), resultp.getStrQR(), resultp.getStrValorFlete()));

                            }

                        }

                        if(arrayaux != null)
                        {

                            mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, arrayaux);
                            mListView.setAdapter(mListaHistoricoAdapter);
                            mListaHistoricoAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, arrayaux);
                            mListView.setAdapter(mListaHistoricoAdapter);
                            mListaHistoricoAdapter.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        mListaHistoricoAdapter = new AdapterHistoricoGuia( HistoricoGuia.this, rotulosGuias);
                        mListView.setAdapter(mListaHistoricoAdapter);
                        mListaHistoricoAdapter.notifyDataSetChanged();
                    }


                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mListView.setOnItemLongClickListener((parent, view, position, id) -> {
            RotulosGuia objRotulo = (RotulosGuia) mListView.getItemAtPosition(position);
            dialorInformativo("Enviar guia por whatsapp",objRotulo.getStrCelCli(),objRotulo.getStrQR(),objRotulo.getStrPedido1()).show();
            return true;
        });

        mListView.setOnItemClickListener((adapterView, view, position, l) -> {
            RotulosGuia objRotulo = (RotulosGuia) mListView.getItemAtPosition(position);
            int intCodusu = objRotulo.getIntCodusu();
            int intCantidad = objRotulo.getIntCantidad();

            if(intCodusu > 0)
            {
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).
                        edit().clear().apply();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("intCodusuImpresionRotulo", intCodusu);
                editor.putInt("intCantidadImpresionRotulo", intCantidad);
                editor.putString("strPedido1ImpresionRotulo", objRotulo.getStrPedido1());
                editor.putString("strFormaPagoImpresionRotulo", objRotulo.getStrNomForPag());
                editor.putString("strCiudadDestinoImpresionRotulo",objRotulo.getStrCiudadDestino());
                editor.putString("strNomDestImpresionRotulo", objRotulo.getStrNomDest());
                editor.putString("strDireccionDestImpresionRotulo", objRotulo.getStrDirDest());
                editor.putString("strCiudadOrigenImpresionRotulo", objRotulo.getStrCiudadOrigen());
                editor.putString("strNomcliImpresionRotulo", objRotulo.getStrNomCli());
                editor.putString("strDirOriImpresionRotulo", objRotulo.getStrDirOri());
                editor.putString("strCelcliImpresion", objRotulo.getStrCelCli());
                editor.putString("strCelDesImpresion", objRotulo.getStrCelDes());
                editor.putInt("intCodCli", objRotulo.getIntCodCli());
                editor.putString("strNomprdImpresion", objRotulo.getStrNomPrd());
                editor.putString("strContenidoImpresion", objRotulo.getStrContenido());
                editor.putString("strPesoPaqImpresion", objRotulo.getStrPesoPaq());
                editor.putString("strValorEnvioImpresion", objRotulo.getStrValorEnvio());
                editor.putString("strValorDecGeneral", objRotulo.getStrValorGeneral());
                editor.putString("strValorDec", objRotulo.getStrValDec());
                editor.putString("bitPuertaEmbarque", objRotulo.getImgEmbarque().toString());// TOSTRING?
                editor.putString("strQR", objRotulo.getStrQR().toString());// TOSTRING?
                editor.putString("strValorFletes", objRotulo.getStrValorFlete().toString());// TOSTRING?
                editor.commit();

                Intent intent = new Intent(HistoricoGuia.this, ImpresionRotulo.class);
                startActivity(intent);
                //new ConceptoGuiaAsyncTask(objRotulo.getStrPedido1().substring(4),objRotulo.getStrPedido1().substring(0,4),750).execute();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoGuia.this);
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Alerta</span><span>"));
                builder.setMessage(Html.fromHtml("<p><span>No puede generar rotulos ya que no hay guias existentes, de lo contrario comunicarse con el administador</span>"));
                builder.setCancelable(false);

                builder.setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.show();
                TextView messageView = dialog.findViewById(android.R.id.message);
                messageView.setTextSize(20);
            }

        });

    }

    public class HistoricoGuiasAsyncTask extends AsyncTask<Integer, Integer, ArrayList<RotulosGuia>> {

        int intCodusu;
        ProgressDialog progress;

        public HistoricoGuiasAsyncTask(int intCodusu) {
            this.intCodusu = intCodusu;
        }

        @Override
        protected  void onPreExecute()
        {
            progress = new ProgressDialog(HistoricoGuia.this);
            progress.setMessage("Cargando");
            progress.show();
        }

        @Override
        protected void onPostExecute(ArrayList<RotulosGuia> s) {
            super.onPostExecute(s);
            progress.dismiss();  // dismiss dialog
            mListaHistoricoAdapter = new AdapterHistoricoGuia(HistoricoGuia.this, s);
            mListView.setAdapter(mListaHistoricoAdapter);
            float intContadorValores = 0;

            for(int i = 0; i < rotulosGuias.size(); i++)
            {
                resultp = rotulosGuias.get(i);
                intContadorValores += Float.parseFloat(resultp.getStrValorGeneral());
            }

            txtTotalPagoGuias.setText("Total recaudado por dia: " + DecimalFormat.getCurrencyInstance().format(intContadorValores));

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<RotulosGuia> doInBackground(Integer... integers)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            String currentDate = dateFormat.format(today);

            SoapObject request = new SoapObject(NAMESPACE, ACTION_HISTORICO);
            request.addProperty("intCodusu", intCodusu);
            request.addProperty("strFecha", currentDate);
            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_HISTORICO, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            int intCantidad, intCodusu, intCodEst, intCodCli;
            String strNomCli, strNomdest, strCiudDest, strCiudOri, strDirOri, strDirDest,
                    strPedido1, strNomforPag, strValorUnico, strValorGeneral, strFecha,
                    strCelCli, strNomprd, strContenido, strPesoPaq, strValorDec, strCelDes,
                    btPuertaEmbarque, strQR, strValFlete;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);
                ArrayList<String> lstRotulos = new ArrayList<>();
                lstRotulos.add("1");
                for (int i = 0; i < table1.getPropertyCount(); i++)
                {
                    boolean pas = true;
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    intCantidad = Integer.parseInt(s.getProperty("CANTIDAD").toString());
                    intCodusu = Integer.parseInt(s.getProperty("CODUSU").toString());
                    strNomCli = s.getProperty("NOMBRE_COMPANIAORI").toString();
                    strNomdest = s.getProperty("NOMBRE_COMPANIADES").toString();
                    strCiudDest = s.getProperty("DESTINO").toString();
                    strCiudOri = s.getProperty("ORIGEN").toString();
                    strDirOri = s.getProperty("DIRCLI").toString();
                    strDirDest = s.getProperty("DIRDES").toString();
                    strPedido1 = s.getProperty("PEDIDO1").toString();
                    strNomforPag = s.getProperty("NOMFORPAG").toString();
                    strValorUnico = s.getProperty("VALPAG").toString();
                    strValorGeneral = s.getProperty("VALORUNICO").toString();
                    strFecha = s.getProperty("FECHA").toString();
                    strCelCli = s.getProperty("CELCLI").toString();
                    strCelDes = s.getProperty("CELDES").toString();
                    strNomprd = s.getProperty("NOMPRD").toString();
                    strContenido = s.getProperty("DESCON").toString();
                    strPesoPaq = s.getProperty("PESPAQ").toString();
                    strValorDec = s.getProperty("VALDEC").toString();
                    intCodEst = Integer.parseInt(s.getProperty("CODEST").toString());
                    intCodCli = Integer.parseInt(s.getProperty("CODCLI").toString());
                    btPuertaEmbarque = s.getProperty("PUERTA_EMBARQUE_IMAGE").toString();
                    strQR = s.getProperty("QR").toString();
                    strValFlete = s.getProperty("VALFLETE").toString();
                    //System.out.println("puerta: "+btPuertaEmbarque);

                    //Valor del flete

                    try{
                        byte [] encodeByte = android.util.Base64.decode(btPuertaEmbarque, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        System.out.println("puerta: "+btPuertaEmbarque);
                        //imgAlfa.setImageBitmap(bitmap);
                        //return bitmap;
                    }
                    catch(Exception e){
                        e.getMessage();
                        return null;
                    }

                    boolean val = true;
                    for(int k=0;k<lstRotulos.size();k++){
                        if(val) {
                            if (lstRotulos.get(k).equalsIgnoreCase(strPedido1) || intCodEst != 0)
                                pas = false;
                        }
                    }

                    if(pas)
                    {
                        lstRotulos.add(strPedido1);
                        RotulosGuia rotulo = new RotulosGuia(intCodusu, intCantidad, strPedido1, strCiudDest,
                                strCiudOri, strNomCli, strNomdest, strNomforPag, strDirOri, strDirDest,
                                strValorUnico, strValorGeneral, strFecha, strCelCli, strNomprd, strContenido,
                                strPesoPaq, strValorDec, btPuertaEmbarque, intCodCli, strCelDes, strQR, strValFlete);
                        rotulosGuias.add(rotulo);

                        // Se almacenan offline las guias leidas de la BD online
                        try {
                            db = usdbh.getWritableDatabase();
                            if (db != null) {
                                try {
                                    int intc = 0;
                                    //db.execSQL("Delete from Historico");
                                    System.out.println("ContadorLEGA: " + intc + s.getProperty("FECHA").toString());
                                    db.execSQL("Insert into Historico (strPedido1, intCantidad, intCodusu," +
                                            " strCompaniaOri, strCompaniaDest, " +
                                            "strCiudadDest, strCiudadOri, strDirOri, strDirDest, " +
                                            "strFormaPago, strValPag, strValorUnico," +
                                            " strFechas, strCelCli, strCelDes, strNomPrd, strContenido," +
                                            " strPesoPaq, strValorDec, intCodEst, intCodCli, strPuertaEmbarque , strValFlete, strQR)" +
                                            " values ('" + s.getProperty("PEDIDO1").toString() + "'," +
                                            " '" + Integer.parseInt(s.getProperty("CANTIDAD").toString()) + "'," +
                                            " '" + Integer.parseInt(s.getProperty("CODUSU").toString()) + "'," +
                                            " '" + s.getProperty("NOMBRE_COMPANIAORI").toString() + "', " +
                                            " '" + s.getProperty("NOMBRE_COMPANIADES").toString() + "'," +
                                            " '" + s.getProperty("DESTINO").toString() + "'," +
                                            " '" + s.getProperty("ORIGEN").toString() + "', " +
                                            " '" + s.getProperty("DIRCLI").toString() + "', " +
                                            " '" + s.getProperty("DIRDES").toString() + "', " +
                                            " '" + s.getProperty("NOMFORPAG").toString() + "'," +
                                            " '" + s.getProperty("VALPAG").toString() + "'," +
                                            " '" + s.getProperty("VALORUNICO").toString() + "'," +
                                            " '" + s.getProperty("FECHA").toString() + "'," +
                                            " '" + s.getProperty("CELCLI").toString() + "'," +
                                            " '" + s.getProperty("CELDES").toString() + "'," +
                                            " '" + s.getProperty("NOMPRD").toString() + "'," +
                                            " '" + s.getProperty("DESCON").toString() + "'," +
                                            " '" + s.getProperty("PESPAQ").toString() + "'," +
                                            " '" + s.getProperty("VALDEC").toString() + "'," +
                                            " '" + Integer.parseInt(s.getProperty("CODEST").toString()) + "'," +
                                            " '" + Integer.parseInt(s.getProperty("CODCLI").toString()) + "'," +
                                            " '" + s.getProperty("PUERTA_EMBARQUE_IMAGE").toString() + "'," +
                                            " '" + s.getProperty("VALFLETE").toString() + "'," +
                                            " '" + s.getProperty("QR").toString() + "')");

                                } catch (SQLException e) {
                                }
                            }
                        } catch (SQLException ex) {
                        }
                    }
                }

            }
            else
            {
                intCantidad = 0;
                intCodusu = 0;
                strNomCli ="";
                strNomdest = "";
                strCiudDest = "";
                strCiudOri = "";
                strDirOri = "";
                strDirDest = "";
                strPedido1 = "No hay guias";
                strNomforPag = "";
                strValorUnico = "$0";
                strValorGeneral = "0";
                strFecha = "";
                strCelCli = "";
                strCelDes = "";
                strNomprd = "";
                strContenido = "";
                btPuertaEmbarque = null;
                strQR = "";
                rotulosGuias.add(new RotulosGuia(intCodusu, intCantidad, strPedido1, strCiudDest,
                        strCiudOri, strNomCli, strNomdest, strNomforPag, strDirOri, strDirDest, strValorUnico,
                        strValorGeneral, strFecha, strCelCli, strNomprd, strContenido, "0", "$0", btPuertaEmbarque ,0, strCelDes, strQR, "0"));

            }
            return rotulosGuias;

        }

    }

    public void get_guias_historico_offine()
    {
        try
        {
            Date today = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(today);

            System.out.println("ESTAS ACA");

            Cursor c = db.rawQuery("select strPedido1, intCantidad, intCodusu, " +
                    "strCompaniaOri, strCompaniaDest, " +
                    "strCiudadDest, strCiudadOri, strDirOri, strDirDest, " +
                    "strFormaPago, strValPag, strValorUnico, " +
                    "strFechas, strCelCli, strCelDes, strNomPrd, strContenido, strPesoPaq, " +
                    "strValorDec, intCodEst, intCodCli, strPuertaEmbarque, strQR, strValFlete from Historico where strFechas = '" + currentDate + "'", null);

            System.out.println(" fecha android:" + currentDate);
            if(c.getCount() > 0)
            {
                System.out.println("ESTAS ACA3");
                if(c.moveToFirst())
                {
                    System.out.println("ESTAS ACA4");
                    do{
                        rotulosGuias.add(new RotulosGuia(Integer.parseInt(c.getString(2)),Integer.parseInt(c.getString(1)), c.getString(0), c.getString(5), c.getString(6), c.getString(3),
                                c.getString(4), c.getString(9), c.getString(7), c.getString(8), c.getString(10), c.getString(11), c.getString(12), c.getString(13),
                                c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(21), c.getInt(20),c.getString(14),c.getString(22), c.getString(23)));
                        System.out.println("ESTAS ACA5");

                    }
                    while (c.moveToNext());

                    if(rotulosGuias.size() > 0)
                    {
                        int intContadorValores = 0;

                        for(int i = 0; i < rotulosGuias.size(); i++)
                        {
                            resultp = rotulosGuias.get(i);
                            intContadorValores += Float.parseFloat(resultp.getStrValorGeneral());
                        }

                        txtTotalPagoGuias.setText("Total recaudado por dia: " + DecimalFormat.getCurrencyInstance().format(intContadorValores));
                    }
                    System.out.println("ESTAS ACA99");
                }
                else
                {
                    System.out.println("ESTAS ACA80");
                    rotulosGuias.add(new RotulosGuia(0, 0, "No hay guias", "",
                            "", "", "", "", "", "", "$0",
                            "0", "", "", "", "", "0", "$0",
                            null, 0 ,"","",""));

                    txtTotalPagoGuias.setText("Total recaudado por dia: " + DecimalFormat.getCurrencyInstance().format(0));

                }
                if(c!=null){
                    System.out.println("ESTAS ACA30");
                    c.close();
                    db.close();
                }
            }
            else{
                System.out.println("ESTAS ACA44");
            }
        }
        catch (SQLException ex)
        {}
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

    //Metodo de consumo de datos
    public class ConceptoGuiaAsyncTask extends AsyncTask<Integer, Integer, String> {

        int intConcepto;
        String strCodGuia, strOficina;

        public ConceptoGuiaAsyncTask(String strCodGuia, String strOficina, int intConcepto) {
            this.strCodGuia = strCodGuia;
            this.strOficina = strOficina;
            this.intConcepto = intConcepto;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("strValorFletes", s);
            editor.commit();

            Intent intent = new Intent(HistoricoGuia.this, ImpresionRotulo.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            SoapObject request = new SoapObject(NAMESPACE, ACTION_CONCEPTO);
            String res = "";
            request.addProperty("CODOFI",strOficina);
            request.addProperty("CONCEPTO",intConcepto);
            request.addProperty("CONGUI",strCodGuia);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try
            {
                httpTransport.call(NAMESPACE+ACTION_CONCEPTO, envelope);
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
                res = "0";
            }
            return res;

        }
    }

    // Generar un alerta para compartir la guia por whatsapp
    public AlertDialog dialorInformativo(String strMensaje, String strCelcli, String strQR, String strPedido)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(HistoricoGuia.this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(strCelcli.toString() != "0") {
                            sendMessageToWhatsAppContact(strCelcli.toString(), strQR, strPedido);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "No puedes enviar un mensaje ya que no contiene un numero de celular",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        ;
        return buldier.create();
    }

    // Se envia msg a Whatsapp
    private void sendMessageToWhatsAppContact(String number, String strQR,String strPedido ) {
        //PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            // Se reemplaza ampersan por el codigo representativo
            strQR = strQR.replace("&","%26");

            String strUrl[] = BASE_URL.split("WebService");

            // Arma msg y llamado a whatsapp
            String strMensaje = "Gracias por preferirnos, "+ this.getString(R.string.Nombre_Compania) + " te desea un buen dia." +
                    "\nEn el siguiente link encuentra la guia " + strPedido +" en formato PDF. \n\n "+ strUrl[0] + this.getString(R.string.URL_GUIA_IMPRESION) + strQR;
            //EL indicadtivo del pais se cambia segun su numero en esta caso panama es el 507 -- 20201106 LEGA
            String url = "https://api.whatsapp.com/send?phone=" + "57" + number + "&text=" + strMensaje;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
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
