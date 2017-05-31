package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Arena extends AppCompatActivity {


    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    private static Context context;
    private static ArrayList<Carta> cartasJogador = new ArrayList<>();
    private static List<Carta> lstCartas = new ArrayList<>();
    private static ListView lst_cards;

    public static Integer contagemCartas;
    public static CartaAdapter arrayAdapter;
    public static GridView grid_view_cartas_jogador;
    public static List<Carta> lstCartasMao = new ArrayList<>();


    TextView nome_jogador;
    TextView nome_jogador2;
    Button botao_sair;
    ArrayAdapter cartasAdapterJogador1;
    ArrayAdapter cartasAdapterJogador2;
    public static ConnectionThread connect;
    String modo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

        context = this;

        nome_jogador2 = (TextView)findViewById(R.id.nome_jogador2);
        botao_sair = (Button) findViewById(R.id.botao_sair);
        grid_view_cartas_jogador = (GridView) findViewById(R.id.grid_view_cartas_jogador);

        botao_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(modo.equals("cliente")){
                        String informacoes = "3$4";

                        byte[] data =  informacoes.getBytes();
                        connect.write(data);
                        startActivity(new Intent(context, JogarActivity.class));
                    }else {
                        finish();
                        startActivity(new Intent(context, JogarActivity.class));
                    }
                }catch(Exception e){
                    e.getMessage();
                }

            }
        });

        Intent intent = getIntent();


        if(intent != null){
            modo = intent.getStringExtra("modo");
        }

        Log.d("modo",modo);

        if (modo.equals("servidor")){
            waitConnection();
        }else if(modo.equals("client")){
            searchPairedDevices();
        }


    }

    public void searchPairedDevices() {
        Intent searchPairedDevicesIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void waitConnection() {

        connect = new ConnectionThread();
        connect.start();
    }

    public void sendAction(View view) {
        String informacoes = "1$2";

        byte[] data =  informacoes.getBytes();
        connect.write(data);
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N")) {
                Toast.makeText(context,"Houve um problema com a conexão !",Toast.LENGTH_LONG).show();
                lstCartasMao.clear();
                context.startActivity(new Intent(context, JogarActivity.class));
            }else if (dataString.equals("---S")){
                Toast.makeText(context,"Conectado com sucesso !",Toast.LENGTH_LONG).show();

                montaLstCartas();

                colocarDadosNaGridView();


            }else {
                String informacoes = new String(data);
                String[] output = informacoes.split("\\$");
            if (output[3].equals("4")){

                lstCartas.clear();
                lstCartasMao.clear();


            }else if (output[1].equals("1")){
                    if (output[0] != "") {
                        DataBaseHelper dbHelper = new DataBaseHelper(context);
                        SQLiteDatabase db = dbHelper.getReadableDatabase();

                        Cursor c;

                        c = db.rawQuery("SELECT * FROM tbl_cartas WHERE idCarta = ?", new String[]{output[0]});

                        if (c.getCount() > 0) {
                            c.moveToFirst();

                            int contador = 1;

                            //Guarda as informações da carta
                            while (contador <= c.getCount()) {

                                Carta carta = new Carta(c.getString(0), c.getString(2), c.getInt(3),
                                        c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getInt(0));

                                Log.d("CARTAS", c.getInt(5) + " imagemZoom - " + c.getInt(9));

                                cartasJogador.add(carta);

                                c.moveToNext();
                                contador++;
                            }
                        }
                    }
                }
            }
        }
    };

    private static void colocarDadosNaGridView() {

        contagemCartas = 0;

        if(lstCartas.size() >= 4) {

            while (contagemCartas < 4) {

                lstCartasMao.add(lstCartas.get(contagemCartas));

                arrayAdapter = new CartaAdapter(context, lstCartasMao, R.layout.layout_carta);

                contagemCartas++;

            }

            grid_view_cartas_jogador.setAdapter(arrayAdapter);

        }else{
            Toast.makeText(context, "Adicione cartas no seu inventário e volte a jogar!", Toast.LENGTH_LONG).show();
            finishMao();
        }
    }

    private static void montaLstCartas(){
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

        if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                nome_jogador2.setText(data.getStringExtra("btDevName"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                try {
                    connect.start();

                }catch(Exception e){

                }
            }
            else {
                Toast.makeText(this,"Não foi possível realizar a conexão !",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, JogarActivity.class));
            }
        }
    }

    //Quando é apertado o botão de voltar no celular
    @Override
    protected void onDestroy() {
        super.onDestroy();

        finishMao();


    }

    //Quando é apertado o botão de sair da activity
    @Override
    public void finish() {
        super.finish();

        finishMao();

    }

    public static void finishMao(){

        connect.cancel();
        lstCartasMao.clear();

    }
}
