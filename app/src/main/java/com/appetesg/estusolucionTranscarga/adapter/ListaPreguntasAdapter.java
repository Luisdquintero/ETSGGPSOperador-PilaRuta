package com.appetesg.estusolucionTranscarga.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appetesg.estusolucionTranscarga.modelos.Pregunta;
import com.appetesg.estusolucionTranscarga.R;

import java.util.ArrayList;


public class ListaPreguntasAdapter extends BaseAdapter {
    private ArrayList<Pregunta> questionsList;
    private LayoutInflater inflter;

    public ListaPreguntasAdapter(Context applicationContext, ArrayList<Pregunta> questionsList) {
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
        view = inflter.inflate(R.layout.items_preguntas, null);

        TextView question = (TextView) view.findViewById(R.id.question);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupRes);

        final Pregunta preguntaActual = (Pregunta) getItem(i);

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