package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Logo Container ko dhire se fade-in karne ke liye
        View logoContainer = findViewById(R.id.logoContainer);
        if (logoContainer != null) {
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1500);
            logoContainer.startAnimation(fadeIn);
        }

        // 5 seconds baad automatic LoginPage1 par bhejne ke liye
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginPage1.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}