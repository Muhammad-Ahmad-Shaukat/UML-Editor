package com.boota.javaproject.UseCaseDiagram;
import java.io.Serializable;
/**
 * Represents a dependency relationship between two Use Cases in a Use Case diagram.
 * A dependency relationship defines a connection between a starting Use Case
 * and an ending Use Case, with a specified type of dependency.
 *
 * This class is serializable to allow persistence or transfer of dependency relationship data.
 */
public class DependencyRelationship implements Serializable {
    private String dependencyType;
    private UseCase startUseCase;
    private UseCase endUseCase;


    /**
     * Constructs a DependencyRelationship object, representing a dependency relationship
     * between two Use Cases in a Use Case diagram with a specified dependency type.
     *
     * @param startUseCase the starting Use Case of the dependency relationship
     * @param endUseCase the ending Use Case of the dependency relationship
     * @param dependencyType the type of dependency, describing the nature of the relationship
     */
    public DependencyRelationship(UseCase startUseCase, UseCase endUseCase, String dependencyType) {
        this.startUseCase = startUseCase;
        this.endUseCase = endUseCase;
        this.dependencyType = dependencyType;
    }


    /**
     * Retrieves the type of dependency represented by this dependency relationship.
     *
     * @return the dependency type as a String.
     */
    public String getDependencyType() {
        return dependencyType;
    }

    /**
     * Sets the type of dependency for this dependency relationship.
     *
     * @param dependencyType the type of dependency, which describes the nature of the relationship
     *                        between the start and end Use Cases
     */
    public void setDependencyType(String dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * Retrieves the starting Use Case of the dependency relationship.
     *
     * @return the starting Use Case of the dependency relationship
     */
    public UseCase getStartUseCase() {
        return startUseCase;
    }

    /**
     * Sets the starting Use Case for the dependency relationship.
     *
     * @param startUseCase the Use Case that serves as the starting point of the dependency relationship
     */
    public void setStartUseCase(UseCase startUseCase) {
        this.startUseCase = startUseCase;
    }

    /**
     * Retrieves the ending Use Case of the dependency relationship.
     *
     * @return the ending Use Case of the dependency relationship
     */
    public UseCase getEndUseCase() {
        return endUseCase;
    }

    /**
     * Sets the ending Use Case for the dependency relationship.
     *
     * @param endUseCase the Use Case that serves as the end point of the dependency relationship
     */
    public void setEndUseCase(UseCase endUseCase) {
        this.endUseCase = endUseCase;
    }



}
