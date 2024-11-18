package com.boota.javaproject;

public class UseCase {
    Point initialpoint;
    String name;

    public UseCase(Point initialpoint) {
        this.initialpoint = initialpoint;
        name = "Use Case";
    }

    public UseCase(Point initialpoint, String name) {
        this.initialpoint = initialpoint;
        this.name = name;
    }

    public Point getInitialpoint() {
        return initialpoint;
    }

    public void setInitialpoint(Point initialpoint) {
        this.initialpoint = initialpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
