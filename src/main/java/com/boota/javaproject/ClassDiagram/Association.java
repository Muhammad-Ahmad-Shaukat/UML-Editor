package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents an association between different elements, such as classes or interfaces,
 * along with optional attributes like multiplicities and descriptive text.
 * An association can exist between two classes, a class and an interface, or two interfaces.
 */
public class Association implements Serializable {

    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private String text;
    private Class startClass;
    private Class endClass;
    private Interface startInterface;
    private Interface endInterface;

    /**
     * Constructs an association between two classes.
     *
     * @param startClass the starting class of the association
     * @param endClass the ending class of the association
     */
    // Constructor for two classes
    public Association(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
    }

    /**
     * Constructs an association between a class and an interface.
     *
     * @param startClass the starting class of the association
     * @param endInterface the ending interface of the association
     */
    // Constructor for class to interface
    public Association(Class startClass, Interface endInterface) {
        this.startClass = startClass;
        this.endInterface = endInterface;
    }

    /**
     * Constructs an association from an interface to a class.
     *
     * @param startInterface the starting interface of the association
     * @param endClass the ending class of the association
     */
    // Constructor for interface to class
    public Association(Interface startInterface, Class endClass) {
        this.startInterface = startInterface;
        this.endClass = endClass;
    }

    /**
     * Constructs an association between two interfaces.
     *
     * @param startInterface the starting interface of the association
     * @param endInterface the ending interface of the association
     */
    // Constructor for two interfaces
    public Association(Interface startInterface, Interface endInterface) {
        this.startInterface = startInterface;
        this.endInterface = endInterface;
    }

    /**
     * Constructs an association between two classes with specified multiplicities and a textual description.
     *
     * @param startMultiplicity the multiplicity of the starting class
     * @param endMultiplicity the multiplicity of the ending class
     * @param text the description of the association
     * @param startClass the starting class of the association
     * @param endClass the ending class of the association
     */
    // Constructor with multiplicities and text
    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, String text, Class startClass, Class endClass) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
        this.startClass = startClass;
        this.endClass = endClass;
    }

    /**
     * Constructs an association between two classes with specified multiplicities.
     *
     * @param startMultiplicity the multiplicity from the starting class
     * @param endMultiplicity the multiplicity to the ending class
     * @param startClass the starting class of the association
     * @param endClass the ending class of the association
     */
    // Constructor with multiplicities only
    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, Class startClass, Class endClass) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
    }

    /**
     * Retrieves the multiplicity of the starting element in the association.
     *
     * @return the {@code Multiplicity} of the starting element, which defines
     *         the range or constraints for the association's starting point.
     */
    // Getters and setters for all fields
    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    /**
     * Sets the multiplicity of the start point in the association.
     *
     * @param startMultiplicity the multiplicity to assign to the start point
     */
    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    /**
     * Retrieves the multiplicity of the ending element in the association.
     *
     * @return the {@code Multiplicity} of the ending element, which specifies
     *         the range or constraints for the association's endpoint.
     */
    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    /**
     * Sets the multiplicity of the endpoint in the association.
     *
     * @param endMultiplicity the multiplicity to assign to the endpoint, representing
     *                        the range or constraints of the association's end point
     */
    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    /**
     * Retrieves the textual description of the association.
     *
     * @return the textual representation of the association
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the textual description of the association.
     *
     * @param text the textual description to set for the association
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the starting class of the association.
     *
     * @return the {@code Class} representing the starting point of the association.
     */
    public Class getStartClass() {
        return startClass;
    }

    /**
     * Sets the starting class of the association.
     *
     * @param startClass the starting class of the association
     */
    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    /**
     * Retrieves the ending class of the association.
     *
     * @return the {@code Class} representing the endpoint of the association.
     */
    public Class getEndClass() {
        return endClass;
    }

    /**
     * Sets the ending class of the association.
     *
     * @param endClass the class to set as the endpoint of the association
     */
    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    /**
     * Retrieves the starting interface of the association.
     *
     * @return the {@code Interface} representing the starting point of the association.
     */
    public Interface getStartInterface() {
        return startInterface;
    }

    /**
     * Sets the starting interface of the association.
     *
     * @param startInterface the interface to set as the starting point of the association
     */
    public void setStartInterface(Interface startInterface) {
        this.startInterface = startInterface;
    }

    /**
     * Retrieves the ending interface of the association.
     *
     * @return the {@code Interface} representing the endpoint of the association.
     */
    public Interface getEndInterface() {
        return endInterface;
    }

    /**
     * Sets the ending interface of the association.
     *
     * @param endInterface the interface to set as the endpoint of the association
     */
    public void setEndInterface(Interface endInterface) {
        this.endInterface = endInterface;
    }
}
