package com.boota.javaproject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PointTest {
    private Point point;

    @BeforeEach
    void setUp() {
        point = new Point(2.5, 3.5);
    }

    @AfterEach
    void tearDown() {
        point = null;
    }

    @Test
    void testGetX() {
        assertEquals(2.5, point.getX(), "getX should return the correct x coordinate");
    }

    @Test
    void testSetX() {
        point.setX(5.0);
        assertEquals(5.0, point.getX(), "setX should correctly update the x coordinate");
    }

    @Test
    void testGetY() {
        assertEquals(3.5, point.getY(), "getY should return the correct y coordinate");
    }

    @Test
    void testSetY() {
        point.setY(7.0);
        assertEquals(7.0, point.getY(), "setY should correctly update the y coordinate");
    }

    @Test
    void testConstructor() {
        Point newPoint = new Point(4.0, 6.0);
        assertEquals(4.0, newPoint.getX(), "Constructor should correctly set the x coordinate");
        assertEquals(6.0, newPoint.getY(), "Constructor should correctly set the y coordinate");
    }
}
