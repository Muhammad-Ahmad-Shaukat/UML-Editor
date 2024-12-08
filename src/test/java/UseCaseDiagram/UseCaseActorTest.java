package UseCaseDiagram;

import ClassDiagram.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseActorTest {

    private UseCaseActor actor;
    private Point initialPoint;

    @BeforeEach
    void setUp() {
        initialPoint = new Point(10.0, 20.0);  // Initialize a Point object with sample coordinates
        actor = new UseCaseActor(initialPoint, "Actor1"); // Create an actor with the initial point and name
    }

    @AfterEach
    void tearDown() {
        actor = null;
        initialPoint = null;
    }

    @Test
    void getInitial() {
        assertEquals(initialPoint, actor.getInitial(), "The initial point should be correctly returned.");
    }

    @Test
    void setInitial() {
        Point newPoint = new Point(30.0, 40.0);
        actor.setInitial(newPoint);
        assertEquals(newPoint, actor.getInitial(), "The initial point should be updated correctly.");
    }

    @Test
    void getName() {
        assertEquals("Actor1", actor.getName(), "The name of the actor should be correctly returned.");
    }

    @Test
    void setName() {
        actor.setName("UpdatedActor");
        assertEquals("UpdatedActor", actor.getName(), "The name should be updated correctly.");
    }
}
