package com.appetesg.estusolucionTranscarga.ImpresionUi.Rotulos;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.appetesg.estusolucionTranscarga.ImpresionRotulo;
import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.RotuloGuia;
import com.appetesg.estusolucionTranscarga.databinding.FragmentRotulosImpBinding;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.text.DecimalFormat;

import me.bendik.simplerangeview.SimpleRangeView;

import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.bluetoothThread;
import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.conectado;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.CreateImage;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.captureScreen;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.decodeBitmap;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.saveImage;

public class ImpRotulosFragment extends Fragment {

    private FragmentRotulosImpBinding binding;

    static String TAG = "ImpresionRotuloQR";

    // Pantalla
    Button btnimprimirRotuloImp;
    TextView txtImpresionCiudad, txtCiudadDestinoImpresion, txtImpresionPago, txtRemitenteGuia, txtDestinoGuia, txtDestinatarioGuia, txtContenidoGuia, txtPiezasRotulo;
    ImageView imgLogo, imgImpresionQR, imgfondo, imgAlfa;
    Bitmap bmp1, myBitmap;
    LinearLayout lytdesing;

    // Datos cliente
    String strPedido;
    String strCiudadDest;
    String strFormaPago;
    String strNomDest;
    String strDirDest;
    String strNomCli;
    String strDirCli;
    String strCiudadOrigen;
    String strValDecIni;
    String strCelcli;
    String strNomprd;
    String strContenido;
    String strValorEnvio;
    String strPesoPaq;
    String strValDec;
    String strValorFlete;
    String strValDecGeneral;
    String strPuertaEmbarque;
    String strCelDes;

    int intCodusu;
    int intCantidad;
    int intCodCli;
    int intCantRotMax, intCantRotMin;

    SharedPreferences sharedPreferences;
    String BASE_URL,PREFS_NAME;

    public AlertDialog alert;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRotulosImpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnimprimirRotuloImp = root.findViewById(R.id.btnimprimirRotuloImp);
        imgfondo = root.findViewById(R.id.imgfondoImp2);
        lytdesing = root.findViewById(R.id.lytdesignRotulo);
        txtImpresionCiudad = root.findViewById(R.id.txtImpresionCiudadRotulo);
        txtCiudadDestinoImpresion = root.findViewById(R.id.txtCiudadDestinoImpresionRotulo);
        txtImpresionPago = root.findViewById(R.id.txtImpresionPagoRotulo);
        imgImpresionQR = root.findViewById(R.id.imgeImpresionQRRotulo);
        imgAlfa = root.findViewById(R.id.imagenAlfanumericoHistoricoRotulo);
        imgLogo = root.findViewById(R.id.imgLogoRotuloImp);

        txtRemitenteGuia = root.findViewById(R.id.txtRemRotulo);
        txtDestinoGuia = root.findViewById(R.id.txtDestRotulo);
        txtDestinatarioGuia = root.findViewById(R.id.txtDestinatarioRotulo);
        txtContenidoGuia = root.findViewById(R.id.txtContenidoRotulo);
        txtPiezasRotulo = root.findViewById(R.id.txtPiezasRotulo);

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        intCodusu = sharedPreferences.getInt("intCodusuImpresionRotulo", 0);
        intCantidad = sharedPreferences.getInt("intCantidadImpresionRotulo", 0);
        intCodCli = sharedPreferences.getInt("intCodCli", 0);
        strPedido = sharedPreferences.getString("strPedido1ImpresionRotulo", "");
        strFormaPago = sharedPreferences.getString("strFormaPagoImpresionRotulo", "");
        strCiudadDest = sharedPreferences.getString("strCiudadDestinoImpresionRotulo", "");
        strNomDest = sharedPreferences.getString("strNomDestImpresionRotulo", "");
        strDirDest = NetworkUtil.validarAnytype(sharedPreferences.getString("strDireccionDestImpresionRotulo", ""));
        strNomCli = sharedPreferences.getString("strNomcliImpresionRotulo", "");
        strDirCli = NetworkUtil.validarAnytype(sharedPreferences.getString("strDirOriImpresionRotulo", ""));
        strCiudadOrigen = sharedPreferences.getString("strCiudadOrigenImpresionRotulo", "");
        strCelcli = NetworkUtil.validarAnytype(sharedPreferences.getString("strCelcliImpresion", ""));
        strCelDes = NetworkUtil.validarAnytype(sharedPreferences.getString("strCelDesImpresion", ""));
        strNomprd = sharedPreferences.getString("strNomprdImpresion", "");
        strContenido = sharedPreferences.getString("strContenidoImpresion", "");
        strValorFlete = sharedPreferences.getString("strValorFletes", "");
        strValorEnvio = sharedPreferences.getString("strValorEnvioImpresion", "");
        strPesoPaq = sharedPreferences.getString("strPesoPaqImpresion", "");
        strValDecGeneral = sharedPreferences.getString("strValorDecGeneral", "");
        strValDecIni = sharedPreferences.getString("strValorDec", "");
        DecimalFormat formatter = new DecimalFormat("$#,###.00");
        strValDec = String.valueOf(Double.parseDouble(strValDecGeneral) - Double.parseDouble(strValorFlete));
        strValDec = formatter.format(Double.parseDouble(strValDec));
        strValorFlete = formatter.format(Double.parseDouble(strValorFlete));
        strPuertaEmbarque = sharedPreferences.getString("bitPuertaEmbarque", "");

        txtRemitenteGuia.setText(strNomCli);
        txtDestinoGuia.setText(strCiudadDest);
        txtDestinatarioGuia.setText(strNomDest);
        txtContenidoGuia.setText(strContenido);
        txtPiezasRotulo.setText(String.valueOf(intCantidad));

        // FORMATO INICIO GUIA
            try {
            // YA NO, SUSPENDIDO CANTIDAD DE PIEZAS DEBIDO A QUE EN LA WEB NO ESTA EL AJUSTE
            bmp1 = CreateImage(strPedido + "/" + intCantidad, "QR Code");

            //txtCiudadDestinoImpresion.setText("Guia: " + strPedido);
            txtCiudadDestinoImpresion.setText("");
            txtCiudadDestinoImpresion.setVisibility(View.GONE);

            txtImpresionCiudad.setText("Origen: " + strCiudadOrigen); // Ciudad origen guia
            txtImpresionPago.setText(strFormaPago);

            imgImpresionQR.setImageBitmap(bmp1);

            lytdesing.post(() -> {

                //take screenshot
                myBitmap = captureScreen(lytdesing);

                try {
                    if (myBitmap != null) {
                        //save image to SD card
                        imgfondo.setImageBitmap(myBitmap);
                        saveImage(myBitmap);

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("error " + e.getMessage());
                }

            });
        } catch (
        WriterException e) {
            e.printStackTrace();
        }

        btnimprimirRotuloImp.setOnClickListener(view -> {
            btnimprimirRotuloImp.setEnabled(false);
            if(conectado()) {
                intCantRotMin = 0;
                intCantRotMax = intCantidad;
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle(Html.fromHtml("<p><span style='color:#B22222; font-size:30;'>Informacion</span><span>"));
                builder.setMessage(Html.fromHtml("<p><span style='color:#B22222; font-weight: bold;'>Guia Nro: </span><span>" + strPedido + "</span>" +
                        "<p><span style='color:#B22222; font-weight: bold;'>Cantidad: </span><span>" + intCantidad + "</span>"));
                builder.setCancelable(false);

                SimpleRangeView slider = new SimpleRangeView(this.getContext());
                TextView max = new TextView(this.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(35,0,35,0);

                max.setLeft(15);
                max.setRight(15);
                max.setLayoutParams(lp);
                max.setVisibility(View.VISIBLE);

                slider.setLeft(15);
                slider.setRight(15);
                slider.setLayoutParams(lp);
                slider.setCount(intCantidad + 1);
                slider.setEnd(intCantidad);

                slider.setVisibility(View.VISIBLE);

                slider.setOnChangeRangeListener((simpleRangeView, i, i1) -> {
                    max.setText(String.valueOf(i1));
                    intCantRotMax = i1;
                    intCantRotMin = i;
                });
                slider.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
                    @Override
                    public void onStartRangeChanged(@NonNull SimpleRangeView simpleRangeView, int i) {
                    }

                    @Override
                    public void onEndRangeChanged(@NonNull SimpleRangeView simpleRangeView, int i) {
                    }
                });

                slider.setOnRangeLabelsListener((simpleRangeView, i, state) -> String.valueOf(i));

                builder.setView(max);
                builder.setView(slider);

                builder.setPositiveButton("Ok", (dialogInterface, i) -> {

                    dialorInformativo("No cierre la ventana, espere que acabe la impresion").show();
                    for (int a = intCantRotMin; a < intCantRotMax; a++) {
                        int ciclo = a + 1;

                        imprimirEtiqueta2(strPedido, strFormaPago, strCiudadDest,
                                strNomDest, strDirDest, strCiudadOrigen,
                                strNomCli, strDirCli, String.valueOf(ciclo), strNomprd, String.valueOf(intCantidad), strPuertaEmbarque);

                    }

                });
                builder.setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.show();
                TextView messageView = dialog.findViewById(android.R.id.message);
                messageView.setTextSize(20);
                btnimprimirRotuloImp.setEnabled(true);
            }
            else
            {
                //load_conectar();
                Toast.makeText(getContext(), "Conecta el dispositivo bluetooth.", Toast.LENGTH_LONG).show();
                btnimprimirRotuloImp.setEnabled(true);
            }
        });

            return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Se genera alerta con el mensaje entrante
    public AlertDialog dialorInformativo(String strMensaje)
    {
        AlertDialog.Builder buldier = new AlertDialog.Builder(getActivity());

        buldier.setTitle("Informacion")
                .setMessage(strMensaje)
                .setPositiveButton("OK", null)
        //.setNegativeButton("Cancelar", null)
        ;
        return buldier.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void imprimirEtiqueta2(String Pedido1, String strNomForPag, String strCiuDest, String strNomDest,
                                  String strDirDest, String strCiudadOri, String strNomcli, String strDircli,
                                  String Cantidad, String strNomPrd, String cantidadTotal, String strPuertaEmbarque)
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
        byte[] bb8 = new byte[]{0x1B,0x21, 0x79};
        byte[] bb9 = new byte[]{0x1B, 0x4A, 127};

        ImpresionRotulo.ThreadConnected myThreadConnected = bluetoothThread();

        //myThreadConnected.write(bb4);

        try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 390, 70, false);
            if(bmp!=null)
            {
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
        byte[] byteToEmbarque = ("    WhatsApp: 3156770401 - 3155453978").toString().getBytes();
        myThreadConnected.write(byteToEmbarque);
        byte[] NewToEmbarque = "\n".getBytes();
        myThreadConnected.write(NewToEmbarque);

        // numero de la guia mas grande
        myThreadConnected.write(bb4);

        byteToEmbarque = ("  Guia: " + strPedido).toString().getBytes();
        myThreadConnected.write(byteToEmbarque);
        myThreadConnected.write(NewToEmbarque);

        myThreadConnected.write(cc);

        if(myThreadConnected!=null){

            myThreadConnected.write(bb5);

            try {

                imgImpresionQR.setImageBitmap(bmp1);
                myBitmap = captureScreen(lytdesing);
                imgfondo.setImageBitmap(myBitmap);

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

            myThreadConnected.write(bb4);
            byte[] bytesTodNomDest = ("Dest." + strCiuDest).toString().getBytes();
            myThreadConnected.write(bytesTodNomDest);
            byte[] NewLineNomDest = "\n".getBytes();
            myThreadConnected.write(NewLineNomDest);

            myThreadConnected.write(cc);
            byte[] bytesTodNomDestin = (strNomDest).toString().getBytes();
            myThreadConnected.write(bytesTodNomDestin);
            byte[] NewLineNomDestin = "\n".getBytes();
            myThreadConnected.write(NewLineNomDestin);

            byte[] bytesTodDireDest = ("Dir. "+strDirDest).toString().getBytes();
            myThreadConnected.write(bytesTodDireDest);
            byte[] NewLineDirDest = "\n\n".getBytes();
            myThreadConnected.write(NewLineDirDest);
            /*
            byte[] bytesTodOrigen = ("ORIGEN "+ strCiudadOri).toString().getBytes();
            myThreadConnected.write(bytesTodOrigen);
            byte[] NewLineOrigen = "\n".getBytes();
            myThreadConnected.write(NewLineOrigen);

            byte[] bytesTodCiudOri = ().toString().getBytes();
            myThreadConnected.write(bytesTodCiudOri);
            byte[] NewLineCiudOri = "\n".getBytes();
            myThreadConnected.write(NewLineCiudOri);

            byte[] bytesTodDes = ("DESTINO").toString().getBytes();
            myThreadConnected.write(bytesTodDes);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesTodCiudDes = (strCiuDest).toString().getBytes();
            myThreadConnected.write(bytesTodCiudDes);
            byte[] NewLineCiudDest = "\n\n".getBytes();
            myThreadConnected.write(NewLineCiudDest);
            */
            myThreadConnected.write(cc);
            byte[] bytesTodNomRemi = ("Rte. "+strNomcli).toString().getBytes();
            myThreadConnected.write(bytesTodNomRemi);
            byte[] NewLineRemi= "\n".getBytes();
            myThreadConnected.write(NewLineRemi);

            byte[] bytesTodDireRemi = ("Dir. "+strDircli).toString().getBytes();
            myThreadConnected.write(bytesTodDireRemi);
            byte[] NewLineDirRemi = "\n".getBytes();
            myThreadConnected.write(NewLineDirRemi);

            byte[] bytesTodPrd= ("Producto. "+ strNomPrd).toString().getBytes();
            myThreadConnected.write(bytesTodPrd);
            byte[] NewLinePrd = "\n".getBytes();
            myThreadConnected.write(NewLinePrd);

            myThreadConnected.write(bb3);
            byte[] bytesTodPiezas= ("Piezas "+ Cantidad +"/"+cantidadTotal).toString().getBytes();
            myThreadConnected.write(bytesTodPiezas);
            byte[] NewLinePiezas = "\n".getBytes();
            myThreadConnected.write(NewLinePiezas);

            myThreadConnected.write(bb4);
            try {

                byte[] decodedString = Base64.decode(strPuertaEmbarque, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgAlfa.setImageBitmap(decodedByte);
                Bitmap bmpPrueba = Bitmap.createScaledBitmap(((BitmapDrawable) imgAlfa.getDrawable()).getBitmap(), 390, 100, false);
                if(decodedByte!=null)
                {
                    byte[] command0 = decodeBitmap(decodedByte);
                    myThreadConnected.write(command0);
                    byte[] NewLine177 = "\n".getBytes();
                    myThreadConnected.write(NewLine177);

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("PrintTools", "the file isn't exists");
            }

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

        }
    }
}