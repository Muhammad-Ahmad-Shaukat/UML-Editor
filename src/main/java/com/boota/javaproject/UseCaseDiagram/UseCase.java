package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Represents a Use Case in a Use Case diagram. A Use Case captures a specific
 * functionality or behavior that the system provides to a user or external system.
 * It includes details such as its initial position in the diagram, its name, and
 * the list of associated dependency relationships with other Use Cases.
 *
 * This class is serializable, enabling it to be stored or transferred as necessary.
 */
public class UseCase implements Serializable {
    private Point initialPoint;
    private String name;
    private ArrayList<DependencyRelationship> associatedRelationships;

    /**
     * Creates a new UseCase with the specified initial position.
     * The UseCase is initialized with a default name ("Use Case")
     * and an empty list of associated relationships.
     *
     * @param initialPoint the initial position of the UseCase in the diagram; must not be null
     */
    public UseCase(Point initialPoint) {
        this.initialPoint = initialPoint;
        this.name = "Use Case";
        this.associatedRelationships = new ArrayList<>();
    }

    /**
     * Constructs a new UseCase with the specified initial position and name.
     * The UseCase is initialized with an empty list of associated dependency relationships.
     *
     * @param initialPoint the initial position of the UseCase in the diagram
     * @param name the name of the UseCase
     */
    public UseCase(Point initialPoint, String name) {
        this.initialPoint = initialPoint;
        this.name = name;
        this.associatedRelationships = new ArrayList<>();
    }

    /**
     * Constructs a new UseCase with the specified initial position, name, and
     * a list of associated dependency relationships.
     *
     * @param initialPoint the initial position of the UseCase in the diagram; must not be null
     * @param name the name of the UseCase; must not be null
     * @param associatedRelationships the list of DependencyRelationship objects associated with this UseCase;
     *                                 must not be null
     */
    public UseCase(Point initialPoint, String name, ArrayList<DependencyRelationship> associatedRelationships) {
        this.initialPoint = initialPoint;
        this.name = name;
        this.associatedRelationships = associatedRelationships;
    }

    /**
     * Retrieves the initial position of this UseCase in the diagram.
     *
     * @return the initial position of this*/
    public Point getInitialPoint() {
        return initialPoint;
    }
    public boolean hasAnyRelationshipWith(UseCase other) {
        return associatedRelationships.stream()
                .anyMatch(rel -> rel.getEndUseCase() == other || rel.getStartUseCase() == other);
    }

    /**
     * Sets the initial position of the UseCase in the diagram.
     *
     * @param initialPoint the new initial position to set for the UseCase; must not be null
     */
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }


    /**
     * Retrieves the name of this UseCase instance.
     *
     * @return the name of the UseCase as a String.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of this UseCase instance.
     *
     * @param name the new name to be assigned to the UseCase; must not be null.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Retrieves the list of dependency relationships associated with this UseCase.
     *
     * @return an ArrayList of DependencyRelationship objects representing
     *         the dependencies connected to this UseCase.
     */
    public ArrayList<DependencyRelationship> getAssociatedRelationships() {
        return associatedRelationships;
    }

    /**
     * Adds a dependency relationship to the list of associated relationships
     * of this Use Case. The dependency relationship represents a connection
     * between this Use Case and another Use Case in the diagram.
     *
     * @param relationship the dependency relationship to be added; must not be null
     */
    public void addAssociatedRelationship(DependencyRelationship relationship) {
        this.associatedRelationships.add(relationship);
    }


}
