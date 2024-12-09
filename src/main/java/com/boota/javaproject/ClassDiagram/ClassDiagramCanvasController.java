package com.boota.javaproject.ClassDiagram;

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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

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

    @FXML
    private void handleGeneralizationClick(){
        activeTool = "Generalization";
        deselectClassBox();
    }

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
    @FXML
    private void handleAssociationClick(){activeTool = "Association";
        deselectClassBox();}

    @FXML
    private void handleCompositionClick(){activeTool = "Composition";
        deselectClassBox();}

    public void handleClassButtonClick() {activeTool = "Class";
        deselectClassBox();}

    private void drawAssociation(Point initialPoint, Point finalPoint) {
        activeTool = null;
        Class startClass = getClassAtPoint(initialPoint);
        Class endClass = getClassAtPoint(finalPoint);
        Line line = null;
        if (startClass != null && endClass != null) {
            line = new Line();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            canvasPane.getChildren().add(line);
            Association association= new Association(startClass,endClass);
            AssociatedClass x= new AssociatedClass(startClass,"Association");
            AssociatedClass y= new AssociatedClass(endClass,"Association");
            startClass.addX(x);
            endClass.addX(y);
            associations.add(association);
            associationMap.put(line, association);
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
    private void updateAttributesBox(Class clazz, VBox attributesBox) {
        attributesBox.getChildren().clear();
        for (Attribute attribute : clazz.getAttributes()) {
            HBox attrBox = new HBox(5);
            TextField nameField = new TextField(attribute.getName());
            ComboBox<String> DataTypeBox = new ComboBox<>();
            DataTypeBox.getItems().addAll("String","Int","Double","Float","Boolean");
            for (Class cl : classes)
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
    private void updateFunctionsBox(Class clazz, VBox functionsBox) {
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
        Class startClass = getClassAtPoint(startPoint);
        Class endClass = getClassAtPoint(endPoint);
        Line line = null;
        if (startClass != null && endClass != null) {
            line = new Line();
            line.setStartX(association.getStartClass().getInitialPoint().getX());
            line.setStartY(association.getStartClass().getInitialPoint().getY());
            line.setEndX(association.getEndClass().getInitialPoint().getX());
            line.setEndY(association.getEndClass().getInitialPoint().getY());
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            canvasPane.getChildren().add(line);
        } else {
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
            return;
        }
        associations.add(association);
        associationMap.put(line, association);
    }
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
                showAlert("File Saved Bhai", "Java files generated in: " + subFolder.getAbsolutePath(), Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                showAlert("Error", "An error occurred while generating files: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Directory Selected", "Please select a directory to save the files.", Alert.AlertType.WARNING);
        }
    }

}