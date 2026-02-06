package com.example.malennachzahlen.views;


import  android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PaintByNumbersView extends View {

    private Bitmap originalBitmap; // originales Bild
    private Bitmap grayBitmap;  // augegrautes Bild
    private int[][] pixelColors;    // "Matrix" mit Farbe der Pixel
    private int[][] colorNumbers;   // "Matrix" mit Nummern der Farben
    private boolean[][] isPainted;
    private Map<Integer, Integer> colorMap; // Mappt Nummer zur entsprechenden Farbe
    private Paint paint;
    private Paint textPaint;
    private float scaleFactor = 1.0f;
    private int selectedColor = -1;
    private int totalPixels = -1; // Anzahl wird beim ersten laden geupdatet und gespeichert

    public PaintByNumbersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Pixel
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        // Zahlen
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

    }

    // Bild laden und analysieren
    public void setImage(Bitmap bitmap) {
        this.originalBitmap = bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        pixelColors = new int[width][height];
        colorNumbers = new int[width][height];
        isPainted = new boolean[width][height];
        colorMap = new HashMap<>();
        grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Pixel untersuchen
        int colorCounter = 1;

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                int pixel = bitmap.getPixel(x,y);
                pixelColors[x][y] = pixel;

                // Wenn Farbe neu, Nummer vergeben
                if (!colorMap.containsValue(pixel)) {
                    colorMap.put(colorCounter, pixel);
                    colorNumbers[x][y] = colorCounter;
                    colorCounter++;
                } else {
                    // Wenn Farbe nicht neu, passende Nummer finden und
                    for (Map.Entry<Integer, Integer> entry : colorMap.entrySet()) { // For each: geht jede Nummer zu Pixel zuordnung aus colorMap durch
                        if (entry.getValue() == pixel) {
                            colorNumbers[x][y] = entry.getKey();
                            break;
                        }
                    }
                }
                // Graues Bild erstellen
                grayBitmap.setPixel(x, y, Color.LTGRAY);

                // noch nicht ausgemalt
                isPainted[x][y] = false;
            }
        }

        invalidate(); // View neu zeichnen

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (grayBitmap == null) return; // Bild noch nciht geladen

        int width = grayBitmap.getWidth();
        int height = grayBitmap.getHeight();

        // Skalierung, Bild an Bildschirm anpassen
        float scaleX = (float) getWidth()/width;
        float scaleY = (float) getHeight()/height;
        scaleFactor = Math.min(scaleX, scaleY);

        float pixelSize = scaleFactor;

        // Pixel zeichnen
        for (int x=0; x<width; x++) {
            for ( int y=0; y<height; y++) {
                //
                float left = x * pixelSize;
                float top = y * pixelSize;
                float right = left + pixelSize;
                float bottom = top + pixelSize;

                //Farbe auswählen
                if (isPainted[x][y]) {
                    paint.setColor(pixelColors[x][y]);
                } else {
                    paint.setColor(grayBitmap.getPixel(x, y));
                }

                // Rechteck zeichnen
                canvas.drawRect(left, top, right, bottom, paint);

                // Nummer einzeichnen
                if (!isPainted[x][y]) {
                    textPaint.setTextSize(pixelSize * 0.5f);
                    canvas.drawText(
                            String.valueOf(colorNumbers[x][y]),
                            left + pixelSize / 2,       // Berechnet mitte des Rechtecks
                            top + pixelSize / 2 + textPaint.getTextSize() / 3,   // wegen Baseline korrektur nach unten
                            textPaint
                    );
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && grayBitmap != null) {

            //Touch Position zu Pixel-Koordinate umrechnen
            int x = (int) (event.getX()/scaleFactor);
            int y = (int) (event.getY()/scaleFactor);

            // Prüfen ob die Postiton innerhalb vom Grid ist
            if (x>=0 && x<grayBitmap.getWidth() && y>=0 && y<grayBitmap.getHeight()) {
                // Prüfen ob die ausgewählte Farbe stimmt
                if (selectedColor == colorNumbers[x][y] && !isPainted[x][y]) {
                    isPainted[x][y] = true; // ausmalen
                    invalidate(); // View neu zeichnen
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    // SETTER UND GETTER
    // Farbe auswählen (wird von GameActivity aufgerufen)
    public void setSelectedColor(int colorNumber) {
        this.selectedColor = colorNumber;
    }

    // Gibt alle Farben mit ihrer dazugehörigen Nummer zurück
    public Map<Integer, Integer> getColorMap() {
        return colorMap;
    }

    // Fortschritt als Map speichern (kann nicht als 2D-Array in Firebase gespeichert werden)
    public Map<String, Boolean> getPaintedPixelsMap() {
        Map<String, Boolean> map = new HashMap<>();
        if (isPainted == null)
            return map; // Variable == null wenn sie initalisiert aber nicht erzeugt wurde

        for (int x=0; x<isPainted.length; x++) {    // Anzahl an Zeilen
            for (int y=0; y<isPainted[0].length; y++) {     // Anzahl Spalten über Länge der ersten Zeile
                if (isPainted[x][y]) {
                    map.put(x + "_" + y, true);     // Speichert die Koordinaten so {"3_5": true}
                }
            }
        }
        return map;
    }

    // Lade gespeicherten Fortschritt
    public void setPaintedPixels(Map<String, Boolean> paintedPixels) {
        if (isPainted == null)
            return;
        for (Map.Entry<String, Boolean> entry : paintedPixels.entrySet()) {
            String[] coords = entry.getKey().split("_");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            isPainted[x][y] = entry.getValue();
        }
        invalidate();
    }

    // Gebe Gesamte Anzahl an Pixel zurück
    public int getTotalPixels(){
        if (totalPixels != -1) {
            return totalPixels; // Nutze Cache
        }

        if (originalBitmap == null) {
            return 0;
        }

        int count = 0;
        for (int x = 0; x < originalBitmap.getWidth(); x++) {
            for (int y = 0; y < originalBitmap.getHeight(); y++) {
                int pixelColor = originalBitmap.getPixel(x, y);
                count++;
            }
        }

        totalPixels = count; // Speichere im Cache
        return count;
    }

    // Zähle ausgemalte Pixel
    public int countPaintedPixels() {
        int count = 0;
        for (int x = 0; x < isPainted.length; x++) {
            for (int y = 0; y < isPainted[0].length; y++) {
                if (isPainted[x][y]) count++;
            }
        }
        return count;
    }
}
