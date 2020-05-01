package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class comparaison extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static final int ENABLE_BLUETOOTH = 1;
    private static final int DISCOVERY_REQUEST = 2;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private IntentFilter filter = new IntentFilter();
    private LinearLayout scrollView;
    private BluetoothSocket serverSocket;
    private BluetoothSocket clientSocket;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparaison);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {
            Toast.makeText(this, " Device does not support Bluetooth ", Toast.LENGTH_LONG ).show();
        }
        else{
            scrollView =  findViewById(R.id.comparaison_scroll_bluetooth);
            Button button = new Button(this);
            button.setText("accept");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptThread.run();
                }
            });
            scrollView.addView(button);
            initBluetooth();
            makeDiscoverable();
            acceptThread = new AcceptThread();
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        acceptThread.cancel();
        connectThread.cancel();
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                Log.d("TAG", deviceName);
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Button b = new Button(context);
                b.setText(deviceName);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectThread =  new ConnectThread(device);
                        connectThread.run();
                        /*acceptThread = new AcceptThread();
                        acceptThread.run();*/
                    }
                });
                scrollView.addView(b);
            }
        }
    };



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














    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Server",UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Log.e("TAG", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("TAG", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    setContentView(R.layout.bluetooth);
                    Log.d(String.valueOf(socket.isConnected()), "run: ");
                    serverSocket = socket;
                    makeButtonReceive();
                    //manageMyConnectedSocket(socket); -------------------------------------------------------------------------------
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the connect socket", e);
            }
        }
    }

    private void makeButtonReceive() {
        Button b = findViewById(R.id.button);
        b.setText("recevoir");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*Toast t = new Toast(getApplicationContext());
                    t.setText(String.valueOf();
                    t.show();*/
                    TextView t = findViewById(R.id.textView3);
                    t.setText(String.valueOf(serverSocket.getInputStream().read()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Log.e("TAG", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            Log.d(String.valueOf(mmSocket.isConnected()), "run: ");
            setContentView(R.layout.bluetooth);
            clientSocket = mmSocket;
            makeButtonSend();


            //manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
    }

    private void makeButtonSend() {
        Button b = findViewById(R.id.button);
        b.setText("envoyer");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clientSocket.getOutputStream().write(15);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
