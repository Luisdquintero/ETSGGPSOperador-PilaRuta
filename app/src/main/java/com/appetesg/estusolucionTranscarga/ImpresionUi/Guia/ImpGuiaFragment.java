package com.appetesg.estusolucionTranscarga.ImpresionUi.Guia;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.appetesg.estusolucionTranscarga.ImpresionRotulo;
import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.databinding.FragmentGuiaImpBinding;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.bluetoothThread;
import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.conectado;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.CreateImage;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.captureScreen;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.decodeBitmap;
import static com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil.saveImage;

public class ImpGuiaFragment extends Fragment {

    private FragmentGuiaImpBinding binding;

    static String TAG = "ImpresionGuiaQR";

    // Pantalla
    Button btnGuia;
    TextView txtImpresionCiudad, txtCiudadDestinoImpresion, txtImpresionPago, txtRemitenteGuia, txtRemitenteDirGuia, txtDestinoGuia, txtRemCelGuia, txtDestinatarioGuia, txtDirDestiGuia, txtDestiCelGuia, txtContenidoGuia, txtTotalGuia;
    ImageView imgLogo, imgImpresionQR, imgfondo;
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

    SharedPreferences sharedPreferences;
    String BASE_URL,PREFS_NAME;

    public AlertDialog alert;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGuiaImpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnGuia = root.findViewById(R.id.btnreimpresionImp);
        imgfondo = root.findViewById(R.id.imgfondoImp);
        lytdesing = root.findViewById(R.id.lytdesignRotulo);
        txtImpresionCiudad = root.findViewById(R.id.txtImpresionCiudad);
        txtCiudadDestinoImpresion = root.findViewById(R.id.txtCiudadDestinoImpresion);
        txtImpresionPago = root.findViewById(R.id.txtImpresionPago);
        imgImpresionQR = root.findViewById(R.id.imgeImpresionQRGuia);
        imgLogo = root.findViewById(R.id.imgLogoGuia);

        txtRemitenteGuia = root.findViewById(R.id.txtRemGuia);
        txtRemitenteDirGuia = root.findViewById(R.id.txtRemDirGuia);
        txtDestinoGuia = root.findViewById(R.id.txtDestGuia);
        txtRemCelGuia = root.findViewById(R.id.txtRemCelGuia);
        txtDestinatarioGuia = root.findViewById(R.id.txtDestinatarioGuia);
        txtDirDestiGuia = root.findViewById(R.id.txtDirDestiGuia);
        txtDestiCelGuia = root.findViewById(R.id.txtDestiCelGuia);
        txtContenidoGuia = root.findViewById(R.id.txtContenidoGuia);
        txtTotalGuia = root.findViewById(R.id.txtTotalGuia);

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
        txtRemitenteDirGuia.setText(strDirCli);
        txtDestinoGuia.setText(strCiudadDest);
        txtRemCelGuia.setText(strCelcli);
        txtDestinatarioGuia.setText(strNomDest);
        txtDirDestiGuia.setText(strDirDest);
        txtDestiCelGuia.setText(strCelDes);
        txtContenidoGuia.setText(strContenido);
        txtTotalGuia.setText(strValorEnvio);

        // FORMATO INICIO GUIA
        try {
            // YA NO, SUSPENDIDO CANTIDAD DE PIEZAS DEBIDO A QUE EN LA WEB NO ESTA EL AJUSTE
            //bmp1 = CreateImage(strPedido + "/" + intCantidad, "QR Code");
            bmp1 = CreateImage(strPedido, "QR Code");

            txtCiudadDestinoImpresion.setText("Guia: " + strPedido);
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
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btnGuia.setOnClickListener(view -> {
            btnGuia.setEnabled(false);
            if(NetworkUtil.hayInternet(this.getActivity())) {
                if(conectado()) {
                    dialorInformativo("No cierre la ventana, espere que acabe la impresion").show();
                    imprimirRegistro(strCiudadDest, strPedido, strNomCli, strDirCli, strCelcli, strNomDest, strDirDest,
                            strNomprd, strPesoPaq, strValorEnvio , String.valueOf(intCantidad), strFormaPago, strValDec ,
                            strContenido, strValorFlete, strValDecIni, strCelDes);
                    btnGuia.setEnabled(true);
                }
                else
                {
                    //load_conectar();
                    Toast.makeText(getContext(), "Conecta el dispositivo bluetooth.", Toast.LENGTH_LONG).show();
                    btnGuia.setEnabled(true);
                }
            }
            else
            {
                Toast.makeText(getContext(), "Sin conexion a internet..", Toast.LENGTH_SHORT).show();
                btnGuia.setEnabled(true);
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

    public void imprimirRegistro(String strNomCiuDesI, String strPedido1, String strNomCliI, String strDicliI, String strCelCliI,
                                 String strNombreDestI, String strDirDestI, String strProductoI, String strPesoI, String strValorI,
                                 String strCantidadI, String strNomFormaI, String strValorS, String strContenidoF, String strValorG,
                                 String strValDecIni, String strCelDes)
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
        ImpresionRotulo.ThreadConnected myThreadConnected = bluetoothThread();

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

            imgImpresionQR.setImageBitmap(bmp1);
            myBitmap = captureScreen(lytdesing);
            imgfondo.setImageBitmap(myBitmap);

            try {

                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgfondo.getDrawable()).getBitmap(), 600, 150, false);
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

            myThreadConnected.write(cc);

            byte[] bytesToRemi = ("RM. "+strNomCliI).toString().getBytes();
            myThreadConnected.write(bytesToRemi);
            byte[] NewLineRemi = "\n".getBytes();
            myThreadConnected.write(NewLineRemi);

            byte[] bytesToRemiDir = ("Dir. " + strDicliI).toString().getBytes();
            myThreadConnected.write(bytesToRemiDir);
            byte[] NewLineRemiDir = "\n".getBytes();
            myThreadConnected.write(NewLineRemiDir);

            myThreadConnected.write(bb);
            byte[] bytesToSend = ("DEST: "+ strNomCiuDesI).getBytes();
            myThreadConnected.write(bytesToSend);
            byte[] NewLine = "\n".getBytes();
            myThreadConnected.write(NewLine);

            myThreadConnected.write(cc);
            byte[] bytesToDest = (strNombreDestI).getBytes();
            myThreadConnected.write(bytesToDest);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesToDestDir = ("Dir. "+ strDirDestI).toString().getBytes();
            myThreadConnected.write(bytesToDestDir);
            byte[] NewLineDestiDir = "\n".getBytes();
            myThreadConnected.write(NewLineDestiDir);

            byte[] bytesToRemiTel = ("Cel. "+ strCelDes).toString().getBytes();
            myThreadConnected.write(bytesToRemiTel);
            byte[] NewLineRemiTel = "\n".getBytes();
            myThreadConnected.write(NewLineRemiTel);

            /*byte[] bytesToDestProd = ("Prod. "+ strProductoI).toString().getBytes();
            myThreadConnected.write(bytesToDestProd);
            byte[] NewLineDestProd = "\n".getBytes();
            myThreadConnected.write(NewLineDestProd);
            */

            byte[] bytesTodDestValFlet = ("Flete . "+ strValorG).toString().getBytes();
            myThreadConnected.write(bytesTodDestValFlet);
            byte[] NewLineDestValFlet  = "\n".getBytes();
            myThreadConnected.write(NewLineDestValFlet);

            byte[] bytesTodDestValTDec = ("VR Declarado . " + strValDecIni).toString().getBytes();
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

            //FECHA
            Date d = new Date();
            CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());

            //byte[] bytesTodDestPeso = ("Peso. "+ strPesoI + "       " + s).toString().getBytes();
            byte[] bytesTodDestPeso = (s).toString().getBytes();
            myThreadConnected.write(bytesTodDestPeso);
            byte[] NewLineDestPeso  = "\n".getBytes();
            myThreadConnected.write(NewLineDestPeso);

            myThreadConnected.write(bb4);
            byte[] bytesTodPiezas = ("    PIEZAS "+ strCantidadI).toString().getBytes();
            myThreadConnected.write(bytesTodPiezas);
            byte[] NewLinePiezas = "\n".getBytes();
            myThreadConnected.write(NewLinePiezas);

            myThreadConnected.write(bb2);

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
        }
    }

}