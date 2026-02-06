package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.example.malennachzahlen.views.PaintByNumbersView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

   private TextView progressText;
   private Button pauseButton;
   private Button backButton;
   private PaintByNumbersView paintView;
   private LinearLayout colorPalette;

   private FirebaseAuth mAuth;
   private FirebaseFirestore db;
   private String userId;
   private String imageFile;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_game);

      // Firebase initalisieren
      mAuth =FirebaseAuth.getInstance();
      db = FirebaseFirestore.getInstance();

      // Views verküpfen
      progressText = findViewById(R.id.progressText);
      backButton = findViewById(R.id.backButton);
      paintView = findViewById(R.id.paintView);
      colorPalette = findViewById(R.id.colorPalette);

      userId = mAuth.getCurrentUser().getUid();

      // TODO image file laden aus intent
      imageFile = getIntent().getStringExtra("IMAGE_FILE");


      // Button Listener
      backButton.setOnClickListener(v -> navigateToHome());

      loadImage();
   }

   private void loadImage() {
      try {
         InputStream is = getAssets().open(imageFile);
         Bitmap bitmap = BitmapFactory.decodeStream(is);
         is.close();

         if (bitmap != null) {
            // Bild in PaintByNumbersView einsetzen
            paintView.setImage(bitmap);
            Toast.makeText(this, "Bild geladen", Toast.LENGTH_SHORT).show();
         } else {
            Toast.makeText(this, "Fehler: Bild konnte nicht geladen werden", Toast.LENGTH_LONG).show();
         }
      } catch (Exception e) {
         e.printStackTrace();
         Toast.makeText(this, "Fehler beim Laden: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
   }

   private void navigateToHome() {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
   }
}