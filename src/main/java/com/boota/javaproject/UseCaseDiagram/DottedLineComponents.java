package com.boota.javaproject.UseCaseDiagram;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.io.Serializable;
/**
 * Represents the components of a dotted line used in diagrams or other representations.
 * The components include:
 * - A line (Line object) representing the main dotted line.
 * - An associated text (Text object) for labeling or additional information.
 * - An arrowhead (Polygon object) indicating directionality or emphasis.
 *
 * This class is serializable, allowing its instances to be saved and transported as needed.
 */
public class DottedLineComponents implements Serializable {
    private Line line;
    private Text text;
    private Polygon arrowHead;

    /**
     * Constructs an instance of DottedLineComponents, representing the components
     * of a dotted line typically used in diagrams. The components include a line,
     * associated text, and an optional arrowhead.
     *
     * @param line the Line object representing the base dotted line component
     * @param text the Text object providing any label or annotation for the line
     * @param arrowHead the Polygon object representing the arrowhead, typically
     *                  used to indicate direction; may be null if no arrowhead is required
     */
    public DottedLineComponents(Line line, Text text, Polygon arrowHead) {
        this.line = line;
        this.text = text;
        this.arrowHead = arrowHead;
    }

    /**
     * Retrieves the Line object representing the main component of the dotted line.
     *
     * @return the Line object associated with this instance.
     */
    public Line getLine() {
        return line;
    }

    /**
     * Retrieves the Text object associated with this instance.
     *
     * @return the Text object providing label or additional information.
     */
    public Text getText() {
        return text;
    }

    /**
     * Retrieves the arrowhead component for the dotted line.
     * The arrowhead is represented as a Polygon object and may
     * indicate directionality or emphasis in a diagram.
     *
     * @return the Polygon object representing the arrowhead, or null
     *         if no arrowhead is associated with the dotted line.
     */
    public Polygon getArrowHead() {
        return arrowHead;
    }
}

