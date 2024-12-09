package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;

public class Generalization implements Serializable {

    private Class startClass;
    private Class endClass;

    public Generalization(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
    }

    public Class getStartClass() {
        return startClass;
    }

    public Class getEndClass() {
        return endClass;
    }

    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }
}
