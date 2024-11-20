package com.boota.javaproject;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import javax.imageio.ImageIO;
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
    ArrayList<UseCaseSystemBoundaryBox> boundaryBoxes = new ArrayList<>();

    private String activeTool = null;
    private Point initialPoint = null;

    private Map<Node, Object> elementMap = new HashMap<>();


    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

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


    public void drawAssociation(Point initial, Point finalPoint) {
        activeTool = null;
        Boolean actor = false;
        Boolean useCase = false;
        UseCaseActor associatedActor = null;
        UseCase associatedUseCase = null;

        Object objectX = findElementNearPoint(initial);
        Object objectY = findElementNearPoint(finalPoint);

        if (objectX != null) {
            if (objectX instanceof UseCaseActor && !actor) {
                actor = true;
                associatedActor = (UseCaseActor) objectX;
            } else if (objectX instanceof UseCase && !useCase) {
                useCase = true;
                associatedUseCase = (UseCase) objectX;
            }
        } else {
            showWarning("No Actor Found", "No Actor or Use Case Found at Initial Point");
            return;
        }

        if (objectY != null) {
            if (objectY instanceof UseCaseActor && !actor) {
                actor = true;
                associatedActor = (UseCaseActor) objectY;
            } else if (objectY instanceof UseCase && !useCase) {
                useCase = true;
                associatedUseCase = (UseCase) objectY;
            } else {
                showWarning("Warning", "Cannot Have Association from Actor to Actor or Use Case to Use Case");
            }
        } else {
            showWarning("No Use Case Found", "No Actor or Use Case Found at Final Point");
            return;
        }

        drawLine(associatedActor.getInitial(),associatedUseCase.getInitialpoint());

    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearCanvas(){
        canvasPane.getChildren().clear();
        actors.clear();
        useCases.clear();
        associations.clear();
        boundaryBoxes.clear();
        includeRelations.clear();
        elementMap.clear();
        excludeRelations.clear();
    }

    public void drawLine(Point x, Point y) {

        Line line = new Line();
        line.setStartX(x.getX());
        line.setStartY(x.getY());
        line.setEndX(y.getX());
        line.setEndY(y.getY());
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        canvasPane.getChildren().add(line);

    }

    private UseCase checkUseCaseOnPoint(Point point) {
        UseCase associatedUseCase = null;
        Object object = findElementNearPoint(point);
        if (object instanceof UseCase) {
            associatedUseCase = (UseCase) object;
            return associatedUseCase;
        }
        return null;
    }

    public void drawInclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);

        if (startUseCase != null && endUseCase != null) {

        }else if (startUseCase == null){
            showWarning("Use Case Not Found","Initial Point Does Not Have Use Case");
        } else if (endUseCase == null) {
            showWarning("Use Case Found","Final Point Does Not Have Use Case");
        }else{
            showWarning("Error","No Use Case Found");
        }
    }

    public void handleSnapshot() {
        activeTool = null;
        try {
            double canvasWidth = canvas.getWidth();
            double canvasHeight = canvas.getHeight();

            if (canvasWidth <= 0 || canvasHeight <= 0) {
                showWarning("Warning", "Canvas Dimension Error");
                return;
            }
            WritableImage writableImage = new WritableImage((int) canvasWidth, (int) canvasHeight);
            canvas.snapshot(null, writableImage);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Canvas Snapshot");
            fileChooser.setInitialFileName("canvas_snapshot.png");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save Canvas Snapshot");
                alert.setHeaderText("Image Saved");
                alert.showAndWait();
            } else {
                showWarning("Save Cancelled", "No file was selected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Unexpected Error", "Unable to save snapshot. Please try later.");
        }
    }

    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase != null && endUseCase != null) {

        }else if (startUseCase == null){
            showWarning("Use Case Not Found","Initial Point Does Not Have Use Case");
        } else if (endUseCase == null) {
            showWarning("Use Case Found","Final Point Does Not Have Use Case");
        }else{
            showWarning("Error","No Use Case Found");
        }
    }

    public void drawBoundaryBox(Point initial) {
        activeTool = null;
        Rectangle rectangle = new Rectangle();
        rectangle.setX(initial.getX());
        rectangle.setY(initial.getY());
        rectangle.setWidth(300.0);
        rectangle.setHeight(350.0);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2.0);

        UseCaseSystemBoundaryBox boundaryBox = new UseCaseSystemBoundaryBox(initial,350.0,300.0);
        Label label = new Label(boundaryBox.getName());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setLayoutX(initial.getX()  + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(initial.getY() + 4);
        boundaryBoxes.add(boundaryBox);

        canvasPane.getChildren().addAll(rectangle,label);
        elementMap.put(rectangle, boundaryBox);
    }

    public void reDrawBoundaryBox(UseCaseSystemBoundaryBox box) {
        activeTool = null;
        Rectangle rectangle = new Rectangle();
        rectangle.setX(box.getInitialPoint().getX());
        rectangle.setY(box.getInitialPoint().getY());
        rectangle.setWidth(box.getWidth());
        rectangle.setHeight(box.getLength());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2.0);

        Label label = new Label(box.getName());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setLayoutX(box.getInitialPoint().getX()  + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(box.getInitialPoint().getY() + 4);
        boundaryBoxes.add(box);

        canvasPane.getChildren().addAll(rectangle,label);
        elementMap.put(rectangle, box);
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

    public void reDrawCanvas(){
        List<UseCaseActor> actorsCopy = new ArrayList<>(actors);
        List<UseCase> useCasesCopy = new ArrayList<>(useCases);
        List<UseCaseSystemBoundaryBox> boundaryBoxesCopy = new ArrayList<>(boundaryBoxes);
        clearCanvas();

        for (UseCaseActor actor : actorsCopy) {
            reDrawActor(actor);
        }
        for (UseCase useCase : useCasesCopy) {
            reDrawUseCase(useCase);
       }
        for (UseCaseSystemBoundaryBox boundaryBox : boundaryBoxesCopy) {
            reDrawBoundaryBox(boundaryBox);
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