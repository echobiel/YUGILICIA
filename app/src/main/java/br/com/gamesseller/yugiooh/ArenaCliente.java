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
import android.util.Log;
import android.widget.Toast;

public class ArenaCliente extends Activity {
    /** Called when the activity is first created. */

    private static final int REQUEST_ENABLE_BT = 1;

    private final int REQUEST_CONNECT_DEVICE = 1;
    private BluetoothAdapter meuAdaptadorBluetooth = null;

    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice = null;

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

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
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                Log.d("resultCode", resultCode + "");
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
                        Toast.makeText(this, "Ocorreu um erro! - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("test", "Ocorreu um erro! - " + e.getMessage());
                    }
                }
                break;
        }
    }
}