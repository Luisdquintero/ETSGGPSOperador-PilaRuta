package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.Agenda;

import java.util.ArrayList;

public class AgendaAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<Agenda> items,itemsFiltrados;
    Agenda agenda;
    ViewHolder holder=new ViewHolder();
    String TAG="AgendaAdapter";

    public AgendaAdapter(AppCompatActivity activity, ArrayList<Agenda> items) {
        super();
        this.activity = activity;
        this.items = items;
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

        agenda = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_agenda2, null);
            holder = new ViewHolder();

            holder.lblFechaInicio = convertView.findViewById(R.id.lblFechaInicio);
            holder.lblFechaFin = convertView.findViewById(R.id.lblFechaFin);
            holder.lblEstado = convertView.findViewById(R.id.lblEstado);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }




        holder.lblFechaInicio.setText(agenda.getFechaInicio());
        holder.lblFechaFin.setText(agenda.getFechaFin());

        if(agenda.isCancelacion()) {
            holder.lblEstado.setText("Cancelada");
            holder.lblEstado.setTextColor(Color.RED);
        }
        else {
            holder.lblEstado.setText("Activa");
            holder.lblEstado.setTextColor(Color.parseColor("#2164FF"));
        }

        return convertView;
    }


    static class ViewHolder {
        TextView lblFechaInicio,lblFechaFin,lblEstado;


    }

}

