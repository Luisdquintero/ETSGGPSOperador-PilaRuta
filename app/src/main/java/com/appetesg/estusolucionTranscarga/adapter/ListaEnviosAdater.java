package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaEnvios;

import java.util.ArrayList;

public class ListaEnviosAdater extends  BaseAdapter{
    protected AppCompatActivity activity;
    protected ArrayList<ListaEnvios> items, listaEnvios;
    ListaEnvios mListaProductos;


    public ListaEnviosAdater(AppCompatActivity activity, ArrayList<ListaEnvios>items)
    {
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
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mListaProductos = items.get(position);
        ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_envios, null);
            holder = new ViewHolder();
            holder.lblSpinner = convertView.findViewById(R.id.lblListaEnvios);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lblSpinner.setText(mListaProductos.getStrEnvio());

        return convertView;

    }

    static class ViewHolder {
        TextView lblSpinner;


    }
}
