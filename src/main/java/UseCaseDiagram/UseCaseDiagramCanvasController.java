package UseCaseDiagram;

import ClassDiagram.Point;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCaseDiagramCanvasController {

    @FXML
    private Pane canvasPane;
    private Canvas canvas;
    private GraphicsContext gc;

    List<UseCase> useCases = new ArrayList<>();
    List<UseCaseAssociation> associations = new ArrayList<>();
    List<UseCaseActor> actors = new ArrayList<>();
    List<DependencyRelationship> includeRelations = new ArrayList<>();
    List<DependencyRelationship> excludeRelations = new ArrayList<>();
    List<UseCaseSystemBoundaryBox> boundaryBoxes = new ArrayList<>();
    private Map<UseCaseAssociation, Line> associationLines = new HashMap<>();
    private UseCaseAssociation currentlySelectedAssociation = null;
    private Map<DependencyRelationship, DottedLineComponents> dottedLineComponentsMap = new HashMap<>();
    private DependencyRelationship currentlySelectedDependency = null;

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

        // Set event listeners for mouse and keyboard
        canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
        canvasPane.setOnMousePressed(this::handleMousePress);
        canvasPane.setOnMouseReleased(this::handleMouseRelease);
        canvasPane.setOnMouseClicked(this::handleMouseClick);
        canvasPane.setOnMouseDragged(this::handleMouseDrag);

        // Set the canvasPane to be focusable and register key press handler
        canvasPane.setFocusTraversable(true); // Make canvasPane focusable
        canvasPane.setOnKeyPressed(this::handleKeyPress); // Register key press handler
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
            return; // Ignore clicks when a tool is active
        }

        // Double-click: Handle element properties or highlight dependencies
        if (event.getClickCount() == 2) {
            // Check for dependency relationship near the click point
            for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                Line line = entry.getValue().getLine();
                if (isPointNearLine(clickPoint, line, 5.0)) { // Tolerance = 5.0
                    // Highlight the dependency relationship
                    if (currentlySelectedDependency != null) {
                        highlightDependency(currentlySelectedDependency, Color.BLACK); // Reset previous selection
                    }
                    currentlySelectedDependency = entry.getKey();
                    highlightDependency(currentlySelectedDependency, Color.BLUE);
                    return; // Dependency found, stop further checks
                }
            }

            // Check if a `UseCase` or `Actor` is clicked
            Object selectedElement = findElementNearPoint(clickPoint);
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                showDetailsIfSelected(); // Show the properties of the selected element
                return;
            }
        }

        // Single-click: Select or reset element/association/dependency
        if (event.getClickCount() == 1) {
            Object selectedElement = findElementNearPoint(clickPoint);

            if (selectedElement != null) {
                // Handle `UseCase` or `Actor` selection
                currentlySelectedElement = selectedElement;

                // Reset other selections
                if (currentlySelectedDependency != null) {
                    highlightDependency(currentlySelectedDependency, Color.BLACK);
                    currentlySelectedDependency = null;
                }
            } else {
                // Check if a line (association or dependency) is clicked
                boolean associationSelected = false;
                for (Map.Entry<UseCaseAssociation, Line> entry : associationLines.entrySet()) {
                    if (isPointNearLine(clickPoint, entry.getValue(), 5.0)) {
                        // Highlight the selected association
                        if (currentlySelectedAssociation != null) {
                            highlightAssociation(currentlySelectedAssociation, Color.BLACK);
                        }
                        currentlySelectedAssociation = entry.getKey();
                        highlightAssociation(currentlySelectedAssociation, Color.BLUE);
                        associationSelected = true;
                        break;
                    }
                }

                // If no association line is clicked, check dependency relationships
                if (!associationSelected) {
                    for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                        if (isPointNearLine(clickPoint, entry.getValue().getLine(), 5.0)) {
                            // Highlight the selected dependency
                            if (currentlySelectedDependency != null) {
                                highlightDependency(currentlySelectedDependency, Color.BLACK);
                            }
                            currentlySelectedDependency = entry.getKey();
                            highlightDependency(currentlySelectedDependency, Color.BLUE);
                            return;
                        }
                    }
                }

                // Reset selection if clicking elsewhere
                if (!associationSelected) {
                    if (currentlySelectedAssociation != null) {
                        highlightAssociation(currentlySelectedAssociation, Color.BLACK);
                        currentlySelectedAssociation = null;
                    }
                    if (currentlySelectedDependency != null) {
                        highlightDependency(currentlySelectedDependency, Color.BLACK);
                        currentlySelectedDependency = null;
                    }
                    currentlySelectedElement = null;
                }
            }
        }

        canvasPane.requestFocus(); // Ensure the canvas pane gets focus
    }


    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            // Handle Dependency Deletion
            if (currentlySelectedDependency != null) {
                DottedLineComponents components = dottedLineComponentsMap.get(currentlySelectedDependency);
                if (components != null) {
                    // Remove all components related to the dependency from the canvas
                    canvasPane.getChildren().removeAll(
                            components.getLine(),
                            components.getArrowHead(),
                            components.getText()
                    );

                    // Remove the dependency from the map
                    dottedLineComponentsMap.remove(currentlySelectedDependency);

                    // Remove the dependency from the corresponding collection
                    if ("include".equals(currentlySelectedDependency.getDependencyType())) {
                        includeRelations.remove(currentlySelectedDependency);
                    } else if ("exclude".equals(currentlySelectedDependency.getDependencyType())) {
                        excludeRelations.remove(currentlySelectedDependency);
                    }

                    // Remove the dependency from the associated relationships of both UseCases
                    UseCase startUseCase = currentlySelectedDependency.getStartUseCase();
                    UseCase endUseCase = currentlySelectedDependency.getEndUseCase();

                    if (startUseCase != null) {
                        // Remove the endUseCase from startUseCase's relationships
                        startUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == endUseCase && rel == currentlySelectedDependency
                        );
                    }

                    if (endUseCase != null) {
                        // Remove the startUseCase from endUseCase's relationships
                        endUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == startUseCase && rel == currentlySelectedDependency
                        );
                    }

                    // Reset the currently selected dependency
                    currentlySelectedDependency = null;
                }
            }


            // Handle Association Deletion
            if (currentlySelectedAssociation != null) {
                Line line = associationLines.get(currentlySelectedAssociation);
                if (line != null) {
                    // Remove the association line from the canvas
                    canvasPane.getChildren().remove(line);

                    // Remove the association from the map and collection
                    associationLines.remove(currentlySelectedAssociation);
                    associations.remove(currentlySelectedAssociation);

                    currentlySelectedAssociation = null; // Reset selection
                }
            }
        }
    }


    private UseCase checkUseCaseOnPoint(Point point) {
        Object object = findElementNearPoint(point);
        return object instanceof UseCase ? (UseCase) object : null;
    }

    public void drawDottedLineWithArrow(Point startPoint, Point endPoint, String name, DependencyRelationship relationship) {
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
        Text text = new Text(midX, midY, "<<" + name + ">>");
        text.setFill(Color.BLACK);
        text.getTransforms().add(new Rotate(Math.toDegrees(angle), midX, midY));
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add components to the canvas
        canvasPane.getChildren().addAll(dottedLine, arrowHead, text);

        // Track all components in the map
        dottedLineComponentsMap.put(relationship, new DottedLineComponents(dottedLine, text, arrowHead));
    }

    public void drawInclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);

        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }

        // Check if any relationship already exists between these Use Cases
        if (startUseCase.hasAnyRelationshipWith(endUseCase)) {
            showWarning("Error", "A dependency relationship already exists between these Use Cases");
            return;
        }

        DependencyRelationship include = new DependencyRelationship(startUseCase, endUseCase, "include");
        includeRelations.add(include);
        startUseCase.addAssociatedRelationship(include);
        endUseCase.addAssociatedRelationship(include);
        drawDottedLineWithArrow(
                include.getStartUseCase().getInitialPoint(),
                include.getEndUseCase().getInitialPoint(),
                include.getDependencyType(),
                include
        );
    }

    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);

        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }

        // Check if any relationship already exists between these Use Cases
        if (startUseCase.hasAnyRelationshipWith(endUseCase)) {
            showWarning("Error", "A dependency relationship already exists between these Use Cases");
            return;
        }

        DependencyRelationship exclude = new DependencyRelationship(startUseCase, endUseCase, "exclude");
        excludeRelations.add(exclude);
        startUseCase.addAssociatedRelationship(exclude);
        endUseCase.addAssociatedRelationship(exclude);
        drawDottedLineWithArrow(
                exclude.getStartUseCase().getInitialPoint(),
                exclude.getEndUseCase().getInitialPoint(),
                exclude.getDependencyType(),
                exclude
        );
    }

    private void reDrawExclude(DependencyRelationship exclude) {
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialPoint(), exclude.getEndUseCase().getInitialPoint(), exclude.getDependencyType(), exclude
        );
        excludeRelations.add(exclude);
    }

    private void redrawInclude(DependencyRelationship include) {
        drawDottedLineWithArrow(
                include.getStartUseCase().getInitialPoint(),
                include.getEndUseCase().getInitialPoint(),
                include.getDependencyType(),
                include
        );
        includeRelations.add(include);
    }

    private void highlightDependency(DependencyRelationship dependency, Color color) {
        DottedLineComponents components = dottedLineComponentsMap.get(dependency);
        if (components != null) {
            components.getLine().setStroke(color); // Highlight the line
            components.getArrowHead().setFill(color); // Highlight the arrowhead
            components.getText().setFill(color); // Highlight the text
        }
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
                return;
            }
        } else {
            showWarning("No Use Case Found", "No Actor or Use Case Found at Final Point");
            return;
        }

        UseCaseAssociation association = new UseCaseAssociation(initial, finalPoint, associatedUseCase, associatedActor);

        // Updated Line Drawing with Adjustments
        Line line = new Line();



        line.setStartX(association.getActor().getInitial().getX());
        line.setStartY(association.getActor().getInitial().getY() + 55);
        line.setEndX(association.getUseCase().getInitialPoint().getX());
        line.setEndY(association.getUseCase().getInitialPoint().getY() + 25);
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        canvasPane.getChildren().add(line);
        associationLines.put(association, line);
        associations.add(association);
    }


    private void reDrawAssociation(UseCaseAssociation association) {
        activeTool = null;
        if (findElementNearPoint(association.getActor().getInitial()) instanceof UseCaseActor &&
                findElementNearPoint(association.getUseCase().getInitialPoint()) instanceof UseCase) {
            Line line = new Line();
            line.setStartX(association.getActor().getInitial().getX());
            line.setStartY(association.getActor().getInitial().getY() + 55);
            line.setEndX(association.getUseCase().getInitialPoint().getX());
            line.setEndY(association.getUseCase().getInitialPoint().getY() + 25);
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            canvasPane.getChildren().add(line);
            associationLines.put(association, line);
            if (!associations.contains(association)) {
                associations.add(association);
            }
        }
    }



    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void highlightAssociation(UseCaseAssociation association, Color color) {
        Line line = associationLines.get(association); // Retrieve the line from the map
        if (line != null) {
            line.setStroke(color); // Change the color of the line
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
                useCase.setInitialPoint(new Point(useCase.getInitialPoint().getX() + deltaX, useCase.getInitialPoint().getY() + deltaY));
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
        dottedLineComponentsMap.clear();
        currentlySelectedDependency = null;
        currentlySelectedAssociation = null;
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
            redrawInclude(relationship);
        }
        for (DependencyRelationship relationship : excludeRelationsCopy) {
            reDrawExclude(relationship);
        }
    }

    public void handleSnapshot() {
        activeTool = null;
        try {
            Node parentNode = canvas.getParent();

            if (parentNode instanceof Group) {
                Group root = (Group) parentNode;
                double canvasWidth = canvas.getWidth();
                double canvasHeight = canvas.getHeight();

                if (canvasWidth <= 0 || canvasHeight <= 0) {
                    showWarning("Warning", "Canvas Dimension Error");
                    return;
                }

                // Capture the entire root (Group or Pane) instead of just the canvas
                WritableImage writableImage = new WritableImage((int) root.getBoundsInLocal().getWidth(),
                        (int) root.getBoundsInLocal().getHeight());
                root.snapshot(null, writableImage);

                saveImage(writableImage);
            } else if (parentNode instanceof Pane) {
                Pane root = (Pane) parentNode;
                double canvasWidth = canvas.getWidth();
                double canvasHeight = canvas.getHeight();

                if (canvasWidth <= 0 || canvasHeight <= 0) {
                    showWarning("Warning", "Canvas Dimension Error");
                    return;
                }

                // Capture the entire root (Pane) instead of just the canvas
                WritableImage writableImage = new WritableImage((int) root.getBoundsInLocal().getWidth(),
                        (int) root.getBoundsInLocal().getHeight());
                root.snapshot(null, writableImage);

                saveImage(writableImage);
            } else {
                showWarning("Unexpected Parent", "The parent is neither Group nor Pane.");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Unexpected Error", "Unable to save snapshot. Please try later.");
        }
    }

    private void saveImage(WritableImage writableImage) throws IOException {
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
    }



    //--------------------------------------

    public void showBoundaryBoxDetails(UseCaseSystemBoundaryBox box) {
        System.out.println("box"+box.getName());
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
        useCasePane.setLayoutX(useCase.getInitialPoint().getX());
        useCasePane.setLayoutY(useCase.getInitialPoint().getY());
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



    public void love(){
        clearCanvas();

    }

    public void deSerialize(UseCase useCase) {

    }

    public void SerializeUseCaseDiagram() throws IOException {
        try (FileOutputStream fs = new FileOutputStream("a.txt");
             ObjectOutputStream os = new ObjectOutputStream(fs)) {

            for (UseCase useCase : useCases) {
                os.writeObject(useCase);
            }
            for (UseCaseActor actor : actors) {
                os.writeObject(actor);
            }
            for (UseCaseAssociation a : associations) {
                os.writeObject(a);
            }
            for (DependencyRelationship a : includeRelations) {
                os.writeObject(a);
            }
            for (DependencyRelationship a : excludeRelations) {
                os.writeObject(a);
            }
            // Add other lists if needed, like boundaryBoxes
        }
    }


    public void DeserializeUseCaseDiagram() throws IOException, ClassNotFoundException {
        // Clear canvas
        clearCanvas();

        // Initialize the lists
        useCases = new ArrayList<>();
        actors = new ArrayList<>();
        associations = new ArrayList<>();
        includeRelations = new ArrayList<>();
        excludeRelations = new ArrayList<>();
        boundaryBoxes = new ArrayList<>();

        try (FileInputStream fs = new FileInputStream("a.txt");
             ObjectInputStream os = new ObjectInputStream(fs)) {

            // Read objects from the file
            while (true) {
                try {
                    Object obj = os.readObject();
                    if (obj instanceof UseCase) {
                        useCases.add((UseCase) obj);
                    } else if (obj instanceof UseCaseActor) {
                        actors.add((UseCaseActor) obj);
                    } else if (obj instanceof UseCaseAssociation) {
                        associations.add((UseCaseAssociation) obj);
                    } else if (obj instanceof DependencyRelationship) {
                        DependencyRelationship relationship = (DependencyRelationship) obj;
                        if ("include".equalsIgnoreCase(relationship.getDependencyType())) {
                            includeRelations.add(relationship);
                        } else if ("exclude".equalsIgnoreCase(relationship.getDependencyType())) {
                            excludeRelations.add(relationship);
                        }
                    } else if (obj instanceof UseCaseSystemBoundaryBox) {
                        boundaryBoxes.add((UseCaseSystemBoundaryBox) obj);
                    }
                } catch (EOFException e) {
                    break; // End of file reached
                }
            }
        }

        // Redraw canvas
        reDrawCanvas();
    }



    private UseCase findUseCaseByName(String name) {
        for (UseCase useCase : useCases) {
            if (useCase.getName().equals(name)) {
                return useCase;
            }
        }
        return null; // Or throw an exception if the reference is critical
    }

    private UseCaseActor findActorByName(String name) {
        for (UseCaseActor actor : actors) {
            if (actor.getName().equals(name)) {
                return actor;
            }
        }
        return null; // Or throw an exception if the reference is critical
    }


    private boolean isPointNearLine(Point point, Line line, double tolerance) {
        // Line start and end points
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();

        // Point coordinates
        double px = point.getX();
        double py = point.getY();

        // Compute the distance from the point to the line
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double area = Math.abs((px - x1) * (y2 - y1) - (py - y1) * (x2 - x1));
        double distance = area / lineLength;

        // Check if the point lies within tolerance and within line segment bounds
        boolean withinBounds = (px >= Math.min(x1, x2) && px <= Math.max(x1, x2)) &&
                (py >= Math.min(y1, y2) && py <= Math.max(y1, y2));
        return distance <= tolerance && withinBounds;
    }
}