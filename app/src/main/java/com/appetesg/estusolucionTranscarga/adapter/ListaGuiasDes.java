package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.GuiasD;

import java.util.ArrayList;

public class ListaGuiasDes extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<GuiasD> items, itemsFiltrados;
    GuiasD mListaGuias;
    ListaGuiasDes.ViewHolder holder = new ListaGuiasDes.ViewHolder();

    String TAG = "ListaPlacasAdapter";

    public ListaGuiasDes(AppCompatActivity activity, ArrayList<GuiasD> items){
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
        mListaGuias = items.get(position);
        ListaGuiasDes.ViewHolder holder = null;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_guias, null);
            holder = new ListaGuiasDes.ViewHolder();

            holder.lblGuia = convertView.findViewById(R.id.txtGuia);
            holder.lblProducto = convertView.findViewById(R.id.txtDescripcionG);
            holder.lblDestinatario = convertView.findViewById(R.id.txtDestinatario);
            holder.lblValor = convertView.findViewById(R.id.txtValor);
            holder.lblPeso = convertView.findViewById(R.id.txtPesoP);
            holder.lblDireccion = convertView.findViewById(R.id.txtDireccionG);

            holder.txtViewGuia = convertView.findViewById(R.id.txtViewGuia);
            holder.txtViewDestinatario = convertView.findViewById(R.id.txtViewDest);
            holder.txtViewDir = convertView.findViewById(R.id.txtViewDir);
            holder.txtViewProducto = convertView.findViewById(R.id.txtViewPrd);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ListaGuiasDes.ViewHolder)convertView.getTag();
        }



        if(mListaGuias.getStrGuia().equalsIgnoreCase("No hay guias")){

            holder.lblGuia.setText(mListaGuias.getStrGuia());

            holder.lblProducto.setVisibility(View.GONE);
            holder.lblDestinatario.setVisibility(View.GONE);
            holder.lblValor.setVisibility(View.GONE);
            holder.lblPeso.setVisibility(View.GONE);
            holder.lblDireccion.setVisibility(View.GONE);

            holder.txtViewGuia.setVisibility(View.GONE);
            holder.txtViewDestinatario.setVisibility(View.GONE);
            holder.txtViewDir.setVisibility(View.GONE);
            holder.txtViewProducto.setVisibility(View.GONE);

        }
        else
        {
            holder.lblGuia.setText(mListaGuias.getStrGuia());
            holder.lblProducto.setText(mListaGuias.getStrDescripcionP());
            holder.lblDestinatario.setText(mListaGuias.getStrDestinatario());
            holder.lblValor.setText("$"+mListaGuias.getStrValor());
            holder.lblPeso.setText("Peso: "+mListaGuias.getStrPeso());
            holder.lblDireccion.setText(mListaGuias.getStrDireccionDe());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lblGuia, lblProducto, lblDestinatario, lblValor, lblPeso, lblDireccion,
                txtViewGuia, txtViewProducto, txtViewDestinatario, txtViewDir;
    }
}
