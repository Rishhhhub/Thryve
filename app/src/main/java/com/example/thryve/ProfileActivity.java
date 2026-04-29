package com.example.thryve;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etAge, etWeight, etHeight;
    private TextView tvBMI;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        tvBMI = findViewById(R.id.tvBMI);

        // Load existing data
        etName.setText(prefs.getString("user_name", "Athlete"));
        etAge.setText(String.valueOf(prefs.getInt("user_age", 25)));
        etWeight.setText(String.valueOf(prefs.getInt("user_weight", 70)));
        etHeight.setText(String.valueOf(prefs.getInt("user_height", 175)));

        updateBMI();

        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateBMI();
            }
        };

        etWeight.addTextChangedListener(bmiWatcher);
        etHeight.addTextChangedListener(bmiWatcher);

        findViewById(R.id.btnBackProfile).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfile());
    }

    private void updateBMI() {
        try {
            int weight = Integer.parseInt(etWeight.getText().toString());
            int heightCm = Integer.parseInt(etHeight.getText().toString());
            
            if (heightCm > 0) {
                double heightM = heightCm / 100.0;
                double bmi = weight / (heightM * heightM);
                tvBMI.setText(String.format(Locale.US, "%.1f", bmi));
            } else {
                tvBMI.setText("--");
            }
        } catch (NumberFormatException e) {
            tvBMI.setText("--");
        }
    }

    private void saveProfile() {
        try {
            String newName = etName.getText().toString();
            int newAge = Integer.parseInt(etAge.getText().toString());
            int newWeight = Integer.parseInt(etWeight.getText().toString());
            int newHeight = Integer.parseInt(etHeight.getText().toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", newName);
            editor.putInt("user_age", newAge);
            editor.putInt("user_weight", newWeight);
            editor.putInt("user_height", newHeight);
            editor.apply();

            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish(); // Back to dashboard
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}