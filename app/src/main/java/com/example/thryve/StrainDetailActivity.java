package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StrainDetailActivity extends AppCompatActivity {

    private int strainValue;
    private int heartRateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_strain_detail);

        // Receive data from Dashboard
        strainValue    = getIntent().getIntExtra("strain_value", 145);
        heartRateValue = getIntent().getIntExtra("heart_rate", 72);

        initViews();
        populateData();
        setupClickListeners();
    }

    private void initViews() {
        // Views are referenced in populateData
    }

    private void populateData() {
        // Peak HR
        TextView tvPeakHR = findViewById(R.id.tvPeakHR);
        tvPeakHR.setText(String.valueOf(strainValue));

        // Avg HR
        TextView tvAvgHR = findViewById(R.id.tvAvgHR);
        tvAvgHR.setText(String.valueOf(heartRateValue));

        // Change label
        TextView tvChange = findViewById(R.id.tvPeakHRChange);
        tvChange.setText("↑ +12% vs yesterday");

        // Calories (static demo data)
        TextView tvCalories = findViewById(R.id.tvCalories);
        tvCalories.setText("1,200");

        // Recommendation text
        TextView tvRec = findViewById(R.id.tvRecommendation);
        tvRec.setText("Your heart rate peaks are trending higher than usual. "
                + "Focus on active recovery tonight to balance your load.");

        // HR Timeline chart (sample data)
        HrTimelineView hrTimeline = findViewById(R.id.hrTimelineView);
        if (hrTimeline != null) {
            int[] hourlyHR = {65, 70, 68, 90, 110, 130, 145, 120, 100, 85, 75, 72};
            hrTimeline.setData(hourlyHR, 145);
        }
    }

    private void setupClickListeners() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Home nav → back to Dashboard
        LinearLayout navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(StrainDetailActivity.this, DashboardActivity.class);
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
