package ClassDiagram;

import com.boota.javaproject.ClassDiagram.Association;
import com.boota.javaproject.ClassDiagram.Class;
import com.boota.javaproject.ClassDiagram.Multiplicity;
import com.boota.javaproject.ClassDiagram.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssociationTest {

    private com.boota.javaproject.ClassDiagram.Class startClass;
    private com.boota.javaproject.ClassDiagram.Class endClass;
    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private Association association;

    @BeforeEach
    void setUp() {
        // Initialize test objects before each test
        startClass = new com.boota.javaproject.ClassDiagram.Class("StartClass", new Point(0.0, 0.0));
        endClass = new com.boota.javaproject.ClassDiagram.Class("EndClass", new Point(10.0, 10.0));
        startMultiplicity = new Multiplicity(1.0,0.0);
        endMultiplicity = new Multiplicity(0.0,1.0);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        association = null;
    }

    @Test
    void testConstructorWithStartAndEndClass() {
        association = new Association(startClass, endClass);

        assertEquals(startClass, association.getStartClass());
        assertEquals(endClass, association.getEndClass());
        assertNull(association.getStartMultiplicity());
        assertNull(association.getEndMultiplicity());
        assertNull(association.getText());
    }

    @Test
    void testConstructorWithAllFields() {
        String text = "association text";
        association = new Association(startMultiplicity, endMultiplicity, text, startClass, endClass);

        assertEquals(startMultiplicity, association.getStartMultiplicity());
        assertEquals(endMultiplicity, association.getEndMultiplicity());
        assertEquals(text, association.getText());
        assertEquals(startClass, association.getStartClass());
        assertEquals(endClass, association.getEndClass());
    }

    @Test
    void testConstructorWithMultiplicityAndClasses() {
        association = new Association(startMultiplicity, endMultiplicity, startClass, endClass);

        assertEquals(startMultiplicity, association.getStartMultiplicity());
        assertEquals(endMultiplicity, association.getEndMultiplicity());
        assertEquals(startClass, association.getStartClass());
        assertEquals(endClass, association.getEndClass());
        assertNull(association.getText());
    }

    @Test
    void getAndSetStartMultiplicity() {
        association = new Association(startClass, endClass);
        association.setStartMultiplicity(startMultiplicity);

        assertEquals(startMultiplicity, association.getStartMultiplicity());
    }

    @Test
    void getAndSetEndMultiplicity() {
        association = new Association(startClass, endClass);
        association.setEndMultiplicity(endMultiplicity);

        assertEquals(endMultiplicity, association.getEndMultiplicity());
    }

    @Test
    void getAndSetText() {
        String text = "association text";
        association = new Association(startClass, endClass);
        association.setText(text);

        assertEquals(text, association.getText());
    }

    @Test
    void getAndSetStartClass() {
        com.boota.javaproject.ClassDiagram.Class newStartClass = new com.boota.javaproject.ClassDiagram.Class("NewStartClass", new Point(5.0, 5.0));
        association = new Association(startClass, endClass);
        association.setStartClass(newStartClass);

        assertEquals(newStartClass, association.getStartClass());
    }

    @Test
    void getAndSetEndClass() {
        com.boota.javaproject.ClassDiagram.Class newEndClass = new Class("NewEndClass", new Point(15.0, 15.0));
        association = new Association(startClass, endClass);
        association.setEndClass(newEndClass);

        assertEquals(newEndClass, association.getEndClass());
    }
}
