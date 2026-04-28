package com.example.thryve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // ✅ Ensure user exists BEFORE proceeding
        if (auth.getCurrentUser() != null) {
            Log.d("AUTH", "User exists: " + auth.getCurrentUser().getUid());
            proceedToNextScreen();
        } else {
            auth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        Log.d("AUTH", "User created: " + authResult.getUser().getUid());
                        proceedToNextScreen();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AUTH", "Auth failed", e);
                        proceedToNextScreen(); // fallback so app doesn't block
                    });
        }
    }

    private void proceedToNextScreen() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean("onboarding_done", false);

            Intent intent;
            if (onboardingDone) {
                intent = new Intent(SplashActivity.this, DashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }

            startActivity(intent);
            finish();

        }, SPLASH_DELAY_MS);
    }
}