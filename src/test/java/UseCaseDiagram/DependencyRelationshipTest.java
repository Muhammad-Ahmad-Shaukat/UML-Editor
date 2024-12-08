package UseCaseDiagram;

import ClassDiagram.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyRelationshipTest {

    private UseCase useCase1;
    private UseCase useCase2;
    private DependencyRelationship relationship;

    @BeforeEach
    void setUp() {
        useCase1 = new UseCase(new Point(0.0, 0.0), "Use Case 1");
        useCase2 = new UseCase(new Point(5.0, 5.0), "Use Case 2");
        relationship = new DependencyRelationship(useCase1, useCase2, "Association");
    }

    @AfterEach
    void tearDown() {
        useCase1 = null;
        useCase2 = null;
        relationship = null;
    }

    @Test
    void getDependencyType() {
        assertEquals("Association", relationship.getDependencyType(), "The dependency type should be 'Association'.");
    }

    @Test
    void setDependencyType() {
        relationship.setDependencyType("Generalization");
        assertEquals("Generalization", relationship.getDependencyType(), "The dependency type should be updated to 'Generalization'.");
    }

    @Test
    void getStartUseCase() {
        assertEquals(useCase1, relationship.getStartUseCase(), "The start use case should be 'Use Case 1'.");
    }

    @Test
    void setStartUseCase() {
        UseCase newStartUseCase = new UseCase(new Point(1.0, 1.0), "Use Case 3");
        relationship.setStartUseCase(newStartUseCase);
        assertEquals(newStartUseCase, relationship.getStartUseCase(), "The start use case should be updated.");
    }

    @Test
    void getEndUseCase() {
        assertEquals(useCase2, relationship.getEndUseCase(), "The end use case should be 'Use Case 2'.");
    }

    @Test
    void setEndUseCase() {
        UseCase newEndUseCase = new UseCase(new Point(10.0, 10.0), "Use Case 4");
        relationship.setEndUseCase(newEndUseCase);
        assertEquals(newEndUseCase, relationship.getEndUseCase(), "The end use case should be updated.");
    }
}
