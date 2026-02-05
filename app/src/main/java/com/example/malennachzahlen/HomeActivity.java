package com.example.malennachzahlen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {
    private Button logoutButton;
    private LinearLayout imageGallery;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Views finden
        logoutButton = findViewById(R.id.logoutButton);
        imageGallery = findViewById(R.id.imageGallery);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Prüfe ob User eingeloggt ist
        if (mAuth.getCurrentUser() == null) {
            navigateToMain();
            return;
        }

        // Logout-Button Listener
        logoutButton.setOnClickListener(v -> logout());

        // Bilder laden und anzeigen
        loadImages();
    }

    private void loadImages() {
        // Hier kannst du später mehrere Bilder hinzufügen
        String[] imageFiles = {"pixil-frame-0.jpg"};

        for (String imageFile : imageFiles) {
            try {
                // Lade Bild als Vorschau
                InputStream is = getAssets().open(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                // Erstelle ImageView für Vorschau
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        400, // Breite in Pixel
                        400  // Höhe in Pixel
                );
                params.setMargins(16, 16, 16, 16);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);

                // Rahmen hinzufügen
                imageView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                imageView.setPadding(8, 8, 8, 8);

                // Click-Listener: Öffne GameActivity mit diesem Bild
                final String selectedImage = imageFile;
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    intent.putExtra("IMAGE_FILE", selectedImage);
                    startActivity(intent);
                });

                // Füge ImageView zur Galerie hinzu
                imageGallery.addView(imageView);

            } catch (Exception e) {
                Toast.makeText(this, "Fehler beim Laden von " + imageFile,
                        Toast.LENGTH_SHORT).show();
            }
        }
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