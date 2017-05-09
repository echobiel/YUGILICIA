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

    private final int REQUEST_CONNECT_DEVICE = 1;
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
                checarEstadoDoBluetooth();
            }
        });


    }

    public void checarEstadoDoBluetooth(){

        //Verificar se o bluetooth é suportado no dispositivo
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Se o mBluetoothAdapter estiver null o dispositivo não tem o necessário para o bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Dispostivo não suporta o Bluetooth",Toast.LENGTH_LONG);
        }
        //Se o bluetooth está ativado
        if (mBluetoothAdapter.isEnabled()) {

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                Intent intent = new Intent();
                intent.setClass(JogarActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
            }

        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(this, "" + requestCode + " // " + REQUEST_CONNECT_DEVICE, Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    //Cancelar a descoberta
                    meuAdaptadorBluetooth.cancelDiscovery();

                    // Obtem o endereço do dispositivo
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Obtem o BluetoothDevice
                    mmDevice = meuAdaptadorBluetooth.getRemoteDevice(address);
                    try {
                        // Cria o socket utilizando o UUID
                        mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                        // Conecta ao dispositivo escolhido
                        mmSocket.connect();
                        // Obtem os fluxos de entrada e saida que lidam com transmissões através do socket
                        mmInStream = mmSocket.getInputStream();
                        mmOutStream = mmSocket.getOutputStream();

                        // Saida:
                        // Envio de uma mensagem pelo .write
                        String enviada = "Teste Rone";
                        byte[] send = enviada.getBytes();
                        mmOutStream.write(send);

                        // Entrada:
                        // bytes returnados da read()
                        int bytes;
                        // buffer de memória para o fluxo
                        byte[] read = new byte[1024];

                        // Continuar ouvindo o InputStream enquanto conectado
                        // O loop principal é dedicado a leitura do InputStream
                        while (true) {
                            try {
                                // Read from the InputStream
                                bytes = mmInStream.read(read);

                                String readMessage = new String(read);
                                Toast.makeText(this, readMessage, Toast.LENGTH_LONG).show();

                            } catch (IOException e) {
                                Toast.makeText(this, "Ocorreu um erro no recebimento da mensagem!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                    catch(IOException e){
                        Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_DISCONNECT_DEVICE:
                Toast.makeText(this, "O usuário não está conectado!", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
