package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaFuec;

import java.util.ArrayList;


public class ListaFuecAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ListaFuec> items, itemsFiltrados;
    ListaFuec mListaFuec;
    ListaFuecAdapter.ViewHolder holder = new ListaFuecAdapter.ViewHolder();

    String TAG = "ListaFuecAdapterextends";

    public ListaFuecAdapter(AppCompatActivity activity, ArrayList<ListaFuec> items){
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
    public View getView(final  int position, View convertView, ViewGroup parent)
    {
        mListaFuec = items.get(position);
        ListaFuecAdapter.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_fuec, null);
            holder = new ViewHolder();

            holder.lblFuec = convertView.findViewById(R.id.lblFuec);
            holder.lblNumerocontrato = convertView.findViewById(R.id.lblNumeroHijo);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListaFuecAdapter.ViewHolder)convertView.getTag();
        }

        holder.lblFuec.setText(mListaFuec.getStrContratante());
        holder.lblNumerocontrato.setText("Nro: "+mListaFuec.getIntNumeroContrato());
        return convertView;
    }

    static class ViewHolder {
        TextView lblFuec, lblNumerocontrato;
    }
}
