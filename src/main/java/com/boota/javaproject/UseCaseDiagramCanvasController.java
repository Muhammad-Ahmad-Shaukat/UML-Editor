package com.boota.javaproject;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class UseCaseDiagramCanvasController {

    @FXML
    private Pane canvasPane;

    private Canvas canvas;
    private GraphicsContext gc;

    private String activeTool = null;

    @FXML
    public void initialize() {
        canvas = new Canvas(canvasPane.getWidth(), canvasPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        canvasPane.getChildren().add(canvas);

    }




    public void handleActorClick(){

    }
}
