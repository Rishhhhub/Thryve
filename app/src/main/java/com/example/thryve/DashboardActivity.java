package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout layoutRecovery;
    private LinearLayout layoutStrain;
    private LinearLayout navTrain;
    private FloatingActionButton fab;
    private TripleRingView tripleRingView;
    private HeartRateBarView heartRateBarView;

    // Sample data (replace with real data source later)
    private int readinessScore = 84;
    private int recoveryPercent = 85;
    private int strainValue = 145;
    private String sleepTime = "7h 48m";
    private int heartRate = 72;
    private int batteryPercent = 88;

    // Ring colors
    private static final int GREEN_COLOR  = 0xFF4CAF50;
    private static final int ORANGE_COLOR = 0xFFFF9800;
    private static final int BLUE_COLOR   = 0xFF2196F3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide action bar for full immersive feel
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_dashboard);

        initViews();
        populateData();
        setupClickListeners();
    }

    private void initViews() {
        layoutRecovery   = findViewById(R.id.layoutRecovery);
        layoutStrain     = findViewById(R.id.layoutStrain);
        navTrain         = findViewById(R.id.navTrain);
        fab              = findViewById(R.id.fab);
        tripleRingView   = findViewById(R.id.tripleRingView);
        heartRateBarView = findViewById(R.id.heartRateBarView);
    }

    private void populateData() {
        // Readiness score
        TextView tvReadiness = findViewById(R.id.tvReadinessScore);
        tvReadiness.setText(String.valueOf(readinessScore));

        // Stats row
        TextView tvRecovery = findViewById(R.id.tvRecovery);
        tvRecovery.setText(recoveryPercent + "%");

        TextView tvStrain = findViewById(R.id.tvStrain);
        tvStrain.setText(String.valueOf(strainValue));

        TextView tvSleep = findViewById(R.id.tvSleep);
        tvSleep.setText(sleepTime);

        // Heart rate
        TextView tvHeartRate = findViewById(R.id.tvHeartRate);
        tvHeartRate.setText(String.valueOf(heartRate));

        // Battery
        TextView tvBattery = findViewById(R.id.tvBatteryPercent);
        tvBattery.setText(batteryPercent + "%");

        // Configure triple ring:
        // outer = recovery (green), middle = strain (orange), inner = sleep (blue)
        if (tripleRingView != null) {
            tripleRingView.setRingValues(
                    (float) recoveryPercent / 100f,   // green ring 0..1
                    (float) strainValue / 200f,        // orange ring 0..1 (max assumed 200)
                    7.8f / 10f                         // blue ring 0..1 (7h48m out of 10h)
            );
            tripleRingView.setRingColors(GREEN_COLOR, ORANGE_COLOR, BLUE_COLOR);
        }

        // Heart rate bars – pass some dummy data
        if (heartRateBarView != null) {
            int[] bpmData = {60, 75, 68, 80, 72, 65, 78, 72, 70, 74};
            heartRateBarView.setData(bpmData, heartRate);
        }
    }

    private void setupClickListeners() {
        // Recovery section → RecoveryDetailActivity
        layoutRecovery.setOnClickListener(v -> openRecoveryDetail());

        // Strain section → StrainDetailActivity
        layoutStrain.setOnClickListener(v -> openStrainDetail());

        // Train nav → StrainDetailActivity (shortcut)
        navTrain.setOnClickListener(v -> openStrainDetail());

        // FAB – could open a new workout log screen (stub)
        fab.setOnClickListener(v -> {
            // TODO: open workout logging screen
        });
    }

    private void openStrainDetail() {
        Intent intent = new Intent(DashboardActivity.this, StrainDetailActivity.class);
        intent.putExtra("strain_value", strainValue);
        intent.putExtra("heart_rate", heartRate);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void openRecoveryDetail() {
        Intent intent = new Intent(DashboardActivity.this, RecoveryDetailActivity.class);
        intent.putExtra("recovery_percent", recoveryPercent);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
