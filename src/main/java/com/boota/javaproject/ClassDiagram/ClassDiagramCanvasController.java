package com.boota.javaproject.ClassDiagram;

import com.boota.javaproject.Main;
import com.boota.javaproject.SnapshotHelper;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
/**
 * The ClassDiagramCanvasController handles the user interface and interaction logic
 * for a class diagram canvas in a modeler application. It manages drawing, editing,
 * and interactions with classes, interfaces, and relationships on the canvas.
 *
 * Responsibilities include:
 * - Managing canvas elements such as classes, interfaces, and relationships.
 * - Handling mouse events such as dragging, clicking, and releasing for drawing and
 *   editing purposes.
 * - Implementing functionalities for creating and redrawing relationships
 *   (e.g., associations, generalizations, aggregations, compositions).
 * - Supporting serialization and deserialization of the class diagram.
 * - Updating user interface components to present class and interface details.
 * - Providing utility methods for managing alignment and interaction of elements.
 * - Facilitating saving and loading class diagram models.
 * - Generating code and managing export features based on the diagram contents.
 */
public class ClassDiagramCanvasController {

    @FXML
    private Pane canvasPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private final List<Class> classes = new ArrayList<>();
    private VBox selectedClassBox = null;
    private String activeTool = null;
    private final Map<String, BiConsumer<Double, Double>> drawActions = new HashMap<>();
    private final Map<Node, Object> elementMap = new HashMap<>();
    private final List<Interface> interfaces = new ArrayList<>();
    private Node selectedNode = null;
    private Point initialMousePosition = null;
    private Line tempLine = null;
    private Point initialPoint = null;
    private final List<Association> associations = new ArrayList<>();
    private final List<CompositeRelations> compositeRelations = new ArrayList<>();
    private final List<Generalization> generalizations = new ArrayList<>();
    private final Map<Line,Association> associationMap = new HashMap<>();
    private final Map<Line,CompositeRelations> compositeRelationMap = new HashMap<>();
    private com.boota.javaproject.ClassDiagram.ClassDiagramSerializer ClassDiagramSerializer;

    /**
     * Initializes the canvasPane and its associated Canvas, providing functionality for
     * dynamically adjusting the Canvas dimensions, handling user interactions, and linking
     * rendering logic.
     *
     * The method binds the width and height properties of the canvasPane to the Canvas,
     * ensuring that the Canvas automatically resizes whenever the canvasPane's dimensions
     * change. It also sets up event listeners for mouse events and initializes drawing actions
     * for creating diagram elements such as classes and interfaces.
     *
     * Key functionalities:
     * - Dynamically resizes the Canvas to match the canvasPane's dimensions.
     * - Initializes necessary components (GraphicsContext, Canvas).
     * - Binds mouse events (such as move, click, press, drag, and release) to appropriate handlers.
     * - Registers drawing actions for diagram elements (e.g., classes and interfaces).
     *
     * This method is called automatically by the JavaFX runtime during the FXML loading process.
     */
    @FXML
    public void initialize() {
        // Bind canvas width and height to canvasPane's width and height properties
        canvasPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (canvas == null) {
                canvas = new Canvas(newValue.doubleValue(), canvasPane.getHeight());
                gc = canvas.getGraphicsContext2D();
                canvasPane.getChildren().add(canvas);

                // Other initialization code
                drawActions.put("Class", this::drawClass);
                drawActions.put("Interface", this::drawInterface);
                canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
                canvasPane.setOnMouseClicked(this::handleCanvasClick);
                canvasPane.setOnMousePressed(this::handleMousePressed);
                canvasPane.setOnMouseDragged(this::handleMouseDragged);
                canvasPane.setOnMouseReleased(this::handleMouseReleased);
            } else {
                // Update canvas width if pane width changes
                canvas.setWidth(newValue.doubleValue());
            }
        });

        // Bind canvas height to canvasPane's height property
        canvasPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (canvas == null) {
                canvas = new Canvas(canvasPane.getWidth(), newValue.doubleValue());
                gc = canvas.getGraphicsContext2D();
                canvasPane.getChildren().add(canvas);
            } else {
                // Update canvas height if pane height changes
                canvas.setHeight(newValue.doubleValue());
            }
        });
    }



    /**
     * Handles the selection of the "Interface" button or tool in the class diagram editor.
     *
     * This method is triggered when the tool for adding or interacting with interfaces is activated
     * by the user. It sets the current active tool to "Interface," enabling the functionality
     * for creating or modifying interface elements within the diagram on the canvas.
     *
     * Key functionality:
     * - Updates the `activeTool` field to reflect the "Interface" mode.
     *
     * This method ensures that subsequent user actions on the canvas are interpreted as
     * operations related to interfaces.
     */
    public void interfacePressed(){
        activeTool = "Interface";
    }
    private void trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
    }

    /**
     * Handles the logic for when the mouse is pressed on the canvasPane.
     * Performs different actions based on the active tool or checks if
     * a node is selected within the bounds of the current mouse position.
     *
     * If the active tool represents a relationship (e.g., Association,
     * Aggregation, Composition, Generalization), it initializes a temporary
     * line for visual feedback. Otherwise, it identifies if a node is
     * selected based on the mouse position.
     *
     * @param event the MouseEvent captured when the mouse is pressed,
     *              used to retrieve positional data and trigger actions.
     */
    private void handleMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        if ("Association".equals(activeTool) || "Aggregation".equals(activeTool)
        || "Composition".equals(activeTool) || "Generalization".equals(activeTool)) {
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            canvasPane.getChildren().add(tempLine);
            return;
        }
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                selectedNode = node;
                initialMousePosition = new Point(x, y);
                return;
            }
        }
    }

    /**
     * Handles the logic for when the mouse is dragged on the canvas.
     *
     * This method updates the properties of elements being moved or resized based
     * on the current mouse position. If a temporary line is active, its end position
     * is adjusted during the drag event. If a node is selected, it adjusts its layout
     * on the canvas and updates its associated element's position accordingly.
     * The canvas is redrawn to reflect these changes.
     *
     * @param event the MouseEvent that captures the current position of the mouse
     *              during the drag event.
     */
    private void handleMouseDragged(MouseEvent event) {
        if (tempLine != null) {
            tempLine.setEndX(event.getX());
            tempLine.setEndY(event.getY());
            return;
        }
        if (selectedNode != null && initialMousePosition != null) {
            double deltaX = event.getX() - initialMousePosition.getX();
            double deltaY = event.getY() - initialMousePosition.getY();
            selectedNode.setLayoutX(selectedNode.getLayoutX() + deltaX);
            selectedNode.setLayoutY(selectedNode.getLayoutY() + deltaY);
            Object element = elementMap.get(selectedNode);
            if (element instanceof Class clazz) {
                clazz.getInitialPoint().setX(clazz.getInitialPoint().getX() + deltaX);
                clazz.getInitialPoint().setY(clazz.getInitialPoint().getY() + deltaY);
            }
            else if (element instanceof Interface clazz) {
                clazz.getInitialPoint().setX(clazz.getInitialPoint().getX() + deltaX);
                clazz.getInitialPoint().setY(clazz.getInitialPoint().getY() + deltaY);
            }
            reDrawCanvas();
            initialMousePosition.setX(event.getX());
            initialMousePosition.setY(event.getY());
        }
    }

    /**
     * Handles the logic executed when the mouse is released on the canvas, finalizing
     * actions related to the active tool or diagram element being created or modified.
     *
     * Depending on the active tool (e.g., Association, Aggregation, Composition,
     * Generalization), this method completes the creation of the respective relationship
     * between diagram elements. If a temporary line exists, it is removed before
     * finalizing the drawing of the intended relationship.
     *
     * Key functionality:
     * - Removes any temporary visual elements (e.g., lines used for previews).
     * - Completes and draws the intended relationship between points on the canvas.
     * - Resets selection and initial mouse position after the operation is complete.
     *
     * @param event the MouseEvent captured when the mouse is released on the canvas,
     *              used to retrieve the final position and trigger appropriate actions.
     */
    private void handleMouseReleased(MouseEvent event) {
        if (tempLine != null && Objects.equals(activeTool, "Association")) {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawAssociation(initialPoint, finalPoint);
        } else if (tempLine != null && Objects.equals(activeTool, "Aggregation")) {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawAggregation(initialPoint, finalPoint);
        }else if (tempLine != null && Objects.equals(activeTool, "Composition")) {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawComposition(initialPoint, finalPoint);
        } else if (tempLine != null && Objects.equals(activeTool, "Generalization")) {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawGeneralization(initialPoint, finalPoint);
        }
        selectedNode = null;
        initialMousePosition = null;
    }

    /**
     * Handles mouse click events on the canvas pane.
     *
     * This method determines the type of element or action associated with the
     * clicked position on the canvas and performs appropriate operations. It
     * supports interactions such as selecting or deselecting class boxes,
     * triggering details forms for associations, compositions, classes, and
     * interfaces, and activating drawing actions based on the selected tool.
     *
     * The method performs the following actions based on the location of the click:
     * - If the click is near a line representing an association or composite relation,
     *   it either shows a details form (on a double click) or handles no further action.
     * - If the click occurs outside the bounds of the selected class box, it deselects it.
     * - If the click is on a class or interface node, it either shows detailed
     *   information (on a double click) or selects the node.
     * - If a drawing tool is active, it executes the corresponding draw action.
     *
     * @param event the MouseEvent triggered upon clicking the canvas, containing
     *              information such as the click position, click count, and
     *              other metadata used for interaction handling.
     */
    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        for (Map.Entry<Line, Association> entry : associationMap.entrySet()) {
            Line line = entry.getKey();
            Association association = entry.getValue();
            if (isNearLine(line, x, y)) {
                if (event.getClickCount() == 2) {
                    showAssociationDetailsForm(association);
                    deselectClassBox();
                }
                return;
            }
        }
        for (Map.Entry<Line, CompositeRelations> entry : compositeRelationMap.entrySet()) {
            CompositeRelations relation = entry.getValue();
            if (isNearLine(entry.getKey(), x, y)) {
                    if (event.getClickCount() == 2) {
                        showAggregationDetailsForm(relation);
                    }
                return;
            }
        }
        if (selectedClassBox != null && !isWithinBounds(selectedClassBox, x, y)) {
            deselectClassBox();
        }
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                Object element = entry.getValue();
                if (element instanceof Class clazz) {
                    if (event.getClickCount() == 2) {
                        showClassDetails(clazz);
                    } else {
                        selectClassBox((VBox) node);
                    }
                    return;
                } else if (element instanceof Interface iface) {
                    if (event.getClickCount() == 2) {
                        showInterfaceDetails(iface);
                    } else {
                        selectClassBox((VBox) node);
                    }
                    return;
                }
            }
        }
        if (activeTool != null) {
            BiConsumer<Double, Double> drawAction = drawActions.get(activeTool);
            if (drawAction != null) {
                drawAction.accept(x, y);
            }
        }
    }

    /**
     * Handles the activation of the "Generalization" tool in the class diagram editor.
     *
     * This method is triggered when the "Generalization" tool is selected by the user,
     * enabling functionality related to creating generalization relationships between
     * diagram elements, such as classes or interfaces, on*/
    @FXML
    private void handleGeneralizationClick(){
        activeTool = "Generalization";
        deselectClassBox();
    }

    /**
     * Redraws the canvas by clearing the existing visual elements and re-rendering the
     * updated state of classes, interfaces, associations, composite relations, and generalizations.
     *
     * The method performs the following steps:
     * 1. Clears all elements currently rendered on the canvas.
     **/
    public void reDrawCanvas() {
        canvasPane.getChildren().clear();
        ArrayList<Association> tempAssociation = new ArrayList<>(associations);
        ArrayList<Class> tempClasses = new ArrayList<>(classes);
        ArrayList<Interface> tempInterfaces = new ArrayList<>(interfaces);
        ArrayList<CompositeRelations> tempAggregations = new ArrayList<>(compositeRelations);
        ArrayList<Generalization> tempGeneral = new ArrayList<>(generalizations);
        associationMap.clear();
        classes.clear();
        associations.clear();
        interfaces.clear();
        generalizations.clear();
        compositeRelations.clear();

        for (Class myClass : tempClasses) {
            redrawClass(myClass);
        }
        for (Interface myInterface : tempInterfaces) {
            reDrawInterface(myInterface);
        }
        for (CompositeRelations r : tempAggregations) {
            if (r.getName().equals("aggregation")) {
                redrawAggregation(r);
            } else {
                redrawComposition(r);
            }
            if (r.getName() != null && !r.getName().isEmpty()) {
                Text relationText = new Text(r.getText());
                Class startClass = r.getStartClass();
                Class endClass = r.getEndClass();
                double midX = (startClass.getInitialPoint().getX() + endClass.getInitialPoint().getX()) / 2;
                double midY = (startClass.getInitialPoint().getY() + endClass.getInitialPoint().getY()) / 2;
                relationText.setX(midX);
                relationText.setY(midY - 10);
                canvasPane.getChildren().add(relationText);
            }
            if (r.getStartMultiplicity() != null) {
                Text startMultiplicityText = new Text(r.getStartMultiplicity().toString());
                startMultiplicityText.setX(r.getStartClass().getInitialPoint().getX() - 15);
                startMultiplicityText.setY(r.getStartClass().getInitialPoint().getY() - 5);
                canvasPane.getChildren().add(startMultiplicityText);
            }
            if (r.getEndMultiplicity() != null) {
                Text endMultiplicityText = new Text(r.getEndMultiplicity().toString());
                endMultiplicityText.setX(r.getEndClass().getInitialPoint().getX() + 5);
                endMultiplicityText.setY(r.getEndClass().getInitialPoint().getY() - 5);
                canvasPane.getChildren().add(endMultiplicityText);
            }
        }

        for (Association association : tempAssociation) {
            redrawAssociation(association);
            if (association.getText() != null && !association.getText().isEmpty()) {
                Text associationText = new Text(association.getText());
                Class startClass = association.getStartClass();
                Class endClass = association.getEndClass();
                double midX = (startClass.getInitialPoint().getX() + endClass.getInitialPoint().getX()) / 2;
                double midY = (startClass.getInitialPoint().getY() + endClass.getInitialPoint().getY()) / 2;
                associationText.setX(midX);
                associationText.setY(midY - 10);
                canvasPane.getChildren().add(associationText);
            }
            if (association.getStartMultiplicity() != null) {
                Text startMultiplicityText = new Text(association.getStartMultiplicity().toString());
                startMultiplicityText.setX(association.getStartClass().getInitialPoint().getX() - 15);
                startMultiplicityText.setY(association.getStartClass().getInitialPoint().getY() - 5);
                canvasPane.getChildren().add(startMultiplicityText);
            }
            if (association.getEndMultiplicity() != null) {
                Text endMultiplicityText = new Text(association.getEndMultiplicity().toString());
                endMultiplicityText.setX(association.getEndClass().getInitialPoint().getX() + 5);
                endMultiplicityText.setY(association.getEndClass().getInitialPoint().getY() - 5);
                canvasPane.getChildren().add(endMultiplicityText);
            }
        }
        for (Generalization g : tempGeneral) {
            redrawGeneralization(g);
        }
    }
    /**
     * Handles the activation of the "Association" tool in the class diagram editor.
     *
     * This method is invoked when the "Association" tool is selected by the user, enabling
     * the functionality for creating association relationships between diagram elements
     * such as classes or interfaces on the canvas.
     *
     * Key functionalities:
     * - Updates the `activeTool` field to "Association".
     * - Calls the `deselectClassBox` method to clear any previously selected class box,
     *   ensuring no conflicts between the selected tool and currently selected elements.
     *
     * Subsequent user actions on the canvas will be interpreted as operations related
     * to creating association relationships.
     */
    @FXML
    private void handleAssociationClick(){activeTool = "Association";
        deselectClassBox();}

    /**
     * Handles the activation of the "Composition" tool in the class diagram editor.
     *
     * This method is triggered when the "Composition" mode is selected by the user.
     * It sets the active tool to "Composition," enabling actions and features specific
     * to creating or interacting with composition relationships between elements on
     * the canvas.
     *
     * Key functionality:
     * - Updates the `activeTool` field to "Composition."
     * - Deselects any currently selected class box on the canvas by calling the
     *   `deselectClassBox` method.
     *
     * This method ensures that subsequent user actions on the canvas are aligned
     * with the "Composition" tool.
     */
    @FXML
    private void handleCompositionClick(){activeTool = "Composition";
        deselectClassBox();}

    public void handleClassButtonClick() {activeTool = "Class";
        deselectClassBox();}
    private void drawAssociation(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        Interface startInterface = getInterfaceAtPoint(initialPoint);
        Interface endInterface = getInterfaceAtPoint(finalPoint);
        Association association = null;
        Line line = null;

        if ((startClass != null || startInterface != null) && (endClass != null || endInterface != null)) {
            line = new Line();

            // Set start and end points for the line depending on whether it's a class or interface
            if (startClass != null) {
                line.setStartX(startClass.getInitialPoint().getX());
                line.setStartY(startClass.getInitialPoint().getY());
            } else {
                line.setStartX(startInterface.getInitialPoint().getX());
                line.setStartY(startInterface.getInitialPoint().getY());
            }

            if (endClass != null) {
                line.setEndX(endClass.getInitialPoint().getX());
                line.setEndY(endClass.getInitialPoint().getY());
            } else {
                line.setEndX(endInterface.getInitialPoint().getX());
                line.setEndY(endInterface.getInitialPoint().getY());
            }

            // Set the style of the line
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            canvasPane.getChildren().add(line);

            // Create an association based on the types of the start and end points
            if (startClass != null && endClass != null) {
                association = new Association(startClass, endClass);
                // Add corresponding AssociatedClass instances for class to class
                AssociatedClass startAssocClass = new AssociatedClass(startClass, "Association");
                AssociatedClass endAssocClass = new AssociatedClass(endClass, "Association");
                startClass.addX(startAssocClass);
                endClass.addX(endAssocClass);
            } else if (startClass != null && endInterface != null) {
                association = new Association(startClass, endInterface);
                // Add corresponding AssociatedClass instances for class to interface
                AssociatedClass startAssocClass = new AssociatedClass(startClass, "Association");
                AssociatedClass endAssocClass = new AssociatedClass(endInterface, "Association");
                startClass.addX(startAssocClass);
                endInterface.addX(endAssocClass);
            } else if (startInterface != null && endClass != null) {
                association = new Association(startInterface, endClass);
                // Add corresponding AssociatedClass instances for interface to class
                AssociatedClass startAssocClass = new AssociatedClass(startInterface, "Association");
                AssociatedClass endAssocClass = new AssociatedClass(endClass, "Association");
                startInterface.addX(startAssocClass);
                endClass.addX(endAssocClass);
            } else if (startInterface != null && endInterface != null) {
                association = new Association(startInterface, endInterface);
                // Add corresponding AssociatedClass instances for interface to interface
                AssociatedClass startAssocClass = new AssociatedClass(startInterface, "Association");
                AssociatedClass endAssocClass = new AssociatedClass(endInterface, "Association");
                startInterface.addX(startAssocClass);
                endInterface.addX(endAssocClass);
            }

            // Add the association to the relevant data structures
            associations.add(association);
            associationMap.put(line, association);
        } else {
            showWarning("Association Error", "Both endpoints must be inside a class or interface.");
        }
    }


    /**
     * Handles the click event for the aggregation tool.
     *
     * This method is triggered when the aggregation tool is clicked in the UI.
     * It sets the active tool to "Aggregation", allowing subsequent actions
     * to be associated with this tool.
     */
    @FXML
    private void handleAggregationClick(){activeTool = "Aggregation";}
    /**
     * Draws an aggregation relationship between two classes represented by their initial points.
     *
     * @param initialPoint the starting point for the aggregation line, representing the first class
     * @param finalPoint the ending point for the aggregation line, representing the second class
     */
    private void drawAggregation(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass == null || endClass == null) {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
            return;
        }
        Line line = new Line();
        line.setStartX(startClass.getInitialPoint().getX());
        line.setStartY(startClass.getInitialPoint().getY());
        line.setEndX(endClass.getInitialPoint().getX());
        line.setEndY(endClass.getInitialPoint().getY());
        canvasPane.getChildren().add(line);
        double dx = endClass.getInitialPoint().getX() - startClass.getInitialPoint().getX();
        double dy = endClass.getInitialPoint().getY() - startClass.getInitialPoint().getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double diamondSize = 10.0;
        double halfSize = diamondSize / 2;
        double[] diamondX = new double[4];
        double[] diamondY = new double[4];
        double finalX = endClass.getInitialPoint().getX();
        double finalY = endClass.getInitialPoint().getY();
        diamondX[0] = finalX;
        diamondY[0] = finalY;
        diamondX[1] = finalX - unitX * diamondSize + unitY * halfSize;
        diamondY[1] = finalY - unitY * diamondSize - unitX * halfSize;
        diamondX[2] = finalX - unitX * diamondSize * 2;
        diamondY[2] = finalY - unitY * diamondSize * 2;
        diamondX[3] = finalX - unitX * diamondSize - unitY * halfSize;
        diamondY[3] = finalY - unitY * diamondSize + unitX * halfSize;
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(diamondX[0], diamondY[0],
                diamondX[1], diamondY[1],
                diamondX[2], diamondY[2],
                diamondX[3], diamondY[3]);
        diamond.setStroke(Color.BLACK);
        diamond.setFill(null);
        canvasPane.getChildren().add(diamond);
        CompositeRelations x = new CompositeRelations(startClass, endClass, "aggregation");
        compositeRelations.add(x);
        compositeRelationMap.put(line, x);
    }
    public void drawComposition(Point initialPoint, Point finalPoint){
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass == null || endClass == null) {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
            return;
        }
        Line line = new Line();
        line.setStartX(startClass.getInitialPoint().getX());
        line.setStartY(startClass.getInitialPoint().getY());
        line.setEndX(endClass.getInitialPoint().getX());
        line.setEndY(endClass.getInitialPoint().getY());
        canvasPane.getChildren().add(line);
        double dx = endClass.getInitialPoint().getX() - startClass.getInitialPoint().getX();
        double dy = endClass.getInitialPoint().getY() - startClass.getInitialPoint().getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double diamondSize = 10.0;
        double halfSize = diamondSize / 2;
        double[] diamondX = new double[4];
        double[] diamondY = new double[4];
        double finalX = endClass.getInitialPoint().getX();
        double finalY = endClass.getInitialPoint().getY();
        diamondX[0] = finalX;
        diamondY[0] = finalY;
        diamondX[1] = finalX - unitX * diamondSize + unitY * halfSize;
        diamondY[1] = finalY - unitY * diamondSize - unitX * halfSize;
        diamondX[2] = finalX - unitX * diamondSize * 2;
        diamondY[2] = finalY - unitY * diamondSize * 2;
        diamondX[3] = finalX - unitX * diamondSize - unitY * halfSize;
        diamondY[3] = finalY - unitY * diamondSize + unitX * halfSize;
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(diamondX[0], diamondY[0],
                diamondX[1], diamondY[1],
                diamondX[2], diamondY[2],
                diamondX[3], diamondY[3]);
        diamond.setStroke(Color.BLACK);
        diamond.setFill(Color.BLACK);
        canvasPane.getChildren().add(diamond);
        CompositeRelations x = new CompositeRelations(startClass, endClass, "composition");
        compositeRelations.add(x);
        compositeRelationMap.put(line, x);
    }
    /**
     * Redraws the visual representation of an aggregation relationship on the canvas.
     * The method validates the start and end classes of the aggregation
     * and creates a line connecting the two classes along with a diamond shape
     * at the end point to indicate aggregation.
     *
     * @param aggregation the aggregation relationship containing information
     *                    about the start and end classes and their initial points.
     */
    private void redrawAggregation(CompositeRelations aggregation) {
        activeTool = null;
        Class startClass = getClassAtPoint(aggregation.startClass.getInitialPoint());
        Class endClass = getClassAtPoint(aggregation.endClass.getInitialPoint());
        if (startClass == null || endClass == null) {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
            return;
        }
        Line line = new Line();
        line.setStartX(startClass.getInitialPoint().getX());
        line.setStartY(startClass.getInitialPoint().getY());
        line.setEndX(endClass.getInitialPoint().getX());
        line.setEndY(endClass.getInitialPoint().getY());
        canvasPane.getChildren().add(line);
        double dx = endClass.getInitialPoint().getX() - startClass.getInitialPoint().getX();
        double dy = endClass.getInitialPoint().getY() - startClass.getInitialPoint().getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double diamondSize = 10.0;
        double halfSize = diamondSize / 2;
        double[] diamondX = new double[4];
        double[] diamondY = new double[4];
        double finalX = endClass.getInitialPoint().getX();
        double finalY = endClass.getInitialPoint().getY();
        diamondX[0] = finalX;
        diamondY[0] = finalY;
        diamondX[1] = finalX - unitX * diamondSize + unitY * halfSize;
        diamondY[1] = finalY - unitY * diamondSize - unitX * halfSize;
        diamondX[2] = finalX - unitX * diamondSize * 2;
        diamondY[2] = finalY - unitY * diamondSize * 2;
        diamondX[3] = finalX - unitX * diamondSize - unitY * halfSize;
        diamondY[3] = finalY - unitY * diamondSize + unitX * halfSize;
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(diamondX[0], diamondY[0],
                diamondX[1], diamondY[1],
                diamondX[2], diamondY[2],
                diamondX[3], diamondY[3]);
        diamond.setStroke(Color.BLACK);
        diamond.setFill(null);
        canvasPane.getChildren().add(diamond);
        compositeRelations.add(aggregation);
        compositeRelationMap.put(line, aggregation);
    }
    /**
     * Redraws a composition relationship between two classes on the canvas. This method ensures that
     * both endpoints of the aggregation are inside valid classes and renders a line and diamond shape
     * to represent the composition.
     *
     * @param aggregation the CompositeRelations object containing information about the start and
     *                    end classes involved in the aggregation relationship.
     */
    public void redrawComposition(CompositeRelations aggregation){
        activeTool = null;
        Class startClass = getClassAtPoint(aggregation.startClass.getInitialPoint());
        Class endClass = getClassAtPoint(aggregation.endClass.getInitialPoint());
        if (startClass == null || endClass == null) {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
            return;
        }
        Line line = new Line();
        line.setStartX(startClass.getInitialPoint().getX());
        line.setStartY(startClass.getInitialPoint().getY());
        line.setEndX(endClass.getInitialPoint().getX());
        line.setEndY(endClass.getInitialPoint().getY());
        canvasPane.getChildren().add(line);
        double dx = endClass.getInitialPoint().getX() - startClass.getInitialPoint().getX();
        double dy = endClass.getInitialPoint().getY() - startClass.getInitialPoint().getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;
        double diamondSize = 10.0;
        double halfSize = diamondSize / 2;
        double[] diamondX = new double[4];
        double[] diamondY = new double[4];
        double finalX = endClass.getInitialPoint().getX();
        double finalY = endClass.getInitialPoint().getY();
        diamondX[0] = finalX;
        diamondY[0] = finalY;
        diamondX[1] = finalX - unitX * diamondSize + unitY * halfSize;
        diamondY[1] = finalY - unitY * diamondSize - unitX * halfSize;
        diamondX[2] = finalX - unitX * diamondSize * 2;
        diamondY[2] = finalY - unitY * diamondSize * 2;
        diamondX[3] = finalX - unitX * diamondSize - unitY * halfSize;
        diamondY[3] = finalY - unitY * diamondSize + unitX * halfSize;
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(diamondX[0], diamondY[0],
                diamondX[1], diamondY[1],
                diamondX[2], diamondY[2],
                diamondX[3], diamondY[3]);
        diamond.setStroke(Color.BLACK);
        diamond.setFill(Color.BLACK);
        canvasPane.getChildren().add(diamond);
        compositeRelations.add(aggregation);
        compositeRelationMap.put(line, aggregation);
    }
    /**
     * Draws a generalization relationship between two classes represented by their start and end points.
     * This method creates a dashed line connecting two classes and an arrowhead pointing towards the final class,
     * indicating the generalization relationship. It also updates the generalization list and associations of the involved classes.
     *
     * @param initialPoint The starting point of the generalization line, representing the initial class.
     * @param finalPoint The ending point of the generalization line, representing the final class.
     */
    public void drawGeneralization(Point initialPoint, Point finalPoint) {
        activeTool=null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass == null || endClass == null) {
            System.out.println("Generalization must connect two valid classes.");
            return;
        }
        Line dottedLine = new Line(startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(), endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY());
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        Polygon arrowHead = new Polygon();
        double arrowSize = 10.0;
        double angle = Math.atan2(finalPoint.getY() - initialPoint.getY(), finalPoint.getX() - initialPoint.getX());
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x1 = finalPoint.getX() - arrowSize * cos - arrowSize / 2 * sin;
        double y1 = finalPoint.getY() - arrowSize * sin + arrowSize / 2 * cos;
        double x2 = finalPoint.getX() - arrowSize * cos + arrowSize / 2 * sin;
        double y2 = finalPoint.getY() - arrowSize * sin - arrowSize / 2 * cos;
        arrowHead.getPoints().addAll(finalPoint.getX(), finalPoint.getY(), x1, y1, x2, y2);
        arrowHead.setFill(Color.BLACK);
        canvasPane.getChildren().addAll(dottedLine, arrowHead);
        Generalization generalization = new Generalization(startClass, endClass);
        AssociatedClass x=new AssociatedClass(startClass,"inheritance");
        AssociatedClass y=new AssociatedClass(endClass,"inheritance");
        endClass.addX(y);
        generalizations.add(generalization);
    }
    /**
     * Draws a visual representation of a UML class on the canvas at the specified coordinates.
     *
     * @param x the x-coordinate where the class should be drawn
     * @param y the y-coordinate where the class should be drawn
     */
    private void drawClass(double x, double y) {
        activeTool = null;
        Point initialPoint = new Point(x, y);
        Class myClass = new Class(initialPoint);
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(x);
        classBox.setLayoutY(y);
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        Label classNameLabel = new Label("Class"+ classes.size());
        myClass.setClassName(classNameLabel.getText());
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Attribute> attributes = myClass.getAttributes();
        for (Attribute attribute : attributes) {
            Label attributeLabel = new Label(attribute.toString());
            attributesBox.getChildren().add(attributeLabel);
        }
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = myClass.getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);
        canvasPane.getChildren().add(classBox);
        classes.add(myClass);
        elementMap.put(classBox, myClass);
    }
    /**
     * Draws an interface on the canvas at the specified position with the defined styling,
     * attributes, and methods. The interface is represented in a VBox containing its name,
     * attributes, and functions.
     *
     * @param x The x-coordinate of the interface's position on the canvas.
     * @param y The y-coordinate of the interface's position on the canvas.
     */
    public void drawInterface(Double x, Double y) {
        activeTool = null;
        Point initialPoint = new Point(x, y);
        Interface myClass = new Interface(initialPoint);
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(x);
        classBox.setLayoutY(y);
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        Label classNameLabel = new Label("Name");
        Label interfaceLabel = new Label("  <<Interface>>");
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        interfaceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox();
        classNameBox.getChildren().addAll(interfaceLabel,classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = myClass.getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);
        canvasPane.getChildren().add(classBox);
        interfaces.add(myClass);
        elementMap.put(classBox, myClass);
    }
    /**
     * Determines whether a given point is near a specified line.
     *
     * @param line the line to check proximity against
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return true if the point is near the line, false otherwise
     */
    private boolean isNearLine(Line line, double x, double y) {
        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());
        Point2D point = new Point2D(x, y);
        return point.distance(start) + point.distance(end) - start.distance(end) < 5;
    }
    /**
     * Displays a warning alert dialog with the given title and message.
     *
     * @param title   the title of the warning alert
     * @param message the message content of the warning alert
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Retrieves the class associated with a node at a specified point.
     *
     * @param point the point to check for a node, represented by x and y coordinates
     * @return the Class object associated with the node at the given point, or null if no such element exists
     */
    private Class getClassAtPoint(Point point) {
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (node instanceof VBox && isWithinBounds(node, point.getX(), point.getY())) {
                Object element = entry.getValue();
                if (element instanceof Class) {
                    return (Class) element;
                }
            }
        }
        return null;
    }
    /**
     * Retrieves the Interface object located at a specific point within the bounds of the elements
     * in the elementMap. The method checks if the given point lies within the bounds of a VBox node
     * within the elementMap, and if the associated value is an instance of Interface, it returns that object.
     *
     * @param point the point to check for the presence of an Interface object.
     * @return the Interface object at the specified point if found, otherwise null.
     */
    private Interface getInterfaceAtPoint(Point point) {
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (node instanceof VBox && isWithinBounds(node, point.getX(), point.getY())) {
                Object element = entry.getValue();
                if (element instanceof Interface) {
                    return (Interface) element;
                }
            }
        }
        return null;
    }
    /**
     * Checks if the specified coordinates (x, y) are within the bounds of the given node.
     *
     * @param node the node whose bounds are to be checked
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates are within the bounds of the node, false otherwise
     */
    private boolean isWithinBounds(Node node, double x, double y) {
        return node.getBoundsInParent().contains(x, y);
    }
    /**
     * Displays the interface details in a new window, allowing the user to
     * view and modify the attributes and functions of the given interface.
     *
     * @param clazz the interface object whose details are to be displayed and edited
     */
    public void showInterfaceDetails(Interface clazz){
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true);
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);
        detailBox.getChildren().add(new Label("Functions:"));
        VBox functionsBox = new VBox(5);
        updateFunctionsBox(clazz, functionsBox);
        Button addFunctionButton = new Button("Add Function");
        addFunctionButton.setOnAction(e -> {
            Function newFunc = new Function("void", "newFunction"); // Default function
            clazz.addFunction(newFunc);
            updateFunctionsBox(clazz, functionsBox);
        });
        detailBox.getChildren().addAll(functionsBox, addFunctionButton);
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            classes.remove(clazz);
            elementMap.values().remove(clazz);
            reDrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(deleteButton);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            clazz.setClassName(classNameField.getText());
            reDrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }
    /**
     * Displays a detailed UI for editing the attributes and functions of a given class.
     * Allows the user to modify the class name, add or remove attributes, add or remove functions,
     * and delete or submit changes to the class. The changes are reflected within the application.
     *
     * @param clazz The class object whose details are to be displayed and edited.
     */
    private void showClassDetails(Class clazz) {
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true);
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);
        detailBox.getChildren().add(new Label("Attributes:"));
        VBox attributesBox = new VBox(5);
        updateAttributesBox(clazz, attributesBox);
        Button addAttributeButton = new Button("Add Attribute");
        addAttributeButton.setOnAction(e -> {
            Attribute newAttr = new Attribute("", "String");
            clazz.addAttribute(newAttr);
            updateAttributesBox(clazz, attributesBox);
        });
        detailBox.getChildren().addAll(attributesBox, addAttributeButton);
        detailBox.getChildren().add(new Label("Functions:"));
        VBox functionsBox = new VBox(5);
        updateFunctionsBox(clazz, functionsBox);
        Button addFunctionButton = new Button("Add Function");
        addFunctionButton.setOnAction(e -> {
            Function newFunc = new Function("void", "newFunction");
            clazz.addFunction(newFunc);
            updateFunctionsBox(clazz, functionsBox);
        });
        detailBox.getChildren().addAll(functionsBox, addFunctionButton);
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            classes.remove(clazz);
            elementMap.values().remove(clazz);
            reDrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(deleteButton);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            clazz.setClassName(classNameField.getText());
            reDrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }
    /**
     * Updates the content of the provided attributesBox to display the attributes of the given class.
     * Each attribute is represented with components to allow editing its properties (such as name, type,
     * and access modifier) or removing the attribute entirely.
     *
     * @param clazz The class whose attributes are to be displayed and modified.
     * @param attributesBox The VBox container where the attributes will be rendered as editable components.
     */
    private void updateAttributesBox(Class clazz, VBox attributesBox) {
        attributesBox.getChildren().clear();
        for (Attribute attribute : clazz.getAttributes()) {
            HBox attrBox = new HBox(5);
            TextField nameField = new TextField(attribute.getName());
            ComboBox<String> DataTypeBox = new ComboBox<>();
            DataTypeBox.getItems().addAll("String","int","Double","Float","List<Int>","List<Double>","List<String>");
            for (Class cl : classes)
            {
                DataTypeBox.getItems().add(cl.getClassName());
                DataTypeBox.getItems().add("List<"+cl.getClassName()+">");
            }
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(attribute.getAccessModifier());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeAttribute(attribute);
                updateAttributesBox(clazz, attributesBox);
            });
            attrBox.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Type:"), DataTypeBox,
                    new Label("Access:"), accessModifierBox,
                    deleteButton
            );
            attributesBox.getChildren().add(attrBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> attribute.setName(newText));
            DataTypeBox.valueProperty().addListener((obs, oldText, newText) -> attribute.setDataType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setAccessModifier(newVal));
        }
    }

    /**
     * Updates the provided functions box (VBox) with details of the functions
     * belonging to the specified class. Each function's information including
     * name, return type, access modifier, and parameters is represented as a
     * user interface component. Provides the ability to add parameters, modify
     * function properties, and delete functions.
     *
     * @param clazz The class whose functions are to be displayed and managed in the functions box.
     * @param functionsBox The VBox container that will be populated with UI components representing the functions of the class.
     */
    private void updateFunctionsBox(Class clazz, VBox functionsBox) {
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);
            TextField nameField = new TextField(function.getName());
            ComboBox<String> returnTypeField = new ComboBox();
            returnTypeField.getItems().addAll("void","String","int","Double","Float","List<Int>","List<Double>","List<String>");
            for (Class cl : classes)
            {
                returnTypeField.getItems().add(cl.getClassName());
                returnTypeField.getItems().add("List<"+cl.getClassName()+">");
            }
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());
            VBox parametersBox = new VBox(5);
            updateParametersBox(function, parametersBox);
            Button addParameterButton = new Button("Add Parameter");
            addParameterButton.setOnAction(e -> {
                function.addAttribute(new Attribute("param", "String"));
                updateParametersBox(function, parametersBox);
            });
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeFunction(function);
                updateFunctionsBox(clazz, functionsBox);
            });
            funcBox.getChildren().addAll(
                    new Label("Function Name:"), nameField,
                    new Label("Return Type:"), returnTypeField,
                    new Label("Access:"), accessModifierBox,
                    new Label("Parameters:"), parametersBox, addParameterButton,
                    deleteButton
            );
            functionsBox.getChildren().add(funcBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.valueProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }
    /**
     * Updates the functionsBox to display and manage the functions of the given interface.
     *
     * @param clazz the interface containing the functions to be displayed
     * @param functionsBox the VBox that will be updated to show the functions in a modifiable form
     */
    private void updateFunctionsBox(Interface clazz, VBox functionsBox) {
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);
            TextField nameField = new TextField(function.getName());
            TextField returnTypeField = new TextField(function.getReturnType());
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());
            VBox parametersBox = new VBox(5);
            updateParametersBox(function, parametersBox);
            Button addParameterButton = new Button("Add Parameter");
            addParameterButton.setOnAction(e -> {
                function.addAttribute(new Attribute("param", "String"));
                updateParametersBox(function, parametersBox);
            });
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeFunction(function);
                updateFunctionsBox(clazz, functionsBox);
            });
            funcBox.getChildren().addAll(
                    new Label("Function Name:"), nameField,
                    new Label("Return Type:"), returnTypeField,
                    new Label("Access:"), accessModifierBox,
                    new Label("Parameters:"), parametersBox, addParameterButton,
                    deleteButton
            );
            functionsBox.getChildren().add(funcBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.textProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }

    /**
     * Updates the parameters box by populating it with UI components for editing
     * the attributes of a given function. The parameters box is cleared and then
     * populated with rows for each attribute, allowing modification of its name
     * and type.
     *
     * @param function The function whose attributes are to be displayed and edited.
     * @param parametersBox The VBox container where the UI components for
     *                      editing the attributes will be added.
     */
    private void updateParametersBox(Function function, VBox parametersBox) {
        parametersBox.getChildren().clear();
        for (Attribute parameter : function.getAttributes()) {
            HBox paramBox = new HBox(5);
            TextField paramNameField = new TextField(parameter.getName());
            ComboBox<String> paramTypeField = new ComboBox<String>();
            paramTypeField.getItems().addAll("String","int","Double","Float","List<Int>","List<Double>","List<String>");
            for (Class cl : classes)
            {
                paramTypeField.getItems().add(cl.getClassName());
                paramTypeField.getItems().add("List<"+cl.getClassName()+">");
            }
            Button deleteParamButton = new Button("Delete");
            deleteParamButton.setOnAction(e -> {
                function.removeAttribute(parameter);
                updateParametersBox(function, parametersBox);
            });
            paramBox.getChildren().addAll(
                    new Label("Param Name:"), paramNameField,
                    new Label("Type:"), paramTypeField,
                    deleteParamButton
            );
            parametersBox.getChildren().add(paramBox);
            paramNameField.textProperty().addListener((obs, oldText, newText) -> parameter.setName(newText));
            paramTypeField.valueProperty().addListener((obs, oldText, newText) -> parameter.setDataType(newText));
        }
    }


    /**
     * Calculates the maximum width of all Label nodes within the given VBox and adds a padding of 10 units.
     *
     * @param vbox the VBox container whose Label nodes will be evaluated
     * @return the calculated maximum width of the Label nodes plus an additional padding of 10 units
     */
    private double getMaxLabelWidth(VBox vbox) {
        double maxWidth = 0;
        for (Node node : vbox.getChildren()) {
            if (node instanceof Label) {
                maxWidth = Math.max(maxWidth, ((Label) node).getWidth());
            }
        }
        return maxWidth + 10;
    }
    /**
     * Redraws an association between two classes or interfaces on a canvas, ensuring
     * the association is correctly represented with a visual connection based on
     * the positions of its start and end elements. If the start or end points are
     * missing, an error message is displayed.
     *
     * @param association The association to be redrawn, which includes the connection
     *                     between two classes or interfaces.
     */
    private void redrawAssociation(Association association) {
        // Get initial points for start and end
        Point startPoint = null;
        Point endPoint = null;

        if (association.getStartClass() != null) {
            startPoint = association.getStartClass().getInitialPoint();
        } else if (association.getStartInterface() != null) {
            startPoint = association.getStartInterface().getInitialPoint();
        }

        if (association.getEndClass() != null) {
            endPoint = association.getEndClass().getInitialPoint();
        } else if (association.getEndInterface() != null) {
            endPoint = association.getEndInterface().getInitialPoint();
        }

        // If either point is null, show an error message
        if (startPoint == null || endPoint == null) {
            showWarning("Redraw Error", "One or both associated classes/interfaces no longer exist at their original positions.");
            return;
        }

        // Get the start and end points of the association
        Class startClass = getClassAtPoint(startPoint);
        Class endClass = getClassAtPoint(endPoint);
        Interface startInterface = getInterfaceAtPoint(startPoint);
        Interface endInterface = getInterfaceAtPoint(endPoint);

        // Draw the line
        Line line = new Line();
        if (startClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
        } else if (startInterface != null) {
            line.setStartX(startInterface.getInitialPoint().getX());
            line.setStartY(startInterface.getInitialPoint().getY());
        }

        if (endClass != null) {
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
        } else if (endInterface != null) {
            line.setEndX(endInterface.getInitialPoint().getX());
            line.setEndY(endInterface.getInitialPoint().getY());
        }

        // Set the style of the line
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);

        // Add the line to the canvas
        canvasPane.getChildren().add(line);

        // Re-add the association to the relevant data structures
        associations.add(association);
        associationMap.put(line, association);
    }

    /**
     * Redraws a generalization relationship between two classes.
     * This method creates a dashed line connecting the two classes
     * and an arrowhead at the end to represent the generalization.
     * If either the start class or the end class is null, the method
     * will print an error and abort the operation.
     *
     * @param generalization the generalization relationship to be redrawn,
     *                       containing information about the starting and
     *                       ending classes, as well as their positions.
     */
    public void redrawGeneralization(Generalization generalization) {
        activeTool = null;
        Class startClass = generalization.getStartClass();
        Class endClass = generalization.getEndClass();
        if (startClass == null || endClass == null) {
            System.out.println("Cannot redraw generalization: One or both classes are null.");
            return;
        }
        Point startPoint = new Point(generalization.getStartClass().getInitialPoint().getX(), generalization.getStartClass().getInitialPoint().getY());
        Point endPoint = new Point(generalization.getEndClass().getInitialPoint().getX(), generalization.getEndClass().getInitialPoint().getY());
        Line dottedLine = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        Polygon arrowHead = new Polygon();
        double arrowSize = 10.0;
        double angle = Math.atan2(endPoint.getY() - startPoint.getY(), endPoint.getX() - startPoint.getX());
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x1 = endPoint.getX() - arrowSize * cos - arrowSize / 2 * sin;
        double y1 = endPoint.getY() - arrowSize * sin + arrowSize / 2 * cos;
        double x2 = endPoint.getX() - arrowSize * cos + arrowSize / 2 * sin;
        double y2 = endPoint.getY() - arrowSize * sin - arrowSize / 2 * cos;
        arrowHead.getPoints().addAll(endPoint.getX(), endPoint.getY(), x1, y1, x2, y2);
        arrowHead.setFill(Color.BLACK);
        canvasPane.getChildren().addAll(dottedLine, arrowHead);
        generalizations.add(generalization);
    }
    /**
     * Redraws a visual representation of a given class on the canvas.
     * The method creates and styles a VBox container for the class, including
     * its name, attributes, and functions, and adjusts its dimensions
     * based on the contents.
     *
     * @param claz the class object containing information about the class,
     *             including its initial position, name, attributes, and functions
     */
    private void redrawClass(Class claz) {
        Point initialPoint = new Point(claz.getInitialPoint().getX(), claz.getInitialPoint().getY());
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        Label classNameLabel = new Label(claz.getClassName());
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Attribute> attributes = claz.getAttributes();
        for (Attribute attribute : attributes) {
            Label attributeLabel = new Label(attribute.toString());
            attributesBox.getChildren().add(attributeLabel);
        }
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = claz.getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);
        canvasPane.getChildren().add(classBox);
        elementMap.put(classBox, claz);
        classes.add(claz);
    }


    /**
     * Handles the operation of taking a snapshot of the current canvas.
     * Sets the active tool to null and invokes the snapshot handling logic
     * provided by the {@code SnapshotHelper} class.
     *
     * The method passes necessary references, including {@code canvasPane},
     * {@code canvas}, a warning handler, and a callback for saving the
     * generated image. If an error occurs during the image saving process,
     * an exception will be thrown.
     */
    @FXML
    public void handleSnapshot() {
        activeTool = null;

        // Call the static method from SnapshotHelper
        SnapshotHelper.handleSnapshot(
                canvasPane,               // Pass canvasPane
                canvas,                   // Pass canvas
                this::showWarning,        // Pass the showWarning method reference
                writableImage -> {
                    try {
                        saveImage(writableImage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }           // Pass the saveImage method reference
        );
    }

    /**
     * Displays a warning dialog with the specified message.
     *
     * @param message the content text to be displayed in the warning dialog
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Saves the provided WritableImage to the user's file system as a PNG file.
     * A file chooser dialog is displayed to allow the user to specify the save location and file name.
     * If a file is successfully saved, a confirmation alert is shown.
     * If the save operation is cancelled, a warning is displayed.
     *
     * @param writableImage the image to be saved as a file
     * @throws IOException if an error occurs during the save operation
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
     * Selects a specified VBox and applies specific styles to indicate selection.
     * If a VBox is already selected, it is deselected before selecting the new one.
     *
     * @param classBox the VBox to be selected and styled
     */
    private void selectClassBox(VBox classBox) {
        if (selectedClassBox != null) {
            deselectClassBox();
        }
        selectedClassBox = classBox;
        classBox.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-padding: 5; -fx-background-color: #e0e0e0;");
    }
    /**
     * Deselects the currently selected class box, if any. This method resets
     * the visual style of the selected class box to its default appearance
     * and clears the reference to the selected class box.
     */
    private void deselectClassBox() {
        if (selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }
    /**
     * Redraws the interface representation on the user interface. This method is used
     * to create a graphical depiction of a given interface with its associated attributes
     * and functions, aligning its layout and styles on a designated canvas.
     *
     * @param claz The interface object to be drawn. This object contains the interface
     *             details such as its name, initial position, and associated functions.
     */
    private void reDrawInterface(Interface claz) {
        Point initialPoint = new Point(claz.getInitialPoint().getX(), claz.getInitialPoint().getY());
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
        Label classNameLabel = new Label(claz.getClassName());
        Label InterfaceLable = new Label("  <<Interface>>");
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        InterfaceLable.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox();
        classNameBox.getChildren().add(InterfaceLable);
        classNameBox.getChildren().add(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = claz.getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);
        canvasPane.getChildren().add(classBox);
        interfaces.add(claz);
    }

    /**
     * Displays a form to edit the details of an association, including its multiplicities
     * and text. The dialog allows the user to update or delete the association. If the user
     * submits valid multiplicity values, the association is updated. If the delete button
     * is pressed, the association is removed.
     *
     * @param association the association object whose details are being edited
     */
    private void showAssociationDetailsForm(Association association) {
        Dialog<ButtonType> dialog = new Dialog<>();
        deselectClassBox();
        dialog.setTitle("Edit Association");
        dialog.setHeaderText("Edit Multiplicity and Text for Association");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        if (association.getStartMultiplicity() != null) {
            startStartField.setText(association.getStartMultiplicity().getStart().toString());
            startEndField.setText(association.getStartMultiplicity().getEnd().toString());
        }
        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        if (association.getEndMultiplicity() != null) {
            endStartField.setText(association.getEndMultiplicity().getStart().toString());
            endEndField.setText(association.getEndMultiplicity().getEnd().toString());
        }
        TextField textField = new TextField();
        textField.setPromptText("Text");
        if (association.getText() != null) {
            textField.setText(association.getText());
        }
        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, deleteButtonType);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                try {
                    Double startStart = Double.parseDouble(startStartField.getText());
                    Double startEnd = Double.parseDouble(startEndField.getText());
                    Double endStart = Double.parseDouble(endStartField.getText());
                    Double endEnd = Double.parseDouble(endEndField.getText());
                    association.setStartMultiplicity(new Multiplicity(startStart, startEnd));
                    association.setEndMultiplicity(new Multiplicity(endStart, endEnd));
                    association.setText(textField.getText());
                    reDrawCanvas();
                } catch (NumberFormatException e) {
                    showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
                }
            } else if (result.get() == deleteButtonType) {
                associations.remove(association);
                associationMap.remove(association);
                reDrawCanvas();
            }
        }
    }

    /**
     * Displays a dialog form to edit details of an aggregation relationship,
     * including its multiplicities and associated text. The user can update
     * the values, delete the aggregation, or cancel the operation.
     *
     * @param aggregation The CompositeRelations object representing the aggregation
     *                    whose details are to be viewed or edited.
     */
    private void showAggregationDetailsForm(CompositeRelations aggregation) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Aggregation");
        dialog.setHeaderText("Edit Multiplicity and Text for Aggregation");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        if (aggregation.getStartMultiplicity() != null) {
            startStartField.setText(aggregation.getStartMultiplicity().getStart().toString());
            startEndField.setText(aggregation.getStartMultiplicity().getEnd().toString());
        }
        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        if (aggregation.getEndMultiplicity() != null) {
            endStartField.setText(aggregation.getEndMultiplicity().getStart().toString());
            endEndField.setText(aggregation.getEndMultiplicity().getEnd().toString());
        }
        TextField textField = new TextField();
        textField.setPromptText("Text");
        if (aggregation.getText() != null) {
            textField.setText(aggregation.getText());
        }
        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, deleteButtonType);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                try {
                    Double startStart = Double.parseDouble(startStartField.getText());
                    Double startEnd = Double.parseDouble(startEndField.getText());
                    Double endStart = Double.parseDouble(endStartField.getText());
                    Double endEnd = Double.parseDouble(endEndField.getText());
                    aggregation.setStartMultiplicity(new Multiplicity(startStart, startEnd));
                    aggregation.setEndMultiplicity(new Multiplicity(endStart, endEnd));
                    aggregation.setText(textField.getText());
                    reDrawCanvas();
                } catch (NumberFormatException e) {
                    showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
                }
            } else if (result.get() == deleteButtonType) {
                compositeRelations.remove(aggregation);
                compositeRelationMap.remove(aggregation);
                reDrawCanvas();
            }
        }
    }

    /**
     * Handles the save action for a class diagram.
     * This method opens a file chooser dialog to allow the user to specify a location
     * and a filename for saving the class diagram. If the user selects a location and filename,
     * the method ensures that the file has the appropriate ".nalla" extension and then attempts
     * to serialize the class diagram data to the specified file.
     *
     * If the save operation is successful, an informational alert is displayed to the user
     * indicating success. In the case of an error during the file saving process, an error
     * alert is displayed with the relevant error message.
     *
     * The method uses the following components:
     * - FileChooser: Allows the user to select the desired location and name for saving the file.
     * - ClassDiagramSerializer: Responsible for serializing the class diagram.
     * - Alert: Displays success or error messages to the user.
     *
     * Exceptions:
     * - If an IOException occurs during the file writing process, the error is handled and displayed
     *   to the user via an alert.
     */
    public void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Class Diagram");
        fileChooser.setInitialFileName("diagram.nalla");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Nalla files (*.nalla)", "*.nalla");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (!file.getName().endsWith(".nalla")) {
                file = new File(file.getAbsolutePath() + ".nalla");
            }
            try {
                ClassDiagramSerializer.serialize(classes, associations, interfaces, compositeRelations, generalizations, file.getAbsolutePath());
                showAlert("Success", "File saved successfully!", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to save the file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Displays an alert dialog with the specified title, message, and alert type.
     *
     * @param title the title of the alert dialog
     * @param message the message content of the alert dialog
     * @param alertType the type of alert (e.g., INFORMATION, WARNING, ERROR)
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the upload of a class diagram file.
     *
     * This method provides a file chooser dialog for the user to select a file with the
     * ".nalla" extension. Upon selecting a valid file, it attempts to deserialize the file's
     * contents into various components of a class diagram. The canvas is cleared before
     * updating with the deserialized data. If the file is invalid or an error occurs during
     * the deserialization process, appropriate warnings are displayed.
     *
     * The file is expected to contain serialized data representing classes, associations,
     * interfaces, composite relations, and generalizations. These components are added to
     * their respective lists and the canvas is redrawn to reflect the loaded data.
     *
     * If the file type is not ".nalla", or no file is selected, the method displays an
     * appropriate warning message.
     *
     * Potential exceptions during deserialization are caught and logged, and the user is
     * notified of any errors that occur during the process through a warning dialog.
     */
    public void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Class Diagram files (*.nalla)", "*.nalla");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            if (file.getName().endsWith(".nalla")) {
                try {
                    Object[] deserializedData = ClassDiagramSerializer.deserialize(file.getAbsolutePath());
                    clearCanvas();
                    classes.addAll((List<Class>) deserializedData[0]);
                    associations.addAll((List<Association>) deserializedData[1]);
                    interfaces.addAll((List<Interface>) deserializedData[2]);
                    compositeRelations.addAll((List<CompositeRelations>) deserializedData[3]);
                    generalizations.addAll((List<Generalization>) deserializedData[4]);
                    reDrawCanvas();

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    showWarning("Error", "An error occurred while loading the class diagram.");
                }
            } else {
                showWarning("Invalid File", "Please select a .nalla file.");
            }
        }
    }
    /**
     * Clears all elements and data associated with the canvas.
     *
     * This method performs the following actions:
     * - Removes all child nodes from the canvasPane.
     * - Clears the list of classes.
     * - Clears the element map.
     * - Clears the list of interfaces.
     * - Clears the list of associations.
     * - Clears the list of composite relations.
     * - Clears the map of associations.
     * - Clears the map of composite relations.
     */
    @FXML
    private void clearCanvas(){
        canvasPane.getChildren().clear();
        classes.clear();
        elementMap.clear();
        interfaces.clear();
        associations.clear();
        compositeRelations.clear();
        associationMap.clear();
        compositeRelationMap.clear();
    }

    /**
     * Handles the action associated with saving generated Java files to a selected
     * directory. This method allows the user to choose a directory via a directory
     * chooser dialog, where a subfolder named "GeneratedClasses" will be created
     * (if it doesn't already exist). Java files for specified classes and interfaces
     * are then generated and saved inside the subfolder.
     *
     * The method performs the following steps:
     * 1. Opens a directory chooser dialog for the user to select a directory.
     * 2. Creates a subfolder named "GeneratedClasses" inside the selected directory.
     * 3. Iterates over a collection of classes and interfaces to generate their
     *    respective Java source files and saves them into the subfolder.
     * 4. Displays a success alert if the operation completes successfully.
     *
     * In case of an error during file generation, an error alert is displayed
     * detailing the exception. If no directory is selected by the user, a warning
     * alert is displayed prompting the user to select a directory.
     */
    @FXML
    private void handleCode() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Java Files");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            try {
                File subFolder = new File(selectedDirectory, "GeneratedClasses");
                if (!subFolder.exists()) {
                    subFolder.mkdir();
                }
                for (Class myClass : classes) {
                    String fileName = myClass.getClassName() + ".java";
                    File javaFile = new File(subFolder, fileName);
                    myClass.generateCode(javaFile.getAbsolutePath());
                }
                for (Interface i : interfaces) {
                    String fileName = i.getClassName() + ".java";
                    File javaFile = new File(subFolder, fileName);
                    i.generateCode(javaFile.getAbsolutePath());
                }
                showAlert("File Saved Bhai", "Java files generated in: " + subFolder.getAbsolutePath(), Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                showAlert("Error", "An error occurred while generating files: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Directory Selected", "Please select a directory to save the files.", Alert.AlertType.WARNING);
        }
    }
    /**
     * Handles the action event to return to the main screen.
     * This method creates a new stage with the specified FXML file
     * and displays it, closing the primary stage.
     *
     * @param event The action event that triggers this method.
     * @throws IOException If there is an error loading the FXML file.
     */
    @FXML
    private void returntomain(ActionEvent event) throws IOException {
        Stage newStage = new Stage();

        // Load the FXML file for the new scene
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/boota/javaproject/MainCanvas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);

        // Set the new stage properties
        newStage.setTitle("xyz!");
        newStage.setScene(scene);
        newStage.show();
        Main.primaryStage.close();
    }
}