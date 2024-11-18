package com.boota.javaproject;

public class Multiplicity {
    Double start;
    Double end;

    public Multiplicity(Double start, Double end) {
        this.start = start;
        this.end = end;
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Double getEnd() {
        return end;
    }

    public void setEnd(Double end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }
}
