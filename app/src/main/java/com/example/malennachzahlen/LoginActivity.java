package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText pwEditText;
    private Button loginButton;
    private Button backButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // lädt XML

        // Firebase initalisieren
        mAuth = FirebaseAuth.getInstance();

        // Views verknüpfen
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        pwEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button);
        backButton = findViewById(R.id.button4);

        // Button-Listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String pw = pwEditText.getText().toString().trim();

        // VALIDIERUNG
        if (email.isEmpty()) {
            emailEditText.setError("Bitte E-Mail eingeben");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Bitte gültige E-Mail eingeben");
            emailEditText.requestFocus();
        }

        if (pw.isEmpty()) {
            pwEditText.setError("Bitte Passwort eingeben");
            pwEditText.requestFocus();
            return;
        }

        // FIREBASE LOGIN

        loginButton.setEnabled(false);
        backButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    backButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login erfolgreich
                        // Toast Benachrichtigung an User
                        Toast.makeText(LoginActivity.this, "Willkommen zurück!", Toast.LENGTH_SHORT).show();

                        navigateToHome();

                    } else {
                        // Login fehlgeschlagen
                        // Standardfehlermeldung gesetzt, die angezeigt wird, falls wir keine genauere Info vom System
                        String errorMessage = "Login fehlgeschlagen";
                        //genaue Ursache des Fehlers erfasst
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // LoginActivity schließen, damit User nicht zurück kann
    }
}