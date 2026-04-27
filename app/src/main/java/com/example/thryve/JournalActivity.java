package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JournalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_journal);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ((Button) findViewById(R.id.btnSaveJournal)).setOnClickListener(v -> {
            Toast.makeText(this, "Journal saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
