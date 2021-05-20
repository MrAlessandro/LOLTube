package com.mralessandro.loltube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ImageView image = findViewById(R.id.splash_stick);
        Animation animation = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.rotate);
        image.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), TubeActivity.class);
            startActivity(intent);
        },3000);
    }
}
