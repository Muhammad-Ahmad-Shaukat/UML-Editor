package com.boota.javaproject;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
/**
 * The SnapshotHelper class provides utility methods for handling snapshots
 * of JavaFX components. This class is intended to streamline the process
 * of capturing the visual representation of a Pane or Canvas and provides
 * mechanisms for displaying warnings and saving the captured snapshot as
 * a WritableImage.
 *
 * This class is designed to handle common issues like invalid dimensions
 * and potential exceptions, making it robust in various usage scenarios.
 * It allows for flexible handling of warnings and saving images by accepting
 * Consumer functional interfaces for these tasks.
 */
public class SnapshotHelper {
    /**
     * Captures a snapshot of the provided `canvasPane` and saves it using the provided `saveImage` consumer.
     * Displays a warning using the `showWarning` consumer if the dimensions of the `canvasPane` are invalid
     * or an error occurs during the snapshot capture process.
     *
     * @param canvasPane the Pane containing the canvas and other UI components to be captured.
     *                   Its dimensions are validated before capturing the snapshot.
     * @param canvas the Canvas contained within the `canvasPane`. Although present as an argument,
     *               it is not directly used in this method.
     * @param showWarning a consumer that accepts a `String` message to display warnings.
     *                    Called when the `canvasPane` dimensions are invalid or an error occurs.
     * @param saveImage a consumer that accepts a `WritableImage` to handle saving of the captured
     *                  snapshot. Called after successfully capturing the snapshot.
     */
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
