package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    // UI-Elemente
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button backButton;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Authentication initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Views mit findViewById verknüpfen
        nameEditText = findViewById(R.id.editTextText);
        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        registerButton = findViewById(R.id.button3);
        backButton = findViewById(R.id.button5);

        // Registrieren Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Zurück Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Schließt diese Activity und geht zurück
            }
        });
    }

    private void registerUser() {
        // Hole die eingegebenen Werte
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // ========== VALIDIERUNG ==========

        // 1. Prüfe ob Name eingegeben wurde
        if (name.isEmpty()) {
            nameEditText.setError("Bitte Namen eingeben");
            nameEditText.requestFocus();
            return;
        }

        // 2. Prüfe ob Email eingegeben wurde
        if (email.isEmpty()) {
            emailEditText.setError("Bitte E-Mail eingeben");
            emailEditText.requestFocus();
            return;
        }

        // 3. Prüfe ob Email gültig ist
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Bitte gültige E-Mail eingeben");
            emailEditText.requestFocus();
            return;
        }

        // 4. Prüfe ob Passwort eingegeben wurde
        if (password.isEmpty()) {
            passwordEditText.setError("Bitte Passwort eingeben");
            passwordEditText.requestFocus();
            return;
        }

        // 5. Prüfe ob Passwort mindestens 6 Zeichen hat (Firebase-Regel)
        if (password.length() < 6) {
            passwordEditText.setError("Passwort muss mindestens 6 Zeichen haben");
            passwordEditText.requestFocus();
            return;
        }

        // ========== FIREBASE REGISTRIERUNG ==========

        // Buttons deaktivieren während Registrierung läuft
        registerButton.setEnabled(false);
        backButton.setEnabled(false);

        // Firebase: Erstelle neuen User mit Email und Passwort
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    // Buttons wieder aktivieren
                    registerButton.setEnabled(true);
                    backButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Registrierung erfolgreich
                        FirebaseUser user = mAuth.getCurrentUser();

                        Toast.makeText(RegisterActivity.this,
                                "Registrierung erfolgreich! Willkommen " + name + "!",
                                Toast.LENGTH_LONG).show();

                        // Gehe zur HomeActivity
                        navigateToHome();

                    } else {
                        // Registrierung fehlgeschlagen
                        String errorMessage = "Registrierung fehlgeschlagen";

                        // Versuche genauere Fehlermeldung zu bekommen
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }

                        Toast.makeText(RegisterActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Schließt RegisterActivity, damit User nicht zurück kann
    }
}