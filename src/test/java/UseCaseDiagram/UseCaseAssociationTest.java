package UseCaseDiagram;

import ClassDiagram.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseAssociationTest {

    private UseCaseAssociation association;
    private Point startPoint;
    private Point endPoint;
    private UseCase useCase;
    private UseCaseActor actor;

    @BeforeEach
    void setUp() {
        startPoint = new Point(10.0, 20.0);
        endPoint = new Point(30.0, 40.0);
        useCase = new UseCase(startPoint, "UseCase1");
        actor = new UseCaseActor(startPoint, "Actor1");

        // Initialize UseCaseAssociation with start, end points, use case, and actor
        association = new UseCaseAssociation(startPoint, endPoint, useCase, actor);
    }

    @AfterEach
    void tearDown() {
        association = null;
        startPoint = null;
        endPoint = null;
        useCase = null;
        actor = null;
    }

    @Test
    void getActor() {
        assertEquals(actor, association.getActor(), "The actor should be correctly returned.");
    }

    @Test
    void setActor() {
        UseCaseActor newActor = new UseCaseActor(new Point(50.0, 60.0), "Actor2");
        association.setActor(newActor);
        assertEquals(newActor, association.getActor(), "The actor should be updated correctly.");
    }

    @Test
    void getStart() {
        assertEquals(startPoint, association.getStart(), "The start point should be correctly returned.");
    }

    @Test
    void setStart() {
        Point newStart = new Point(100.0, 200.0);
        association.setStart(newStart);
        assertEquals(newStart, association.getStart(), "The start point should be updated correctly.");
    }

    @Test
    void getEnd() {
        assertEquals(endPoint, association.getEnd(), "The end point should be correctly returned.");
    }

    @Test
    void setEnd() {
        Point newEnd = new Point(150.0, 250.0);
        association.setEnd(newEnd);
        assertEquals(newEnd, association.getEnd(), "The end point should be updated correctly.");
    }

    @Test
    void getUseCase() {
        assertEquals(useCase, association.getUseCase(), "The use case should be correctly returned.");
    }

    @Test
    void setUseCase() {
        UseCase newUseCase = new UseCase(new Point(200.0, 300.0), "UseCase2");
        association.setUseCase(newUseCase);
        assertEquals(newUseCase, association.getUseCase(), "The use case should be updated correctly.");
    }
}
