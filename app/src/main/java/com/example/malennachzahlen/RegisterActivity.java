package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button backToStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backToStartButton = findViewById(R.id.button5);

        backToStartButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
            startActivity(intent);
            finish(); // Register schließen
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
