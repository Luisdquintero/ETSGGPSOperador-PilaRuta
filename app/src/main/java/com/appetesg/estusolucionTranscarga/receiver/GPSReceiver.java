package com.appetesg.estusolucionTranscarga.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by RafaelCastro on 11/21/18.
 */

public class GPSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int gpsSignal = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(gpsSignal==0){
                //Toast.makeText(context.getApplicationContext(),"El GPS está apagado",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent1);

            }else{
                //Toast.makeText(context.getApplicationContext(),"El GPS está encendido",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){

        }

    }
}
