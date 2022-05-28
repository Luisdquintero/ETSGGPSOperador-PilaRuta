package com.appetesg.estusolucionTranscarga.ImpresionUi.Cliente;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.appetesg.estusolucionTranscarga.databinding.FragmentClienteImpBinding;
import com.appetesg.estusolucionTranscarga.utilidades.BluetoothUtil;
import com.appetesg.estusolucionTranscarga.utilidades.NetworkUtil;
import com.google.zxing.WriterException;

import java.io.IOException;

import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.bluetoothThread;
import static com.appetesg.estusolucionTranscarga.ImpresionRotulo.conectado;

public class ImpClienteQRFragment extends Fragment {

    static String TAG = "ImpresionClienteQR";

    private FragmentClienteImpBinding binding;

    // Pantalla
    Button btnQR;
    TextView txtDirImp, txtClienteImp, txtCelImp, txtCodImp;
    ImageView imgLogo, imgImpresionQR, imgRecogida, imgQR;
    Bitmap bmp2, myBitmap;
    LinearLayout lytdesingQR;

    // Datos cliente
    int intCodCli;
    String strNomCli, strDirCli, strCelcli;

    SharedPreferences sharedPreferences;
    String BASE_URL,PREFS_NAME;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentClienteImpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnQR = root.findViewById(R.id.btnQrClienteImp);
        imgLogo = root.findViewById(R.id.imagenHistoricoRotuloImp);
        lytdesingQR = root.findViewById(R.id.lytdesignQRImp);
        imgImpresionQR = root.findViewById(R.id.imgeImpresionQRImp);
        imgQR = root.findViewById(R.id.imgQRImp);
        txtCelImp = root.findViewById(R.id.txtCelImp);
        txtClienteImp = root.findViewById(R.id.txtClienteImp);
        txtCodImp = root.findViewById(R.id.txtCodImp);
        txtDirImp = root.findViewById(R.id.txtDirImp);
        imgRecogida = root.findViewById(R.id.imgRecogidaImp);

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        strNomCli = NetworkUtil.validarAnytype(sharedPreferences.getString("strNomcliImpresionRotulo", ""));
        strDirCli = NetworkUtil.validarAnytype(sharedPreferences.getString("strDirOriImpresionRotulo", ""));
        intCodCli = sharedPreferences.getInt("intCodCli", 0);
        strCelcli = NetworkUtil.validarAnytype(sharedPreferences.getString("strCelcliImpresion", ""));

        txtDirImp.setText(strDirCli);
        txtCodImp.setText(String.valueOf(intCodCli));
        txtClienteImp.setText(strNomCli);
        txtCelImp.setText(strCelcli);

        // Se genera QR con el codigo del cliente para TARJETA PRESENTACION
        try {
            bmp2 = BluetoothUtil.CreateImage(String.valueOf(intCodCli), "QR Code");

            imgImpresionQR.setImageBitmap(bmp2);

            lytdesingQR.post(() -> {

                //take screenshot
                myBitmap = BluetoothUtil.captureScreen(lytdesingQR);

                try {
                    if (myBitmap != null) {
                        //save image to SD card
                        imgQR.setImageBitmap(myBitmap);
                        BluetoothUtil.saveImage(myBitmap);
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

        btnQR.setOnClickListener(view -> {
            btnQR.setEnabled(false);
            if(NetworkUtil.hayInternet(this.getActivity())) {
                if(conectado()) {
                    dialorInformativo("No cierre la ventana, porfavor espere que termine la impresion").show();
                    imprimirQR(strNomCli, String.valueOf(intCodCli), strCelcli);
                    btnQR.setEnabled(true);
                }
                else
                {
                    //load_conectar();
                    Toast.makeText(getContext(), "Conecta el dispositivo bluetooth.", Toast.LENGTH_LONG).show();
                    btnQR.setEnabled(true);
                }
            }
            else
            {
                Toast.makeText(getContext(), "Sin conexion a internet...", Toast.LENGTH_SHORT).show();
                btnQR.setEnabled(true);
            }

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void imprimirQR(String strNomCliI, String strCodCli, String strCelCliI)
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

        ImpresionRotulo.ThreadConnected myThreadConnected = bluetoothThread();
        // myThreadConnected.write(bb4);

        // LOGO DE LA EMPRESA
        try {

            Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 390, 70, false);
            if(bmp!=null)
            {
                byte[] command0 = BluetoothUtil.decodeBitmap(bmp);
                myThreadConnected.write(command0);

            }else{

                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e("PrintTools", "the file isn't exists");
        }

        myThreadConnected.write(cc);

        byte[] NewLineRemi = "\n".getBytes();
        myThreadConnected.write(NewLineRemi);

        byte[] bytesToRemi = (" Cliente: "+strNomCliI).toString().getBytes();
        myThreadConnected.write(bytesToRemi);
        myThreadConnected.write(NewLineRemi);

        byte[] bytesToRemiTel = (" Cel. "+ strCelCliI).toString().getBytes();
        myThreadConnected.write(bytesToRemiTel);
        myThreadConnected.write(NewLineRemi);
        //myThreadConnected.write(NewLineRemi);

//        myThreadConnected.write(bb2);
//        byte[] bytesToScan = ("   ESCANEAME! ").toString().getBytes();
//        myThreadConnected.write(bytesToScan);

        myThreadConnected.write(bb5);

        if(myThreadConnected!=null){

            imgImpresionQR.setImageBitmap(bmp2);

            // CODIGO QR
            try {

                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgQR.getDrawable()).getBitmap(), 320, 210, false);
                if(bmp!=null)
                {
                    byte[] command0 = BluetoothUtil.decodeBitmap(bmp);
                    myThreadConnected.write(command0);
                    myThreadConnected.write(NewLineRemi);
                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("PrintTools", "the file isn't exists");
            }

            myThreadConnected.write(cc);

            bytesToRemiTel = ("        Codigo: "+ strCodCli).toString().getBytes();
            myThreadConnected.write(bytesToRemiTel);
            myThreadConnected.write(NewLineRemi);

            byte[] bytesToRemiQR = ("Para programar recogidas, \nEscanear el siguiente codigo QR:").toString().getBytes();
            myThreadConnected.write(bytesToRemiQR);
            myThreadConnected.write(NewLineRemi);

            // QR RECOGIDAS
            try {

                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgRecogida.getDrawable()).getBitmap(), 400, 190, false);
                if(bmp!=null)
                {
                    byte[] command0 = BluetoothUtil.decodeBitmap(bmp);
                    myThreadConnected.write(command0);

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("PrintTools", "the file isn't exists");
            }

            myThreadConnected.write(bb7);
            byte[] byteslinea = ("_________________________________________").toString().getBytes();
            myThreadConnected.write(byteslinea);
            byte[] NewLinelinea  = "\n".getBytes();
            myThreadConnected.write(NewLinelinea);

            myThreadConnected.write(bb7);
            byte[] bytesLink = ("    Impreso por www.estusolucion.com").toString().getBytes();
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

}