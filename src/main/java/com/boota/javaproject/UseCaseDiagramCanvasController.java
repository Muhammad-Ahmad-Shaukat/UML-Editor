package com.boota.javaproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ArrayList<DependencyRelationship> includeRelations = new ArrayList<>();
    ArrayList<DependencyRelationship> excludeRelations = new ArrayList<>();

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
                if (selectedElement instanceof UseCaseActor) {
                    UseCaseActor actor = (UseCaseActor) selectedElement;
                    showActorDetails(actor);
                }
            } else {

            }
        }
    }

    private void showActorDetails(UseCaseActor actor) {
        Stage stage = new Stage();
        stage.setTitle("Actor Details");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label nameLabel = new Label("Actor Name:");
        TextField nameField = new TextField(actor.getName());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String newName = nameField.getText();
            actor.setName(newName); // Update the actor's name
            reDrawCanvas(); // Redraw the canvas to reflect the changes
            stage.close(); // Close the form window
        });

        // Add the components to the layout
        layout.getChildren().addAll(nameLabel, nameField, submitButton);

        // Create and set the Scene
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show(); // Show the form
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
        UseCaseActor actor = new UseCaseActor(initial);
        double size = 50.0;

        Rectangle square = new Rectangle(size, size);
        square.setFill(Color.BLACK);

        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(Color.BLACK);

        VBox actorBox = new VBox(5);
        actorBox.setLayoutX(initial.getX());
        actorBox.setLayoutY(initial.getY());
        actorBox.setAlignment(Pos.CENTER);
        actorBox.getChildren().addAll(square, actorNameLabel);

        canvasPane.getChildren().add(actorBox);


        actors.add(actor);
        elementMap.put(initial, actor);
    }

    public void reDrawActor(UseCaseActor actor) {
        activeTool = null;
        double size = 50.0;

        Rectangle square = new Rectangle(size, size);
        square.setFill(Color.BLACK);

        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(Color.BLACK);

        VBox actorBox = new VBox(5);
        actorBox.setLayoutX(actor.getInitial().getX());
        actorBox.setLayoutY(actor.getInitial().getY());
        actorBox.setAlignment(Pos.CENTER);
        actorBox.getChildren().addAll(square, actorNameLabel);

        canvasPane.getChildren().add(actorBox);

        actors.add(actor);
        elementMap.put(actor.getInitial(), actor);
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

    public void reDrawCanvas(){
        List<UseCaseActor> actorsCopy = new ArrayList<>(actors);
canvasPane.getChildren().clear();


            // Loop to draw actors
        for (UseCaseActor actor : actorsCopy) {
            reDrawActor(actor);
        }
//        for (UseCaseActor actor : actors) {
//            drawActor(actor.getInitial());
//        }
//        for (UseCaseAssociation association : associations) {
//            drawAssociation(association.getStart(), association.getEnd());
//        }
//        for (UseCase useCase : useCases) {
//            drawUseCase(useCase.getInitialpoint());
//        }
//        for (DependencyRelationship includeRelation : includeRelations) {
//            drawInclude(includeRelation.getStartPoint(), includeRelation.getEndPoint());
//        }
//        for (DependencyRelationship excludeRelation : excludeRelations) {
//            drawExclude(excludeRelation.getStartPoint(), excludeRelation.getEndPoint());
//        }
    }
}