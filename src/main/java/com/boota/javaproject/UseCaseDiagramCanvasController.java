package com.boota.javaproject;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Node, Object> elementMap = new HashMap<>();


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
                } else if (selectedElement instanceof UseCase) {
                    UseCase useCase = (UseCase) selectedElement;
                    showUseCasdeDetails(useCase);
                }
            } else {

            }
        }
    }

    private void showUseCasdeDetails(UseCase useCase) {
        Stage stage = new Stage();
        stage.setTitle("UseCase Details");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label nameLabel = new Label("UseCase Name:");
        TextField nameField = new TextField(useCase.getName());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String newName = nameField.getText();
            useCase.setName(newName);
            reDrawCanvas();
            stage.close();
        });

        layout.getChildren().addAll(nameLabel, nameField, submitButton);

        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
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
            actor.setName(newName);
            reDrawCanvas();
            stage.close();
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

        // Load and convert SVG image to PNG (code unchanged)
        String svgFilePath = "C:\\Users\\ahmad\\IdeaProjects\\javaproject\\src\\main\\resources\\com\\boota\\javaproject\\actor.svg";
        Image svgImage = null;
        try {
            File svgFile = new File(svgFilePath);
            File pngFile = File.createTempFile("temp-actor", ".png");
            TranscoderInput inputSvgImage = new TranscoderInput(new FileInputStream(svgFile));
            TranscoderOutput outputPngImage = new TranscoderOutput(new FileOutputStream(pngFile));
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size * 2);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size * 4);
            transcoder.transcode(inputSvgImage, outputPngImage);
            svgImage = new Image(pngFile.toURI().toString());
            pngFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create VBox for Actor
        ImageView svgImageView = new ImageView(svgImage);
        svgImageView.setFitWidth(size);
        svgImageView.setPreserveRatio(true);

        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        VBox actorBox = new VBox(5);
        actorBox.setLayoutX(initial.getX());
        actorBox.setLayoutY(initial.getY());
        actorBox.setAlignment(Pos.CENTER);
        actorBox.getChildren().addAll(svgImageView, actorNameLabel);

        canvasPane.getChildren().add(actorBox);


        elementMap.put(actorBox, actor);
        actors.add(actor);
    }

    public void reDrawActor(UseCaseActor actor) {
        activeTool = null;
        double size = 50.0;

        // Load and convert SVG image to PNG (code unchanged)
        String svgFilePath = "C:\\Users\\ahmad\\IdeaProjects\\javaproject\\src\\main\\resources\\com\\boota\\javaproject\\actor.svg";
        Image svgImage = null;
        try {
            File svgFile = new File(svgFilePath);
            File pngFile = File.createTempFile("temp-actor", ".png");
            TranscoderInput inputSvgImage = new TranscoderInput(new FileInputStream(svgFile));
            TranscoderOutput outputPngImage = new TranscoderOutput(new FileOutputStream(pngFile));
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size * 2);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size * 4);
            transcoder.transcode(inputSvgImage, outputPngImage);
            svgImage = new Image(pngFile.toURI().toString());
            pngFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView svgImageView = new ImageView(svgImage);
        svgImageView.setFitWidth(size);
        svgImageView.setPreserveRatio(true);

        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(javafx.scene.paint.Color.BLACK);

        VBox actorBox = new VBox(5);
        actorBox.setLayoutX(actor.getInitial().getX());
        actorBox.setLayoutY(actor.getInitial().getY());
        actorBox.setAlignment(Pos.CENTER);
        actorBox.getChildren().addAll(svgImageView, actorNameLabel);

        canvasPane.getChildren().add(actorBox);


        elementMap.put(actorBox, actor);
        actors.add(actor);
    }

    public void drawUseCase(Point initial) {
        activeTool = null;
        UseCase useCase = new UseCase(initial);
        useCases.add(useCase);

        StackPane useCasePane = new StackPane();
        useCasePane.setLayoutX(initial.getX());
        useCasePane.setLayoutY(initial.getY());

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(20);
        ellipse.setRadiusY(20);
        ellipse.setFill(Color.WHITE);
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(2);

        Text text = new Text(useCase.getName());
        text.setFill(Color.BLACK); // Text color
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        text.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
            double textWidth = newBounds.getWidth();
            double textHeight = newBounds.getHeight();

            ellipse.setRadiusX(Math.max(50, textWidth / 2 + 20)); // Ensure a minimum size
            ellipse.setRadiusY(Math.max(30, textHeight / 2 + 20));
        });

        useCasePane.getChildren().addAll(ellipse, text);
        canvasPane.getChildren().add(useCasePane);

        elementMap.put(useCasePane, useCase);
        useCases.add(useCase);
    }

    public void reDrawUseCase(UseCase useCase) {
        activeTool = null;

        StackPane useCasePane = new StackPane();
        useCasePane.setLayoutX(useCase.getInitialpoint().getX());
        useCasePane.setLayoutY(useCase.getInitialpoint().getY());

        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(100);
        ellipse.setRadiusY(50);
        ellipse.setFill(Color.WHITE);
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(2);

        Text text = new Text(useCase.getName());
        text.setFill(Color.BLACK); // Text color
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        text.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
            double textWidth = newBounds.getWidth();
            double textHeight = newBounds.getHeight();

            ellipse.setRadiusX(Math.max(50, textWidth / 2 + 20));
            ellipse.setRadiusY(Math.max(30, textHeight / 2 + 20));
        });

        useCasePane.getChildren().addAll(ellipse, text);
        canvasPane.getChildren().add(useCasePane);

        elementMap.put(useCasePane, useCase);
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
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            if (entry.getKey() instanceof VBox) {
                VBox vbox = (VBox) entry.getKey();
                Bounds bounds = vbox.getBoundsInParent();
                if (bounds.contains(point.getX(), point.getY())) {
                    return entry.getValue();
                }
            }
            if (entry.getKey() instanceof StackPane) {
                StackPane stackPane = (StackPane) entry.getKey();
                Bounds bounds = stackPane.getBoundsInParent();
                if (bounds.contains(point.getX(), point.getY())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public void reDrawCanvas(){
        List<UseCaseActor> actorsCopy = new ArrayList<>(actors);
        List<UseCase> useCasesCopy = new ArrayList<>(useCases);
        canvasPane.getChildren().clear();

        for (UseCaseActor actor : actorsCopy) {
            reDrawActor(actor);
        }
        for (UseCase useCase : useCasesCopy) {
            reDrawUseCase(useCase);
       }
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