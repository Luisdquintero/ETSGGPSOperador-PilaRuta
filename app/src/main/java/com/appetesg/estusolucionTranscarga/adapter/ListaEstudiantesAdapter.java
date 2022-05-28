package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.ListaEstudiantes;

import java.util.ArrayList;

public class ListaEstudiantesAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ListaEstudiantes> items,itemsFiltrados;
    ListaEstudiantes mListaEstudiantes;
    ViewHolder holder=new ViewHolder();
    String TAG="ListaEstudiantesAdapter";

    public ListaEstudiantesAdapter(AppCompatActivity activity, ArrayList<ListaEstudiantes> items) {
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

        mListaEstudiantes = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_entrega_estudiante, null);
            holder = new ViewHolder();

            holder.lblEstudiante = convertView.findViewById(R.id.lblEstudiante);
            holder.lblEstado = convertView.findViewById(R.id.lblEstado);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        /*
        holder.chkEstudiante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("ESTUDIANTE","id estudiante: "+mListaEstudiantes.getId());
                }
            }
        });*/
        holder.lblEstudiante.setText(mListaEstudiantes.getNombreEstudiante());
        holder.lblEstado.setText(mListaEstudiantes.getNomEstado());

        return convertView;
    }


    static class ViewHolder {
        TextView lblEstudiante,lblEstado;


    }

}

