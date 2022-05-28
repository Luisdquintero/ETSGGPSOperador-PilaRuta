package com.appetesg.estusolucionTranscarga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetesg.estusolucionTranscarga.adapter.ChatAdapter;
import com.appetesg.estusolucionTranscarga.modelos.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ListView lstChat;
    ImageButton imbEnviar;
    TextView txtMensaje,lblNombreContacto;
    ImageView imgFotoCliente;
    ArrayList<Chat> chats = new ArrayList<>();
    ChatAdapter adapter;
    String enviado,usuario;


    SharedPreferences sharedPreferences;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

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
        setContentView(R.layout.activity_chat);
        sharedPreferences = this.getSharedPreferences(this.getString(R.string.SPREF), 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView lblTextoToolbar = (TextView) toolbar.findViewById(R.id.
                lblTextoToolbar);


        lblTextoToolbar.setText("Chat -  App SisColint "+ getResources().getString(R.string.versionApp));
        imbEnviar = findViewById(R.id.imbEnviar);
        txtMensaje = findViewById(R.id.txtMensaje);
        lstChat = findViewById(R.id.lstChat);
        lblNombreContacto = findViewById(R.id.lblContacto);
        lblNombreContacto.setText("Chateando con conductor");
        root = FirebaseDatabase.getInstance().getReference().child(sharedPreferences.getString("ordenActiva",""));

        imbEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = txtMensaje.getText().toString();
                if (mensaje.length()>0) {

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String hora = sdf.format(cal.getTime());

                    Map<String, Object> map = new HashMap<>();
                    String key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference msgRoot = root.child(key);
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("nombre", "xxxx");
                    map2.put("mensaje", mensaje);
                    map2.put("enviado","cliente");
                    map2.put("hora",hora);

                    msgRoot.updateChildren(map2);
                }else{
                    Toast.makeText(getApplicationContext(),"No se pueden enviar mensajes vacíos",Toast.LENGTH_LONG).show();
                }
                txtMensaje.setText("");
            }

        });


        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                actualizarChat(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                actualizarChat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // actualizarChat(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    private void actualizarChat(DataSnapshot dataSnapshot){

        try {
            Iterator iterator = dataSnapshot.getChildren().iterator();
            while (iterator.hasNext()) {
                enviado = (String) ((DataSnapshot) iterator.next()).getValue();
                String h = (String) ((DataSnapshot) iterator.next()).getValue();
                String mensaje = (String) ((DataSnapshot) iterator.next()).getValue();
                usuario = (String) ((DataSnapshot) iterator.next()).getValue();

                Log.d("CHAT", "usuario:" + usuario + ", enviado: " + enviado + ", mensaje: " + mensaje+" hora: "+h);
                chats.add(new Chat(enviado,mensaje,h));

            }

        }catch (Exception ex){
            Log.d("CHAT",ex.getMessage());
        }
        adapter = new ChatAdapter(ChatActivity.this,chats,enviado);
        lstChat.setAdapter(adapter);
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
