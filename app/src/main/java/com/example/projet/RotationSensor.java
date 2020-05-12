package com.example.projet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RotationSensor {

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] gravityReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private android.hardware.Sensor accelerometer;
    private android.hardware.Sensor magneticField;
    private android.hardware.Sensor gravity;

    private Activity activity;
    private TextView textView;

    private float[] valuesXYZ= new float[3];

    public RotationSensor() {

    }

    public void setup(Activity activity, TextView t){
        this.textView = t;
        this.activity = activity;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        magneticField = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(magneticFieldListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(gravity != null){
            sensorManager.registerListener(gravityListener, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
            updateOrientationAngles();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private  SensorEventListener magneticFieldListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
            updateOrientationAngles();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener gravityListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, gravityReading,
                    0, gravityReading.length);
            updateOrientationAngles();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };









    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    @SuppressLint("DefaultLocale")
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        // "mOrientationAngles" now has up-to-date information.



        //correction of non linear sensor values
        double x = correctionValues(rotationMatrix[6]*90);
        double y = correctionValues(rotationMatrix[7]*90);
        double z = rotationMatrix[0];

        //getting 360° angle from rotation matrix and x, y, z
        x=getRotationY(x, rotationMatrix[8]);
        y = getRotationY(y, rotationMatrix[8]);





        textView.setText(String.format("%.1f", x)+"/"+String.format("%.1f", y)
                /*+"\norientation z: "+String.format("%.1f", z)
                +"\n\n"+String.format("%.1f", rotationMatrix[0]*90)
                +"\n"+String.format("%.1f", rotationMatrix[1]*90)
                +"\n"+String.format("%.1f", rotationMatrix[2]*90)
                +"\n"+String.format("%.1f", rotationMatrix[3]*90)
                +"\n"+String.format("%.1f", rotationMatrix[4]*90)
                +"\n"+String.format("%.1f", rotationMatrix[5]*90)
                +"\n"+String.format("%.1f", rotationMatrix[6]*90)
                +"\n"+String.format("%.1f", rotationMatrix[7]*90)
                +"\n"+String.format("%.1f", rotationMatrix[8]*90)*/);

        vectorProcessing(rotationMatrix);

    }

    public double correctionValues(double value){
        int sign =1;
        if(value <0){
            sign = -1;
            value = -1*value;
        }
        double a1=0.262*value;
        double a2=68.38*0.001*Math.pow(value,2);
        double a3 = -3.724*0.001*Math.pow(value,3);
        double a4=89.887*0.000001*Math.pow(value,4);
        double a5=-991.557*0.000000001*Math.pow(value,5);
        double a6=4.113*0.000000001*Math.pow(value,6);

        return sign*(a1+a2+a3+a4+a5+a6);

    }

    public double getRotationX(double value, double sign){
        if(sign<0 && value>0){
            //rien à faire
        }
        else if(sign>0 && value>0){
            value = 180-value;
        }
        else if(sign>0 && value<0){
            value = 180-value;
        }
        else if(sign<0 && value <0){
            value = 360+value;
        }
        return value;
    }

    public double getRotationY(double value, double sign){
        if(sign>0 && value>0){
            //rien à faire
        }
        else if(sign<0 && value>0){
            value = 180-value;
        }
        else if(sign<0 && value<0){
            value = 180-value;
        }
        else if(sign>0 && value <0){
            value = 360+value;
        }
        return value;
    }

    public float[] vectorProcessing(float[] vector){
        //processing vector construction
        return vector;
    }

}
