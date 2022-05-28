package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.RotulosGuia;

import java.util.ArrayList;

public class ListaRotulosAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<RotulosGuia> items, itemsFiltrados;
    RotulosGuia mListaRotulo;
    ViewHolder holder = new ViewHolder();

    String TAG = "ListaRotulosAdapter";

    public ListaRotulosAdapter(AppCompatActivity activity, ArrayList<RotulosGuia> items){
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
        mListaRotulo = items.get(position);
        ListaRotulosAdapter.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_rotulo, null);
            holder = new ViewHolder();

            holder.lblRotulo = convertView.findViewById(R.id.lblRotulo);
            holder.lblPiezas = convertView.findViewById(R.id.lblPiezas);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.lblRotulo.setText(mListaRotulo.getStrPedido1());
        holder.lblPiezas.setText(mListaRotulo.getStrContenido());
        return convertView;
    }

    static class ViewHolder {
        TextView lblPiezas, lblRotulo;

    }
}
