package com.appetesg.estusolucionTranscarga.receiver;

/**
 * Created by RafaelCastro on 11/20/18.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import static com.appetesg.estusolucionTranscarga.MainActivity.lblConectividad;
import static com.appetesg.estusolucionTranscarga.MenuActivity.lblConnMenu;
import static com.appetesg.estusolucionTranscarga.MenuActivity.rlCnn;
public class NetworkChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (isOnline(context)) {
                try {
                    lblConectividad.setVisibility(View.VISIBLE);
                    lblConectividad.setText("");
                }catch (Exception ex){

                }

                try{
                    rlCnn.setVisibility(View.GONE);
                }catch (Exception ex){

                }


            } else {
                try {
                    lblConectividad.setVisibility(View.VISIBLE);
                    lblConectividad.setText("No hay conectividad a la red");
                }catch (Exception ex){

                }

                try{
                    rlCnn.setVisibility(View.VISIBLE);
                    lblConnMenu.setVisibility(View.VISIBLE);
                    lblConnMenu.setText("No hay conectividad a la red");
                }catch (Exception ex){

                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}