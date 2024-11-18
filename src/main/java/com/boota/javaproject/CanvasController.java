package com.boota.javaproject;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class CanvasController {

    private Point initialPoint;
    private Point finalPoint;

    @FXML
    private Pane canvasPane;

    private Canvas canvas;
    private GraphicsContext gc;
    private ArrayList<Class> classes = new ArrayList<>();
    private ArrayList<Association> associations = new ArrayList<>();

    private String activeTool = null;
    private Map<String, BiConsumer<Double, Double>> drawActions = new HashMap<>();
    private Map<Node, Object> elementMap = new HashMap<>();

    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);

        drawActions.put("Class", this::drawClass);
        drawActions.put("Comment", this::drawComment);

        canvasPane.setOnMouseMoved(this::trackMouseCoordinates);
        canvasPane.setOnMouseClicked(this::handleCanvasClick);
            }


    public void handleClassButtonClick() {activeTool = "Class";}

    public void handleAssociationButtonClick() {activeTool = "Association";}

    public void handleCommentButtonClick() {activeTool = "Comment";}

    private void trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
    }


    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (event.getClickCount() == 2) {
            handleDoubleClick(x, y);
        }
        else if (activeTool != null) {
            BiConsumer<Double, Double> drawAction = drawActions.get(activeTool);
            if (drawAction != null) {
                drawAction.accept(x, y);
            }
       }
    }

    private void handleDoubleClick(double x, double y) {
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                Object element = entry.getValue();
                showElementOptions(element);
                return;
            }
        }
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
        elementMap.put(classBox, myClass); // Track element for double-click detection
    }


    private boolean isWithinBounds(Node node, double x, double y) {
        return x >= node.getLayoutX() && x <= node.getLayoutX() + node.getBoundsInParent().getWidth() &&
                y >= node.getLayoutY() && y <= node.getLayoutY() + node.getBoundsInParent().getHeight();
    }

    private void showElementOptions(Object element) {

        if (element instanceof Class) {
            Class clazz = (Class) element;

                showClassDetails(clazz);



        }
    }

    private void showClassDetails(Object element) {
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(10);

        if (element instanceof Class) {
            Class clazz = (Class) element;

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

            // Submit Button to Apply Changes
            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                // Apply changes to the original class instance
                clazz.setClassName(classNameField.getText());
                redrawcanvas();
                detailStage.close();
            });
            detailBox.getChildren().add(submitButton);
        }

        detailStage.setScene(scene);
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

            // Update attribute on field change
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
        for (Class myClass : classes) {
            redrawClass(myClass);
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
}