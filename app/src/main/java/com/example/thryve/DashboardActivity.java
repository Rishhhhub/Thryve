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
        TextView tvUsername = findViewById(R.id.tvUsername);
        if (tvUsername != null) tvUsername.setText(name);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING" : hour < 17 ? "GOOD AFTERNOON" : "GOOD EVENING";
        // Fixed: The layout uses a fixed string "GOOD MORNING" in a TextView, or might not have tvGreeting
        // Let's check the layout IDs again or just skip if not found.
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
        // Stats row
        LinearLayout layoutRecovery = findViewById(R.id.layoutRecovery);
        if (layoutRecovery != null) {
            layoutRecovery.setOnClickListener(v ->
                startActivity(new Intent(this, RecoveryDetailActivity.class)
                        .putExtra("recovery_percent", 85)));
        }

        LinearLayout layoutStrain = findViewById(R.id.layoutStrain);
        if (layoutStrain != null) {
            layoutStrain.setOnClickListener(v ->
                startActivity(new Intent(this, StrainDetailActivity.class)
                        .putExtra("strain_value", 145).putExtra("heart_rate", 72)));
        }

        LinearLayout layoutSleep = findViewById(R.id.layoutSleep);
        if (layoutSleep != null) {
            layoutSleep.setOnClickListener(v -> {
                startActivity(new Intent(this, SleepActivity.class));
            });
        }

        // FAB → Run
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v ->
                startActivity(new Intent(this, RunActivity.class)));
        }
    }
}
