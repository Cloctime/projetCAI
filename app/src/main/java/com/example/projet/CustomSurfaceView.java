
package com.example.projet;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CustomSurfaceView<mode> extends SurfaceView implements SurfaceHolder.Callback,  View.OnTouchListener {

    // Paint
    private Paint mPaint;
    private Paint bPaint;
    private SurfaceHolder mHolder;

    // thread
    private UpdateThread updateThread;

    // ball
    private int circleRadius = 90;
    private int xPos;
    private int yPos;

    private int aPos;
    private int bPos;

    private int xOrigine;
    private int yOrigine;

    private int xVel = 7;
    private int yVel = 7;

    private float xGyro;
    private float yGyro;
    private int mode;



    private Vibrator vibrator;



    private Context mContext;

    SensorManager sensorManager;
    Sensor sensor;


    public CustomSurfaceView(Context context, int x, int y) {
        super(context);

        // Getting the holder
        mHolder = getHolder();
        mHolder.addCallback(this);  // for Thread

        // Initializing the paint object mPaint
        mPaint = new Paint();
        bPaint = new Paint();

        // Setting the color for the paint object
        mPaint.setColor(Color.GREEN);
        mPaint.setColor(Color.RED);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);


        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        xOrigine=x;
        yOrigine=y;

        aPos=yOrigine;
        bPos=yOrigine;

        mContext = context;
        mode=2;



    }



    public SensorEventListener gyro= new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            xGyro = event.values[0];
            yGyro = event.values[1];
            Log.d("x", String.valueOf(xGyro));

            Log.d("y", String.valueOf(yGyro));}

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void updatePhysics() {


        if (mode==1){
            xPos += xVel;
            bPos += yVel;
            if (bPos - circleRadius < 0 || bPos + circleRadius > getHeight()) {
                // top or  bottom
                if (bPos - circleRadius < 0) {
                    // top
                    bPos = circleRadius;
                } else {
                    //  bottom
                    bPos = getHeight() - circleRadius;
                }

                // reverse the y direction of the ball
                yVel *= -1;


            }

            if (xPos - circleRadius < 0 || xPos + circleRadius > getWidth()) {
                // sides
                if (xPos - circleRadius < 0) {
                    // left
                    xPos = circleRadius;
                } else {
                    // right
                    xPos = getWidth() - circleRadius;
                }

                // reverse the x direction of the ball
                xVel *= -1;
            }
        }
        else if (mode==2){
            sensorManager.registerListener(gyro, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            xPos -= 5*xGyro;
            bPos += 5*yGyro;
            if (bPos - circleRadius < 0 || bPos + circleRadius > getHeight()) {
                // top or  bottom
                if (bPos - circleRadius < 0) {
                    // top
                    bPos = circleRadius;
                } else {
                    //  bottom
                    bPos = getHeight() - circleRadius;
                }



            }

            if (xPos - circleRadius < 0 || xPos + circleRadius > getWidth()) {
                // sides
                if (xPos - circleRadius < 0) {
                    // left
                    xPos = circleRadius;
                } else {
                    // right
                    xPos = getWidth() - circleRadius;
                }

                // reverse the x direction of the ball

            }
        }}


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {

        /*int bColision = 0 ;

        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            updateThread.toUpdateP = true;
        }
        else   if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            updateThread.toUpdateP = false;
        }

        int xTemp = (int) motionEvent.getX();
        int yTemp = (int) motionEvent.getY();

        if (yTemp - circleRadius < 0 || yTemp + circleRadius > getHeight()) {
            // top or  bottom

            if (yTemp - circleRadius < 0) {
                // top
                yPos = circleRadius;
            } else {
                //  bottom
                yPos = getHeight() - circleRadius;
            }

            xPos = xTemp;
            bColision = 1;
        }

        if (xTemp - circleRadius < 0 || xTemp + circleRadius > getWidth()) {
            // sides
            if (xTemp - circleRadius < 0) {
                // left
                xPos = circleRadius;
            } else {
                // right
                xPos = getWidth() - circleRadius;
            }

            yPos = yTemp;
            bColision = 1;
        }

        if(bColision == 1) return true;

        xPos = (int) motionEvent.getX();
        yPos = (int) motionEvent.getY();

        */
        return true;
    }

    /* This method will be invoked to draw a circle in canvas. */
    public void drawRedBall(Canvas canvas)
    {
        // Draw the background
        canvas.drawColor(Color.WHITE);
        // Draw the circle.
        canvas.drawCircle(xPos, yPos,circleRadius, mPaint);
        canvas.drawCircle(aPos, bPos,circleRadius, bPaint);


    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        xPos = xOrigine;
        yPos = yOrigine;

        updateThread = new UpdateThread(this);
        updateThread.setRunning(true);
        updateThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        updateThread.setRunning(false);
        while (retry) {
            try {
                updateThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

}
