package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    int idUsuario=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // controlar las excepciones cuando se cierra la app
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> excepcionCapturada(thread, e));
        setContentView(R.layout.activity_splash);

        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                if (idUsuario>0) {

                    Intent mainIntent = new Intent().setClass(SplashActivity.this, MenuActivity.class);
                    startActivity(mainIntent);

                    //Intent kilometrajeItem = new Intent().setClass(SplashActivity.this, KilometrajeConductor.class);
                    //startActivity(kilometrajeItem);

                }else {
                    Intent mainIntent = new Intent().setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                }

                finish();//Destruimos esta activity para prevenir que el usuario retorne aqui presionando el boton Atras.
            }
        };


        Timer timer = new Timer();
        timer.schedule(task, 5000);
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
