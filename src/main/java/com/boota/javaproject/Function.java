package com.boota.javaproject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class Function {
    String name;
    String returnType;
    ArrayList<Attribute> attributes;
    String accessModifier;

    public Function(String returnType, String name) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        accessModifier = "public";
    }

    public Function(String returnType, String name, String accessModifier) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        this.accessModifier = accessModifier;
    }

    public Function(String returnType, String name, ArrayList<Attribute> attributes , String accessModifier) {
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.name = name;
        this.attributes = attributes;
    }

    public Function(String returnType, String name, Attribute attribute) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        this.attributes.add(attribute);
        accessModifier = "public";
    }

    public Function(String returnType, String name, Attribute attribute, String accessModifier) {
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.name = name;
        attributes = new ArrayList<>();
        this.attributes.add(attribute);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    @Override
    public String toString() {
        String attributesString = attributes.stream()
                .map(attr -> attr.getName() + " " + attr.getDataType())
                .collect(Collectors.joining(", "));
        String prefix;
        switch (accessModifier.toLowerCase()) {
            case "private":
                prefix = "-";
                break;
            case "public":
                prefix = "+";
                break;
            default:
                prefix = "#";
        }

        return prefix + name + "(" + attributesString + ") : " + returnType;
    }
}
