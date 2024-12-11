package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents a comment box with a specified position, dimensions, and an optional comment.
 * This class allows for setting and retrieving the position, dimensions, and comment,
 * and provides a string representation of the comment with truncation for long comments.
 * Implements the Serializable interface for object serialization.
 */
public class CommentBox implements Serializable {
    Point initialpoint;
    String comment;
    Double length;
    Double width;

    /**
     * Creates a new CommentBox object with the specified initial position.
     * The comment is initialized as an empty string and the dimensions are set to default values.
     *
     * @param initialpoint the initial position of the comment box, represented as a Point object
     */
    public CommentBox(Point initialpoint) {
        this.initialpoint = initialpoint;
        comment = "";
        length = 15.0;
        width = 7.0;
    }

    /**
     * Retrieves the initial position of the comment box.
     *
     * @return the initial position of the comment box as a Point object
     */
    public Point getInitialpoint() {
        return initialpoint;
    }

    /**
     * Sets the initial point of the comment box.
     *
     * @param initialpoint the new initial position of the comment box, represented as a Point object
     */
    public void setInitialpoint(Point initialpoint) {
        this.initialpoint = initialpoint;
    }

    /**
     * Retrieves the width of the comment box.
     *
     * @return the width of the comment box as a Double
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the width of the CommentBox.
     *
     * @param width the new width of the CommentBox as a Double
     */
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Retrieves the length of the comment box.
     *
     * @return the length of the comment box as a Double
     */
    public Double getLength() {
        return length;
    }

    /**
     * Sets the length of the comment box.
     *
     * @param length the new length of the comment box as a Double
     */
    public void setLength(Double length) {
        this.length = length;
    }

    /**
     * Retrieves the comment associated with the CommentBox.
     *
     * @return the comment as a String
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment for the CommentBox.
     *
     * @param comment the new comment to be associated with the CommentBox as a String
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        if (comment.length()<7){
            return comment;
        }
        else{
            return comment.substring(0, 5)+"...";
        }
    }

}