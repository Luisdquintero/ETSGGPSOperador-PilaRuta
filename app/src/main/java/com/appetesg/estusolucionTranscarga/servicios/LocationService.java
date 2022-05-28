package com.appetesg.estusolucionTranscarga.servicios;

/**
 * Created by RafaelCastro on 11/17/18.
 */

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
//import android.support.annotation.Nullable;

import androidx.annotation.Nullable;

import com.appetesg.estusolucionTranscarga.MainActivity;
import com.appetesg.estusolucionTranscarga.R;
import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;

        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;


public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    static String TAG="LocationService";
    static String PREFS_NAME;
    double speed;
    String datos;
    SharedPreferences sharedpreferences;

    private static final String SOAP_ACTION = "http://tempuri.org/TiempoGeolocalizacion";
    private static final String METHOD_NAME = "TiempoGeolocalizacion";
    private static final String NAMESPACE = "http://tempuri.org/";


    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PREFS_NAME = this.getString(R.string.SPREF);
        sharedpreferences = this.getSharedPreferences(PREFS_NAME, 0);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        MainActivity.mProgressDialog.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else {
            lEnd = mCurrentLocation;

        }

        //Calling the method below updates the  live values of distance and speed to the TextViews.
        updateUI();

        datos = ""+location.getLatitude()+","+location.getLongitude()+" vel: "+speed;

        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
        speed = location.getSpeed() * 18 / 5;
        MainActivity.mMap.clear();
        if (location.getLongitude()!=0) {
            LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
            MainActivity.mMap.addMarker(new MarkerOptions().position(posicion).title("Mi ubicación"));
            MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion,18f));
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }


    }

    //The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {

            if (lStart!=lEnd)
                distance += lStart.distanceTo(lEnd);

        //Log.d("DISTANCIA",""+lStart.getLatitude()+","+lStart.getLongitude());
        //Log.d("DISTANCIA",""+lEnd.getLatitude()+","+lEnd.getLongitude());
        lStart = lEnd;

            //Log.d("DISTANCIA",""+distance);


            //MainActivity.endTime = System.currentTimeMillis();
            //long diff = MainActivity.endTime - MainActivity.startTime;
            //diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            //MainActivity.time.setText("Total Time: " + diff + " minutes");

                MainActivity.txtVelocidad.setText("" + new DecimalFormat("#.#").format(speed));

                if (distance>=1000) {
                    MainActivity.txtDistancia.setText(new DecimalFormat("#.##").format(distance/1000) + " Km");
                }else{
                    MainActivity.txtDistancia.setText(new DecimalFormat("###").format(distance) + " m");
                }




    }


    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }
}