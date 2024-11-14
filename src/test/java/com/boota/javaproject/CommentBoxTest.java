package com.boota.javaproject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentBoxTest {
    private CommentBox commentBox;
    private Point initialPoint;

    @BeforeEach
    void setUp() {
        initialPoint = new Point(2.0, 3.0);
        commentBox = new CommentBox(initialPoint);
    }

    @AfterEach
    void tearDown() {
        commentBox = null;
        initialPoint = null;
    }

    @Test
    void testGetInitialpoint() {
        assertEquals(initialPoint, commentBox.getInitialpoint(), "Initial point should match the constructor's input");
    }

    @Test
    void testSetInitialpoint() {
        Point newPoint = new Point(5.0, 6.0);
        commentBox.setInitialpoint(newPoint);
        assertEquals(newPoint, commentBox.getInitialpoint(), "setInitialpoint should update the initial point correctly");
    }

    @Test
    void testGetWidth() {
        assertEquals(7.0, commentBox.getWidth(), "Default width should be 7.0");
    }

    @Test
    void testSetWidth() {
        commentBox.setWidth(10.0);
        assertEquals(10.0, commentBox.getWidth(), "setWidth should update the width correctly");
    }

    @Test
    void testGetLength() {
        assertEquals(15.0, commentBox.getLength(), "Default length should be 15.0");
    }

    @Test
    void testSetLength() {
        commentBox.setLength(20.0);
        assertEquals(20.0, commentBox.getLength(), "setLength should update the length correctly");
    }

    @Test
    void testGetComment() {
        assertEquals("", commentBox.getComment(), "Initial comment should be an empty string");
    }

    @Test
    void testSetComment() {
        String newComment = "This is a test comment";
        commentBox.setComment(newComment);
        assertEquals(newComment, commentBox.getComment(), "setComment should update the comment correctly");
    }

    @Test
    void testToStringWithShortComment() {
        String shortComment = "Hello";
        commentBox.setComment(shortComment);
        assertEquals("Hello", commentBox.toString(), "toString should return the full comment if it's shorter than 7 characters");
    }

    @Test
    void testToStringWithLongComment() {
        String longComment = "This is a long comment";
        commentBox.setComment(longComment);
        assertEquals("This ...", commentBox.toString(), "toString should return the first 5 characters followed by '...'");
    }
}
