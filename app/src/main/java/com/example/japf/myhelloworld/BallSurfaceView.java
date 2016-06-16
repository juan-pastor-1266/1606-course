package com.example.japf.myhelloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by japf on 16/06/2016.
 * Inspired in http://cell0907.blogspot.com.es/2013/10/tutorial-for-drawing-in-android.html
 */
public class BallSurfaceView  extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private BallThread ballThread;

    public BallSurfaceView(Context context){
        super(context);
        holder = getHolder();
        holder.addCallback(this);
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

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getVx() {
            return vx;
        }

        public void setVx(int vx) {
            this.vx = vx;
        }

        public int getVy() {
            return vy;
        }

        public void setVy(int vy) {
            this.vy = vy;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    // The cartoonist thread :)
    public class BallThread extends Thread {
        private int mCanvasWidth;
        private int mCanvasHeight;
        private ArrayList<Ball> balls = new ArrayList<Ball>(); // Dynamic array with dots
        private SurfaceHolder holder;
        private boolean running = false;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int refresh_rate = 100;

        public BallThread(SurfaceHolder holder){
            balls.add(new Ball( 30,  30, 12, 8, 100, Color.RED)); // red ball
            balls.add(new Ball( 60,  60,  6, 4, 200, Color.BLUE)); // blue ball
            balls.add(new Ball(180, 180,  3, 2, 300, Color.GREEN)); // green ball
            this.holder = holder;
        }

        @Override
        public void run(){

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

                // UPDATE balls positions.
                for(Ball b: balls){
                    b.setX((b.x + b.vx)%(mCanvasWidth -10));
                    b.setY((b.y + b.vy)%(mCanvasHeight-10));
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
                        Log.e("Graphics1Activity", "  XXXXXXXXXX ");
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                // WAIT
                try {
                    Thread.sleep(refresh_rate-5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                if (count > 10){
                    count = 0;
                    Log.e("Graphics1Activity", "  onCreate: still running ... ------ ");
                }
            }
            Log.e("Graphics1Activity", "  onCreate: exiting ... ------ ");

        }

        private void draw(Canvas canvas){
            Ball currentBall = balls.get(0);
            canvas.drawColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText("hola", 200, 200, paint);
            for(Ball b: balls){
                currentBall=b;
                paint.setColor(currentBall.getColor());
                canvas.drawCircle((float)currentBall.getX(),
                        (float)currentBall.getY(),
                        (float)currentBall.getRadius(),
                        paint);
            }
            Log.e("Graphics1Activity", "  onCreate: drawing ... ------ " + currentBall.getX() + "," + currentBall.getY());
            paint.setColor(0x4CAF50);
            canvas.drawCircle(100, 100, 50, paint);
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
