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
import com.appetesg.estusolucionTranscarga.modelos.ListaClientesDesti;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterDestinatarios extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ListaClientesDesti>items, itemFiltrados;
    ListaClientesDesti mListaDestinatarios;
    ViewHolder holder =  new ViewHolder();
    String TAG = "AdapterDestinatarios";

    public AdapterDestinatarios(AppCompatActivity activity, ArrayList<ListaClientesDesti> items) {
        super();
        this.activity = activity;
        this.items = items;
    }

    public AdapterDestinatarios()
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
        mListaDestinatarios = items.get(position);
        AdapterDestinatarios.ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_destinatarios, null);
            holder = new ViewHolder();

            holder.lblNombre = convertView.findViewById(R.id.lblNombreDest);
            holder.lblCedula = convertView.findViewById(R.id.lblCedulaDest);
            holder.lblFecha = convertView.findViewById(R.id.lblFechaRegDest);
            holder.lblCelular = convertView.findViewById(R.id.lblCelularDest);
            holder.lblDireccion = convertView.findViewById(R.id.lblDireccionDest);
            holder.lblCiudad = convertView.findViewById(R.id.lblCelularDest2);
            //holder.circleImageView = convertView.findViewById(R.id.cicleImageDep);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mListaDestinatarios.getIntCodDest() == -1){
            holder.lblCedula.setVisibility(View.GONE);
            holder.lblFecha.setVisibility(View.GONE);
            holder.lblCelular.setVisibility(View.GONE);
            holder.lblDireccion.setVisibility(View.GONE);
            holder.lblCiudad.setVisibility(View.GONE);

        }
        else{
            holder.lblCedula.setVisibility(View.VISIBLE);
            holder.lblFecha.setVisibility(View.VISIBLE);
            holder.lblCelular.setVisibility(View.VISIBLE);
            holder.lblDireccion.setVisibility(View.VISIBLE);
            holder.lblCiudad.setVisibility(View.VISIBLE);

            holder.lblCedula.setText("C.C/NIT: "+mListaDestinatarios.getStrCedulaDest());
            holder.lblFecha.setText(mListaDestinatarios.getStrFechaDest());
            holder.lblCelular.setText("Cel: "+mListaDestinatarios.getStrTelDest());
            holder.lblDireccion.setText("Dir: "+mListaDestinatarios.getStrDireDest());
            holder.lblCiudad.setText(mListaDestinatarios.getStrCiudad());
        }
        if(mListaDestinatarios.getStrNombreDest().equals("") || mListaDestinatarios.getStrNombreDest().isEmpty())
            holder.lblNombre.setText(mListaDestinatarios.getStrCompaniaDest());
        else
            holder.lblNombre.setText(mListaDestinatarios.getStrNombreDest());

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
        TextView lblNombre, lblCedula, lblFecha, lblCelular, lblDireccion, lblCiudad;
        CircleImageView circleImageView;
    }
}