package com.boota.javaproject;

public class UseCaseSystemBoundaryBox {
    Point initialPoint;
    Double length;
    Double width;
    String name;

    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width, String name) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        this.name = name;
    }

    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        name = "BoundaryBox";
    }

    public UseCaseSystemBoundaryBox(Point initialPoint) {
        this.initialPoint = initialPoint;
        name = "BoundaryBox";
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
