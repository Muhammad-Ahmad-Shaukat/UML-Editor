package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.Serializable;

public class UseCaseActor implements Serializable {
    private String name;
    private Point initial;

    public UseCaseActor(Point initial, String name) {
        this.initial = initial;
        this.name = name;
    }

    public UseCaseActor(Point initial) {
        this.initial = initial;
        name = "Actor";
    }

    public Point getInitial() {
        return initial;
    }

    public void setInitial(Point initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}