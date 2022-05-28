package com.appetesg.estusolucionTranscarga.utilidades;

import com.activeandroid.Configuration;
import com.activeandroid.content.ContentProvider;
import com.appetesg.estusolucionTranscarga.modelo_db.Monitoreo;
import com.appetesg.estusolucionTranscarga.modelo_db.UsuariosColegio;


/**
 * Created by RafaelCastro on 20/6/18.
 */

public class DBContentProvider extends ContentProvider {
    @Override
    protected Configuration getConfiguration() {
        Configuration.Builder builder = new Configuration.Builder(getContext());
        builder.addModelClass(Monitoreo.class);
        builder.addModelClass(UsuariosColegio.class);
        return builder.create();
    }
}
