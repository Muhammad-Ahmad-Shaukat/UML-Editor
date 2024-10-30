package com.boota.demo;

public class Multiplicity {
    String first;
    String second;
    public Multiplicity(String first, String second) {
        this.first = first;
        this.second = second;
    }
    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getSecond() {
        return second;
    }
    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return first + ".." + second;
    }
}
