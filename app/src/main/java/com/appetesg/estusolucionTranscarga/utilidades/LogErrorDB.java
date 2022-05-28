package com.appetesg.estusolucionTranscarga.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LogErrorDB {

    SharedPreferences sharedPreferences;
    private static final String METHOD_NAME = "LogError";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static void LogError(int idUsuario, String strError, String strPantalla, String BASE_URL, Context context){
        new SendErrorTask( idUsuario,  strError, strPantalla, BASE_URL, context).execute();
    }

    public static class SendErrorTask extends AsyncTask<Integer, Integer, String>
    {
        String strError, strPantalla, BASE_URL;
        Context context;
        int idUsuario;

        public SendErrorTask(int idUsuario, String strError, String strPantalla, String BASE_URL ,Context context)
        {
            this.idUsuario = idUsuario;
            this.strError = strError;
            this.strPantalla = strPantalla;
            this.BASE_URL = BASE_URL;
            this.context= context;
        }

        //Metodo en string
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
        }
        @Override
        protected String doInBackground(Integer... integers)
        {
            String res = "";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("IdUsuario", idUsuario);
            request.addProperty("strError",strError);
            request.addProperty("strpantalla", strPantalla);

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
                Log.d("LOGERROR",ex.getMessage());
                ex.printStackTrace();
            }
            Object  result = null;
            try {
                result = (Object)envelope.getResponse();
                Log.i("LOGERROR",String.valueOf(result)); // see output in the console
                res = String.valueOf(result);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.e("SOAPLOG", e.getMessage());
                e.printStackTrace();
                res = "false";
            }

            //Intent intent = new Intent(context,LoginActivity.class);
            //context.startActivity(intent);
            ((Activity)context).finish();
            return res;
        }

    }


}
