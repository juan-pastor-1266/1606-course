package com.example.japf.myhelloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class Graphics1Activity extends AppCompatActivity {

    BallSurfaceView ballsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.e("Graphics1Activity", "  onCreate: exiting ... ------ ");
    }

    @Override
    public void onPause(){
        ballsView.surfaceDestroyed(ballsView.getHolder());
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ballsView = new BallSurfaceView(this);
        setContentView(ballsView);
    }

}
