package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_device);

        // 1. Back Button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 2. Firmware Update
        LinearLayout btnFirmware = findViewById(R.id.btnFirmwareUpdate);
        if (btnFirmware != null) {
            btnFirmware.setOnClickListener(v -> Toast.makeText(this, "Checking for Updates...", Toast.LENGTH_SHORT).show());
        }

        // 3. Notifications
        SwitchCompat swNotif = findViewById(R.id.swNotifications);
        if (swNotif != null) {
            swNotif.setOnCheckedChangeListener((b, isChecked) -> Toast.makeText(this, isChecked ? "Notifications On" : "Notifications Off", Toast.LENGTH_SHORT).show());
        }

        // 4. Personalization
        LinearLayout btnWatchFaces = findViewById(R.id.btnWatchFaces);
        if (btnWatchFaces != null) {
            btnWatchFaces.setOnClickListener(v -> Toast.makeText(this, "Opening Watch Faces...", Toast.LENGTH_SHORT).show());
        }

        LinearLayout btnDisplay = findViewById(R.id.btnDisplaySettings);
        if (btnDisplay != null) {
            btnDisplay.setOnClickListener(v -> Toast.makeText(this, "Opening Display Settings...", Toast.LENGTH_SHORT).show());
        }

        // 5. Bottom Navigation
        setupNav();
    }

    private void setupNav() {
        findViewById(R.id.btnNavWorkout).setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutSummaryActivity.class));
            finish();
        });
        findViewById(R.id.btnNavJournal).setOnClickListener(v -> {
            startActivity(new Intent(this, DailyJournalActivity.class));
            finish();
        });
    }
}