package com.example.thryve;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etAge, etWeight;
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

        // Load existing data
        etName.setText(prefs.getString("user_name", "Athlete"));
        etAge.setText(String.valueOf(prefs.getInt("user_age", 25)));
        etWeight.setText(String.valueOf(prefs.getInt("user_weight", 70)));

        findViewById(R.id.btnBackProfile).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String newName = etName.getText().toString();
        int newAge = Integer.parseInt(etAge.getText().toString());
        int newWeight = Integer.parseInt(etWeight.getText().toString());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", newName);
        editor.putInt("user_age", newAge);
        editor.putInt("user_weight", newWeight);
        editor.apply();

        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        finish(); // Back to dashboard
    }
}