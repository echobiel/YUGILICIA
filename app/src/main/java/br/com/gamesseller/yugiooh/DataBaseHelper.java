package br.com.gamesseller.yugiooh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ecobiel on 21/03/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String NOME_BANCO = "yugilicia.db";
    private static final int VERSAO = 1;

    public DataBaseHelper(Context context){
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE tbl_cartas(" +
                "idCarta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "descricao TEXT," +
                "elemento INTEGER," +
                "nivel INTEGER," +
                "imagem INTEGER," +
                "tipo INTEGER," +
                "atk INTEGER," +
                "def INTEGER," +
                "imagemZoom INTEGER); ";

        db.execSQL(sql);

        // Tabela Relação entre cartas e o deck
        sql = "CREATE TABLE tbl_cartasDeck(" +
                "idCartasDeck INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idDeck INTEGER," +
                "idCarta INTEGER); ";

        db.execSQL(sql);

        sql = "INSERT or replace INTO tbl_cartas (nome, descricao, elemento, nivel, tipo, imagem, atk, def, imagemZoom)" +
                "values" +
                "('Jailson Mendes'," +
                "'Quando colocado em batalha, o URSO desperta e dizima, com extrema destreza e agressividade, " +
                "todos os montros de level menor que 3 do adversário.'," +
                "1," +
                "4," +
                "1," +
                R.drawable.jailson + "," +
                "300," +
                "1200," +
                R.drawable.jailson_mendes + ");";

        db.execSQL(sql);

        sql = "INSERT or replace INTO tbl_cartas (nome, descricao, elemento, nivel, tipo, imagem, atk, def, imagemZoom)" +
                "values" +
                "('Inhegas'," +
                "'Só é possível invocar a carta se for usado 1 monstro, de level 3 ou menos, como tributo.'," +
                "2," +//Elemento
                "6," +//Nível
                "2," +//Tipo
                R.drawable.inhegas_lista + "," +//ImagemList
                "500," +//ATK
                "300," +//DEF
                R.drawable.inhegas + ");";//ImagemZoom

        db.execSQL(sql);

        sql = "INSERT or replace INTO tbl_cartas (nome, descricao, elemento, nivel, tipo, imagem, atk, def, imagemZoom)" +
                "values" +
                "('Lobinho'," +
                "'Ataca com suas garras e fere com seus ouvidos com seus uivados'," +
                "3," +//Elemento
                "1," +//Nível
                "3," +//Tipo
                R.drawable.lobinho_lista + "," +//ImagemList
                "100," +//ATK
                "130," +//DEF
                R.drawable.lobinho + ");";//ImagemZoom

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS tbl_cartas");
        onCreate(db);
    }
}
