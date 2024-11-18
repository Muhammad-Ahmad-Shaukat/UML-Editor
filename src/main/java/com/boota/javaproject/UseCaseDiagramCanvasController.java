package com.boota.javaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class UseCaseDiagramCanvasController {

    @FXML
    private Pane canvasPane;

    private Canvas canvas;
    private GraphicsContext gc;
    ArrayList<UseCase> useCases = new ArrayList<>();
    ArrayList<UseCaseAssociation> associations = new ArrayList<>();
    ArrayList<UseCaseActor> actors = new ArrayList<>();
    ArrayList<DependencyRelationship> includeRelation = new ArrayList<>();
    ArrayList<DependencyRelationship> excludeRelation = new ArrayList<>();

    private String activeTool = null;
    private Point initialPoint = null;

    // Map to track all elements by their positions
    private Map<Point, Object> elementMap = new HashMap<>();

    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);

        canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
        canvasPane.setOnMousePressed(this::handleMousePress);
        canvasPane.setOnMouseReleased(this::handleMouseRelease);
        canvasPane.setOnMouseClicked(this::handleDoubleClick);
    }

    private void handleMousePress(MouseEvent event) {
        if (activeTool != null) {
            initialPoint = trackMouseCoordinates(event);
        }
    }

    private void handleDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Point clickPoint = trackMouseCoordinates(event);
            Object selectedElement = findElementNearPoint(clickPoint);

            if (selectedElement != null) {
                System.out.println("Element selected: " + selectedElement);

                // Check if the selected element is a UseCaseActor
                if (selectedElement instanceof UseCaseActor) {
                    UseCaseActor actor = (UseCaseActor) selectedElement;
                    showActorDetails(actor); // Call the function to show actor details
                } else {
                    System.out.println("Selected element is not a UseCaseActor.");
                    // Optionally, handle other element types here
                }
            } else {
                System.out.println("No element found near point: " + clickPoint);
            }
        }
    }


    private void showActorDetails(UseCaseActor actor) {
        System.out.println("Showing actor: ");
    }

    private void handleMouseRelease(MouseEvent event) {
        if (activeTool != null && initialPoint != null) {
            Point finalPoint = trackMouseCoordinates(event);

            if ("Actor".equals(activeTool)) {
                drawActor(initialPoint);
            } else if ("Exclude".equals(activeTool)) {
                drawExclude(initialPoint, finalPoint);
            } else if ("Include".equals(activeTool)) {
                drawInclude(initialPoint, finalPoint);
            } else if ("UseCase".equals(activeTool)) {
                drawUseCase(initialPoint);
            } else if ("UseCaseAssociation".equals(activeTool)) {
                drawAssociation(initialPoint, finalPoint);
            } else if ("BoundaryBox".equals(activeTool)) {
                drawBoundaryBox(initialPoint);
            }

            initialPoint = null;
        }
    }

    private Point trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        return new Point(x, y);
    }

    public void handleActorClick() {
        activeTool = "Actor";
    }

    public void handleBoundaryBoxClick() {
        activeTool = "BoundaryBox";
    }

    public void handleAssociationClick() {
        activeTool = "UseCaseAssociation";
    }

    public void handleIncludeClick() {
        activeTool = "Include";
    }

    public void handleExcludeClick() {
        activeTool = "Exclude";
    }

    public void handleUseCaseClick() {
        activeTool = "UseCase";
    }

    public void drawActor(Point initial) {
        activeTool = null;

        double size = 50.0;
        Rectangle square = new Rectangle(initial.getX(), initial.getY(), size, size);
        square.setFill(Color.BLACK);
        canvasPane.getChildren().add(square);

        UseCaseActor actor = new UseCaseActor(initial);
        actors.add(actor);
        elementMap.put(initial, actor);
    }


    public void drawUseCase(Point initial)  {
        activeTool = null;
        UseCase useCase = new UseCase(initial);
        useCases.add(useCase);
        elementMap.put(initial, useCase); // Add to map

    }

    public void drawAssociation(Point initial, Point finalPoint) {
        activeTool = null;
    }

    public void drawInclude(Point initial, Point finalPoint) {
        activeTool = null;
    }

    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
    }

    public void drawBoundaryBox(Point initial) {
        activeTool = null;
    }

    private Object findElementNearPoint(Point point) {
        double tolerance = 10.0; // Define a tolerance range for selection
        for (Map.Entry<Point, Object> entry : elementMap.entrySet()) {
            Point elementPoint = entry.getKey();
            if (point.distance(elementPoint) <= tolerance) {
                return entry.getValue();
            }
        }
        return null;
    }
}