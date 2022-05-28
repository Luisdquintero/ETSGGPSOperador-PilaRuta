package com.appetesg.estusolucionTranscarga.servicios;

import android.content.SharedPreferences;

import com.appetesg.estusolucionTranscarga.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    static String TAG="FirebaseInstanceIDService";
    String PREFS_NAME;

    SharedPreferences sharedPreferences;
    @Override
    public void onTokenRefresh() {
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);


            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firebaseToken",refreshedToken);
            editor.commit();
    }



}
