package com.appetesg.estusolucionTranscarga;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import io.reactivex.disposables.Disposable;

import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;
import com.appetesg.estusolucionTranscarga.utilidades.DrawingView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.appetesg.estusolucionTranscarga.utilidades.ImagePickerActivity;
import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.android.material.snackbar.Snackbar;

public class firmaImagen extends AppCompatActivity {
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    SharedPreferences sharedPreferences;
    private ImageView iv_image;
    private Disposable singleImageDisposable;
    ImageButton imgButonGuia, imgButonDeleteGuia;
    ImageView imgFotoGuia, imgFotoCompleta;
    DrawingView mDrawingView;
    private static final String METHOD_NAME_ESTADOS_GUIA = "ActualizarEstado";
    private static final String NAMESPACE = "http://tempuri.org/";
    LinearLayout linearFirma, linFirma;
    ImageButton imbLimpiarFirma;
    TextView lblTitulo;
    static String TAG="firmaImagen";
    ProgressBar progress;
    String strNroGuia, strUsuarioRecibe, strFechaEstado;
    String BASE_URL,PREFS_NAME;
    int idUsuario, intEstados;
    Button btnEnviarFirma;
    //Db usdbh;
    //SQLiteDatabase db;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(firmaImagen.this, EstadoGuiaActivity.class);
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

        setContentView(R.layout.activity_firma_imagen);

        PREFS_NAME = this.getString(R.string.SPREF);
        //usdbh = new Db(firmaImagen.this, getResources().getString(R.string.name_bd), null, Integer.parseInt(getResources().getString(R.string.version_database)));
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        //BASE_URL = this.getString(R.string.BASE_URL2);
        BASE_URL = sharedPreferences.getString("urlColegio","");
        progress = (ProgressBar) findViewById(R.id.progress);

        lblTitulo = findViewById(R.id.titlleGuia);
        linearFirma = (LinearLayout)findViewById(R.id.linearFirmaGuiaDestino);
        linFirma = (LinearLayout)findViewById(R.id.linFirma);
        imbLimpiarFirma = (ImageButton)findViewById(R.id.imbLimpiarFirmaGuia);
        //imgButonGuia = (ImageButton)findViewById(R.id.imbFotoGuia);
        //imgButonDeleteGuia = (ImageButton)findViewById(R.id.imbFotoBorrarGuia);
        imgFotoGuia = (ImageView) findViewById(R.id.imgFotoGuia);
        imgFotoCompleta = (ImageView) findViewById(R.id.imgGuiaCompleta);
        btnEnviarFirma = (Button)findViewById(R.id.btnFirma);

        strNroGuia = sharedPreferences.getString("NroGuia", "");
        strFechaEstado = sharedPreferences.getString("strFechaEstado", "");
        strUsuarioRecibe = sharedPreferences.getString("strRecibe", "");
        intEstados = sharedPreferences.getInt("intEstado",0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        lblTitulo.setText("No. Guia: " + strNroGuia);
        mDrawingView = new DrawingView(this);
        linearFirma.addView(mDrawingView);

        if(intEstados!=600) {
            linearFirma.setVisibility(View.GONE);
            imbLimpiarFirma.setVisibility(View.GONE);
            linFirma.setVisibility(View.GONE);
        }

        /*FloatingActionButton fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRxSingleShowButton();
            }
        });*/

        imgFotoGuia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        imbLimpiarFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearFirma.removeAllViews();
                mDrawingView = new DrawingView(firmaImagen.this);
                linearFirma.addView(mDrawingView);
            }
        });

//        imgButonDeleteGuia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgFotoGuia.setImageResource(0);
//                imgFotoCompleta.setImageResource(0);
//            }
//        });
//
//        imgButonGuia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                int MyVersion = Build.VERSION.SDK_INT;
//                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    if (!checkIfAlreadyhavePermission()) {
//                        requestForSpecificPermission();
//                    } else {
//                        TedBottomPicker.with(firmaImagen.this)
//                                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
//                                {
//
//                                    @Override
//                                    public void onImageSelected(Uri uri) {
//
//                                        final Uri url_picture = Uri.fromFile(new File(uri.getPath()));
//
//                                        Picasso.get().load(url_picture).resize(300,300)
//                                                .into(imgFotoGuia, new Callback()
//                                                {
//                                                    @Override
//                                                    public void onSuccess() {
//                                                        Picasso.get().load(url_picture).resize(1200,1200)
//
//                                                                .into(imgFotoCompleta, new Callback()
//                                                                {
//                                                                    @Override
//                                                                    public void onSuccess() {
//
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onError(Exception e) {
//
//                                                                        displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
//                                                                    }
//                                                                });
//                                                    }
//
//                                                    @Override
//                                                    public void onError(Exception e) {
//
//                                                        displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
//                                                    }
//                                                });
//                                    }
//
//                                    public void onImageError(String message)
//                                    {
//                                        displayMessage("Intente nuevamente");
//                                    }
//                                });
//                    }
//                } else {
//                    TedBottomPicker.with(firmaImagen.this)
//                            .show(new TedBottomSheetDialogFragment.OnImageSelectedListener()
//                            {
//
//                                public void onImageSelected(Uri uri) {
//                                    // here is selected image uri
//                                    final Uri url_picture = Uri.fromFile(new File(uri.getPath()));
//
//                                    Picasso.get().load(url_picture).resize(300,300)
//
//                                            .into(imgFotoGuia, new Callback()
//                                            {
//                                                @Override
//                                                public void onSuccess() {
//                                                    Picasso.get().load(url_picture).resize(1200,1200)
//
//                                                            .into(imgFotoCompleta, new Callback()
//                                                            {
//                                                                @Override
//                                                                public void onSuccess() {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onError(Exception e) {
//
//                                                                    displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
//                                                                }
//
//
//                                                            });
//                                                }
//
//                                                @Override
//                                                public void onError(Exception e) {
//
//                                                    displayMessage("La foto del perfil no fue cargada, intente nuevamente ");
//                                                }
//
//                                            });
//                                }
//
//                                public void onImageError(String message)
//                                {
//                                    displayMessage("Intente nuevamente");
//                                }
//                            });
//                }
//            }
//        });

        btnEnviarFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgFotoCompleta.getDrawable() != null)
                {
                    linearFirma.setDrawingCacheEnabled(true);
                    linearFirma.buildDrawingCache();
                    Bitmap bmpFoto, bmpFirma;
                    String strFoto, strFirma = "";
                    String strLatitud = "0";
                    String strLongitud = "0";
                    try {
                        bmpFoto = ((BitmapDrawable) imgFotoCompleta.getDrawable()).getBitmap();
                        // bmpFoto = ((BitmapDrawable)imgFotoGuia.getDrawable()).getBitmap();
                        strFoto = convertirBitmapBase64(bmpFoto);
                        Log.d("foto_enviada", strFoto);

                        if (intEstados != 600) {
                            //strFirma = "";
                        } else {
                            bmpFirma = linearFirma.getDrawingCache();
                            strFirma = convertirBitmapBase64(bmpFirma);
                            Log.d("firma_enviada/ ", strFoto);
                        }
                    } catch (Exception ex) {
                        strFoto = "";
                        //strFirma = "";
                    }
                    if (hasConnection(firmaImagen.this)) {
                        progress.setVisibility(View.VISIBLE);
                        new SendEstadosyncTask(idUsuario, strNroGuia, strFechaEstado, intEstados, strLatitud, strLongitud, strFirma, strFoto, strUsuarioRecibe).execute();
                    }
                    //guardamos localmente
                    else {/*
                        try
                        {
                            /b = usdbh.getWritableDatabase();
                            if(db != null)
                            {
                                try
                                {
                                    //Insertamos en la DB
                                    db.execSQL("update guias"+
                                            " set idUsuario = "+idUsuario+", DocumentoReferencia = '"+strNroGuia+"' ,"+
                                            " Fecha = '"+strFechaEstado+"', Estado = "+intEstados+", Latitud = '"+strLatitud+"', Longitud = '" + strLongitud + "', Imagen = '"+strFirma+"', , srtRecibido = '"+strUsuarioRecibe + "', pendiente = 1 where strGuia = '"+strNroGuia+"'");

                                    Toast.makeText(getApplicationContext(), "Su proceso fue exitoso", Toast.LENGTH_LONG).show();

                                    UsuariosColegio usuariosColegio = new UsuariosColegio();
                                    usuariosColegio.idUsuario = idUsuario;
                                    usuariosColegio.save();

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("idUsuario", idUsuario);
                                    editor.commit();

                                    Intent intent = new Intent(firmaImagen.this, MenuLogistica.class);
                                    startActivity(intent);
                                    finish();

                                }
                                catch (SQLException e)
                                {
                                    try{

                                    }
                                    catch (SQLException sl)
                                    {

                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {

                        }*/
                    }
                }
                else{
                    Toast.makeText(getBaseContext(),"Ingrese la foto correspondiente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

   /*private void setRxSingleShowButton() {
        FloatingActionButton btnPhto = (FloatingActionButton) findViewById(R.id.fab1);
        btnPhto.setOnClickListener((view -> {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                    singleImageDisposable = TedRxBottomPicker.with(MainActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setSelectedUri(selectedUri)
                            //.showVideoMedia()
                            .setPeekHeight(1200)
                            .show()
                            .subscribe(uri -> {
                                selectedUri = uri;

                                iv_image.setVisibility(View.VISIBLE);
                                mSelectedImagesContainer.setVisibility(View.GONE);

                                requestManager
                                        .load(uri)
                                        .into(iv_image);
                            }, Throwable::printStackTrace);


                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }


            };

            checkPermission(permissionlistener);
        });
    }*/

    private void openCameraIntent()
    {
        Intent pictureItem = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if(pictureItem.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(pictureItem, REQUEST_CAPTURE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {

            Uri uri = data.getParcelableExtra("path");
            try {
                // You can update this bitmap to your server
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imgFotoGuia.setImageBitmap(bitmap);

                imgFotoCompleta.setImageURI(uri);
                // loading profile image from local cache
                //loadProfile(uri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

//            if (data != null && data.getExtras() != null) {
//                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//                imgFotoGuia.setImageBitmap(imageBitmap);
//
//                System.out.println("Size imagen " + imageBitmap.getHeight() + " - " + imageBitmap.getWidth());
//                imgFotoCompleta.setImageBitmap(imageBitmap);
//            }
        }
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    public String convertirBitmapBase64(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public class SendEstadosyncTask extends AsyncTask<Integer, Integer, String>
    {
        String strPedido, strLatitud, strLongitud, strRecibido, strImagen, strImagenGuia, strFecha;
        int idUsuario, intEstado;

        public SendEstadosyncTask(int idUsuario,String strPedido, String strFecha, int intEstado, String strLatitud, String strLongitud, String strImagen, String strImagenGuia,
                                  String strReibido)
        {
            this.idUsuario = idUsuario;
            this.strPedido = strPedido;
            this.strFecha = strFecha;
            this.intEstado = intEstado;
            this.strLatitud = strLatitud;
            this.strLongitud = strLongitud;
            this.strImagen = strImagen; // firma
            this.strRecibido = strReibido;
            this.strImagenGuia = strImagenGuia; // foto
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.setVisibility(View.GONE);

            // Limpiar el cache por las imagenes utilizadas
            ImagePickerActivity.clearCache(getBaseContext());

            if(s.equalsIgnoreCase("True")|| s.equalsIgnoreCase("true"))
            {
                Toast.makeText(getApplicationContext(), "Proceso fue exitoso.", Toast.LENGTH_LONG).show();

                UsuariosColegio usuarioColegio = new UsuariosColegio();
                usuarioColegio.idUsuario = idUsuario;

                //usuarioColegio.usuario = txtEnviarPas.getText().toString();
                usuarioColegio.save();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("idUsuario", idUsuario);
                editor.commit();

                //manda a imprimir
                //imprimir();
                Intent intent = new Intent(firmaImagen.this, MenuLogistica.class);
                startActivity(intent);
                finish();

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
            request.addProperty("ImgenGuia",strImagenGuia);
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
                res = "Envio Fallido.";
            }

            return res;
        }

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

    public void displayMessage(String toastString) {

        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(firmaImagen.this,""+toastString,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(firmaImagen.this, Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(firmaImagen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(firmaImagen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
        ActivityCompat.requestPermissions(firmaImagen.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
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

    private void launchGalleryIntent() {
        Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }
}
