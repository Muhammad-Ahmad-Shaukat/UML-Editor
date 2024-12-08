package ClassDiagram;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssociatedClassTest {

    private AssociatedClass associatedClassWithMultiplicity;
    private AssociatedClass associatedClassWithRelation;
    private AssociatedClass associatedClassWithInterface;

    private Class testClass;
    private Multiplicity testMultiplicity;
    private Interface testInterface;

    @BeforeEach
    void setUp() {
        // Initialize test objects before each test

        testClass = new Class("TestClass", new Point(0.0, 0.0));
        testMultiplicity = new Multiplicity(1.0,2.0);
        testInterface = new Interface(new Point(30.0,40.0));

        associatedClassWithMultiplicity = new AssociatedClass(testClass, testMultiplicity, "association");
        associatedClassWithRelation = new AssociatedClass(testClass, "inheritance");
        associatedClassWithInterface = new AssociatedClass(testInterface);
    }

    @AfterEach
    void tearDown() {
        // Clean up test objects after each test
        associatedClassWithMultiplicity = null;
        associatedClassWithRelation = null;
        associatedClassWithInterface = null;
        testClass = null;
        testMultiplicity = null;
        testInterface = null;
    }

    // Constructor Tests
    @Test
    void constructorWithMultiplicity() {
        assertEquals(testClass, associatedClassWithMultiplicity.getName());
        assertEquals(testMultiplicity, associatedClassWithMultiplicity.getMultiplicity());
        assertEquals("association", associatedClassWithMultiplicity.getRelation());
        assertNull(associatedClassWithMultiplicity.getAssociatedinterface());
    }

    @Test
    void constructorWithRelation() {
        assertEquals(testClass, associatedClassWithRelation.getName());
        assertNull(associatedClassWithRelation.getMultiplicity());
        assertEquals("inheritance", associatedClassWithRelation.getRelation());
        assertNull(associatedClassWithRelation.getAssociatedinterface());
    }

    @Test
    void constructorWithInterface() {
        assertNull(associatedClassWithInterface.getName());
        assertNull(associatedClassWithInterface.getMultiplicity());
        assertEquals(testInterface, associatedClassWithInterface.getAssociatedinterface());
    }

    // Getter and Setter Tests
    @Test
    void getName() {
        assertEquals(testClass, associatedClassWithMultiplicity.getName());
    }

    @Test
    void setName() {
        Class newClass = new Class("NewClass", new Point(1.0, 1.0));
        associatedClassWithMultiplicity.setName(newClass);
        assertEquals(newClass, associatedClassWithMultiplicity.getName());
    }

    @Test
    void getMultiplicity() {
        assertEquals(testMultiplicity, associatedClassWithMultiplicity.getMultiplicity());
    }

    @Test
    void setMultiplicity() {
        Multiplicity newMultiplicity = new Multiplicity(1.0,1.0);
        associatedClassWithMultiplicity.setMultiplicity(newMultiplicity);
        assertEquals(newMultiplicity, associatedClassWithMultiplicity.getMultiplicity());
    }

    @Test
    void getAssociatedinterface() {
        assertEquals(testInterface, associatedClassWithInterface.getAssociatedinterface());
    }

    @Test
    void setAssociatedinterface() {
        Interface newInterface = new Interface(new Point(30.0,40.0));
        associatedClassWithInterface.setAssociatedinterface(newInterface);
        assertEquals(newInterface, associatedClassWithInterface.getAssociatedinterface());
    }

    @Test
    void getRelation() {
        assertEquals("association", associatedClassWithMultiplicity.getRelation());
    }

    @Test
    void setRelation() {
        associatedClassWithMultiplicity.setRelation("composition");
        assertEquals("composition", associatedClassWithMultiplicity.getRelation());
    }
}
