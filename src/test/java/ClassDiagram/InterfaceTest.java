package ClassDiagram;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InterfaceTest {

    private Interface testInterface;
    private Function testFunction;
    private Point testPoint;

    @BeforeEach
    void setUp() {
        // Initialize test objects
        testPoint = new Point(10.0, 20.0);  // Assuming a simple Point constructor (x, y)
        testInterface = new Interface("TestInterface", testPoint);
        testFunction = new Function("void", "testFunction");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        testInterface = null;
        testFunction = null;
        testPoint = null;
    }

    @Test
    void getClassName() {
        assertEquals("TestInterface", testInterface.getClassName(), "Class name should be initialized correctly.");
    }

    @Test
    void setClassName() {
        testInterface.setClassName("UpdatedInterface");
        assertEquals("UpdatedInterface", testInterface.getClassName(), "Class name should be updated.");
    }

    @Test
    void getFunctions() {
        assertTrue(testInterface.getFunctions().isEmpty(), "Initially, the function list should be empty.");
    }

    @Test
    void setFunctions() {
        ArrayList<Function> functions = new ArrayList<>();
        functions.add(testFunction);
        testInterface.setFunctions(functions);
        assertEquals(1, testInterface.getFunctions().size(), "Functions list should have one function.");
    }

    @Test
    void getInitialPoint() {
        assertEquals(testPoint, testInterface.getInitialPoint(), "Initial point should be correctly set.");
    }

    @Test
    void setInitialPoint() {
        Point newPoint = new Point(30.0, 40.0);
        testInterface.setInitialPoint(newPoint);
        assertEquals(newPoint, testInterface.getInitialPoint(), "Initial point should be updated.");
    }

    @Test
    void addFunction() {
        testInterface.addFunction(testFunction);
        assertEquals(1, testInterface.getFunctions().size(), "One function should be added.");
        assertTrue(testInterface.getFunctions().contains(testFunction), "Function should be present in the list.");
    }

    @Test
    void removeFunction() {
        testInterface.addFunction(testFunction);
        testInterface.removeFunction(testFunction);
        assertEquals(0, testInterface.getFunctions().size(), "Function should be removed from the list.");
    }

    @Test
    void returnFunction() {
        testInterface.addFunction(testFunction);
        String expected = testFunction.toString() + "\n";
        assertEquals(expected, testInterface.returnFunction(), "The return function string should match the expected output.");
    }
}
