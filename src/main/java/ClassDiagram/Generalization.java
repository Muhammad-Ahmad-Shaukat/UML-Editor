package ClassDiagram;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Generalization {

    private Class startClass;
    private Class endClass;
    private Line line;
    private Polygon arrowHead;

    public Generalization(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = new Line();
        this.arrowHead = new Polygon();
        updateLine();
    }

    public Generalization(Class startClass, Class endClass, Line line, Polygon arrowHead) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
        this.arrowHead = arrowHead;
    }

    public void updateLine() {
        if (startClass != null && endClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());

            // Update the arrowhead position
            updateArrowHead();
        }
    }

    private void updateArrowHead() {
        if (startClass != null && endClass != null) {
            double x1 = line.getStartX();
            double y1 = line.getStartY();
            double x2 = line.getEndX();
            double y2 = line.getEndY();

            // Compute the direction vector
            double angle = Math.atan2(y2 - y1, x2 - x1);

            // Arrowhead dimensions
            double arrowLength = 15;
            double arrowWidth = 7;

            // Points for the triangle
            double arrowX1 = x2 - arrowLength * Math.cos(angle - Math.PI / 6);
            double arrowY1 = y2 - arrowLength * Math.sin(angle - Math.PI / 6);

            double arrowX2 = x2 - arrowLength * Math.cos(angle + Math.PI / 6);
            double arrowY2 = y2 - arrowLength * Math.sin(angle + Math.PI / 6);

            arrowHead.getPoints().setAll(
                    x2, y2,  // Tip of the arrow
                    arrowX1, arrowY1,
                    arrowX2, arrowY2
            );
        }
    }

    public Class getStartClass() {
        return startClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    public Class getEndClass() {
        return endClass;
    }

    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Polygon getArrowHead() {
        return arrowHead;
    }

    public void setArrowHead(Polygon arrowHead) {
        this.arrowHead = arrowHead;
    }
}
