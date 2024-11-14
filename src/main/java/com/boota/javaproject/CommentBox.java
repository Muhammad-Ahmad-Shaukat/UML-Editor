package com.boota.javaproject;

public class CommentBox {
    Point initialpoint;
    String comment;
    Double length;
    Double width;

    public CommentBox(Point initialpoint) {
        this.initialpoint = initialpoint;
        comment = "";
        length = 15.0;
        width = 7.0;
    }

    public Point getInitialpoint() {
        return initialpoint;
    }

    public void setInitialpoint(Point initialpoint) {
        this.initialpoint = initialpoint;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getComment() {
        return comment;
    }

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