package com.example.thryve;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SleepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_sleep);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
