package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.DocumentosVehiculo;

import java.util.ArrayList;

public class DocumentosAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<DocumentosVehiculo> items, itemsFiltrados;
    DocumentosVehiculo mDocumentoVehiculo;
    ViewHolder holder = new ViewHolder();

    String TAG = "DocumentosAdapter";

    public DocumentosAdapter(AppCompatActivity activity, ArrayList<DocumentosVehiculo> items){
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
        mDocumentoVehiculo = items.get(position);
        DocumentosAdapter.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_documento, null);
            holder = new ViewHolder();

            holder.lblDocumento = convertView.findViewById(R.id.lblDocumento);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.lblDocumento.setText(mDocumentoVehiculo.getDescripcion());
        return convertView;
    }
    /*public View getView(final int position, View convertView, ViewGroup parent) {

        mDocumentoVehiculo = items.get(position);
        ListaEstudiantesAdapter.ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_documento, null);
            holder = new DocumentosAdapter.ViewHolder();

            holder.lblEstudiante = convertView.findViewById(R.id.lblEstudiante);
            holder.lblEstado = convertView.findViewById(R.id.lblEstado);
            convertView.setTag(holder);

        } else {
            holder = (ListaEstudiantesAdapter.ViewHolder) convertView.getTag();

        }*/
        /*
        holder.chkEstudiante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("ESTUDIANTE","id estudiante: "+mListaEstudiantes.getId());
                }
            }
        });*/
        /*holder.lblEstudiante.setText(mListaEstudiantes.getNombreEstudiante());
        holder.lblEstado.setText(mListaEstudiantes.getNomEstado());

        return convertView;
    }*/


    static class ViewHolder {
        TextView lblDocumento;
    }
}
