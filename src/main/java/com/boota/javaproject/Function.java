package com.boota.javaproject;

import java.util.ArrayList;

public class Function {
    String name;
    String returntype;
    ArrayList<Attribute> attributes=new ArrayList<>();

    public Function(String returntype, String name) {
        this.returntype = returntype;
        this.name = name;
    }

    public Function(String returntype, String name, ArrayList<Attribute> attributes) {
        this.returntype = returntype;
        this.name = name;
        this.attributes = attributes;
    }
}
