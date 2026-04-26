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

        Button btnLogin = findViewById(R.id.btnLoginSubmit);

        // Short and clean way to write click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jab click ho toh ye chalega
                // Abhi ke liye hum next activity banayenge tab link karenge
            }
        });
    }
}