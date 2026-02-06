package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {
    private Button logoutButton;
    private Button startGameButton;
    private TextView userInfoText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Views finden
        startGameButton = findViewById(R.id.startGameButton);
        logoutButton = findViewById(R.id.logoutButton);
        userInfoText = findViewById(R.id.userInfoText);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Prüfe ob User eingeloggt ist
        if (mAuth.getCurrentUser() == null) {
            navigateToMain();
            return;
        }

        // Button Listener
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("IMAGE_FILE", "auto.png");
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> logout());

    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Abmelden")
                .setMessage("Möchtest du dich wirklich abmelden?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    mAuth.signOut();
                    navigateToMain();
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}