package com.appetesg.estusolucionTranscarga;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ImpresionGuia extends AppCompatActivity {

        private static final int REQUEST_ENABLE_BT = 1;
        Bitmap myBitmap;
        ImageView imgLogo, imgAlfa;
        Bitmap bmp1;
        //Toolbar toolbar;
        private int size = 180;
        private int size_width = 390;
        private int size_height = 70;
        private static String hexStr = "0123456789ABCDEF";
        boolean conectado = false;
        private static String[] binaryArray = {"0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111"};
        BluetoothAdapter bluetoothAdapter;
        ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
        public AlertDialog alert;
        private UUID myUUID;

        private final String UUID_STRING_WELL_KNOWN_SPP =
                "00001101-0000-1000-8000-00805F9B34FB";
        ThreadConnectBTdevice myThreadConnectBTdevice;
        ThreadConnected myThreadConnected;

        ArrayList<BluetoothDevice> pairedDeviceArrayList;
        ListView listViewPairedDevice;
        ImageView imgfondo, imgImpresionQR;
        TextView txtImpresionCiudad, txtCiudadDestinoImpresion;
        SharedPreferences sharedPreferences;
        String BASE_URL, PREFS_NAME;
        int intCodusu, intCantidad;
        String strPedido, strCiudadDest, strFormaPago, strNomDest, strDirDest, strNomCli, strDirCli, strCiudadOrigen,
                strCelcli, strNomprd, strContenido, strValorEnvio, strPesoPaq, strValDec;
        LinearLayout lytdesing;

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
        setContentView(R.layout.activity_impresion);
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME,0);
        strPedido = sharedPreferences.getString("Metodo", "");
        intCodusu = sharedPreferences.getInt("intCodusuImpresionRotulo", 0);
        intCantidad = sharedPreferences.getInt("intCantidadImpresionRotulo", 0);
        //strPedido = sharedPreferences.getString("strPedido1ImpresionRotulo", "");
        strFormaPago = sharedPreferences.getString("strFormaPagoImpresionRotulo", "");
        strCiudadDest = sharedPreferences.getString("strCiudadDestinoImpresionRotulo", "");
        strNomDest = sharedPreferences.getString("strNomDestImpresionRotulo", "");
        strDirDest = sharedPreferences.getString("strDireccionDestImpresionRotulo", "");
        strNomCli = sharedPreferences.getString("strNomcliImpresionRotulo", "");
        strDirCli = sharedPreferences.getString("strDirOriImpresionRotulo", "");
        strCiudadOrigen = sharedPreferences.getString("strCiudadOrigenImpresionRotulo", "");
        strCelcli = sharedPreferences.getString("strCelcliImpresion", "");
        strNomprd = sharedPreferences.getString("strNomprdImpresion", "");
        strContenido = sharedPreferences.getString("strContenidoImpresion", "");
        strValorEnvio = sharedPreferences.getString("strValorEnvioImpresion", "");
        strPesoPaq = sharedPreferences.getString("strPesoPaqImpresion", "");
        strValDec = sharedPreferences.getString("strValorEnvioImpresion", "");

        if(strPedido.equalsIgnoreCase("Reimpresion")){
            Load_ImprimirGuia(strCiudadDest,"", strNomCli, strDirCli, strCelcli,
                    strNomDest, strDirDest, strNomprd, strPesoPaq, strValorEnvio,
                    String.valueOf(intCantidad), strFormaPago, "strValDec", strContenido, "strValorFlete");
        }
    }
        ImpresionGuia(){
                /*
            PREFS_NAME = this.getString(R.string.SPREF);
            sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);

            imgfondo = (ImageView) findViewById(R.id.imgfondo);
            lytdesing = (LinearLayout) findViewById(R.id.lytdesignRotulo);

            imgLogo = (ImageView) findViewById(R.id.imagenHistoricoRotulo);
            imgAlfa = (ImageView) findViewById(R.id.imagenAlfanumericoHistoricoRotulo);
            txtImpresionCiudad = (TextView) findViewById(R.id.txtImpresionCiudad);
            txtCiudadDestinoImpresion = (TextView) findViewById(R.id.txtCiudadDestinoImpresion);
            imgImpresionQR = (ImageView) findViewById(R.id.imgeImpresionQR);

            intCodusu = sharedPreferences.getInt("intCodusuImpresionRotulo", 0);
            intCantidad = sharedPreferences.getInt("intCantidadImpresionRotulo", 0);
            strPedido = sharedPreferences.getString("strPedido1ImpresionRotulo", "");
            strFormaPago = sharedPreferences.getString("strFormaPagoImpresionRotulo", "");
            strCiudadDest = sharedPreferences.getString("strCiudadDestinoImpresionRotulo", "");
            strNomDest = sharedPreferences.getString("strNomDestImpresionRotulo", "");
            strDirDest = sharedPreferences.getString("strDireccionDestImpresionRotulo", "");
            strNomCli = sharedPreferences.getString("strNomcliImpresionRotulo", "");
            strDirCli = sharedPreferences.getString("strDirOriImpresionRotulo", "");
            strCiudadOrigen = sharedPreferences.getString("strCiudadOrigenImpresionRotulo", "");
            strCelcli = sharedPreferences.getString("strCelcliImpresion", "");
            strNomprd = sharedPreferences.getString("strNomprdImpresion", "");
            strContenido = sharedPreferences.getString("strContenidoImpresion", "");
            strValorEnvio = sharedPreferences.getString("strValorEnvioImpresion", "");
            strPesoPaq = sharedPreferences.getString("strPesoPaqImpresion", "");
            strValDec = sharedPreferences.getString("strValorEnvioImpresion", "");

            try {
                bmp1 = CreateImage(strPedido, "QR Code");

                txtCiudadDestinoImpresion.setText(strCiudadDest);

                imgImpresionQR.setImageBitmap(bmp1);

                lytdesing.post(new Runnable() {
                    public void run() {

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

                    }
                });
            } catch (WriterException e) {
                e.printStackTrace();
            }*/
        }

    public void Load_ImprimirGuia(String strNomCiuDesI, String strPedido1, String strNomCliI, String strDicliI, String strCelCliI,
                                  String strNombreDestI, String strDirDestI, String strProductoI, String strPesoI, String strValorI,
                                  String strCantidadI, String strNomFormaI, String strValorS, String strContenidoF, String strValorG){
        if (conectado) {
            imprimirRegistro(strNomCiuDesI, "", strNomCliI, strDicliI, strCelCliI,
                    strNombreDestI, strDirDestI, strProductoI, strPesoI, strValorI,
                    String.valueOf(strCantidadI), strNomFormaI, strValorS, strContenidoF, strValorG);

        } else {
            load_conectar();
        }
    }

    public void Load_ImprimirRotulo(String strPedido, String strFormaPago, String strCiudadDest, String strNomDest, String strDirDest,
                                  String strCiudadOrigen, String strNomCli, String strDirCli, String strValorCiclo){
        if (conectado) {
            imprimirEtiqueta2(strPedido, strFormaPago, strCiudadDest,
                    strNomDest, strDirDest, strCiudadOrigen,
                    strNomCli, strDirCli, strValorCiclo);
        } else {
            load_conectar();
        }
    }

    public void lanza_alert() {
        LayoutInflater layoutInflater = LayoutInflater.from(ImpresionGuia.this);

        View promptView = layoutInflater.inflate(R.layout.layout_dispositivos, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ImpresionGuia.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        alertDialogBuilder.setView(promptView);
        alert = alertDialogBuilder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);

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

                    listViewPairedDevice.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // myThreadConnected.cancel();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startThreadConnected(BluetoothSocket socket) {

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
                        Toast.makeText(ImpresionGuia.this, "Conectado correctamente", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ImpresionGuia.this, "No se pudo conectar al dispositivo, intente nuevamente", Toast.LENGTH_LONG).show();

                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
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

            } else {
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

                while (true) //// aqui inicia el bucle
                {
                    try {
                        bytes = connectedInputStream.read(buffer);
                        final String strReceived = new String(buffer, 0, bytes);
                        final String strByteCnt = String.valueOf(bytes);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                            }
                        });

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        final String msgConnectionLost = "Connection lost:\n"
                                + e.getMessage();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(ImpresionGuia.this, "Se perdio la conexión", Toast.LENGTH_LONG).show();
                            }
                        });
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
                    Toast.makeText(ImpresionGuia.this, "Conexión cerrada", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        public static byte[] decodeBitmap(Bitmap bmp) {
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
            commandList.add(commandHexString + widthHexString + heightHexString);
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

        public Bitmap CreateImage(String message, String type) throws WriterException {
            BitMatrix bitMatrix = null;
            // BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
            switch (type) {
                case "QR Code":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                    break;
                case "Barcode":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, size_width, size_height);
                    break;
                case "Data Matrix":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.DATA_MATRIX, size, size);
                    break;
                case "PDF 417":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.PDF_417, size_width, size_height);
                    break;
                case "Barcode-39":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_39, size_width, size_height);
                    break;
                case "Barcode-93":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_93, size_width, size_height);
                    break;
                case "AZTEC":
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.AZTEC, size, size);
                    break;
                default:
                    bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                    break;
            }
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (bitMatrix.get(j, i)) {
                        pixels[i * width + j] = 0xff000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }

        public void imprimirEtiqueta2(String Pedido1, String strNomForPag, String strCiuDest, String strNomDest,
                                      String strDirDest, String strCiudadOri, String strNomcli, String strDircli, String Cantidad) {

            //aqui esta las opciones o instrucciones estas se ejecutan antes
            //para dar la orden del que texto a imprimir debe aplicarlesele ese formato
            byte[] cc = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
            byte[] bb4 = new byte[]{0x1B, 0x21, 0x39};
            byte[] bb5 = new byte[]{0x1B, 0x21, 0x29};
            byte[] bb6 = new byte[]{0x1B, 0x21, 0x09};
            byte[] bb7 = new byte[]{0x1B, 0x21, 0x45};
            byte[] bb8 = new byte[]{0x1B, 0x21, 0x70};
            byte[] bb9 = new byte[]{0x1B, 0x4A, 127};

            myThreadConnected.write(bb4);

            try {

                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgLogo.getDrawable()).getBitmap(), 390, 70, false);
                if (bmp != null) {
                    System.out.println("fallo1");
                    byte[] command0 = decodeBitmap(bmp);
                    myThreadConnected.write(command0);
                    //byte[] NewLine177 = "\n".getBytes();
                    //myThreadConnected.write(NewLine177);

                } else {

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

            myThreadConnected.write(cc);

            if (myThreadConnected != null) {

                myThreadConnected.write(bb4);

                byte[] bytesToSend2 = ("Guia No:" + Pedido1).toString().getBytes();
                myThreadConnected.write(bytesToSend2);
                byte[] NewLine2 = "\n".getBytes();
                myThreadConnected.write(NewLine2);

                myThreadConnected.write(bb8);
                byte[] bytesTodForm = ("  " + strNomForPag).toString().getBytes();
                myThreadConnected.write(bytesTodForm);
                byte[] NewLineForm = "\n".getBytes();
                myThreadConnected.write(NewLineForm);

          /*  myThreadConnected.write(bb4);
            byte[] bytesTodDes = ("DESTINO").toString().getBytes();
            myThreadConnected.write(bytesTodDes);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesTodCiudDes = (strCiuDest).toString().getBytes();
            myThreadConnected.write(bytesTodCiudDes);
            byte[] NewLineCiudDest = "\n\n".getBytes();
            myThreadConnected.write(NewLineCiudDest);*/
                myThreadConnected.write(bb5);

                try {

                    Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) imgfondo.getDrawable()).getBitmap(), 530, 150, false);
                    if (bmp != null) {
                        byte[] command0 = decodeBitmap(bmp);
                        myThreadConnected.write(command0);

                    } else {

                        Log.e("Print Photo error", "the file isn't exists");
                    }
                } catch (Exception e) {
                    e.printStackTrace();


                    Log.e("PrintTools", "the file isn't exists");
                }

                myThreadConnected.write(cc);
                byte[] bytesTodNomDest = ("Dest. " + strNomDest).toString().getBytes();
                myThreadConnected.write(bytesTodNomDest);
                byte[] NewLineNomDest = "\n".getBytes();
                myThreadConnected.write(NewLineNomDest);

                byte[] bytesTodDireDest = ("Dir. " + strDirDest).toString().getBytes();
                myThreadConnected.write(bytesTodDireDest);
                byte[] NewLineDirDest = "\n".getBytes();
                myThreadConnected.write(NewLineDirDest);

                myThreadConnected.write(bb4);
                byte[] bytesTodOrigen = ("ORIGEN").toString().getBytes();
                myThreadConnected.write(bytesTodOrigen);
                byte[] NewLineOrigen = "\n".getBytes();
                myThreadConnected.write(NewLineOrigen);

                byte[] bytesTodCiudOri = (strCiudadOri).toString().getBytes();
                myThreadConnected.write(bytesTodCiudOri);
                byte[] NewLineCiudOri = "\n".getBytes();
                myThreadConnected.write(NewLineCiudOri);
            /*byte[] bytesTodDes = ("DESTINO").toString().getBytes();
            myThreadConnected.write(bytesTodDes);
            byte[] NewLineDest = "\n".getBytes();
            myThreadConnected.write(NewLineDest);

            byte[] bytesTodCiudDes = (strCiuDest).toString().getBytes();
            myThreadConnected.write(bytesTodCiudDes);
            byte[] NewLineCiudDest = "\n\n".getBytes();
            myThreadConnected.write(NewLineCiudDest);
*/
                myThreadConnected.write(cc);
                byte[] bytesTodNomRemi = ("Rte. " + strNomcli).toString().getBytes();
                myThreadConnected.write(bytesTodNomRemi);
                byte[] NewLineRemi = "\n".getBytes();
                myThreadConnected.write(NewLineRemi);

                byte[] bytesTodDireRemi = ("Dir. " + strDircli).toString().getBytes();
                myThreadConnected.write(bytesTodDireRemi);
                byte[] NewLineDirRemi = "\n".getBytes();
                myThreadConnected.write(NewLineDirRemi);


                byte[] bytesTodPiezas = ("Piezas " + Cantidad).toString().getBytes();
                myThreadConnected.write(bytesTodPiezas);
                byte[] NewLinePiezas = "\n".getBytes();
                myThreadConnected.write(NewLinePiezas);

                myThreadConnected.write(bb4);

                try {

                    Bitmap bmpPrueba = Bitmap.createScaledBitmap(((BitmapDrawable) imgAlfa.getDrawable()).getBitmap(), 390, 100, false);
                    if (bmpPrueba != null) {
                        byte[] command0 = decodeBitmap(bmpPrueba);
                        myThreadConnected.write(command0);
                    /*byte[] NewLine177 = "\n".getBytes();
                    myThreadConnected.write(NewLine177);
*/

                    } else {

                        Log.e("Print Photo error", "the file isn't exists");
                    }
                } catch (Exception e) {
                    e.printStackTrace();


                    Log.e("PrintTools", "the file isn't exists");
                }

                myThreadConnected.write(bb7);
                byte[] bytesLink = ("transcargamundial.com/terminosycondiciones").toString().getBytes();
                myThreadConnected.write(bytesLink);
                byte[] NewLineLink = "\n\n\n\n".getBytes();
                myThreadConnected.write(NewLineLink);

                //salimos  del activity
                //finish();
            }


        }


    public void imprimirRegistro(String strNomCiuDesI, String strPedido1, String strNomCliI, String strDicliI, String strCelCliI,
                                 String strNombreDestI, String strDirDestI, String strProductoI, String strPesoI, String strValorI,
                                 String strCantidadI, String strNomFormaI, String strValorS, String strContenidoF, String strValorG)
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

            /*try {

                Bitmap bmpPrueba = Bitmap.createScaledBitmap(((BitmapDrawable) imgAlfa.getDrawable()).getBitmap(), 390, 70, false);
                if(bmpPrueba!=null)
                {
                    byte[] command0 = decodeBitmap(bmpPrueba);
                    myThreadConnected.write(command0);

                }else{

                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("PrintTools", "the file isn't exists");
            }*/

            byte[] bytesFirma = ("Firma:").toString().getBytes();
            myThreadConnected.write(bytesFirma);
            byte[] NewLinefirma= "\n\n".getBytes();
            myThreadConnected.write(NewLinefirma);

            myThreadConnected.write(bb7);
            byte[] byteslinea = ("_________________________________________").toString().getBytes();
            myThreadConnected.write(byteslinea);
            byte[] NewLinelinea  = "\n".getBytes();
            myThreadConnected.write(NewLinelinea);

            myThreadConnected.write(bb7);
            byte[] bytesLink = ("transcargamundial.com/terminosycondiciones").toString().getBytes();
            myThreadConnected.write(bytesLink);
            byte[] NewLineLink  = "\n".getBytes();
            myThreadConnected.write(NewLineLink);

            //salimos  del activity

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

            if (v != null) {

                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }

        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
        }

        return screenshot;
    }

    public static void saveImage(Bitmap bitmap) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/H2HOY");
        if (!dir.exists()) {

            dir.mkdirs();
            System.out.println("creo carpeta");
        }
        File f = new File(dir.getAbsolutePath() + "/test-h2hoy.png");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
    }

    public void load_conectar() {
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

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

        setup();
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