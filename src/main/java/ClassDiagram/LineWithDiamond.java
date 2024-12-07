package ClassDiagram;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class LineWithDiamond {
    private Line line;
    private Polygon diamond;

    public LineWithDiamond(double startX, double startY, double endX, double endY, double size, boolean isFilled) {
        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2.0);
        this.line.setStroke(Color.BLACK);
        double halfSize = size / 2;
        double x = endX;
        double y = endY;

        this.diamond = new Polygon(
                x, y,
                x - size, y - halfSize,
                x - size * 2, y,
                x - size, y + halfSize
        );
        this.diamond.setStroke(Color.BLACK);
        this.diamond.setFill(isFilled ? Color.BLACK : Color.TRANSPARENT);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setDiamond(Polygon diamond) {
        this.diamond = diamond;
    }

    public Polygon getDiamond() {
        return diamond;
    }
}
