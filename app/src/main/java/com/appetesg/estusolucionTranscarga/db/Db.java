package com.appetesg.estusolucionTranscarga.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ing. Juan Antonio on 05/07/2017.
 */

public class Db extends SQLiteOpenHelper
{
    public Db(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    String create_guias = "Create Table guias(strGuia TEXT"
            + " unique , " +
            "   strProducto TEXT ," +
            "   strDestinatario TEXT,"+
            "   strValor TEXT," +
            "   strPeso  TEXT," +
            "   strDireccion TEXT,remitente TEXT, teldes TEXT,dircli TEXT,nomprd TEXT,pedido1 TEXT,pendiente integer," +
            " IdUsuario integer,DocumentoReferencia TEXT,Fecha text, Estado integer,Latitud TEXT, Longitud TEXT," +
            " Imagen TEXT,srtRecibido TEXT );";
    String create_estados = "Create Table estados(CODEST TEXT"
            + " unique , " +
            "   NOMEST TEXT);";

    //Tabla Bioseguridad LEGA 2020-11-20

    String create_bioseguridad = "Create Table bioseguridad(intId integer"+
            " unique ,"+
            " strDescripcion TEXT,"+
            " strRespuesta TEXT,"+
            " idUsuario integer,"+
            " idPlaca integer,"+
            " strTemp TEXT,"+
            " pendiente integer);";

    String create_pesv = "Create Table pesv( intId Integer"+
            " unique,"+
            " strDescripcion TEXT,"+
            " strRespuesta TEXT,"+
            " idUsuario integer, "+
            " idPlaca integer,"+
            " pendiente integer);";

    String create_historico = "Create Table Historico(strPedido1 Text"+
            " unique," +
            " intCantidad Integer,"+
            " intCodusu Integer,"+
            " strCompaniaOri Text,"+
            " strCompaniaDest Text,"+
            " strCiudadDest Text,"+
            " strCiudadOri Text,"+
            " strDirOri Text,"+
            " strDirDest Text,"+
            " strFormaPago Text,"+
            " strValPag Text,"+
            " strValorUnico Text,"+
            " strFechas Text,"+
            " strCelCli Text,"+
            " strCelDes Text,"+
            " strNomPrd Text,"+
            " strContenido Text,"+
            " strPesoPaq Text,"+
            " strValorDec Text,"+
            " intCodEst Integer,"+
            " intCodCli Integer,"+
            " strPuertaEmbarque Text,"+
            " strValFlete Text,"+
            " strQR Text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(create_guias );
        db.execSQL(create_estados );
        db.execSQL(create_bioseguridad);
        db.execSQL(create_pesv);
        db.execSQL(create_historico);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS guias");
        db.execSQL("DROP TABLE IF EXISTS estados");
        db.execSQL("DROP TABLE IF EXISTS bioseguridad");
        db.execSQL("DROP TABLE IF EXISTS pesv");
        db.execSQL("DROP TABLE IF EXISTS Historico");

        db.execSQL(create_guias );
        db.execSQL(create_estados );
        db.execSQL(create_bioseguridad);
        db.execSQL(create_pesv);
        db.execSQL(create_historico);
    }


}
