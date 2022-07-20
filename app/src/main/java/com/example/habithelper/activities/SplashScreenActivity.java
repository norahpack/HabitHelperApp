package com.example.habithelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.habithelper.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int MILLIS_TO_DELAY = 3600;

    Handler handler;
    private ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ivSplash = findViewById(R.id.ivSplash);

        // Adding the gif here using glide library
        Glide.with(this).load(R.drawable.main_screen_new).into(ivSplash);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        } , MILLIS_TO_DELAY);
    }
}
