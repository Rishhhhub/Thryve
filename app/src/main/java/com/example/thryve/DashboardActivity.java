package com.example.thryve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thryve.ui.HeartRateBarView;
import com.example.thryve.ui.TripleRingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        loadUserData();
        setupMockData();
        setupNavigation();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FIREBASE_TEST", "Attempting write...");
    }

    // 🔥 IMPORTANT → updates UI after coming back from Profile
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Athlete");

        TextView tvUsername = findViewById(R.id.tvUsername);
        if (tvUsername != null) tvUsername.setText(name);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING"
                : hour < 17 ? "GOOD AFTERNOON"
                : "GOOD EVENING";

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) tvGreeting.setText(greeting);
    }

    private void setupMockData() {

        TripleRingView ring = findViewById(R.id.tripleRingView);
        if (ring != null) {
            ring.setRingValues(0.85f, 0.72f, 0.78f);
            ring.setRingColors(0xFF4CAF50, 0xFFFF9800, 0xFF2196F3);
        }

        HeartRateBarView hrBar = findViewById(R.id.heartRateBarView);
        if (hrBar != null) {
            hrBar.setData(new int[]{60, 75, 68, 80, 72, 65, 78, 72, 70, 74}, 72);
        }
    }

    private void setupNavigation() {

        // 🔥 NEW → Avatar opens Profile
        View imgAvatar = findViewById(R.id.imgAvatar);
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class)));
        }

        // 🔥 NEW → Heart Rate card opens Health screen
        View cardHeartRate = findViewById(R.id.cardHeartRate);
        if (cardHeartRate != null) {
            cardHeartRate.setOnClickListener(v ->
                    startActivity(new Intent(this, HealthActivity.class)));
        }

        // EXISTING LOGIC (UNCHANGED)

        View layoutRecovery = findViewById(R.id.layoutRecovery);
        if (layoutRecovery != null) {
            layoutRecovery.setOnClickListener(v ->
                    startActivity(new Intent(this, RecoveryDetailActivity.class)
                            .putExtra("recovery_percent", 85)));
        }

        View layoutStrain = findViewById(R.id.layoutStrain);
        if (layoutStrain != null) {
            layoutStrain.setOnClickListener(v ->
                    startActivity(new Intent(this, StrainDetailActivity.class)
                            .putExtra("strain_value", 145)
                            .putExtra("heart_rate", 72)));
        }

        View layoutSleep = findViewById(R.id.layoutSleep);
        if (layoutSleep != null) {
            layoutSleep.setOnClickListener(v ->
                    startActivity(new Intent(this, SleepActivity.class)));
        }

        View cardSteps = findViewById(R.id.cardSteps);
        if (cardSteps != null) {
            cardSteps.setOnClickListener(v ->
                    startActivity(new Intent(this, StepsActivity.class)));
        }

        View notification = findViewById(R.id.imgNotification);
        if (notification != null) {
            notification.setOnClickListener(v -> {
                // future feature
            });
        }

        // 🔥 RUN BUTTON (COMMON HANDLER)
        View.OnClickListener startRunClick = v ->
                startActivity(new Intent(this, RunActivity.class));

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(startRunClick);
        }

        View btnStartRun = findViewById(R.id.btnStartRun);
        if (btnStartRun != null) {
            btnStartRun.setOnClickListener(startRunClick);
        }

        View navJournal = findViewById(R.id.navJournal);
        if (navJournal != null) {
            navJournal.setOnClickListener(v ->
                    startActivity(new Intent(this, JournalActivity.class)));
        }

        View navTrain = findViewById(R.id.navTrain);
        if (navTrain != null) {
            navTrain.setOnClickListener(v ->
                    startActivity(new Intent(this, HistoryActivity.class)));
        }

        View navDevice = findViewById(R.id.navDevice);
        if (navDevice != null) {
            navDevice.setOnClickListener(v ->
                    startActivity(new Intent(this, DeviceActivity.class)));
        }
    }
}