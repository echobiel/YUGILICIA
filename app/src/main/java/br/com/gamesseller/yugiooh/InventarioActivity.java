package br.com.gamesseller.yugiooh;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;

public class InventarioActivity extends AppCompatActivity {
    Context context;
    DynamicGridView grid_view_cartas;
    DynamicGridView grid_view_cartasUser;
    LinearLayout zoom;
    List<Carta> lstCartas1 = new ArrayList<>();
    List<Carta> lstCartas2 = new ArrayList<>();
    Integer idCarta;
    ViewSwitcher switch_botao;
    Integer verificador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        context = this;

        grid_view_cartas = (DynamicGridView) findViewById(R.id.grid_view_cartas);
        grid_view_cartasUser = (DynamicGridView) findViewById(R.id.grid_view_cartasUser);

        switch_botao = (ViewSwitcher) findViewById(R.id.switch_botao);

        zoom = (LinearLayout) findViewById(R.id.zoom);

        buscarCardsBanco();

        clickCard();
    }

    private void clickCard() {
        grid_view_cartasUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                zoomImagem(position);
                idCarta = lstCartas1.get(position).getIdCarta();
                //Mostra o botão de excluir
                switch_botao.setDisplayedChild(1);

            }
        });
        grid_view_cartas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                zoomImagem2(position);
                idCarta = lstCartas2.get(position).getIdCarta();
                //Mostra o botão de adicionar
                switch_botao.setDisplayedChild(0);

            }
        });
    }

    //Zoom apena dos itens do gridview contendo as cartas do deck
    public void zoomImagem(int position){
        int imagem = lstCartas1.get(position).getImagem();
        ImageView img_zoom = (ImageView) findViewById(R.id.img_zoom);

        if (imagem == R.drawable.emptycard){
            zoom.setVisibility(View.INVISIBLE);
        }else{
            int imagemZoom = lstCartas1.get(position).getImagemZoom();
            Picasso.with(context) // CARTA
                    .load(imagemZoom)
                    .into(img_zoom);

            zoom.setVisibility(View.VISIBLE);
        }
    }

    public void zoomImagem2(int position){
        int imagem = lstCartas2.get(position).getImagem();
        ImageView img_zoom = (ImageView) findViewById(R.id.img_zoom);

        if (imagem == R.drawable.emptycard){
            zoom.setVisibility(View.INVISIBLE);
        }else{
            int imagemZoom = lstCartas2.get(position).getImagemZoom();
            Picasso.with(context) // CARTA
                    .load(imagemZoom)
                    .into(img_zoom);

            zoom.setVisibility(View.VISIBLE);
        }
    }

    public void buscarCardsBanco() {
        DataBaseHelper dbHelper = new DataBaseHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        selectCartas(db);

        dbHelper = new DataBaseHelper(this.getApplicationContext());
        db = dbHelper.getReadableDatabase();

        selectDeck(db);
    }

    private void selectCartas(SQLiteDatabase db) {
        Cursor c;

        c = db.rawQuery("SELECT * FROM tbl_cartas",null);

        if (c.getCount() > 0){
            c.moveToFirst();

            int contador = 1;

            //Guarda as informações da carta
            while (contador <= c.getCount()){

                Carta carta = new Carta(c.getString(0), c.getString(2) , c.getInt(3),
                        c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8),c.getInt(9),c.getInt(0));

                Log.d("CARTAS", c.getInt(5) + " imagemZoom - " + c.getInt(9));

                lstCartas2.add(carta);

                c.moveToNext();
                contador++;
            }
        }

        CartaAdapter adapter = new CartaAdapter(this,lstCartas2 , R.layout.list_view_item_carta);

        grid_view_cartas.setAdapter(adapter);
    }

    private void selectDeck(SQLiteDatabase db) {
        Cursor c;

        c = db.rawQuery("SELECT * FROM tbl_cartas as c INNER JOIN tbl_cartasDeck as cd ON c.idCarta = cd.idCarta ORDER BY c.idCarta",null);

        int contador = 1;

        if (c.getCount() > 0){
            c.moveToFirst();

            //Guarda as informações da carta
            while (contador <= c.getCount()){

                Carta carta = new Carta(c.getString(1), c.getString(2) , c.getInt(3),
                        c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getInt(0));

                lstCartas1.add(carta);

                c.moveToNext();
                contador++;
            }
            while (contador <= 20){
                Carta carta = new Carta(R.drawable.emptycard);

                lstCartas1.add(carta);

                c.moveToNext();
                contador++;
            }
        }

        while (contador <= 20){
            Carta carta = new Carta(R.drawable.emptycard);

            lstCartas1.add(carta);

            c.moveToNext();
            contador++;
        }

        CartaAdapter adapter = new CartaAdapter(this, lstCartas1, R.layout.list_view_item_carta);

        grid_view_cartasUser.setAdapter(adapter);
    }

    private boolean selectCardRepetido(SQLiteDatabase db, Integer idCarta) {
        Cursor c;

        c = db.rawQuery("SELECT * FROM tbl_cartasDeck where idCarta = ? ORDER BY idCarta",new String[]{idCarta.toString()});
        //Contador para o funcionamento do while
        int contador = 1;
        //Contagem de cartas repetidas
        int cartasRepitidas = 0;

        if (c.getCount() > 0){
            c.moveToFirst();

            //Guarda as informações da carta
            while (contador <= c.getCount()){
                c.moveToNext();
                contador++;
                cartasRepitidas = cartasRepitidas + 1;
            }
        }

        if (cartasRepitidas < 3) {
            return true;
        }else {
            return false;
        }
    }

    public void fecharZoom(View view) {
        zoom.setVisibility(View.INVISIBLE);
    }

    public void addCarta(View view) {
        inserirCartaDeck();
        fecharZoom(view);
    }

    private void inserirCartaDeck() {
        //Banco de dados de escrita
        SQLiteDatabase db = new DataBaseHelper(context).getWritableDatabase();

        if (selectCardRepetido(db, idCarta)){
            ContentValues contentValues = new ContentValues();

            contentValues.put("idDeck", 1);
            contentValues.put("idCarta", idCarta);

            db.insert("tbl_cartasDeck", null, contentValues);

            DataBaseHelper dbHelper = new DataBaseHelper(this.getApplicationContext());
            db = dbHelper.getReadableDatabase();

            lstCartas1.clear();

            selectDeck(db);
        }else {
            Toast.makeText(context,"Já existem 3 cartas no deck !",Toast.LENGTH_SHORT).show();
        }

    }

    public void excluirCarta(View view) {
        deleteCard();
        fecharZoom(view);
    }

    private void deleteCard() {
        SQLiteDatabase db = new DataBaseHelper(context).getReadableDatabase();

        Cursor c;

        Integer idCartasDeck = 0;

        c = db.rawQuery("SELECT * FROM tbl_cartasDeck where idCarta = ? ORDER BY idCartasDeck",new String[]{idCarta.toString()});

        if (c.getCount() > 0){
            c.moveToLast();

            idCartasDeck = c.getInt(0);
        }

        db = new DataBaseHelper(context).getWritableDatabase();

        db.delete("tbl_cartasDeck","idCartasDeck = ?", new String[]{idCartasDeck.toString()});

        DataBaseHelper dbHelper = new DataBaseHelper(this.getApplicationContext());
        db = dbHelper.getReadableDatabase();

        lstCartas1.clear();

        selectDeck(db);
    }
}
