package br.com.gamesseller.yugiooh;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class JogarActivity extends Activity {

    Button btnListaDeDispositivosPareados, btnServer;
    ArrayList<String> listaDispositivosPareados = new ArrayList<>();
    ArrayAdapter btDispostivosAdapter;
    ListView dispositivosPareados;
    Context context;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;

    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");


    private final int REQUEST_DISCONNECT_DEVICE = 2;

    private BluetoothAdapter meuAdaptadorBluetooth = null;

    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice = null;

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    //- Exclusivo do servidor
    private BluetoothServerSocket mmServerSocket;
    private static final String NAME = "AppBT2_servidor";
    private int mState;

    // Constants that indicate the current connection state
    // Cria contanstes de estado
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogar);

        context = this;

        btnListaDeDispositivosPareados = (Button)findViewById(R.id.listaDispositivosPareados);
        btnServer = (Button) findViewById(R.id.criarSala);
        dispositivosPareados = (ListView) findViewById(R.id.lista_dispositivos_pareados);

        btDispostivosAdapter = new ArrayAdapter(this, R.layout.device_list, listaDispositivosPareados);


        btnListaDeDispositivosPareados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarEstadoDoBluetoothCliente();
            }
        });

        btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarEstadoDoBluetoothServer();
            }
        });


    }

    public void checarEstadoDoBluetoothCliente(){

        //Verificar se o bluetooth é suportado no dispositivo
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Se o mBluetoothAdapter estiver null o dispositivo não tem o necessário para o bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Dispostivo não suporta o Bluetooth",Toast.LENGTH_LONG);
        }
        //Se o bluetooth está ativado
        if (mBluetoothAdapter.isEnabled()) {

            startActivity(new Intent(this, ArenaCliente.class));

        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    public void checarEstadoDoBluetoothServer(){

        //Verificar se o bluetooth é suportado no dispositivo
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Se o mBluetoothAdapter estiver null o dispositivo não tem o necessário para o bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Dispostivo não suporta o Bluetooth",Toast.LENGTH_LONG);
        }
        //Se o bluetooth está ativado
        if (mBluetoothAdapter.isEnabled()) {

            startActivity(new Intent(this, ArenaServidor.class));

        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

}
