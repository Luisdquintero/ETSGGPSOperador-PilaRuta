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
import com.appetesg.estusolucionTranscarga.modelos.CiudadesD;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CiudadesDesApapter extends BaseAdapter {
    protected AppCompatActivity activity;
    protected ArrayList<CiudadesD>items, itemFiltrados;
    CiudadesD mListaCiudadDestino;
    ViewHolder holder =  new ViewHolder();
    String TAG = "CiudadesDesAdapter";

        public CiudadesDesApapter(AppCompatActivity activity, ArrayList<CiudadesD> items) {
            super();
            this.activity = activity;
            this.items = items;
        }

        public CiudadesDesApapter()
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
            mListaCiudadDestino = items.get(position);
            CiudadesDesApapter.ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_lista_ciudades, null);
                holder = new ViewHolder();

                holder.lblCiudades = convertView.findViewById(R.id.lblCiudadDest);
                //holder.circleImageView = convertView.findViewById(R.id.cicleImageDep);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.lblCiudades.setText(mListaCiudadDestino.getStrNomCiuDe());

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
            TextView lblCiudades;
            CircleImageView circleImageView;
        }
}