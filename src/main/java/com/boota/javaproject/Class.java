package com.boota.javaproject;

import java.util.ArrayList;

public class Class {
    String className;
    ArrayList<Attribute> attributes;
    ArrayList<Function> functions;
    Point initialPoint;
    Double length;
    Double width;

    public Class(String className, Point initialPoint) {
        this.className = className;
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
    }

    public Class(Point initialPoint) {
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
        className = "Class";
    }

    public Class(String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        this.className = className;
        this.attributes = attributes;
        this.functions = functions;
        this.initialPoint = initialPoint;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    void addFunction(Function function) {
        functions.add(function);
    }

    void removeFunction(Function function) {
        functions.remove(function);
    }

    void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    String returnAttribute() {
        StringBuilder result = new StringBuilder();
        for (Attribute attribute : attributes) {
            result.append(attribute.toString()).append("\n");
        }
        return result.toString();
    }

    String returnFunction() {
        StringBuilder result = new StringBuilder();
        for (Function function : functions) {
            result.append(function.toString()).append("\n");
        }
        return result.toString();
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
}
