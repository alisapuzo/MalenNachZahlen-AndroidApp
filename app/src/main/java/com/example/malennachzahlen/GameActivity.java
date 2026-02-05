package com.example.malennachzahlen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.malennachzahlen.views.PaintByNumbersView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

      imageFile = "ampel.png";

      // Button Listener
      backButton.setOnClickListener(v -> navigateToHome());
   }

   private void navigateToHome() {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
   }
}