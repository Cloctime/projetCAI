package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class comparaison extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private static final int ENABLE_BLUETOOTH = 1;
    private static final int DISCOVERY_REQUEST = 2;
    private IntentFilter filter = new IntentFilter();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //mesgText.setText(" Discovery started ");
                Log.v("TAG", " ACTION_DISCOVERY_STARTED ");
// discovery starts , we can show progress dialog or perform other tasks
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
// discovery finishes , dismis progress dialog
                //mesgText.setText(" Discovery finished ");
                Log.v("TAG", "ACTION_DISCOVERY_FINISHED");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)){
// bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!deviceListName.isEmpty() && firstElement) {
                    firstElement = false;
                    deviceListName.clear();
                }
                deviceBT.add(device);
                deviceListName.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
                Log.v("TAG", " Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED) {
                    Log.v("TAG", " Paired ");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.
                        BOND_BONDED) {
                    Log.v("TAG", " Unpaired ");
                }
            }
        }
    };






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparaison);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {
            Toast.makeText(this, " Device does not support Bluetooth ", Toast.LENGTH_LONG ).show();
        }
        else{
            initBluetooth();
            makeDiscoverable();
            mBluetoothAdapter.startDiscovery();
        }

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED );
        filter.addAction(BluetoothDevice.ACTION_FOUND );
        filter.addAction( BluetoothAdapter.ACTION_DISCOVERY_STARTED );
        filter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );
        filter.addAction( BluetoothDevice.ACTION_BOND_STATE_CHANGED );
        registerReceiver(mReceiver, filter);
    }



    //bluetooth part ---------------------------------------------------------------------------

    private void initBluetooth(){
        if (!mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent , ENABLE_BLUETOOTH);
        }
    }

    protected void onActivityResult(int requestCode ,int resultCode ,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH)
            if (resultCode == RESULT_OK) {
                Log.v("TAG", " BT = " + ENABLE_BLUETOOTH);
            }
        if (requestCode == DISCOVERY_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", " Discovery cancelled by user ");
            } else {
                Log.v("TAG", " Discovery allowed ");
            }
        }
    }

    private void makeDiscoverable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // discoverable for 5 minutes (~300 seconds )
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, DISCOVERY_REQUEST);
        Log.i( " Log " , " Discoverable ");
    }

}
