package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents a composite relationship between two class entities within a UML diagram.
 * A CompositeRelations object facilitates the definition of a relationship between two classes,
 * including the associated multiplicity, text for labeling, and the names of the related classes.
 * Composite relations are used to model a whole-part relationship, where the part depends on the existence of the whole.
 *
 * Attributes:
 * - startMultiplicity (Multiplicity): Represents the multiplicity on the start side of the relationship.
 * - endMultiplicity (Multiplicity): Represents the multiplicity on the end side of the relationship.
 * - text (String): A descriptive text or label for the relationship.
 * - startClass (Class): The class at the start (whole) of the relationship.
 * - endClass (Class): The class at the end (part) of the relationship.
 * - name (String): The name or identifier of the relationship.
 *
 * Constructors:
 * - CompositeRelations(Class startClass, Class endClass, String name):
 *   Initializes a relationship with the specified start class, end class, and name.
 * - CompositeRelations(Multiplicity startMultiplicity, Multiplicity endMultiplicity,
 *   String text, Class startClass, Class endClass, String name):
 *   Initializes a relationship with specified multiplicities, textual label, classes, and name.
 *
 * Key Methods:
 * - getName(): Retrieves the name of the relationship.
 * - getStartMultiplicity() / setStartMultiplicity(Multiplicity): Gets or sets the start multiplicity.
 * - getEndMultiplicity() / setEndMultiplicity(Multiplicity): Gets or sets the end multiplicity.
 * - getText() / setText(String): Gets or sets the descriptive text of the relationship.
 * - getStartClass() / setStartClass(Class): Gets or sets the start class in the relationship.
 * - getEndClass() / setEndClass(Class): Gets or sets the end class in the relationship.
 */
public class CompositeRelations implements Serializable {

    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private String text;
    Class startClass;
    Class endClass;
    String name;

    //Hello

    /**
     * Constructs a CompositeRelations object with the specified start class,
     * end class, and name.
     *
     * @param startClass the starting class for the composite relationship
     * @param endClass the ending class for the composite relationship
     * @param name the name of the composite relationship
     */
    public CompositeRelations(Class startClass, Class endClass,String name) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.name = name;
    }

    /**
     * Constructs an instance of CompositeRelations with the specified parameters.
     *
     * @param startMultiplicity the multiplicity at the start of the relationship
     * @param endMultiplicity the multiplicity at the end of the relationship
     * @param text textual representation or additional description of the relationship
     * @param startClass the starting class in the relationship
     * @param endClass the ending class in the relationship
     * @param name the name of the composite relation
     */
    public CompositeRelations(Multiplicity startMultiplicity, Multiplicity endMultiplicity, String text, Class startClass, Class endClass, String name) {
        this(startClass, endClass,name);
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
    }

    /**
     * Retrieves the name associated with this object.
     *
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the multiplicity value at the start of the composite relationship.
     *
     * @return the start multiplicity of the relationship, represented as a {@code Multiplicity} object
     */
    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    /**
     * Sets the multiplicity value at the start of the composite relationship.
     *
     * @param startMultiplicity the start multiplicity of the relationship, represented as a {@code Multiplicity} object
     */
    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    /**
     * Retrieves the multiplicity at the end of the composite relationship.
     *
     * @return the end multiplicity of the relationship, represented as a {@code Multiplicity} object
     */
    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    /**
     * Sets the multiplicity value at the end of the composite relationship.
     *
     * @param endMultiplicity the end multiplicity of the relationship, represented as a {@code Multiplicity} object
     */
    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    /**
     * Retrieves the text representing or describing the composite relationship.
     *
     * @return the text as a String
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text for this composite relationship.
     *
     * @param text the textual description or representation of the relationship
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the starting class associated with this composite relationship.
     *
     * @return the starting class of the relationship as a {@code Class} object
     */
    public Class getStartClass() {
        return startClass;
    }

    /**
     * Sets the starting class for the composite relationship.
     *
     * @param startClass the starting class in the composite relationship
     */
    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    /**
     * Retrieves the ending class associated with this composite relationship.
     *
     * @return the ending class of the relationship as a {@code Class} object
     */
    public Class getEndClass() {
        return endClass;
    }

    /**
     * Sets the ending class for the composite relationship.
     *
     * @param endClass the ending class in the composite relationship
     */
    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

}
