package com.boota.javaproject;

public class DependencyRelationship {
    private String dependencyType;
    private UseCase startUseCase;
    private UseCase endUseCase;


    public DependencyRelationship(UseCase startUseCase, UseCase endUseCase, String dependencyType) {
        this.startUseCase = startUseCase;
        this.endUseCase = endUseCase;
        this.dependencyType = dependencyType;
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

//    public boolean isPointOnLine(Point point) {
//        double tolerance = 5.0;
//        double lineLength = Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2) + Math.pow(endPoint.getY() - startPoint.getY(), 2));
//        double area = Math.abs((point.getX() - startPoint.getX()) * (endPoint.getY() - startPoint.getY()) -
//                (point.getY() - startPoint.getY()) * (endPoint.getX() - startPoint.getX()));
//        double distanceFromLine = area / lineLength;
//
//        if (distanceFromLine > tolerance) {
//            return false;
//        }
//        return (point.getX() >= Math.min(startPoint.getX(), endPoint.getX()) &&
//                point.getX() <= Math.max(startPoint.getX(), endPoint.getX()) &&
//                point.getY() >= Math.min(startPoint.getY(), endPoint.getY()) &&
//                point.getY() <= Math.max(startPoint.getY(), endPoint.getY()));
//    }
}

