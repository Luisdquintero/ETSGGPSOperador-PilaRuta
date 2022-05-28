package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaServiciosDatos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by RafaelCastro on 12/14/18.
 */

public class ListaServicioAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ListaServiciosDatos> items,itemsFiltrados;
    ListaServiciosDatos mListaServicio;
    ViewHolder holder=new ViewHolder();
    String TAG="ListaServicioAdapter";

    public ListaServicioAdapter(AppCompatActivity activity, ArrayList<ListaServiciosDatos> items) {
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

        mListaServicio = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_servicios, null);
            holder = new ViewHolder();

            holder.lblNServicio = convertView.findViewById(R.id.lblNServicio);
            holder.lblFechaServicio = convertView.findViewById(R.id.lblFecha);
            holder.lblTipoServicio = convertView.findViewById(R.id.lblTipoServicio);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }


        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fechHora;
       try {
           Date convertedDate = sourceFormat.parse(mListaServicio.getStrFechaServi());
           fechHora = destFormat.format(convertedDate);
       }catch (Exception ex){
           fechHora = "";

       }

            holder.lblNServicio.setText(mListaServicio.getNumServi());
            holder.lblFechaServicio.setText(mListaServicio.getStrFechaServi());
            //holder.lblTipoServicio.setText(mListaServicio.getHoraServicio());
            holder.lblFechaServicio.setText(fechHora);
            if(mListaServicio.getTipoServi().equalsIgnoreCase("1"))
                holder.lblTipoServicio.setText("Solicitado");
            else
                holder.lblTipoServicio.setText("Cierre");

        return convertView;
    }


    static class ViewHolder {
        TextView lblNServicio,lblTipoServicio,lblFechaServicio;
        //,lblhoraLllegada, lblHoraCita


    }

}
