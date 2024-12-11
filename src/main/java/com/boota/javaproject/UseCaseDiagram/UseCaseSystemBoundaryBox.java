package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;

import java.io.Serializable;
/**
 * Represents a system boundary box in a Use Case diagram.
 * A system boundary box is used to visually enclose the use cases
 * that belong to the system being modeled. It includes details
 * such as the initial position of the boundary box, its dimensions
 * (length and width), and an optional name to label the box.
 *
 * This class is serializable, allowing it to be transferred or
 * stored as part of a diagram representation.
 */
public class UseCaseSystemBoundaryBox implements Serializable {
    Point initialPoint;
    Double length;
    Double width;
    String name;


    /**
     * Constructs a UseCaseSystemBoundaryBox with the specified initial point, dimensions, and name.
     *
     * @param initialPoint the initial position of the system boundary box in the diagram; must not be null
     * @param length the length of the system boundary box; must not be null
     * @param width the width of the system boundary box; must not be null
     * @param name the name or label of the system boundary box; must not be null
     */
    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width, String name) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        this.name = name;
    }

    /**
     * Constructs a UseCaseSystemBoundaryBox with the specified initial position, length, and width.
     * The name of the boundary box is initialized to "BoundaryBox".
     *
     * @param initialPoint the initial position of the boundary box in the diagram; must not be null
     * @param length the length of the boundary box; must not be null
     * @param width the width of the boundary box; must not be null
     */
    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        name = "BoundaryBox";
    }

    /**
     * Constructs a UseCaseSystemBoundaryBox with the specified initial position.
     * The name of the boundary box is initialized to "BoundaryBox".
     *
     * @param initialPoint the initial position of the boundary box in the diagram; must not be null
     */
    public UseCaseSystemBoundaryBox(Point initialPoint) {
        this.initialPoint = initialPoint;
        name = "BoundaryBox";
    }

    /**
     * Retrieves the initial position of the system boundary box in the diagram.
     *
     * @return the initial position as a Point object
     */
    public Point getInitialPoint() {
        return initialPoint;
    }

    /**
     * Sets the initial position of the system boundary box.
     *
     * @param initialPoint the new initial position of the boundary box; must not be null
     */
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    /**
     * Retrieves the length of the UseCaseSystemBoundaryBox.
     *
     * @return the length of the boundary box as a Double.
     */
    public Double getLength() {
        return length;
    }

    /**
     * Sets the length of the UseCaseSystemBoundaryBox.
     *
     * @param length the new length of the system boundary box; must not be null
     */
    public void setLength(Double length) {
        this.length = length;
    }

    /**
     * Retrieves the width of the UseCaseSystemBoundaryBox.
     *
     * @return the width of the boundary box as a Double.
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the width of the UseCaseSystemBoundaryBox.
     *
     * @param width the new width of the system boundary box; must not be null
     */
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Retrieves the name of the UseCaseSystemBoundaryBox.
     *
     * @return the name of the system boundary box as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the system boundary box.
     *
     * @param name the new name to assign to the system boundary box; must not be null.
     */
    public void setName(String name) {
        this.name = name;
    }
}
