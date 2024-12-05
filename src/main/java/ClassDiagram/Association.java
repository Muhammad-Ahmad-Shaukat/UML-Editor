package ClassDiagram;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Association implements Serializable {

    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private String text;
    Class startClass;
    Class endClass;
    Line line;

    public Association(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = new Line();
        updateLine();
    }



    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, String text, Class startClass, Class endClass, Line line) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, Class startClass, Class endClass, Line line) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
        this.line = line;
    }

    public void updateLine() {
        if (startClass != null && endClass != null) {
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
        }
    }

    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}

