package ClassDiagram;

import javafx.fxml.FXML;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;


public class ClassDiagramCanvasController {

    @FXML
    private Pane canvasPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private ArrayList<ClassDiagram.Class> classes = new ArrayList<>();
    private VBox selectedClassBox = null;
    private String activeTool = null;
    private Map<String, BiConsumer<Double, Double>> drawActions = new HashMap<>();
    private Map<Node, Object> elementMap = new HashMap<>();
    private ArrayList<Interface> interfaces = new ArrayList<>();
    private Node selectedNode = null;
    private Point initialMousePosition = null;
    private Line tempLine = null;
    private Point initialPoint = null;
    private ArrayList<Association> associations = new ArrayList<>();
    private ArrayList<CompositeRelations> aggregations = new ArrayList<>();
    private ArrayList<Generalization> generalizations = new ArrayList<>();

    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);
        drawActions.put("Class", this::drawClass);
        drawActions.put("Interface", this::drawInterface);
        canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
        canvasPane.setOnMouseClicked(this::handleCanvasClick);
        canvasPane.setOnMousePressed(this::handleMousePressed);
        canvasPane.setOnMouseDragged(this::handleMouseDragged);
        canvasPane.setOnMouseReleased(this::handleMouseReleased);
    }

    public void interfacePressed(){
        activeTool = "Interface";
    }
    private void trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
    }

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
            if (element instanceof ClassDiagram.Class) {
                Class clazz = (ClassDiagram.Class) element;
                clazz.getInitialPoint().setX(clazz.getInitialPoint().getX() + deltaX);
                clazz.getInitialPoint().setY(clazz.getInitialPoint().getY() + deltaY);
            }
            else if (element instanceof Interface) {
                Interface clazz = (Interface) element;
                clazz.getInitialPoint().setX(clazz.getInitialPoint().getX() + deltaX);
                clazz.getInitialPoint().setY(clazz.getInitialPoint().getY() + deltaY);
            }
            reDrawCanvas();
            initialMousePosition.setX(event.getX());
            initialMousePosition.setY(event.getY());
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (tempLine != null && activeTool=="Association") {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawAssociation(initialPoint, finalPoint);
        } else if (tempLine != null && activeTool=="Aggregation") {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawAggregation(initialPoint, finalPoint);
        }else if (tempLine != null && activeTool=="Composition") {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawComposition(initialPoint, finalPoint);
        } else if (tempLine != null && activeTool=="Generalization") {
            canvasPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            drawGeneralization(initialPoint, finalPoint);
        }
        selectedNode = null;
        initialMousePosition = null;
    }

    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        for (Association association : associations) {
            if (isNearLine(association.getLine(), x, y)) {
                if (event.getClickCount() == 2) {
                    showAssociationDetailsForm(association);
                }
                return;
            }
        }
        for (CompositeRelations aggregation : aggregations) {
            if (isNearLine(aggregation.getLine(), x, y)) {
                showAggregationDetailsForm(aggregation);
            }
        }
        if (selectedClassBox != null && !isWithinBounds(selectedClassBox, x, y)) {
            deselectClassBox();
        }
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                Object element = entry.getValue();
                if (element instanceof ClassDiagram.Class) {
                    if (event.getClickCount() == 2) {
                        ClassDiagram.Class clazz = (ClassDiagram.Class) element;
                        showClassDetails(clazz);
                    } else {
                        selectClassBox((VBox) node);
                    }
                    return;
                } else if (element instanceof Interface) {
                    if (event.getClickCount() == 2) {
                        ClassDiagram.Interface clazz = (ClassDiagram.Interface) element;
                        showInterfaceDetails(clazz);
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

    @FXML
    private void handleGeneralizationClick(){
        activeTool = "Generalization";
    }

    public void reDrawCanvas() {
        canvasPane.getChildren().clear();

        ArrayList<Association> tempAssociation = new ArrayList<>(associations);
        ArrayList<Class> tempClasses = new ArrayList<>(classes);
        ArrayList<Interface> tempInterfaces = new ArrayList<>(interfaces);
        ArrayList<CompositeRelations> tempAggregations = new ArrayList<>(aggregations);
        ArrayList<Generalization> tempGeneral = new ArrayList<>(generalizations);
        classes.clear();
        associations.clear();
        interfaces.clear();

        for (Class myClass : tempClasses) {
            redrawClass(myClass);
        }
        for (Interface myInterface : tempInterfaces) {
            reDrawInterface(myInterface);
        }
        for (CompositeRelations myAggregation : tempAggregations) {
            redrawAggregation(myAggregation);
            if (myAggregation.getText() != null && !myAggregation.getText().isEmpty()) {
                Text aggregationText = new Text(myAggregation.getText());
                aggregationText.setX((myAggregation.getLine().getStartX() + myAggregation.getLine().getEndX()) / 2);
                aggregationText.setY((myAggregation.getLine().getStartY() + myAggregation.getLine().getEndY()) / 2 - 10);
                canvasPane.getChildren().add(aggregationText);
            }
            if (myAggregation.getStartMultiplicity() != null) {
                Text startMultiplicityText = new Text(myAggregation.getStartMultiplicity().toString());
                startMultiplicityText.setX(myAggregation.getLine().getStartX() - 15);
                startMultiplicityText.setY(myAggregation.getLine().getStartY() - 5);
                canvasPane.getChildren().add(startMultiplicityText);
            }
            if (myAggregation.getEndMultiplicity() != null) {
                Text endMultiplicityText = new Text(myAggregation.getEndMultiplicity().toString());
                endMultiplicityText.setX(myAggregation.getLine().getEndX() + 5);
                endMultiplicityText.setY(myAggregation.getLine().getEndY() - 5);
                canvasPane.getChildren().add(endMultiplicityText);
            }
        }
        for (Association a : tempAssociation) {
            redrawAssociation(a);
            if (a.getText() != null && !a.getText().isEmpty()) {
                Text associationText = new Text(a.getText());
                associationText.setX((a.getLine().getStartX() + a.getLine().getEndX()) / 2);
                associationText.setY((a.getLine().getStartY() + a.getLine().getEndY()) / 2 - 10);
                canvasPane.getChildren().add(associationText);
            }
            if (a.getStartMultiplicity() != null) {
                Text startMultiplicityText = new Text(a.getStartMultiplicity().toString());
                startMultiplicityText.setX(a.getLine().getStartX() - 15);
                startMultiplicityText.setY(a.getLine().getStartY() - 5);
                canvasPane.getChildren().add(startMultiplicityText);
            }
            if (a.getEndMultiplicity() != null) {
                Text endMultiplicityText = new Text(a.getEndMultiplicity().toString());
                endMultiplicityText.setX(a.getLine().getEndX() + 5);
                endMultiplicityText.setY(a.getLine().getEndY() - 5);
                canvasPane.getChildren().add(endMultiplicityText);
            }
        }
        for (Generalization g : tempGeneral) {
            redrawGeneralization(g);
        }
    }


    @FXML
    private void handleAssociationClick(){activeTool = "Association";}

    @FXML
    private void handleCompositionClick(){activeTool = "Composition";}

    public void handleClassButtonClick() {activeTool = "Class";}

    private void drawAssociation(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass != null && endClass != null) {
            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(2.0);
            canvasPane.getChildren().add(line);
            Association association = new Association(startClass, endClass);
            association.setLine(line);
            associations.add(association);
        } else {
            showWarning("Association Error", "Both endpoints must be inside a class.");
        }
    }
    @FXML
    private void handleAggregationClick(){activeTool = "Aggregation";}

    private void drawAggregation(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass != null && endClass != null) {
            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX() - 5, endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(2.0);
            Polygon diamond = createDiamond(
                    endClass.getInitialPoint(),
                    15,
                    7
            );
            diamond.setStrokeWidth(2.0);
            diamond.setStroke(Color.BLACK);
            diamond.setFill(Color.TRANSPARENT);
            canvasPane.getChildren().addAll(line, diamond);
            CompositeRelations aggregation = new CompositeRelations(startClass, endClass, "Aggregation");
            aggregation.setLine(line);
            aggregation.setDiamond(diamond);
            aggregations.add(aggregation);
        } else {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
        }
    }

    public void drawGeneralization(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass != null && endClass != null) {
            Line line = new Line();
            line.getStrokeDashArray().addAll(10.0, 5.0);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2.0);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            Polygon arrowHead = new Polygon();
            arrowHead.setFill(Color.TRANSPARENT);
            arrowHead.setStroke(Color.BLACK);
            arrowHead.setStrokeWidth(2.0);
            Generalization generalization = new Generalization(startClass, endClass, line, arrowHead);
            generalizations.add(generalization);
            generalization.updateLine();
            canvasPane.getChildren().addAll(line, arrowHead);
        } else {
            showWarning("Generalization Error", "Both endpoints must be inside a class.");
        }
    }



    public void drawComposition(Point initialPoint, Point finalPoint){
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        if (startClass != null && endClass != null) {
            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX() - 5, endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(2.0);
            Polygon diamond = createDiamond(
                    endClass.getInitialPoint(),
                    15,
                    7
            );
            diamond.setStrokeWidth(2.0);
            diamond.setStroke(Color.BLACK);
            diamond.setFill(Color.BLACK);
            canvasPane.getChildren().addAll(line, diamond);
            CompositeRelations aggregation = new CompositeRelations(startClass, endClass, "Composition");
            aggregation.setLine(line);
            aggregation.setDiamond(diamond);
            aggregations.add(aggregation);
        } else {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
        }
    }

    private Polygon createDiamond(Point position, double width, double height) {
        Polygon diamond = new Polygon();
        double centerX = position.getX();
        double centerY = position.getY();
        double x1 = centerX - height;
        double y1 = centerY;
        double x2 = centerX;
        double y2 = centerY - (width / 2);
        double x3 = centerX + height;
        double y3 = centerY;
        double x4 = centerX;
        double y4 = centerY + (width / 2);
        diamond.getPoints().addAll(
                x1, y1,
                x2, y2,
                x3, y3,
                x4, y4
        );
        return diamond;
    }

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
        elementMap.put(classBox, myClass);
    }

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

    private boolean isNearLine(Line line, double x, double y) {
        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());
        Point2D point = new Point2D(x, y);
        return point.distance(start) + point.distance(end) - start.distance(end) < 5;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ClassDiagram.Class getClassAtPoint(Point point) {
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (node instanceof VBox && isWithinBounds(node, point.getX(), point.getY())) {
                Object element = entry.getValue();
                if (element instanceof ClassDiagram.Class) {
                    return (ClassDiagram.Class) element;
                }
            }
        }
        return null;
    }

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

    private boolean isWithinBounds(Node node, double x, double y) {
        return node.getBoundsInParent().contains(x, y);
    }

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

    private void showClassDetails(ClassDiagram.Class clazz) {
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


    private void updateFunctionsBox(ClassDiagram.Interface clazz, VBox functionsBox) {
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

    private void redrawAssociation(Association association) {
        Point startPoint = association.getStartClass().getInitialPoint();
        Point endPoint = association.getEndClass().getInitialPoint();
        ClassDiagram.Class startClass = getClassAtPoint(startPoint);
        ClassDiagram.Class endClass = getClassAtPoint(endPoint);
        if (startClass != null && endClass != null) {
            Line line = association.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            if (!canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().add(line);
            }
        } else {
            Line line = association.getLine();
            if (canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().remove(line);
            }
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
            return;
        }
        associations.add(association);
    }

    private void redrawGeneralization(Generalization generalization) {
        Point startPoint = generalization.getStartClass().getInitialPoint();
        Point endPoint = generalization.getEndClass().getInitialPoint();
        ClassDiagram.Class startClass = getClassAtPoint(startPoint);
        ClassDiagram.Class endClass = getClassAtPoint(endPoint);

        if (startClass != null && endClass != null) {
            // Update the line
            Line line = generalization.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());

            // Update the arrowhead
            generalization.updateLine(); // This will also update the arrowhead position

            // Add the line and arrowhead to the canvas if not already present
            if (!canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().add(line);
            }

            Polygon arrowHead = generalization.getArrowHead();
            if (!canvasPane.getChildren().contains(arrowHead)) {
                canvasPane.getChildren().add(arrowHead);
            }
        } else {
            // Remove from canvas if the classes are no longer valid
            Line line = generalization.getLine();
            Polygon arrowHead = generalization.getArrowHead();

            if (canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().remove(line);
            }
            if (canvasPane.getChildren().contains(arrowHead)) {
                canvasPane.getChildren().remove(arrowHead);
            }
            showWarning("Redraw Error", "One or both classes in the generalization no longer exist.");
        }
    }


    private void redrawAggregation(CompositeRelations aggregation) {
        Point startPoint = aggregation.getStartClass().getInitialPoint();
        Point endPoint = aggregation.getEndClass().getInitialPoint();
        Class startClass = getClassAtPoint(startPoint);
        Class endClass = getClassAtPoint(endPoint);
        if (startClass != null && endClass != null) {
            Line line = aggregation.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            Polygon diamond = aggregation.getDiamond();
            updateDiamondPosition(diamond, endClass.getInitialPoint(), 10, 6);
            if (!canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().add(line);
            }
            if (!canvasPane.getChildren().contains(diamond)) {
                canvasPane.getChildren().add(diamond);
            }
        } else {
            Line line = aggregation.getLine();
            Polygon diamond = aggregation.getDiamond();
            if (canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().remove(line);
            }
            if (canvasPane.getChildren().contains(diamond)) {
                canvasPane.getChildren().remove(diamond);
            }
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
        }
    }

    public void redrawComposition(CompositeRelations aggregation){
        Point startPoint = aggregation.getStartClass().getInitialPoint();
        Point endPoint = aggregation.getEndClass().getInitialPoint();
        Class startClass = getClassAtPoint(startPoint);
        Class endClass = getClassAtPoint(endPoint);
        if (startClass != null && endClass != null) {
            Line line = aggregation.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            Polygon diamond = aggregation.getDiamond();
            updateDiamondPosition(diamond, endClass.getInitialPoint(), 10, 6);
            if (!canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().add(line);
            }
            if (!canvasPane.getChildren().contains(diamond)) {
                canvasPane.getChildren().add(diamond);
            }
        } else {
            Line line = aggregation.getLine();
            Polygon diamond = aggregation.getDiamond();
            if (canvasPane.getChildren().contains(line)) {
                canvasPane.getChildren().remove(line);
            }
            if (canvasPane.getChildren().contains(diamond)) {
                canvasPane.getChildren().remove(diamond);
            }
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
        }
    }

    private void updateDiamondPosition(Polygon diamond, Point position, double width, double height) {
        double centerX = position.getX();
        double centerY = position.getY();

        // Diamond vertices
        double x1 = centerX - height;       // Left point
        double y1 = centerY;
        double x2 = centerX;                // Top point
        double y2 = centerY - (width / 2);
        double x3 = centerX + height;       // Right point
        double y3 = centerY;
        double x4 = centerX;                // Bottom point
        double y4 = centerY + (width / 2);

        diamond.getPoints().setAll(
                x1, y1,
                x2, y2,
                x3, y3,
                x4, y4
        );
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
        classes.add(claz);
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
        interfaces.add(claz);
    }

    private void showAssociationDetailsForm(Association association) {
        Dialog<ButtonType> dialog = new Dialog<>();
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
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
        }
    }
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
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
        }
    }

    @FXML
    private void clearCanvas(){
        canvasPane.getChildren().clear();
        classes.clear();
        elementMap.clear();
        interfaces.clear();
        associations.clear();
        aggregations.clear();
    }
}