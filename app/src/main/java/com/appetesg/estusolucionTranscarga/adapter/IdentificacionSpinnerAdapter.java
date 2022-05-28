package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.Identificacion;

import java.util.ArrayList;

public class IdentificacionSpinnerAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<Identificacion> itemsI, imtesIFiltrados;
    Identificacion mIdentificacion;
    String TAG="FiltroSpinnerAdapter";

    public IdentificacionSpinnerAdapter(AppCompatActivity activity, ArrayList<Identificacion> itemsI) {
        super();
        this.activity = activity;
        this.itemsI = itemsI;
    }

    @Override
    public int getCount() {
        return itemsI.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsI.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        mIdentificacion = itemsI.get(position);
        IdentificacionSpinnerAdapter.ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_filtro, null);
            holder = new IdentificacionSpinnerAdapter.ViewHolder();

            holder.lblSpinner = convertView.findViewById(R.id.lblSpinner);


            convertView.setTag(holder);

        } else {
            holder = (IdentificacionSpinnerAdapter.ViewHolder) convertView.getTag();

        }



        holder.lblSpinner.setText(mIdentificacion.getStrIdentificacion());

        return convertView;
    }


    static class ViewHolder {
        TextView lblSpinner;


    }

}
