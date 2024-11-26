package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.FileWriter;
import java.io.IOException;

public class UseCaseSystemBoundaryBox {
    Point initialPoint;
    Double length;
    Double width;
    String name;

    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width, String name) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        this.name = name;
    }

    public UseCaseSystemBoundaryBox(Point initialPoint, Double length, Double width) {
        this.initialPoint = initialPoint;
        this.length = length;
        this.width = width;
        name = "BoundaryBox";
    }

    public UseCaseSystemBoundaryBox(Point initialPoint) {
        this.initialPoint = initialPoint;
        name = "BoundaryBox";
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void serializeBoundaryBox(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
            writer.write("<UseCaseSystemBoundaryBox>\n");

            // Serialize Initial Point
            writer.write("  <InitialPoint>\n");
            writer.write("    <x>" + initialPoint.getX() + "</x>\n");
            writer.write("    <y>" + initialPoint.getY() + "</y>\n");
            writer.write("  </InitialPoint>\n");

            // Serialize Length and Width
            writer.write("  <Length>" + length + "</Length>\n");
            writer.write("  <Width>" + width + "</Width>\n");

            // Serialize Name
            writer.write("  <Name>" + name + "</Name>\n");

            writer.write("</UseCaseSystemBoundaryBox>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
