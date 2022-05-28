    package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.modelos.DatosUsuario;
import com.appetesg.estusolucionTranscarga.modelos.FormaDePagoList;
import com.appetesg.estusolucionTranscarga.adapter.FormaDePagoAdapter;
import com.appetesg.estusolucionTranscarga.modelos.DatosDestinatarioSeleccionado;
import com.appetesg.estusolucionTranscarga.utilidades.DrawingView;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.bumptech.glide.RequestManager;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import android.widget.ProgressBar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class GeneracionGuia extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    Bitmap bmp1;
    Bitmap imagen,myBitmap;
    FormaDePagoAdapter mListaPagos;
    LinearLayout lytdesing;
    byte[] command10;
    TextView txtImpresionCiudad, txtImpresionPedido, txtImpresionPago;
    AlertDialog dialog;
    ImageView imgLogo, imgAlfa,imgfondo, imgImpresionQR;
    private Uri selectedUri;
    Button btnimprimir;
    private RequestManager requestManager;
    DrawingView mDrawingView;
    ImageView imgEvidencia, imgEvidenciaE;
    FloatingActionButton btnCamara;
    EditText edContenido, edObservaciones, edNoPedido;
    CheckBox checkFacturaE;
    Button btnFinalizar;
    ImageButton imgButtonRe;
    ProgressDialog p;
    String strVacio = null;
    boolean conectado = false, blClienteCorp = false, v = true, blClienteCon = false, entro = false;
    SharedPreferences sharedPreferences;
    String strCiudadGuia, strProductoGuia, strEnvio,
            strPesoGuia, strCantidadGuia, strValorGuia, strCompanhiaCli, strNomCli, strApeCli, strCoreCli, strDicli, strCelCli,
            strNombreDest, strDirecionDest, strTelefonoDest, strDocumentoDest, strDocumentoCli, strNomFoma, strNomCiuDes,strValorTotal, strValorConSigno,
            strValorSeguro, strBonoDescuento, strCiudadOrigen, strValorFlete, strValorTotalSin, strApellidoDest, strCompaniaDest;
    String BASE_URL,PREFS_NAME;
    Spinner spForma;
    int intCodCli = 0, intCodest = 0, intIdUsuario = 0, intIdproducto = 0, intIdEnvio = 0, intForma = 0;
    String intPorcentaje;
    ArrayList<DatosDestinatarioSeleccionado> DestinatarioSeleccionado = new ArrayList<>();
    DatosDestinatarioSeleccionado dtDestinatario;
    private static final String ACTION_GENERAR_GUIA = "GeneracionDeGuia";
    private static final String ACTION_NAME_FP = "ListaFormaPagosCiudad";
    private static final String NAMESPACE = "http://tempuri.org/";
    static String TAG = "dtDestinatario";
    ProgressBar progress;

    private int size = 180;
    private int size_width = 390;
    private int size_height = 70;
    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    //Db usdbh;
    //SQLiteDatabase db;
    public AlertDialog alert;
    private UUID myUUID;

    private static final String ACTION_METHOD_DATOS = "TraerDatosImagen";
    DatosUsuario datosUsuario;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    ListView listViewPairedDevice;

    ArrayList<FormaDePagoList> listFP = new ArrayList<>();

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
        setContentView(R.layout.activity_generacion_guia);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);
        BASE_URL = sharedPreferences.getString("urlColegio", "");

        progress = (ProgressBar) findViewById(R.id.progress);
        imgfondo =(ImageView) findViewById(R.id.imgfondo);
        lytdesing = (LinearLayout) findViewById(R.id.lytdesign);

        btnimprimir = (Button) findViewById(R.id.btnimprimir);

        checkFacturaE = (CheckBox) findViewById(R.id.checkFacturaE);
        edContenido = (EditText)findViewById(R.id.edContenido);
        edNoPedido = (EditText)findViewById(R.id.edNoPedido);
        edObservaciones = (EditText)findViewById(R.id.edObservaciones);
        spForma = (Spinner) findViewById(R.id.lstFormaPago);
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        imgButtonRe = (ImageButton) findViewById(R.id.btnRetornoF);
        imgEvidencia = (ImageView) findViewById(R.id.imgEvidencia);
        btnCamara = (FloatingActionButton) findViewById(R.id.btnCamaraE);
        imgEvidenciaE = (ImageView) findViewById(R.id.imgEvidenciaC);
        imgLogo = (ImageView) findViewById(R.id.imagenRegistro);
        imgAlfa = (ImageView) findViewById(R.id.imagenAlfanumerico);


        imgButtonRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeneracionGuia.this, ListaDestinatarios.class);
                startActivity(intent);
                finish();
            }
        });


        txtImpresionCiudad = (TextView) findViewById(R.id.txtImpresionCiudad);
        txtImpresionPedido = (TextView) findViewById(R.id.txtImpresionPedido);
        txtImpresionPago = (TextView) findViewById(R.id.txtImpresionPago);

        imgImpresionQR = (ImageView) findViewById(R.id.imgeImpresionQR);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    } else {
                        TedBottomPicker.with(GeneracionGuia.this)
                                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
                                {

                                    @Override
                                    public void onImageSelected(Uri uri) {

                                        final Uri url_picture = Uri.fromFile(new File(uri.getPath()));

                                        Picasso.get().load(url_picture)

                                                .into(imgEvidenciaE, new Callback()
                                                {
                                                    @Override
                                                    public void onSuccess() {
                                                        Picasso.get().load(url_picture).resize(700,800)



                                                                .into(imgEvidencia, new Callback()
                                                                {
                                                                    @Override
                                                                    public void onSuccess() {

                                                                    }

                                                                    @Override
                                                                    public void onError(Exception e) {

                                                                        displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                                    }


                                                                });
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                        displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                    }


                                                });
                                    }

                                    public void onImageError(String message)
                                    {
                                        displayMessage("Intente nuevamente");
                                    }
                                });
                    }
                }
                else {
                    TedBottomPicker.with(GeneracionGuia.this)
                            .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
                            {
                                public void onImageSelected(Uri uri) {
                                    // here is selected image uri
                                    final Uri url_picture = Uri.fromFile(new File(uri.getPath()));

                                    Picasso.get().load(url_picture)
                                            .into(imgEvidenciaE, new Callback()
                                            {
                                                @Override
                                                public void onSuccess() {
                                                    Picasso.get().load(url_picture).resize(300,300)

                                                            .into(imgEvidencia, new Callback()
                                                            {
                                                                @Override
                                                                public void onSuccess() {

                                                                }

                                                                @Override
                                                                public void onError(Exception e) {

                                                                    displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                                }


                                                            });
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                    displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
                                                }


                                            });
                                }

                                public void onImageError(String message)
                                {
                                    displayMessage("Intente nuevamente");
                                }
                            });
                }
            }
        });

        //Datos Ciudad
        strNomCiuDes = sharedPreferences.getString("strNomciuDest", "");

        //Datos Registro
        intIdUsuario = sharedPreferences.getInt("idUsuario",0);
        //strCiudadOrigen = ciudadOrigen(String.valueOf(intIdUsuario));
        new DatoCiudadOrigenUsuario(intIdUsuario).execute();
        intPorcentaje = sharedPreferences.getString("intPorcentaje", "");
        strCiudadGuia = sharedPreferences.getString("strCiudadC", "");
        strProductoGuia = sharedPreferences.getString("strNombrePC", "");
        strEnvio = sharedPreferences.getString("strNombreEC", "");
        intIdproducto = sharedPreferences.getInt("intCodProdC", 0);
        intIdEnvio = sharedPreferences.getInt("intCodTienvC", 0);
        strPesoGuia =sharedPreferences.getString("strPesoC", "");
        strCantidadGuia = sharedPreferences.getString("strCantidadC", "");
        strValorFlete = sharedPreferences.getString("strValorFlete", "");
        strValorGuia = sharedPreferences.getString("strValorC", "");
        strValorTotal = sharedPreferences.getString("strTotalC", "");
        strValorTotalSin = sharedPreferences.getString("strTotalSin", "");
        strValorConSigno = sharedPreferences.getString("strTotalE", "");
        strValorSeguro = sharedPreferences.getString("strValorS", "");
        strBonoDescuento = sharedPreferences.getString("strBonoDescuento", "");
        blClienteCorp = sharedPreferences.getBoolean( "blClienteCorp", false);
        blClienteCon = sharedPreferences.getBoolean( "blClienteCon", false);

        //Datos Remitente
        intCodCli = sharedPreferences.getInt("intCodCliN", 0);
        strNomCli = sharedPreferences.getString("strNombreC", "");
        strCompanhiaCli = sharedPreferences.getString("strCompaniaC", "");
        strApeCli = sharedPreferences.getString("strApellidoC", "");
        strDocumentoCli  = sharedPreferences.getString("strDocumentoC", "");
        strDicli = sharedPreferences.getString("strDireccionC", "");
        strCelCli = sharedPreferences.getString("strTelefonoC", "");

        //Datos Destinatario
        strNombreDest = sharedPreferences.getString("strNombreDe", "");
        strApellidoDest = sharedPreferences.getString("strApellidoDe", "");
        strCompaniaDest = sharedPreferences.getString("strCompaniaDe", "");
        strDirecionDest = sharedPreferences.getString("strDireccionDe", "");
        strDocumentoDest = sharedPreferences.getString("strDocumentoDe", "");
        strTelefonoDest = sharedPreferences.getString("strTelefonoDe", "");
        intCodest = sharedPreferences.getInt("intCodDestN", 0);

        //ListaPagos();

        new ListaFPAsyncTask(strCiudadGuia).execute();

        spForma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object o = spForma.getItemAtPosition(position);
                FormaDePagoList ListForm = (FormaDePagoList) o;
                strNomFoma  = ListForm.getStrNombreF();
                intForma = ListForm.getIntCodigoF();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conectado)
                {
                    load_imprimir();
                }
                else
                {
                    myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter == null) {
                        Toast.makeText(GeneracionGuia.this,
                                "Bluetooth NO SOPORTADO",
                                Toast.LENGTH_LONG).show();
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

                    setup();
                }

                //finish();

            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFinalizar.setEnabled(false);
                if(edContenido.length() > 0)
                {
                    // progress.setVisibility(View.GONE);
                    DecimalFormat formatter = new DecimalFormat("$#,###.00");
                    AlertDialog.Builder builder = new AlertDialog.Builder(GeneracionGuia.this);
                    builder.setTitle(Html.fromHtml("<p><span style='color:#000000; font-size:30;'>Datos De La Guia</span><span>"));
                    builder.setMessage(Html.fromHtml("<p><h4><span style='color:#B22222; font-weight: bold;'>Ciudad Destino: </span></h4></p><p><span>"+strNomCiuDes+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Direccion Dest: </span></h4></p><p><span>"+strDirecionDest+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Valor Total: </span></h4></p><p><span>"+strValorTotal+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Telefono Dest: </span></h4></p><p><span>"+strTelefonoDest+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Producto: </span></h4></p><p><span>"+strProductoGuia+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Tipo De Envio: </span></h4></p><p><span>"+strEnvio+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Peso: </span></h4><span>"+strPesoGuia+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Cantidad: </span></h4></p><p><span>"+strCantidadGuia+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>VR. Dec: </span></h4></p><p><span>"+formatter.format(Double.parseDouble(strValorGuia))+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Documento Remitente: </span></h4></p><p><span>"+strDocumentoCli+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Documento Destinatario: </span></h4></p><p><span>"+strDocumentoDest+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Forma De Pago: </span></h4></p><p><span>"+strNomFoma+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Contenido: </span></h4></p><p><span>"+edContenido.getText().toString()+"</span></p>"
                            +"<p><h4><span style='color:#B22222; font-weight: bold;'>Observaciones: </span></h4></p><p><span>"+edObservaciones.getText().toString()+"</span></p>"

                    ));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(NetworkUtil.hayInternet(GeneracionGuia.this)) {
                                if(!entro){
                                    entro = true;
                                    btnCamara.setVisibility(View.GONE);
                                    btnCamara.setEnabled(false);

                                    checkFacturaE.setEnabled(false);
                                    edContenido.setEnabled(false);
                                    edNoPedido.setEnabled(false);
                                    edObservaciones.setEnabled(false);
                                    spForma.setEnabled(false);

                                    Bitmap btFoto;
                                    String strVacio = "", strFoto;
                                    try {
                                        Bitmap btmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgEvidenciaE.getDrawable()).getBitmap(), 1000, 1000, false);
                                        //btFoto = ((BitmapDrawable) imgEvidenciaE.getDrawable()).getBitmap();
                                        strFoto = convertirBitmapBase64(btmp);
                                        System.out.println("reslt: " + 1);
                                    } catch (Exception ex) {
                                        strFoto = "";
                                        System.out.println("reslt: " + 2 + ex);

                                    }

                                    new SendDatosGuiasyncTask(intIdUsuario, intCodCli, strDicli, strCelCli, strVacio,
                                            strDocumentoCli, strNomCli, strNombreDest, strCiudadGuia, strTelefonoDest,
                                            strDocumentoDest, strVacio, intCodest, edContenido.getText().toString(),
                                            strPesoGuia, strValorGuia, intIdproducto, intIdEnvio, Integer.parseInt(strCantidadGuia),
                                            strVacio, intForma, strNomFoma, edObservaciones.getText().toString(), strFoto, strDirecionDest,
                                            strProductoGuia, strNomCiuDes, strValorTotalSin, strValorSeguro, strBonoDescuento,
                                            edNoPedido.getText().toString(), checkFacturaE.isChecked(), intPorcentaje, strValorFlete,
                                            strCompanhiaCli, strApeCli, strApellidoDest, strCompaniaDest).execute();

                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Sin Conexion A Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    AlertDialog dialog = builder.show();
                    TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                    messageView.setTextSize(20);
                    btnFinalizar.setEnabled(true);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Debes ingresar el contenido del articulo.", Toast.LENGTH_SHORT).show();
                    btnFinalizar.setEnabled(true);
                }
            }
        });
    }

    public void displayMessage(String toastString) {

        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(GeneracionGuia.this,""+toastString,Toast.LENGTH_SHORT).show();
        }
    }

    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(GeneracionGuia.this, android.Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(GeneracionGuia.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(GeneracionGuia.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fine == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED)
        {
            return true;

        }

        else
        {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(GeneracionGuia.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }


    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(GeneracionGuia.this);

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("Finalizado", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(GeneracionGuia.this, MenuLogistica.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }

    public class SendDatosGuiasyncTask extends AsyncTask<Integer, Integer, String>
    {

        int intCodusuF;
        int intCodCliF;
        int intCodDestF;
        int intCodProdF;
        int intCodTipEnvF;
        int intCantidadF;
        int intFormaPagoF;
        String strValorTotal;
        String strDirCliF, strTelCliF, strEmailCliF, strIdeCliF, strCompaniaCliF, strCompaniaDestF, strCiudestF, strTelDestF,
                strIdeDestF, strEmailDestF, strContenidoF, strPesoF, strValorDeclaradoF, strValorTotalF, strNomForPagF,strObservacionesF, strImgen,
                strDirDestF, strNomProdF, strNomCiuDes, strValorS, strBono, strNoPedido, strValorFlete, intPorcentual, strNombreCli, strApellidoCliF,
                strApellidoDest, strNombreDest;
        boolean blFacturaE;
        String strVacio = "";

        public SendDatosGuiasyncTask(int intCodusuF, int intCodCliF, String strDirCliF, String strTelCliF, String strEmailCliF, String strIdeCliF,
                                     String strNombreCli, String strNombreDest, String strCiudestF, String strTelDestF, String strIdeDestF,
                                     String strEmailDestF, int intCodDestF, String strContenidoF, String strPesoF, String strValorDeclaradoF, int intCodProdF,
                                     int intCodTipEnvF, int intCantidadF, String strValorTotalF, int intFormaPagoF, String strNomForPagF, String strObservacionesF, String strImgen,
                                     String strDirDestF, String strNomProdF, String strNomCiuDes, String strValorTotal, String strValorS, String strBono,
                                     String strNoPedido, boolean blFacturaE, String intPorcentual, String strValorFlete, String strCompaniaCliF, String strApellidoCliF,
                                     String strApellidoDest, String strCompaniaDestF)
        {
            this.intCodusuF = intCodusuF;
            this.intCodCliF = intCodCliF;
            this.strDirCliF = strDirCliF;
            this.strTelCliF = strTelCliF;
            this.strEmailCliF = strEmailCliF;
            this.strIdeCliF = strIdeCliF;
            this.strCompaniaCliF = strCompaniaCliF;
            this.strNombreCli = strNombreCli;
            this.strApellidoCliF = strApellidoCliF;
            this.strCompaniaDestF = strCompaniaDestF;
            this.strApellidoDest = strApellidoDest;
            this.strNombreDest = strNombreDest;
            this.strCiudestF = strCiudestF;
            this.strTelDestF = strTelDestF;
            this.strIdeDestF = strIdeDestF;
            this.strEmailDestF = strEmailDestF;
            this.intCodDestF = intCodDestF;
            this.strContenidoF = strContenidoF;
            this.strPesoF = strPesoF;
            this.strValorDeclaradoF = strValorDeclaradoF;
            this.intCodProdF = intCodProdF;
            this.intCodTipEnvF = intCodTipEnvF;
            this.intCantidadF = intCantidadF;
            this.strValorTotalF = strValorTotalF;
            this.intFormaPagoF = intFormaPagoF;
            this.strNomForPagF = strNomForPagF;
            this.strObservacionesF = strObservacionesF;
            this.strImgen = strImgen;
            this.strDirDestF = strDirDestF;
            this.strNomProdF = strNomProdF;
            this.strNomCiuDes = strNomCiuDes;
            this.strValorTotal = strValorTotal;
            this.intPorcentual = intPorcentual;
            this.strValorS = strValorS;
            this.strBono = strBono;
            this.blFacturaE = blFacturaE;
            this.strNoPedido = strNoPedido;
            this.strValorFlete = strValorFlete;
        }
        //Metodo en string

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //p =  ProgressDialog.show(GeneracionGuia.this, "",
            //      "Transmitiendo Informacion...", true, false);
            // Start a new thread that will download all the data
            /*p = new ProgressDialog(GeneracionGuia.this);
            p.show(GeneracionGuia.this, "Procesando...", "por favor esperar un momento.",false);*/
        }

        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            //p.cancel();
            //progress.setVisibility(View.GONE);
            if(s.length() > 0) {

                String strValor = s;

                AlertDialog.Builder builder = new AlertDialog.Builder(GeneracionGuia.this);
                builder.setTitle("Aviso");
                builder.setMessage(s);
//                if(s.equalsIgnoreCase(" GUIA MANUAL NO DISPONIBLE")) {
//                    builder.setMessage(s);
//                }
//                else
//                    //builder.setMessage("Guía generada correctamente \n No."+ s);
//                    builder.setMessage(s);

                builder.setCancelable(false);

                builder.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                        if(!s.equalsIgnoreCase(" GUIA MANUAL NO DISPONIBLE")) {
                            Intent act = new Intent(GeneracionGuia.this,MenuLogistica.class);
                            startActivity(act);
                            finish();
                        }else{
                            btnCamara.setVisibility(View.VISIBLE);
                            btnCamara.setEnabled(true);
                            checkFacturaE.setEnabled(true);
                            edContenido.setEnabled(true);
                            edNoPedido.setEnabled(true);
                            edObservaciones.setEnabled(true);
                            spForma.setEnabled(true);
                        }
                    }
                });

                dialog = builder.show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"El proceso de generar de guia no se pudo realizar", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_GENERAR_GUIA);

            request.addProperty("intCodusu", intCodusuF);
            request.addProperty("intCodCli",intCodCliF);
            request.addProperty("strApellidoCli", strApellidoCliF);
            request.addProperty("strNombreCli", strNombreCli);//strCompaniaCliF);
            request.addProperty("strDirCli", strDirCliF);
            request.addProperty("strTelefonoCLi", strTelCliF);
            request.addProperty("strEmailCli", strEmailCliF);
            request.addProperty("strIdentificacionCli", strIdeCliF);
            request.addProperty("strCompaniaCli", strCompaniaCliF);
            request.addProperty("strApellidoDest", strApellidoDest);
            request.addProperty("strNombreDest", strNombreDest);
            request.addProperty("strCompaniaDest", strCompaniaDestF);
            request.addProperty("strCiudadDest", strCiudestF);
            request.addProperty("strTelefonoDest", strTelDestF);
            request.addProperty("strIdentificacionDest", strIdeDestF);
            request.addProperty("strEmailDest", strEmailDestF);
            request.addProperty("intCoddest", intCodDestF);
            request.addProperty("strContenido", strContenidoF);
            request.addProperty("strPeso", strPesoF);
            request.addProperty("strValorDecl", strValorDeclaradoF);
            request.addProperty("intCodProd", intCodProdF);
            request.addProperty("intCodTienv", intCodTipEnvF);
            request.addProperty("intCantidad", intCantidadF);
            request.addProperty("strValorTotal", strValorTotal);
            request.addProperty("strValorFlete", strValorFlete);
            request.addProperty("intFormaPago", intFormaPagoF);
            request.addProperty("strNomForPag", strNomForPagF);
            request.addProperty("strObservaciones", strObservacionesF);
            request.addProperty("strImgen", strImgen);
            request.addProperty("strBono_G", strBono);
            request.addProperty("strDirDest", strDirDestF);
            request.addProperty("strNoPedido", strNoPedido);
            request.addProperty("blFacturaE", blFacturaE);
            request.addProperty("decPorcentual", String.valueOf(intPorcentual));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_GENERAR_GUIA, envelope);
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
                res = "No generado.";
            }

            return res;
        }

    }


    @Override
    protected void onStart() {
        //
        super.onStart();


    }
    public void lanza_alert()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(GeneracionGuia.this);

        View  promptView = layoutInflater.inflate(R.layout.layout_dispositivos, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GeneracionGuia.this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        alertDialogBuilder.setView(promptView);
        if(alert==null)
        {
            alert = alertDialogBuilder.create();
            alert.show();
            alert.setCanceledOnTouchOutside(false);
        }

        listViewPairedDevice = (ListView) promptView.findViewById(R.id.list_dispositivos);

    }

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

                    Toast.makeText(GeneracionGuia.this,"Espere un momento",Toast.LENGTH_LONG).show();
                    //listViewPairedDevice.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imgEvidencia.setImageBitmap(imageBitmap);

                System.out.println("Size imagen " + imageBitmap.getHeight() + " - " + imageBitmap.getWidth());
                imgEvidenciaE.setImageBitmap(imageBitmap);
            }
        }
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


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
                runOnUiThread(new Runnable() {
                    public void run() {

                        listViewPairedDevice.setVisibility(View.GONE);
                        Toast.makeText(GeneracionGuia.this,"Conectado correctamente",Toast.LENGTH_LONG).show();
                        conectado = true;

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //alert.cancel();
                        Toast.makeText(GeneracionGuia.this,"No se pudo conectar al dispositivo, intente nuevamente",Toast.LENGTH_LONG).show();

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

            v=true;

            while (v) //// aqui inicia el bucle
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
                            Toast.makeText(GeneracionGuia.this,"Se perdio la conexión",Toast.LENGTH_LONG).show();
                            try {
                                connectedInputStream.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            v=false;

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

    public void imprimirRegistro(String strNomCiuDesI, String strPedido1, String strNomCliI, String strDicliI, String strCelCliI,
                                 String strNombreDestI, String strDirDestI, String strProductoI, String strPesoI, String strValorI,
                                 String strCantidadI, String strNomFormaI, String strValorS, String strContenidoF, String strValorG, String strValorDec)
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
        byte[] SET_LINE_SPACING_24 = new byte[]{0x1B, 'a', 0x02};
        byte[] align_left = new byte[]{0x1B, 'a', 0x00};
        byte[] left = {0x1B, 0x61, 0};
        byte[] right = {0x1B, 0x61, 2};

        // myThreadConnected.write(bb4);

        try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 390, 70, false);
            if(bmp!=null)
            {
                byte[] command0 = decodeBitmap(bmp);
                myThreadConnected.write(command0);

            }else{

                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();


            Log.e("PrintTools", "the file isn't exists");
        }

        myThreadConnected.write(bb5);

        if(myThreadConnected!=null){

            try {

                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgfondo.getDrawable()).getBitmap(), 530, 150, false);
                if(bmp!=null)
                {
                    byte[] command0 = decodeBitmap(bmp);
                    myThreadConnected.write(command0);

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();


                Log.e("PrintTools", "the file isn't exists");
            }

            //myThreadConnected.write(bytesTodDestCiud+command10);
            //byte[] NewLineDestCiud  = "\n".getBytes();
            //myThreadConnected.write(NewLineDestCiud);

            /*
            nombre      image
            pedido
            forma
             */

            //myThreadConnected.write(SET_LINE_SPACING_24);
            //myThreadConnected.write(right);

            //imrpimimos la imagen

            //myThreadConnected.write(left);
            myThreadConnected.write(cc);

            byte[] bytesToRemi = ("RM. "+strNomCliI).toString().getBytes();
            myThreadConnected.write(bytesToRemi);
            byte[] NewLineRemi = "\n".getBytes();
            myThreadConnected.write(NewLineRemi);

            byte[] bytesToRemiDir = ("Dir. " + strDicliI).toString().getBytes();
            myThreadConnected.write(bytesToRemiDir);
            byte[] NewLineRemiDir = "\n".getBytes();
            myThreadConnected.write(NewLineRemiDir);

            byte[] bytesToRemiTel = ("Cel. "+ strCelCliI).toString().getBytes();
            myThreadConnected.write(bytesToRemiTel);
            byte[] NewLineRemiTel = "\n".getBytes();
            myThreadConnected.write(NewLineRemiTel);

            myThreadConnected.write(bb);
            byte[] bytesToSend = ("DEST: "+ strNomCiuDesI).toString().getBytes();
            myThreadConnected.write(bytesToSend);
            byte[] NewLine = "\n".getBytes();
            myThreadConnected.write(NewLine);

            myThreadConnected.write(cc);
            byte[] bytesToDest = (strNombreDestI).toString().getBytes();
            myThreadConnected.write(bytesToDest);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesToDestDir = ("Dir. "+ strDirDestI).toString().getBytes();
            myThreadConnected.write(bytesToDestDir);
            byte[] NewLineDestiDir = "\n".getBytes();
            myThreadConnected.write(NewLineDestiDir);

            /*byte[] bytesToDestProd = ("Prod. "+ strProductoI).toString().getBytes();
            myThreadConnected.write(bytesToDestProd);
            byte[] NewLineDestProd = "\n".getBytes();
            myThreadConnected.write(NewLineDestProd);
            */

            byte[] bytesTodDestPeso = ("Peso. "+ strPesoI).toString().getBytes();
            myThreadConnected.write(bytesTodDestPeso);
            byte[] NewLineDestPeso  = "\n".getBytes();
            myThreadConnected.write(NewLineDestPeso);

            byte[] bytesTodDestValFlet = ("Flete . "+ strValorG).toString().getBytes();
            myThreadConnected.write(bytesTodDestValFlet);
            byte[] NewLineDestValFlet  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValFlet);

            byte[] bytesTodDestValTDec = ("VR Declarado . " + strValorDec).toString().getBytes();
            myThreadConnected.write(bytesTodDestValTDec);
            byte[] NewLineDestValTDec  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValTDec);

            byte[] bytesTodDestValSeg = ("VR Seguro . " + strValorS).toString().getBytes();
            myThreadConnected.write(bytesTodDestValSeg);
            byte[] NewLineDestValSeg  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValSeg);

            byte[] bytesTodDestValDec = ("VT. " + strValorI).toString().getBytes();
            myThreadConnected.write(bytesTodDestValDec);
            byte[] NewLineDestValDec  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValDec);

            myThreadConnected.write(cc);
            byte[] bytesContenido= ("Cont. "+strContenidoF).toString().getBytes();
            myThreadConnected.write(bytesContenido);
            byte[] NewLineContenido  = "\n".getBytes();
            myThreadConnected.write(NewLineContenido);

            myThreadConnected.write(bb8);
            byte[] bytesTodPiezas = ("    PIEZAS "+ strCantidadI).toString().getBytes();
            myThreadConnected.write(bytesTodPiezas);
            byte[] NewLinePiezas = "\n".getBytes();
            myThreadConnected.write(NewLinePiezas);

            myThreadConnected.write(bb4);

            byte[] bytesFirma = ("Firma:").toString().getBytes();
            myThreadConnected.write(bytesFirma);
            byte[] NewLinefirma= "\n".getBytes();
            myThreadConnected.write(NewLinefirma);

            myThreadConnected.write(bb7);
            byte[] byteslinea = ("_________________________________________").toString().getBytes();
            myThreadConnected.write(byteslinea);
            byte[] NewLinelinea  = "\n".getBytes();
            myThreadConnected.write(NewLinelinea);

            myThreadConnected.write(bb7);
            byte[] bytesLink = (getResources().getString(R.string.TerminosCondiciones)).toString().getBytes();
            myThreadConnected.write(bytesLink);
            byte[] NewLineLink  = "\n".getBytes();
            myThreadConnected.write(NewLineLink);

            //SALTO PAGINA
            int var1=0,var0=1;
            byte[] var2 = new byte[]{29, 86, 65, (byte)var1};
            if (var0 == 1) {
                var2[2] = 66;
            }
            myThreadConnected.write(var2);

            //salimos  del activity
            btnFinalizar.setEnabled(true);
            // myThreadConnected.cancel();
            //finish();
            // Intent intent = new Intent(GeneracionGuia.this, Menuotros.class);
            //startActivity(intent);
            //finish();
        }
    }

    public static Bitmap captureScreen(View v) {

        Bitmap screenshot = null;
        try {
            if(v!=null) {
                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }
        }catch (Exception e){
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
        }
        return screenshot;
    }

    public static void saveImage(Bitmap bitmap) throws IOException{

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
        File root = Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/H2HOY");
        if(!dir.exists())
        {
            dir.mkdirs();
            System.out.println("creo carpeta");
        }
        File f = new File(dir.getAbsolutePath() + "/test-h2hoy.png");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
    }

    public class ListaFPAsyncTask extends AsyncTask<Integer,Integer,ArrayList<FormaDePagoList>> {

        String strCiudadDes;
        public ListaFPAsyncTask(String strCiudadDes) {
            this.strCiudadDes = strCiudadDes;
        }

        @Override
        protected void onPostExecute(ArrayList<FormaDePagoList> s) {
            super.onPostExecute(s);
            mListaPagos = new FormaDePagoAdapter(GeneracionGuia.this,s);
            spForma.setAdapter(mListaPagos);
        }

        @Override
        protected ArrayList<FormaDePagoList> doInBackground(Integer... integers) {

            SoapObject request = new SoapObject(NAMESPACE, ACTION_NAME_FP);
            request.addProperty("strCodCiuDest", strCiudadDes);
            SoapObject result;

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try {
                httpTransport.call(NAMESPACE + ACTION_NAME_FP, envelope);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            result = (SoapObject) envelope.bodyIn;

            int intCodigoFP;
            String strNomFP;

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {

                    SoapObject s = (SoapObject) table1.getProperty(i);
                    intCodigoFP = Integer.parseInt(s.getProperty("CODFORPAG").toString());
                    strNomFP = s.getProperty("NOMFORPAG").toString();

                    // SI ES CREDITO SOLO PARA CLIENTES CORP
                    if(blClienteCorp){
                        // TODAS LAS FORMAS
                        if(blClienteCon){
                            // CONTADO = 18
                            // CREDITO = 1
                            listFP.add(new FormaDePagoList(intCodigoFP, strNomFP));
                        }
                        // SOLO CREDITO
                        else{
                            if(intCodigoFP == 1)
                                listFP.add(new FormaDePagoList(intCodigoFP, strNomFP));
                        }
                    }
                    else{
                        // NO ES CREDITO
                       if(intCodigoFP != 1)
                            listFP.add(new FormaDePagoList(intCodigoFP, strNomFP));
                    }
                }

            }
            else
            {
                listFP.add(new FormaDePagoList(0, "Error al consumir el ws"));
            }
            return listFP;
        }
    }

    public void load_imprimir()
    {
        DecimalFormat formatter = new DecimalFormat("$#,###.00");
        imprimirRegistro(strNomCiuDes,"", strNomCli, strDicli, strCelCli,
                strNombreDest, strDirecionDest, strProductoGuia, strPesoGuia, strValorTotal,
                String.valueOf(strCantidadGuia), strNomFoma, strValorSeguro, edContenido.getText().toString(),strValorFlete,formatter.format(Double.parseDouble(strValorGuia)));

        Intent intent = new Intent(GeneracionGuia.this, MenuLogistica.class);
        startActivity(intent);
        finish();
    }

    public class DatoCiudadOrigenUsuario extends AsyncTask<Integer, Integer, String>
    {

        int intCodusuF;

        String strVacio = "";

        public DatoCiudadOrigenUsuario(int intCodusuF)
        {
            this.intCodusuF = intCodusuF;
        }
        //Metodo en string

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            strCiudadOrigen = s;
            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, ACTION_METHOD_DATOS);

            request.addProperty("intCodusu", intCodusuF);
            SoapObject result;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;
            try
            {
                httpTransport.call(NAMESPACE+ACTION_METHOD_DATOS, envelope);
            }

            catch (Exception ex)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,ex.getMessage());
                ex.printStackTrace();
            }
            result = (SoapObject) envelope.bodyIn;

            String strNomciu = "";

            SoapObject getListResponse = (SoapObject) result.getProperty(0);
            SoapObject DocumentElement = (SoapObject) getListResponse.getProperty(1);

            if(DocumentElement.getPropertyCount() > 0) {

                SoapObject table1 = (SoapObject) DocumentElement.getProperty(0);

                for (int i = 0; i < table1.getPropertyCount(); i++) {
                    SoapObject s = (SoapObject) table1.getProperty(i);

                    strNomciu = s.getProperty("NOMCIU").toString();
                }
                return  strNomciu;
            }
            else
            {
                return strNomciu;
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

