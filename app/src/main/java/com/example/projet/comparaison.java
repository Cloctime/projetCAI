package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class comparaison extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private static final int ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparaison);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {
            Toast.makeText (this, " Device does not support Bluetooth ", Toast.LENGTH_LONG ).show();
        }
        else{
            initBluetooth();
        }
    }

    private void initBluetooth(){
        if (!mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent ( BluetoothAdapter . ACTION_REQUEST_ENABLE );
            startActivityForResult ( intent , ENABLE_BLUETOOTH );
        }
    }
}
