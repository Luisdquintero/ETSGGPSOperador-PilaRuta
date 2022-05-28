package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.R;
import com.appetesg.estusolucionTranscarga.modelos.DocumentosVehiculo;

import java.util.ArrayList;


public class ListaDocumentosVehiculoAdapter extends BaseAdapter {
    private ArrayList<DocumentosVehiculo> questionsList;
    private LayoutInflater inflter;

    public ListaDocumentosVehiculoAdapter(Context applicationContext, ArrayList<DocumentosVehiculo> questionsList) {
        this.questionsList = questionsList;

        inflter = (LayoutInflater.from(applicationContext));

        Log.i("oncreate", "oncreate");
    }

    @Override
    public int getCount() {
        return questionsList.size();
    }

    @Override
    public Object getItem(int i) {
        return questionsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.items_preguntas_pesv, null);

        TextView question = (TextView) view.findViewById(R.id.questionPesv);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupResPesv);

        final DocumentosVehiculo preguntaActual = (DocumentosVehiculo) getItem(i);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.yes:
                        preguntaActual.setRespuesta("true");
                        break;
                    case R.id.no:
                        preguntaActual.setRespuesta("false");
                        break;
                }
            }
        });

        question.setText(questionsList.get(i).getDescripcion());

        if (preguntaActual.getRespuesta().equals("true")) {
            radioGroup.check(R.id.yes);
        } else {
            radioGroup.check(R.id.no);
        }

        return view;
    }
}