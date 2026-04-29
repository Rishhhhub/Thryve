package com.example.thryve;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.thryve.ui.HeartRateBarView;
import com.example.thryve.ui.TripleRingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvUsername;
    private TextView tvSteps;
    
    private StepSensorHelper stepSensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvSteps = findViewById(R.id.tvSteps);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FIREBASE_TEST", "Attempting write...");

        setupMockData();
        setupNavigation();
        loadUserData();
        
        stepSensorHelper = StepSensorHelper.getInstance(this);
        if (!stepSensorHelper.isSensorPresent() && tvSteps != null) {
            tvSteps.setText("No Sensor");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        }
    }
    
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted && tvSteps != null) {
                    tvSteps.setText("Perm. Denied");
                }
            });

    // important: updates UI after coming back from Profile
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        
        if (stepSensorHelper.isSensorPresent() && ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            stepSensorHelper.startListening(steps -> {
                if (tvSteps != null) {
                    tvSteps.setText(String.format(java.util.Locale.getDefault(), "%,d", steps));
                }
            });
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensorHelper.isSensorPresent()) {
            stepSensorHelper.stopListening();
        }
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

        TextView tvUsername = findViewById(R.id.tvUsername);
        if (tvUsername != null)
            tvUsername.setText(name);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING"
                : hour < 17 ? "GOOD AFTERNOON"
                        : "GOOD EVENING";

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "GOOD MORNING" : hour < 17 ? "GOOD AFTERNOON" : "GOOD EVENING";
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null)
            tvGreeting.setText(greeting);
    }

    private void setupMockData() {
        TripleRingView ring = findViewById(R.id.tripleRingView);
        if (ring != null) {
            ring.setRingValues(0.85f, 0.72f, 0.78f);
            ring.setRingColors(0xFF4CAF50, 0xFFFF9800, 0xFF2196F3);
        }
        HeartRateBarView hrBar = findViewById(R.id.heartRateBarView);
        if (hrBar != null) {
            hrBar.setData(new int[] { 60, 75, 68, 80, 72, 65, 78, 72, 70, 74 }, 72);
        }
    }

    private void setupNavigation() {

        // avatar opens Profile
        View imgAvatar = findViewById(R.id.imgAvatar);
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        // heart rate card opens Health screen
        View cardHeartRate = findViewById(R.id.cardHeartRate);
        if (cardHeartRate != null) {
            cardHeartRate.setOnClickListener(v -> startActivity(new Intent(this, HealthActivity.class)));
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
        View layoutRecovery = findViewById(R.id.layoutRecovery);
        if (layoutRecovery != null) {
            layoutRecovery.setOnClickListener(v -> startActivity(new Intent(this, RecoveryDetailActivity.class)
                    .putExtra("recovery_percent", 85)));
        }

        View layoutStrain = findViewById(R.id.layoutStrain);
        if (layoutStrain != null) {
            layoutStrain.setOnClickListener(v -> startActivity(new Intent(this, StrainDetailActivity.class)
                    .putExtra("strain_value", 145)
                    .putExtra("heart_rate", 72)));
        }

        View layoutSleep = findViewById(R.id.layoutSleep);
        if (layoutSleep != null) {
            layoutSleep.setOnClickListener(v -> startActivity(new Intent(this, SleepActivity.class)));
        }

        View cardSteps = findViewById(R.id.cardSteps);
        if (cardSteps != null) {
            cardSteps.setOnClickListener(v -> startActivity(new Intent(this, StepsActivity.class)));
        }

        View notification = findViewById(R.id.imgNotification);
        if (notification != null) {
            notification.setOnClickListener(v -> {
                // future feature
            });
        }

        // run button click handler
        View.OnClickListener startRunClick = v -> startActivity(new Intent(this, RunActivity.class));

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
            navJournal.setOnClickListener(v -> startActivity(new Intent(this, JournalActivity.class)));
        }

        View navHistory = findViewById(R.id.navHistory);
        if (navHistory != null) {
            navHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        }

        View navDevice = findViewById(R.id.navDevice);
        if (navDevice != null) {
            navDevice.setOnClickListener(v -> startActivity(new Intent(this, DeviceActivity.class)));
        }
    }
}