package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.Chat;

import java.util.ArrayList;


/**
 * Created by RafaelCastro on 8/4/18.
 */

public class ChatAdapter extends BaseAdapter {

    protected AppCompatActivity activity;
    protected ArrayList<Chat> items;
    Chat chat;
    String enviado;
    static SharedPreferences sharedpreferences;
    Typeface typeface,typeface1;
    ViewHolder holder=new ViewHolder();
    String TAG="ChatAdapter";

    public ChatAdapter(AppCompatActivity activity, ArrayList<Chat> items, String enviado) {
        super();
        this.activity = activity;
        this.items = items;
        sharedpreferences = activity.getSharedPreferences(activity.getString(R.string.SPREF), 0);
        //typeface1 = Typeface.createFromAsset(activity.getAssets(),"fonts/Lato-Regular.ttf");
        this.enviado = enviado;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        chat = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_chat, null);
            holder = new ViewHolder();

            holder.lblMensaje = convertView.findViewById(R.id.lblMensaje);
            holder.lblHora = convertView.findViewById(R.id.lblHora);

            holder.lblMensaje.setTypeface(typeface1);
            holder.lblHora.setTypeface(typeface1);



/*
            LayerDrawable stars = (LayerDrawable) holder.rbCalif.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#F4B400"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(Color.parseColor("#6F6F6E"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.parseColor("#6F6F6E"), PorterDuff.Mode.SRC_ATOP);
            */

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();

        }




        holder.lblMensaje.setText(chat.getMensaje());
        holder.lblHora.setText(chat.getHora());
        //holder.rlContainer.setBackgroundColor(Color.parseColor(menuPrincipal.getColor()));
        if (enviado.equalsIgnoreCase("cliente")){
            holder.lblMensaje.setTextColor(Color.BLACK);
        }else{
            holder.lblMensaje.setTextColor(Color.BLUE);
        }





        /*
        holder.fabInfo.setOnClickListener(this);
        holder.fabCorazon.setOnClickListener(this);
*/


        return convertView;
    }



    static class ViewHolder {
        TextView lblMensaje,lblHora;
    }




}


