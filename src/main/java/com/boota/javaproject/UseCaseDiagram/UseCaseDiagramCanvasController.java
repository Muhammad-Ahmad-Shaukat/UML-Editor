package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;
import com.boota.javaproject.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Controller class for managing the operations and interactions on a Use Case Diagram canvas.
 * This class provides functionalities to handle user actions such as drawing, selecting,
 * and managing Use Case diagram elements like actors, Use Cases, associations, dependency
 * relationships, and boundary boxes. It also supports saving and uploading diagrams,
 * and redrawing the canvas upon changes.
 *
 * The controller interacts with the JavaFX UI components and processes events such as
 * mouse clicks, key presses, and drag actions to modify the diagram. Core functionalities
 * include creating, updating, and rendering graphical elements on the canvas pane.
 */
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

    /**
     * Initializes the canvas and sets up event listeners for mouse and keyboard interactions.
     * This method is executed after the associated FXML file has been loaded.
     *
     * The method performs the following actions:
     * - Creates a canvas with dimensions matching the `canvasPane`.
     * - Obtains the `GraphicsContext` for drawing on the canvas.
     * - Binds the canvas's width and height properties to those of the `canvasPane`.
     * - Adds the canvas to the `canvasPane`.
     * - Sets up event handlers for mouse and keyboard events, such as moving, pressing, releasing,
     *   clicking, and dragging the mouse, as well as key presses.
     * - Ensures the `canvasPane` can receive focus for keyboard interactions.
     */
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
        canvasPane.setFocusTraversable(true);
        canvasPane.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Tracks the X and Y coordinates of a mouse event and returns them as a Point object.
     *
     * @param event the MouseEvent containing the coordinates to be tracked; must not be null
     * @return a Point object representing the X and Y coordinates of the mouse event
     */
    private Point trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        return new Point(x, y);
    }

    /**
     * Handles mouse click events on the canvas for selecting, interacting, and managing elements,
     * lines, and associations in the use case diagram.
     *
     * The method differentiates between single-click and double-click actions and updates the
     * currently selected element, dependency, or association based on the click's location.
     * It also highlights or clears selections as necessary.
     *
     * @param event the MouseEvent triggered when the user clicks on the canvas.
     *              The event must not be null and should contain details about the click,
     *              such as the location and click count.
     */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        Point clickPoint = trackMouseCoordinates(event);
        if (activeTool != null) {
            return;
        }
        if (event.getClickCount() == 2) {
            for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                Line line = entry.getValue().getLine();
                if (isPointNearLine(clickPoint, line, 5.0)) {
                    if (currentlySelectedDependency != null) {
                        highlightDependency(currentlySelectedDependency, Color.BLACK);
                    }
                    currentlySelectedDependency = entry.getKey();
                    highlightDependency(currentlySelectedDependency, Color.BLUE);
                    return;
                }
            }
            Object selectedElement = findElementNearPoint(clickPoint);
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                showDetailsIfSelected();
                return;
            }
        }
        if (event.getClickCount() == 1) {
            Object selectedElement = findElementNearPoint(clickPoint);
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                if (currentlySelectedDependency != null) {
                    highlightDependency(currentlySelectedDependency, Color.BLACK);
                    currentlySelectedDependency = null;
                }
            } else {
                boolean associationSelected = false;
                for (Map.Entry<UseCaseAssociation, Line> entry : associationLines.entrySet()) {
                    if (isPointNearLine(clickPoint, entry.getValue(), 5.0)) {
                        if (currentlySelectedAssociation != null) {
                            highlightAssociation(currentlySelectedAssociation, Color.BLACK);
                        }
                        currentlySelectedAssociation = entry.getKey();
                        highlightAssociation(currentlySelectedAssociation, Color.BLUE);
                        associationSelected = true;
                        break;
                    }
                }
                if (!associationSelected) {
                    for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                        if (isPointNearLine(clickPoint, entry.getValue().getLine(), 5.0)) {
                            if (currentlySelectedDependency != null) {
                                highlightDependency(currentlySelectedDependency, Color.BLACK);
                            }
                            currentlySelectedDependency = entry.getKey();
                            highlightDependency(currentlySelectedDependency, Color.BLUE);
                            return;
                        }
                    }
                }
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
        canvasPane.requestFocus();
    }

    /**
     * Handles a key press event, specifically to remove selected dependencies or associations
     * in a use case diagram when the DELETE key is pressed. The method performs the following actions:
     * - Deletes the currently selected dependency and its associated components (line, arrowhead, text)
     *   from the diagram and updates related data structures.
     * - Removes any associated relationships between the start and end use cases of the dependency.
     * - Deletes the currently selected association and its graphical representation.
     *
     * @param event the KeyEvent triggered by pressing a key; must not be null.
     *              The method primarily handles the DELETE key to remove elements.
     */
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            if (currentlySelectedDependency != null) {
                DottedLineComponents components = dottedLineComponentsMap.get(currentlySelectedDependency);
                if (components != null) {
                    canvasPane.getChildren().removeAll(
                            components.getLine(),
                            components.getArrowHead(),
                            components.getText()
                    );
                    dottedLineComponentsMap.remove(currentlySelectedDependency);
                    if ("include".equals(currentlySelectedDependency.getDependencyType())) {
                        includeRelations.remove(currentlySelectedDependency);
                    } else if ("exclude".equals(currentlySelectedDependency.getDependencyType())) {
                        excludeRelations.remove(currentlySelectedDependency);
                    }
                    UseCase startUseCase = currentlySelectedDependency.getStartUseCase();
                    UseCase endUseCase = currentlySelectedDependency.getEndUseCase();
                    if (startUseCase != null) {
                        startUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == endUseCase && rel == currentlySelectedDependency
                        );
                    }
                    if (endUseCase != null) {
                        endUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == startUseCase && rel == currentlySelectedDependency
                        );
                    }
                    currentlySelectedDependency = null;
                }
            }
            if (currentlySelectedAssociation != null) {
                Line line = associationLines.get(currentlySelectedAssociation);
                if (line != null) {
                    canvasPane.getChildren().remove(line);
                    associationLines.remove(currentlySelectedAssociation);
                    associations.remove(currentlySelectedAssociation);
                    currentlySelectedAssociation = null;
                }
            }
        }
    }

    /**
     * Checks if there is a UseCase located at or near the given point in the use case diagram.
     * If an element near the specified point is found and it is an instance of UseCase, it returns
     * that UseCase; otherwise, it returns null.
     *
     * @param point the Point object representing the location to check for a UseCase; must not be null
     * @return the UseCase located at or near the specified point if found, otherwise null
     */
    private UseCase checkUseCaseOnPoint(Point point) {
        Object object = findElementNearPoint(point);
        return object instanceof UseCase ? (UseCase) object : null;
    }

    /**
     * Draws a dotted line with an arrowhead connecting two points, with a label in the middle
     * representing the specified dependency relationship. This method adds the line, arrowhead,
     * and label to the canvas and tracks the components in a map for future reference or updates.
     *
     * @param startPoint the starting Point of the line; must not be null
     * @param endPoint the ending Point of the line; must not be null
     * @param name the descriptive name of the dependency relationship to be displayed as a label; must not be null
     * @param relationship the DependencyRelationship object representing the association between elements; must not be null
     */
    public void drawDottedLineWithArrow(Point startPoint, Point endPoint, String name, DependencyRelationship relationship) {
        Point[] optimalPoints = findClosestPoints(startPoint, endPoint);
        Point optimalStart = optimalPoints[0];
        Point optimalEnd = optimalPoints[1];
        Line dottedLine = new Line(optimalStart.getX(), optimalStart.getY(), optimalEnd.getX(), optimalEnd.getY());
        dottedLine.setStroke(Color.BLACK);
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        dottedLine.setStrokeWidth(2);
        double angle = Math.atan2(optimalEnd.getY() - optimalStart.getY(), optimalEnd.getX() - optimalStart.getX());
        double arrowLength = 10;
        double x1 = optimalEnd.getX() - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = optimalEnd.getY() - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = optimalEnd.getX() - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = optimalEnd.getY() - arrowLength * Math.sin(angle + Math.PI / 6);
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                optimalEnd.getX(), optimalEnd.getY(),
                x1, y1,
                x2, y2
        );
        arrowHead.setFill(Color.BLACK);
        double midX = ((optimalStart.getX() + optimalEnd.getX()) / 2) - 1;
        double midY = ((optimalStart.getY() + optimalEnd.getY()) / 2) + 1;
        Text text = new Text(midX, midY, "<<" + name + ">>");
        text.setFill(Color.BLACK);
        text.getTransforms().add(new Rotate(Math.toDegrees(angle), midX, midY));
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        canvasPane.getChildren().addAll(dottedLine, arrowHead, text);
        dottedLineComponentsMap.put(relationship, new DottedLineComponents(dottedLine, text, arrowHead));
    }
    /**
     * Finds the pair of closest points by generating variations of the input points
     * and calculating the shortest distance between all combinations of the variants.
     * Returns the optimal pair of points that have the minimal distance.
     *
     * @param p1 the first point from which to generate variations; must not be null
     * @param p2 the second point from which to generate variations; must not be null
     * @return an array containing two points, representing the closest pair of points
     */
    private Point[] findClosestPoints(Point p1, Point p2) {
        List<Point> p1Variants = new ArrayList<>();
        p1Variants.add(new Point(p1.getX() + 50, p1.getY() + 10));
        p1Variants.add(new Point(p1.getX() + 50, p1.getY() + 60));
        p1Variants.add(new Point(p1.getX(), p1.getY() + 40));
        p1Variants.add(new Point(p1.getX() + 100, p1.getY() + 40));
        List<Point> p2Variants = new ArrayList<>();
        p2Variants.add(new Point(p2.getX() + 50, p2.getY() + 10));
        p2Variants.add(new Point(p2.getX() + 50, p2.getY() + 60));
        p2Variants.add(new Point(p2.getX(), p2.getY() + 40));
        p2Variants.add(new Point(p2.getX() + 100, p2.getY() + 40));
        double shortestDistance = Double.MAX_VALUE;
        Point optimalP1 = null;
        Point optimalP2 = null;
        for (Point variant1 : p1Variants) {
            for (Point variant2 : p2Variants) {
                double distance = calculateDistance(variant1, variant2);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    optimalP1 = variant1;
                    optimalP2 = variant2;
                }
            }
        }
        return new Point[]{optimalP1, optimalP2};
    }

    /**
     * Draws an "include" dependency relationship between two UseCase elements
     * in the use case diagram, represented by the provided initial and final points.
     * The method verifies if both points correspond to valid UseCase elements
     * and checks if a dependency relationship already exists between them.
     * If eligible, it adds the relationship to the diagram and visually represents it
     * by drawing a dotted line with an arrow and a label.
     *
     * @param initial the starting Point in the diagram where the "include" dependency begins; must not be null
     * @param finalPoint the ending Point in the diagram where the "include" dependency ends; must not be null
     */
    public void drawInclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }
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

    /**
     * Draws an "exclude" dependency relationship between two UseCase elements
     * in the use case diagram, represented by the provided initial and final points.
     * The method verifies if both points correspond to valid UseCase elements
     * and checks if an exclude dependency relationship already exists between them.
     * If eligible, it adds the relationship to the diagram and visually represents it
     * by drawing a dotted line with an arrow.
     *
     * @param initial the starting Point location to check for a UseCase; must not be null
     * @param finalPoint the ending Point location to check for a UseCase; must not be null
     */
    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }
        if (startUseCase.hasAnyRelationshipWith(endUseCase)) {
            showWarning("Error", "A dependency relationship already exists between these Use Cases");
            return;
        }
        DependencyRelationship exclude = new DependencyRelationship(startUseCase, endUseCase, "exclude");
        excludeRelations.add(exclude);
        startUseCase.addAssociatedRelationship(exclude);
        endUseCase.addAssociatedRelationship(exclude);
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialPoint(), exclude.getEndUseCase().getInitialPoint(), exclude.getDependencyType(), exclude
        );
    }

    /**
     * Redraws the "exclude" dependency relationship between two UseCase elements in the diagram.
     * This method verifies the existence of valid start and end UseCase elements at the specified points
     * and renders the dependency relationship by drawing a dotted line with an arrow and updating relevant data structures.
     *
     * @param exclude the DependencyRelationship representing the "exclude" dependency to be redrawn; must not be null
     */
    private void reDrawExclude(DependencyRelationship exclude) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(exclude.getStartUseCase().getInitialPoint());
        UseCase endUseCase = checkUseCaseOnPoint(exclude.getEndUseCase().getInitialPoint());
        if (startUseCase == null || endUseCase == null) {
            return;
        }
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialPoint(), exclude.getEndUseCase().getInitialPoint(), exclude.getDependencyType(), exclude);
        excludeRelations.add(exclude);
    }

    /**
     * Redraws an "include" dependency relationship between two UseCase elements in the diagram.
     * This method validates the existence of valid start and end UseCase elements at their respective
     * initial points. If both UseCase elements are valid, the method visually represents the relationship
     * by drawing a dotted line with an arrow and updates the relevant data structures for tracking the
     * relationship.
     *
     * @param include the DependencyRelationship object representing the "include" dependency to be redrawn; must not be null
     */
    private void redrawInclude(DependencyRelationship include) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(include.getStartUseCase().getInitialPoint());
        UseCase endUseCase = checkUseCaseOnPoint(include.getEndUseCase().getInitialPoint());
        if (startUseCase == null || endUseCase == null) {
            return;
        }
        drawDottedLineWithArrow(include.getStartUseCase().getInitialPoint(),include.getEndUseCase().getInitialPoint(), include.getDependencyType(),include);
        includeRelations.add(include);
    }

    /**
     * Highlights the specified dependency relationship by updating its graphical components
     * (line, arrowhead, and text) with the provided color.
     *
     * @param dependency the DependencyRelationship to be highlighted; must not be null
     * @param color the Color to apply to the dependency's graphical components; must not be null
     */
    private void highlightDependency(DependencyRelationship dependency, Color color) {
        DottedLineComponents components = dottedLineComponentsMap.get(dependency);
        if (components != null) {
            components.getLine().setStroke(color);
            components.getArrowHead().setFill(color);
            components.getText().setFill(color);
        }
    }

    /**
     * Draws an association (link) between a Use Case and an Actor or vice versa
     * on a diagram canvas based on the given initial and final points. The method
     * ensures that valid associations are created only between an Actor and a
     * Use Case, and handles scenarios where objects at the specified points are
     * not valid for association.
     *
     * @param initial The initial point where the association starts.
     * @param finalPoint The final point where the association ends.
     */
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
        Point actorInitial = association.getActor().getInitial();
        Point useCaseInitial = association.getUseCase().getInitialPoint();
        Point actorPoint_y55 = new Point(actorInitial.getX(), actorInitial.getY() + 55);
        Point actorPoint_x40_y55 = new Point(actorInitial.getX() + 50, actorInitial.getY() + 55);
        Point useCasePoint_y55 = new Point(useCaseInitial.getX(), useCaseInitial.getY() + 35);
        Point useCasePoint_x40_y55 = new Point(useCaseInitial.getX() + 100, useCaseInitial.getY() + 35);
        double distance1 = calculateDistance(actorPoint_y55, useCasePoint_y55);
        double distance2 = calculateDistance(actorPoint_x40_y55, useCasePoint_y55);
        double distance3 = calculateDistance(actorPoint_x40_y55, useCasePoint_x40_y55);
        double distance4 = calculateDistance(actorPoint_y55, useCasePoint_x40_y55);
        Point start = actorPoint_y55;
        Point end = useCasePoint_y55;
        if (distance2 < distance1 && distance2 <= distance3 && distance2 <= distance4) {
            start = actorPoint_x40_y55;
            end = useCasePoint_y55;
        } else if (distance3 < distance1 && distance3 <= distance2 && distance3 <= distance4) {
            start = actorPoint_x40_y55;
            end = useCasePoint_x40_y55;
        } else if (distance4 < distance1 && distance4 <= distance2 && distance4 <= distance3) {
            start = actorPoint_y55;
            end = useCasePoint_x40_y55;
        }
        Line line = new Line();
        line.setStartX(start.getX());
        line.setStartY(start.getY());
        line.setEndX(end.getX());
        line.setEndY(end.getY());
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        canvasPane.getChildren().add(line);
        associationLines.put(association, line);
        associations.add(association);
    }

    /**
     * Calculates the Euclidean distance between two points in a 2D space.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance between the two points
     */
    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }


    /**
     * Redraws the specified association between a UseCaseActor and a UseCase
     * by calculating the shortest path and adding a visual representation of the association
     * in the form of a line to the canvas pane.
     *
     * @param association the UseCaseAssociation object representing the association
     *                     between a UseCaseActor and a UseCase to be redrawn
     */
    private void reDrawAssociation(UseCaseAssociation association) {
        activeTool = null;
        if (findElementNearPoint(association.getActor().getInitial()) instanceof UseCaseActor &&
                findElementNearPoint(association.getUseCase().getInitialPoint()) instanceof UseCase) {
            Point actorInitial = association.getActor().getInitial();
            Point useCaseInitial = association.getUseCase().getInitialPoint();
            Point actorPoint_y55 = new Point(actorInitial.getX(), actorInitial.getY() + 55);
            Point actorPoint_x40_y55 = new Point(actorInitial.getX() + 50, actorInitial.getY() + 55);
            Point useCasePoint_y55 = new Point(useCaseInitial.getX(), useCaseInitial.getY() + 35);
            Point useCasePoint_x40_y55 = new Point(useCaseInitial.getX() + 100, useCaseInitial.getY() + 35);
            double distance1 = calculateDistance(actorPoint_y55, useCasePoint_y55);
            double distance2 = calculateDistance(actorPoint_x40_y55, useCasePoint_y55);
            double distance3 = calculateDistance(actorPoint_x40_y55, useCasePoint_x40_y55);
            double distance4 = calculateDistance(actorPoint_y55, useCasePoint_x40_y55);
            Point start = actorPoint_y55;
            Point end = useCasePoint_y55;
            if (distance2 < distance1 && distance2 <= distance3 && distance2 <= distance4) {
                start = actorPoint_x40_y55;
                end = useCasePoint_y55;
            } else if (distance3 < distance1 && distance3 <= distance2 && distance3 <= distance4) {
                start = actorPoint_x40_y55;
                end = useCasePoint_x40_y55;
            } else if (distance4 < distance1 && distance4 <= distance2 && distance4 <= distance3) {
                start = actorPoint_y55;
                end = useCasePoint_x40_y55;
            }
            Line line = new Line();
            line.setStartX(start.getX());
            line.setStartY(start.getY());
            line.setEndX(end.getX());
            line.setEndY(end.getY());
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            canvasPane.getChildren().add(line);
            associationLines.put(association, line);
            if (!associations.contains(association)) {
                associations.add(association);
            }
        }
    }

    /**
     * Displays a warning dialog with the specified title and message.
     *
     * @param title   the title of the warning dialog
     * @param message the message to be displayed in the warning dialog
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Highlights the specified association by changing the stroke color of its corresponding line.
     *
     * @param association the use case association to be highlighted
     * @param color the color to set as the stroke for the association line
     */
    private void highlightAssociation(UseCaseAssociation association, Color color) {
        Line line = associationLines.get(association);
        if (line != null) {
            line.setStroke(color);
        }
    }

    /**
     * Handles a mouse press event and tracks the initial mouse coordinates.
     *
     * @param event the MouseEvent that triggers this method, providing details about the mouse press event
     */
    @FXML
    private void handleMousePress(MouseEvent event) {
        initialPoint = trackMouseCoordinates(event);
    }

    /**
     * Handles the dragging of the mouse event to update the position
     * of the currently selected diagram element.
     *
     * @param event the mouse event that triggers the drag operation
     */
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

    /**
     * Handles the mouse release event and executes the appropriate drawing operation
     * based on the active tool and the initial mouse click point.
     *
     * @param event the MouseEvent triggered when the mouse is released
     */
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

    /**
     * Displays the detailed information of the currently selected element if it is a recognized type.
     *
     * The method checks the type of the currentlySelectedElement and delegates the logic to the appropriate
     * method to display details based on the specific type of the selected object. Currently, it supports
     * UseCaseActor, UseCase, and UseCaseSystemBoundaryBox types. If the currentlySelectedElement does not
     * match any of these types, no action is performed.
     *
     * Preconditions:
     * - `currentlySelectedElement` must be non-null to attempt any operation.
     *
     * Postconditions:
     * - Details for the selected element will be displayed if it is of a supported type.
     */
    private void showDetailsIfSelected() {
        if (currentlySelectedElement instanceof UseCaseActor) {
            showActorDetails((UseCaseActor) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCase) {
            showUseCaseDetails((UseCase) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCaseSystemBoundaryBox) {
            showBoundaryBoxDetails((UseCaseSystemBoundaryBox) currentlySelectedElement);
        }
    }

    /**
     * Handles the action triggered when an actor is clicked in the UI.
     *
     * This method updates the currently active tool to "Actor", enabling
     * functionalities related to actor selection or manipulation within the application.
     */
    public void handleActorClick() {
        activeTool = "Actor";
    }

    /**
     * Handles the action when the "Boundary Box" is clicked.
     *
     * This method sets the active tool to "BoundaryBox", indicating that
     * the user has selected the Boundary Box tool for the application.
     * It updates the application state accordingly.
     */
    public void handleBoundaryBoxClick() {
        activeTool = "BoundaryBox";
    }

    /**
     * Handles the action to be performed when an association is clicked.
     * This method sets the active tool to "UseCaseAssociation".
     */
    public void handleAssociationClick() {
        activeTool = "UseCaseAssociation";
    }

    /**
     * Handles the action for when the "Include" tool is clicked.
     * This method sets the currently active tool to "Include".
     */
    public void handleIncludeClick() {
        activeTool = "Include";
    }

    /**
     * Handles the action for when the exclude tool is selected.
     * This method sets the activeTool variable to "Exclude",
     * indicating that the exclude functionality is now active.
     */
    public void handleExcludeClick() {
        activeTool = "Exclude";
    }

    /**
     * Handles the action when a use case is selected or clicked.
     * This method sets the active tool to "UseCase".
     */
    public void handleUseCaseClick() {
        activeTool = "UseCase";
    }

    /**
     * Clears all elements and associations from the canvas and resets internal data structures.
     * This method removes all graphical components from the canvas pane,
     * clears all collections managing actors, use cases, associations,
     * boundary boxes, include and exclude relations, and dotted line components.
     * It also resets all currently selected dependencies and associations to null.
     */
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

    /**
     * Refreshes and redraws all elements on the canvas.
     * This method ensures the canvas is cleared and all its components,
     * including actors, use cases, boundary boxes, associations, and relationships,
     * are redrawn based on their current state.
     *
     * The method makes internal copies of the elements to shield against
     * modifications during the redrawing process. After clearing the canvas,
     * it iteratively redraws each component, maintaining the expected visual state.
     */
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

    /**
     * Captures a snapshot of the current graphical canvas and saves it as an image.
     * The method checks the parent node of the canvas and handles snapshots based on
     * whether the parent is a {@code Group} or a {@code Pane}.
     * It validates canvas dimensions before proceeding with the snapshot operation
     * and generates warnings for invalid scenarios or unexpected errors.
     *
     * Operational Steps:
     * - Sets the active tool to null to ensure no interaction during the snapshot process.
     * - Validates the canvas dimensions to ensure they are greater than zero.
     * - Depending on the parent node type:
     *   - If the parent is of type {@code Group}, it captures a snapshot of the {@code Group}.
     *   - If the parent is of type {@code Pane}, it captures a snapshot of the {@code Pane}.
     * - Saves the captured snapshot as an image file.
     * - Displays appropriate warning messages in the following cases:
     *   - If canvas dimensions are invalid.
     *   - If the parent node is neither {@code Group} nor {@code Pane}.
     *   - If an unexpected error occurs during the operation.
     */
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

    /**
     * Saves the given writable image to a file selected by the user through a file chooser dialog.
     * If the user provides a valid file name, the image will be saved in PNG format.
     * Displays an informational alert upon successful save or a warning if no file is selected.
     *
     * @param writableImage the WritableImage object that represents the image to be saved.
     * @throws IOException if an error occurs while writing the image to the specified file.
     */
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

    /**
     * Displays the details of the provided boundary box by printing its name.
     *
     * @param box the UseCaseSystemBoundaryBox object whose details are to be displayed
     */
    public void showBoundaryBoxDetails(UseCaseSystemBoundaryBox box) {
        System.out.println("box"+box.getName());
    }

    /**
     * Draws a boundary box on the canvas with a specified starting point.
     *
     * @param initial the initial point where the boundary box will be drawn;
     *                it specifies the top-left corner of the boundary box.
     */
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

    /**
     * Redraws the boundary box representing a system use case on the canvas.
     * It resets the currently active tool, updates the graphical elements for the box
     * with specified dimensions, and labels it with the name of the use case.
     *
     * @param box the UseCaseSystemBoundaryBox instance containing the coordinates,
     *            dimensions, and name of the boundary box to be drawn.
     */
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

    /**
     * Finds and returns an element associated with a UI component that contains the specified point.
     * The method checks if the provided point is within the bounds of VBox or StackPane instances
     * in the elementMap.
     *
     * @param point the Point object representing the x and y coordinates to search near.
     * @return the associated element if a matching VBox or StackPane containing the specified point is found,
     *         otherwise returns null.
     */
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


    /**
     * Draws an actor representation (including its visual and label) onto a canvas at the specified initial position.
     *
     * @param initial The initial position where the actor should be placed on the canvas. It is represented as a Point object with X and Y coordinates.
     */
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

    /**
     * Renders a given use case actor by creating a visual representation on the canvas. This includes
     * loading an SVG image, converting it to a PNG file for display, adding the actor's name as a label,
     * and positioning the graphical components on the user interface.
     *
     * @param actor The {@code UseCaseActor} instance representing the actor to be redrawn. Contains
     *              properties such as the name and initial position of the actor, which are used to
     *              place and display it.
     */
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


    /**
     * Draws a use case diagram on the canvas at the specified initial point.
     * This method creates a visual representation of a use case, including
     * an ellipse and a label, and adds it to the canvas.
     *
     * @param initial The initial point where the use case diagram will be drawn.
     */
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
        text.setFill(Color.BLACK);
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

    /**
     * Re-draws a specified use case on the canvas by creating a visual representation
     * of the use case as an ellipse with text inside, and adding it to the canvas pane.
     * Also handles dynamic resizing of the ellipse based on the text's dimensions.
     *
     * @param useCase The use case object to be drawn. Contains initial position
     *                and name information used to render the element.
     */
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
        text.setFill(Color.BLACK);
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

    /**
     * Displays a detailed view for a specified UseCase.
     * Allows the user to update the name of the UseCase or delete it.
     *
     * @param useCase the UseCase object whose details are to be displayed
     */
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
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
           useCases.remove(useCase);
           elementMap.remove(useCase);
           stage.close();
           reDrawCanvas();
        });
        layout.getChildren().addAll(nameLabel, nameField, submitButton,deleteButton);
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays a dialog to show and edit details of a given UseCaseActor.
     * The dialog allows the user to update the actor's name or delete the actor.
     * After performing these operations, the canvas is redrawn to reflect the changes.
     *
     * @param actor the UseCaseActor whose details are to be displayed and edited; must not be null
     */
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
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            actors.remove(actor);
            elementMap.remove(actor);
            stage.close();
            reDrawCanvas();
        });
        layout.getChildren().addAll(nameLabel, nameField, submitButton,deleteButton);
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the process of uploading a file through a file chooser dialog.
     * <ul>
     *   Handles files with a specific ".bota" extension, applying the necessary filter in the dialog.
     *   Opens a file chooser dialog, allowing the user to select a file to upload.
     *   Verifies the selected file has a ".bota" extension.
     *   Deserializes the selected file to populate use case diagram elements including use cases,
     *   actors, associations, include relations, exclude relations, and boundary boxes.
     *   Re-draws the canvas to reflect the deserialized data.
     *   Displays a warning dialog in case the selected file is invalid or of unsupported format.
     *   Handles exceptions related to file reading or deserialization, logging stack traces if errors occur.
     * </ul>
     */
    public void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BOTA files (*.bota)", "*.bota");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            if (file.getName().endsWith(".bota")) {
                try {
                    UseCaseDiagramDeserializer.deserializeUseCaseDiagram(
                            file.getAbsolutePath(),
                            useCases, actors, associations, includeRelations, excludeRelations, boundaryBoxes
                    );
                    reDrawCanvas();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                showWarning("Invalid File","Please select a .bota file");
            }
        }
    }

    /**
     * Handles the save operation for a specific file format.
     * This method opens a file chooser dialog to allow the user to select
     * a location and name for saving the file. It ensures the file has
     * the proper extension and serializes the use case diagram data to
     * the specified file.
     *
     * It uses the FileChooser class for generating a user interface for
     * file selection and delegates the serialization process to the
     * UseCaseDiagramSerializer.
     *
     * File is saved with a ".bota" extension if not provided explicitly.
     * If any IO exception occurs during the save operation, it will be
     * printed to the error stream.
     *
     * Preconditions:
     * - The required data for serialization (e.g., useCases, actors,
     *   associations, includeRelations, excludeRelations, and boundaryBoxes)
     *   are assumed to be initialized and available for processing.
     *
     * Postconditions:
     * - The data will be written to the selected file in the specified format,
     *   or no action is performed if the save dialog is canceled.
     */
    public void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("noob.bota");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BOTA files (*.bota)", "*.bota");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (!file.getName().endsWith(".bota")) {
                file = new File(file.getAbsolutePath() + ".bota");
            }
            try {
                UseCaseDiagramSerializer.serializeUseCaseDiagram(
                        useCases, actors, associations, includeRelations, excludeRelations, boundaryBoxes,file.getAbsolutePath()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determines whether a given point is near a specified line within a given tolerance.
     *
     * @param point the point to check
     * @param line the line against which the proximity of the point is being checked
     * @param tolerance the maximum allowable distance from the line for the point to be considered near
     * @return true if the point is near the line within the specified tolerance and within the line bounds; false otherwise
     */
    private boolean isPointNearLine(Point point, Line line, double tolerance) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();
        double px = point.getX();
        double py = point.getY();
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double area = Math.abs((px - x1) * (y2 - y1) - (py - y1) * (x2 - x1));
        double distance = area / lineLength;
        boolean withinBounds = (px >= Math.min(x1, x2) && px <= Math.max(x1, x2)) &&
                (py >= Math.min(y1, y2) && py <= Math.max(y1, y2));
        return distance <= tolerance && withinBounds;
    }

    /**
     * Handles the action event to return to the main screen.
     * Loads the "MainCanvas.fxml" layout and sets it as the scene for a new stage.
     * Closes the current primary stage after opening the new stage.
     *
     * @param event The ActionEvent triggered by the user interaction that calls this method.
     * @throws IOException If an input or output exception occurs while loading the FXML resource.
     */
    @FXML
    private void returntomain(ActionEvent event) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/boota/javaproject/MainCanvas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        newStage.setTitle("xyz!");
        newStage.setScene(scene);
        newStage.show();
        Main.primaryStage.close();
    }

}