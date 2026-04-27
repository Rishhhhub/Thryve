package com.example.thryve;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.Slider;

public class MetricsActivity extends AppCompatActivity {

    private double currentWeight = 72.5; // Default Weight

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);

        // 1. Gender Spinner Fix (Text visible karne ke liye)
        Spinner spinner = findViewById(R.id.genderSpinner);
        String[] genders = {"Select Gender", "Male", "Female", "Other"};

        // Custom layout use kar rahe hain taaki dark background pe text dikhe
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 2. Height Slider Logic
        TextView heightDisplay = findViewById(R.id.heightDisplay);
        Slider heightSlider = findViewById(R.id.heightSlider);
        heightSlider.addOnChangeListener((slider, value, fromUser) -> {
            heightDisplay.setText((int)value + " cm");
        });

        // 3. Weight Plus/Minus Logic (As per Figma)
        TextView weightValue = findViewById(R.id.weightValue);
        TextView btnMinus = findViewById(R.id.btnMinus);
        TextView btnPlus = findViewById(R.id.btnPlus);

        btnPlus.setOnClickListener(v -> {
            currentWeight += 0.5;
            weightValue.setText(String.format("%.1f kg", currentWeight));
        });

        btnMinus.setOnClickListener(v -> {
            if (currentWeight > 10) {
                currentWeight -= 0.5;
                weightValue.setText(String.format("%.1f kg", currentWeight));
            }
        });

        // 4. Continue Button
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            // Next screen navigation yahan aayega
        });
    }
}