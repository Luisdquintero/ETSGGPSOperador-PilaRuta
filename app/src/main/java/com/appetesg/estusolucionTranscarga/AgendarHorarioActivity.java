package com.appetesg.estusolucionTranscarga;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import android.support.design.widget.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.utilidades.LogErrorDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgendarHorarioActivity extends AppCompatActivity {
TextView txtFechaInicio,txtHoraInicio,txtFechaFin,txtHoraFin,txtObserv;
CheckBox chkCancela;
    ProgressDialog progressDialog;
FloatingActionButton fabAgenda;
    final Calendar calendar = Calendar.getInstance();
    boolean cancela;
    static String TAG="AgendarHorarioActivity";
    SharedPreferences sharedPreferences;
    private static final String SOAP_ACTION = "http://tempuri.org/Agenda";
    private static final String METHOD_NAME = "Agenda";

    //Edición
    private static final String SOAP_ACTION_EDITAR = "http://tempuri.org/CancelacionAgenda";
    private static final String METHOD_NAME_EDITAR = "CancelacionAgenda";

    private static final String NAMESPACE = "http://tempuri.org/";
    String BASE_URL,PREFS_NAME;
    int idUsuario=0;
    int editar=0;
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
        setContentView(R.layout.activity_agendar_horario);

        PREFS_NAME = this.getString(R.string.SPREF);
        sharedPreferences = this.getSharedPreferences(PREFS_NAME, 0);
        idUsuario = sharedPreferences.getInt("idUsuario",0);
        try{
            editar = getIntent().getIntExtra("edit",0);
        }catch (Exception ex){

        }
        BASE_URL = sharedPreferences.getString("urlColegio","");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgendarHorarioActivity.this, ListadoAgendaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);
        lblTextoToolbar.setText("Disponabilidar");
        fabAgenda = (FloatingActionButton)findViewById(R.id.fabAgenda);
        txtFechaInicio = (TextView) findViewById(R.id.txtFechaInicio);
        txtFechaFin = (TextView) findViewById(R.id.txtFechaFin);
        txtHoraInicio = (TextView) findViewById(R.id.txtHoraInicio);
        txtHoraFin = (TextView) findViewById(R.id.txtHoraFin);
        txtObserv = (TextView) findViewById(R.id.txtObserv);
        chkCancela = (CheckBox)findViewById(R.id.chkCancela);

        if (editar==0){
            Log.d(TAG,"Insertar...");
            setDate(txtFechaFin);
            setDate(txtFechaInicio);
            setDate(txtFechaInicio);

            txtFechaInicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(AgendarHorarioActivity.this, date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txtHoraInicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AgendarHorarioActivity.this,hora,
                            calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),
                            true).show();
                }
            });

            txtFechaFin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(AgendarHorarioActivity.this, dateFin, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txtHoraFin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(AgendarHorarioActivity.this,horaFin,
                            calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),
                            true).show();
                }
            });

            fabAgenda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String fhoraInicio = txtFechaInicio.getText().toString() + " " + txtHoraInicio.getText().toString();
                    String fhoraFin = txtFechaFin.getText().toString() + " " + txtHoraFin.getText().toString();
                    Date finicio = convertirFecha(fhoraInicio);
                    Date ffin = convertirFecha(fhoraFin);
                    if (txtHoraFin.getText().toString().length()>0 && txtHoraInicio.getText().toString().length()>0 &&
                    txtFechaFin.getText().toString().length()>0 && txtFechaInicio.getText().toString().length()>0) {
                        if (finicio.compareTo(ffin) <= 0) {
                            new AgendarHorarioAsyncTask(txtFechaInicio.getText().toString(), txtFechaFin.getText().toString(),
                                    txtHoraInicio.getText().toString(), txtHoraFin.getText().toString(),
                                    txtObserv.getText().toString(), idUsuario, cancela).execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "La fecha de inicio no puede ser posterior a la final", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Todos los campos de fecha y hora son obligatorios",Toast.LENGTH_LONG).show();
                    }
                }
            });

            chkCancela.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        cancela=true;
                    }else{
                        cancela=false;
                    }
                }
            });
        }else{
            //Editar. Solo cambiará el estado de la cancelación
            //Llenado de campos
            Log.d(TAG,"Editar...");
            String fechaHoraInicio = getIntent().getStringExtra("fechaInicio");
            String fechaInicio = fechaHoraInicio.split(" ")[0];
            String horaInicio = fechaHoraInicio.split(" ")[1];

            String fechaHoraFin = getIntent().getStringExtra("fechaFin");
            String fechaFin = fechaHoraFin.split(" ")[0];
            String horaFin = fechaHoraFin.split(" ")[1];

            txtFechaInicio.setText(fechaInicio);
            txtHoraInicio.setText(horaInicio);
            txtFechaFin.setText(fechaFin);
            txtHoraFin.setText(horaFin);
            txtObserv.setText(getIntent().getStringExtra("observ"));

                chkCancela.setChecked(getIntent().getBooleanExtra("cancelacion",false));
                chkCancela.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            cancela=true;
                        }else{
                            cancela=false;
                        }
                    }
                });


                fabAgenda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ModificarHorarioAsyncTask(getIntent().getIntExtra("idAgenda",0),idUsuario,cancela).execute();
                    }
                });

        }




    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(1);
        }

    };

   final TimePickerDialog.OnTimeSetListener hora = new TimePickerDialog.OnTimeSetListener() {
       @Override
       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
           calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
           calendar.set(Calendar.MINUTE,minute);
           updateLabelHoras(1);
       }
   };



    final DatePickerDialog.OnDateSetListener dateFin = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(2);
        }

    };


    final TimePickerDialog.OnTimeSetListener horaFin = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            updateLabelHoras(2);
        }
    };

    private void updateLabel(int index) {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        if(index==1)
            txtFechaInicio.setText(sdf.format(calendar.getTime()));
        if(index==2)
            txtFechaFin.setText(sdf.format(calendar.getTime()));


    }


    private void updateLabelHoras(int index) {

        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        if(index==1)
            txtHoraInicio.setText(sdf.format(calendar.getTime()));
        if(index==2)
            txtHoraFin.setText(sdf.format(calendar.getTime()));


    }


    public Date convertirFecha(String fecha){
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            d = sdf.parse(fecha);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return d;
    }


    //poner la fecha de hoy
    public void setDate (TextView view){

        Date today = Calendar.getInstance().getTime();//getting date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");//formating according to my need
        String date = formatter.format(today);
        view.setText(date);
    }


    public class AgendarHorarioAsyncTask extends AsyncTask<Integer,Integer,Boolean> {

        String fechaIni,fechaFin,horaIni,horaFin;
        String observ;
        int idUsuario;
        boolean cancela;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cargar("Enviando horario");
        }

        public AgendarHorarioAsyncTask(String fechaIni, String fechaFin, String horaIni,
                                       String horaFin, String observ, int idUsuario, boolean cancela) {
            this.fechaIni = fechaIni;
            this.fechaFin = fechaFin;
            this.horaIni = horaIni;
            this.horaFin = horaFin;
            this.observ = observ;
            this.idUsuario = idUsuario;
            this.cancela = cancela;
        }





        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s) {
                Toast.makeText(getApplicationContext(), "Periodo agendado", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AgendarHorarioActivity.this,MenuActivity.class);
                startActivity(intent);
                finish();
            }

        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            //formatear fechas
            String myFormat1 = "dd/MM/yyyy HH:mm";
            boolean r=false;
            SimpleDateFormat spf = new SimpleDateFormat(myFormat1, Locale.getDefault());
            try {
                Date dFechaInicio = spf.parse(fechaIni+" "+horaIni);
                Date dFechaFin = spf.parse(fechaFin+" "+horaFin);


                String myFormat2 = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat2, Locale.getDefault());
                String fechaIniConv = sdf.format(dFechaInicio);
                String fechaFinConv = sdf.format(dFechaFin);


                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;
                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;
                request.addProperty("CodUsu",String.valueOf(idUsuario));
                request.addProperty("Fecha_inicio", fechaIniConv);
                request.addProperty("Fecha_Fin", fechaFinConv);
                request.addProperty("Observaciones", observ);
                request.addProperty("blCancelacion", cancela);
                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
                httpTransport.debug = true;
                try {
                    httpTransport.call(SOAP_ACTION, envelope);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                    r=false;
                }

                result = (SoapObject) envelope.bodyIn;
                r=true;
            }catch (ParseException ex){
                Log.d(TAG,ex.getMessage());
                r=false;
                Toast.makeText(getApplicationContext(),"error:"+ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return r;
        }
    }
    public class ModificarHorarioAsyncTask extends AsyncTask<Integer,Integer,Boolean> {

        int id;
        int idUsuario;
        boolean cancela;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cargar("Modificando horario");
        }

        public ModificarHorarioAsyncTask(int id, int idUsuario, boolean cancela) {
            this.id = id;
            this.idUsuario = idUsuario;
            this.cancela = cancela;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s) {
                Toast.makeText(getApplicationContext(), "Periodo modificado", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AgendarHorarioActivity.this,ListadoAgendaActivity.class);
                startActivity(intent);
                finish();
            }

        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            boolean r=false;
            try {


                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_EDITAR);
                SoapObject result;

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                envelope.implicitTypes = true;
                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;
                request.addProperty("IdUsuario",String.valueOf(idUsuario));
                request.addProperty("intId", id);
                request.addProperty("blCancelacion", cancela);
                HttpTransportSE httpTransport = new HttpTransportSE(BASE_URL,30000);
                httpTransport.debug = true;
                try {
                    httpTransport.call(SOAP_ACTION_EDITAR, envelope);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, e.getMessage());
                    e.printStackTrace();
                    r=false;
                }

                result = (SoapObject) envelope.bodyIn;
                r=true;
            }catch (Exception ex){
                Log.d(TAG,ex.getMessage());
                r=false;
                Toast.makeText(getApplicationContext(),"error:"+ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            return r;
        }
    }




    public void cargar(String msj) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msj);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    /**
     * Capturar el error
     * @param thread
     * @param e
     */
    private void excepcionCapturada(Thread thread, Throwable e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        LogErrorDB.LogError(sharedPreferences.getInt("idUsuario",0),errors.toString(), this.getClass().getCanonicalName(), BASE_URL, this);
        //Intent intent = new Intent(getBaseContext(),)
        //finish();
        //System.exit(1); // salir de la app sin mostrar el popup de excepcion feo
    }

}
