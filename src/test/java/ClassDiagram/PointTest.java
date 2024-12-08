package ClassDiagram;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    private Point point1;
    private Point point2;

    @BeforeEach
    void setUp() {
        // Initialize points with test values
        point1 = new Point(3.0, 4.0);
        point2 = new Point(0.0, 0.0);
    }

    @AfterEach
    void tearDown() {
        point1 = null;
        point2 = null;
    }

    @Test
    void getX() {
        assertEquals(3.0, point1.getX(), "The X coordinate should be correctly retrieved.");
    }

    @Test
    void setX() {
        point1.setX(5.0);
        assertEquals(5.0, point1.getX(), "The X coordinate should be updated.");
    }

    @Test
    void getY() {
        assertEquals(4.0, point1.getY(), "The Y coordinate should be correctly retrieved.");
    }

    @Test
    void setY() {
        point1.setY(6.0);
        assertEquals(6.0, point1.getY(), "The Y coordinate should be updated.");
    }

    @Test
    void distance() {
        double result = point1.distance(point2);
        assertEquals(5.0, result, 0.0001, "The distance between points should be calculated correctly.");
    }

    @Test
    void distance_withNullPoint() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            point1.distance(null);
        });
        assertEquals("Other point cannot be null", thrown.getMessage(), "Exception message should match the expected.");
    }

    @Test
    void testToString() {
        assertEquals("<X>3.0</X><Y>4.0</Y>", point1.toString(), "The toString method should return the correct string format.");
    }
}
