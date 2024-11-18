package com.boota.javaproject;

public class Line {

    Point start;
    Point end;
    Class initialClass;
    Class finalClass;

    public Line(Class initialClass, Class finalClass, Point end, Point start) {
        this.initialClass = initialClass;
        this.finalClass = finalClass;
        this.end = end;
        this.start = start;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Class getInitialClass() {
        return initialClass;
    }

    public void setInitialClass(Class initialClass) {
        this.initialClass = initialClass;
    }

    public Class getFinalClass() {
        return finalClass;
    }

    public void setFinalClass(Class finalClass) {
        this.finalClass = finalClass;
    }
}
