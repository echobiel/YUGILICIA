package br.com.gamesseller.yugiooh;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout content_main;
    ImageView fundo;
    Button imgbtn_jogar;
    private static Context context;
    private static List<Carta> lstCartas = new ArrayList<>();

    View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        mDecorView = getWindow().getDecorView();
        imgbtn_jogar = (Button) findViewById(R.id.imgbtn_jogar);

        montaLstCartas();

        if(lstCartas.size() < 4){
            imgbtn_jogar.setEnabled(false);
            Toast.makeText(context, "Adicione cartas no seu inventário e volte a jogar!", Toast.LENGTH_LONG).show();
        }else{
            imgbtn_jogar.setEnabled(true);
        }
    }

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.imgbtn_jogar:
                startActivity(new Intent(this, JogarActivity.class));
                break;
            case R.id.imgbtn_inventario:
                startActivityForResult(new Intent(this, InventarioActivity.class), 1);
                break;
            case R.id.imgbtn_instrucoes:
                startActivity(new Intent(this, JogarActivity.class));
                break;
            case R.id.imgbtn_creditos:
                startActivity(new Intent(this, JogarActivity.class));
                break;
        }

    }

    private static void montaLstCartas(){

        lstCartas.clear();
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c;

        c = db.rawQuery("SELECT c.idCarta, c.nome, c.descricao, c.elemento, c.nivel, c.imagem, c.tipo, c.atk, c.def, c.imagemZoom " +
                "FROM tbl_cartasDeck as cd " +
                "LEFT JOIN tbl_cartas as c ON c.idCarta = cd.idCarta ORDER BY RANDOM()",null);

        if (c.getCount() > 0){
            c.moveToFirst();

            int contador = 1;

            //Guarda as informações da carta
            while (contador <= c.getCount()){

                Carta carta = new Carta(c.getString(1), c.getString(2) , c.getInt(3),
                        c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8),c.getInt(9),c.getInt(0));

                Log.d("CARTAS", c.getString(1) + " imagemZoom - " + c.getInt(9));

                lstCartas.add(carta);

                c.moveToNext();
                contador++;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        montaLstCartas();

        if(lstCartas.size() < 4){
            imgbtn_jogar.setEnabled(false);
            Toast.makeText(context, "Adicione cartas no seu inventário e comece a jogar!", Toast.LENGTH_LONG).show();
        }else{
            imgbtn_jogar.setEnabled(true);
        }
    }
}
