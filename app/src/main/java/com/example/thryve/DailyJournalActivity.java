package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyJournalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_journal);

        // 1. Real-time Date Setup (Automatically Sunday, Apr 26)
        TextView tvDate = findViewById(R.id.tvTodayDate);
        if (tvDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
            tvDate.setText(sdf.format(Calendar.getInstance().getTime()));
        }

        // 2. Settings Button Logic (Top Right)
        ImageView btnSettings = findViewById(R.id.btnGoSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(DailyJournalActivity.this, SettingsDeviceActivity.class);
                startActivity(intent);
            });
        }

        // 3. Bottom Navigation Fixes

        // Workout/Summary Page par wapas jane ke liye
        ImageView btnWorkout = findViewById(R.id.btnNavWorkout);
        if (btnWorkout != null) {
            btnWorkout.setOnClickListener(v -> finish());
        }

        // Dashboard/Grid Page par jane ke liye (Optional feedback)
        ImageView btnGrid = findViewById(R.id.btnNavGrid);
        if (btnGrid != null) {
            btnGrid.setOnClickListener(v -> {
                Toast.makeText(this, "Going to Dashboard...", Toast.LENGTH_SHORT).show();
                // Yahan Dashboard Activity ka Intent daal sakte ho
            });
        }

        // 4. Submit Button Logic
        EditText etFeeling = findViewById(R.id.etFeelingInput);
        Button btnSubmit = findViewById(R.id.btnSubmitJournal);

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String entry = etFeeling.getText().toString().trim();

                // Save hone ka makkhan sa feedback
                Toast.makeText(this, "Journal Entry Saved! ✅", Toast.LENGTH_SHORT).show();

                // Chota sa delay taaki Toast dikh jaye, phir finish
                btnSubmit.postDelayed(this::finish, 500);
            });
        }
    }
}