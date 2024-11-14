package com.boota.javaproject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTest {

    private Function function;
    private Attribute testAttribute;

    @BeforeEach
    void setUp() {
        testAttribute = new Attribute("int", "x");
        function = new Function("void", "testFunction");
    }

    @AfterEach
    void tearDown() {
        function = null;
        testAttribute = null;
    }

    @Test
    void getName() {
        assertEquals("testFunction", function.getName());
    }

    @Test
    void setName() {
        function.setName("newName");
        assertEquals("newName", function.getName());
    }

    @Test
    void getReturnType() {
        assertEquals("void", function.getReturnType());
    }

    @Test
    void setReturnType() {
        function.setReturnType("String");
        assertEquals("String", function.getReturnType());
    }

    @Test
    void getAttributes() {
        assertTrue(function.getAttributes().isEmpty());
    }

    @Test
    void setAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(testAttribute);
        function.setAttributes(attributes);
        assertEquals(attributes, function.getAttributes());
    }

    @Test
    void addAttribute() {
        function.addAttribute(testAttribute);
        assertTrue(function.getAttributes().contains(testAttribute));
    }

    @Test
    void removeAttribute() {
        function.addAttribute(testAttribute);
        function.removeAttribute(testAttribute);
        assertFalse(function.getAttributes().contains(testAttribute));
    }

    @Test
    void getAccessModifier() {
        assertEquals("public", function.getAccessModifier());
    }

    @Test
    void setAccessModifier() {
        function.setAccessModifier("private");
        assertEquals("private", function.getAccessModifier());
    }

    @Test
    void testToString() {
        function.addAttribute(testAttribute);
        assertEquals("+testFunction(int x) : void", function.toString());
    }
}
