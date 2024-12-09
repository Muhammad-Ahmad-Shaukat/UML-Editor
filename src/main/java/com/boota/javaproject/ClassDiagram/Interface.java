package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
import java.util.ArrayList;

public class Interface implements Serializable {
    public String className;
    public ArrayList<Function> functions;
    public Point initialPoint;
    public ArrayList<Class> InheritedClasses;

    public Interface(String className, Point initialPoint) {
        this.className = className;
        this.initialPoint = initialPoint;
        functions = new ArrayList<>();
    }

    public Interface(Point initialPoint) {
        this.initialPoint = initialPoint;
        functions = new ArrayList<>();
        className = "Class";
    }

    public Interface(String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        this.className = className;
        this.functions = functions;
        this.initialPoint = initialPoint;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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


    public void addFunction(Function function) {
        functions.add(function);
    }

    public void removeFunction(Function function) {
        functions.remove(function);
    }


    public String returnFunction() {
        StringBuilder result = new StringBuilder();
        for (Function function : functions) {
            result.append(function.toString()).append("\n");
        }
        return result.toString();
    }
}
