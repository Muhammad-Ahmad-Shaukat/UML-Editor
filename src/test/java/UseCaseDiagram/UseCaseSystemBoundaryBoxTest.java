package UseCaseDiagram;

import ClassDiagram.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseSystemBoundaryBoxTest {

    private UseCaseSystemBoundaryBox boundaryBox;
    private Point initialPoint;

    @BeforeEach
    void setUp() {
        initialPoint = new Point(0.0, 0.0);
        boundaryBox = new UseCaseSystemBoundaryBox(initialPoint, 100.0, 50.0, "SystemBoundaryBox");
    }

    @AfterEach
    void tearDown() {
        boundaryBox = null;
        initialPoint = null;
    }

    @Test
    void getInitialPoint() {
        assertEquals(initialPoint, boundaryBox.getInitialPoint(), "The initial point should match the given point.");
    }

    @Test
    void setInitialPoint() {
        Point newPoint = new Point(10.0, 20.0);
        boundaryBox.setInitialPoint(newPoint);
        assertEquals(newPoint, boundaryBox.getInitialPoint(), "The initial point should be updated.");
    }

    @Test
    void getLength() {
        assertEquals(100.0, boundaryBox.getLength(), "The length should match the given value.");
    }

    @Test
    void setLength() {
        boundaryBox.setLength(200.0);
        assertEquals(200.0, boundaryBox.getLength(), "The length should be updated.");
    }

    @Test
    void getWidth() {
        assertEquals(50.0, boundaryBox.getWidth(), "The width should match the given value.");
    }

    @Test
    void setWidth() {
        boundaryBox.setWidth(75.0);
        assertEquals(75.0, boundaryBox.getWidth(), "The width should be updated.");
    }

    @Test
    void getName() {
        assertEquals("SystemBoundaryBox", boundaryBox.getName(), "The name should match the given value.");
    }

    @Test
    void setName() {
        boundaryBox.setName("UpdatedBoundaryBox");
        assertEquals("UpdatedBoundaryBox", boundaryBox.getName(), "The name should be updated.");
    }
}
