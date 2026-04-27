package com.saarthak.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Selection page mein Running card ki ID check karo
        View cardRunning = findViewById(R.id.cardRunning);
        if (cardRunning != null) {
            cardRunning.setOnClickListener(v -> {
                Intent intent = new Intent(SelectionActivity.this, LiveTrackingActivity.class);
                startActivity(intent);
            });
        }
    }
}