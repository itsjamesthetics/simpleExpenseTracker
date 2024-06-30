package com.jamesaldteves.test2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Handler to start the MainActivity and close this Splashscreen after 3 seconds.
        new Handler().postDelayed(() -> {
            // Start main activity
            Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
            startActivity(intent);
            // Close this activity
            finish();
        }, 3000); // wait for 3 seconds
    }
}