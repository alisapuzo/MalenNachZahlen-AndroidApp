package com.example.malennachzahlen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private LinearLayout imageContainer;
    private String selectedImage = null; //speichert welches Bild gewählt wurde

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Views finden
        startGameButton = findViewById(R.id.startGameButton);
        logoutButton = findViewById(R.id.logoutButton);
        userInfoText = findViewById(R.id.userInfoText);
        imageContainer = findViewById(R.id.imageContainer);

        // Firebase initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Prüfe ob User eingeloggt ist
        if (mAuth.getCurrentUser() == null) {
            navigateToMain();
            return;
        }

        // Button Listener
        startGameButton.setOnClickListener(v -> {

            if (selectedImage == null) {
                Toast.makeText(this, "Bitte zuerst ein Bild auswählen!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("IMAGE_FILE", selectedImage);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> logout());

        loadImageChoices(); //Methode aufrufen
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

    private void loadImageChoices() {
        String[] imageFiles = {"ampel.png", "car.png", "groot.png", "rocket.png"};

        for (String imageName : imageFiles) {
            ImageView imageView = new ImageView(this);

            // Größe der Vorschau
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
            params.setMargins(16, 0, 16, 0); //Abstand zw Bildern
            imageView.setLayoutParams(params); //Layout-Parameter von Parent-Layouts zugewiesen
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(0xFFCCCCCC);

            // Bild aus assets laden
            try {
                InputStream is = getAssets().open(imageName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Klick-Logik
            imageView.setOnClickListener(v -> {
                selectedImage = imageName;
                Toast.makeText(this, imageName + " ausgewählt", Toast.LENGTH_SHORT).show();

                // Alle Rahmen zurücksetzen
                for (int i = 0; i < imageContainer.getChildCount(); i++) {
                    imageContainer.getChildAt(i).setBackgroundColor(0xFFCCCCCC);
                }

                // Aktives Bild hervorheben
                imageView.setBackgroundColor(0xFF00BCD4);
            });

            imageContainer.addView(imageView);
        }

    }


}