package com.appetesg.estusolucionTranscarga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.modelos.MenuPrincipal;

import java.util.ArrayList;

/**
 * Created by RafaelCastro on 11/17/18.
 */

public class MenuAdapter extends BaseAdapter {

    protected AppCompatActivity activity;
    protected ArrayList<MenuPrincipal> items;
    MenuPrincipal menuPrincipal;
    ViewHolder holder=new ViewHolder();
    String TAG=MenuAdapter.class.getName();
    public MenuAdapter(AppCompatActivity activity, ArrayList<MenuPrincipal> items) {
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

        menuPrincipal = items.get(position);
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_guia
                    , null);
            holder = new ViewHolder();

            holder.lblNombreGuia = (TextView) convertView.findViewById(R.id.lblNombreGuia);
            holder.imgGuia = (ImageView) convertView.findViewById(R.id.imgGuia);



            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();

        }


        holder.lblNombreGuia.setText(menuPrincipal.getNombre());
        holder.imgGuia.setImageResource(menuPrincipal.getIcono());




        /*
        holder.fabInfo.setOnClickListener(this);
        holder.fabCorazon.setOnClickListener(this);
*/


        return convertView;
    }


    static class ViewHolder {
        ImageView imgGuia;
        TextView lblNombreGuia;

    }
}
