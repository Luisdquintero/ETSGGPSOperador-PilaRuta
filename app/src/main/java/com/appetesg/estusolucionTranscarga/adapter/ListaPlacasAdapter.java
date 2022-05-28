package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaPlacas;

import java.util.ArrayList;

public class ListaPlacasAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ListaPlacas> items, itemsFiltrados;
    ListaPlacas mListaPlaca;
    ViewHolder holder = new ViewHolder();

    String TAG = "ListaPlacasAdapter";

    public ListaPlacasAdapter(AppCompatActivity activity, ArrayList<ListaPlacas> items){
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
    public  View getView(final  int position, View convertView, ViewGroup parent)
    {
        mListaPlaca = items.get(position);
        ListaPlacasAdapter.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_placa, null);
            holder = new ViewHolder();

            holder.lbPlaca = convertView.findViewById(R.id.lblPlaca);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.lbPlaca.setText(mListaPlaca.getStrPlaca());
        return convertView;
    }

    static class ViewHolder {
        TextView lbPlaca;
    }
}
