package com.example.malennachzahlen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {


    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    Button backToStartButton;
    int counter = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login); //lädt XML

        emailInput = findViewById(R.id.editTextTextEmailAddress);
        passwordInput = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button);
        backToStartButton = findViewById(R.id.button4);

        backToStartButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StartActivity.class);
            startActivity(intent);
            finish(); // Login schließen
        });

        loginButton.setOnClickListener(new View.OnClickListener() { //anonyme innere Klasse
            @Override //interface
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (email.equals("admin") && password.equals("admin")) {
                    Toast.makeText(LoginActivity.this,
                            "Login erfolgreich", Toast.LENGTH_SHORT).show();
                } else {
                    counter--;

                    Toast.makeText(LoginActivity.this,
                            "Falsche Daten. Versuche übrig: " + counter,
                            Toast.LENGTH_SHORT).show();
                    if (counter == 0) {
                        loginButton.setEnabled(false); //Button deaktivieren
                        Toast.makeText(LoginActivity.this,
                                "Login gesperrt", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}