package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class niveau extends AppCompatActivity {

    private Vibrator v;
    private  SensorManager sensorManager;
    private  Sensor sensor;
    private boolean twoAxes;
    private TextView textview1;
    private TextView textview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveau);

        // Initializing vibrator
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        // Initializing gyroscope
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        textview1 = findViewById(R.id.textView);
        textview2 =  findViewById(R.id.textView2);

        Switch s = findViewById(R.id.switch1);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    twoAxes =  true;
                }
                else{
                    twoAxes  = false;
                }
            }
        });

        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            textview1.setText("x value: "+String.valueOf(event.values[0])); //x value
            if(twoAxes) {
                textview2.setText("y value: " + String.valueOf(event.values[1])); //y value
            }
            else{
                textview2.setText("");
            }
        }
    };
}
