package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
//import android.support.v4.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.appetesg.estusolucionTranscarga.adapter.EstadosSpinnerAdapter;
import com.appetesg.estusolucionTranscarga.db.Db;
import com.appetesg.estusolucionTranscarga.modelos.GuiasD;
import com.appetesg.estusolucionTranscarga.modelos.Estado;
import com.appetesg.estusolucionTranscarga.utilidades.DrawingView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class EstadoGuiaActivity extends AppCompatActivity {
    DrawingView mDrawingView;
    Context context;
    ImageView imgLogo, imgAlfa;
    Bitmap bmp1;
    private int size = 90;
    private int size_width = 420;
    private int size_height = 70;
    private static final int REQUEST_ENABLE_BT = 1;
    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    Db usdbh;
    SQLiteDatabase db;
    public AlertDialog alert;
    private UUID myUUID;

    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    ListView listViewPairedDevice;
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/DatosGuia";
    private static final String METHOD_NAME = "DatosGuia";
    private static final String METHOD_NAME_ESTADOS = "Estados";
    private static final String METHOD_NAME_ESTADOS_GUIA = "ActualizarEstado";
    private static final String NAMESPACE = "http://tempuri.org/";
    ArrayList<GuiasD> listaGuias = new ArrayList<>();
    ArrayList<Estado> estados = new ArrayList<>();
    GuiasD dtGuia;
    static String TAG="EstadoGuiaActivity";
    String BASE_URL,PREFS_NAME;
    TextView txtRemitente, txtDestinatario, txtPeso, txtCelular, txtValor, txtTipoEnvio,
            txtDireccionR, txtDireccionD, txtProducto, txtTtitle, txtDescripcion,
            txtPrueba;
    EditText txtUsuario;
    Spinner spEstadosGuia;
    EstadosSpinnerAdapter mAdapterEstados;
    Button btnEnviar;
    int idUsuario, intPantalla = 0;
    CircleImageView imageCall, imageMessage, imageMap;
    String edo="0";
    LinearLayout linearFirma;
    ImageButton imbLimpiarFirma;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(EstadoGuiaActivity.this, ListaGuiasActivity.class);
        if(intPantalla == 1)
            intent = new Intent(EstadoGuiaActivity.this, ListaCumplidosActivity.class);
        startActivity(intent);
        finish();
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
        setContentView(R.layout.activity_estado_guia);
        PREFS_NAME = this.getString(R.string.SPREF);
        usdbh = new Db(EstadoGuiaActivity.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        intPantalla = sharedPreferences.getInt("pantalla",0);

        txtRemitente = (TextView) findViewById(R.id.txtRemitenteG);
        txtDestinatario = (TextView)findViewById(R.id.txtDestinararioG);
        txtPeso = (TextView)findViewById(R.id.txtPesoP);
        txtCelular = (TextView)findViewById(R.id.txtCelularD);
        txtValor = (TextView)findViewById(R.id.txtValorP);
        txtTipoEnvio = (TextView)findViewById(R.id.textView8); // campo tipo de envio
        txtDireccionR = (TextView)findViewById(R.id.txtDireccionRe);
        txtDireccionD = (TextView)findViewById(R.id.txtDireccionDesti);
        txtProducto = (TextView)findViewById(R.id.txtproductoG);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcionPro);
        final String strPedido1 = sharedPreferences.getString("strGuia", "");
        //btnRegreso = (FloatingActionButton)findViewById(R.id.btnRegreso);
        spEstadosGuia = (Spinner) findViewById(R.id.sprEstadosGuia);
        txtTtitle = (TextView) findViewById(R.id.titlleGuia);
        btnEnviar = (Button) findViewById(R.id.btnEnviarGuia);
        imageCall = (CircleImageView)findViewById(R.id.circleCallD);
        imageMessage = (CircleImageView)findViewById(R.id.circleMesageD);
        imageMap = (CircleImageView)findViewById(R.id.circleMapDest);
        txtUsuario = (EditText) findViewById(R.id.yourName);
        //txtPrueba = (TextView) findViewById(R.id.txtNumeroPruebas);
        //linearFirma = (LinearLayout)findViewById(R.id.linearFirmaGuiaDestino);
        //imbLimpiarFirma = (ImageButton)findViewById(R.id.imbLimpiarFirmaGuia);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        imgLogo = (ImageView) findViewById(R.id.imageprueba);
        imgAlfa = (ImageView) findViewById(R.id.imagenAlfanumericoC);

        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelular.getText().toString() != "0") {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtCelular.getText()));

                    if (ActivityCompat.checkSelfPermission(EstadoGuiaActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(EstadoGuiaActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // Se tiene permiso
                            } else {
                                ActivityCompat.requestPermissions(EstadoGuiaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                                return;
                            }
                        }
                    }
                    startActivity(intent);
                }
            }
        });

        imageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelular.getText().toString() != "0") {
                    sendMessageToWhatsAppContact(txtCelular.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No puedes enviar un mensaje ya que no contiene un numero de celular",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDireccionD.getText().toString() != "") {
                    navigateExternalTo(txtDireccionD.getText().toString());
                    // DIRECCION: CALLE 72 #65-30
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No puedes enviar ver la ubicacion direccion invalida",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCelular.getText().toString() != "0") {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtCelular.getText()));


                    if (ActivityCompat.checkSelfPermission(EstadoGuiaActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(EstadoGuiaActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // Se tiene permiso
                            } else {
                                ActivityCompat.requestPermissions(EstadoGuiaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                                return;
                            }
                        }
                    }
                    startActivity(intent);
                }
            }
        });


        mDrawingView = new DrawingView(this);
        /*linearFirma.addView(mDrawingView);

        imbLimpiarFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearFirma.removeAllViews();
                mDrawingView=new DrawingView(EstadoGuiaActivity.this);
                linearFirma.addView(mDrawingView);
            }
        });*/


        new ListaDatoGuiaAsyncTask(strPedido1).execute();

        new ListarEstadosAsyncTask().execute();


        spEstadosGuia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = spEstadosGuia.getItemAtPosition(position);
                Estado estado = (Estado)o;
                edo = estado.getIdEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idUsuario = sharedPreferences.getInt("idUsuario",0);

                String strFoto,strFirma;
                Bitmap bmpFoto,bmpFirma;
                //Nueva solicitud de servicios
                /*linearFirma.setDrawingCacheEnabled(true);
                linearFirma.buildDrawingCache();
                try{
                    bmpFirma = linearFirma.getDrawingCache();
                    strFirma = convertirBitmapBase64(bmpFirma);
                }catch (Exception ex){
                    strFoto = "";
                    strFirma = "";
                }*/
                Calendar c1 = Calendar.getInstance();
                Date f = null;
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                c1.add(Calendar.DATE,1);
                f = c1.getTime();
                String strFecha = formatter.format(f);
                String strLatitud = "0";
                String strLongitud = "0";
                btnEnviar.setEnabled(false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("NroGuia", strPedido1);
                editor.putString("strFechaEstado", strFecha);
                editor.putInt("intEstado", Integer.parseInt(edo));
                editor.putString("strRecibe", txtUsuario.getText().toString());
                editor.commit();

                Intent intent = new Intent(EstadoGuiaActivity.this, firmaImagen.class);
                startActivity(intent);
                finish();
                //imprimir(R.drawable.ic_launcher);
                //imprimirEtiqueta2(R.drawable.ic_launcher);
                //new SendEstadosyncTask(idUsuario, strPedido1, strFecha, Integer.parseInt(edo), strLatitud, strLongitud, strFirma, txtUsuario.getText().toString() ).execute();

                //Toast.makeText(getApplicationContext(), "En construccion", Toast.LENGTH_SHORT).show();
                //new SendEstadosyncTask(strCodido, idUsuario, Integer.parseInt(edo), txtObs.getText().toString()).execute();
            }
        });

    }

    private void sendMessageToWhatsAppContact(String number) {
        //PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {

            String strMensaje = "Buen dia, soy el operador de transporte asignado.";
            //EL indicadtivo del pais se cambia segun su numero en esta caso panama es el 507 -- 20201106 LEGA
            String url = "https://api.whatsapp.com/send?phone=" + "57"+number + "&text=" + strMensaje;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateExternalTo(String busqueda) {
        // Search for busqueda nearby
        try{

            //Uri gmmIntentUri = Uri.parse("google.navigation:q="+busqueda);
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+busqueda);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ListaDatoGuiaAsyncTask extends AsyncTask<Integer, Integer, ArrayList<GuiasD>> {

        String strPedido1;

        public ListaDatoGuiaAsyncTask(String strPedido1) {
            this.strPedido1 = strPedido1;
        }

        @Override
        protected void onPostExecute(ArrayList<GuiasD> s) {
            super.onPostExecute(s);
            txtTtitle.setText(txtTtitle.getText() + " " + dtGuia.getStrGuia());
            try {

                bmp1 =  CreateImage(dtGuia.getStrGuia().toString(), "Barcode");
                //Toast.makeText(EstadoGuiaActivity.this," barcode generado "+dtGuia.getStrGuia().toString(),Toast.LENGTH_LONG).show();
            } catch (WriterException e) {
                Toast.makeText(EstadoGuiaActivity.this,"no pudo generar el barcode",Toast.LENGTH_LONG).show();
            }
            txtRemitente.setText(s.get(0).getRemitente());
            txtDestinatario.setText(dtGuia.getStrDestinatario());

            if(!dtGuia.getStrNomForPag().isEmpty())
            {
                txtTipoEnvio.setText(dtGuia.getStrNomForPag());
            }

            if(txtDireccionD.equals(""))
            {
                imageMap.setVisibility(View.GONE);
            }

            if(!dtGuia.getStrCelular().equals("anyType{}"))
            {
                txtCelular.setText(dtGuia.getStrCelular());
            }
            else
            {
                imageCall.setClickable(false);
                imageMessage.setClickable(false);
                imageCall.setVisibility(View.GONE);
                imageMessage.setVisibility(View.GONE);
                txtCelular.setText("No disponible");
            }

            txtPeso.setText(dtGuia.getStrPeso());
            txtValor.setText("$"+dtGuia.getStrValor());
            txtDireccionR.setText(s.get(0).getDircli());
            txtDireccionD.setText(dtGuia.getStrDireccionDe());
            txtProducto.setText(s.get(0).getStrProducto());
            txtDescripcion.setText(dtGuia.getStrDescripcionP());
        }

        @Override
        protected ArrayList<GuiasD> doInBackground(Integer... integers) {

            String strGuia,strRemitente, strDestinatario, strCelular, strPeso, strValor, strDireccionRe, strDireccionDe,strProducto, strDescripcionP, strNomForPag;

            dtGuia = new GuiasD(sharedPreferences.getString("PEDIDO1",""),sharedPreferences.getString("DESCON",""),
                    sharedPreferences.getString("DESTINATARIO",""), sharedPreferences.getString("VALPAG",""),
                    sharedPreferences.getString("PESPAQ",""),sharedPreferences.getString("DIRDES",""),
                    sharedPreferences.getString("REMITENTE",""), sharedPreferences.getString("TELDES",""),
                    sharedPreferences.getString("NOMPRD",""),
                    sharedPreferences.getString("DIRCLI",""),sharedPreferences.getString("NOMFORPAG",""));

            strGuia = sharedPreferences.getString("PEDIDO1","");
            strRemitente = sharedPreferences.getString("REMITENTE","");
            strDestinatario = sharedPreferences.getString("DESTINATARIO","");
            strCelular =  sharedPreferences.getString("TELDES","");
            strPeso = sharedPreferences.getString("PESPAQ","");
            strValor = sharedPreferences.getString("VALPAG","");
            strDireccionRe = sharedPreferences.getString("DIRCLI","");
            strDireccionDe = sharedPreferences.getString("DIRDES","");
            strProducto= sharedPreferences.getString("NOMPRD","");
            strDescripcionP= sharedPreferences.getString("DESCON","");
            strNomForPag= sharedPreferences.getString("NOMFORPAG","");

            listaGuias.add(new GuiasD(strGuia,
                    strDescripcionP, strDestinatario, strValor, strPeso, strDireccionDe,
                    strRemitente, strCelular, strDireccionRe, strProducto,strNomForPag));

            return listaGuias;

        }
    }


    public class ListarEstadosAsyncTask extends AsyncTask<Integer,Integer,ArrayList<Estado>> {


        public ListarEstadosAsyncTask() {
        }

        @Override
        protected void onPostExecute(ArrayList<Estado> s) {
            super.onPostExecute(s);
            mAdapterEstados = new EstadosSpinnerAdapter(EstadoGuiaActivity.this,s);
            spEstadosGuia.setAdapter(mAdapterEstados);
        }

        @Override
        protected ArrayList<Estado> doInBackground(Integer... integers) {

            try
            {
                db = usdbh.getWritableDatabase();
                if (db != null) {

                    Cursor c = db.rawQuery("select * from estados  ", null);

                    if (c.getCount() > 0) {

                        System.out.println("si hay registros");

                        if (c.moveToFirst()) {
                            do {

                                if(intPantalla == 1){
                                    if(c.getString(0).equalsIgnoreCase("600"))
                                        estados.add(new Estado(c.getString(0),c.getString(1)));
                                }else
                                    estados.add(new Estado(c.getString(0),c.getString(1)));

                            } while (c.moveToNext());

                        }
                    } else {

                        System.out.println("NO HAY ESTADOS");

                    }

                    if(c!=null){
                        c.close();
                        db.close();
                    }

                }

            }catch (SQLException e)
            {

            }

            return estados;

        }
    }


    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public class SendEstadosyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strPedido, strLatitud, strLongitud, strRecibido, strImagen, strFecha;
        int idUsuario, intEstado;

        public SendEstadosyncTask(int idUsuario,String strPedido, String strFecha, int intEstado, String strLatitud, String strLongitud, String strImagen, String strReibido)
        {
            this.idUsuario = idUsuario;
            this.strPedido = strPedido;
            this.strFecha = strFecha;
            this.intEstado = intEstado;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.strImagen = strImagen;
            this.strRecibido = strReibido;
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Su proceso fue exitoso", Toast.LENGTH_LONG).show();

                /*UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();*/

                //manda a imprimir
                //imprimir();
                //imprimirEmbarque();
                //Intent intent = new Intent(EstadoGuiaActivity.this, Menuotros.class);
                //startActivity(intent);



            }
            else
            {
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_ESTADOS_GUIA);

            request.addProperty("IdUsuario", idUsuario);
            request.addProperty("DocumentoReferencia",strPedido);
            request.addProperty("Fecha", strFecha);
            request.addProperty("Estado", intEstado);
            request.addProperty("Latitud", strLatitud);
            request.addProperty("Longitud", strLongitud);
            request.addProperty("Imagen", strImagen);
            request.addProperty("srtRecibido", strRecibido);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME_ESTADOS_GUIA, envelope);
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


    @Override
    protected void onStart() {
       //
            super.onStart();
            myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
/*
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(this,
                        "Bluetooth NO SOPORTADO",
                        Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String stInfo = bluetoothAdapter.getName() + "\n" +
                    bluetoothAdapter.getAddress();

            //Turn ON BlueTooth if it is OFF
            if (!bluetoothAdapter.isEnabled()) {
                //descomentar
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }

            lanza_alert();

            setup();*/

    }
    public void lanza_alert()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(EstadoGuiaActivity.this);

        View  promptView = layoutInflater.inflate(R.layout.layout_dispositivos, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EstadoGuiaActivity.this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        alertDialogBuilder.setView(promptView);
        alert = alertDialogBuilder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);

        listViewPairedDevice = (ListView) promptView.findViewById(R.id.list_dispositivos);

    }
/*
    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }



            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);



                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();

                    listViewPairedDevice.setVisibility(View.GONE);
                }
            });
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }



    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alert.cancel();
                        Toast.makeText(EstadoGuiaActivity.this,"something wrong bluetoothSocket.connect()",Toast.LENGTH_LONG).show();

                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //  textStatus.setText("");
                        //  textByteCnt.setText("");
                        //     Toast.makeText(MainActivity.this, msgconnected, Toast.LENGTH_LONG).show();

                        //   listViewPairedDevice.setVisibility(View.GONE);
                        // list_productos.setVisibility(View.VISIBLE);

                        //inputPane.setVisibility(View.VISIBLE);
                        alert.cancel();
                    }
                });

                startThreadConnected(bluetoothSocket);

            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            String strRx = "";


            // adapter.edtpeso.setText("");



            while (true) //// aqui inicia el bucle
            {
                try {
                    bytes = connectedInputStream.read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);
                    final String strByteCnt = String.valueOf(bytes)  ;

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {

                        }});

                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Toast.makeText(EstadoGuiaActivity.this,"Se perdio la conexión",Toast.LENGTH_LONG).show();
                        }});
                }
            }

            /// aqui termina el bucle


        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;

        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }


    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public void imprimir(int img)
    {

        //aqui esta las opciones o instrucciones estas se ejecutan antes
        //para dar la orden del que texto a imprimir debe aplicarlesele ese formato
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x39};
        byte[] bb5 = new byte[]{0x1B,0x21,0x29};
        byte[] bb6 = new byte[]{0x1B,0x21,0x09};
        byte[] bb7 = new byte[]{0x1B,0x21,0x45};

        myThreadConnected.write(bb4);

        try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 150, 150, false);
                if(bmp!=null)
                {
                    System.out.println("fallo1");
                    byte[] command0 = decodeBitmap(bmp);
                    myThreadConnected.write(command0);
                    /*byte[] NewLine177 = "\n".getBytes();
                    myThreadConnected.write(NewLine177);
*/

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
            }



        myThreadConnected.write(bb4); //aqui se le aplicado texto bold wth medium text y luego imprime

        byte[] byteToTitle1 = ("Transcarga Mundial").toString().getBytes();
        myThreadConnected.write(byteToTitle1);
        byte[] NewToTitle1 = "\n".getBytes();
        myThreadConnected.write(NewToTitle1);


        //el texto siguiente le dices lo quiero normal le das intruccion y asi lo imprimira listo.
        myThreadConnected.write(cc);


        if(myThreadConnected!=null){

           try {
                                /*bmp = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.ic_launcher); //antes era municipio*/

               if(bmp1!=null)
             {


                    byte[] command10 = decodeBitmap(bmp1);
                    myThreadConnected.write(command10);
                    /*byte[] NewLine178 = "\n".getBytes();
                    myThreadConnected.write(NewLine178);
*/

                }else{

                    Toast.makeText(EstadoGuiaActivity.this,"no puede imprimir barcode",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
               e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
           }
            myThreadConnected.write(bb5);
            byte[] bytesTodDestCiud = ("Destino: Medellin").toString().getBytes();
            myThreadConnected.write(bytesTodDestCiud);
            byte[] NewLineDestCiud  = "\n".getBytes();
            myThreadConnected.write(NewLineDestCiud);


            myThreadConnected.write(cc);
            String[] strGuia = txtTtitle.getText().toString().split(":");
            byte[] bytesToSend2 = ("Ref No: "+strGuia[1]).toString().getBytes();
            myThreadConnected.write(bytesToSend2);
            byte[] NewLine2 = "\n".getBytes();
            myThreadConnected.write(NewLine2);


            myThreadConnected.write(cc);
            byte[] bytesTitleRemi = ("Remitente").toString().getBytes();
            myThreadConnected.write(bytesTitleRemi);
            byte[] NewLineRemi1 = "\n".getBytes();
            myThreadConnected.write(NewLineRemi1);


            byte[] bytesToRemi = (txtRemitente.getText()).toString().getBytes();
            myThreadConnected.write(bytesToRemi);
            byte[] NewLineRemi = "\n".getBytes();
            myThreadConnected.write(NewLineRemi);


            byte[] bytesToRemiDir = ("Dir: Kr 88 bis 40 b 22 Sur").toString().getBytes();
            myThreadConnected.write(bytesToRemiDir);
            byte[] NewLineRemiDir = "\n".getBytes();
            myThreadConnected.write(NewLineRemiDir);

            byte[] bytesToRemiTel = ("Cel: 3045370193").toString().getBytes();
            myThreadConnected.write(bytesToRemiTel);
            byte[] NewLineRemiTel = "\n".getBytes();
            myThreadConnected.write(NewLineRemiTel);

            myThreadConnected.write(bb5);
            byte[] bytesToRemiCiud = ("Origen: Bogota").toString().getBytes();
            myThreadConnected.write(bytesToRemiCiud);
            byte[] NewLineRemiCiud = "\n\n".getBytes();
            myThreadConnected.write(NewLineRemiCiud);

            /*byte[] byteEspacio1 = "--------------------------------".getBytes();
            myThreadConnected.write(byteEspacio1);
            byte[] NewEspacio1= "\n".getBytes();
            myThreadConnected.write(NewEspacio1);
*/
            myThreadConnected.write(bb5);
            byte[] bytesToSend = ("Destinatario").toString().getBytes();
            myThreadConnected.write(bytesToSend);
            byte[] NewLine = "\n".getBytes();
            myThreadConnected.write(NewLine);


            myThreadConnected.write(cc);
            byte[] bytesToDest = (txtDestinatario.getText()).toString().getBytes();
            myThreadConnected.write(bytesToDest);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);


            byte[] bytesToDestDir = ("Dir: Calle 59 # 37 -86").toString().getBytes();
            myThreadConnected.write(bytesToDestDir);
            byte[] NewLineDestiDir = "\n".getBytes();
            myThreadConnected.write(NewLineDestiDir);

            byte[] bytesToDestProd = ("Pro: URBANO").toString().getBytes();
            myThreadConnected.write(bytesToDestProd);
            byte[] NewLineDestProd = "\n".getBytes();
            myThreadConnected.write(NewLineDestProd);


            //manda a imprimir segunda


            byte[] bytesTodDestPeso = ("Peso: 30.7").toString().getBytes();
            myThreadConnected.write(bytesTodDestPeso);
            byte[] NewLineDestPeso  = "\n".getBytes();
            myThreadConnected.write(NewLineDestPeso);

            byte[] bytesTodDestValDec = ("Valor Dec: $10.000.00").toString().getBytes();
            myThreadConnected.write(bytesTodDestValDec);
            byte[] NewLineDestValDec  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValDec);

            byte[] bytesTodPiezas = ("PIEZAS 1/3").toString().getBytes();
            myThreadConnected.write(bytesTodPiezas);
            byte[] NewLinePiezas = "\n".getBytes();
            myThreadConnected.write(NewLinePiezas);

            myThreadConnected.write(bb7);
            byte[] bytesLink = ("transcargamundial.com/terminos-y-condiciones").toString().getBytes();
            myThreadConnected.write(bytesLink);
            byte[] NewLineLink  = "\n".getBytes();
            myThreadConnected.write(NewLineLink);

            /*byte[] bytesToSend1 = "Quien Recibe:".getBytes();
            myThreadConnected.write(bytesToSend1);
            byte[] NewLine1 = "\n".getBytes();
            myThreadConnected.write(NewLine1);*/




  /*          byte[] byteToContacto = "Contacto: +57 1 6 533 640".getBytes();
            myThreadConnected.write(byteToContacto);
            byte[] NewToContacto = "\n".getBytes();
            myThreadConnected.write(NewToContacto);
*/
            /*byte[] byteToDireccion = "Carrera 40 N 10a 91, Bogota, Colombia".getBytes();
            myThreadConnected.write(byteToDireccion);
            byte[] NewToAdress= "\n".getBytes();
            myThreadConnected.write(NewToAdress);*/


  /*          byte[] NewToDivisora = "\n".getBytes();
            myThreadConnected.write(NewToDivisora);

            byte[] NewToDivisora2 = "\n".getBytes();
            myThreadConnected.write(NewToDivisora2);
*/

            //salimos  del activity
            btnEnviar.setEnabled(true);
            Intent intent = new Intent(EstadoGuiaActivity.this, Menuotros.class);
            startActivity(intent);
            finish();

        }


    }

    public void imprimirEmbarque(int img)
    {

        //aqui esta las opciones o instrucciones estas se ejecutan antes
        //para dar la orden del que texto a imprimir debe aplicarlesele ese formato
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x39};
        byte[] bb5 = new byte[]{0x1B,0x21,0x29};
        byte[] bb6 = new byte[]{0x1B,0x21,0x09};
        byte[] bb7 = new byte[]{0x1B,0x21,0x45};

        myThreadConnected.write(bb4);

       /*try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 150, 150, false);
            if(bmp!=null)
            {
                System.out.println("fallo1");
                byte[] command0 = decodeBitmap(bmp);
                myThreadConnected.write(command0);
                    //byte[] NewLine177 = "\n".getBytes();
                    //myThreadConnected.write(NewLine177);


            }else{

                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();


            Log.e("PrintTools", "the file isn't exists");
        }
*/

        byte[] byteToEmbarque = ("NRO: 1").toString().getBytes();
        myThreadConnected.write(byteToEmbarque);
        byte[] NewToEmbarque = "\n".getBytes();
        myThreadConnected.write(NewToEmbarque);

        myThreadConnected.write(bb4); //aqui se le aplicado texto bold wth medium text y luego imprime

        byte[] byteToTitle1 = ("Transcarga").toString().getBytes();
        myThreadConnected.write(byteToTitle1);
        byte[] NewToTitle1 = "\n".getBytes();
        myThreadConnected.write(NewToTitle1);

        byte[] byteToTitle2 = ("Mundial S.A.S").toString().getBytes();
        myThreadConnected.write(byteToTitle2);
        byte[] NewToTitle2 = "\n".getBytes();
        myThreadConnected.write(NewToTitle2);


        //el texto siguiente le dices lo quiero normal le das intruccion y asi lo imprimira listo.
        myThreadConnected.write(cc);


        if(myThreadConnected!=null){

            try {
                                /*bmp = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.ic_launcher); //antes era municipio*/

                if(bmp1!=null)
                {


                    byte[] command10 = decodeBitmap(bmp1);
                    myThreadConnected.write(command10);
                    /*byte[] NewLine178 = "\n".getBytes();
                    myThreadConnected.write(NewLine178);
*/

                }else{

                    Toast.makeText(EstadoGuiaActivity.this,"no puede imprimir barcode",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
            }
            myThreadConnected.write(bb4);
            byte[] bytesTodDestCiud = ("Medellin - CONTRAENTREGA").toString().getBytes();
            myThreadConnected.write(bytesTodDestCiud);
            byte[] NewLineDestCiud  = "\n".getBytes();
            myThreadConnected.write(NewLineDestCiud);


            myThreadConnected.write(cc);
            String[] strGuia = txtTtitle.getText().toString().split(":");
            byte[] bytesToSend2 = ("Ref No: "+strGuia[1]).toString().getBytes();
            myThreadConnected.write(bytesToSend2);
            byte[] NewLine2 = "\n".getBytes();
            myThreadConnected.write(NewLine2);


            myThreadConnected.write(cc);
            byte[] bytesTitleRemi = ("Remitente").toString().getBytes();
            myThreadConnected.write(bytesTitleRemi);
            byte[] NewLineRemi1 = "\n".getBytes();
            myThreadConnected.write(NewLineRemi1);


            byte[] bytesToRemi = (txtRemitente.getText()).toString().getBytes();
            myThreadConnected.write(bytesToRemi);
            byte[] NewLineRemi = "\n".getBytes();
            myThreadConnected.write(NewLineRemi);

            byte[] bytesToRemiDir = ("Dir: Kr 88 bis 40 b 22 Sur").toString().getBytes();
            myThreadConnected.write(bytesToRemiDir);
            byte[] NewLineRemiDir = "\n".getBytes();
            myThreadConnected.write(NewLineRemiDir);

            byte[] bytesToRemiTel = ("Cel: 3045370193").toString().getBytes();
            myThreadConnected.write(bytesToRemiTel);
            byte[] NewLineRemiTel = "\n".getBytes();
            myThreadConnected.write(NewLineRemiTel);

            myThreadConnected.write(cc);
            byte[] bytesToRemiCiud = ("Origen: Bogota").toString().getBytes();
            myThreadConnected.write(bytesToRemiCiud);
            byte[] NewLineRemiCiud = "\n\n".getBytes();
            myThreadConnected.write(NewLineRemiCiud);

            /*byte[] byteEspacio1 = "--------------------------------".getBytes();
            myThreadConnected.write(byteEspacio1);
            byte[] NewEspacio1= "\n".getBytes();
            myThreadConnected.write(NewEspacio1);
*/
            myThreadConnected.write(bb5);
            byte[] bytesToSend = ("Destinatario").toString().getBytes();
            myThreadConnected.write(bytesToSend);
            byte[] NewLine = "\n".getBytes();
            myThreadConnected.write(NewLine);


            myThreadConnected.write(cc);
            byte[] bytesToDest = (txtDestinatario.getText()).toString().getBytes();
            myThreadConnected.write(bytesToDest);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);


            byte[] bytesToDestDir = ("Dir: Calle 59 # 37 -86").toString().getBytes();
            myThreadConnected.write(bytesToDestDir);
            byte[] NewLineDestiDir = "\n".getBytes();
            myThreadConnected.write(NewLineDestiDir);

            byte[] bytesToDestProd = ("Pro: URBANO").toString().getBytes();
            myThreadConnected.write(bytesToDestProd);
            byte[] NewLineDestProd = "\n".getBytes();
            myThreadConnected.write(NewLineDestProd);


            //manda a imprimir segunda


            byte[] bytesTodDestPeso = ("Peso: 30.7").toString().getBytes();
            myThreadConnected.write(bytesTodDestPeso);
            byte[] NewLineDestPeso  = "\n".getBytes();
            myThreadConnected.write(NewLineDestPeso);

            byte[] bytesTodDestValDec = ("Valor Dec: $10.000.00").toString().getBytes();
            myThreadConnected.write(bytesTodDestValDec);
            byte[] NewLineDestValDec  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValDec);

            byte[] bytesTodPiezas= ("PIEZAS 1/2").toString().getBytes();
            myThreadConnected.write(bytesTodDestValDec);
            byte[] bytesTodLinePiezas  = "\n".getBytes();
            myThreadConnected.write(bytesTodLinePiezas);

            myThreadConnected.write(bb7);
            byte[] bytesLink = ("transcargamundial.com/terminos-y-condiciones").toString().getBytes();
            myThreadConnected.write(bytesLink);
            byte[] NewLineLink  = "\n".getBytes();
            myThreadConnected.write(NewLineLink);

            /*byte[] bytesToSend1 = "Quien Recibe:".getBytes();
            myThreadConnected.write(bytesToSend1);
            byte[] NewLine1 = "\n".getBytes();
            myThreadConnected.write(NewLine1);*/




  /*          byte[] byteToContacto = "Contacto: +57 1 6 533 640".getBytes();
            myThreadConnected.write(byteToContacto);
            byte[] NewToContacto = "\n".getBytes();
            myThreadConnected.write(NewToContacto);
*/
            /*byte[] byteToDireccion = "Carrera 40 N 10a 91, Bogota, Colombia".getBytes();
            myThreadConnected.write(byteToDireccion);
            byte[] NewToAdress= "\n".getBytes();
            myThreadConnected.write(NewToAdress);*/


  /*          byte[] NewToDivisora = "\n".getBytes();
            myThreadConnected.write(NewToDivisora);

            byte[] NewToDivisora2 = "\n".getBytes();
            myThreadConnected.write(NewToDivisora2);
*/

            //salimos  del activity
            btnEnviar.setEnabled(true);
            Intent intent = new Intent(EstadoGuiaActivity.this, Menuotros.class);
            startActivity(intent);
            finish();

        }


    }


    public void imprimirEtiqueta2(int img)
    {

        //aqui esta las opciones o instrucciones estas se ejecutan antes
        //para dar la orden del que texto a imprimir debe aplicarlesele ese formato
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x39};
        byte[] bb5 = new byte[]{0x1B,0x21,0x29};
        byte[] bb6 = new byte[]{0x1B,0x21,0x09};
        byte[] bb7 = new byte[]{0x1B,0x21,0x45};
        byte[] bb8 = new byte[]{0x1B,0x21,0x70};
        byte[] bb9 = new byte[]{0x1B, 0x4A, 127};

        myThreadConnected.write(bb4);

       try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 390, 70, false);
            if(bmp!=null)
            {
                System.out.println("fallo1");
                byte[] command0 = decodeBitmap(bmp);
                myThreadConnected.write(command0);
                    //byte[] NewLine177 = "\n".getBytes();
                    //myThreadConnected.write(NewLine177);


            }else{

                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();


            Log.e("PrintTools", "the file isn't exists");
        }


        myThreadConnected.write(bb7);

        byte[] byteToEmbarque = ("WhatsApp: 3156770401 - 3155453978").toString().getBytes();
        myThreadConnected.write(byteToEmbarque);
        byte[] NewToEmbarque = "\n".getBytes();
        myThreadConnected.write(NewToEmbarque);

        /*byte[] byteToEmbarque = ("NRO: 1").toString().getBytes();
        myThreadConnected.write(byteToEmbarque);
        byte[] NewToEmbarque = "\n".getBytes();
        myThreadConnected.write(NewToEmbarque);*/

       // myThreadConnected.write(bb4); //aqui se le aplicado texto bold wth medium text y luego imprime

        /*byte[] byteToTitle1 = ("Transcarga").toString().getBytes();
        myThreadConnected.write(byteToTitle1);
        byte[] NewToTitle1 = "\n".getBytes();
        myThreadConnected.write(NewToTitle1);

        byte[] byteToTitle2 = ("Mundial S.A.S").toString().getBytes();
        myThreadConnected.write(byteToTitle2);
        byte[] NewToTitle2 = "\n".getBytes();
        myThreadConnected.write(NewToTitle2);*/


        //el texto siguiente le dices lo quiero normal le das intruccion y asi lo imprimira listo.
        myThreadConnected.write(cc);


        if(myThreadConnected!=null){
            try {
                                /*bmp = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.ic_launcher); //antes era municipio*/

                if(bmp1!=null)
                {


                    byte[] command10 = decodeBitmap(bmp1);
                    myThreadConnected.write(command10);
                    /*byte[] NewLine178 = "\n".getBytes();
                    myThreadConnected.write(NewLine178);
*/

                }else{

                    Toast.makeText(EstadoGuiaActivity.this,"no puede imprimir barcode",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
            }
            myThreadConnected.write(bb4);

            String[] strGuia = txtTtitle.getText().toString().split(":");
            byte[] bytesToSend2 = ("Ref No: "+strGuia[1]).toString().getBytes();
            myThreadConnected.write(bytesToSend2);
            byte[] NewLine2 = "\n".getBytes();
            myThreadConnected.write(NewLine2);

            myThreadConnected.write(bb8);
            byte[] bytesTodForm = ("  CONTRAENTREGA").toString().getBytes();
            myThreadConnected.write(bytesTodForm);
            byte[] NewLineForm = "\n".getBytes();
            myThreadConnected.write(NewLineForm);

            myThreadConnected.write(bb4);
            byte[] bytesTodDes = ("DESTINO").toString().getBytes();
            myThreadConnected.write(bytesTodDes);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesTodCiudDes = ("MEDELLIN").toString().getBytes();
            myThreadConnected.write(bytesTodCiudDes);
            byte[] NewLineCiudDest = "\n\n".getBytes();
            myThreadConnected.write(NewLineCiudDest);

            byte[] bytesTodOrigen = ("ORIGEN").toString().getBytes();
            myThreadConnected.write(bytesTodOrigen);
            byte[] NewLineOrigen = "\n".getBytes();
            myThreadConnected.write(NewLineOrigen);

            byte[] bytesTodCiudOri = ("BOGOTA").toString().getBytes();
            myThreadConnected.write(bytesTodCiudOri);
            byte[] NewLineCiudOri = "\n".getBytes();
            myThreadConnected.write(NewLineCiudOri);


            byte[] bytesTodPiezas= ("Piezas 1/45").toString().getBytes();
            myThreadConnected.write(bytesTodPiezas);
            byte[] NewLinePiezas = "\n".getBytes();
            myThreadConnected.write(NewLinePiezas);

            myThreadConnected.write(bb7);
            byte[] bytesLink = ("transcargamundial.com/terminosycondiciones").toString().getBytes();
            myThreadConnected.write(bytesLink);
            byte[] NewLineLink  = "\n".getBytes();
            myThreadConnected.write(NewLineLink);


            myThreadConnected.write(bb4);

            try {

                Bitmap bmpPrueba = Bitmap.createScaledBitmap(((BitmapDrawable) imgAlfa.getDrawable()).getBitmap(), 390, 70, false);
                if(bmpPrueba!=null)
                {
                    byte[] command0 = decodeBitmap(bmpPrueba);
                    myThreadConnected.write(command0);
                    /*byte[] NewLine177 = "\n".getBytes();
                    myThreadConnected.write(NewLine177);
*/

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
            }


            //salimos  del activity
            btnEnviar.setEnabled(true);
            Intent intent = new Intent(EstadoGuiaActivity.this, Menuotros.class);
            startActivity(intent);
            finish();

        }


    }



    public Bitmap CreateImage(String message, String type) throws WriterException
    {
        BitMatrix bitMatrix = null;
        // BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
        switch (type)
        {
            case "QR Code": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);break;
            case "Barcode": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, size_width, size_height);break;
            case "Data Matrix": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.DATA_MATRIX, size, size);break;
            case "PDF 417": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.PDF_417, size_width, size_height);break;
            case "Barcode-39":bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_39, size_width, size_height);break;
            case "Barcode-93":bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_93, size_width, size_height);break;
            case "AZTEC": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.AZTEC, size, size);break;
            default: bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);break;
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int [] pixels = new int [width * height];
        for (int i = 0 ; i < height ; i++)
        {
            for (int j = 0 ; j < width ; j++)
            {
                if (bitMatrix.get(j, i))
                {
                    pixels[i * width + j] = 0xff000000;
                }
                else
                {
                    pixels[i * width + j] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
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
