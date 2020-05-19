package com.example.projet;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class UpdateThread extends Thread {
    private long time;
    private final int fps = 20;
    private boolean toRun = false;
    private CustomSurfaceView movementView;
    private SurfaceHolder surfaceHolder;


    public boolean toUpdateP = true;

    public UpdateThread(CustomSurfaceView rMovementView) {
        movementView = rMovementView;
        surfaceHolder = movementView.getHolder();
    }

    public void setRunning(boolean run) {
        toRun = run;
    }

    @Override
    public void run() {

        Canvas c;
        while (toRun) {

            long cTime = System.currentTimeMillis();

            if ((cTime - time) <= (1000 / fps)) {
                c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);

                    if (toUpdateP) {movementView.updatePhysics();}
                    movementView.drawRedBall(c);
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
            time = cTime;
        }
    }
}
