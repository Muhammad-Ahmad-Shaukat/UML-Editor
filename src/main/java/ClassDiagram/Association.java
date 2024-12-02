package ClassDiagram;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

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
        text = "";
        startMultiplicity = new Multiplicity(1.0,1.0);
        endMultiplicity = new Multiplicity(1.0,1.0);
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
    private void updateTextPositions() {
        // Calculate positions for multiplicity and text based on the line coordinates
        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());

        // Create text nodes for the multiplicities and the text in the middle
        Text startMultiplicityText = new Text(start.getX(), start.getY(), startMultiplicity.toString());
        Text endMultiplicityText = new Text(end.getX(), end.getY(), endMultiplicity.toString());

        // Position the text in the middle of the line
        double middleX = (start.getX() + end.getX()) / 2;
        double middleY = (start.getY() + end.getY()) / 2;
        Text associationText = new Text(middleX, middleY, text);

        // Add text nodes to the canvas (or your UI component)
        // Assuming you have a canvas or Pane where the association is drawn
        // For example, canvas.getChildren().add(startMultiplicityText);
        // For now, just printing the positions and text to verify
        System.out.println("Start multiplicity: " + startMultiplicity.toString() + " at position: " + start.getX() + "," + start.getY());
        System.out.println("End multiplicity: " + endMultiplicity.toString() + " at position: " + end.getX() + "," + end.getY());
        System.out.println("Association text: " + text + " at position: " + middleX + "," + middleY);
    }
}

