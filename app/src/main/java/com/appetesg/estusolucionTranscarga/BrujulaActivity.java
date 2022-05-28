package com.appetesg.estusolucionTranscarga;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;


public class BrujulaActivity extends AppCompatActivity implements SensorEventListener{
    SensorManager sensorManager;
    ImageView imgBrujula;
    float currentDegree;
    TextView lblGrados,lblOrientacion;
    double grd;
    Toolbar toolbar;
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
        setContentView(R.layout.activity_brujula);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Brújula -  App SisColint "+ getResources().getString(R.string.versionApp));
        imgBrujula = (ImageView)findViewById(R.id.imgBrujula);
        lblOrientacion = (TextView)findViewById(R.id.lblOrientacion);
        lblGrados = (TextView)findViewById(R.id.lblGrados);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree=Math.round(event.values[0]);
        int grados =(int)degree;
        grd = grados;
        String orient="...";
        Log.d("GRADOS",String.valueOf(grados));
        if ((degree > 340.0f && degree < 359.0f) || (degree > 0.0f && degree <= 20.0f)) {
            this.lblOrientacion.setText("N");
        } else if (degree > 20.0f && degree <= 60.0f) {
            this.lblOrientacion.setText("NE");
        }
        if (degree > 60.0f && degree <= 120.0f) {
            this.lblOrientacion.setText("E");
        } else if (degree > 120.0f && degree<= 160.0f) {
            this.lblOrientacion.setText("SE");
        } else if (degree > 160.0f && degree <= 200.0f) {
            this.lblOrientacion.setText("S");
        } else if (degree > 200.0f && degree <= 240.0f) {
            this.lblOrientacion.setText("SO");
        } else if (degree > 240.0f && degree <= 290.0f) {
            this.lblOrientacion.setText("O");
        } else if (degree > 290.0f && degree <= 340.0f) {
            this.lblOrientacion.setText("NO");
        }

        lblGrados.setText(String.valueOf(grados)+"º");

        //Rotar imagen
        //Bitmap source = BitmapFactory.decodeResource(MonitoreoGPSActivity.this.getResources(), R.drawable.compass);
        //Bitmap target = RotateMyBitmap(source, grados);
        //imgBrujula.setImageBitmap(target);

        RotateAnimation ra=new RotateAnimation(currentDegree,-degree, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(120);
        ra.setFillAfter(true);
        imgBrujula.startAnimation(ra);
        currentDegree=-degree;

    }

    /**
     * Capturar el error
     * @param thread
     * @param e
     */
    private void excepcionCapturada(Thread thread, Throwable e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        //LogErrorDB.LogError(0,errors.toString(), this.getClass().getCanonicalName(), BASE_URL);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
