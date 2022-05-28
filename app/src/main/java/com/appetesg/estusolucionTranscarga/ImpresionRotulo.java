package com.appetesg.estusolucionTranscarga;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.appetesg.estusolucionTranscarga.databinding.ActivityImpresionRotuloBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ImpresionRotulo extends AppCompatActivity {

    private ActivityImpresionRotuloBinding binding;
    public ImageButton imgButtonRe;
    public FloatingActionButton btnBluetooth;

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    public AlertDialog alert;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    ThreadConnectBTdevice myThreadConnectBTdevice;
    static ThreadConnected  myThreadConnected;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    ListView listViewPairedDevice;

    SharedPreferences sharedPreferences;
    String BASE_URL,PREFS_NAME;

    static boolean conectado= false;

    @Override
    public void onBackPressed() {
        //NO HACE NADA AL OPRIMIR
    }

    public static ThreadConnected bluetoothThread(){
        return myThreadConnected;
    }

    public static boolean conectado(){
        return conectado;
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

        binding = ActivityImpresionRotuloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        BASE_URL = sharedPreferences.getString("urlColegio","");

        imgButtonRe = findViewById(R.id.regresar);
        btnBluetooth = findViewById(R.id.BtnFltBlue);

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_conectar();
            }
        });

        imgButtonRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImpresionRotulo.this, HistoricoGuia.class);
                finishC();
                finish();
                startActivity(intent);
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_impresion_rotulo);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        load_conectar();
    }


    // Conecta el bluetooth
    public void load_conectar()
    {
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth NO SOPORTADO",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //String stInfo = bluetoothAdapter.getName() + "\n" +  bluetoothAdapter.getAddress();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            //descomentar
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        }
        lanza_alert();

        setup();
    }

    // Muestra los dispositivos bluetooth disponibles
    public void lanza_alert()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.layout_dispositivos, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        alertDialogBuilder.setView(promptView);
        alert = alertDialogBuilder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);

        listViewPairedDevice = promptView.findViewById(R.id.list_dispositivos);
    }

    // Realiza conexion bluetooth
    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices) {
                if(device.getAddress().toString().equals(sharedPreferences.getString("mac","")))
                {
                    Toast.makeText(this,
                            "Conectando con el dispositivo...",
                            Toast.LENGTH_SHORT).show();

                    System.out.println(" CONEXIÓN AUTOMATICA ");

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();

                    listViewPairedDevice.setVisibility(View.GONE);
                }
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener((parent, view, position, id) -> {
                BluetoothDevice device =
                        (BluetoothDevice) parent.getItemAtPosition(position);
                System.out.println("CLIC BLE "+((BluetoothDevice) parent.getItemAtPosition(position)).getAddress().toString());

                SharedPreferences.Editor ed =  sharedPreferences.edit();
                ed.putString("mac",((BluetoothDevice) parent.getItemAtPosition(position)).getAddress().toString());
                ed.commit();

                myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                myThreadConnectBTdevice.start();

                listViewPairedDevice.setVisibility(View.GONE);
            });
        }
    }

    // termina la conexion
    public void finishC()
    {
        try {
            if(myThreadConnected != null)
                myThreadConnected.connectedBluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth no disponible.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startThreadConnected(BluetoothSocket socket)
    {
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
                runOnUiThread(() -> {

                    listViewPairedDevice.setVisibility(View.GONE);
                    Toast.makeText(ImpresionRotulo.this,"Conectado correctamente",Toast.LENGTH_LONG).show();
                    conectado = true;

                });

            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();

                runOnUiThread(() -> {
                    //alert.cancel();
                    Toast.makeText(ImpresionRotulo.this,"No se pudo conectar al dispositivo, intente nuevamente",Toast.LENGTH_LONG).show();
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

                runOnUiThread(() -> alert.cancel());

                startThreadConnected(bluetoothSocket);

            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(ImpresionRotulo.this,
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
    public class ThreadConnected extends Thread {
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

            while (myThreadConnected.connectedBluetoothSocket.isConnected()) //// aqui inicia el bucle
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

                    // Mensaje con la excepcion
                    final String msgConnectionLost = "Connection lost:\n" + e.getMessage();

                    runOnUiThread(() -> Toast.makeText(ImpresionRotulo.this,"Se termino la conexión",Toast.LENGTH_LONG).show());
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
                Toast.makeText(ImpresionRotulo.this,"Conexión cerrada",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        //LogErrorDB.LogError(sharedPreferences.getInt("idUsuario",0),errors.toString(), this.getClass().getCanonicalName(), BASE_URL, this);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }

}