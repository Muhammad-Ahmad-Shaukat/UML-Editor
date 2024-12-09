package com.boota.javaproject.UseCaseDiagram;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.io.Serializable;

public class DottedLineComponents implements Serializable {
    private Line line;
    private Text text;
    private Polygon arrowHead;

    public DottedLineComponents(Line line, Text text, Polygon arrowHead) {
        this.line = line;
        this.text = text;
        this.arrowHead = arrowHead;
    }

    public Line getLine() {
        return line;
    }

    public Text getText() {
        return text;
    }

    public Polygon getArrowHead() {
        return arrowHead;
    }
}

