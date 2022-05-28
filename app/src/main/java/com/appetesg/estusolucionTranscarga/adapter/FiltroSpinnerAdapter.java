package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.Filtro;

import java.util.ArrayList;

/**
 * Created by RafaelCastro on 12/15/18.
 */

public class FiltroSpinnerAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<Filtro> items,itemsFiltrados;
    Filtro mFiltro;
    ListaServicioAdapter.ViewHolder holder=new ListaServicioAdapter.ViewHolder();
    String TAG="FiltroSpinnerAdapter";

    public FiltroSpinnerAdapter(AppCompatActivity activity, ArrayList<Filtro> items) {
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

        mFiltro = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_filtro, null);
            holder = new ViewHolder();

            holder.lblSpinner = convertView.findViewById(R.id.lblSpinner);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }



            holder.lblSpinner.setText(mFiltro.getFiltroNombre());

        return convertView;
    }


    static class ViewHolder {
        TextView lblSpinner;


    }

}
