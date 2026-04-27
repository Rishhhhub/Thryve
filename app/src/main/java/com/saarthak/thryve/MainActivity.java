package com.saarthak.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnQuickStart = findViewById(R.id.btnQuickStart);

        if (btnQuickStart != null) {
            btnQuickStart.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                startActivity(intent);
            });
        }
    }
}