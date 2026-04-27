package com.example.thryve;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.thryve.ui.RecoveryRingView;

public class StepsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_steps);

        // Set ring progress: 8432/10000 = 0.84
        RecoveryRingView ring = findViewById(R.id.stepsRingView);
        ring.setProgress(0.84f, 0xFF4CAF50);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
