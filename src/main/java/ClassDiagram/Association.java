package ClassDiagram;

import javafx.scene.shape.Line;

public class Association{

    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private String text;
    private Line line;

Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, String text) {
    this.startMultiplicity = startMultiplicity;
    this.endMultiplicity = endMultiplicity;
    this.text = text;
}

    public Association(Line line) {
        this.line = line;
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

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}

