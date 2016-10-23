package com.shikhar.face.filter.facetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by aditya.pratapsingh on 23/10/16.
 */

public class HomeActivity extends Activity implements View.OnClickListener {

    Button btnStartFaceTracker, btnStartEyesTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        btnStartFaceTracker = (Button) findViewById(R.id.btn_start_face_tracker);
        btnStartFaceTracker.setOnClickListener(this);

        btnStartEyesTracker = (Button) findViewById(R.id.btn_start_eyes_tracker);
        btnStartEyesTracker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_face_tracker:
                Intent faceTrackerIntent = new Intent(HomeActivity.this, FaceTrackerActivity.class);
                startActivity(faceTrackerIntent);
                break;
            case R.id.btn_start_eyes_tracker:
                Intent eyesTrackerIntent = new Intent(HomeActivity.this, EyesTrackerActivity.class);
                startActivity(eyesTrackerIntent);
                break;
        }
    }
}
