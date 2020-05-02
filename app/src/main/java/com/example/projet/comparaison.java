package com.example.projet;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
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
    private Sensor sensor;
    private int precision;
    private Vibrator v;
    private int vibrate;
    private boolean mesurePlan;
    private boolean mesureAngle;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparaison);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {
            Toast.makeText(this, " Device does not support Bluetooth ", Toast.LENGTH_LONG ).show();
        }
        else{
            scrollView =  findViewById(R.id.comparaison_scroll_bluetooth);
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
        if (acceptThread != null){
            acceptThread.cancel();
        }
        if (connectThread != null) {
            connectThread.cancel();
        }
    }






    //broadcast receiver -----------------------------------------------------------------------------------------
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
                    }
                });
                scrollView.addView(b);
            }
        }
    };



    //bluetooth init ---------------------------------------------------------------------------

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












    //accept thread -------------------------------------------------------------------------------

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
                    SeekBar seekBar = findViewById(R.id.seekBar);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            precision = progress;
                            TextView textView = findViewById(R.id.precision);
                            textView.setText(String.valueOf(progress));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    mesurePlan = true;
                    mesureAngle = false;
                    final Switch switchAngle = findViewById(R.id.switchAngle);
                    final Switch switchPlan = findViewById(R.id.switchPlan);
                    switchPlan.setChecked(true);
                    switchAngle.setChecked(false);
                    switchAngle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            switchPlan.setChecked(!isChecked);
                            mesureAngle = isChecked;
                            mesurePlan = !isChecked;
                        }
                    });
                    switchPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            switchAngle.setChecked(!isChecked);
                            mesurePlan = isChecked;
                            mesureAngle = !isChecked;
                        }
                    });
                    initializeSensorsAndTransmition();
                    //makeButtonReceive();
                    //makeButtonSend();
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









    // connect thread ----------------------------------------------------------------------------

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
            clientSocket = mmSocket;
            acceptThread.run();

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











    /*private void makeButtonSend() {
        Button b = findViewById(R.id.buttonSend);
        b.setText("envoyer");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText editText =  findViewById(R.id.editTextToSend);
                    String stringToSend = editText.getText().toString();
                    byte[] bytes  = stringToSend.getBytes();
                    clientSocket.getOutputStream().write((bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }






    private void makeButtonReceive() {
        Button b = findViewById(R.id.buttonReceive);
        b.setText("recevoir");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TextView t = findViewById(R.id.textViewReceive);
                    InputStream inputStream = serverSocket.getInputStream();
                    String s = "";
                    while(inputStream.available()!=0){
                        s+= (char)inputStream.read();
                    }
                    t.setText(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/










    public void initializeSensorsAndTransmition(){
        precision = 3;
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(gyroListener, sensor, 99999);
        // Initializing vibrator
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate = 0;
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            //send sensor values
            double xValue = (event.values[0])*90/9.81;
            double yValue = (event.values[1])*90/9.81;
            double zValue = (event.values[2])*90/9.81;
            String stringToSend = String.format("%.1f", xValue)+"/"+String.format("%.1f", yValue)+"/"+
                    String.format("%.1f", zValue);
            byte[] bytes  = stringToSend.getBytes();

            try {
                clientSocket.getOutputStream().write((bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //get other device values
            try {
                InputStream inputStream = serverSocket.getInputStream();
                String s = "";
                while(inputStream.available()!=0){
                    s+= (char)inputStream.read();
                }


                //show devices rotation differences
                try {
                    String axesValues[] = s.split("/");
                    double xDiff = Math.abs(xValue)-Math.abs(Double.parseDouble(axesValues[0]));
                    double yDiff = Math.abs(yValue)-Math.abs(Double.parseDouble(axesValues[1]));
                    double zDiff = Math.abs(zValue)-Math.abs(Double.parseDouble(axesValues[2]));

                    TextView t = findViewById(R.id.textViewXYZ);
                    t.setText("Différence en x: "+String.format("%.1f",xDiff)+"\nDifférence en y: "+String.format("%.1f",yDiff)+
                            "\nDifférence en z: "+String.format("%.1f",zDiff));
                    ConstraintLayout constraintLayout = findViewById(R.id.bluetoothXmlLayout);
                    if (mesurePlan == true){
                        if (Math.abs(xDiff) <= precision && Math.abs(yDiff) <= precision && Math.abs(zDiff) <= precision){
                            if (vibrate == 0){
                                v.vibrate(100);
                                vibrate = 1;
                            }
                            if (vibrate == 1){
                                vibrate = -1;
                            }
                            constraintLayout.setBackgroundColor(Color.argb(50,0,255,0));
                        }
                        else{
                            vibrate = 0;
                            constraintLayout.setBackgroundColor(Color.WHITE);
                        }
                    }
                    if (mesureAngle == true ){
                        if(Math.abs(Math.abs(yDiff)-Math.abs(zDiff)) <= precision && Math.abs(xDiff)<=precision
                        && Math.abs(yDiff) > precision && Math.abs(zDiff)>precision){
                            if (vibrate == 0){
                                v.vibrate(100);
                                vibrate = 1;
                            }
                            if (vibrate == 1){
                                vibrate = -1;
                            }
                            constraintLayout.setBackgroundColor(Color.argb(50,0,255,0));
                        }
                        else{
                            vibrate = 0;
                            constraintLayout.setBackgroundColor(Color.WHITE);
                        }

                    }



                }
                catch (Exception e){
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };



}
