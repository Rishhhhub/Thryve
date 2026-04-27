package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thryve.ui.HrvChartView;
import com.example.thryve.ui.RecoveryRingView;

public class RecoveryDetailActivity extends AppCompatActivity {

    private int recoveryPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_recovery_detail);

        // Receive data from Dashboard
        recoveryPercent = getIntent().getIntExtra("recovery_percent", 85);

        populateData();
        setupClickListeners();
    }

    private void populateData() {
        // Recovery ring
        RecoveryRingView ringView = findViewById(R.id.recoveryRingView);
        if (ringView != null) {
            ringView.setProgress((float) recoveryPercent / 100f, 0xFF4CAF50);
        }

        // Recovery percent text
        TextView tvPercent = findViewById(R.id.tvRecoveryPercent);
        tvPercent.setText(recoveryPercent + "%");

        // Status message
        TextView tvStatus = findViewById(R.id.tvStatusMessage);
        tvStatus.setText("You are " + recoveryPercent + "% Recovered. "
                + determineRecoveryMessage(recoveryPercent));

        // HRV
        TextView tvHrv = findViewById(R.id.tvHrv);
        tvHrv.setText("64");

        TextView tvHrvBadge = findViewById(R.id.tvHrvBadge);
        tvHrvBadge.setText("+12% vs avg");

        // HRV chart data
        HrvChartView hrvChart = findViewById(R.id.hrvChartView);
        if (hrvChart != null) {
            float[] hrvData = {55f, 50f, 45f, 52f, 58f, 62f, 64f, 60f, 63f, 61f};
            hrvChart.setData(hrvData);
        }
    }

    private String determineRecoveryMessage(int percent) {
        if (percent >= 80) {
            return "You can handle high strain today.";
        } else if (percent >= 60) {
            return "Moderate training recommended.";
        } else {
            return "Consider rest or light activity.";
        }
    }

    private void setupClickListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Home nav → back to Dashboard
        LinearLayout navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RecoveryDetailActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
