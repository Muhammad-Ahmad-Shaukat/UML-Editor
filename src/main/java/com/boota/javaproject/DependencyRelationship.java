package com.boota.javaproject;

public class DependencyRelationship {
    private String dependencyType;
    private UseCase startUseCase;
    private UseCase endUseCase;
    private Point startPoint;
    private Point endPoint;

    public DependencyRelationship(UseCase startUseCase, UseCase endUseCase, String dependencyType, Point startPoint, Point endPoint) {
        this.startUseCase = startUseCase;
        this.endUseCase = endUseCase;
        this.dependencyType = dependencyType;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(String dependencyType) {
        this.dependencyType = dependencyType;
    }

    public UseCase getStartUseCase() {
        return startUseCase;
    }

    public void setStartUseCase(UseCase startUseCase) {
        this.startUseCase = startUseCase;
    }

    public UseCase getEndUseCase() {
        return endUseCase;
    }

    public void setEndUseCase(UseCase endUseCase) {
        this.endUseCase = endUseCase;
    }
}

