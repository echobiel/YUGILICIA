package br.com.gamesseller.yugiooh;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Arena extends AppCompatActivity {


    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

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
            String dataString = new String(data);

            if (dataString.equals("---N")) {
                //statusMessage.setText("Ocorreu um erro durante a conexão D:");
            }else if (dataString.equals("---S")){
                //statusMessage.setText("Conectado :D");
            }else {

                Log.d("data", new String(data));
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                //statusMessage.setText("Bluetooth ativado :D");
            }
            else {
                //statusMessage.setText("Bluetooth não ativado :(");
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                //statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                //        + data.getStringExtra("btDevAddress"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            }
            else {
                //statusMessage.setText("Nenhum dispositivo selecionado :(");
            }
        }
    }


}
