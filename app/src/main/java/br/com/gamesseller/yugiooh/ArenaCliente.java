package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ArenaCliente extends Activity {
    /** Called when the activity is first created. */

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter meuAdaptadorBluetooth = null;
    ConnectionThread connect;

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
                startActivityForResult(intent, ENABLE_BLUETOOTH);
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
        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth ativado :D", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Bluetooth não ativado :(", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"), Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "Nenhum dispositivo selecionado :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendMessage(View view) {

        EditText messageBox = (EditText) findViewById(R.id.editText_MessageBox);
        String messageBoxString = messageBox.getText().toString();
        byte[] data =  messageBoxString.getBytes();
        connect.write(data);
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                Log.d("SEI","Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                Log.d("SEI", "Conectado :D");
            else {
                Log.d("SEI", new String(data));
            }
            Log.d("SEI", "test");
        }
    };
}