package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;

import java.io.Serializable;
/**
 * Represents an actor in a Use Case diagram. An actor is a role played
 * by a human, organization, or external system that interacts with
 * the system being modeled. This class provides details such as the
 * actor's name and initial position in the diagram.
 *
 * Instances of this class are serializable, allowing them to be saved
 * to files or transferred over a network as part of a Use Case diagram
 * representation.
 */
public class UseCaseActor implements Serializable {
    private String name;
    private Point initial;

    /**
     * Constructs a new UseCaseActor with the specified initial position and name.
     * The UseCaseActor represents an actor in a Use Case diagram with a specific
     * role and position.
     *
     * @param initial the initial position of the actor in the diagram; must not be null
     * @param name the name of the actor; must not be null
     */
    public UseCaseActor(Point initial, String name) {
        this.initial = initial;
        this.name = name;
    }

    /**
     * Constructs a new UseCaseActor with the specified initial position.
     * The UseCaseActor represents an actor in a Use Case diagram and is
     * initialized with a default name ("Actor").
     *
     * @param initial the initial position of the actor in the diagram; must not be null
     */
    public UseCaseActor(Point initial) {
        this.initial = initial;
        name = "Actor";
    }

    /**
     * Retrieves the initial position of the actor in the Use Case diagram.
     *
     * @return the initial position as a Point object
     */
    public Point getInitial() {
        return initial;
    }

    /**
     * Sets the initial position of the actor in the Use Case diagram.
     *
     * @param initial the new initial position to set; must not be null
     */
    public void setInitial(Point initial) {
        this.initial = initial;
    }

    /**
     * Retrieves the name of the actor in the Use Case diagram.
     *
     * @return the name of the actor as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the actor in the Use Case diagram.
     *
     * @param name the new name to assign to the actor; must not be null
     */
    public void setName(String name) {
        this.name = name;
    }


}