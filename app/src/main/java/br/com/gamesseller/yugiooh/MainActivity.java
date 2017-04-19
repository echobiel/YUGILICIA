package br.com.gamesseller.yugiooh;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    LinearLayout content_main;
    ImageView fundo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.imgbtn_jogar:
                startActivity(new Intent(this, JogarActivity.class));
                break;
            case R.id.imgbtn_inventario:
                startActivity(new Intent(this, InventarioActivity.class));
                break;
            case R.id.imgbtn_instrucoes:
                startActivity(new Intent(this, JogarActivity.class));
                break;
            case R.id.imgbtn_creditos:
                startActivity(new Intent(this, JogarActivity.class));
                break;
        }

    }
}
