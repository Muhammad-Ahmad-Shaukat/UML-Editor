package ClassDiagram;

import com.boota.javaproject.ClassDiagram.Multiplicity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiplicityTest {

    private Multiplicity testMultiplicity;

    @BeforeEach
    void setUp() {
        // Initialize the test object
        testMultiplicity = new Multiplicity(1.0, 5.0);  // Example start and end values
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        testMultiplicity = null;
    }

    @Test
    void getStart() {
        assertEquals(1.0, testMultiplicity.getStart(), "Start value should be correctly retrieved.");
    }

    @Test
    void setStart() {
        testMultiplicity.setStart(2.0);
        assertEquals(2.0, testMultiplicity.getStart(), "Start value should be updated correctly.");
    }

    @Test
    void getEnd() {
        assertEquals(5.0, testMultiplicity.getEnd(), "End value should be correctly retrieved.");
    }

    @Test
    void setEnd() {
        testMultiplicity.setEnd(10.0);
        assertEquals(10.0, testMultiplicity.getEnd(), "End value should be updated correctly.");
    }

    @Test
    void testToString() {
        String expected = "1.0..5.0";
        assertEquals(expected, testMultiplicity.toString(), "The toString method should return the correct format.");
    }
}
