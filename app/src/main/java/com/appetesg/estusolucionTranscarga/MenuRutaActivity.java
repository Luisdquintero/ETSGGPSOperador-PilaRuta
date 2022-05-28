package com.appetesg.estusolucionTranscarga;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.modelos.MenuPrincipal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class MenuRutaActivity extends AppCompatActivity {
    GridView mGridView;
    ArrayList<MenuPrincipal> items = new ArrayList<>();
    Toolbar toolbar;
    static String TAG= MenuRutaActivity.class.getName();
    SharedPreferences sharedPreferences;

    public static RelativeLayout rlCnn;
    public static TextView lblConnMenu;


    @Override
    public void onDestroy() {
        super.onDestroy();

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
        setContentView(R.layout.activity_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuRutaActivity.this, ListaRutaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);

        lblTextoToolbar.setText("Menú de ruta - App SisColint "+ getResources().getString(R.string.versionApp));
        mGridView = (GridView) findViewById(R.id.gdvPrincipal);
        lblConnMenu = (TextView) findViewById(R.id.lblConnMenu);
        rlCnn = (RelativeLayout) findViewById(R.id.rlCnn);
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        GPSActivo();
        //0
        items.add(new MenuPrincipal(1,R.drawable.casa,"Recoger (mañana)"));
        //10
        items.add(new MenuPrincipal(2,R.drawable.escolar,"Dejados (mañana)"));

        //901
        items.add(new MenuPrincipal(3,R.drawable.colegio,"Recoger (tarde)"));
        //903
        items.add(new MenuPrincipal(4,R.drawable.escolartarde,"Dejados (tarde)"));

        //startService(new Intent(this, MonitoreoService.class));
        MenuAdapter adapter = new MenuAdapter(this,items);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = mGridView.getItemAtPosition(position);
                MenuPrincipal m = (MenuPrincipal) object;
                if (m.getId() == 1) {
                    Intent intent = new Intent(MenuRutaActivity.this, ListaEstudiantes0Activity.class);
                    startActivity(intent);
                }
                if (m.getId() == 2) {
                    Intent intent = new Intent(MenuRutaActivity.this, ListaEstudiantes10Activity.class);
                    startActivity(intent);
                }

                if (m.getId() == 3) {
                    Intent intent = new Intent(MenuRutaActivity.this, ListaEstudiantes901Activity.class);
                    startActivity(intent);
                }

                if (m.getId() == 4) {
                    Intent intent = new Intent(MenuRutaActivity.this, ListaEstudiantes903Activity.class);
                    startActivity(intent);
                }


            }
            });


    }

    @Override
    public void onBackPressed() {
   }


    private boolean svcEjecutando(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void GPSActivo(){
        try{
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
            if(gpsSignal==0){
                showGPSAlert();
            }
        }catch (Settings.SettingNotFoundException ex){
            ex.printStackTrace();
        }
    }

    private void showGPSAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Señal GPS")
                .setMessage("No tiene activo el GPS")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
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
