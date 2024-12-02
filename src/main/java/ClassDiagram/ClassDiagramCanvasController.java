package ClassDiagram;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class ClassDiagramCanvasController {

    @FXML
    private Pane canvasPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private ArrayList<ClassDiagram.Class> classes = new ArrayList<>();
    private ArrayList<Association> associations = new ArrayList<>();
    private VBox selectedClassBox = null;
    private Association selectedAssociation = null;
    private String activeTool = null;
    private Map<String, BiConsumer<Double, Double>> drawActions = new HashMap<>();
    private Map<Node, Object> elementMap = new HashMap<>();
    private Point initialPoint = null;
    private Line tempLine = null;
    private ArrayList<Interface> interfaces = new ArrayList<>();

    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);

        drawActions.put("Class", this::drawClass);
        drawActions.put("Association", this::drawAssociation);
        drawActions.put("Interface", this::drawInterface);

        // Set event handlers for mouse actions
        canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
        canvasPane.setOnMouseClicked(this::handleCanvasClick);
        canvasPane.setOnMousePressed(this::handleMousePress);
        canvasPane.setOnMouseReleased(this::handleMouseRelease);
        canvasPane.setOnMouseDragged(this::handleMouseDragged);

        // Set event listener for delete key on scene
        canvasPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.DELETE && selectedAssociation != null) {
                        deleteSelectedAssociation(); // Delete the selected association
                    }
                });
            }
        });
    }

    // Handle mouse drag for association drawing
    private void handleMouseDragged(MouseEvent event) {
        if ("Association".equals(activeTool) && initialPoint != null) {
            if (tempLine == null) {
                // Ensure tempLine starts at the initial point
                tempLine = new Line(initialPoint.getX(), initialPoint.getY(), event.getX(), event.getY());
                tempLine.setStroke(javafx.scene.paint.Color.GRAY);
                tempLine.setStrokeWidth(1);
                canvasPane.getChildren().add(tempLine);
            } else {
                tempLine.setEndX(event.getX());
                tempLine.setEndY(event.getY());
            }
        }
    }

    // Handle mouse press for starting an association
    private void handleMousePress(MouseEvent event) {
        if ("Association".equals(activeTool)) {
            initialPoint = new Point(event.getX(), event.getY());
        }
    }
    private void handleMouseRelease(MouseEvent event) {
        if ("Association".equals(activeTool) && initialPoint != null) {
            Point finalPoint = new Point(event.getX(), event.getY());

            // Check for valid classes at both points
            if (getClassAtPoint(initialPoint.getX(), initialPoint.getY()) == null || getClassAtPoint(finalPoint.getX(), finalPoint.getY()) == null) {
                // Show error message
                showWarning("Error", "No class found at start or end point. Association cannot be drawn.");

                // Remove temporary line, if exists
                if (tempLine != null) {
                    canvasPane.getChildren().remove(tempLine);
                    tempLine = null;
                }

                // Reset initial state
                initialPoint = null;
                activeTool = null;
                return;
            }

            // Draw the permanent line if points are valid
            Line line = new Line(initialPoint.getX(), initialPoint.getY(), finalPoint.getX(), finalPoint.getY());
            line.setStrokeWidth(2);
            line.setStroke(javafx.scene.paint.Color.BLACK);
            line.setOnMouseClicked(this::handleLineClick);
            canvasPane.getChildren().add(line);

            // Create association and add to collections
            Association association = new Association(line);
            associations.add(association);
            elementMap.put(line, association);

            // Clear initial state
            initialPoint = null;
            activeTool = null;

            // Remove temporary line
            if (tempLine != null) {
                canvasPane.getChildren().remove(tempLine);
                tempLine = null;
            }
        }
    }

    public void interfacePressed(){
        activeTool = "Interface";
    }

    public void drawInterface(Double x, Double y) {
        activeTool = null;

        Point initialPoint = new Point(x, y);
        ClassDiagram.Interface myClass = new ClassDiagram.Interface(initialPoint);

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
        elementMap.put(classBox, myClass); // Track element for double-click detection
    }

    // Handle click event on a line (to select or deselect association)
    private void handleLineClick(MouseEvent event) {
        Line clickedLine = (Line) event.getSource();
        if (event.getClickCount() == 2) {
            // Toggle selection state for the association line
            if (clickedLine.getStroke() == javafx.scene.paint.Color.BLUE) {
                clickedLine.setStroke(javafx.scene.paint.Color.BLACK);  // Reset color to black
                selectedAssociation = null;  // Deselect the association
            } else {
                clickedLine.setStroke(javafx.scene.paint.Color.BLUE);  // Select and highlight in blue
                selectedAssociation = (Association) elementMap.get(clickedLine);
                showAssociationOptions(selectedAssociation);
            }
        }
    }

    private void showAssociationOptions(Association association) {
        // Get the multiplicity values and text from the association
        Multiplicity startMultiplicity = association.getStartMultiplicity();
        Multiplicity endMultiplicity = association.getEndMultiplicity();
        String text = association.getText();

        // Create a new window (Stage) to show the form
        Stage formStage = new Stage();
        formStage.setTitle("Association Details");

        // Create a GridPane layout for the form
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        // Add form labels and input fields
        Label textLabel = new Label("Text:");
        TextField textField = new TextField(text);

        // Multiplicity fields
        Label multiplicity1Label = new Label("Start Multiplicity (Start):");
        TextField multiplicity1StartField = new TextField(startMultiplicity.getStart().toString());

        Label multiplicity1EndLabel = new Label("Start Multiplicity (End):");
        TextField multiplicity1EndField = new TextField(startMultiplicity.getEnd().toString());

        Label multiplicity2Label = new Label("End Multiplicity (Start):");
        TextField multiplicity2StartField = new TextField(endMultiplicity.getStart().toString());

        Label multiplicity2EndLabel = new Label("End Multiplicity (End):");
        TextField multiplicity2EndField = new TextField(endMultiplicity.getEnd().toString());

        // Add components to the grid
        grid.add(textLabel, 0, 0);
        grid.add(textField, 1, 0);

        grid.add(multiplicity1Label, 0, 1);
        grid.add(multiplicity1StartField, 1, 1);

        grid.add(multiplicity1EndLabel, 0, 2);
        grid.add(multiplicity1EndField, 1, 2);

        grid.add(multiplicity2Label, 0, 3);
        grid.add(multiplicity2StartField, 1, 3);

        grid.add(multiplicity2EndLabel, 0, 4);
        grid.add(multiplicity2EndField, 1, 4);

        // Create a Save button to update the association
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Save the updated values back to the association object
            association.setText(textField.getText());

            // Update the multiplicity objects
            Double multiplicity1Start = Double.parseDouble(multiplicity1StartField.getText());
            Double multiplicity1End = Double.parseDouble(multiplicity1EndField.getText());
            association.setStartMultiplicity(new Multiplicity(multiplicity1Start, multiplicity1End));

            Double multiplicity2Start = Double.parseDouble(multiplicity2StartField.getText());
            Double multiplicity2End = Double.parseDouble(multiplicity2EndField.getText());
            association.setEndMultiplicity(new Multiplicity(multiplicity2Start, multiplicity2End));

            // Update the texts on the canvas after saving
            updateAssociationTextOnCanvas(association);

            // Close the form window after saving
            formStage.close();
        });

        // Add the Save button to the grid
        grid.add(saveButton, 1, 5);

        // Create and set the scene
        Scene scene = new Scene(grid, 350, 300);
        formStage.setScene(scene);
        formStage.show();
    }

    private void updateAssociationTextOnCanvas(Association association) {
        // Retrieve the start and end points of the association line
        Line line = association.getLine();
        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());

        // Remove existing texts
        canvasPane.getChildren().removeIf(node -> node instanceof Text);

        // Calculate the angle of the line (for placing text above it)
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double angle = Math.atan2(dy, dx);

        // Add text labels for multiplicities and association text
        double offsetX = 10 * Math.cos(angle + Math.PI / 2); // Perpendicular offset
        double offsetY = 10 * Math.sin(angle + Math.PI / 2); // Perpendicular offset

        // Multiplicities at the start and end
        Text startMultiplicityText = new Text(start.getX() + offsetX, start.getY() + offsetY, association.getStartMultiplicity().toString());
        Text endMultiplicityText = new Text(end.getX() + offsetX, end.getY() + offsetY, association.getEndMultiplicity().toString());

        // Association text in the middle
        double middleX = (start.getX() + end.getX()) / 2 + offsetX;
        double middleY = (start.getY() + end.getY()) / 2 + offsetY;
        Text associationText = new Text(middleX, middleY, association.getText());

        // Add updated texts to the canvas
        canvasPane.getChildren().addAll(startMultiplicityText, endMultiplicityText, associationText);
    }





    // Show warning alert with a message
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteSelectedAssociation() {
        if (selectedAssociation != null) {
            // Remove the line from the canvas
            canvasPane.getChildren().remove(selectedAssociation.getLine());

            // Remove the association from collections
            associations.remove(selectedAssociation);
            elementMap.remove(selectedAssociation.getLine());

            // Deselect the association
            selectedAssociation = null;
        }
    }

    public void drawAssociation(double x, double y) {
        if (initialPoint == null) {
            // Check if the click is inside a class at the initial point
            VBox startClassBox = getClassAtPoint(x, y);
            if (startClassBox == null) {
                showWarning("Error", "No class found at the initial point. Cannot start association.");
                return;  // Do not proceed with drawing
            }

            // Get the nearest corner of the VBox at the initial point
            initialPoint = getNearestCorner(startClassBox, x, y);

            // Draw the temporary line (while the user is moving the mouse)
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.setStrokeWidth(2);
            tempLine.setStroke(javafx.scene.paint.Color.GRAY);  // Temporary line color
            canvasPane.getChildren().add(tempLine);
        } else {
            // Check if the click is inside a class at the final point
            VBox endClassBox = getClassAtPoint(x, y);
            if (endClassBox == null) {
                showWarning("Error", "No class found at the final point. Cannot complete association.");
                canvasPane.getChildren().remove(tempLine);  // Remove temporary line
                initialPoint = null;  // Reset initial point
                return;  // Do not proceed with drawing
            }

            // Get the nearest corner of the VBox at the final point
            Point finalPoint = getNearestCorner(endClassBox, x, y);

            // Remove the temporary line
            canvasPane.getChildren().remove(tempLine);

            // Retrieve initial and final points from associated classes
            Point i = ClassAtPoint(initialPoint.getX(), initialPoint.getY()).getInitialPoint();
            Point f = ClassAtPoint(finalPoint.getX(), finalPoint.getY()).getInitialPoint();

            // Create the final line
            Line finalLine = new Line(i.getX(), i.getY(), f.getX(), f.getY());
            finalLine.setStrokeWidth(2);
            finalLine.setStroke(javafx.scene.paint.Color.BLACK);
            canvasPane.getChildren().add(finalLine);

            // Create the association and add it to collections
            Association association = new Association(finalLine);
            associations.add(association);
            elementMap.put(finalLine, association);

            // Calculate the angle of the line (for placing text above it)
            double dx = f.getX() - i.getX();
            double dy = f.getY() - i.getY();
            double angle = Math.atan2(dy, dx);

            // Add text labels for multiplicities and association text
            double offsetX = 10 * Math.cos(angle + Math.PI / 2); // Perpendicular offset
            double offsetY = 10 * Math.sin(angle + Math.PI / 2); // Perpendicular offset

            // Multiplicities at the start and end
            Text startMultiplicityText = new Text(i.getX() + offsetX, i.getY() + offsetY, association.getStartMultiplicity().toString());
            Text endMultiplicityText = new Text(f.getX() + offsetX, f.getY() + offsetY, association.getEndMultiplicity().toString());

            // Association text in the middle
            double middleX = (i.getX() + f.getX()) / 2 + offsetX;
            double middleY = (i.getY() + f.getY()) / 2 + offsetY;
            Text associationText = new Text(middleX, middleY, association.getText());

            // Add texts to the canvas
            canvasPane.getChildren().add(startMultiplicityText);
            canvasPane.getChildren().add(endMultiplicityText);
            canvasPane.getChildren().add(associationText);

            // Reset initial point for the next association
            initialPoint = null;
        }
    }

    private Point getNearestCorner(VBox box, double x, double y) {
        Bounds bounds = box.localToScene(box.getBoundsInLocal());

        // Get the corners of the VBox
        Point topLeft = new Point(bounds.getMinX(), bounds.getMinY());
        Point topRight = new Point(bounds.getMaxX(), bounds.getMinY());
        Point bottomLeft = new Point(bounds.getMinX(), bounds.getMaxY());
        Point bottomRight = new Point(bounds.getMaxX(), bounds.getMaxY());

        // Compare distances to find the nearest corner
        Point[] corners = {topLeft, topRight, bottomLeft, bottomRight};
        Point nearestCorner = corners[0];
        double minDistance = calculateDistance(x, y, nearestCorner);

        for (Point corner : corners) {
            double distance = calculateDistance(x, y, corner);
            if (distance < minDistance) {
                minDistance = distance;
                nearestCorner = corner;
            }
        }

        return nearestCorner;
    }

    /**
     * Calculates the distance between two points.
     */
    private double calculateDistance(double x1, double y1, Point point) {
        double x2 = point.getX();
        double y2 = point.getY();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Gets the VBox containing a class at a given point.
     */
    private VBox getClassAtPoint(double x, double y) {
        for (Node node : canvasPane.getChildren()) {
            if (node instanceof VBox) {
                VBox classBox = (VBox) node;
                if (isWithinBounds(classBox, x, y)) {
                    return classBox;
                }
            }
        }
        return null;
    }

    /**
     * Gets the Class object associated with a given point.
     */
    private ClassDiagram.Class ClassAtPoint(double x, double y) {
        for (Node node : canvasPane.getChildren()) {
            if (node instanceof VBox) {
                VBox classBox = (VBox) node;
                if (isWithinBounds(classBox, x, y)) {
                    return (ClassDiagram.Class) elementMap.get(classBox);
                }
            }
        }
        return null;
    }




    // Check if a point (x, y) is within the bounds of a given node
    private boolean isWithinBounds(Node node, double x, double y) {
        return node.getBoundsInParent().contains(x, y);  // Check if point is inside node's bounds
    }

    // Handle mouse click on the canvas
    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        // Deselect the selected class if it's clicked elsewhere
        if (selectedClassBox != null && !isWithinBounds(selectedClassBox, x, y)) {
            deselectClassBox();
        }

        // Iterate through all elements on the canvas
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                Object element = entry.getValue();

                if (element instanceof ClassDiagram.Class) {
                    if (event.getClickCount() == 2) {
                        // Double-click to open the class details form
                        ClassDiagram.Class clazz = (ClassDiagram.Class) element;
                        showClassDetails(clazz);
                    } else {
                        // Single click to select the class
                        selectClassBox((VBox) node);
                    }
                    return;
                }
                else if (element instanceof  Interface) {
                    if (event.getClickCount() == 2) {
                        ClassDiagram.Interface clazz = (ClassDiagram.Interface) element;
                        showInterfaceDetails(clazz);
                    }
                    else {
                        // Single click to select the class
                        selectClassBox((VBox) node);
                    }
                    return;
                }
            }
        }

        // Handle tools for drawing new elements
        if (activeTool != null) {
            BiConsumer<Double, Double> drawAction = drawActions.get(activeTool);
            if (drawAction != null) {
                drawAction.accept(x, y);
            }
        }
    }

    public void showInterfaceDetails(Interface clazz){
        Stage detailStage = new Stage();

        // Main container for the form
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true); // Ensure the content resizes well

        // Class Name Editing
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);

        // Functions Section
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

        // Delete Button
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            classes.remove(clazz);
            elementMap.values().remove(clazz);
            redrawcanvas(); // Redraw the canvas after deletion
            detailStage.close(); // Close the detail window
        });
        detailBox.getChildren().add(deleteButton);

        // Submit Button to Apply Changes
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Apply changes to the original class instance
            clazz.setClassName(classNameField.getText());
            redrawcanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);

        // Show the form
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }


    private void trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
    }


    public void handleClassButtonClick() {activeTool = "Class";}

    public void handleAssociationButtonClick() {activeTool = "Association";}

    private void drawClass(double x, double y) {
        activeTool = null;

        Point initialPoint = new Point(x, y);
        ClassDiagram.Class myClass = new ClassDiagram.Class(initialPoint);

        double initialWidth = 120;

        VBox classBox = new VBox();
        classBox.setLayoutX(x);
        classBox.setLayoutY(y);
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");

        Label classNameLabel = new Label("Class"+ classes.size());
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
        elementMap.put(classBox, myClass); // Track element for double-click detection
    }



    private void showElementOptions(Object element) {

        if (element instanceof ClassDiagram.Class) {
            ClassDiagram.Class clazz = (ClassDiagram.Class) element;

                showClassDetails(clazz);


//            deleteButton.setOnAction(event -> {
//                classes.remove(clazz);
//                elementMap.remove(clazz);
//                redrawcanvas();
//            });




        }
    }

    private void showClassDetails(ClassDiagram.Class clazz) {
        Stage detailStage = new Stage();

        // Main container for the form
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true); // Ensure the content resizes well

        // Class Name Editing
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);

        // Attributes Section
        detailBox.getChildren().add(new Label("Attributes:"));
        VBox attributesBox = new VBox(5);
        updateAttributesBox(clazz, attributesBox);
        Button addAttributeButton = new Button("Add Attribute");
        addAttributeButton.setOnAction(e -> {
            Attribute newAttr = new Attribute("", "String"); // Default attribute
            clazz.addAttribute(newAttr);
            updateAttributesBox(clazz, attributesBox);
        });
        detailBox.getChildren().addAll(attributesBox, addAttributeButton);

        // Functions Section
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

        // Delete Button
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            classes.remove(clazz);
            elementMap.values().remove(clazz);
            redrawcanvas(); // Redraw the canvas after deletion
            detailStage.close(); // Close the detail window
        });
        detailBox.getChildren().add(deleteButton);

        // Submit Button to Apply Changes
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Apply changes to the original class instance
            clazz.setClassName(classNameField.getText());
            redrawcanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);

        // Show the form
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }



    private void updateAttributesBox(ClassDiagram.Class clazz, VBox attributesBox) {
        attributesBox.getChildren().clear();
        for (Attribute attribute : clazz.getAttributes()) {
            HBox attrBox = new HBox(5);

            TextField nameField = new TextField(attribute.getName());
            ComboBox<String> DataTypeBox = new ComboBox<>();
            DataTypeBox.getItems().addAll("String","Int","Double","Float","Boolean");
            for (ClassDiagram.Class cl : classes)
            {
                DataTypeBox.getItems().add(cl.getClassName());
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

            // Update attribute on field change
            nameField.textProperty().addListener((obs, oldText, newText) -> attribute.setName(newText));
            DataTypeBox.valueProperty().addListener((obs, oldText, newText) -> attribute.setDataType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setAccessModifier(newVal));
        }
    }

    private void updateFunctionsBox(ClassDiagram.Class clazz, VBox functionsBox) {
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);

            TextField nameField = new TextField(function.getName());
            TextField returnTypeField = new TextField(function.getReturnType());
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());

            // Manage parameters
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

            // Update function on field change
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.textProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }


    private void updateFunctionsBox(ClassDiagram.Interface clazz, VBox functionsBox) {
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);

            TextField nameField = new TextField(function.getName());
            TextField returnTypeField = new TextField(function.getReturnType());
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());

            // Manage parameters
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

            // Update function on field change
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.textProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }


    private void updateParametersBox(Function function, VBox parametersBox) {
        parametersBox.getChildren().clear();
        for (Attribute parameter : function.getAttributes()) {
            HBox paramBox = new HBox(5);

            TextField paramNameField = new TextField(parameter.getName());
            TextField paramTypeField = new TextField(parameter.getDataType());

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

            // Update parameter on field change
            paramNameField.textProperty().addListener((obs, oldText, newText) -> parameter.setName(newText));
            paramTypeField.textProperty().addListener((obs, oldText, newText) -> parameter.setDataType(newText));
        }
    }

    private double getMaxLabelWidth(VBox vbox) {
        double maxWidth = 0;
        for (Node node : vbox.getChildren()) {
            if (node instanceof Label) {
                maxWidth = Math.max(maxWidth, ((Label) node).getWidth());
            }
        }
        return maxWidth + 10;
    }

    public void redrawcanvas() {
        canvasPane.getChildren().clear();
        for (ClassDiagram.Class myClass : classes) {
            redrawClass(myClass);
        }
        for (ClassDiagram.Interface myInterface : interfaces) {
            reDrawInterface(myInterface);
        }
    }

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
    }

    private void drawComment(double x, double y) {
        activeTool = null;
        // Implement similar logic to track comments as elements
    }

    public void snapCanvas(){
        activeTool = null;
    }

    private void selectClassBox(VBox classBox) {
        if (selectedClassBox != null) {
            deselectClassBox();
        }
        selectedClassBox = classBox;
        classBox.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-padding: 5; -fx-background-color: #e0e0e0;");
    }

    private void deselectClassBox() {
        if (selectedClassBox != null) {
            selectedClassBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #e0e0e0;");
            selectedClassBox = null;
        }
    }

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

        elementMap.put(classBox, claz);
    }
}