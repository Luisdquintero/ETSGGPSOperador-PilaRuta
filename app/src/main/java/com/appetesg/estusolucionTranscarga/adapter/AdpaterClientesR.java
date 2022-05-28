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
import com.appetesg.estusolucionTranscarga.modelos.ClientesR;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdpaterClientesR extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<ClientesR>items, itemFiltrados;
    ClientesR mListaClientes;
    ViewHolder holder =  new ViewHolder();
    String TAG = "AdpaterClientesR";

    public AdpaterClientesR(AppCompatActivity activity, ArrayList<ClientesR> items) {
        super();
        this.activity = activity;
        this.items = items;
    }

    public AdpaterClientesR()
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
        mListaClientes = items.get(position);
        AdpaterClientesR.ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_lista_remitentes, null);
            holder = new ViewHolder();

            holder.lblNombre = convertView.findViewById(R.id.lblNombreRemi);
            holder.lblCedula = convertView.findViewById(R.id.lblCedulaRe);
            holder.lblFecha = convertView.findViewById(R.id.lblFechaReg);
            holder.lblCelular = convertView.findViewById(R.id.lblCelularRemi);
            holder.lblCorp = convertView.findViewById(R.id.lblCorp);
            holder.lblDir = convertView.findViewById(R.id.lblNombreRemi3);
            //holder.circleImageView = convertView.findViewById(R.id.cicleImageDep);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mListaClientes.getIntCodCli() == -1){
            holder.lblCedula.setVisibility(View.GONE);
            holder.lblFecha.setVisibility(View.GONE);
            holder.lblCelular.setVisibility(View.GONE);
            holder.lblCorp.setVisibility(View.GONE);
            holder.lblDir.setVisibility(View.GONE);

        }
        else{
            holder.lblCedula.setVisibility(View.VISIBLE);
            holder.lblFecha.setVisibility(View.VISIBLE);
            holder.lblCelular.setVisibility(View.VISIBLE);
            holder.lblCorp.setVisibility(View.VISIBLE);
            holder.lblDir.setVisibility(View.VISIBLE);

            holder.lblCedula.setText("C.C/NIT: "+mListaClientes.getStrCedula());
            holder.lblFecha.setText(mListaClientes.getStrFecha());
            holder.lblCelular.setText("Cel: "+mListaClientes.getStrCelCli());
            holder.lblCorp.setText(mListaClientes.getStrCodCiu());
            holder.lblDir.setText("Dir:" +mListaClientes.getStrDireccion());
        }

        if(mListaClientes.getStrNomCli().equals("") || mListaClientes.getStrNomCli().isEmpty())
            holder.lblNombre.setText(mListaClientes.getStrCompania());
        else
            holder.lblNombre.setText(mListaClientes.getStrNomCli());

        /*
        if(mListaClientes.getIntCorp() == 1)
            holder.lblCorp.setText("CORP");
        else
            holder.lblCorp.setText("");*/

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
        TextView lblNombre, lblCedula, lblFecha, lblCelular, lblCorp, lblDir;
        CircleImageView circleImageView;
    }
}