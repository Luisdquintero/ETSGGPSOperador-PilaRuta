package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.Urls;

import java.util.ArrayList;

public class ListaUrlsAdapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<Urls> items,itemsFiltrados;
    Urls urls;
    ViewHolder holder=new ViewHolder();
    String TAG="ListaUrlsAdapter";

    public ListaUrlsAdapter(AppCompatActivity activity, ArrayList<Urls> items) {
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

        urls = items.get(position);
        ViewHolder holder = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_urls, null);
            holder = new ViewHolder();

            holder.lblColegio = convertView.findViewById(R.id.lblColegio);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.lblColegio.setText(urls.getNombre());

        return convertView;
    }


    static class ViewHolder {
        TextView lblColegio;


    }

}


