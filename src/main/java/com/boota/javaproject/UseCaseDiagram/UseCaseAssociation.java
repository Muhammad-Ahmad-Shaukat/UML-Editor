package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;

import java.io.Serializable;
/**
 * Represents an association in a Use Case diagram. This class models the
 * relationship between a Use Case and an actor, including the start and
 * end points of the association line, the related Use Case, and optionally
 * an associated actor.
 *
 * This class is serializable, making it suitable for use in contexts where
 * persistence or network communication of the Use Case diagram is required.
 */
public class UseCaseAssociation implements Serializable {
    private Point start;
    private Point end;
    private UseCase useCase;
    private UseCaseActor actor;

    /**
     * Constructs a new UseCaseAssociation, representing a relationship between
     * a Use Case and an actor in a Use Case diagram.
     *
     * @param start the starting point of the association line; must not be null
     * @param end the ending point of the association line; must not be null
     * @param useCase the Use Case involved in the association; must not be null
     * @param actor the actor involved in the association; may be null if no actor is associated
     */
    public UseCaseAssociation(Point start, Point end, UseCase useCase, UseCaseActor actor) {
        this.start = start;
        this.end = end;
        this.useCase = useCase;
        this.actor = actor;
    }

    /**
     * Constructs a UseCaseAssociation object without an associated actor. This
     * constructor initializes the start and end points of the association as
     * well as the related Use Case.
     *
     * @param start the starting point of the association; must not be null
     * @param end the ending point of the association; must not be null
     * @param useCase the Use Case associated with this association; must not be null
     */
    public UseCaseAssociation(Point start, Point end, UseCase useCase) {
        this(start, end, useCase, null);
    }


    /**
     * Retrieves the actor associated with this UseCaseAssociation.
     *
     * @return the associated UseCaseActor, or null if no actor is associated.
     */
    public UseCaseActor getActor() {
        return actor;
    }

    /**
     * Sets the actor associated with this Use Case association.
     *
     * @param actor the actor to be associated with this Use Case association;
     *              may be null if no actor is to be associated.
     */
    public void setActor(UseCaseActor actor) {
        this.actor = actor;
    }

    /**
     * Retrieves the starting point of this association.
     *
     * @return the starting point of the association as a Point object.
     */
    public Point getStart() {
        return start;
    }

    /**
     * Sets the starting point of the association.
     *
     * @param start the starting point of the association; must not be null
     */
    public void setStart(Point start) {
        this.start = start;
    }

    /**
     * Retrieves the ending point of this association.
     *
     * @return the ending point of the association as a Point object.
     */
    public Point getEnd() {
        return end;
    }

    /**
     * Sets the ending point of the association.
     *
     * @param end the ending point of the association; must not be null
     */
    public void setEnd(Point end) {
        this.end = end;
    }

    /**
     * Retrieves the Use Case associated with this Use Case association.
     *
     * @return the associated UseCase object, or null if no Use Case is set.
     */
    public UseCase getUseCase() {
        return useCase;
    }

    /**
     * Sets the Use Case associated with this association.
     *
     * @param useCase the Use Case to be associated with this association; must not be null
     */
    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }


}
