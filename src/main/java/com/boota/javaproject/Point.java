package com.boota.javaproject;

public class Point {
    Double x;
    Double y;

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public double distance(Point other) {
        if (other == null) {
            throw new IllegalArgumentException("Other point cannot be null");
        }
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}
