package com.example.japf.myhelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by japf on 16/06/2016.
 * Inspired in http://cell0907.blogspot.com.es/2013/10/tutorial-for-drawing-in-android.html
 */
public class BallSurfaceView  extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    public final String TimerMode = "TimerMode";
    public final String TouchMode = "TouchMode";
    public final String AccelMode = "AccelMode";

    private SurfaceHolder holder;
    private BallThread ballThread;
    private String mode = TimerMode;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    public BallSurfaceView(Context context){
        super(context);
        holder = getHolder();
        holder.addCallback(this);

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setMode(String mode){

        String lastMode = new String(mode);

        if (mode.equals(TimerMode) || mode.equals(TouchMode) || mode.equals(AccelMode)){
            this.mode = mode;
            Log.d("BallSurfaceView", "MODE: " + mode + "**********************");
            if (lastMode == AccelMode && mode != AccelMode){
                mSensorManager.unregisterListener(this);
            }
            if (lastMode != AccelMode && mode == AccelMode){
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me){
        if (ballThread != null) {
            ballThread.setBlueBallCoords((int)me.getX(), (int)me.getY());
            ballThread.raiseNotification();
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (ballThread != null) {
            ballThread.setGreenBallCoords((int) event.values[0], (int) event.values[1]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (ballThread == null){
            ballThread = new BallThread(holder);
            ballThread.setRunning(true);
            ballThread.setSurfaceSize(width, height);
            ballThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (mode.equals(TouchMode)){
            ballThread.raiseNotification();
        }
        ballThread.setRunning(false);
        while (retry) {
            try {
                ballThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    // The objects to draw.
    private class Ball {
        private int x, y, vx, vy, radius, color;

        Ball(int x, int y, int vx, int vy, int radius, int color){
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.color = color;
        }
    }

    // The cartoonist thread :)
    public class BallThread extends Thread {
        private int mCanvasWidth;
        private int mCanvasHeight;
        private Ball rBall, bBall, gBall;
        private SurfaceHolder holder;
        private boolean running = false;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int refresh_rate = 100;


        public BallThread(SurfaceHolder holder){
            rBall =  new Ball( 30,  30, 72, 24, 60, Color.RED); // red ball
            bBall = new Ball( 60,  60, 36, 12, 120, Color.BLUE); // blue ball
            gBall = new Ball(180, 180, 18,  6, 240, Color.GREEN); // green ball
            this.holder = holder;
        }

        public synchronized void setBlueBallCoords(int x, int y){
            bBall.x = x;
            bBall.y = y;
        }

        public synchronized void setGreenBallCoords(int x, int y){
            if (gBall != null && mode.equals(AccelMode)) {
                gBall.x -= x;
                gBall.y -= y;
                // Make sure we do not draw outside the bounds of the view
                if (gBall.x <= gBall.radius) {
                    gBall.x = gBall.radius;
                } else if (gBall.x >= mCanvasWidth - gBall.radius) {
                    gBall.x = mCanvasWidth - gBall.radius;
                }

                if (gBall.y <= gBall.radius) {
                    gBall.y = gBall.radius;
                } else if (gBall.y >= mCanvasHeight - gBall.radius) {
                    gBall.y = mCanvasHeight - gBall.radius;
                }
            }
        }


        public synchronized void raiseNotification(){
            notify();
        }

        @Override
        public synchronized void run(){

            long previousTime, currentTime;
            previousTime = System.currentTimeMillis();
            Canvas canvas = null;
            int count = 0;

            Log.e("Graphics1Activity", "  running ... ------ ");

            while (running){
                // Look if time has past
                currentTime=System.currentTimeMillis();
                while ((currentTime-previousTime) < refresh_rate){
                    currentTime=System.currentTimeMillis();
                }
                previousTime = currentTime;

                // UPDATE balls positions according to mode.
                if (mode.equals(TimerMode)) {
                    rBall.x = ((rBall.x + rBall.vx) % (mCanvasWidth - 10));
                    rBall.y = ((rBall.y + rBall.vy) % (mCanvasHeight - 10));
                    bBall.x = ((bBall.x + bBall.vx) % (mCanvasWidth - 10));
                    bBall.y = ((bBall.y + bBall.vy) % (mCanvasHeight - 10));
                    gBall.x = ((gBall.x + gBall.vx) % (mCanvasWidth - 10));
                    gBall.y = ((gBall.y + gBall.vy) % (mCanvasHeight - 10));
                }
                else if (mode.equals(TouchMode)){
                    try{
                        wait();
                    }
                    catch(InterruptedException e){}

                }
                else if (mode.equals(AccelMode)){
                }

                // PAINT command.
                try {
                    canvas = holder.lockCanvas();
                    synchronized (holder) {
                        draw(canvas);
                    }
                }
                finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                // WAIT it timer mode
                if (mode == TimerMode || mode == AccelMode) {
                    try {
                        Thread.sleep(refresh_rate - 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e("Graphics1Activity", "  surface thread: exiting ... ------ ");
        }

        private void draw(Canvas canvas){

            canvas.drawColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            if (mode.equals(TimerMode)) {
                paint.setColor(rBall.color);
                canvas.drawCircle((float) rBall.x, (float) rBall.y, (float) rBall.radius, paint);
                paint.setColor(bBall.color);
                canvas.drawCircle((float) bBall.x, (float) bBall.y, (float) bBall.radius, paint);
                paint.setColor(gBall.color);
                canvas.drawCircle((float) gBall.x, (float) gBall.y, (float) gBall.radius, paint);
            }
            if (mode.equals(TouchMode)) {
                paint.setColor(bBall.color);
                canvas.drawCircle((float) bBall.x, (float) bBall.y, (float) bBall.radius, paint);
            }
            if (mode.equals(AccelMode)) {
                paint.setColor(gBall.color);
                canvas.drawCircle((float) gBall.x, (float) gBall.y, (float) gBall.radius, paint);
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (holder){
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
        }
    }
}
