package com.example.thryve;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen and hide action bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_health);

        // Back button logic
        View btnBack = findViewById(R.id.btnBackHealth);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Mock Data display (Optional - matches your screenshot)
        updateVitalsUI();
    }

    private void updateVitalsUI() {
        // IDs check kar lena apne activity_health.xml mein
        // TextView tvSpO2 = findViewById(R.id.tvSpO2Value);
        // if(tvSpO2 != null) tvSpO2.setText("98%");
    }
}