package com.example.malennachzahlen.views;


import  android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
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

    public PaintByNumbersView(Context context, AttributeSet attrs) {
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
    }
}
