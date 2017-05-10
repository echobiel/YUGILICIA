package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

public class ArenaCliente extends Activity {
    /** Called when the activity is first created. */

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter meuAdaptadorBluetooth = null;
    private BluetoothServerSocket mmServerSocket;
    private final int REQUEST_CONNECT_DEVICE = 1;
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Name for the SDP record when creating server socket
    private static final String NAME = "AppBT2_servidor";
    private int mState;

    // Constants that indicate the current connection state
    // Cria contanstes de estado
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_cliente);

        //Verificar se o bluetooth é suportado no dispositivo
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                Intent intent = new Intent();
                intent.setClass(ArenaCliente.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
            }

        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        meuAdaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();

        // Testa disponibilidade do Bluetooth no dispositivo
        if (meuAdaptadorBluetooth == null) {
            Toast.makeText(this, "Bluetooth não disponivel", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Se Não BT habilitado, solicita habilitar
        if (!meuAdaptadorBluetooth.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth agora está habilidado
                    // Escuta da solicitação de conexão
                    try {
                        // Cria o socket utilizando o UUID
                        mmServerSocket = meuAdaptadorBluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

                        // This is a blocking call and will only return on a
                        // successful connection or an exception
                        BluetoothSocket mmSocket = mmServerSocket.accept();

                        // If a connection was accepted
                        if (mmSocket != null) {
                            synchronized (ArenaCliente.this) {
                                switch (mState) {
                                    case STATE_CONNECTING:
                                        // Situation normal. Start the connected thread.
                                        try {

                                            // Obtem os fluxos de entrada e saida que lidam com transmissões através do socket
                                            mmInStream = mmSocket.getInputStream();
                                            mmOutStream = mmSocket.getOutputStream();

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

                                                    // Reenvio da mensagem
                                                    byte[] send = readMessage.getBytes();
                                                    mmOutStream.write(send);

                                                } catch (IOException e) {
                                                    Toast.makeText(this, "Ocorreu um erro na transmissão da mensagem!", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                        }
                                        catch(IOException e){
                                            Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_LONG).show();
                                        }

                                        break;
                                    case STATE_CONNECTED:
                                        // Either not ready or already connected. Terminate new socket.
                                        try {
                                            mmSocket.close();
                                        } catch (IOException e) {
                                            Toast.makeText(this, "Nao foi possivel fechar o socket!", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                }
                            }
                        }

                    }
                    catch(IOException e){
                        Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Bluetooth não habilitado corretamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}