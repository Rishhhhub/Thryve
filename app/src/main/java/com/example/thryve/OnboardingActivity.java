package com.example.thryve;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etName;
    private SeekBar seekAge, seekHeight, seekWeight;
    private TextView tvAgeValue, tvHeightValue, tvWeightValue, tvBMI;
    private RadioGroup rgGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_onboarding);

        etName = findViewById(R.id.etName);
        seekAge = findViewById(R.id.seekAge);
        seekHeight = findViewById(R.id.seekHeight);
        seekWeight = findViewById(R.id.seekWeight);
        tvAgeValue = findViewById(R.id.tvAgeValue);
        tvHeightValue = findViewById(R.id.tvHeightValue);
        tvWeightValue = findViewById(R.id.tvWeightValue);
        tvBMI = findViewById(R.id.tvBMI);
        rgGender = findViewById(R.id.rgGender);

        updateBMI();

        seekAge.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                tvAgeValue.setText(String.valueOf(p));
            }
        });
        seekHeight.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                tvHeightValue.setText(p + " cm");
                updateBMI();
            }
        });
        seekWeight.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                tvWeightValue.setText(p + " kg");
                updateBMI();
            }
        });

        Button btnStart = findViewById(R.id.btnGetStarted);
        btnStart.setOnClickListener(v -> saveAndProceed());
    }

    private void updateBMI() {
        int weight = seekWeight.getProgress();
        int heightCm = seekHeight.getProgress();
        
        if (heightCm > 0) {
            double heightM = heightCm / 100.0;
            double bmi = weight / (heightM * heightM);
            tvBMI.setText(String.format(Locale.US, "%.1f", bmi));
        } else {
            tvBMI.setText("--");
        }
    }

    private void saveAndProceed() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        String gender = "Male";
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rbFemale) gender = "Female";
        else if (checkedId == R.id.rbOther) gender = "Other";

        prefs.edit()
                .putString("user_name", etName.getText().toString().trim().isEmpty()
                        ? "Athlete" : etName.getText().toString().trim())
                .putInt("user_age", seekAge.getProgress())
                .putInt("user_height", seekHeight.getProgress())
                .putInt("user_weight", seekWeight.getProgress())
                .putString("user_gender", gender)
                .putBoolean("onboarding_done", true)
                .apply();

        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    // Simple abstract adapter to avoid boilerplate
    abstract static class SimpleSeekListener implements SeekBar.OnSeekBarChangeListener {
        public void onStartTrackingTouch(SeekBar s) {}
        public void onStopTrackingTouch(SeekBar s) {}
    }
}
