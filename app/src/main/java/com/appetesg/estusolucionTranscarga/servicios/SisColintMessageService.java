package com.appetesg.estusolucionTranscarga.servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.appetesg.estusolucionTranscarga.MenuActivity;
import com.appetesg.estusolucionTranscarga.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class SisColintMessageService extends FirebaseMessagingService {

    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage message) {

        Log.d("MENSAJE",""+message.getNotification().toString());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }
            sendNotification(message);


    }


    private void sendNotification(RemoteMessage r) {

//On click of notification it redirect to this Activity
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.carro)//notification icon
                .setContentTitle(r.getNotification().getTitle())
                .setContentText(r.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = ADMIN_CHANNEL_ID;
        String adminChannelDescription = ADMIN_CHANNEL_ID;

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}



