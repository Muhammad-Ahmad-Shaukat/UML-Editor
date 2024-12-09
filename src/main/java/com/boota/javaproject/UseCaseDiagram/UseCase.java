package com.boota.javaproject.UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;

import java.io.Serializable;
import java.util.ArrayList;

public class UseCase implements Serializable {
    private Point initialPoint;
    private String name;
    private ArrayList<DependencyRelationship> associatedRelationships;

    public UseCase(Point initialPoint) {
        this.initialPoint = initialPoint;
        this.name = "Use Case";
        this.associatedRelationships = new ArrayList<>();
    }

    public UseCase(Point initialPoint, String name) {
        this.initialPoint = initialPoint;
        this.name = name;
        this.associatedRelationships = new ArrayList<>();
    }

    public UseCase(Point initialPoint, String name, ArrayList<DependencyRelationship> associatedRelationships) {
        this.initialPoint = initialPoint;
        this.name = name;
        this.associatedRelationships = associatedRelationships;
    }

    public Point getInitialPoint() {
        return initialPoint;
    }
    public boolean hasAnyRelationshipWith(UseCase other) {
        return associatedRelationships.stream()
                .anyMatch(rel -> rel.getEndUseCase() == other || rel.getStartUseCase() == other);
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DependencyRelationship> getAssociatedRelationships() {
        return associatedRelationships;
    }

    public void addAssociatedRelationship(DependencyRelationship relationship) {
        this.associatedRelationships.add(relationship);
    }


}
