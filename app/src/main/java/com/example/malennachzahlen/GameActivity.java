package com.example.malennachzahlen;

import android.content.Intent;
import android.graphics.Color;
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
import java.util.HashMap;
import java.util.Map;

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
   private int selectedColorNumber = -1;
   private Map<Integer, Integer> colorMap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_game);

      // Firebase initalisieren
      mAuth =FirebaseAuth.getInstance();
      db = FirebaseFirestore.getInstance();

      // UserId holen
      userId = mAuth.getCurrentUser().getUid();

      // Views verküpfen
      progressText = findViewById(R.id.progressText);
      backButton = findViewById(R.id.backButton);
      paintView = findViewById(R.id.paintView);
      colorPalette = findViewById(R.id.colorPalette);

      imageFile = getIntent().getStringExtra("IMAGE_FILE");

      // Button Listener
      backButton.setOnClickListener(v -> {
         saveProgress();
         navigateToHome();
      });

      // Bild unf Fortschritt laden
      loadImage();
      loadProgress();

//       Listener für Fortschrittsanzeige
//      paintView.setOnPixelPaintedListener(() -> {
//         updateProgressDisplay();
//      });

   }

   // Bild mithilfe der PaintByNumbersView laden
   private void loadImage() {
      try {
         InputStream is = getAssets().open(imageFile);
         Bitmap bitmap = BitmapFactory.decodeStream(is);
         is.close();

         if (bitmap != null) {

            // Bild in PaintByNumbersView einsetzen
            paintView.setImage(bitmap);

            // ColorMap (Pixelfarbe - Zahl Zuordnung) aus PaintByNumbersView holen
            colorMap = paintView.getColorMap();

            // Erstelle Palette
            createColorPalette();

            Toast.makeText(this, "Bild geladen", Toast.LENGTH_SHORT).show();
         } else {
            Toast.makeText(this, "Fehler: Bild konnte nicht geladen werden", Toast.LENGTH_LONG).show();
         }
      } catch (Exception e) {
         e.printStackTrace();
         Toast.makeText(this, "Fehler beim Laden: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
   }

   private void createColorPalette() {
      for (Map.Entry<Integer, Integer> entry : colorMap.entrySet()) {
         int number = entry.getKey();
         int color = entry.getValue();

         // Button für jede Farbe erstellen
         Button colorButton = new Button(this);
         colorButton.setText(String.valueOf(number));
         colorButton.setBackgroundColor(color);

         // Button-Größe
         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                 150,
                 150
         );
         params.setMargins(8,8,8,8);
         colorButton.setLayoutParams(params);

         // Textfarbe der Zahl auf Button je nach Helligkeit der Farbe
         colorButton.setTextColor(isColorDark(color) ? Color.WHITE : Color.BLACK);
         colorButton.setTextSize(18);

         // Click Listener
         colorButton.setOnClickListener(v -> {
            selectedColorNumber = number;
            paintView.setSelectedColor(number);
            Toast.makeText(this, "Farbe " + number + " ausgewählt", Toast.LENGTH_SHORT).show();
         });

         colorPalette.addView(colorButton);
      }

   }

   // Herausfinden ob Farbe Hell oder Dunkel ist
   private boolean isColorDark(int color) {
      int red = Color.red(color);
      int green = Color.green(color);
      int blue = Color.blue(color);

      // Helligkeit berechnen (Formel vereinfacht, weil ausreichend genau und besser für Performance)
      double brightness = (red * 0.299 + green * 0.587 + blue * 0.114);
      return brightness < 128;
   }

   // Fortschrittsanzeige
   private void updateProgressDisplay() {
      int painted = paintView.countPaintedPixels();
      int total = paintView.getTotalPixels();

      if (total > 0) {
         int percentage = (painted * 100) / total;
         progressText.setText("Fortschritt: " + percentage + "%");
      } else {
         progressText.setText("Fortschritt: 0%");
      }
   }

   private void saveProgress() {
      Map<String, Boolean> paintedPixelsMap = paintView.getPaintedPixelsMap();

      Map<String, Object> progressData = new HashMap<>();
      progressData.put("imageFile", imageFile);
      progressData.put("paintedPixels", paintedPixelsMap);
      progressData.put("paintedCount", paintView.countPaintedPixels());
      progressData.put("totalPixels", paintView.getTotalPixels());

      db.collection("user")
              .document(userId)
              .collection("progress")
              .document(imageFile)
              .set(progressData)
              .addOnFailureListener(e -> {
                 Toast.makeText(this, "Fehler beim Speichern", Toast.LENGTH_SHORT).show();
              });

   }

   private void loadProgress() {
      db.collection("user")
              .document(userId)
              .collection("progress")
              .document(imageFile)
              .get()
              .addOnSuccessListener(documentSnapshot -> {
                 if (documentSnapshot.exists()) {
                    Map<String, Boolean> paintedPixelsMap = (Map<String, Boolean>) documentSnapshot.get("paintedPixels");

                    if (paintedPixelsMap != null) {
                       paintView.setPaintedPixels(paintedPixelsMap);
                       updateProgressDisplay();
                    }
                 }
              })
              .addOnFailureListener(e -> {
                 Toast.makeText(this, "Fehler beim Laden", Toast.LENGTH_LONG).show();
              });
   }

   private void navigateToHome() {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
   }
}