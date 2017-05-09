package br.com.gamesseller.yugiooh;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JogarActivity extends AppCompatActivity {

    Button btnListaDeDispositivosPareados, btnServer;
    ArrayList<String> listaDispositivosPareados = new ArrayList<>();
    ArrayAdapter btDispostivosAdapter;
    ListView dispositivosPareados;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogar);

        btnListaDeDispositivosPareados = (Button)findViewById(R.id.listaDispositivosPareados);
        btnServer = (Button) findViewById(R.id.criarSala);
        dispositivosPareados = (ListView) findViewById(R.id.lista_dispositivos_pareados);

        listaDispositivosPareados.add(0,"Paum");

        btDispostivosAdapter = new ArrayAdapter(this, R.layout.dispositivos_pareados, listaDispositivosPareados);


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
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    btDispostivosAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                dispositivosPareados.setAdapter(btDispostivosAdapter);
            }

        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }



}
