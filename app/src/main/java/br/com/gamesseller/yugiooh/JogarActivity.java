package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JogarActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;

    Button btnListaDeDispositivosPareados;
    BluetoothAdapter bluetoothAdapter;

    boolean verificador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogar);

        btnListaDeDispositivosPareados = (Button)findViewById(R.id.listaDispositivosPareados);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        verificador = false;

        ChecarEstadoDoBluetooth();
        btnListaDeDispositivosPareados.setOnClickListener(btnListaDeDispositivosPareadosOnClickListener);

    }

    private void ChecarEstadoDoBluetooth(){

        if (verificador == false) {
            if (bluetoothAdapter == null) {

            } else {

                if (bluetoothAdapter.isEnabled()) {
                    if (bluetoothAdapter.isDiscovering()) {

                    } else {
                        btnListaDeDispositivosPareados.setEnabled(true);
                    }
                } else {

                    verificador = true;
                }
            }
        }

    }

    private Button.OnClickListener btnListaDeDispositivosPareadosOnClickListener
            = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {

            if(bluetoothAdapter.isEnabled()) {

                Intent intent = new Intent();
                intent.setClass(JogarActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_PAIRED_DEVICE);

            }else{
                Intent ativarBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ativarBluetoothIntent, REQUEST_ENABLE_BT);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            ChecarEstadoDoBluetooth();

            if(bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent();
                intent.setClass(JogarActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
            }else{

            }

        }

        if (requestCode == REQUEST_PAIRED_DEVICE){
            if(resultCode == RESULT_OK){

            }
        }
    }



}
