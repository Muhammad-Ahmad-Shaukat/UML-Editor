package com.boota.javaproject;


public class Association {
    Point start;
    Point end;
    Multiplicity startMultiplicity;
    Multiplicity endMultiplicity;
    String text;
    Class initialClass;
    Class finalClass;

        this.start = start;
        this.end = end;
    }

    public Association(Point start, Point end, Multiplicity startMultiplicity,
                       Multiplicity endMultiplicity, String text, Class initialClass,
                       Class finalClass) {
        this.start = start;
        this.end = end;
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
        this.initialClass = initialClass;
        this.finalClass = finalClass;
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

    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
