package com.boota.javaproject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    private Attribute attribute;

    @BeforeEach
    void setUp() {
        attribute = new Attribute("age", "int", "private");
    }

    @AfterEach
    void tearDown() {
        attribute = null;
    }

    @Test
    void getName() {
        assertEquals("age", attribute.getName());
    }

    @Test
    void setName() {
        attribute.setName("height");
        assertEquals("height", attribute.getName());
    }

    @Test
    void getDataType() {
        assertEquals("int", attribute.getDataType());
    }

    @Test
    void setDataType() {
        attribute.setDataType("float");
        assertEquals("float", attribute.getDataType());
    }

    @Test
    void getAccessModifier() {
        assertEquals("private", attribute.getAccessModifier());
    }

    @Test
    void setAccessModifier() {
        attribute.setAccessModifier("public");
        assertEquals("public", attribute.getAccessModifier());
    }

    @Test
    void testToString() {
        assertEquals("-age : int", attribute.toString());

        attribute.setAccessModifier("public");
        assertEquals("+age : int", attribute.toString());

        attribute.setAccessModifier("protected");
        assertEquals("#age : int", attribute.toString());
    }

}
