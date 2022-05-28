package com.appetesg.estusolucionTranscarga;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class prueba extends AppCompatActivity
{
    ImageView imgfondo;
    Bitmap myBitmap;
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
        setContentView(R.layout.activity_prueba);
        imgfondo =(ImageView) findViewById(R.id.imgfondo);
        LinearLayout lytdesing = (LinearLayout) findViewById(R.id.lytdesign);

        lytdesing.post(new Runnable() {
            public void run() {

                //take screenshot
                myBitmap = captureScreen(lytdesing);



                try {
                    if(myBitmap!=null){
                        //save image to SD card
                        imgfondo.setImageBitmap(myBitmap);
                        saveImage(myBitmap);

                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("error "+e.getMessage());
                }

            }
        });

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
