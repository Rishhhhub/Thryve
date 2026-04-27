package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page1);

        // Button ko XML ID se connect kar rahe hain
        Button btnLogin = findViewById(R.id.btnLoginSubmit);

        // Click hone par kya hoga:
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Login click karte hi MetricsActivity (3rd page) khulega
                Intent intent = new Intent(LoginPage1.this, MetricsActivity.class);
                startActivity(intent);

                // Optional: finish() use karo agar aap nahi chahte ki user wapas Login pe aaye
                // finish();
            }
        });
    }
}