package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class Comparison extends AppCompatActivity {
    private BluetoothSocket serverSocket;
    private BluetoothSocket clientSocket;

    private Sensor sensor;
    private SensorManager sensorManager;
    private int precision;
    private Vibrator v;
    private int vibrate;
    private boolean mesurePlan;
    private boolean mesureAngle;
    private int angle;
    private TextView textViewMyAngles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);
        this.serverSocket = Connexion.serverSocket;
        this.clientSocket = Connexion.clientSocket;
        Connexion.finishActivity();

        initializeSensorsAndTransmition();
    }

    public void initializeSensorsAndTransmition(){
        //set default value of precision
        precision = 3;

        //setup view content
        setupViewContent();

        // Initializing vibrator
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate = 0;
    }

    public void setupViewContent(){
        setContentView(R.layout.bluetooth);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                precision = progress+1;
                TextView textView = findViewById(R.id.precision);
                textView.setText(String.valueOf(precision));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar seekBar1 = findViewById(R.id.seekBarAngle);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angle = progress;
                TextView textView = findViewById(R.id.textViewAngle);
                textView.setText(String.valueOf(angle));
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
        angle = 90;
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
                if(isChecked){
                    TextView textViewAngle = findViewById(R.id.textViewAngle);
                    TextView textViewAngleValue = findViewById(R.id.textViewAngleValue);
                    SeekBar seekBarAngle = findViewById(R.id.seekBarAngle);
                    textViewAngle.setVisibility(View.VISIBLE);
                    textViewAngleValue.setVisibility(View.VISIBLE);
                    seekBarAngle.setVisibility(View.VISIBLE);

                    //disable sensor listener
                    sensorManager.unregisterListener(gyroListener);
                }
            }
        });
        switchPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchAngle.setChecked(!isChecked);
                mesurePlan = isChecked;
                mesureAngle = !isChecked;
                if(isChecked){
                    TextView textViewAngle = findViewById(R.id.textViewAngle);
                    TextView textViewAngleValue = findViewById(R.id.textViewAngleValue);
                    SeekBar seekBarAngle = findViewById(R.id.seekBarAngle);
                    textViewAngle.setVisibility(View.INVISIBLE);
                    textViewAngleValue.setVisibility(View.INVISIBLE);
                    seekBarAngle.setVisibility(View.INVISIBLE);

                    //enable listener
                    sensorManager.registerListener(gyroListener, sensor, 99999);
                }
            }
        });

        textViewMyAngles = findViewById(R.id.textViewMyAngles);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                compare();
            }
        };
        textViewMyAngles.addTextChangedListener(watcher);
    }



    public void compare(){
        String stringValues[] = String.valueOf(textViewMyAngles.getText()).split("/");
        double xValue = Double.parseDouble(stringValues[0]);
        double yValue = Double.parseDouble(stringValues[1]);

        String stringToSend = String.format("%.1f", xValue)+"/"+String.format("%.1f", yValue);
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
            String axesValues[] = s.split("/");


            //show devices rotation differences




            TextView t = findViewById(R.id.textViewXYZ);
            double xDiff = Math.abs(xValue)-Math.abs(Double.parseDouble(axesValues[0]));
            double yDiff = Math.abs(yValue)-Math.abs(Double.parseDouble(axesValues[1]));


            t.setText("Différence en x: "+String.format("%.1f",xDiff)+"\nDifférence en y: "+String.format("%.1f",yDiff));

            ConstraintLayout constraintLayout = findViewById(R.id.bluetoothXmlLayout);
            if (mesurePlan == true){




                /*if (Math.abs(xDiff) <= precision && Math.abs(yDiff) <= precision && Math.abs(zDiff) <= precision){
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
                }*/
            }
            if (mesureAngle == true ){

            }




        }
        catch (Exception e){
        }
    }







    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            //send sensor values


        }
    };

    public String stringVector(double[] v){
        String s="";
        for(int i=0; i<v.length;i++){
            s+="\n";
            s+=String.format("%.1f", v[i]);
        }
        return s;
    }
}
