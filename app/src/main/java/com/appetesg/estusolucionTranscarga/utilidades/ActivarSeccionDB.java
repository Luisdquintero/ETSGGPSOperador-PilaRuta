package com.appetesg.estusolucionTranscarga.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appetesg.estusolucionTranscarga.RegistroGuia;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ActivarSeccionDB {

    private static final String METHOD_NAME = "TraerParametroGen";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static void LogError(String strID, Context context, String BASE_URL){
        new SeccionDBAsyncTask( strID, context, BASE_URL).execute();
    }

    // Valida si esta habilitada la seccion
    public static class SeccionDBAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        private static final String TAG = "SECCIONBD";
        String strID;
        Context context;
        String BASE_URL;

        public SeccionDBAsyncTask(String strID, Context context, String BASE_URL)
        {
            this.strID = strID;
            this.context= context;
            this.BASE_URL = BASE_URL;
        }

        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("strIdParamGen", strID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL, 30000);
            httpTransport.debug = true;

            try
            {
                httpTransport.call(NAMESPACE+METHOD_NAME, envelope);
            }

            catch (Exception ex)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,ex.getMessage());
                ex.printStackTrace();
            }
            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                Log.i(TAG,String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            return res;
        }

    }



}
