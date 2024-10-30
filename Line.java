package com.boota.demo;

public class Line {
    private final Point start;
    private final Point end;
    private Multiplicity startpoint;
    private Multiplicity endpoint;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
    public boolean isPointOnLine(Point point) {
        // Define a tolerance range for clicking near the line
        double tolerance = 5.0;  // This can be adjusted based on the required sensitivity

        // Calculate the distance of the point from the line
        double lineLength = Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
        double area = Math.abs((point.getX() - start.getX()) * (end.getY() - start.getY()) -
                (point.getY() - start.getY()) * (end.getX() - start.getX()));
        double distanceFromLine = area / lineLength;

        // Check if the point is close enough to the line
        if (distanceFromLine > tolerance) {
            return false;
        }

        // Also ensure the point is within the line segment bounds
        return (point.getX() >= Math.min(start.getX(), end.getX()) &&
                point.getX() <= Math.max(start.getX(), end.getX()) &&
                point.getY() >= Math.min(start.getY(), end.getY()) &&
                point.getY() <= Math.max(start.getY(), end.getY()));
    }


    public Point getStart() {
        return start;
    }
    public Point getEnd() {
        return end;
    }
    public Multiplicity getStartpoint() {
        return startpoint;
    }
    public void setStartpoint(Multiplicity startpoint) {
        this.startpoint = startpoint;
    }
    public Multiplicity getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(Multiplicity endpoint) {
        this.endpoint = endpoint;
    }
}
