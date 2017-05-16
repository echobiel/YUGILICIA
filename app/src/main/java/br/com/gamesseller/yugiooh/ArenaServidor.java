package br.com.gamesseller.yugiooh;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

public class ArenaServidor extends Activity {

    ConnectionThread connect;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_servidor);

        context = this;

        //Coloca a conexao em espera
        connect = new ConnectionThread();
        connect.start();
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                Log.d("SEILA","Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                Log.d("SEILA", "Conectado :D");
            else {
                Log.d("SEILA", new String(data));
            }
        }
    };
}
