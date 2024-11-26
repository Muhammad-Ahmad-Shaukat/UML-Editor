package UseCaseDiagram;

import ClassDiagram.Point;
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
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
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
    private Object currentlySelectedElement = null;

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
        canvasPane.setOnMouseClicked(this::handleMouseClick);
        canvasPane.setOnMouseDragged(this::handleMouseDrag);
    }

    private Point trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        return new Point(x, y);
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        Point clickPoint = trackMouseCoordinates(event);
        if (activeTool != null) {
            return;
        }
        Object selectedElement = findElementNearPoint(clickPoint);
        if (event.getClickCount() == 2) {
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                showDetailsIfSelected();
            }
        } else if (event.getClickCount() == 1) {
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
            } else {
                currentlySelectedElement = null;
            }
        }
    }

    @FXML
    private void handleMousePress(MouseEvent event) {
        initialPoint = trackMouseCoordinates(event);
    }

    @FXML
    private void handleMouseDrag(MouseEvent event) {
        if (currentlySelectedElement != null && activeTool == null) {
            Point currentPoint = trackMouseCoordinates(event);
            double deltaX = currentPoint.getX() - initialPoint.getX();
            double deltaY = currentPoint.getY() - initialPoint.getY();
            if (currentlySelectedElement instanceof UseCaseActor) {
                UseCaseActor actor = (UseCaseActor) currentlySelectedElement;
                actor.setInitial(new Point(actor.getInitial().getX() + deltaX, actor.getInitial().getY() + deltaY));
            } else if (currentlySelectedElement instanceof UseCase) {
                UseCase useCase = (UseCase) currentlySelectedElement;
                useCase.setInitialPoint(new Point(useCase.getInitialpoint().getX() + deltaX, useCase.getInitialpoint().getY() + deltaY));
            } else if (currentlySelectedElement instanceof UseCaseSystemBoundaryBox) {
                UseCaseSystemBoundaryBox boundaryBox = (UseCaseSystemBoundaryBox) currentlySelectedElement;
                boundaryBox.setInitialPoint(new Point(boundaryBox.getInitialPoint().getX() + deltaX, boundaryBox.getInitialPoint().getY() + deltaY));
            }
            initialPoint = currentPoint;
            reDrawCanvas();
        }
    }

    @FXML
    private void handleMouseRelease(MouseEvent event) {
        Point releasePoint = trackMouseCoordinates(event);
        if (activeTool != null && initialPoint != null) {
            if ("Actor".equals(activeTool)) {
                drawActor(initialPoint);
            } else if ("Exclude".equals(activeTool)) {
                drawExclude(initialPoint, releasePoint);
            } else if ("Include".equals(activeTool)) {
                drawInclude(initialPoint, releasePoint);
            } else if ("UseCase".equals(activeTool)) {
                drawUseCase(initialPoint);
            } else if ("UseCaseAssociation".equals(activeTool)) {
                drawAssociation(initialPoint, releasePoint);
            } else if ("BoundaryBox".equals(activeTool)) {
                drawBoundaryBox(initialPoint);
            }
        }
        initialPoint = null;
    }

    private void showDetailsIfSelected() {
        if (currentlySelectedElement instanceof UseCaseActor) {
            showActorDetails((UseCaseActor) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCase) {
            showUseCaseDetails((UseCase) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCaseSystemBoundaryBox) {
            showBoundaryBoxDetails((UseCaseSystemBoundaryBox) currentlySelectedElement);
        }
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

    public void clearCanvas() {
        canvasPane.getChildren().clear();
        actors.clear();
        useCases.clear();
        associations.clear();
        boundaryBoxes.clear();
        includeRelations.clear();
        elementMap.clear();
        excludeRelations.clear();
    }

    public void reDrawCanvas() {
        List<UseCaseActor> actorsCopy = new ArrayList<>(actors);
        List<UseCase> useCasesCopy = new ArrayList<>(useCases);
        List<UseCaseSystemBoundaryBox> boundaryBoxesCopy = new ArrayList<>(boundaryBoxes);
        List<UseCaseAssociation> associationsCopy = new ArrayList<>(associations);
        List<DependencyRelationship> includeRelationsCopy = new ArrayList<>(includeRelations);
        List<DependencyRelationship> excludeRelationsCopy = new ArrayList<>(excludeRelations);
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
        for (UseCaseAssociation association : associationsCopy) {
            reDrawAssociation(association);
        }
        for (DependencyRelationship relationship : includeRelationsCopy) {
            reDrawExclude(relationship);
        }
        for (DependencyRelationship relationship : excludeRelationsCopy) {
            reDrawExclude(relationship);
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
    //--------------------------------------

    public void showBoundaryBoxDetails(UseCaseSystemBoundaryBox box) {
        System.out.println("box"+box.getName());
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
        UseCaseAssociation association = new UseCaseAssociation(initial, finalPoint, associatedUseCase, associatedActor);
        associations.add(association);
        drawLine(associatedActor.getInitial(), associatedUseCase.getInitialpoint());
    }

    private void reDrawAssociation(UseCaseAssociation association) {
        activeTool = null;
            if (findElementNearPoint(association.getActor().getInitial()) instanceof UseCaseActor ||
                    findElementNearPoint(association.getUseCase().getInitialpoint()) instanceof UseCaseActor) {
                associations.add(association);
                drawLine(association.getActor().getInitial(), association.getUseCase().getInitialpoint());
            }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void drawLine(Point x, Point y) {
        Line line = new Line();
        line.setStartX(x.getX());
        line.setStartY(x.getY()+55);
        line.setEndX(y.getX());
        line.setEndY(y.getY()+25);
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

        } else if (startUseCase == null) {
            showWarning("Use Case Not Found", "Initial Point Does Not Have Use Case");
            return;
        } else if (endUseCase == null) {
            showWarning("Use Case Found", "Final Point Does Not Have Use Case");
            return;
        } else {
            showWarning("Error", "No Use Case Found");
            return;
        }
        DependencyRelationship include = new DependencyRelationship(startUseCase, endUseCase, "include");
        includeRelations.add(include);
        drawDottedLineWithArrow(include.getStartUseCase().getInitialpoint(), include.getEndUseCase().getInitialpoint(),include.getDependencyType());
    }

    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase != null && endUseCase != null) {

        } else if (startUseCase == null) {
            showWarning("Use Case Not Found", "Initial Point Does Not Have Use Case");
            return;
        } else if (endUseCase == null) {
            showWarning("Use Case Found", "Final Point Does Not Have Use Case");
            return;
        } else {
            showWarning("Error", "No Use Case Found");
            return;
        }
        DependencyRelationship exclude = new DependencyRelationship(startUseCase, endUseCase, "exclude");
        excludeRelations.add(exclude);
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialpoint(), exclude.getEndUseCase().getInitialpoint(),exclude.getDependencyType());
    }

    private void reDrawExclude(DependencyRelationship exclude) {
        excludeRelations.add(exclude);
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialpoint(), exclude.getEndUseCase().getInitialpoint(),exclude.getDependencyType());
    }

    private void redrawInclude(DependencyRelationship include) {
        excludeRelations.add(include);
        drawDottedLineWithArrow(include.getStartUseCase().getInitialpoint(), include.getEndUseCase().getInitialpoint(),include.getDependencyType());
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
        UseCaseSystemBoundaryBox boundaryBox = new UseCaseSystemBoundaryBox(initial, 350.0, 300.0);
        Label label = new Label(boundaryBox.getName());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setLayoutX(initial.getX() + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(initial.getY() + 4);
        boundaryBoxes.add(boundaryBox);
        canvasPane.getChildren().addAll(rectangle, label);
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
        label.setLayoutX(box.getInitialPoint().getX() + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(box.getInitialPoint().getY() + 4);
        boundaryBoxes.add(box);
        canvasPane.getChildren().addAll(rectangle, label);
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
        StackPane actorPane = new StackPane();
        actorPane.setLayoutX(initial.getX());
        actorPane.setLayoutY(initial.getY());
        StackPane.setAlignment(svgImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(actorNameLabel, Pos.BOTTOM_CENTER);
        actorPane.getChildren().addAll(svgImageView, actorNameLabel);
        canvasPane.getChildren().add(actorPane);
        elementMap.put(actorPane, actor);
        actors.add(actor);
    }

    public void reDrawActor(UseCaseActor actor) {
        activeTool = null;
        double size = 50.0;
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
        StackPane actorPane = new StackPane();
        actorPane.setLayoutX(actor.getInitial().getX());
        actorPane.setLayoutY(actor.getInitial().getY());
        StackPane.setAlignment(svgImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(actorNameLabel, Pos.BOTTOM_CENTER);
        actorPane.getChildren().addAll(svgImageView, actorNameLabel);
        canvasPane.getChildren().add(actorPane);
        elementMap.put(actorPane, actor);
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
        useCases.add(useCase);
    }

    private void showUseCaseDetails(UseCase useCase) {
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
        layout.getChildren().addAll(nameLabel, nameField, submitButton);
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
    }


    public void drawDottedLineWithArrow(Point startPoint, Point endPoint, String name) {
        Line dottedLine = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        dottedLine.setStroke(Color.BLACK);
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        dottedLine.setStrokeWidth(2);
        double angle = Math.atan2(endPoint.getY() - startPoint.getY(), endPoint.getX() - startPoint.getX());
        double arrowLength = 10;
        double x1 = endPoint.getX() - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = endPoint.getY() - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = endPoint.getX() - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = endPoint.getY() - arrowLength * Math.sin(angle + Math.PI / 6);
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                endPoint.getX(), endPoint.getY(),
                x1, y1,
                x2, y2
        );
        arrowHead.setFill(Color.BLACK);
        double midX = (startPoint.getX() + endPoint.getX()) / 2;
        double midY = (startPoint.getY() + endPoint.getY()) / 2;
        Text text = new Text(midX, midY, "<<"+name+">>");
        text.setFill(Color.BLACK);
        text.getTransforms().add(new Rotate(Math.toDegrees(angle), midX, midY));
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        canvasPane.getChildren().addAll(dottedLine, arrowHead, text);
    }

    public void serializeUseCaseDiagram(){
        for (UseCase useCase : useCases) {
            useCase.serialize("C:\\Users\\ahmad\\IdeaProjects\\javaproject\\abc.txt");
        }
        for (UseCaseActor actor : actors) {
            actor.serializeUseCaseActor("C:\\Users\\ahmad\\IdeaProjects\\javaproject\\abc.txt");
        }
        for (UseCaseAssociation association : associations) {
            association.serialize("C:\\Users\\ahmad\\IdeaProjects\\javaproject\\abc.txt");
        }
        for (DependencyRelationship include: includeRelations){
            include.serializedependencyRelationship("C:\\Users\\ahmad\\IdeaProjects\\javaproject\\abc.txt");
        }
        for (DependencyRelationship exclude: excludeRelations){
            exclude.serializedependencyRelationship("C:\\Users\\ahmad\\IdeaProjects\\javaproject\\abc.txt");
        }

    }

    public void deSerializeUseCaseDiagram(){

    }

}