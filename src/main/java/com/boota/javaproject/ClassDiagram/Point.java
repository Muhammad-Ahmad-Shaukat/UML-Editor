package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents a point in a two-dimensional space with X and Y coordinates.
 * This class provides methods for getting and setting the coordinates,
 * as well as calculating the distance to another point.
 *
 * The Point class is serializable and can be used in contexts where
 * persistence or network transmission of point data is required.
 */
public class Point implements Serializable {
    private Double x;
    private Double y;

    /**
     * Creates a new Point with the specified X and Y coordinates.
     *
     * @param x the X coordinate of the point
     * @param y the Y coordinate of the point
     */
    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the X coordinate of this point.
     *
     * @return the X coordinate as a Double
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets the X coordinate of this point.
     *
     * @param x the new X coordinate of the point
     */
    public void setX(Double x) {
        this.x = x;
    }

    /**
     * Retrieves the Y coordinate of this point.
     *
     * @return the Y coordinate as a Double
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of this point.
     *
     * @param y the new Y coordinate of the point
     */
    public void setY(Double y) {
        this.y = y;
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * @param other the other point to calculate the distance to; must not be null
     * @return the Euclidean distance between this point and the other point
     * @throws IllegalArgumentException if the other point is null
     */
    public double distance(Point other) {
        if (other == null) {
            throw new IllegalArgumentException("Other point cannot be null");
        }
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Returns a string representation of this Point object in the format:
     * "<X>" followed by the X coordinate, "</X>", "<Y>" followed by the Y coordinate, and "</Y>".
     *
     * @return a string representation of this Point object, including the X and Y coordinates
     */
    @Override
    public String toString() {
        return "<X>" + x + "</X>" + "<Y>" + y + "</Y>";
    }
}