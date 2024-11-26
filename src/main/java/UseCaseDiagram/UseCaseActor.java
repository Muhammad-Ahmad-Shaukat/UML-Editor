package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.FileWriter;
import java.io.IOException;

public class UseCaseActor {
    private String name;
    private Point initial;

    public UseCaseActor(Point initial, String name) {
        this.initial = initial;
        this.name = name;
    }

    public UseCaseActor(Point initial) {
        this.initial = initial;
        name = "Actor";
    }

    public Point getInitial() {
        return initial;
    }

    public void setInitial(Point initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void serializeUseCaseActor(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
            writer.write("<UseCaseActor>\n");
            writer.write("  <Name>" + name + "</Name>\n");
            writer.write("  <InitialPoint>\n");
            writer.write("    <x>" + initial.getX() + "</x>\n");
            writer.write("    <y>" + initial.getY() + "</y>\n");
            writer.write("  </InitialPoint>\n");
            writer.write("</UseCaseActor>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
