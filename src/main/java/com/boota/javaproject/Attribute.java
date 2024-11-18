package com.boota.javaproject;

public class Attribute {
    private String name;
    private String dataType;
    private String accessModifier;

    public Attribute(String name, String dataType, String accessModifier) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = accessModifier;
    }

    public Attribute(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = "public";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    @Override
    public String toString() {
        switch (accessModifier.toLowerCase()) {
            case "private":
                return "-" + name + " : " + dataType;
            case "public":
                return "+" + name + " : " + dataType;
            default:
                return "#" + name + " : " + dataType;
        }
    }

    public void print() {
        System.out.println(name + " : " + dataType);
    }
}
