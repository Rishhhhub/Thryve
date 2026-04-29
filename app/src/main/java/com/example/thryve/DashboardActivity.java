package com.example.thryve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thryve.ui.HeartRateBarView;
import com.example.thryve.ui.TripleRingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tvUsername);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FIREBASE_TEST", "Attempting write...");

        setupMockData();
        setupNavigation();
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Athlete");
        if (tvUsername != null) tvUsername.setText(name);

        String path = prefs.getString("profile_path", null);
        if (path != null && imgAvatar != null) {
            try {
                imgAvatar.setImageURI(Uri.parse(path));
                imgAvatar.setColorFilter(null);
            } catch (Exception e) {
                Log.e("DASHBOARD", "Load error", e);
            }
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING" : hour < 17 ? "GOOD AFTERNOON" : "GOOD EVENING";
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
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v -> startActivity(new Intent(this, OnboardingActivity.class)));
        }

        View.OnClickListener startRunClick = v -> startActivity(new Intent(this, RunActivity.class));
        if (findViewById(R.id.btnStartRun) != null) findViewById(R.id.btnStartRun).setOnClickListener(startRunClick);
        if (findViewById(R.id.fab) != null) findViewById(R.id.fab).setOnClickListener(startRunClick);

        findViewById(R.id.navJournal).setOnClickListener(v -> startActivity(new Intent(this, JournalActivity.class)));
        findViewById(R.id.navTrain).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.navDevice).setOnClickListener(v -> startActivity(new Intent(this, DeviceActivity.class)));

        if (findViewById(R.id.layoutRecovery) != null) {
            findViewById(R.id.layoutRecovery).setOnClickListener(v ->
                    startActivity(new Intent(this, RecoveryDetailActivity.class).putExtra("recovery_percent", 85)));
        }
        if (findViewById(R.id.layoutStrain) != null) {
            findViewById(R.id.layoutStrain).setOnClickListener(v ->
                    startActivity(new Intent(this, StrainDetailActivity.class).putExtra("strain_value", 145).putExtra("heart_rate", 72)));
        }
        if (findViewById(R.id.layoutSleep) != null) {
            findViewById(R.id.layoutSleep).setOnClickListener(v -> startActivity(new Intent(this, SleepActivity.class)));
        }
        if (findViewById(R.id.cardSteps) != null) {
            findViewById(R.id.cardSteps).setOnClickListener(v -> startActivity(new Intent(this, StepsActivity.class)));
        }
    }
}