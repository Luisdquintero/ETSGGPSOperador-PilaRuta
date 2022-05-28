package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaFiltro;

import java.util.ArrayList;

public class ListaFiltroAdapter extends BaseAdapter{
    protected AppCompatActivity activity;
    protected ArrayList<ListaFiltro> items, itemsFiltrados;
    ListaFiltro mListaFiltro;
    ViewHolder holder = new ViewHolder();

    String TAG = "ListaPlacasAdapter";

    public ListaFiltroAdapter(AppCompatActivity activity, ArrayList<ListaFiltro> items){
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
        mListaFiltro = items.get(position);
        ListaFiltroAdapter.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_filtro, null);
            holder = new ViewHolder();

            holder.lblFiltroNota = convertView.findViewById(R.id.lblFiltroLista);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.lblFiltroNota.setText(mListaFiltro.getStrDescripcion());
        return convertView;
    }

    static class ViewHolder {
        TextView lblFiltroNota;
    }
}
