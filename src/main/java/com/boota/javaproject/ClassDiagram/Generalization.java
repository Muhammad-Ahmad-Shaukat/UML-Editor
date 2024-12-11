package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents a generalization relationship between two classes in a class diagram.
 * A generalization is a directed relationship, typically represented as an arrow
 * from a more specific subclass to a more general superclass.
 *
 * This class holds references to the starting and ending classes that participate
 * in the generalization. The starting class (subclass) extends or inherits
 * from the ending class (superclass).
 *
 * Features include:
 * - Managing the starting (subclass) and ending (superclass) classes.
 * - Setting and retrieving the start and end classes.
 *
 * Implements the Serializable interface for potential use in scenarios where
 * serialization of the generalization relationship is required.
 */
public class Generalization implements Serializable {

    private Class startClass;
    private Class endClass;

    /**
     * Constructs a Generalization object representing a relationship between two classes.
     * The relationship is typically directed, with the starting class representing
     * the subclass and the ending class representing the superclass.
     *
     * @param startClass the class from which the generalization starts, representing the subclass
     * @param endClass the class at which the generalization ends, representing the superclass
     */
    public Generalization(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
    }

    /**
     * Retrieves the starting class (subclass) in the generalization relationship.
     *
     * @return the starting class (subclass) involved in this generalization relationship.
     */
    public Class getStartClass() {
        return startClass;
    }

    /**
     * Retrieves the ending class in the generalization relationship.
     * The ending class typically represents the superclass
     * or the more general class in the relationship.
     *
     * @return the ending class of the generalization relationship
     */
    public Class getEndClass() {
        return endClass;
    }

    /**
     * Sets the ending class in the generalization relationship.
     * The ending class typically represents the superclass or the more general class.
     *
     * @param endClass the class to be set as the ending class in the generalization relationship
     */
    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    /**
     * Sets the starting class (subclass) in the generalization relationship.
     *
     * @param startClass the class from which the generalization starts, representing the subclass
     */
    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }
}
