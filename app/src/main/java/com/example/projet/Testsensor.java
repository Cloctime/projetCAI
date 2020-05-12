package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Testsensor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);
        RotationSensor rotationSensor = new RotationSensor();
        TextView t = findViewById(R.id.textViewTestSensor);
        rotationSensor.setup(this, t);
    }
}
