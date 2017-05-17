package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    ConnectionThread connect;
    private static Context context;

    private static ArrayList<String> cartasJogador1 = new ArrayList<>();
    ArrayList<String> cartasJogador2 = new ArrayList<>();
    ArrayAdapter cartasAdapterJogador1;
    ArrayAdapter cartasAdapterJogador2;
    private static ListView lst_cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

        lst_cards = (ListView) findViewById(R.id.lst_cards);

        context = this;

        Intent intent = getIntent();
        String modo = "";

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
        EditText idCard = (EditText) findViewById(R.id.editText_idCard);
        EditText idAction = (EditText) findViewById(R.id.editText_idAction);
        String informacoes = idCard.getText().toString() + "$" + idAction.getText().toString();

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
                context.startActivity(new Intent(context, JogarActivity.class));
            }else if (dataString.equals("---S")){
                Toast.makeText(context,"Conectado com sucesso !",Toast.LENGTH_LONG).show();
            }else {
                String informacoes = new String(data);
                String[] output = informacoes.split("\\$");

                if (output[1].equals("1")){
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

                                cartasJogador1.add(carta.getNome());

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        context,
                                        android.R.layout.simple_list_item_1,
                                        cartasJogador1 );

                                lst_cards.setAdapter(arrayAdapter);

                                c.moveToNext();
                                contador++;
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                //statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                //        + data.getStringExtra("btDevAddress"));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionThread ct = new ConnectionThread();
        ct.cancel();
    }
}
