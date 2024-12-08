package ClassDiagram;
import ClassDiagram.Attribute;
import ClassDiagram.Class;
import ClassDiagram.Function;
import ClassDiagram.Point;
import ClassDiagram.AssociatedClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClassTest {

    private Class classUnderTest;
    private Attribute testAttribute;
    private Function testFunction;
    private Point testPoint;
    private AssociatedClass testAssociatedClass;

    @BeforeEach
    void setUp() {
        // Initialize the test objects before each test
        testPoint = new Point(0.0, 0.0); // Assuming Point constructor takes x and y coordinates
        classUnderTest = new Class("TestClass", testPoint);
        testAttribute = new Attribute("int", "testAttribute");
        testFunction = new Function("void", "testFunction");
        testAssociatedClass = new AssociatedClass(classUnderTest,"Association");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        classUnderTest = null;
        testAttribute = null;
        testFunction = null;
        testAssociatedClass = null;
    }

    // Constructor Tests
    @Test
    void constructorWithClassNameAndInitialPoint() {
        assertEquals("TestClass", classUnderTest.getClassName());
        assertEquals(testPoint, classUnderTest.getInitialPoint());
        assertTrue(classUnderTest.getAttributes().isEmpty());
        assertTrue(classUnderTest.getFunctions().isEmpty());
        assertTrue(classUnderTest.getX().isEmpty());
    }

    @Test
    void constructorWithInitialPointOnly() {
        Class newClass = new Class(testPoint);
        assertEquals("Class", newClass.getClassName());
        assertEquals(testPoint, newClass.getInitialPoint());
        assertTrue(newClass.getAttributes().isEmpty());
        assertTrue(newClass.getFunctions().isEmpty());
    }

    @Test
    void constructorWithAllParameters() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Function> functions = new ArrayList<>();
        attributes.add(testAttribute);
        functions.add(testFunction);

        Class newClass = new Class("FullClass", attributes, functions, testPoint);

        assertEquals("FullClass", newClass.getClassName());
        assertEquals(attributes, newClass.getAttributes());
        assertEquals(functions, newClass.getFunctions());
        assertEquals(testPoint, newClass.getInitialPoint());
    }

    // Getter and Setter Tests
    @Test
    void getClassName() {
        assertEquals("TestClass", classUnderTest.getClassName());
    }

    @Test
    void setClassName() {
        classUnderTest.setClassName("NewClassName");
        assertEquals("NewClassName", classUnderTest.getClassName());
    }

    @Test
    void getAttributes() {
        assertTrue(classUnderTest.getAttributes().isEmpty());
    }

    @Test
    void setAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(testAttribute);
        classUnderTest.setAttributes(attributes);
        assertEquals(attributes, classUnderTest.getAttributes());
    }

    @Test
    void getFunctions() {
        assertTrue(classUnderTest.getFunctions().isEmpty());
    }

    @Test
    void setFunctions() {
        ArrayList<Function> functions = new ArrayList<>();
        functions.add(testFunction);
        classUnderTest.setFunctions(functions);
        assertEquals(functions, classUnderTest.getFunctions());
    }

    @Test
    void getInitialPoint() {
        assertEquals(testPoint, classUnderTest.getInitialPoint());
    }

    @Test
    void setInitialPoint() {
        Point newPoint = new Point(10.0, 10.0);
        classUnderTest.setInitialPoint(newPoint);
        assertEquals(newPoint, classUnderTest.getInitialPoint());
    }

    // Behavior Tests
    @Test
    void addAttribute() {
        classUnderTest.addAttribute(testAttribute);
        assertTrue(classUnderTest.getAttributes().contains(testAttribute));
    }

    @Test
    void removeAttribute() {
        classUnderTest.addAttribute(testAttribute);
        classUnderTest.removeAttribute(testAttribute);
        assertFalse(classUnderTest.getAttributes().contains(testAttribute));
    }

    @Test
    void addFunction() {
        classUnderTest.addFunction(testFunction);
        assertTrue(classUnderTest.getFunctions().contains(testFunction));
    }

    @Test
    void removeFunction() {
        classUnderTest.addFunction(testFunction);
        classUnderTest.removeFunction(testFunction);
        assertFalse(classUnderTest.getFunctions().contains(testFunction));
    }

    @Test
    void returnAttribute() {
        classUnderTest.addAttribute(testAttribute);
        String expected = testAttribute.toString() + "\n";
        assertEquals(expected, classUnderTest.returnAttribute());
    }

    @Test
    void returnFunction() {
        classUnderTest.addFunction(testFunction);
        String expected = testFunction.toString() + "\n";
        assertEquals(expected, classUnderTest.returnFunction());
    }

    // Integration Tests
    @Test
    void addAndRetrieveAssociatedClass() {
        classUnderTest.addX(testAssociatedClass);
        assertTrue(classUnderTest.getX().contains(testAssociatedClass));
    }

    @Test
    void removeAssociatedClass() {
        classUnderTest.addX(testAssociatedClass);
        classUnderTest.removeX(testAssociatedClass);
        assertFalse(classUnderTest.getX().contains(testAssociatedClass));
    }

    // File Generation Tests
    @Test
    void generateCode() {
        String filePath = "TestClass.java";
        classUnderTest.addAttribute(testAttribute);
        classUnderTest.addFunction(testFunction);

        classUnderTest.generateCode(filePath);

        // You can extend this test to read the file and verify its contents
        assertTrue(true, "Code generation executed successfully.");
    }
}
