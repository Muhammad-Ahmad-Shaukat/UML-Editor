package com.boota.javaproject.ClassDiagram;
/**
 * Represents an association between classes or interfaces along with the relation type
 * and optional multiplicity.
 * This class is used to capture metadata about associations and relationships such as
 * inheritance, implementation, or general relationships between classes and interfaces.
 */
public class AssociatedClass {
    private Class name;
    private Multiplicity multiplicity;
    private Interface associatedinterface;
    String relation;


    /**
     * Constructs an AssociatedClass object that represents an association between a class,
     * a multiplicity, and a relation type.
     *
     * @param className the Class object representing the associated class.
     * @param multiplicity the Multiplicity object representing the association's multiplicity.
     * @param relation the type of relation (e.g., inheritance, implementation, association).
     */
    public AssociatedClass(Class className, Multiplicity multiplicity, String relation) {
        this.name = className;
        this.multiplicity = multiplicity;
        this.relation = relation;
    }

    /**
     * Constructs an AssociatedClass instance that represents a relationship between
     * a specific class and an interface.
     *
     * @param name the class involved in the association
     * @param associatedinterface the interface associated with the given class
     */
    public AssociatedClass(Class name, Interface associatedinterface) {
        this.name = name;
        this.associatedinterface = associatedinterface;
    }

    /**
     * Constructs an AssociatedClass instance that represents a relationship between
     * a specific class, an interface, and a relation type.
     *
     * @param relation the type of relation (e.g., inheritance, implementation, association)
     * @param associatedinterface the interface associated with the given class
     * @param name the class involved in the association
     */
    public AssociatedClass(String relation, Interface associatedinterface, Class name) {
        this.relation = relation;
        this.associatedinterface = associatedinterface;
        this.name = name;
    }

    /**
     * Constructs an AssociatedClass instance that represents a relationship
     * between a specific relation type and an interface.
     *
     * @param relation the type of relation (e.g., inheritance, implementation, association)
     * @param associatedinterface the interface associated with the given relation
     */
    public AssociatedClass(String relation, Interface associatedinterface) {
        this.relation = relation;
        this.associatedinterface = associatedinterface;
    }

    /**
     * Constructs an AssociatedClass instance that represents a relationship
     * between an interface and a specific relation type.
     *
     * @param associatedinterface the interface associated with the relationship
     * @param relation the type of relation (e.g., inheritance, implementation, association)
     */
    public AssociatedClass(Interface associatedinterface, String relation) {
        this.associatedinterface = associatedinterface;
        this.relation = relation;
    }

    /**
     * Constructs an AssociatedClass instance that represents a relationship
     * between a specific class and a type of relation.
     *
     * @param inheritedClass the Class object representing the associated class
     * @param relation the type of relation (e.g., inheritance, implementation, association)
     */
    public AssociatedClass(Class inheritedClass, String relation) {
        this.name = inheritedClass;
        this.relation = relation;
    }

    /**
     * Constructs an AssociatedClass object that represents a relationship between
     * a given interface and an associated class context.
     *
     * @param associatedinterface the interface associated with the relationship
     */
    public AssociatedClass(Interface associatedinterface) {
        this.associatedinterface = associatedinterface;
    }

    /**
     * Constructs an AssociatedClass instance that represents an association
     * defined by a specific multiplicity.
     *
     * @param m the Multiplicity object representing the association's multiplicity
     */
    public AssociatedClass(Multiplicity m) {
        this.multiplicity = m;
    }

    /**
     * Retrieves the class associated with this instance.
     *
     * @return the Class object representing the associated class.
     */
    public Class getName() {
        return name;
    }

    /**
     * Sets the class associated with this instance.
     *
     * @param name the Class object to be associated.
     */
    public void setName(Class name) {
        this.name = name;
    }

    /**
     * Retrieves the multiplicity associated with this instance.
     *
     * @return the Multiplicity object representing the association's multiplicity.
     */
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the multiplicity for this instance.
     *
     * @param multiplicity the Multiplicity object representing the association's multiplicity
     */
    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Retrieves the interface associated with this instance.
     *
     * @return the Interface object representing the associated interface.
     */
    public Interface getAssociatedinterface() {
        return associatedinterface;
    }

    /**
     * Sets the associated interface for this instance.
     *
     * @param associatedinterface the Interface object to be associated
     */
    public void setAssociatedinterface(Interface associatedinterface) {
        this.associatedinterface = associatedinterface;
    }

    /**
     * Retrieves the type of relation associated with this instance.
     *
     * @return the relation type as a String (e.g., inheritance, implementation, association).
     */
    public String getRelation() {
        return relation;
    }

    /**
     * Sets the type of relation associated with this instance.
     *
     * @param relation the type of relation (e.g., inheritance, implementation, association)
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }
}
