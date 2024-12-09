package com.boota.javaproject;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public class SnapshotHelper {
    public static void handleSnapshot(Pane canvasPane, Canvas canvas, Consumer<String> showWarning, Consumer<WritableImage> saveImage) {
        try {
            // Ensure valid dimensions for canvasPane
            double canvasPaneWidth = canvasPane.getWidth();
            double canvasPaneHeight = canvasPane.getHeight();

            if (canvasPaneWidth <= 0 || canvasPaneHeight <= 0) {
                showWarning.accept("Canvas Pane Dimension Error");
                return;
            }

            // Create a WritableImage large enough to capture the canvasPane
            WritableImage writableImage = new WritableImage((int) canvasPaneWidth, (int) canvasPaneHeight);

            // Capture the content of canvasPane (including Canvas and other UI elements)
            canvasPane.snapshot(null, writableImage);

            // Call the saveImage method
            saveImage.accept(writableImage);

        } catch (Exception e) {
            e.printStackTrace();
            showWarning.accept("Unable to save snapshot. Please try later.");
        }
    }
}
