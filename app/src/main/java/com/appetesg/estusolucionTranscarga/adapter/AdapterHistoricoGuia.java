package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.RotulosGuia;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHistoricoGuia extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<RotulosGuia>items, itemFiltrados;
    RotulosGuia mListaGuias;
    ViewHolder holder =  new ViewHolder();
    String TAG = "AdapterHistoricoGuia";

    public AdapterHistoricoGuia(AppCompatActivity activity, ArrayList<RotulosGuia> items) {
        super();
        this.activity = activity;
        this.items = items;
    }

    public AdapterHistoricoGuia()
    {}

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
        mListaGuias = items.get(position);

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_historico, null);
            holder = new ViewHolder();

            holder.lblGuia = convertView.findViewById(R.id.lblGuiaXOpe);
            holder.lblDestinoHistorico = convertView.findViewById(R.id.lblDestinoHistorico);
            holder.lblCantidadHistorico = convertView.findViewById(R.id.lblCantidadHistorico);
            holder.lblTotalEnvioHs = convertView.findViewById(R.id.lblTotalEnvioHs);
            //holder.circleImageView = convertView.findViewById(R.id.cicleImageDep);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lblGuia.setText(mListaGuias.getStrPedido1());
        holder.lblDestinoHistorico.setText("Dst: "+mListaGuias.getStrCiudadDestino());
        holder.lblCantidadHistorico.setText("Cantidad: "+String.valueOf(mListaGuias.getIntCantidad()));
        holder.lblTotalEnvioHs.setText("Vl: " + mListaGuias.getStrValorEnvio());
        //String strImagen = mListaDepartamentos.getStrImage();
            /*Bitmap bm = StringToBitMap(strImagen);
            if (bm != null) {
                holder.circleImageView.setImageBitmap(bm);
            } else {
                holder.circleImageView.setImageResource(R.drawable.logo);
            }*/
        return convertView;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            if (bitmap == null) {
                return null;
            } else {
                return bitmap;
            }
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    static class ViewHolder {
        TextView lblGuia, lblDestinoHistorico, lblCantidadHistorico, lblTotalEnvioHs;
        CircleImageView circleImageView;
    }
}