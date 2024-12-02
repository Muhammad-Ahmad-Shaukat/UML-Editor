package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class UseCaseAssociation implements Serializable {
    private Point start;
    private Point end;
    private UseCase useCase;
    private UseCaseActor actor;

    public UseCaseAssociation(Point start, Point end, UseCase useCase, UseCaseActor actor) {
        this.start = start;
        this.end = end;
        this.useCase = useCase;
        this.actor = actor;
    }

    public UseCaseAssociation(Point start, Point end, UseCase useCase) {
        this(start, end, useCase, null);
    }


    public UseCaseActor getActor() {
        return actor;
    }

    public void setActor(UseCaseActor actor) {
        this.actor = actor;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public UseCase getUseCase() {
        return useCase;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }


}
