package UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;
import com.boota.javaproject.UseCaseDiagram.DependencyRelationship;
import com.boota.javaproject.UseCaseDiagram.UseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseTest {

    private UseCase useCase1;
    private UseCase useCase2;
    private DependencyRelationship relationship;

    @BeforeEach
    void setUp() {
        Point point1 = new Point(5.0, 6.0);
        Point point2 = new Point(2.0, 3.0);
        useCase1 = new UseCase(point1, "Use Case 1");
        useCase2 = new UseCase(point2, "Use Case 2");
        relationship = new DependencyRelationship(useCase1, useCase2,"Include");
        useCase1.addAssociatedRelationship(relationship);
    }

    @AfterEach
    void tearDown() {
        useCase1 = null;
        useCase2 = null;
        relationship = null;
    }

    @Test
    void getInitialPoint() {
        assertEquals(5.0, useCase1.getInitialPoint().getX(), "Initial point X should match.");
        assertEquals(6.0, useCase1.getInitialPoint().getY(), "Initial point Y should match.");
    }

    @Test
    void hasAnyRelationshipWith() {
        assertTrue(useCase1.hasAnyRelationshipWith(useCase2), "Use Case 1 should have a relationship with Use Case 2.");
        assertFalse(useCase2.hasAnyRelationshipWith(useCase1), "Use Case 2 should not have a relationship with Use Case 1.");
    }

    @Test
    void setInitialPoint() {
        Point newPoint = new Point(7.0, 8.0);
        useCase1.setInitialPoint(newPoint);
        assertEquals(7.0, useCase1.getInitialPoint().getX(), "Initial point X should be updated.");
        assertEquals(8.0, useCase1.getInitialPoint().getY(), "Initial point Y should be updated.");
    }

    @Test
    void getName() {
        assertEquals("Use Case 1", useCase1.getName(), "The name of the use case should match.");
    }

    @Test
    void setName() {
        useCase1.setName("Updated Use Case");
        assertEquals("Updated Use Case", useCase1.getName(), "The name should be updated.");
    }

    @Test
    void getAssociatedRelationships() {
        assertEquals(1, useCase1.getAssociatedRelationships().size(), "There should be 1 associated relationship.");
    }

    @Test
    void addAssociatedRelationship() {
        UseCase useCase3 = new UseCase(new Point(10.0, 12.0), "Use Case 3");
        DependencyRelationship newRelationship = new DependencyRelationship(useCase1, useCase3,"Exclude");
        useCase1.addAssociatedRelationship(newRelationship);
        assertEquals(2, useCase1.getAssociatedRelationships().size(), "There should now be 2 associated relationships.");
    }
}
