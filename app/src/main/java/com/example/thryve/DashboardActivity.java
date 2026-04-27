package com.example.thryve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thryve.ui.HeartRateBarView;
import com.example.thryve.ui.TripleRingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Athlete");
        ((TextView) findViewById(R.id.tvUsername)).setText(name);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING" : hour < 17 ? "GOOD AFTERNOON" : "GOOD EVENING";
        ((TextView) findViewById(R.id.tvGreeting)).setText(greeting);
    }

    private void setupMockData() {
        TripleRingView ring = findViewById(R.id.tripleRingView);
        ring.setRingValues(0.85f, 0.72f, 0.78f);
        ring.setRingColors(0xFF4CAF50, 0xFFFF9800, 0xFF2196F3);

        HeartRateBarView hrBar = findViewById(R.id.heartRateBarView);
        hrBar.setData(new int[]{60, 75, 68, 80, 72, 65, 78, 72, 70, 74}, 72);
    }

    private void setupNavigation() {
        // Stats row
        findViewById(R.id.layoutRecovery).setOnClickListener(v ->
                startActivity(new Intent(this, RecoveryDetailActivity.class)
                        .putExtra("recovery_percent", 85)));

        findViewById(R.id.layoutStrain).setOnClickListener(v ->
                startActivity(new Intent(this, StrainDetailActivity.class)
                        .putExtra("strain_value", 145).putExtra("heart_rate", 72)));

        findViewById(R.id.layoutSleep).setOnClickListener(v ->
                startActivity(new Intent(this, SleepActivity.class)));

        // Quick action cards
        findViewById(R.id.cardSteps).setOnClickListener(v ->
                startActivity(new Intent(this, StepsActivity.class)));

        findViewById(R.id.cardSleepSummary).setOnClickListener(v ->
                startActivity(new Intent(this, SleepActivity.class)));

        // START RUN button
        ((Button) findViewById(R.id.btnStartRun)).setOnClickListener(v ->
                startActivity(new Intent(this, RunActivity.class)));

        // Bottom Nav
        ((LinearLayout) findViewById(R.id.navJournal)).setOnClickListener(v ->
                startActivity(new Intent(this, JournalActivity.class)));
        ((LinearLayout) findViewById(R.id.navTrain)).setOnClickListener(v ->
                startActivity(new Intent(this, StrainDetailActivity.class)));
        ((LinearLayout) findViewById(R.id.navDevice)).setOnClickListener(v ->
                startActivity(new Intent(this, DeviceActivity.class)));

        // FAB → Run
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(v ->
                startActivity(new Intent(this, RunActivity.class)));
    }
}
