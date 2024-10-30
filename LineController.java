package com.boota.demo;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.ArrayList;
import java.util.List;

public class LineController {

    //fxml elements
    @FXML
    private VBox root;
    @FXML
    private Canvas canvas;
    @FXML
    private Button drawButton;
    @FXML
    private Button clearButton;

    private Point startPoint; // point of start of line
    private boolean isDrawing = false;// initial since user has to select option
    private List<Line> lines = new ArrayList<>();//arraylist to store all lines

    @FXML
    public void initialize() {
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(drawButton.getHeight()
                + clearButton.getHeight() + 20));

        // Combine event handling into a single method
        canvas.setOnMousePressed(this::handleMouseEvent);
        canvas.setOnMouseDragged(this::handleMouseEvent);
        canvas.setOnMouseReleased(this::handleMouseEvent);

        // Handle double-clicks to select line
        canvas.setOnMouseClicked(this::handleDoubleClick);
    }

    private void handleMouseEvent(MouseEvent event) {
        //to get initial point of start of line
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            startDrawing(event);
        }
        // to draw a temporary dotted line to help user navigate
        else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            drawTemporaryLine(event);
        }
        // to get end coordinate of the line
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            endDrawing(event);
        }
    }

    // double mouse click if on the line to select it
    private void handleDoubleClick(MouseEvent event) {
        if (!isDrawing && event.getClickCount() == 2) {
            Point clickPoint = new Point(event.getX(), event.getY());
            Line selectedLine = findLineAtPoint(clickPoint);
            if (selectedLine != null) {
                displayLineDetails(selectedLine);
            }
        }
    }

    //to call line function to confirm if a point is on the line or not
    private Line findLineAtPoint(Point point) {
        for (Line line : lines) {
            if (line.isPointOnLine(point)) {
                return line;
            }
        }
        return null;
    }

    //till now a dummy function
    private void displayLineDetails(Line line) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Line Details");
        VBox box = new VBox();
        box.setSpacing(20);

        box.setAlignment(Pos.CENTER);
        HBox firstEndBox = new HBox();
        firstEndBox.setSpacing(15);

        Label firstEndLabel = new Label("First End Multiplicity:");
        TextField frommultiplicity = new TextField();
        TextField tomultiplicity = new TextField();
        firstEndBox.getChildren().addAll(firstEndLabel, frommultiplicity,tomultiplicity);

        // Second end multiplicity
        HBox secondEndBox = new HBox();
        secondEndBox.setSpacing(15);
        Label secondEndLabel = new Label("Second End Multiplicity:");
        TextField secondEndTextField = new TextField();
        TextField xyz = new TextField();
        secondEndBox.getChildren().addAll(secondEndLabel, secondEndTextField,xyz);

        box.getChildren().addAll(firstEndBox, secondEndBox);

        Scene scene = new Scene(box, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void startDrawing(MouseEvent event) {
        if (isDrawing) {
            startPoint = new Point(event.getX(), event.getY());
        }
    }

    private void drawTemporaryLine(MouseEvent event) {
        if (isDrawing && startPoint != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            redrawCanvas(gc); // Clear and redraw all permanent lines
            drawDottedLine(gc, startPoint.getX(), startPoint.getY(), event.getX(), event.getY());
        }
    }

    private void endDrawing(MouseEvent event) {
        if (isDrawing && startPoint != null) {
            Point endPoint = new Point(event.getX(), event.getY());
            lines.add(new Line(startPoint, endPoint));
            startPoint = null;
            redrawCanvas(canvas.getGraphicsContext2D());
        }
    }

    public void toggleDrawing() {
        isDrawing = !isDrawing;
        drawButton.setText(isDrawing ? "Stop Drawing" : "Start Drawing");
    }

    public void clearCanvas() {
        lines.clear();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void redrawCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        for (Line line : lines) {
            gc.strokeLine(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY());
        }
    }

    private void drawDottedLine(GraphicsContext gc, double startX, double startY, double endX, double endY) {
        gc.setLineDashes(10); // Set to dotted for preview
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(startX, startY, endX, endY);
        gc.setLineDashes(null); // Reset to solid for future drawings
    }
}
