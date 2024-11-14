package com.boota.javaproject;

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

    @BeforeEach
    void setUp() {
        // Initialize the test objects before each test
        testPoint = new Point(0.0, 0.0); // Assuming Point constructor takes x and y coordinates
        classUnderTest = new Class("TestClass", testPoint);
        testAttribute = new Attribute("int", "testAttribute");
        testFunction = new Function("void", "testFunction");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        classUnderTest = null;
        testAttribute = null;
        testFunction = null;
    }

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

    @Test
    void addAttribute() {
        classUnderTest.addAttribute(testAttribute);
        assertTrue(classUnderTest.getAttributes().contains(testAttribute));
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
    void removeAttribute() {
        classUnderTest.addAttribute(testAttribute);
        classUnderTest.removeAttribute(testAttribute);
        assertFalse(classUnderTest.getAttributes().contains(testAttribute));
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
}
