package com.example.thryve;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thryve.ui.RecoveryRingView;

public class StepsActivity extends AppCompatActivity {

    private TextView tvStepCount, tvStepDistance, tvStepCalories;
    private RecoveryRingView ring;
    private StepSensorHelper stepSensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_steps);

        tvStepCount = findViewById(R.id.tvStepCount);
        tvStepDistance = findViewById(R.id.tvStepDistance);
        tvStepCalories = findViewById(R.id.tvStepCalories);
        ring = findViewById(R.id.stepsRingView);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        stepSensorHelper = StepSensorHelper.getInstance(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensorHelper.isSensorPresent()) {
            stepSensorHelper.startListening(steps -> {
                updateUI(steps);
            });
        } else {
            if (tvStepCount != null) tvStepCount.setText("No Sensor");
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensorHelper.isSensorPresent()) {
            stepSensorHelper.stopListening();
        }
    }
    
    private void updateUI(int steps) {
        if (tvStepCount != null) {
            tvStepCount.setText(String.format(java.util.Locale.getDefault(), "%,d", steps));
        }
        
        // 1 step = ~0.000762 km
        double distanceKm = steps * 0.000762;
        if (tvStepDistance != null) {
            tvStepDistance.setText(String.format(java.util.Locale.getDefault(), "%.1f km", distanceKm));
        }
        
        // 1 step = ~0.04 calories
        int calories = (int) (steps * 0.04);
        if (tvStepCalories != null) {
            tvStepCalories.setText(String.format(java.util.Locale.getDefault(), "%d kcal", calories));
        }
        
        if (ring != null) {
            float progress = Math.min(1.0f, steps / 10000.0f);
            ring.setProgress(progress, 0xFF4CAF50);
        }
    }
}
